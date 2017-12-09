/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.ComponentCallbacks2
import android.content.pm.PackageManager
import android.support.annotation.NonNull
import android.support.v7.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.youtube.player.YouTubeApiServiceUtil
import com.google.android.youtube.player.YouTubeInitializationResult
import com.moviemagic.dpaul.android.app.adapter.MovieGridRecyclerAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.NetworkReceiver
import com.moviemagic.dpaul.android.app.backgroundmodules.Utility
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.syncadapter.MovieMagicSyncAdapterUtility
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
public class MovieMagicMainActivity extends AppCompatActivity implements
        GridMovieFragment.CallbackForGridItemClick, GridMovieFragment.CollectionColorChangeCallback,
        HomeMovieFragment.CallbackForHomeMovieClick, HomeMovieFragment.CallbackForShowAllButtonClick,
        ComponentCallbacks2 {
    private static final String LOG_TAG = MovieMagicMainActivity.class.getSimpleName()
    private static final String STATE_APP_TITLE = 'app_title'
    private static final String GRID_FRAGMENT_FLAG = 'grid_fragment_flag'
    private NavigationView mNavigationView
    private TextView mNavPanelUserNameTextView, mNavPanelUserIdTextView
    private Button mNavPanelLoginButton
    private AccountManager mAccountManager
    public static boolean isUserLoggedIn = false
    private NetworkReceiver networkReceiver
    private AsyncTask mUpdateMenuCounterAsyncTask
    private boolean mIsGridFragment = false

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        setContentView(R.layout.activity_movie_magic_main)

        // Nullify the theme's background to reduce overdraw
        getWindow().setBackgroundDrawable(null)

        // Show the EULA - first install or any update to the software
        new MyMoviesEULA(this).checkAndShowEula()

        final Toolbar toolbar = findViewById(R.id.main_activity_toolbar) as Toolbar
        setSupportActionBar(toolbar)

        final DrawerLayout drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        mNavigationView = findViewById(R.id.nav_view) as NavigationView
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                takeActionOnNavigationItemSelected(item)
                return true
            }
        })

        final View navigationHeader = mNavigationView.getHeaderView(0)
        mNavPanelUserNameTextView = navigationHeader.findViewById(R.id.nav_drawer_user_name) as TextView
        mNavPanelUserIdTextView = navigationHeader.findViewById(R.id.nav_drawer_user_id) as TextView
        mNavPanelLoginButton = navigationHeader.findViewById(R.id.nav_drawer_log_in) as Button
        mNavPanelLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG,'Login button is clicked',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                loginToTmdbAccount()
            }
        })

        // Show the drawer when application is launched for the first time
        final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE)
        final boolean firstTimeDrawerShow = sharedPref.getBoolean(getString(R.string.app_pref_show_drawer_first_time_key),true)
        if(firstTimeDrawerShow) {
            drawer.openDrawer(GravityCompat.START)
            // Now set the value to false, so that drawer is not shown next time
            final SharedPreferences.Editor editor = sharedPref.edit()
            editor.putBoolean(getString(R.string.app_pref_show_drawer_first_time_key), false)
            editor.commit()
        }

        // Create an instance of AccountManager
        mAccountManager = AccountManager.get(getApplicationContext())

        // Initialize the SyncAdapter
        MovieMagicSyncAdapterUtility.initializeSyncAdapter(getApplicationContext())
        //*** Comment before release **********************
        //MovieMagicSyncAdapterUtility.syncImmediately(this)

        // Registers BroadcastReceiver to track network connection changes. This is more lightweight
        // than declaring a <receiver> in the manifest to avoid not waking up the app when not in use (less battery usage)
        final IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        networkReceiver = new NetworkReceiver()
        registerReceiver(networkReceiver, filter)

        // Set default values for Settings
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preference_xml, false)
        
        // Check if user already logged in to TMDb account. If yes then perform the required steps
        checkUserLoginAndPerformAction()

        //Update the user list menu counter
        //Program fails if 'Void' is used for parameter, could be because of groovy compiler??
        //So to get rid of the problem a 'dummy' value is passed
        mUpdateMenuCounterAsyncTask = new UpdateMenuCounter(this, mNavigationView).execute(['dummy'] as String[])

        //Check to ensure Youtube exists on the device
        final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(getApplicationContext())
        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            result.getErrorDialog(this, 0).show()
        }

        //Load the Home Fragment
        if(savedInstanceState == null) {
            LogDisplay.callLog(LOG_TAG,'This is first time, so load homeFragment..',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            loadHomeFragment()
        } else {
            LogDisplay.callLog(LOG_TAG,'This is restore scenario..so need to load homeFragment as is',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        }
    }

    @Override
    protected void onStart() {
        super.onStart()
        LogDisplay.callLog(LOG_TAG,'onStart is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        // Check if the user is online or not, if not then open a dialog
        final boolean isOnline = Utility.isOnline(getApplicationContext())
        if(!isOnline) {
            LogDisplay.callLog(LOG_TAG,'Not connected to network!!',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            showNotConnectedErrorDialog()
        } else {
            LogDisplay.callLog(LOG_TAG,'Connected to network!!',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG,"WiFi connection flag: $GlobalStaticVariables.WIFI_CONNECTED",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG,"Mobile connection flag: $GlobalStaticVariables.MOBILE_CONNECTED",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            // If user has selected only WiFi but user is online without WiFi then show a dialog
            if(Utility.isOnlyWifi(getApplicationContext()) & !GlobalStaticVariables.WIFI_CONNECTED) {
                showNotConnectedToWiFiErrorDialog()
            }
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        LogDisplay.callLog(LOG_TAG,'onSaveInstanceState is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        outState.putString(STATE_APP_TITLE,getSupportActionBar().getTitle().toString())
        outState.putBoolean(GRID_FRAGMENT_FLAG,mIsGridFragment)
        // Now call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG,'onRestoreInstanceState is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        // Always call the superclass first, so that Bundle is retrieved properly
        super.onRestoreInstanceState(savedInstanceState)
        getSupportActionBar().setTitle(savedInstanceState.getCharSequence(STATE_APP_TITLE,'error'))
        mIsGridFragment = savedInstanceState.getBoolean(GRID_FRAGMENT_FLAG,false)
    }

    @Override
    protected void onPause() {
        super.onPause()
        LogDisplay.callLog(LOG_TAG,'onPause is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
    }

    @Override
    protected void onResume() {
        super.onResume()
        LogDisplay.callLog(LOG_TAG,'onResume is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
    }

    @Override
    protected void onRestart() {
        super.onRestart()
        LogDisplay.callLog(LOG_TAG,'onRestart is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        //Update the user list menu counter
        //Program fails if 'Void' is used for parameter, could be because of groovy compiler??
        //So to get rid of the problem a 'dummy' value is passed
        mUpdateMenuCounterAsyncTask = new UpdateMenuCounter(this, mNavigationView).execute(['dummy'] as String[])
    }

    @Override
    protected void onStop() {
        super.onStop()
        LogDisplay.callLog(LOG_TAG,'onStop is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
    }

    @Override
    protected void onDestroy() {
        LogDisplay.callLog(LOG_TAG,'onDestroy is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        // Unregisters BroadcastReceiver when app is destroyed.
        if(networkReceiver) {
            unregisterReceiver(networkReceiver)
        }
        // Cancel the AsyncTask
        mUpdateMenuCounterAsyncTask.cancel(true)
        super.onDestroy()
    }

    @Override
    public void onBackPressed() {
        LogDisplay.callLog(LOG_TAG,'onBackPressed is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            final int fargmentCount = getFragmentManager().getBackStackEntryCount()
            LogDisplay.callLog(LOG_TAG,"onBackPressed:Fragment count->$fargmentCount",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)

            // If it's Grid fragment then go back to home Fragment
            if(mIsGridFragment) {
                // Set the title of home page
                setItemTitleAndSubTitle(getString(R.string.app_name))
                // Now load the home fragment
                loadHomeFragment()
            } else { // Otherwise call super method and exit
                super.onBackPressed()
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu)

        // Get the SearchView and set the searchable configuration
        final SearchManager searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        final SearchView searchView = menu.findItem(R.id.menu_action_search).getActionView() as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()))
        searchView.setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        searchView.setSubmitButtonEnabled(true) // Enable submit button

        return true
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_action_settings) {
            openSettingsActivity()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    public boolean takeActionOnNavigationItemSelected(final MenuItem item) {
        LogDisplay.callLog(LOG_TAG, 'takeActionOnNavigationItemSelected is called.', LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        // Handle navigation view item clicks here.
        final int id = item.getItemId()

        if (id == R.id.nav_home) {
            setItemTitleAndSubTitle(getString(R.string.app_name))
            loadHomeFragment()
        } else if (id == R.id.nav_tmdb_popular) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_tmdb_popular))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_POPULAR)
        } else if (id == R.id.nav_tmdb_toprated) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_tmdb_toprated))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED)
        } else if (id == R.id.nav_tmdb_nowplaying) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_tmdb_nowplaying))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING)
        } else if (id == R.id.nav_tmdb_upcoming) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_tmdb_upcoming))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING)
        } else if (id == R.id.nav_user_watched) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_user_watched))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WATCHED)
        } else if (id == R.id.nav_user_wishlist) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_user_wishlist))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WISH_LIST)
        } else if (id == R.id.nav_user_favourite) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_user_favourite))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_FAVOURITE)
        } else if (id == R.id.nav_user_collection) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_user_collection))
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_COLLECTION)
        } else if (id == R.id.nav_tmdb_user_watchlist) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_tmdb_user_watchlist) +" (TMDb)")
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST)
        } else if (id == R.id.nav_tmdb_user_favourite) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_tmdb_user_favourite) +" (TMDb)")
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE)
        } else if (id == R.id.nav_tmdb_user_rated) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_tmdb_user_rated) +" (TMDb)")
            loadGridFragment(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED)
        } else if (id == R.id.nav_menu_settings) {
            // Disable checkable behaviour
            item.setCheckable(false)
            openSettingsActivity()
        } else if (id == R.id.nav_menu_logout) {
            logoutFromTmdbAccount()
        } else if(id == R.id.nav_menu_rate_the_app) {
            // Disable checkable behaviour
            item.setCheckable(false)
            launchGooglePlayForRatingAndFeedback()
        } else if(id == R.id.nav_menu_contact_developer) {
            // Disable checkable behaviour
            item.setCheckable(false)
            contactDeveloperAction()
        } else if(id == R.id.nav_menu_developer_website) {
            // Disable checkable behaviour
            item.setCheckable(false)
            openDeveloperWebsite()
        } else if(id == R.id.nav_menu_privacy_policy) {
            // Disable checkable behaviour
            item.setCheckable(false)
            openPrivacyPolicy()
        } else if(id == R.id.nav_menu_eula) {
            // Disable checkable behaviour
            item.setCheckable(false)
            // Show the EULA to user
            new MyMoviesEULA(this).showEula(false)
        } else if(id == R.id.nav_menu_donate) {
            // Disable checkable behaviour
            item.setCheckable(false)
            openDonateActivity()
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
    }

    /**
     * Login logic to allow user to login to TMDb account
     */
    private void loginToTmdbAccount() {
        LogDisplay.callLog(LOG_TAG,'loginToTmdbAccount is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        final AccountManagerFuture<Bundle> amFuture= mAccountManager.addAccount(getString(R.string.authenticator_account_type),
                GlobalStaticVariables.AUTHTOKEN_TYPE_FULL_ACCESS,null,null,this,new AccountManagerCallback<Bundle>() {
            @Override
            void run(final AccountManagerFuture<Bundle> future) {
                try { //getResult will throw exception if login is not successful
                    final Bundle bundle = future.getResult()
                    // Set the name to the TMDb user name, if it's not present then set the app name
                    if(bundle.getString(AccountManager.KEY_USERDATA)) {
                        mNavPanelUserNameTextView.setText(bundle.getString(AccountManager.KEY_USERDATA))
                        // Show the user id TextView and set the correct value
                        mNavPanelUserIdTextView.setVisibility(TextView.VISIBLE)
                        mNavPanelUserIdTextView.setText(bundle.getString(AccountManager.KEY_ACCOUNT_NAME))
                        // Hide the Login button
                        mNavPanelLoginButton.setVisibility(Button.GONE)
                        // Show the tmdb menu & logout
                        final Menu tmdbMenu = mNavigationView.getMenu()
                        tmdbMenu.findItem(R.id.nav_tmdb_user_menu_items).setVisible(true)
                        tmdbMenu.findItem(R.id.nav_menu_logout).setVisible(true)
                        // Set the login flag to true
                        isUserLoggedIn = true
                        // Now show a message to the user
                        Toast.makeText(getBaseContext(),getString(R.string.tmdb_successful_login_message),Toast.LENGTH_SHORT).show()
                        LogDisplay.callLog(LOG_TAG,"Login successful, accout bundle: $bundle",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                    } else {
                        mNavPanelUserNameTextView.setText(getString(R.string.app_name))
                    }
                } catch (final Exception e) {
                    LogDisplay.callLog(LOG_TAG,"Login failed, error message: ${e.getMessage()}",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                    Log.e(LOG_TAG, "Error: ${e.message}", e)
                }
            }
        }, null)
    }

    /**
     * Logout logic to allow user to logout from TMDb account
     */
    private void logoutFromTmdbAccount() {
        LogDisplay.callLog(LOG_TAG,'logoutFromTmdbAccount is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        final String accountType = getString(R.string.authenticator_account_type)
        // Application supports only a single account, so safe to use this logic
        final Account[] accounts = mAccountManager.getAccountsByType(accountType)
        final Context context = getApplicationContext()
        if( accounts && accounts.size() == 1) {
            LogDisplay.callLog(LOG_TAG,"Removing account -> ${accounts[0].name}",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            // Remove the account
            if (Build.VERSION.SDK_INT >= 21) {
                mAccountManager.removeAccount(accounts[0], this, new AccountManagerCallback<Bundle>() {
                    @Override
                    void run(final AccountManagerFuture<Bundle> future) {
                        try { //getResult will throw exception if logout is not successful
                            // Remove the Periodic Sync for the account
                            MovieMagicSyncAdapterUtility.removePeriodicSync(accounts[0], context)
                            final Bundle bundle = future.getResult()
                            finishLogout()
                            LogDisplay.callLog(LOG_TAG,"Remove account successful, accout bundle: $bundle",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                        } catch (final Exception e) {
                            LogDisplay.callLog(LOG_TAG,"Remove account failed, error message: ${e.getMessage()}",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                            Toast.makeText(getBaseContext(), getString(R.string.cannot_perform_operation_message), Toast.LENGTH_SHORT).show()
                            Log.e(LOG_TAG, "Error: ${e.message}", e)
                        }
                    }
                }, null)
            } else {
                mAccountManager.removeAccount(accounts[0], new AccountManagerCallback<Boolean>() {
                    @Override
                    void run(final AccountManagerFuture<Boolean> future) {
                        final boolean returnStatus = future.getResult()
                        if(returnStatus) {
                            // Remove the Periodic Sync for the account
                            MovieMagicSyncAdapterUtility.removePeriodicSync(accounts[0], context)
                            finishLogout()
                            LogDisplay.callLog(LOG_TAG,'Remove account successful',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                        } else {
                            LogDisplay.callLog(LOG_TAG,'Remove account failed',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                            Toast.makeText(getBaseContext(), getString(R.string.cannot_perform_operation_message), Toast.LENGTH_SHORT).show()
                        }
                    }
                }, null)
            }
        } else if (accounts.size() > 1) {
            LogDisplay.callLog(LOG_TAG,"Error.More than one account, number of accounts -> ${accounts.size()}",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            Toast.makeText(getBaseContext(), getString(R.string.cannot_perform_operation_message), Toast.LENGTH_SHORT).show()
        } else {
            LogDisplay.callLog(LOG_TAG,'Error.No account found',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            Toast.makeText(getBaseContext(), getString(R.string.cannot_perform_operation_message), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Finish the logout activities
     */
    private void finishLogout() {
        LogDisplay.callLog(LOG_TAG,'finishLogout is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        // Hide the user id TextView
        mNavPanelUserIdTextView.setVisibility(TextView.GONE)
        // Hide the TMDb user menu & logout menu
        final Menu tmdbMenu = mNavigationView.getMenu()
        tmdbMenu.findItem(R.id.nav_tmdb_user_menu_items).setVisible(false)
        tmdbMenu.findItem(R.id.nav_menu_logout).setVisible(false)
        // Show the Login button
        mNavPanelLoginButton.setVisibility(Button.VISIBLE)
        // Set the name as application name
        mNavPanelUserNameTextView.setText(getString(R.string.app_name))
        // Now call this method so that a dummy account gets created and setup for sync adapter
        MovieMagicSyncAdapterUtility.initializeSyncAdapter(getApplicationContext())
        // Take the user to home screen
        setItemTitleAndSubTitle(getString(R.string.app_name))
        loadHomeFragment()
        // Set the home item as selected
        mNavigationView.getMenu().getItem(0).setChecked(true)
        // Reset the login flag
        isUserLoggedIn = false
        // Show a message to the user
        Snackbar.make(findViewById(R.id.content_movie_magic_main_layout), getString(R.string.tmdb_successful_logout_message), Snackbar.LENGTH_LONG).show()
    }

    /**
     * Check if the user is already logged in to TMDb (valid account exist or not)
     */
    private void checkUserLoginAndPerformAction() {
        LogDisplay.callLog(LOG_TAG,'checkUserLoginAndPerformAction is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        //Check if any account exists
        final Account[] accounts = mAccountManager.getAccountsByType(getString(R.string.authenticator_account_type))
        if(accounts.size() == 1) {
            LogDisplay.callLog(LOG_TAG,"Existing account. Account name->${accounts[0].name}",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            //Application can have only one account, so safe to use the following line
            if(accounts[0].name == getString(R.string.app_name)) {
                LogDisplay.callLog(LOG_TAG, 'Default SyncAdapter account, so user is not logged in & no action needed', LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            } else { //Application can have only one account, so if it's not SyncAdapter then must be user's account
                if(mAccountManager.getUserData(accounts[0],AccountManager.KEY_USERDATA)) {
                    mNavPanelUserNameTextView.setText(mAccountManager.getUserData(accounts[0],AccountManager.KEY_USERDATA))
                } else {
                    mNavPanelUserNameTextView.setText(getString(R.string.app_name))
                }
                // Show the user id TextView and set the correct value
                mNavPanelUserIdTextView.setVisibility(TextView.VISIBLE)
                mNavPanelUserIdTextView.setText(accounts[0].name)
                // Hide the Login button
                mNavPanelLoginButton.setVisibility(Button.GONE)
                // Show the tmdb menu & logout
                final Menu tmdbMenu = mNavigationView.getMenu()
                tmdbMenu.findItem(R.id.nav_tmdb_user_menu_items).setVisible(true)
                tmdbMenu.findItem(R.id.nav_menu_logout).setVisible(true)
                // Set the login flag
                isUserLoggedIn = true
            }
        } else if (accounts.size() > 1) {
            LogDisplay.callLog(LOG_TAG,"Error.More than one account, number of accounts -> ${accounts.size()}",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG,'Error.No account found',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        }
    }

    /**
     * Show a dialog when user is not connected to network
     */
    private void showNotConnectedErrorDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AppCompatAlertDialogTheme)
        builder.setTitle(getString(R.string.not_connected_dialog_title))
                .setMessage(getString(R.string.not_connected_dialog_message))
        builder.setPositiveButton(getString(R.string.not_connected_open_settings_button), new DialogInterface.OnClickListener() {
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, 'Dialog open network settings is clicked, go and open it', LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                openSystemSettings()
            }
        })
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, 'Dialog cancel is clicked. No action needed.', LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                dialog.dismiss()
            }
        })
        // Create the AlertDialog & show
        final AlertDialog dialog = builder.create()
        dialog.show()
    }

    /**
     * Show a dialog when user is online without WiFi and selected settings as to use only WiFi
     */
    private void showNotConnectedToWiFiErrorDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AppCompatAlertDialogTheme)
        builder.setTitle(getString(R.string.not_wifi_connected_dialog_title))
                .setMessage(getString(R.string.not_wifi_connected_dialog_message))
        builder.setPositiveButton(getString(R.string.not_wifi_connected_open_settings_button), new DialogInterface.OnClickListener() {
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, 'Dialog change settings is clicked, go and open settings activity', LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                openSettingsActivity()
            }
        })
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, 'Dialog cancel is clicked. No action needed.', LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                dialog.dismiss()
            }
        })
        // Create the AlertDialog & show
        final AlertDialog dialog = builder.create()
        dialog.show()
    }

    /**
     * Open the system settings if users selects so when there is no network
     */
    private void openSystemSettings() {
        final Intent intent = new Intent(Settings.ACTION_SETTINGS)
        startActivity(intent)
    }

    /**
     * Open the system settings if users selects so when there is no network
     */
    private void openSettingsActivity() {
        final Intent intent = new Intent(this, SettingsActivity.class)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    /**
     * Load the Home Fragment
     */
    private void loadHomeFragment() {
        LogDisplay.callLog(LOG_TAG, 'loadHomeFragment is called.', LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        final HomeMovieFragment homeMovieFragment = new HomeMovieFragment()
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
        //Set the custom animation
        fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_in_animation,0)
        fragmentTransaction.replace(R.id.content_movie_magic_main_layout, homeMovieFragment)
        fragmentTransaction.commit()

        // Set the mIsGridFragment to false
        mIsGridFragment = false

        // Set the nave menu item as checked
        mNavigationView.setCheckedItem(R.id.nav_home)
    }

    /**
     * Load the Grid Fragment of the movie selected movie category
     * @param category Movie category
     */
    private void loadGridFragment(final String category) {
        LogDisplay.callLog(LOG_TAG, 'loadGridFragment is called.', LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        //Set this flag as false so that theme primaryDark color is used in the grid
        MovieGridRecyclerAdapter.collectionGridFlag = false
        final Bundle bundle = new Bundle()
        //Since it's not for collection category, so collection id is passed as zero
        final Uri uri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategoryAndCollectionId(category,0)
        bundle.putParcelable(GlobalStaticVariables.MOVIE_CATEGORY_AND_COLL_ID_URI,uri)
        final GridMovieFragment gridMovieFragment = new GridMovieFragment()
        gridMovieFragment.setArguments(bundle)
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
        //Set the custom animation
        fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_in_animation,0)
        fragmentTransaction.replace(R.id.content_movie_magic_main_layout, gridMovieFragment)
        fragmentTransaction.commit()

        // Set the mIsGridFragment to true
        mIsGridFragment = true
    }

    /**
     * This method sets the Title of the Activity
     * @param title Title to be set for the activity
     */
    private void setItemTitleAndSubTitle(final CharSequence title){
        LogDisplay.callLog(LOG_TAG,"The drawer menu $title is called",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        getSupportActionBar().setTitle(title)
        // Filter menu of GridFragment can set the subtitle, so reset it
        getSupportActionBar().setSubtitle('')
    }

    /**
     * Fragment callback method for HomeMovie Item - called when a movie item is clicked
     * @param movieId  Movie id of the selected movie
     * @param viewHolder HomeMovieApterViewHolder
     */
    @Override
    void onHomeMovieItemSelected(final int movieId, final String movieCategory) {
        final Bundle bundle = new Bundle()
        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,movieCategory)
        final Intent intent = new Intent(this, DetailMovieActivity.class)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    /**
     * Fragment callback method for HomeMovie Show All button for In Cinemas or Coming Soon
     * @param movieCategory The movie category (now_playing or upcoming) of the corresponding button click
     */
    @Override
    void onShowAllButtonClicked(final String movieCategory) {
        if(movieCategory == GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_tmdb_nowplaying))
            // Set the corresponding item in nav drawer
            mNavigationView.getMenu().getItem(3).setChecked(true)
        } else if (movieCategory == GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING) {
            setItemTitleAndSubTitle(getString(R.string.drawer_menu_tmdb_upcoming))
            // Set the corresponding item in nav drawer
            mNavigationView.getMenu().getItem(4).setChecked(true)
        } else {
            LogDisplay.callLog(LOG_TAG,"Unknow category sent by HomeFragment. Category->$movieCategory",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        }
        loadGridFragment(movieCategory)
    }

    /**
     * This method launches an intent to open the application in GooglePlay store
     */
    protected void launchGooglePlayForRatingAndFeedback() {
        final String packageName = getPackageName()
        final Intent intent = new Intent(Intent.ACTION_VIEW)
        //TODO
        // use this for google play store apk
        intent.setData(Uri.parse("market://details?id=$packageName"))
        // use this for amazon apk
//        intent.setData(Uri.parse("amzn://apps/android?p=$packageName"))
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent)
        } else {
            LogDisplay.callLog(LOG_TAG,'Google Play store is not installed in this device',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        }
    }

    /**
     * This method launches the email Intent to share feedback / comment
     */
    protected void contactDeveloperAction() {
        final String codeName = Build.VERSION.CODENAME
        final String release = Build.VERSION.RELEASE
        final String brand = Build.BRAND
        final String device = Build.DEVICE
        final String manufacturer = Build.MANUFACTURER
        final String model = Build.MODEL
        final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),PackageManager.GET_ACTIVITIES)
        final String versionCode = Integer.toString(packageInfo.versionCode)
        final String versionName = packageInfo.versionName

        final String[] emailAddress = ["${getString(R.string.developer_email_id)}"]
        final String emailSubject = "${getString(R.string.app_name)} (Version $versionName) - Feedback"
        final String emailBody = "Android Code Name: $codeName\nAndroid Release: $release\nBrand: $brand\nDevice: $device" +
                "\nManufacturer: $manufacturer\nModel: $model\nApp Version Code: $versionCode" +
                "\nApp Version Name: $versionName\n\nFeedback / Comments - "
        final Intent intent = new Intent(Intent.ACTION_SENDTO)
        intent.setData(Uri.parse("mailto:")) // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
        intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        intent.putExtra(Intent.EXTRA_TEXT, emailBody)
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent)
        } else {
            LogDisplay.callLog(LOG_TAG,'No email client installed in this device',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
        }
    }

    /**
     * Launch the Developer's website
     */
    protected void openDeveloperWebsite() {
        final String url = getString(R.string.developer_web_address)
        Intent i = new Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(url))
        startActivity(i)
    }

    /**
     * Launch the Developer's website
     */
    protected void openPrivacyPolicy() {
        final String url = getString(R.string.application_privacy_policy)
        Intent i = new Intent(Intent.ACTION_VIEW)
        i.setData(Uri.parse(url))
        startActivity(i)
    }

    /**
     * Launch the Donate Activity
     */
    protected void openDonateActivity() {
        final Intent intent = new Intent(this, DonateActivity.class)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    /**
     * Updating of menu counter is tightly coupled with main_activity_menu activity, so no separate class is
     * created for the AsyncTask
     */
    private static class UpdateMenuCounter extends AsyncTask<String, Void, Integer[]> {
        private final Context context
        private final NavigationView navigationView

        public UpdateMenuCounter(final Context context, final NavigationView navigationView) {
            LogDisplay.callLog(LOG_TAG,'UpdateMenuCounter constructor is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            this.context = context
            this.navigationView = navigationView
        }
        @Override
        protected Integer[] doInBackground(final String... params) {
            final Integer[] result = new Integer[7]
            Cursor cursor
            //Get the count for watched
            cursor = context.getContentResolver().query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                    [GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WATCHED] as String[],
                    null)
            if(cursor.moveToFirst()) result[0] = cursor.getCount()
            //Close the cursor
            if(cursor) cursor.close()
            //Get the count for wish list
            cursor = context.getContentResolver().query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                    [GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WISH_LIST] as String[],
                    null)
            if(cursor.moveToFirst()) result[1] = cursor.getCount()
            //Close the cursor
            if(cursor) cursor.close()
            //Get the count for favourite
            cursor = context.getContentResolver().query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                    [GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_FAVOURITE] as String[],
                    null)
            if(cursor.moveToFirst()) result[2] = cursor.getCount()
            //Close the cursor
            if(cursor) cursor.close()
            //Get the count for wish list
            cursor = context.getContentResolver().query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                    [GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_COLLECTION] as String[],
                    null)
            if(cursor.moveToFirst()) result[3] = cursor.getCount()
            //Close the cursor
            if(cursor) cursor.close()

            // Set the counters for Tmdb List if user is logged in
            if(isUserLoggedIn) {
                //Get the count for TMDb Watchlists
                cursor = context.getContentResolver().query(
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                        null,
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                        [GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST] as String[],
                        null)
                if(cursor.moveToFirst()) result[4] = cursor.getCount()
                //Close the cursor
                if(cursor) cursor.close()
                //Get the count for TMDb Favourites
                cursor = context.getContentResolver().query(
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                        null,
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                        [GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE] as String[],
                        null)
                if(cursor.moveToFirst()) result[5] = cursor.getCount()
                //Close the cursor
                if(cursor) cursor.close()
                //Get the count for TMDb Rated
                cursor = context.getContentResolver().query(
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                        null,
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? ",
                        [GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED] as String[],
                        null)
                if(cursor.moveToFirst()) result[6] = cursor.getCount()
                //Close the cursor
                if(cursor) cursor.close()
            } else {
                result[4] = 0
                result[5] = 0
                result[6] = 0
            }
            return result
        }

        @Override
        protected void onPostExecute(final Integer[] result) {
            //Set the Watched counter
            final TextView watchedView = (TextView) navigationView.getMenu().findItem(R.id.nav_user_watched).getActionView()
            watchedView.setText(result[0] > 0 ? String.valueOf(result[0]) : null)
            //Set the wish list counter
            final TextView wishListView = (TextView) navigationView.getMenu().findItem(R.id.nav_user_wishlist).getActionView()
            wishListView.setText(result[1] > 0 ? String.valueOf(result[1]) : null)
            //Set the favourite counter
            final TextView favouriteView = (TextView) navigationView.getMenu().findItem(R.id.nav_user_favourite).getActionView()
            favouriteView.setText(result[2] > 0 ? String.valueOf(result[2]) : null)
            //Set the collection counter
            final TextView collectionView = (TextView) navigationView.getMenu().findItem(R.id.nav_user_collection).getActionView()
            collectionView.setText(result[3] > 0 ? String.valueOf(result[3]) : null)

            // Set the TMDb lists counter only if the user is logged in
            if(isUserLoggedIn) {
                //Set the TMDb Watchlists counter
                final TextView tmdbWatchlistView = (TextView) navigationView.getMenu().findItem(R.id.nav_tmdb_user_watchlist).getActionView()
                tmdbWatchlistView.setText(result[4] > 0 ? String.valueOf(result[4]) : null)
                //Set the TMDb Favourites counter
                final TextView tmdbFavouriteView = (TextView) navigationView.getMenu().findItem(R.id.nav_tmdb_user_favourite).getActionView()
                tmdbFavouriteView.setText(result[5] > 0 ? String.valueOf(result[5]) : null)
                //Set the TMDb Rated counter
                final TextView tmdbRatedView = (TextView) navigationView.getMenu().findItem(R.id.nav_tmdb_user_rated).getActionView()
                tmdbRatedView.setText(result[6] > 0 ? String.valueOf(result[6]) : null)
            }
        }
    }

    // Override the callback method of GridMovieFragment
    // Once an item is clicked then it will be called and it will launch the DetailMovie activity
    @Override
    public void onMovieGridItemSelected(final int movieId, final String movieCategory) {
        final Intent intent = new Intent(this, DetailMovieActivity.class)
        final Bundle bundle = new Bundle()
        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,movieCategory)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    @Override
    public void notifyCollectionColorChange() {
        //Do nothing. This is not called for main activity
        //Implemented, otherwise application will give error as GridFragment onAttach has check if it is implemented
    }

    /**
     * Methods related to ComponentCallbacks2 - for memory monitoring
     *
     */
    @Override
    public void onTrimMemory(final int level) {
        LogDisplay.callLog(LOG_TAG,'TonTrimMemory is called',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)

        // Determine which lifecycle or system event was raised.
        switch (level) {
            /*
                Release any UI objects that currently hold memory.
                The user interface has moved to the background.
            */
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                LogDisplay.callLog(LOG_TAG,'TRIM_MEMORY_UI_HIDDEN -> app moved to background',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                break
            /*
                Release any memory that your app doesn't need to run.
                The device is running low on memory while the app is running.
                The event raised indicates the severity of the memory-related event.
                If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                begin killing background processes.
            */
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
                LogDisplay.callLog(LOG_TAG,'TRIM_MEMORY_RUNNING_MODERATE -> device is running low on memory while the app is running',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                break
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
                LogDisplay.callLog(LOG_TAG,'TRIM_MEMORY_RUNNING_LOW -> device is running low on memory while the app is running',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                break
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                LogDisplay.callLog(LOG_TAG,'TRIM_MEMORY_RUNNING_CRITICAL -> device is running low on memory while the app is running',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                break
            /*
                Release as much memory as the process can.
                The app is on the LRU list and the system is running low on memory.
                The event raised indicates where the app sits within the LRU list.
                If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                the first to be terminated.
            */
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
                LogDisplay.callLog(LOG_TAG,'TRIM_MEMORY_BACKGROUND -> app is on the LRU list and the system is running low on memory',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                break
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
                LogDisplay.callLog(LOG_TAG,'TRIM_MEMORY_BACKGROUND -> app is on the LRU list and the system is running low on memory',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                break
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                LogDisplay.callLog(LOG_TAG,'TRIM_MEMORY_BACKGROUND -> app is on the LRU list and the system is running low on memory',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                break

            default:
                /*
                  Release any non-critical data structures.
                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                LogDisplay.callLog(LOG_TAG,'The app received an unrecognized memory level value from the system. Treat this as a generic low-memory message.',LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
                break
        }
    }
}
