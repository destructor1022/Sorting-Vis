package com.toughproject.sortingvis;

import androidx.annotation.Nullable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import static java.lang.Math.sqrt;

public class Settings extends PreferenceActivity {



    private static int default_time = MainActivity.main_screen_traits.getAnimation_time();
    private static int default_circles = MainActivity.main_screen_traits.getFg_circle_count();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();


        SharedPreferences settings = getSharedPreferences("hi", 0);
        default_time = settings.getInt("animation_time", MainActivity.main_screen_traits.getAnimation_time());
        default_circles = settings.getInt("number_circles", MainActivity.main_screen_traits.getFg_circle_count());

    }


    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final Preference root_preference = (Preference) findPreference("root_preference");

            Preference initial_animation_time = (Preference) findPreference("animation_time");
            initial_animation_time.setTitle(String.format("%d ms", (int) default_time));
            initial_animation_time.setDefaultValue((int) (-1.97 + sqrt(1.97*1.97 - 4 * (1 - (double) default_time) * 0.0802) / (2 * 0.0802)));


            ((Preference) findPreference("animation_time")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    progress = Double.valueOf(String.valueOf(newValue));
                    progress = 0.0802 * progress * progress + 1.97 * progress + 1;
                    preference.setTitle(String.format("%d ms", (int) progress));
                    preference.setDefaultValue(newValue);

                    return true;
                }
            });

            Preference initial_number_circles = (Preference) findPreference("number_circles_value");
            initial_number_circles.setTitle(String.format("%d circles", (int) default_circles));
            initial_number_circles.setDefaultValue(default_circles);

            ((Preference) findPreference("number_circles_value")).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    progress2 = Integer.valueOf(String.valueOf(newValue));
                    progress2 = progress2 * 2 + 3;
                    preference.setTitle(String.format("%d circles", progress2));
                    preference.setDefaultValue(newValue);

                    return true;
                }
            });

        }
    }

    private static double progress;
    private static int progress2;

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences settings = getSharedPreferences("hi", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("animation_time", (int) progress);
        editor.putInt("number_circles", progress2);
        editor.apply();
    }
}