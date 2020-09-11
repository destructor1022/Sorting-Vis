package com.toughproject.sortingvis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class Settings2 extends WearableActivity {

    double a = 0.0252381;
    double b = 2.37619;
    double c = 10;

    public static class settings_traits {
        //private static int number_of_circles = MainActivity.main_screen_traits.getFg_circle_count();
        private static int animation_time = MainActivity.main_screen_traits.getAnimation_time();

        public static int getAnimation_time() {
            return animation_time;
        }

        /*
        public static int getNumber_of_circles() {
            return number_of_circles;
        }

         */

        public static void setAnimation_time(int animation_time) {
            settings_traits.animation_time = animation_time;
        }

        /*
        public static void setNumber_of_circles(int number_of_circles) {
            settings_traits.number_of_circles = number_of_circles;
        }

         */
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        final LinearLayout root_layout = findViewById(R.id.root_layout);

        /*
        final TextView NC_value = findViewById(R.id.NC_value);
        final SeekBar NC_seek = findViewById(R.id.NC_seek);
        final int NC_count = MainActivity.main_screen_traits.getFg_circle_count();


         */
        final TextView AT_value = findViewById(R.id.AT_value);
        final SeekBar AT_seek = findViewById(R.id.AT_seek);
        final int AT_count = MainActivity.main_screen_traits.getAnimation_time();


        root_layout.post( new Runnable() {
            @Override
            public void run() {
                //run everything in here after layout has been updated
                //NC_value.setText(Integer.toString(NC_count) + " circles");
                //NC_seek.setProgress((NC_count - 3) / 2);

                AT_value.setText(Integer.toString(AT_count) + " ms");
                AT_seek.setProgress((int) ((-b + Math.sqrt(b * b - 4 * a * (c - (double) AT_count))) / (2 * a)));

            }
        });
    }

    protected void onStart() {
        super.onStart();

        final LinearLayout root_layout = findViewById(R.id.root_layout);

        //final TextView NC_value = findViewById(R.id.NC_value);
        //final SeekBar NC_seek = findViewById(R.id.NC_seek);

        final TextView AT_value = findViewById(R.id.AT_value);
        final SeekBar AT_seek = findViewById(R.id.AT_seek);

        /*

        NC_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int current;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub

                current = (int) (progress * 2 + 3);
                NC_value.setText(current + " circles");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //settings_traits.setNumber_of_circles(current);

                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putInt("NC", current);
                editor.commit();
            }
        });

         */

        AT_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int current;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub

                current = (int) ((double) progress * (double) progress * a + (double) progress * b + c);
                AT_value.setText(current + " ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //settings_traits.setAnimation_time(current);
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putInt("AT", current);
                editor.commit();
            }
        });


    }
}