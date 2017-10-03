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
import android.text.InputType;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

/**
 * Created by AbdelKader on 2015-04-04.
 */
public class Temperature extends Activity
{
    String[] units = {"°C", "°F", "K", "°R"};
    String[] lexical = {"°C (Celsius)", "°F (Fahrenheit)", "K (Kelvin)", "°R (Rankine)"};
    EditText input;  TextView unitin;
    EditText output; TextView unitout;
    SharedPreferences tempsp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metric_layout);

        //Changing background color
        RelativeLayout lLayout = (RelativeLayout) findViewById(R.id.layout);
        lLayout.setBackgroundColor(Color.parseColor("#FF9C9C"));

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Temperature");

        //Show keyboard
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        final Vibrator vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        input = (EditText)findViewById(R.id.input);
        /*Added this May 8 (user prompt)
        input.setText(" Press here");
        if(input.getText().toString().equals(" Press here"))
        {
            input.setTextColor(Color.LTGRAY);
        }
        //When EditText is touched
        input.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (MotionEvent.ACTION_UP == event.getAction())
                {
                    input.setTextColor(Color.BLACK);
                    if(input.getText().toString().equals(" Press here")) {
                        input.setText("");
                    }
                }
                return false; //If it returns true, the event is handled and the keyboard won't popup.
            }
        }); */

        output = (EditText)findViewById(R.id.output);
        output.setEnabled(false);

        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);

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
                savePreferences("storedItemp", unitin.getText().toString());
                savePreferences("storedOtemp", unitout.getText().toString());
                startActivity(new Intent(Temperature.this, MainActivity.class));
                finish();
            }
        });

        loadSavedPreferences();
    }


    private void loadSavedPreferences() {
        tempsp = PreferenceManager.getDefaultSharedPreferences(this);
        String u = tempsp.getString("storedItemp", units[0]);
        String v = tempsp.getString("storedOtemp", units[1]);
        unitin.setText(u);
        unitout.setText(v);
    }

    private void savePreferences(String key, String value) {
        //   SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = tempsp.edit();
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

            calculate(num, "°C", denom, "°C", in);
            calculate(num, "°C", denom, "°F", (in * 9 / 5 + 32));
            calculate(num, "°C", denom, "K", (in + 273.15));
            calculate(num, "°C", denom, "°R", ((in + 273.15) * 9 / 5));

            calculate(num, "°F", denom, "°C", ((in - 32) * 5 / 9));
            calculate(num, "°F", denom, "°F", in);
            calculate(num, "°F", denom, "K", ((in - 32) * 5 / 9 + 273.15));
            calculate(num, "°F", denom, "°R", (in+459.67));

            calculate(num, "K", denom, "°C", (in-273.15));
            calculate(num, "K", denom, "°F", ((in-273.15)*9/5+32));
            calculate(num, "K", denom, "K", in);
            calculate(num, "K", denom, "°R", (in * 9 / 5));

            calculate(num, "°R", denom, "°C", ((in - 491.67) * 5 / 9));
            calculate(num, "°R", denom, "°F", (in-459.67));
            calculate(num, "°R", denom, "K", (in * 5 / 9));
            calculate(num, "°R", denom, "°R", in);
        }

        catch(Exception e)
        {
            output.setText("");
        }

    }


    //Instead of having a lot of if-statements: the more efficient way ;)
    public void calculate(String num, String vnum, String denom, String vdenom, double result)
    {
        if(num.equals(vnum) && denom.equals(vdenom))
        {
            output.setText(String.format("%.2f", result));
        }
    }


    public void onBackPressed()
    {
        savePreferences("storedItemp", unitin.getText().toString());
        savePreferences("storedOtemp", unitout.getText().toString());
        startActivity(new Intent(Temperature.this, MainActivity.class));
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

