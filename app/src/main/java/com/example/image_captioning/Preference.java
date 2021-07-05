package com.example.image_captioning;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;

import javax.annotation.Nullable;

public class Preference extends PreferenceActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preference);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        android.preference.SwitchPreference preference = (android.preference.SwitchPreference) findPreference("background_theme");
        preference.setSummaryOff("Switch off state updated from code");
        preference.setSummaryOn("Switch on state updated from code");
        preference.setOnPreferenceChangeListener((android.preference.Preference.OnPreferenceChangeListener) (preference1, newValue) -> {
            boolean yes = (boolean) newValue;
            if (yes) {
                getListView().setBackgroundColor(Color.parseColor("#222222"));
            } else {
                getListView().setBackgroundColor(Color.parseColor("#ffffff"));
            }
            return true;
        });

//        SwitchPreference preference1 = (SwitchPreference) findPreference("language");
//        preference1.setSummaryOff("English Language");
//        preference1.setSummaryOn("Türkçe Dili");
//        preference1.setOnPreferenceChangeListener((android.preference.Preference.OnPreferenceChangeListener) (preference2, newValue) -> {
//            boolean yes = (boolean) newValue;
//            if (yes) {
//                MainActivity.setLanguage(false);
//            } else {
//                MainActivity.setLanguage(true);
//            }
//            return true;
//        });
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        android.preference.Preference seekBarPreference = new android.preference.Preference(this);
        seekBarPreference = findPreference("seek_speed");
        seekBarPreference.setOnPreferenceChangeListener((android.preference.Preference.OnPreferenceChangeListener) (preference3, newValue) -> {

             Integer value = (Integer) newValue;
             Toast.makeText(this, String.valueOf(value),Toast.LENGTH_LONG).show();
             MainActivity.setSpeechSpeed(value);

             return true;
        });

        seekBarPreference = findPreference("seek_pitch");
        seekBarPreference.setOnPreferenceChangeListener((android.preference.Preference.OnPreferenceChangeListener) (preference4, newValue) -> {
            Integer value = (Integer) newValue;
            Toast.makeText(this, String.valueOf(value),Toast.LENGTH_LONG).show();
            MainActivity.setSpeechPitch(value);

            return true;
        });

        android.preference.Preference listPreference = findPreference("language_select");
        listPreference.setOnPreferenceChangeListener((android.preference.Preference.OnPreferenceChangeListener) (preference5, newValue) -> {
            String key = (String) newValue;
            if ("1".equals(key)){
                MainActivity.setSelectedLanguage(1);
                Toast.makeText(this,"English is selected",Toast.LENGTH_LONG).show();

            }
            if ("2".equals(key)){
                Toast.makeText(this,"Afrikaans is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(2);
                listPreference.setSummary("Afrikaans");
            }
            if ("3".equals(key)){
                Toast.makeText(this,"Arabic is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(3);
                listPreference.setSummary("Arabic");
            }
            if ("4".equals(key)){
                Toast.makeText(this,"Azerbaijani is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(4);
                listPreference.setSummary("Azerbaijani");
            }
            if ("5".equals(key)){
                Toast.makeText(this,"Bulgaria is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(5);
                listPreference.setSummary("Bulgaria");
            }
            if ("6".equals(key)){
                Toast.makeText(this,"Catalan is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(6);
                listPreference.setSummary("Catalan");
            }
            if ("7".equals(key)){
                Toast.makeText(this,"Chinese is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(7);
                listPreference.setSummary("Chinese");
            }
            if ("8".equals(key)){
                Toast.makeText(this,"Croatian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(8);
                listPreference.setSummary("Croatian");
            }
            if ("9".equals(key)){
                Toast.makeText(this,"Czech is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(9);
                listPreference.setSummary("Czech");
            }
            if ("10".equals(key)){
                Toast.makeText(this,"Danish is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(10);
                listPreference.setSummary("Danish");
            }
            if ("11".equals(key)){
                Toast.makeText(this,"Dutch is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(11);
                listPreference.setSummary("Dutch");
            }
            if ("12".equals(key)){
                Toast.makeText(this,"Estonian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(12);
                listPreference.setSummary("Estonian");
            }
            if ("13".equals(key)){
                Toast.makeText(this,"Finnish is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(13);
                listPreference.setSummary("Finnish");
            }
            if ("14".equals(key)){
                Toast.makeText(this,"French is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(14);
                listPreference.setSummary("French");
            }
            if ("15".equals(key)){
                Toast.makeText(this,"Galician is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(15);
                listPreference.setSummary("Galician");
            }
            if ("16".equals(key)){
                Toast.makeText(this,"Georgian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(16);
                listPreference.setSummary("Georgian");
            }
            if ("17".equals(key)){
                Toast.makeText(this,"German is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(17);
                listPreference.setSummary("German");
            }
            if ("18".equals(key)){
                Toast.makeText(this,"Greek is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(18);
                listPreference.setSummary("Greek");
            }
            if ("19".equals(key)){
                Toast.makeText(this,"Hindi is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(19);
                listPreference.setSummary("Hindi");
            }
            if ("20".equals(key)){
                Toast.makeText(this,"Hungarian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(20);
                listPreference.setSummary("Hungarian");
            }
            if ("21".equals(key)){
                Toast.makeText(this,"Icelandic is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(21);
                listPreference.setSummary("Icelandic");
            }
            if ("22".equals(key)){
                Toast.makeText(this,"Indonesian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(22);
                listPreference.setSummary("Indonesian");
            }
            if ("23".equals(key)){
                Toast.makeText(this,"Italian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(23);
                listPreference.setSummary("Italian");
            }
            if ("24".equals(key)){
                Toast.makeText(this,"Japanese is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(24);
                listPreference.setSummary("Japanese");
            }
            if ("25".equals(key)){
                Toast.makeText(this,"Korean is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(25);
                listPreference.setSummary("Korean");
            }
            if ("26".equals(key)){
                Toast.makeText(this,"Latvian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(26);
                listPreference.setSummary("Latvian");
            }
            if ("27".equals(key)){
                Toast.makeText(this,"Lithuanian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(27);
                listPreference.setSummary("Lithuanian");
            }
            if ("28".equals(key)){
                Toast.makeText(this,"Malay is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(28);
                listPreference.setSummary("Malay");
            }
            if ("29".equals(key)){
                Toast.makeText(this,"Norwegian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(29);
                listPreference.setSummary("Norwegian");
            }
            if ("30".equals(key)){
                Toast.makeText(this,"Persian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(30);
                listPreference.setSummary("Persian");
            }
            if ("31".equals(key)){
                Toast.makeText(this,"Polish is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(31);
                listPreference.setSummary("Polish");
            }
            if ("32".equals(key)){
                Toast.makeText(this,"Portuguese is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(32);
                listPreference.setSummary("Portuguese");
            }
            if ("33".equals(key)){
                Toast.makeText(this,"Romanian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(33);
                listPreference.setSummary("Romanian");
            }
            if ("34".equals(key)){
                Toast.makeText(this,"Russian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(34);
                listPreference.setSummary("Russian");
            }
            if ("35".equals(key)){
                Toast.makeText(this,"Serbian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(35);
                listPreference.setSummary("Serbian");
            }
            if ("36".equals(key)){
                Toast.makeText(this,"Slovak is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(36);
                listPreference.setSummary("Slovak");
            }
            if ("37".equals(key)){
                Toast.makeText(this,"Slovenian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(37);
                listPreference.setSummary("Slovenian");
            }
            if ("38".equals(key)){
                Toast.makeText(this,"Spanish is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(38);
                listPreference.setSummary("Spanish");
            }
            if ("39".equals(key)){
                Toast.makeText(this,"Swedish is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(39);
                listPreference.setSummary("Swedish");
            }
            if ("40".equals(key)){
                Toast.makeText(this,"Thai is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(40);
                listPreference.setSummary("Thai");
            }
            if ("41".equals(key)){
                Toast.makeText(this,"Turkish is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(41);
                listPreference.setSummary("Turkish");
            }
            if ("42".equals(key)){
                Toast.makeText(this,"Ukrainian is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(42);
                listPreference.setSummary("Ukrainian");
            }
            if ("43".equals(key)){
                Toast.makeText(this,"Vietnamese is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(43);
                listPreference.setSummary("Vietnamese");
            }
            if ("44".equals(key)){
                Toast.makeText(this,"Welsh is selected",Toast.LENGTH_LONG).show();
                MainActivity.setSelectedLanguage(44);
                listPreference.setSummary("Welsh");
            }

            return true;
        });

    }

}