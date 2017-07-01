/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.support.v7.app.AlertDialog
import android.support.v7.preference.PreferenceManager;
import groovy.transform.CompileStatic

@CompileStatic
class MyMoviesEULA {
    private String EULA_PREFIX = "eula_"
    private Context mContext
    private PackageInfo mVersionInfo
    private String mEulaKey
    private SharedPreferences mPrefs

    public MyMoviesEULA(Context context) {
        mContext = context
        mVersionInfo = getPackageInfo()
        mEulaKey = EULA_PREFIX + mVersionInfo.versionCode
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext)
    }

    // Get the package info
    private PackageInfo getPackageInfo() {
        PackageInfo pi = null
        try {
            pi = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES)
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace()
        }
        return pi
    }

    public void checkAndShowEula() {
        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        boolean hasBeenShown = mPrefs.getBoolean(mEulaKey, false)
        if(hasBeenShown == false){
            showEula(true)
        }
    }

    public void showEula(boolean firstTime) {
        // Show the Eula
        String title = mContext.getString(R.string.app_name) + " v" + mVersionInfo.versionName

        //Includes the updates as well so users know what changed.
        String message = mContext.getString(R.string.app_update_detail) + "\n\n" + mContext.getString(R.string.eula_detail)

        // Disable orientation changes, to prevent parent activity
        // reinitialization
        if(((Activity)mContext).getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        } else {
            ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false) // Set this to ensure user cannot dismiss dialog by pressing back button or clicking outside
                .setPositiveButton(mContext.getString(R.string.eula_accept), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Mark this version as read.
                SharedPreferences.Editor editor = mPrefs.edit()
                editor.putBoolean(mEulaKey, true)
                editor.commit()
                dialogInterface.dismiss()
                // Enable orientation changes based on device's sensor
                ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
            }
        })
                .setNegativeButton(mContext.getString(R.string.eula_decline), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!firstTime) {
                    // Mark this version as not accepted because saw it but didn't accept it
                    SharedPreferences.Editor editor = mPrefs.edit()
                    editor.putBoolean(mEulaKey, false)
                    editor.commit()
                }
                // Close the activity as the user declined the EULA
                ((Activity)mContext).finish()
                // Enable orientation changes based on device's sensor
                ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
            }

        })
        builder.create().show()
    }
}