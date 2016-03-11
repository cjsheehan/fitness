package com.cjsheehan.fitness.activity.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.cjsheehan.fitness.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_settings);
    }
}
