package com.cjsheehan.fitness.activity;

import android.preference.PreferenceActivity;

import com.cjsheehan.fitness.R;
import com.cjsheehan.fitness.activity.fragment.SettingsFragment;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.user_settings, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}