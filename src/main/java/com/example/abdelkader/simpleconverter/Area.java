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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by AbdelKader on 2015-03-31.
 */
public class Area extends Activity
{
    String[] units = {"cm²", "m²", "km²", "sq in", "sq ft", "sq yard", "sq mile", "acre", "hectare"};
    String[] lexical = {"cm²", "m²", "km²", "square inch", "square foot", "square yard", "square mile", "acre", "hectare"};
    EditText input;  TextView unitin;
    EditText output; TextView unitout;
    SharedPreferences areasharedp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metric_layout);

        //Changing background color
        RelativeLayout lLayout = (RelativeLayout) findViewById(R.id.layout);
        lLayout.setBackgroundColor(Color.parseColor("#B5BBEB"));

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Area");

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
                savePreferences("storedIarea", unitin.getText().toString());
                savePreferences("storedOarea", unitout.getText().toString());
                startActivity(new Intent(Area.this, MainActivity.class));
                finish();
            }

        });

        //Saving units
        loadSavedPreferences();

    }


    private void loadSavedPreferences()
    {
        areasharedp = PreferenceManager.getDefaultSharedPreferences(this);
        String u = areasharedp.getString("storedIarea", units[1]);
        String v = areasharedp.getString("storedOarea", units[4]);
        unitin.setText(u);
        unitout.setText(v);
    }

    private void savePreferences(String key, String value)
    {
        SharedPreferences.Editor editor = areasharedp.edit();
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
                try
                {
                    convert(unitin, unitout);
                }
                catch(Exception e){
                    output.setText("");
                }
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
            try {
                convert(unitin, unitout);
            }
            catch(Exception e)
            {
                //Should NOT happen
                output.setText("ERROR");
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


    public void convert(TextView in, TextView out) throws Exception
    {
        String value = input.getText().toString();

        output.setEnabled(false);
        output.setTextColor(Color.parseColor("#000000"));

        /*I put my Area textfile inside raw in resources.
        Same principle as BufferedReader in normal Java except InputStreamReader is used instead of FileReader.
       */
        InputStream is = this.getResources().openRawResource(R.raw.area);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String currentLine = br.readLine();
        String[] sqcm = currentLine.split(",");

        currentLine = br.readLine();
        String[] sqm = currentLine.split(",");

        currentLine = br.readLine();
        String[] sqkm = currentLine.split(",");

        currentLine = br.readLine();
        String[] sqin = currentLine.split(",");

        currentLine = br.readLine();
        String[] sqft = currentLine.split(",");

        currentLine = br.readLine();
        String[] sqyard = currentLine.split(",");

        currentLine = br.readLine();
        String[] sqmile = currentLine.split(",");

        currentLine = br.readLine();
        String[] acre = currentLine.split(",");

        currentLine = br.readLine();
        String[] hectare = currentLine.split(",");

        String[][] table = {sqcm, sqm, sqkm, sqin, sqft, sqyard, sqmile, acre, hectare};
        //eg: table[0][1] = 0.001 (from sqcm to sqm)

        String unitin = in.getText().toString();
        String unitout = out.getText().toString();
        for (int i = 0; i < units.length; i++) {
            for (int j = 0; j < units.length; j++) {
                if (unitin.equals(units[i]) && unitout.equals(units[j]))
                {
                    double temp = 0.0;
                    try {
                        temp = Double.parseDouble(table[i][j]);
                    } catch (Exception e) {
                        output.setText(e.toString());
                    }

                    try{
                        double input = Double.parseDouble(value);
                        double result = input * temp;
                        Disp d = new Disp();
                        d.disp(result, output);
                    }
                    catch(Exception e){
                        output.setText("");
                    }
                }
            }
        }

        br.close();
    }



    public void onBackPressed()
    {
        savePreferences("storedIarea", unitin.getText().toString());
        savePreferences("storedOarea", unitout.getText().toString());
        startActivity(new Intent(Area.this, MainActivity.class));
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

