package com.example.ayush.todopractise;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by admin on 10/18/2017.
 */

public class Settings  extends AppCompatActivity {
    private static int prefs = R.xml.preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,new PF()).commit();
        ActionBar ab = getSupportActionBar();
        //enables navigation to parent activity
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public static class PF extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(Settings.prefs);


        }
    }
}
