/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.transform.CompileStatic

@CompileStatic
class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = SettingsFragment.class.getSimpleName()

    @Override
    void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.SETTINGS_FRAGMENT_LOG_FLAG)

        //Add the preference (i.e. Settings) xml
        addPreferencesFromResource(R.xml.preference_xml)

        // Set the correct summary for Theme
        final Preference preference = findPreference(getString(R.string.pref_theme_key))
        if(preference instanceof ListPreference) {
            final ListPreference listPreference = preference as ListPreference
            final SharedPreferences sharedPreferences = preference.getSharedPreferences()
            final int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(getString(R.string.pref_theme_key),''))
            if(prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex])
            }
        }
    }

    @Override
    void onResume() {
        super.onResume()
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this)
    }

    @Override
    void onPause() {
        super.onPause()
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }

    @Override
    void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        LogDisplay.callLog(LOG_TAG,'onSharedPreferenceChanged is called',LogDisplay.SETTINGS_FRAGMENT_LOG_FLAG)
        final Preference preference = findPreference(key) as Preference
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list (since they have separate labels/values).
        if(preference instanceof ListPreference) {
            final ListPreference listPreference = preference as ListPreference
            final int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key,''))
            if(prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex])
            }
        } else if(key.equals(getString(R.string.pref_recommendation_key))) {
            // Trigger a notify change so that home screen gets updated with recommended movie preference
            getActivity().getContentResolver().notifyChange(MovieMagicContract.MovieBasicInfo.CONTENT_URI,null)
        } else if(key.equals(getString(R.string.pref_reduce_data_use_key))) {
            // Trigger a notify change so that home screen gets refreshed
            getActivity().getContentResolver().notifyChange(MovieMagicContract.MovieVideo.CONTENT_URI,null)
            getActivity().getContentResolver().notifyChange(MovieMagicContract.MovieBasicInfo.CONTENT_URI,null)
        } else {
            // No action
            LogDisplay.callLog(LOG_TAG,"No action for key: $key",LogDisplay.SETTINGS_FRAGMENT_LOG_FLAG)
        }
    }
}