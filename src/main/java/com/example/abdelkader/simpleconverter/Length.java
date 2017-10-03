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
public class Length extends Activity
{
    String[] units = {"m", "cm", "inch", "feet", "yard", "km", "mile"};
    EditText input;  TextView unitin;
    EditText output; TextView unitout;
    SharedPreferences lengthsp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metric_layout);

        //Changing background color
        RelativeLayout lLayout = (RelativeLayout) findViewById(R.id.layout);
        lLayout.setBackgroundColor(Color.parseColor("#E8E799"));

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Length");

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
                savePreferences("storedIl", unitin.getText().toString());
                savePreferences("storedOl", unitout.getText().toString());
                startActivity(new Intent(Length.this, MainActivity.class));
                finish();
            }
        });

        loadSavedPreferences();
    }


    private void loadSavedPreferences()
    {
        lengthsp = PreferenceManager.getDefaultSharedPreferences(this);
        String u = lengthsp.getString("storedIl", units[0]);
        String v = lengthsp.getString("storedOl", units[3]);
        unitin.setText(u);
        unitout.setText(v);
    }

    private void savePreferences(String key, String value)
    {
        SharedPreferences.Editor editor = lengthsp.edit();
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

            calculate(num, "m", denom, "m", in);
            calculate(num, "m", denom, "km", (in / 1000));
            calculate(num, "m", denom, "cm", (in*100));
            calculate(num, "m", denom, "mile", (in*0.000621371192));
            calculate(num, "m", denom, "yard", (in*1.09361329));
            calculate(num, "m", denom, "feet", (in*3.280839895));
            calculate(num, "m", denom, "inch", (in*39.3700787));

            calculate(num, "km", denom, "m", (in*1000));
            calculate(num, "km", denom, "km", in);
            calculate(num, "km", denom, "cm", (in*100000));
            calculate(num, "km", denom, "mile", (in*0.621371192));
            calculate(num, "km", denom, "yard", (in*1.0936133*1000));
            calculate(num, "km", denom, "feet", (in*3.280839895*1000));
            calculate(num, "km", denom, "inch", (in*39.3700787*1000));

            calculate(num, "cm", denom, "m", (in/100));
            calculate(num, "cm", denom, "km", (in / 100000));
            calculate(num, "cm", denom, "cm", in);
            calculate(num, "cm", denom, "mile", (in*0.00000621371192));
            calculate(num, "cm", denom, "yard", (in*0.0109361329));
            calculate(num, "cm", denom, "feet", (in*0.03280839895));
            calculate(num, "cm", denom, "inch", (in*0.393700787));

            calculate(num, "mile", denom, "m", (in*1609.344000614692));
            calculate(num, "mile", denom, "km", (in*1.609344000614692));
            calculate(num, "mile", denom, "cm", (in*160934.4000614692));
            calculate(num, "mile", denom, "mile", in);
            calculate(num, "mile", denom, "yard", (in*1760));
            calculate(num, "mile", denom, "feet", (in*5280));
            calculate(num, "mile", denom, "inch", (in*63360));

            calculate(num, "yard", denom, "m", (in*0.9143999986));
            calculate(num, "yard", denom, "km", (in*0.0009143999986));
            calculate(num, "yard", denom, "cm", (in*91.43999986));
            calculate(num, "yard", denom, "mile", (in*0.0005681818));
            calculate(num, "yard", denom, "yard", in);
            calculate(num, "yard", denom, "feet", (in*3));
            calculate(num, "yard", denom, "inch", (in*36));

            calculate(num, "feet", denom, "m", (in*0.3048));
            calculate(num, "feet", denom, "km", (in*0.3048/1000));
            calculate(num, "feet", denom, "cm", (in*0.3048*100));
            calculate(num, "feet", denom, "mile", (in*0.0001893939));
            calculate(num, "feet", denom, "yard", (in/3));
            calculate(num, "feet", denom, "feet", in);
            calculate(num, "feet", denom, "inch", (in*12));

            calculate(num, "inch", denom, "m", (in*0.0254));
            calculate(num, "inch", denom, "km", (in*0.0000254));
            calculate(num, "inch", denom, "cm", (in*2.54));
            calculate(num, "inch", denom, "mile", (in*0.0000157828));
            calculate(num, "inch", denom, "yard", (in*1/36));
            calculate(num, "inch", denom, "feet", (in*1/12));
            calculate(num, "inch", denom, "inch", in);
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
        savePreferences("storedIl", unitin.getText().toString());
        savePreferences("storedOl", unitout.getText().toString());
        startActivity(new Intent(Length.this, MainActivity.class));
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
