package com.example.abdelkader.simpleconverter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

//Updated May 9th

public class MainActivity extends ActionBarActivity
{
    Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        vibe = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        ImageButton time = (ImageButton) findViewById(R.id.time);
        ImageButton weight = (ImageButton)findViewById(R.id.weight);
        ImageButton length = (ImageButton)findViewById(R.id.length);
        ImageButton temp = (ImageButton)findViewById(R.id.temp);
        ImageButton roman = (ImageButton)findViewById(R.id.roman);
        ImageButton speed = (ImageButton)findViewById(R.id.speed);
        ImageButton area = (ImageButton)findViewById(R.id.area);
        ImageButton height = (ImageButton)findViewById(R.id.height);
        ImageButton volume = (ImageButton)findViewById(R.id.volume);
        ImageButton pressure = (ImageButton)findViewById(R.id.pressure);
        ImageButton energy = (ImageButton)findViewById(R.id.energy);
        ImageButton exit = (ImageButton)findViewById(R.id.exit);

        time.setOnClickListener(onClickListener);
        weight.setOnClickListener(onClickListener);
        length.setOnClickListener(onClickListener);
        temp.setOnClickListener(onClickListener);
        roman.setOnClickListener(onClickListener);
        speed.setOnClickListener(onClickListener);
        area.setOnClickListener(onClickListener);
        volume.setOnClickListener(onClickListener);
        pressure.setOnClickListener(onClickListener);
        energy.setOnClickListener(onClickListener);
        exit.setOnClickListener(onClickListener);

        height.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                vibe.vibrate(20);
                DialogHeight();
            }
        });
    }

    private OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.time:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Time.class));
                    break;
                case R.id.weight:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Weight.class));
                    break;
                case R.id.length:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Length.class));
                    break;
                case R.id.temp:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Temperature.class));
                    break;
                case R.id.roman:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Roman.class));
                    break;
                case R.id.speed:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Speed.class));
                    break;
                case R.id.area:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Area.class));
                    break;
                case R.id.volume:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Volume.class));
                    break;
                case R.id.pressure:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Pressure.class));
                    break;
                case R.id.energy:
                    vibe.vibrate(20);
                    startActivity(new Intent(MainActivity.this, Energy.class));
                    break;
                case R.id.exit:
                    vibe.vibrate(15);
                    finish();
                    break;
            }

            finish();
        }
    };


    //Menu choice for Height category
    public void DialogHeight()
    {
        final String[] choice = {"From feet to meters", "From meters to feet"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Height");
        builder.setItems(choice, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                if(item == 0)
                {
                    startActivity(new Intent(MainActivity.this, Heightfromfeet.class));
                    finish();
                }
                else if(item == 1)
                {
                    startActivity(new Intent(MainActivity.this, Heightfrommeters.class));
                    finish();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

}
