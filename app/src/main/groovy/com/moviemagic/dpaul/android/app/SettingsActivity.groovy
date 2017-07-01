/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName()

    @Override
    void onCreate(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.SETTINGS_ACTIVITY_LOG_FLAG)
        super.onCreate(savedInstanceState)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)

        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit()
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Press appbar back button to go to previous activity
            LogDisplay.callLog(LOG_TAG,'App bar back button is called',LogDisplay.SETTINGS_ACTIVITY_LOG_FLAG)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    void onBackPressed() {
        super.onBackPressed()
        LogDisplay.callLog(LOG_TAG,'Regular back button is called',LogDisplay.SETTINGS_ACTIVITY_LOG_FLAG)
        //Start the animation
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }
}