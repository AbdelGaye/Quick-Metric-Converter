package com.example.abdelkader.simpleconverter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.*;

/**
 * Created by AbdelKader on 2015-04-04.
 */
public class Roman extends Activity
{
    EditText input; TextView in;
    EditText output; TextView out;
    int i, j, k, l;  //Using those as counters for toast display

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roman);

        //Show keyboard
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        in = (TextView) findViewById(R.id.unitin);
        out = (TextView) findViewById(R.id.unitout);
        i = j = k = l = 0;

        //Input numbers, output Roman as default
        in.setText("in decimal");
        out.setText("in Roman");

        input = (EditText) findViewById(R.id.input);

        output = (EditText) findViewById(R.id.output);
        output.setEnabled(false);

        //Using TextWatcher method way below for automatic conversion
        input.addTextChangedListener(autoconvert);

        //Handling the Convert button and the output field
        final ImageButton convert = (ImageButton) findViewById(R.id.bconvert);
        convert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Hiding the virtual keyboard
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                String it = in.getText().toString();        //textview unit
                String editin = input.getText().toString(); //user input
                if (it.equals("in decimal")) {
                    try {
                        int in = Integer.parseInt(editin);
                        convertToRom(in);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Please put a number between 1 and 3999.", Toast.LENGTH_SHORT).show();
                    }
                } else if (it.equals("in Roman")) {
                    convertToDec();
                }
            }
        });


        //Handling the Swap button
        ImageButton swap = (ImageButton) findViewById(R.id.swap);
        swap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String it = in.getText().toString();
                String ot = out.getText().toString();
                in.setText(ot);
                out.setText(it);

                vibe.vibrate(30);
                //TextViews are already swapped
                if (in.getText().toString().equals("in Roman")) {
                    swap(input, output);
                } else if (in.getText().toString().equals("in decimal")) {
                    swapback(input, output);
                }
                input.setText("");
                output.setText("");
            }
        });


        //Menu button handler
        Button menu = (Button) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibe.vibrate(30);
                startActivity(new Intent(Roman.this, MainActivity.class));
                finish();
            }
        });
    }


    TextWatcher autoconvert = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable arg0)
        {
            if(in.getText().toString().equals("in decimal"))
            {
                try {
                    int in = Integer.parseInt(input.getText().toString());
                    convertToRom(in);
                } catch (Exception e) {
                    output.setText("");
                }
            }
            else if(in.getText().toString().equals("in Roman"))
            {
                convertToDec();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            //Check http://www.learn-android-easily.com/2013/06/using-textwatcher-in-android.html
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };


    public String convertToRom(int in)
    {
        Hashtable<Integer, String> t = new Hashtable<>();
        t.put(1, "I");
        t.put(4, "IV");
        t.put(5, "V");
        t.put(9, "IX");
        t.put(10, "X");
        t.put(40, "XL");
        t.put(50, "L");
        t.put(90, "XC");
        t.put(100, "C");
        t.put(400, "CD");
        t.put(500, "D");
        t.put(900, "CM");
        t.put(1000, "M");

        String roman = "";

        output.setEnabled(false);
        output.setTextColor(Color.parseColor("#000000"));

        if (in > 3999 || in == 0)
        {
            output.setText("INVALID INPUT");
            if(i == 0) {
                Toast.makeText(getApplicationContext(), "Please put a number between 1 and 3999.", Toast.LENGTH_LONG).show();
            }
            i = 1;      //So that the toast above will only be shown once!
        }
        else if (t.containsKey(in))
        {
            output.setText(t.get(in));
        }
        else
        {
            //Following algorithm from 22/3/2015 on notebook (from rapidtables.com)
            int x = 0;
            int y = 0;
            while (in != 0)
            {
                //Taking highest value in hashtable t for input in
                while ((in - x) >= 0)
                {
                    //Without this, infinite loop with numbers > 1000 because of the next while
                    if (x >= 1000)
                    {
                        break;
                    }
                    //Ensuring x is in hashtable t
                    while (!t.containsKey(x))
                    {
                        x++;
                    }

                    if ((in - x) >= 0)
                    {
                        y = x;    //Now y is in the hashtable t
                        x++;
                    }
                }
                in = in - y;
                roman += t.get(y);
                x = 0;    //resetting x back to 0
            }

            output.setText(roman);
        }
        return roman;
    }


    public void convertToDec()
    {
        Hashtable<String, Integer> t = new Hashtable<>();
        t.put("I", 1);
        t.put("IV", 4);
        t.put("V", 5);
        t.put("IX", 9);
        t.put("X", 10);
        t.put("XL", 40);
        t.put("L", 50);
        t.put("XC", 90);
        t.put("C", 100);
        t.put("CD", 400);
        t.put("D", 500);
        t.put("CM", 900);
        t.put("M", 1000);

        //The user can use lowercase letters
        String numin = input.getText().toString().toUpperCase();

        output.setEnabled(false);
        output.setTextColor(Color.parseColor("#000000"));

        if (numin.equals(""))
        {
            if(j == 0) {
                Toast.makeText(getApplicationContext(), "Please put a number between 1 and 3999.", Toast.LENGTH_SHORT).show();
            }
            output.setText("");
            j = 1;      //So that the toast above will only be shown once!
        }
        else if (t.containsKey(numin))
        {
            output.setText(t.get(numin) + "");
        }
        else
        {
            int val = 0;
            int res = 0;
            for (int x = 0; x < numin.length(); x++)
            {
                if ((x + 1) < numin.length())
                {
                    String s = numin.substring(x, x + 2);  //endindex is exclusive
                    String c = numin.charAt(x) + "";

                    if (t.containsKey(s))
                    {
                        val = t.get(s);
                        //Skip the next character
                        x++;
                    }
                    else if (t.containsKey(c))
                    {
                        val = t.get(c);
                    }
                    else
                    {    //Error
                        res = 0;
                        break;
                    }
                }
                //For the last digit
                else if ((x + 1) >= numin.length())
                {
                    String c = numin.charAt(x) + "";
                    if (t.containsKey(c))
                    {
                        val = t.get(c);
                    }
                    else
                    {    //Error
                        res = 0;
                        break;
                    }
                }
                res += val;
            }

            //Calculations are over. Now conditions.
            //If there is an error in the input
            if (res == 0)
            {
                if(k == 0)
                    Toast.makeText(getApplicationContext(), "INPUT ERROR: Please review your numeral (I, V, X, L, C, D, M).",
                            Toast.LENGTH_LONG).show();
                k = 1;
                output.setText("INPUT ERROR");
            }
            else if (!convertToRom(res).equals(numin))
            {    //If the value entered by the user doesn't match the convertToRom roman value
                if(l == 0)
                    Toast.makeText(getApplicationContext(), "INVALID: This is not a valid Roman numeral version.",
                            Toast.LENGTH_LONG).show();
                l = 1;
                output.setText("INVALID");
            }
            else
            {
                output.setText(res + "");
            }

        }

    }


    //Changing the keyboards (integers only or with letters)
    public void swap(EditText in, EditText out)
    {
        in.setInputType(InputType.TYPE_CLASS_TEXT);
        out.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    public void swapback(EditText in, EditText out)
    {
        in.setInputType(InputType.TYPE_CLASS_NUMBER);
        out.setInputType(InputType.TYPE_CLASS_TEXT);
    }


    public void onBackPressed()
    {
        startActivity(new Intent(Roman.this, MainActivity.class));
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

}

