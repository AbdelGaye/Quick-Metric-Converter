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

/**
 * Created by AbdelKader on 2015-04-04.
 */
public class Speed extends Activity
{
    String[] units = {"km/hr", "miles/hr", "m/sec", "feet/sec", "knot"};
    EditText input;  TextView unitin;
    EditText output; TextView unitout;
    SharedPreferences ssp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metric_layout);

        //Changing background color
        RelativeLayout lLayout = (RelativeLayout) findViewById(R.id.layout);
        lLayout.setBackgroundColor(Color.parseColor("#F2C8AC"));

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Speed");

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
                savePreferences("storedIsp", unitin.getText().toString());
                savePreferences("storedOsp", unitout.getText().toString());
                startActivity(new Intent(Speed.this, MainActivity.class));
                finish();
            }
        });

        loadSavedPreferences();
    }


    private void loadSavedPreferences()
    {
        ssp = PreferenceManager.getDefaultSharedPreferences(this);
        String u = ssp.getString("storedIsp", units[0]);
        String v = ssp.getString("storedOsp", units[1]);
        unitin.setText(u);
        unitout.setText(v);
    }

    private void savePreferences(String key, String value)
    {
        SharedPreferences.Editor editor = ssp.edit();
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
        builder.setItems(units, new DialogInterface.OnClickListener() {
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


    public void convert(TextView in, TextView out)
    {
        String value = input.getText().toString();

        output.setEnabled(false);
        output.setTextColor(Color.parseColor("#000000"));

        //Unit conditions are now starting
        try {
            double res = Double.parseDouble(value);

            // km/hour
            calculate(in, out, 0, 0, res);
            calculate(in, out, 0, 1, res * 0.6213711916666);
            calculate(in, out, 0, 2, res * 5 / 18);
            calculate(in, out, 0, 3, res * 0.91134416666);
            calculate(in, out, 0, 4, res * 0.539956805555);

            // miles/hour
            calculate(in, out, 1, 0, res * 1.609344);
            calculate(in, out, 1, 1, res);
            calculate(in, out, 1, 2, res * 0.44704);
            calculate(in, out, 1, 3, res * 1.46666667);
            calculate(in, out, 1, 4, res * 0.868976246078);

            // meters/sec
            calculate(in, out, 2, 0, res * 3.6);
            calculate(in, out, 2, 1, res * 2.23693629);
            calculate(in, out, 2, 2, res);
            calculate(in, out, 2, 3, res * 3.2808399);
            calculate(in, out, 2, 4, res * 1.9438445);

            // feet/sec
            calculate(in, out, 3, 0, res * 1.09728);
            calculate(in, out, 3, 1, res * 0.68181818);
            calculate(in, out, 3, 2, res * 0.3048);
            calculate(in, out, 3, 3, res);
            calculate(in, out, 3, 4, res * 0.5924838027);

            // knot
            calculate(in, out, 4, 0, res * 1.852);
            calculate(in, out, 4, 1, res * 1.15077944249);
            calculate(in, out, 4, 2, res * 0.51444444);
            calculate(in, out, 4, 3, res * 1.68780985310296);
            calculate(in, out, 4, 4, res);
        }

        catch(Exception e)
        {
            output.setText("");
        }
    }


    //Instead of having a lot of if-statements: the more efficient way ;)
    public void calculate(TextView in, TextView out, int inpos, int outpos, double result)
    {
        Disp d = new Disp();
        int inputn = 0;
        int outputn = 0;
        for(int i = 0; i < units.length; i++)
        {
            for(int j = 0; j< units.length; j++)
            {
                if (in.getText().toString().equals(units[i]) && out.getText().toString().equals(units[j]))
                {
                    inputn = i;
                    outputn = j;
                    break;
                }
            }
        }

        if(inputn == inpos && outputn == outpos)
        {
            d.disp(result, output);
        }
    }


    public void onBackPressed()
    {
        savePreferences("storedIsp", unitin.getText().toString());
        savePreferences("storedOsp", unitout.getText().toString());
        startActivity(new Intent(Speed.this, MainActivity.class));
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

