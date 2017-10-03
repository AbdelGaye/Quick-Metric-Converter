package com.example.abdelkader.simpleconverter;

/**
 * Created by AbdelKader on 2015-04-26.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class Heightfromfeet extends Activity
{
    EditText infeet, ininch, output;
    Button clear, swap;
    Vibrator vibe;
    int temp, tempo;
    TextView meters;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fromfeet);

        //Show keyboard
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        infeet = (EditText)findViewById(R.id.inputfeet);
        ininch = (EditText)findViewById(R.id.inputinch);

        output = (EditText)findViewById(R.id.output);
        output.setEnabled(false);

        meters = (TextView)findViewById(R.id.unitout);

        infeet.addTextChangedListener(autoconvert);
        ininch.addTextChangedListener(autoconvert);

        //Handling the Convert button and the output field
        final ImageButton convert = (ImageButton)findViewById(R.id.bconvert);
        convert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(infeet.getWindowToken(), 0);
            }
        });

        temp = 0; tempo = 0;

        //Clearing both inputs
        clear = (Button)findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                vibe.vibrate(15);

                infeet.setText("");
                ininch.setText("");

                output.setEnabled(false);
                output.setText("");

                meters.setText("meters");

                infeet.requestFocus();	//Setting focus to EditText feet (the left one)
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        //Menu button handler
        Button menu = (Button)findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                vibe.vibrate(30);
                startActivity(new Intent(Heightfromfeet.this, MainActivity.class));
                finish();
            }

        });

        //Handling the button to switch to "from meters to feet'inches"
        swap = (Button)findViewById(R.id.swap);
        swap.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                vibe.vibrate(15);
                startActivity(new Intent(Heightfromfeet.this, Heightfrommeters.class));
                finish();
            }
        });

    }


    TextWatcher autoconvert = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable arg0)
        {
            convert();
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            //Check http://www.learn-android-easily.com/2013/06/using-textwatcher-in-android.html
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };


    public void convert()
    {
        String feet = infeet.getText().toString();
        String inch = ininch.getText().toString();

        output.setEnabled(false);
        output.setTextColor(Color.parseColor("#000000"));

        if(feet.equals(""))
        {
            try
            {
                double numinch = Double.parseDouble(inch);
                double result = numinch / 39.3701;
                output.setText(String.format("%.3f", result));
            }

            catch(Exception e)
            {
                output.setText("");
            }
        }

        else
        {
            double numfeet = Double.parseDouble(feet);

            if(inch.equals("") || inch.equals("."))
            {
                //Calculate as if inches = 0
                double result;
                result = (numfeet*12*2.54/100);
                output.setText(String.format("%.3f", result));
            }

            else
            {
                double numinch = Double.parseDouble(inch);
                //1 feet = 12 in, 1 in = 2.54 cm, 1 m = 100 cm, 1 m = 39.3701 in
                double result = (numfeet*12*2.54/100)+(numinch/39.3701);
                pathconvert(numinch, result, numfeet);
            }
        }

    }


    public void pathconvert(double numinch, double result, double numfeet)
    {
        if(numinch >= 12 && numinch <= 100 && numfeet <= 684)
        {
            if(temp == 0)
            {
                //Display toast message once
                Toast.makeText(getApplicationContext(), "Here, inches are usually between 0 and 12 exclusive.", Toast.LENGTH_LONG).show();
            }
            temp = 1;

            //Convert from meters to feet
            double tx = result * 3.2808;
            //Want to take number away from decimal
            int x = (int) tx;
            //Inches part
            double y = (tx - x) * 12;

            //Making sure feet'12.0 is never displayed as output
            if (y > 11.9 && y <= 12.0) {
                y = 0.0;
                x = x + 1;
            }

            meters.setText("");
            output.setText("eq. to " + x + "" + " ' " + String.format("%.1f", y));
        }

        else if(numinch > 100 || numfeet > 684 && numinch > 11)
        {
            meters.setText("");
            output.setText("ERROR");
            if(tempo == 0)
            {
                //Display toast message once
                Toast.makeText(getApplicationContext(), "Inches are usually between 0 and 12 exclusive.", Toast.LENGTH_LONG).show();
            }
            tempo = 1;
        }

        else
        {
            meters.setText("meters");
            output.setText(String.format("%.3f", result));
        }
    }


    public void onBackPressed()
    {
        startActivity(new Intent(Heightfromfeet.this, MainActivity.class));
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

