package com.example.abdelkader.simpleconverter;

import android.widget.EditText;

/**
 * Created by AbdelKader on 2015-06-05.
 */
public class Disp
{

    public void disp(double res, EditText output)
    {
        //To show for example 1.4353E-7 instead of 0.0000
        if(res < 0.0010 && res !=0)     // res!=0 so that we do not have an infinite loop!
        {
            int exp = 0;
            while((int)res == 0)
            {
                res *= 10;
                exp++;
            }
            String fin = String.format("%.4f", res) + "E-" + exp+"";
            output.setText(fin);
        }

        //To show for example 1.3434E+7 instead of 13434543
        else if(res > 1000000)
        {
            int exp = 0;
            while((int)res >= 10)
            {
                res /= 10;
                exp++;
            }
            String fin = String.format("%.4f", res) + "E+" + exp+"";
            output.setText(fin);
        }

        else
        {
            output.setText(String.format("%.4f", res));
        }
    }

}
