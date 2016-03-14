package com.cjsheehan.fitness.activity.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.util.Util;

import android.preference.Preference;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_settings);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);
            if (preference instanceof PreferenceGroup) {
                PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                    Preference singlePref = preferenceGroup.getPreference(j);
                    updatePreference(singlePref, singlePref.getKey());
                }
            } else {
                updatePreference(preference, preference.getKey());
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key), key);
        if(key == getString(R.string.default_view_key)) {
            Util.restartApplication(getActivity());
        }
    }

    private void updatePreference(Preference preference, String key) {
        if (preference == null) return;
        //if (preference instanceof ListPreference) {
        //    ListPreference listPreference = (ListPreference) preference;
        //    listPreference.setSummary(listPreference.getEntry());
        //    return;
        //}
        SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();

        if(key == getString(R.string.progress_increment_amount)) {
            preference.setSummary(sharedPrefs.getString(key, ""));
        }
        else if(key == getString(R.string.user_stride_length_key)) {
            preference.setSummary(sharedPrefs.getString(key, ""));
        }
        else if(key == getString(R.string.default_view_key)) {
            //String defaultViewValue = sharedPrefs.getString(key, "");
            //if(defaultViewValue == "1")
            preference.setSummary(sharedPrefs.getString(key, ""));
        }
    }
}
