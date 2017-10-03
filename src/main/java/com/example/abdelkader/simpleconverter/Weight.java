package com.example.abdelkader.simpleconverter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

/*The first non-main activity I made.
 * I thought of and tested everything from here. (Well kind of...)
 */
/**
 * Created by AbdelKader on 2015-04-04.
 */
public class Weight extends Activity
{
    String[] units = {"g", "kg", "lbs", "tons", "oz"};
    String[] lexical = {"g (grams)", "kg", "lbs (pounds)", "tons (metric tons)", "oz (ounces)"};
    EditText input; TextView unitin;
    EditText output; TextView unitout;
    SharedPreferences wsp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metric_layout);

        //Changing background color
        RelativeLayout lLayout = (RelativeLayout) findViewById(R.id.layout);
        lLayout.setBackgroundColor(Color.parseColor("#BFEDAF"));

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Weight");

        //Show keyboard
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        input = (EditText)findViewById(R.id.input);

        output = (EditText)findViewById(R.id.output);
        output.setEnabled(false);

        unitin = (TextView)findViewById(R.id.unitin);
        unitout = (TextView)findViewById(R.id.unitout);

        //Handling the spinners
        ImageButton in = (ImageButton)findViewById(R.id.spin);
        ImageButton out = (ImageButton)findViewById(R.id.spout);

        in.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                vibe.vibrate(20);
                unitDialog(unitin);
            }
        });

        out.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                vibe.vibrate(20);
                unitDialog(unitout);
            }
        });

        //Using TextWatcher method way below for automatic conversion
        input.addTextChangedListener(autoconvert);

        //Handling the Convert button
        final ImageButton convert = (ImageButton)findViewById(R.id.bconvert);
        convert.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //Hiding the virtual keyboard
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });


        //Handling the Swap button
        ImageButton swap = (ImageButton)findViewById(R.id.swap);
        swap.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //Don't forget to name permission inside Manifest once!
                vibe.vibrate(30);	//in milliseconds
                swap(unitin, unitout);
                input.setText("");
                output.setText("");
            }
        });


        //Menu button handler
        Button menu = (Button)findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                vibe.vibrate(30);
                savePreferences("storedIw", unitin.getText().toString());
                savePreferences("storedOw", unitout.getText().toString());
                startActivity(new Intent(Weight.this, MainActivity.class));
                finish();
            }
        });

        loadSavedPreferences();
    }


    private void loadSavedPreferences()
    {
        wsp = PreferenceManager.getDefaultSharedPreferences(this);
        String u = wsp.getString("storedIw", units[1]);
        String v = wsp.getString("storedOw", units[2]);
        unitin.setText(u);
        unitout.setText(v);
    }

    private void savePreferences(String key, String value)
    {
        SharedPreferences.Editor editor = wsp.edit();
        editor.putString(key, value);
        editor.commit();
    }


    //I implemented this method to make it easier to fill in the spinners with items
    public void unitDialog(final TextView t)
    {
        output.setEnabled(false);
        output.setTextColor(Color.parseColor("#000000"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a unit:");
        builder.setItems(lexical, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                t.setText(units[item]);
                convert(unitin, unitout);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void swap(TextView in, TextView out)
    {
        String temp = in.getText().toString();
        in.setText(out.getText().toString());
        out.setText(temp);
    }


    TextWatcher autoconvert = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable arg0)
        {
            convert(unitin, unitout);
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            //Check http://www.learn-android-easily.com/2013/06/using-textwatcher-in-android.html
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };


    public void convert(TextView spin, TextView spout)
    {
        String value = input.getText().toString();

        output.setEnabled(false);
        output.setTextColor(Color.parseColor("#000000"));

        String num = spin.getText().toString();
        String denom = spout.getText().toString();

        //Unit conditions are now starting
        try
        {
            double in = Double.parseDouble(value);
            double result;
            Disp d = new Disp();

            if(num.equals("g") && denom.equals("g"))
            {
                result = in;
                d.disp(result, output);
            }

            if(num.equals("g") && denom.equals("kg"))
            {
                result = in / 1000;
                d.disp(result, output);
            }

            if(num.equals("g") && denom.equals("lbs"))
            {
                result = in * 0.0022046226218;
                d.disp(result, output);
            }

            if(num.equals("g") && denom.equals("tons"))
            {
                result = in / 1000000;
                d.disp(result, output);
            }

            if(num.equals("g") && denom.equals("oz"))
            {
                result = in * 0.0352739619;
                d.disp(result, output);
            }
            //CAN'T DO THIS FOR EVERY SINGLE UNIT!
            //MUST BE A MORE EFFICIENT WAY!

            calculate(num, "kg", denom, "g", (in*1000));
            calculate(num, "kg", denom, "kg", in);
            calculate(num, "kg", denom, "lbs", (in*2.2046226218));
            calculate(num, "kg", denom, "tons", (in/1000));
            calculate(num, "kg", denom, "oz", (in*35.2739619));

            calculate(num, "lbs", denom, "g", (in*453.59237038));
            calculate(num, "lbs", denom, "kg", (in*0.45359237038));
            calculate(num, "lbs", denom, "lbs", in);
            calculate(num, "lbs", denom, "tons", (in*0.00045359237038));
            calculate(num, "lbs", denom, "oz", (in*16));

            calculate(num, "tons", denom, "g", (in*1000000));
            calculate(num, "tons", denom, "kg", (in*1000));
            calculate(num, "tons", denom, "lbs", (in*2204.62262));
            calculate(num, "tons", denom, "tons", in);
            calculate(num, "tons", denom, "oz", (in*35273.9619));

            calculate(num, "oz", denom, "g", (in*28.3495231648));
            calculate(num, "oz", denom, "kg", (in*0.0283495231648));
            calculate(num, "oz", denom, "lbs", (in*0.0625));
            calculate(num, "oz", denom, "tons", (in*0.0000283495231648));
            calculate(num, "oz", denom, "oz", in);
        }

        catch(Exception e)
        {
            output.setText("");
        }
    }

    //Instead of having a lot of if-statements: the more efficient way ;)
    public void calculate(String num, String vnum, String denom, String vdenom, double result)
    {
        Disp d = new Disp();
        if(num.equals(vnum) && denom.equals(vdenom))
        {
            d.disp(result, output);
        }
    }


    public void onBackPressed()
    {
        savePreferences("storedIw", unitin.getText().toString());
        savePreferences("storedOw", unitout.getText().toString());
        startActivity(new Intent(Weight.this, MainActivity.class));
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

