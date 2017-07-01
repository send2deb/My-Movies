/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.authentication

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.Utility
import com.moviemagic.dpaul.android.app.syncadapter.MovieMagicSyncAdapterUtility
import groovy.transform.CompileStatic

/**
 * The Authenticator activity.
 * A login screen that offers TMDb login to user via user id/password.
 */
@CompileStatic
class MovieMagicAuthenticatorActivity extends AccountAuthenticatorActivity {
    private static final String LOG_TAG = MovieMagicAuthenticatorActivity.class.getSimpleName()

    public static final String IS_NEW_ACCOUNT = 'is_new_account'
    private AccountManager mAccountManager
    private EditText mUserNameEditTextView,mPasswordEditTextView
    private Button mSignInButton, mCancelButton, mSignUpButton
    private ProgressBar mProgressView
    private LinearLayout mLoginFormView
    private String mAuthTokenType
    private boolean isOnline
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mUserLoginTask = null

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
        setContentView(R.layout.activity_movie_magic_authenticator)

        mUserNameEditTextView = findViewById(R.id.username) as EditText
        mPasswordEditTextView = findViewById(R.id.password) as EditText
        mSignInButton = findViewById(R.id.sign_in_button) as Button
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                attemptLogin()
            }
        })
        mSignUpButton = findViewById(R.id.sign_up_button) as Button
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            void onClick(final View v) {
                launchSignUpPage()
            }
        })
        mCancelButton = findViewById(R.id.cancel_button) as Button
        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            void onClick(final View v) {
                cancelLogin()
            }
        })
        mLoginFormView = findViewById(R.id.login_form_linearlayout) as LinearLayout
        mProgressView = findViewById(R.id.login_progress) as ProgressBar

        mAccountManager = AccountManager.get(this)
        final String accountName = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
        mAuthTokenType = getIntent().getStringExtra(AccountManager.KEY_AUTH_TOKEN_LABEL)
        // If authTokenType is null then set it to full access
        if (mAuthTokenType == null)
            mAuthTokenType = GlobalStaticVariables.AUTHTOKEN_TYPE_FULL_ACCESS
        // If accountName is not null then populate the username field
        if (accountName) {
            (findViewById(R.id.username) as TextView).setText(accountName)
        }
    }

    void onStart() {
        super.onStart()
        // Check if the user is online or not, if not then show a message
        isOnline = Utility.isOnline(getApplicationContext())
        if(!isOnline) {
            Snackbar.make(mLoginFormView, getString(R.string.no_internet_connection_message), Snackbar.LENGTH_LONG).show()
        }
    }

    /**
     * Attempts to sign in to TMDb account based on user provided data.
     * If there are errors (invalid user id, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        LogDisplay.callLog(LOG_TAG,'attemptLogin is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
        //If the UserLoginTask is running then return
        if (mUserLoginTask != null) {
            return
        }

        // Reset errors.
        mUserNameEditTextView.setError(null)
        mPasswordEditTextView.setError(null)

        // Store values at the time of the login attempt.
        final String username = mUserNameEditTextView.getText().toString()
        final String password = mPasswordEditTextView.getText().toString()
        LogDisplay.callLog(LOG_TAG,"attemptLogin:Username->$username & Password->$password",LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)

        boolean cancel = false
        View focusView = null

        //Check that password is not blank
        if(!password) {
            mPasswordEditTextView.setError(getString(R.string.error_empty_password))
            focusView = mPasswordEditTextView
            cancel = true
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordEditTextView.setError(getString(R.string.error_invalid_password))
            focusView = mPasswordEditTextView
            cancel = true
        }

        //Check that username is not blank
        if(!username) {
            mUserNameEditTextView.setError(getString(R.string.error_empty_username))
            focusView = mUserNameEditTextView
            cancel = true
        }

        // Check for a valid username, if the user entered it.
        if (!TextUtils.isEmpty(username) && !isUserIdValid(username)) {
            mUserNameEditTextView.setError(getString(R.string.error_invalid_username))
            focusView = mUserNameEditTextView
            cancel = true
        }

        if (cancel) {
            // There is an error, don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus()
        } else {
            if(isOnline) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true)
                mUserLoginTask = new UserLoginTask()
                mUserLoginTask.execute([username,password] as String[])
            } else { // Show error message
                Toast.makeText(getBaseContext(), getString(R.string.no_internet_cannot_login_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private static boolean isUserIdValid(final String username) {
        //TMDb's user name is not necessary to be an email id, so just checking for any length greater than one
        return username.length() > 1
    }

    private static boolean isPasswordValid(final String password) {
        //TMDb's minimum password length is 4
        return password.length() >= 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        // Check if the build version is greater than equal Build.VERSION_CODES.HONEYCOMB_MR2 (i.e. version 13)
        if (Build.VERSION.SDK_INT >= 13) {
            final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime)
            mLoginFormView.setVisibility(show ? LinearLayout.INVISIBLE : LinearLayout.VISIBLE)
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(final Animator animation) {
                    mLoginFormView.setVisibility(show ? LinearLayout.INVISIBLE : LinearLayout.VISIBLE)
                }
            })

            mProgressView.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.INVISIBLE)
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(final Animator animation) {
                    mProgressView.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.INVISIBLE)
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.INVISIBLE)
            mLoginFormView.setVisibility(show ? LinearLayout.INVISIBLE : LinearLayout.VISIBLE)
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    protected class UserLoginTask extends AsyncTask<String, Void, Intent> {

        UserLoginTask() {
            LogDisplay.callLog(LOG_TAG,'UserLoginTask constructor is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
        }

        @Override
        protected Intent doInBackground(final String... params) {
            LogDisplay.callLog(LOG_TAG,'Started the authentication..',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
            final String username = params[0]
            final String password = params[1]
            final Bundle data = new Bundle()
            try {
                final Bundle bundle = GlobalStaticVariables.sTmdbAuthenticateInterface
                        .tmdbUserSignIn(username, password, mAuthTokenType)
                if(bundle) {
                    final String authtoken = bundle.getString(GlobalStaticVariables.TMDB_AUTH_TOKEN)
                    final String tmdbUserName = bundle.getString(GlobalStaticVariables.TMDB_USER_NAME)
                    final String tmdbAccountId = bundle.getString(GlobalStaticVariables.TMDB_USER_ACCOUNT_ID)
                    final String accountType = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
                    if (!bundle.getBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG)) {
                        LogDisplay.callLog(LOG_TAG, "AuthToken recevied successfully->$authtoken", LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, username)
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType)
                        data.putString(AccountManager.KEY_AUTHTOKEN, authtoken)
                        data.putString(AccountManager.KEY_PASSWORD, password)
                        data.putString(AccountManager.KEY_USERDATA, tmdbUserName)
                        data.putString(GlobalStaticVariables.TMDB_USERDATA_ACCOUNT_ID, tmdbAccountId)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Not able to retrive AuthToken. Error message->${bundle.getString(GlobalStaticVariables.TMDB_AUTH_ERROR_MSG)}", LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
                        data.putString(AccountManager.KEY_ERROR_MESSAGE, bundle.getString(GlobalStaticVariables.TMDB_AUTH_ERROR_MSG))
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Unexpected error..investigation needed', LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
                    data.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.authenticator_jellybean_error_message))
                }

            } catch (final Exception e) {
                data.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage())
                LogDisplay.callLog(LOG_TAG,'Exception thrown while authenticating the user',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
                Log.e(LOG_TAG, "Error: ${e.message}", e)
            }
            final Intent result = new Intent()
            result.putExtras(data)
            return result
        }

        @Override
        protected void onPostExecute(final Intent intent) {
            mUserLoginTask = null
            showProgress(false)
            if (intent.hasExtra(AccountManager.KEY_ERROR_MESSAGE)) {
                Toast.makeText(getBaseContext(), intent.getStringExtra(AccountManager.KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show()
            } else {
                mUserNameEditTextView.requestFocus()
                finishLogin(intent)
            }
        }

        @Override
        protected void onCancelled() {
            mUserLoginTask = null
            showProgress(false)
        }
    }

    /**
     * This method is called if the login is successful and an authToken is received
     * @param intent Intent containing the required details of the account
     */
    private void finishLogin(final Intent intent) {
        LogDisplay.callLog(LOG_TAG,'finishLogin is called',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)

        final String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
        final String accountPassword = intent.getStringExtra(AccountManager.KEY_PASSWORD)
        final String accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
        final String tmdbUSerName = intent.getStringExtra(AccountManager.KEY_USERDATA)
        final String tmdbAccountId = intent.getStringExtra(GlobalStaticVariables.TMDB_USERDATA_ACCOUNT_ID)
        LogDisplay.callLog(LOG_TAG,"finishLogin:Account name->$accountName & Account Type->$accountType",LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
        final Account account = new Account(accountName, accountType)

        if (getIntent().getBooleanExtra(IS_NEW_ACCOUNT, false)) {
            LogDisplay.callLog(LOG_TAG,'New account creation request',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
            final String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)
            final Context context = getApplicationContext()
            // Application supports only a single account, so first remove existing ones
            final Account[] accounts = mAccountManager.getAccountsByType(accountType)
            for(final i in 0..(accounts.size() -1)) {
                LogDisplay.callLog(LOG_TAG,"Removing account -> ${accounts[i].name}",LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
                // Remove the account
                if (Build.VERSION.SDK_INT >= 21) {
                    mAccountManager.removeAccount(accounts[i], this, new AccountManagerCallback<Bundle>() {
                        @Override
                        void run(final AccountManagerFuture<Bundle> future) {
                            try { //getResult will throw exception if login is not successful
                                final Bundle bundle = future.getResult()
                                LogDisplay.callLog(LOG_TAG,"Remove account successful, accout bundle: $bundle",LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
                                // Remove the Periodic Sync for the account
                                MovieMagicSyncAdapterUtility.removePeriodicSync(accounts[i], context)
                                // Add the account and authToken
                                addAcountAndAuthToken(intent, account, authtoken, tmdbUSerName, tmdbAccountId, accountPassword)
                            } catch (final Exception e) {
                                LogDisplay.callLog(LOG_TAG,"Remove account failed, error message: ${e.getMessage()}",LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
                                Toast.makeText(getBaseContext(), getString(R.string.cannot_perform_operation_message), Toast.LENGTH_SHORT).show()
                                Log.e(LOG_TAG, "Error: ${e.message}", e)
                            }
                        }
                    }, null)
                } else {
                    mAccountManager.removeAccount(accounts[i], new AccountManagerCallback<Boolean>() {
                        @Override
                        void run(final AccountManagerFuture<Boolean> future) {
                            final boolean returnStatus = future.getResult()
                            if(returnStatus) {
                                LogDisplay.callLog(LOG_TAG,'Remove account successful',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
                                // Remove the Periodic Sync for the account
                                MovieMagicSyncAdapterUtility.removePeriodicSync(accounts[i], context)
                                // Add the account and authToken
                                addAcountAndAuthToken(intent, account, authtoken, tmdbUSerName, tmdbAccountId, accountPassword)
                            } else {
                                LogDisplay.callLog(LOG_TAG,'Remove account failed',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
                                Toast.makeText(getBaseContext(), getString(R.string.cannot_perform_operation_message), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }, null)
                }
            }
        } else {
            LogDisplay.callLog(LOG_TAG,'Existing account, update password',LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
            mAccountManager.setPassword(account, accountPassword)
            // Let the authenticator know that login is done
            setAccountAuthenticatorResult(intent.getExtras())
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    /**
     * This method adds the account to the account manager and set the authToken
     * @param account The account to be added
     * @param authtoken The authToken to set for the account
     * @param tmdbUSerName Name of the TMDb user which is stored as user data
     * @param tmdbAccountId Account id of Tmdb account which is stored as user data
     * @param accountPassword Password of the account
     */
    private addAcountAndAuthToken(
            final Intent intent, final Account account, final String authtoken, final String tmdbUSerName, final String tmdbAccountId, final String accountPassword) {
        // Since it's a new account so create the account and set the authToken
        // (Not setting the auth token will cause another call to the server to authenticate the user)
        final Bundle userDataBundle = new Bundle()
        userDataBundle.putString(AccountManager.KEY_USERDATA,tmdbUSerName)
        userDataBundle.putString(GlobalStaticVariables.TMDB_USERDATA_ACCOUNT_ID,tmdbAccountId)
        mAccountManager.addAccountExplicitly(account, accountPassword, userDataBundle)
        mAccountManager.setAuthToken(account, mAuthTokenType, authtoken)
        //Now configure the Periodic Sync for the new account
        MovieMagicSyncAdapterUtility.onAccountCreated(account, getApplicationContext())
        // Let the authenticator know that login is done
        setAccountAuthenticatorResult(intent.getExtras())
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * This method will launch the sign up page of TMDb
     */
    private void launchSignUpPage() {
        //Start an intent and re-direct user to TMDb sign-up page
        final Intent intent = new Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(getString(R.string.tmdb_sign_up_page)))
        final Context context = getApplicationContext() as Context
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * This method will cancel the login and return back the user to previous state
     */
    private void cancelLogin() {
        // Let the authenticator know that user cancelled login
        setAccountAuthenticatorResult(null)
        setResult(AccountManager.ERROR_CODE_CANCELED, null)
        finish()
    }

    @Override
    void onBackPressed() {
        LogDisplay.callLog(LOG_TAG, 'onBackPressed is called', LogDisplay.MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG)
        super.onBackPressed()
        //Start the animation
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }
}

