/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.authentication

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.NetworkErrorException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

/**
 * Manages "Authentication" to MovieMagic's user and backend SyncAdapter service.
 * The SyncAdapter framework requires an authenticator object. The SyncAdapter is designed in a way that
 * if it manages to find authToken it will use that to download TMDb user lists otherwise ignores it.
 * SyncAdapter will not prompt user to login if it does not find valid authToken
 */

/**
 * The Authentication logic is built based on a blog post written by Udi Cohen. Many thanks to Udi Cohen.
 * (reference http://blog.udinic.com/2013/04/24/write-your-own-android-authenticator/)
 */

@CompileStatic
class MovieMagicAuthenticator extends AbstractAccountAuthenticator {
    private static final String LOG_TAG = MovieMagicAuthenticator.class.getSimpleName()

    private final Context mContext

    public MovieMagicAuthenticator(final Context context) {
        super(context)
        LogDisplay.callLog(LOG_TAG,'MovieMagicAuthenticator constructor is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
        mContext = context
    }

    // Adds an account of the specified accountType
    // Called when the user wants to log-in or add a new account to the device
    // This method can be called by the app itself by calling AccountManager#addAccount()
    // (requires a special permission for that) or from the phone’s settings screen
    @Override
    public Bundle addAccount(
            final AccountAuthenticatorResponse response, final String accountType, final String authTokenType,
            final String[] requiredFeatures, final Bundle options) throws NetworkErrorException {
        LogDisplay.callLog(LOG_TAG,'addAccount is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
        // Create the AuthenticatorActivity intent
        final Intent intent = new Intent(mContext, MovieMagicAuthenticatorActivity.class)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType)
        intent.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, authTokenType)
        intent.putExtra(MovieMagicAuthenticatorActivity.IS_NEW_ACCOUNT, true)

        final Bundle bundle = new Bundle()
        // Send the intent with key 'KEY_INTENT' so that AuthenticatorActivity is launched
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    // Gets a stored auth-token for the account type from a previous successful log-in on the device.
    // If the auth-token is not found then the user will be prompted for log-in
    @Override
    public Bundle getAuthToken(
            final AccountAuthenticatorResponse response, final Account account, final String authTokenType,
            final Bundle options) throws NetworkErrorException {
        LogDisplay.callLog(LOG_TAG,'getAuthToken is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
        // If the caller requested an authToken type which  is not supported, then return error
        if (!authTokenType.equals(GlobalStaticVariables.AUTHTOKEN_TYPE_READ_ONLY) &&
                !authTokenType.equals(GlobalStaticVariables.AUTHTOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle()
            result.putString(AccountManager.KEY_ERROR_MESSAGE, 'invalid authTokenType')
            return result
        }
        // Get an instance of AccountManager
        final AccountManager accountManager = AccountManager.get(mContext)
        // Gets an auth token from the AccountManager's cache. If no auth token is cached for this account,
        // null will be returned. A new auth token will not be generated, and the server will not be contacted.
        // Intended for use by the authenticator, not directly by applications.
        String authToken = accountManager.peekAuthToken(account, authTokenType)
        LogDisplay.callLog(LOG_TAG,"Returned authToken -> $authToken",LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)

        // Lets give another try to authenticate the user
        if (!authToken) {
            final String password = accountManager.getPassword(account)
            // If password is available then give it another try
            if (password != null) {
                try {
                    LogDisplay.callLog(LOG_TAG,'Re-attempt to authenticate user',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
                    final Bundle bundle = GlobalStaticVariables.sTmdbAuthenticateInterface.tmdbUserSignIn(account.name, password, authTokenType)
                    authToken = bundle.getString(GlobalStaticVariables.TMDB_AUTH_TOKEN)
                } catch (final Exception e) {
                    LogDisplay.callLog(LOG_TAG,'Re-attempt to authenticate user failed',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
                    Log.e(LOG_TAG, "Error: ${e.message}", e)
                }
            }
        }

        // If a valid token is retrieved then return it
        if (authToken) {
            final Bundle result = new Bundle()
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            // Send the result with key 'KEY_AUTHTOKEN' to indicate a valid token is found
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
            return result
        }

        // If it reaches here that means a valid token is not found or password is not accessible
        // So, start AuthenticatorActivity and request user to enter login credentials
        final Intent intent = new Intent(mContext, MovieMagicAuthenticatorActivity.class)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type)
        intent.putExtra(AccountManager.KEY_AUTH_TOKEN_LABEL, authTokenType)
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name)
        intent.putExtra(MovieMagicAuthenticatorActivity.IS_NEW_ACCOUNT, false)

        final Bundle bundle = new Bundle()
        // Send the intent with key 'KEY_INTENT' so that AuthenticatorActivity is launched
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    // Ask the authenticator for a localized label for the given authTokenType.
    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        LogDisplay.callLog(LOG_TAG,'getAuthTokenLabel is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
        if (GlobalStaticVariables.AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return GlobalStaticVariables.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL
        else if (GlobalStaticVariables.AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return GlobalStaticVariables.AUTHTOKEN_TYPE_READ_ONLY_LABEL
        else
            return "$authTokenType (Label)"
    }

    @Override
    public Bundle hasFeatures(final AccountAuthenticatorResponse response, final Account account,
                              final String[] features) throws NetworkErrorException {
        LogDisplay.callLog(LOG_TAG,'hasFeatures is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
        final Bundle result = new Bundle()
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
        return result
    }

    // Returns a Bundle that contains the Intent of the activity that can be used to edit the properties.
    // In order to indicate success the activity should call response.setResult() with a non-null Bundle.
    // - Not implemented
    @Override
    public Bundle editProperties(final AccountAuthenticatorResponse response, final String accountType) {
        LogDisplay.callLog(LOG_TAG,'editProperties is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
        return null
    }

    // Checks that the user knows the credentials of an account. - Not implemented
    @Override
    public Bundle confirmCredentials(final AccountAuthenticatorResponse response, final Account account,
                                     final Bundle options) throws NetworkErrorException {
        LogDisplay.callLog(LOG_TAG,'confirmCredentials is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
        return null
    }

    //Update the locally stored credentials for an account. - Not implemented
    @Override
    public Bundle updateCredentials(
            final AccountAuthenticatorResponse response, final Account account, final String authTokenType,
            final Bundle options) throws NetworkErrorException {
        LogDisplay.callLog(LOG_TAG,'updateCredentials is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG)
        return null
    }
}