/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.authentication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */

@CompileStatic
class MovieMagicAuthenticatorService extends Service {
    private static final String LOG_TAG = MovieMagicAuthenticatorService.class.getSimpleName()

    // Instance field that stores the authenticator object
    private MovieMagicAuthenticator mAuthenticator

    @Override
    void onCreate() {
        // Create a new authenticator object
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_SERVICE_LOG_FLAG)
        mAuthenticator = new MovieMagicAuthenticator(this)
    }

    /**
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    IBinder onBind(final Intent intent) {
        LogDisplay.callLog(LOG_TAG,'onBind is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_SERVICE_LOG_FLAG)
        return mAuthenticator.getIBinder()
    }
}
