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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;

//Thanks to Wikihow for part of the math

public class Heightfrommeters extends Activity
{
    EditText input, output;
    Vibrator vibe;
    TextView feet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frommeters);

        //Show keyboard
        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        output = (EditText)findViewById(R.id.output);
        output.setEnabled(false);

        input = (EditText)findViewById(R.id.input);
        feet = (TextView)findViewById(R.id.unitout);

        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        //Using TextWatcher method way below for automatic conversion
        input.addTextChangedListener(autoconvert);

        //Handling the Convert button and the output field
        final ImageButton convert = (ImageButton)findViewById(R.id.bconvert);
        convert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Hiding the virtual keyboard
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });


        //Menu button handler
        Button menu = (Button)findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                vibe.vibrate(30);
                startActivity(new Intent(Heightfrommeters.this, MainActivity.class));
                finish();
            }

        });


        //Swap units button handler
        Button swap = (Button)findViewById(R.id.swap);
        swap.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                vibe.vibrate(15);
                startActivity(new Intent(Heightfrommeters.this, Heightfromfeet.class));
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
        String numin = input.getText().toString();

        output.setEnabled(false);
        output.setTextColor(Color.parseColor("#000000"));

        try
        {
            double in = Double.parseDouble(numin);
            //Convert from meters to feet
            double tx = in*3.2808;
            //Want to take number away from decimal
            int x = (int)tx;
            //Inches part
            double y = (tx-x)*12;

            if(in > 600000000)
            {
                feet.setText("");
                output.setText("OVERFLOW");
            }
            else
            {
                feet.setText("feet ' inches");
                output.setText(x+"" + " ' " + String.format("%.1f", y));
            }
        }

        catch(Exception e)
        {
            output.setText("0 ' 0");
        }
    }


    public void onBackPressed()
    {
        startActivity(new Intent(Heightfrommeters.this, MainActivity.class));
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
