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
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

/**
 * Created by AbdelKader on 2015-03-31.
 */
public class Time extends Activity
{
    String[] units = {"sec", "min", "hour", "day", "week", "month", "year"};
    String[] lexical = {"second", "minute", "hour", "day", "week", "month", "year"};
    EditText input;  TextView unitin;
    EditText output; TextView unitout;
    SharedPreferences sharedp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metric_layout);

        //Changing background color
        RelativeLayout lLayout = (RelativeLayout) findViewById(R.id.layout);
        lLayout.setBackgroundColor(Color.parseColor("#DCFFEA"));

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Time");

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

        out.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                vibe.vibrate(20);
                unitDialog(unitout);
            }
        });


        //Using TextWatcher method way below for automatic conversion
        input.addTextChangedListener(autoconvert);


        //Handling the Convert button
        final ImageButton convert = (ImageButton)findViewById(R.id.bconvert);
        convert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Hiding the virtual keyboard
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });


        //Handling the Swap button
        ImageButton swap = (ImageButton)findViewById(R.id.swap);
        swap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Don't forget to name permission inside Manifest once!
                vibe.vibrate(30);    //in milliseconds
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
                savePreferences("storedI", unitin.getText().toString());
                savePreferences("storedO", unitout.getText().toString());
                startActivity(new Intent(Time.this, MainActivity.class));
                finish();
            }

        });

        //Saving units
        loadSavedPreferences();
    }


    private void loadSavedPreferences() {
        sharedp = PreferenceManager.getDefaultSharedPreferences(this);
        String u = sharedp.getString("storedI", units[0]);
        String v = sharedp.getString("storedO", units[1]);
        unitin.setText(u);
        unitout.setText(v);
    }

    private void savePreferences(String key, String value) {
     //   SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedp.edit();
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
        builder.setItems(lexical, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                t.setText(units[item]);
                convert(unitin, unitout);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    //EDITED
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

        int in = 0;
        int out = 0;
        String In = spin.getText().toString();
        String Out = spout.getText().toString();

        for(int i = 0; i < units.length; i++)
        {
            for(int j = 0; j < units.length; j++)
            {
                if (In.equals(units[i]) && Out.equals(units[j]))
                {
                    in = i;
                    out = j;
                    break;
                }
            }
        }
        //Time units in order look like a multiplying tree
        double[] mult = {1, 60, 60, 24, 7, 4.348125, 12};

        //Unit conditions are now starting
        try
        {
            double input = Double.parseDouble(value);
            double r;
            Disp d = new Disp();

            for(int i = 0; i <= 6; i++)
            {	// <= 6 because there are 7 units inside the Spinner arraylist
                for(int j = 0; j <= 6; j++)
                {
                    if(i == in && j == out)
                    {
                        if(in == out)
                        {
                            r = input;
                            d.disp(r, output);
                            break;
                        }
                        //Case input unit > output unit
                        else if(in > out)
                        {
                            r = input * multiply(mult, j, i);
                            d.disp(r, output);
                            break;
                        }
                        //Case input unit < output unit
                        else if(in < out)
                        {
                            r = input / multiply(mult, i, j);
                            d.disp(r, output);
                            break;
                        }
                    }
                }
            }
        }

        catch(Exception e)
        {
            output.setText("");
        }

    }

    //No need to touch this.
    public double multiply(double[] arr, int begin, int stop)
    {
        double d = 1;
        for(int i = begin+1; i <= stop; i++)
        {
            d *= arr[i];
        }
        return d;
    }


    public void onBackPressed()
    {
        savePreferences("storedI", unitin.getText().toString());
        savePreferences("storedO", unitout.getText().toString());
        startActivity(new Intent(Time.this, MainActivity.class));
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
