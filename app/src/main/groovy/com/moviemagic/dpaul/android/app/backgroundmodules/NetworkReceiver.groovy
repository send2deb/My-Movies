/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import groovy.transform.CompileStatic

/**
 *
 * This BroadcastReceiver intercepts the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
 * which indicates a connection change. It checks if connection is available.
 * If available then sets the WiFi or Mobile data flag accordingly
 *
 */
@CompileStatic
class NetworkReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = NetworkReceiver.class.getSimpleName()

    @Override
    void onReceive(final Context context, final Intent intent) {
        LogDisplay.callLog(LOG_TAG,'onReceive is called',LogDisplay.NETWORK_RECEIVER_LOG_FLAG)
        final ConnectivityManager connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        final NetworkInfo activeInfo = connMgr.getActiveNetworkInfo()
        if (activeInfo != null && activeInfo.isConnected()) {
            GlobalStaticVariables.WIFI_CONNECTED = activeInfo.getType() == ConnectivityManager.TYPE_WIFI
            GlobalStaticVariables.MOBILE_CONNECTED = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE
        } else {
            GlobalStaticVariables.WIFI_CONNECTED = false
            GlobalStaticVariables.MOBILE_CONNECTED = false
        }
    }
}