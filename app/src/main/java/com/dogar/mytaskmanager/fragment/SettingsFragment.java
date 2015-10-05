package com.dogar.mytaskmanager.fragment;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.dogar.mytaskmanager.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static SettingsFragment newInstance() {
        SettingsFragment moreAppInfoFragment = new SettingsFragment();
        return moreAppInfoFragment;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.fragment_settings);
    }
}
