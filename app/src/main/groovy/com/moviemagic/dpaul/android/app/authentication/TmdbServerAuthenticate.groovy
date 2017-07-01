/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.authentication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.JsonParse
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class TmdbServerAuthenticate implements TmdbAuthenticateInterface {
    private static final String LOG_TAG = TmdbServerAuthenticate.class.getSimpleName()

    @Override
    public Bundle tmdbUserSignIn(final String userName, final String password, final String authTokenType) throws Exception {
        LogDisplay.callLog(LOG_TAG,'tmdbUserSignIn is called',LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
        Bundle bundle = new Bundle()
        try {
            // Request a token first
            // TMDB api example for requesting a token
            // https://api.themoviedb.org/3/authentication/token/new?api_key=key
            final Uri.Builder tokenUriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri tokenUri = tokenUriBuilder.appendPath(GlobalStaticVariables.TMDB_AUTHENTICATION_PATH)
                    .appendPath(GlobalStaticVariables.TMDB_TOKEN_PATH)
                    .appendPath(GlobalStaticVariables.TMDB_NEW_PATH)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .build()
            final URL tokenUrl = new URL(tokenUri.toString())
            LogDisplay.callLog(LOG_TAG,"Token url -> ${tokenUri.toString()}",LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
            final Bundle tokenBundle = getNewRequestToken(tokenUrl)
            LogDisplay.callLog(LOG_TAG,"tokenBundle -> $tokenBundle",LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)

            if(!tokenBundle.getBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG)) {
                // Authenticate user with Tmdb userid and password using request_token and get a authenticated token
                // TMDb api example to authenticate user
                // https://api.themoviedb.org/3/authentication/token/validate_with_login?api_key=key&request_token=token&username=user name&password=password
                final Uri.Builder authenticateUriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()
                final Uri authenticateUri = authenticateUriBuilder.appendPath(GlobalStaticVariables.TMDB_AUTHENTICATION_PATH)
                        .appendPath(GlobalStaticVariables.TMDB_TOKEN_PATH)
                        .appendPath(GlobalStaticVariables.TMDB_VALIDATE_WITH_LOGIN_PATH)
                        .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter(GlobalStaticVariables.TMDB_AUTHENTICATE_TOKEN_KEY,tokenBundle.getString(GlobalStaticVariables.TMDB_REQ_TOKEN))
                        .appendQueryParameter(GlobalStaticVariables.TMDB_AUTHENTICATE_USER_NAME_KEY,userName)
                        .appendQueryParameter(GlobalStaticVariables.TMDB_AUTHENTICATE_PASSWORD_KEY,password)
                        .build()
                final URL authenticateUrl = new URL(authenticateUri.toString())
                LogDisplay.callLog(LOG_TAG,"Authenticate url -> ${authenticateUri.toString()}",LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
                final Bundle authenticatedTokenBundle = validateRequestToken(authenticateUrl)
                LogDisplay.callLog(LOG_TAG,"authenticatedTokenBundle -> $authenticatedTokenBundle",LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)

                if(authenticatedTokenBundle && !authenticatedTokenBundle.getBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG)) {
                    // Generate a session id using the authenticated token
                    // TMDB api example to generate session id
                    // https://api.themoviedb.org/3/authentication/session/new?api_key=key&request_token=authenticated token
                    final Uri.Builder sessionIdUriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()
                    final Uri sessionIdUri = sessionIdUriBuilder.appendPath(GlobalStaticVariables.TMDB_AUTHENTICATION_PATH)
                            .appendPath(GlobalStaticVariables.TMDB_SESSION_PATH)
                            .appendPath(GlobalStaticVariables.TMDB_NEW_PATH)
                            .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                            .appendQueryParameter(GlobalStaticVariables.TMDB_AUTHENTICATE_TOKEN_KEY,authenticatedTokenBundle.getString(GlobalStaticVariables.TMDB_AUTHENTICATED_TOKEN))
                            .build()
                    final URL sessionIdUrl = new URL(sessionIdUri.toString())
                    LogDisplay.callLog(LOG_TAG,"Session id url -> ${sessionIdUri.toString()}",LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
                    final Bundle sessionIdBundle = getSessionId(sessionIdUrl)
                    LogDisplay.callLog(LOG_TAG,"sessionIdBundle -> $sessionIdBundle",LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)

                    if(sessionIdBundle && !sessionIdBundle.getBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG)) {
                        // Now get basic information of the account using session id
                        // TMDB api example to get basic information of the account
                        // https://api.themoviedb.org/3/account?api_key=key&session_id=session id
                        final Uri.Builder accountInfoUriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()
                        final Uri accountInfoUri = accountInfoUriBuilder.appendPath(GlobalStaticVariables.TMDB_ACCOUNT_PATH)
                                .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                                .appendQueryParameter(GlobalStaticVariables.TMDB_SESSION_ID_KEY,sessionIdBundle.getString(GlobalStaticVariables.TMDB_SESSION_ID))
                                .build()
                        final URL accountInfoUrl = new URL(accountInfoUri.toString())
                        LogDisplay.callLog(LOG_TAG,"Account info url -> ${accountInfoUri.toString()}",LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
                        final Bundle accountInfoBundle = getAccountDetails(accountInfoUrl)
                        LogDisplay.callLog(LOG_TAG,"accountInfoBundle -> $accountInfoBundle",LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
                        if(accountInfoBundle && !accountInfoBundle.getBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG)) {
                            LogDisplay.callLog(LOG_TAG,'TMDb login successful..',LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
                            /** Set the authToken to session id which will be used later to get data for the user **/
                            bundle.putString(GlobalStaticVariables.TMDB_AUTH_TOKEN,
                                    sessionIdBundle.getString(GlobalStaticVariables.TMDB_SESSION_ID))
                            /** Set the name of the TMDb user which will be used later to display on Nav drawer **/
                            bundle.putString(GlobalStaticVariables.TMDB_USER_NAME,
                                    accountInfoBundle.getString(GlobalStaticVariables.TMDB_USER_NAME))
                            /** Set the account id of TMDb user account which will be used later in SyncAdapter **/
                            bundle.putString(GlobalStaticVariables.TMDB_USER_ACCOUNT_ID,
                                    accountInfoBundle.getString(GlobalStaticVariables.TMDB_USER_ACCOUNT_ID))
                        } else {
                            LogDisplay.callLog(LOG_TAG,'TMDb account information retrieval failed',LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
                            bundle = accountInfoBundle
                        }
                    } else {
                        LogDisplay.callLog(LOG_TAG,'TMDb session id creation failed',LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
                        bundle = sessionIdBundle
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG,'TMDb validate token (i.e. login) failed',LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
                    bundle = authenticatedTokenBundle
                }
            } else {
                LogDisplay.callLog(LOG_TAG,'New TMDb request token failed',LogDisplay.TMDB_SERVER_AUTHENTICATE_LOG_FLAG)
                bundle = tokenBundle
            }
        } catch (final URISyntaxException e) {
            Log.e(LOG_TAG, "URISyntaxException: ${e.message}", e)
        }
        return bundle
    }

/**
 * This method retrieves a request token from TMDb server
 * @param url The TMDb URL
 * @return Bundle with formatted JSON data or status message based on response code & error flag
 */
    private static Bundle getNewRequestToken(final URL url) {
        InputStream inputStream = null
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection()
        Bundle tokenBundle = null
        try {
            conn.setReadTimeout(10000) /* milliseconds */
            conn.setConnectTimeout(15000) /* milliseconds */
            conn.setRequestMethod("GET")
            conn.setDoInput(true)
            // Starts the connection
            conn.connect()
            // Get the response code
            final int respCode = conn.getResponseCode()
            // Get the data and parse it
            if(respCode == 200) {
                inputStream = conn.getInputStream()
            } else {
                inputStream = conn.getErrorStream()
            }
            final def tokenJsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(inputStream)
            tokenBundle = JsonParse.parseTmdbToken(tokenJsonData, respCode)
        } catch (final JsonException e) {
            Log.e(LOG_TAG, "JsonException: ${e.message}", e)
        } catch (final IOException e) {
            Log.e(LOG_TAG, "IOException: ${e.message}", e)
        } finally {
            // Close the connection and input stream
            conn.disconnect()
            if(inputStream)
                inputStream.close()
        }
        return tokenBundle
    }

    /**
     * This method validates the request token along with user name and password in TMDb server
     * @param url The TMDb URL
     * @return Bundle with formatted JSON data or status message based on response code & error flag
     */
    private static Bundle validateRequestToken(final URL url) {
        InputStream inputStream = null
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection()
        Bundle authenticatedTokenBundle = null
        try {
            conn.setReadTimeout(10000) /* milliseconds */
            conn.setConnectTimeout(15000) /* milliseconds */
            conn.setRequestMethod("GET")
            conn.setDoInput(true)
            // Starts the connection
            conn.connect()
            // Get the response code
            final int respCode = conn.getResponseCode()
            // Get the data and parse it
            if(respCode == 200) {
                inputStream = conn.getInputStream()
            } else {
                inputStream = conn.getErrorStream()
            }
            final def validateTokenJsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(inputStream)
            authenticatedTokenBundle = JsonParse.parseTmdbAuthenticatedToken(validateTokenJsonData, respCode)
        } catch (final JsonException e) {
            Log.e(LOG_TAG, "JsonException: ${e.message}", e)
        } catch (final IOException e) {
            Log.e(LOG_TAG, "IOException: ${e.message}", e)
        } finally {
            // Close the connection and input stream
            conn.disconnect()
            if(inputStream)
                inputStream.close()
        }
        return authenticatedTokenBundle
    }

    /**
     * This method retrieves session id using a validated token from TMDb server
     * @param url The TMDb URL
     * @return Bundle with formatted JSON data or status message based on response code & error flag
     */
    private static Bundle getSessionId(final URL url) {
        InputStream inputStream = null
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection()
        Bundle SessionIdBundle = null
        try {
            conn.setReadTimeout(10000) /* milliseconds */
            conn.setConnectTimeout(15000) /* milliseconds */
            conn.setRequestMethod("GET")
            conn.setDoInput(true)
            // Starts the connection
            conn.connect()
            // Get the response code
            final int respCode = conn.getResponseCode()
            // Get the data and parse it
            if(respCode == 200) {
                inputStream = conn.getInputStream()
            } else {
                inputStream = conn.getErrorStream()
            }
            final def tokenJsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(inputStream)
            SessionIdBundle = JsonParse.parseTmdbSessionId(tokenJsonData, respCode)
        } catch (final JsonException e) {
            Log.e(LOG_TAG, "JsonException: ${e.message}", e)
        } catch (final IOException e) {
            Log.e(LOG_TAG, "IOException: ${e.message}", e)
        } finally {
            // Close the connection and input stream
            conn.disconnect()
            if(inputStream)
                inputStream.close()
        }
        return SessionIdBundle
    }

    /**
     * This method retrieves user name (TMDb from account information) using session id from TMDb server
     * @param url The TMDb URL
     * @return Bundle with Name of the user of the TMDb account & error flag
     */
    private static Bundle getAccountDetails(final URL url) {
        InputStream inputStream =null
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection()
        Bundle nameOfUser = null
        try {
            conn.setReadTimeout(10000) /* milliseconds */
            conn.setConnectTimeout(15000) /* milliseconds */
            conn.setRequestMethod("GET")
            conn.setDoInput(true)
            // Starts the connection
            conn.connect()
            // Get the response code
            final int respCode = conn.getResponseCode()
            // Get the data and parse it
            if(respCode == 200) {
                inputStream = conn.getInputStream()
            } else {
                inputStream = conn.getErrorStream()
            }
            final def tokenJsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(inputStream)
            nameOfUser = JsonParse.parseTmdbAccountInfo(tokenJsonData, respCode)
        } catch (final JsonException e) {
            Log.e(LOG_TAG, "JsonException: ${e.message}", e)
        } catch (final IOException e) {
            Log.e(LOG_TAG, "IOException: ${e.message}", e)
        } finally {
            // Close the connection and input stream
            conn.disconnect()
            if(inputStream)
                inputStream.close()
        }
        return nameOfUser
    }
}