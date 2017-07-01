/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.authentication

import android.os.Bundle
import groovy.transform.CompileStatic

@CompileStatic
/**
 * An interface which will be implemented by the ServerAuthenticate Class
 */
public interface TmdbAuthenticateInterface {
    public Bundle tmdbUserSignIn(final String userName, final String password, String authTokenType) throws Exception
}