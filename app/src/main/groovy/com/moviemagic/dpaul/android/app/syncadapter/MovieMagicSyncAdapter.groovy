/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.syncadapter

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.AbstractThreadedSyncAdapter
import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SyncResult
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v7.preference.PreferenceManager
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.DetailMovieActivity
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.LoadMovieDetails
import com.moviemagic.dpaul.android.app.backgroundmodules.SearchDatabaseTable
import com.moviemagic.dpaul.android.app.backgroundmodules.Utility
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.JsonParse
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicDbHelper
import com.squareup.picasso.Picasso
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class MovieMagicSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = MovieMagicSyncAdapter.class.getSimpleName()

    //This variable indicates the number of pages for initial load.
    private final static int MAX_PAGE_DOWNLOAD = 3
    //Define a variable for api page count
    private static int mTotalPage = 0
    //Define a variable for Tmdb user movie list page count
    private static int mTotalPageTmdbUserList = 0
    // Define a variable to contain a content resolver instance
    private final ContentResolver mContentResolver
    private final Context mContext
    // Set the Date & Time stamp which is used for all the new records, this is used while housekeeping so
    // a single constant value is used for all the records
    private String mDateTimeStamp
    private boolean mFirstTotalPageRead = true
    // Flags to track if exception happened
    private boolean mPublicTmdbProcessingException = false



    //Columns to fetch from movie_basic_info table
    private static final String[] MOVIE_BASIC_INFO_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_BASIC_ID = 0
    final static int COL_MOVIE_BASIC_MOVIE_ID = 1
    final static int COL_MOVIE_BASIC_RELEASE_DATE = 2
    final static int COL_MOVIE_BASIC_TITLE = 3
    final static int COL_MOVIE_BASIC_POSTER_PATH = 4
    final static int COL_MOVIE_BASIC_BACKDROP_PATH = 5
    final static int COL_MOVIE_BASIC_MOVIE_CATEGORY = 6

    MovieMagicSyncAdapter(final Context context, final boolean autoInitialize) {
        super(context, autoInitialize)
        mContentResolver = context.getContentResolver()
        mContext = context
    }
    /**
     * -- Needed if the min API level is 11 or above --
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
//    MovieMagicSyncAdapter(
//            Context context,
//            boolean autoInitialize,
//            boolean allowParallelSyncs) {
//        super(context, autoInitialize, allowParallelSyncs)
//        mContentResolver = context.getContentResolver()
//    }

    @Override
    void onPerformSync(
            final Account account, final Bundle extras, final String authority, final ContentProviderClient provider, final SyncResult syncResult) {
        LogDisplay.callLog(LOG_TAG,'onPerformSync is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        if(Utility.isReadyToDownload(mContext.getApplicationContext())) {
            mDateTimeStamp = Utility.getTodayDate()
            List<ContentValues> contentValues = []
            //mTotalPage is set to 1 so that at least first page is downloaded in downloadMovieList
            // later this variable is overridden by the total page value retrieved from the api
            mTotalPage = 1
            for (final i in 1..MAX_PAGE_DOWNLOAD) {
                contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_POPULAR, i)
                if (contentValues) {
                    insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_POPULAR)
                    contentValues = []
                } else {
                    LogDisplay.callLog(LOG_TAG, "No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_POPULAR", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                }
            }
            mTotalPage = 1
            mFirstTotalPageRead = true
            for (final i in 1..MAX_PAGE_DOWNLOAD) {
                contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED, i)
                if (contentValues) {
                    insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED)
                    contentValues = []
                } else {
                    LogDisplay.callLog(LOG_TAG, "No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                }
            }
            mTotalPage = 1
            mFirstTotalPageRead = true
            for (final i in 1..MAX_PAGE_DOWNLOAD) {
                contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING, i)
                if (contentValues) {
                    insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING)
                    contentValues = []
                } else {
                    LogDisplay.callLog(LOG_TAG, "No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                }
            }
            mTotalPage = 1
            mFirstTotalPageRead = true
            for (final i in 1..MAX_PAGE_DOWNLOAD) {
                contentValues = downloadMovieList(GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING, i)
                if (contentValues) {
                    insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING)
                    contentValues = []
                } else {
                    LogDisplay.callLog(LOG_TAG, "No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                }
            }
            //Now load details for home page movie items
            loadMovieDetailsForHomePageItems()

            // Check if the account is user's TMDb account(i.e. user is logged in to TMDb)
            // or regular SyncAdapter dummy account
            final boolean isUserAccount = checkAccountType(account)
            if (isUserAccount) {
                LogDisplay.callLog(LOG_TAG, 'This is a user account', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                // Get the authToken( TMDb session id) which is needed for TMDb call
                final AccountManager accountManager = AccountManager.get(mContext)
                // This convenience helper synchronously gets an auth token with getAuthToken(Account, String, boolean, AccountManagerCallback, Handler)
                final String authToken = accountManager.blockingGetAuthToken(account, GlobalStaticVariables.AUTHTOKEN_TYPE_FULL_ACCESS, true)
                // Get the account id from user data
                final String accountId = accountManager.getUserData(account, GlobalStaticVariables.TMDB_USERDATA_ACCOUNT_ID)
                LogDisplay.callLog(LOG_TAG, "AuthToken & AccountId. AuthToken -> $authToken " +
                        "& AccountID -> $accountId", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                if (authToken && accountId) {
                    processTmdbLists(authToken, accountId)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Either authToken or accountId or both null. So tmdb library download skipped', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                }
            } else {
                LogDisplay.callLog(LOG_TAG, 'This is SyncAdapter dummy account. So no further action', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            }

            // Let's do some housekeeping now. This is done at the end so that new records get inserted
            // before deleting existing records
            performHouseKeeping()
        } else {
            LogDisplay.callLog(LOG_TAG, 'Device is offline or connected to internet without WiFi and user selected download only on WiFi, so skipped data loading..', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }

        // Create notification
        createNotification()
    }

    /**
     * This helper method is used to download Tmdb public movie lists (i.e. popular, top rated, now playing, upcoming)
     * @param category The category of the movie which is to be downloaded
     * @param page Page number of the list
     * @return Formatted movie data as content values
     */
    private List<ContentValues> downloadMovieList (final String category, final int page) {
        //TMDB api example
        //https://api.themoviedb.org/3/movie/popular?api_key=key&page=1

        List<ContentValues> movieList = null

        try {
            final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                    .appendPath(category)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_PAGE,Integer.toString(page))
                    .build()

            final URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG,"Movie url for $category & page# $page -> ${uri.toString()}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

            //This is intentional so that at lest one page is not loaded in order to make sure
            //at least one (i.e. first) LoadMoreMovies call is always successful (?? Think it's not true!! need to check later)
            if (page <= mTotalPage) {
                final def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)
                LogDisplay.callLog(LOG_TAG, "JSON DATA for $category -> $jsonData",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                movieList = JsonParse.parseMovieListJson(mContext,jsonData, category, GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_PUBLIC, mDateTimeStamp)
                if(mFirstTotalPageRead) {
                    mTotalPage = JsonParse.getTotalPages(jsonData)
                    mFirstTotalPageRead = false
                }
            }
        } catch (final URISyntaxException e) {
            mPublicTmdbProcessingException = true
            Log.e(LOG_TAG, "URISyntaxException Error: ${e.message}", e)
        } catch (final JsonException e) {
            mPublicTmdbProcessingException = true
            Log.e(LOG_TAG, " JsonException Error: ${e.message}", e)
        } catch (final IOException e) {
            mPublicTmdbProcessingException = true
            Log.e(LOG_TAG, "IOException Error: ${e.message}", e)
        }
        return movieList
    }

    /**
     * This method checks the type of the account (Dummy SyncAdapter account or user's Tmdb account)
     * @param account The account for which the type needs to be determined
     * @return True if it's a user's Tmdb account
     */
    private boolean checkAccountType(final Account account) {
        LogDisplay.callLog(LOG_TAG,'checkAccountType is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG,"Account name -> ${account.name}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        // Application can have only one account, so if it's SyncAdapter's dummy account then return false otherwise true
        if(account.name == mContext.getString(R.string.app_name)) {
            return false
        } else {
            return true
        }
    }

    /**
     * This method processes the user's Tmdb lists (i.e. Watchlist, Favourite & Rated)
     * @param sessionId The session if which is required to get the data from Tmdb server
     * @param accountId The account id of the user's account, needed to get data from Tmdb server
     */
    private void processTmdbLists(final String sessionId, final String accountId) {
        LogDisplay.callLog(LOG_TAG,"processTmdbLists:Session id found -> $sessionId",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        List<ContentValues> contentValues = []
        // Download user's Tmdb Watchlist movies
        mTotalPageTmdbUserList = 1
        contentValues = downloadTmdbUserList(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST, accountId, sessionId, mTotalPageTmdbUserList)
        if(contentValues) {
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST)
            contentValues = []
        } else {
            LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
        // If more than one page exists then load the rest of the pages
        if(mTotalPageTmdbUserList > 1) {
            for (final pageCount in 2..mTotalPageTmdbUserList) {
                contentValues = downloadTmdbUserList(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST, accountId, sessionId, pageCount)
                if(contentValues) {
                    insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST)
                    contentValues = []
                } else {
                    LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                    LogDisplay.callLog(LOG_TAG,"Page count -> $pageCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                }
            }
        }
        // Reset the total page to 1
        mTotalPageTmdbUserList = 1

        // Download user's Tmdb Favourite movies
        contentValues = downloadTmdbUserList(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE, accountId, sessionId, mTotalPageTmdbUserList)
        if(contentValues) {
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE)
            contentValues = []
        }  else {
            LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
        // If more than one page exists then load the rest of the pages
        if(mTotalPageTmdbUserList > 1) {
            for (final pageCount in 2..mTotalPageTmdbUserList) {
                contentValues = downloadTmdbUserList(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE, accountId, sessionId, mTotalPageTmdbUserList)
                if(contentValues) {
                    insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE)
                    contentValues = []
                }  else {
                    LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                    LogDisplay.callLog(LOG_TAG,"Page count -> $pageCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                }
            }
        }
        // Reset the total page to 1
        mTotalPageTmdbUserList = 1

        // Download user's Tmdb Rated movies
        contentValues = downloadTmdbUserList(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED, accountId, sessionId, mTotalPageTmdbUserList)
        if(contentValues) {
            insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED)
            contentValues = []
        } else {
            LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
        // If more than one page exists then load the rest of the pages
        if(mTotalPageTmdbUserList > 1) {
            for (final pageCount in 2..mTotalPageTmdbUserList) {
                contentValues = downloadTmdbUserList(GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED, accountId, sessionId, mTotalPageTmdbUserList)
                if(contentValues) {
                    insertBulkRecords(contentValues, GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED)
                    contentValues = []
                } else {
                    LogDisplay.callLog(LOG_TAG,"No movie data for category -> $GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                    LogDisplay.callLog(LOG_TAG,"Page count -> $pageCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                }
            }
        }
    }

    /**
     * This helper method is used to download Tmdb user movie lists
     * @param category The category of the movie which is to be downloaded
     * @param accountId The account id of the user's account, needed to get data from Tmdb server
     * @param sessionId The session if which is required to get the data from Tmdb server
     * @return Formatted movie data as content values
     */
    private List<ContentValues> downloadTmdbUserList (
            final String category, final String accountId, final String sessionId, final int totalPage) {
        //TMDB api example
        // https://api.themoviedb.org/3/account/<accountId>/watchlist/movies?api_key=apiKey&session_id=sessionId
        List<ContentValues> tmdbUserMovieList = null

        try {
            final Uri.Builder tmdbUserUriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri tmdbUserUri = tmdbUserUriBuilder.appendPath(GlobalStaticVariables.TMDB_ACCOUNT_PATH)
                    .appendPath(accountId)
                    .appendPath(category)
                    .appendPath(GlobalStaticVariables.TMDB_USER_MOVIES_PATH)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_SESSION_ID_KEY,sessionId)
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_PAGE,Integer.toString(totalPage))
                    .build()

            final URL tmdbUserUrl = new URL(tmdbUserUri.toString())
            LogDisplay.callLog(LOG_TAG,"Tmdb user movie url for $category -> ${tmdbUserUri.toString()}",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

            final def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(tmdbUserUrl)
            LogDisplay.callLog(LOG_TAG, "Tmdb user movie JSON data for $category -> $jsonData",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            if(jsonData) {
                tmdbUserMovieList = JsonParse.parseMovieListJson(mContext, jsonData, category, GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_USER, mDateTimeStamp)
                mTotalPageTmdbUserList = JsonParse.getTotalPages(jsonData)
            }
        } catch (final URISyntaxException e) {
            mPublicTmdbProcessingException = true
            Log.e(LOG_TAG, "URISyntaxException Error: ${e.message}", e)
        } catch (final JsonException e) {
            mPublicTmdbProcessingException = true
            Log.e(LOG_TAG, " JsonException Error: ${e.message}", e)
        } catch (final IOException e) {
            mPublicTmdbProcessingException = true
            Log.e(LOG_TAG, "IOException Error: ${e.message}", e)
        }
        return tmdbUserMovieList
    }

    /**
     * This method inserts the data to the movie_basic_info database
     * @param cvList The content values to ve inserted
     * @param category The category of the data (movie list) is to be inserted (used for display purpose only)
     */
    private void insertBulkRecords(final List<ContentValues> cvList, final String category) {
        final ContentValues[] cv = cvList as ContentValues []
        if(cv) {
            final int insertCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, cv)
            LogDisplay.callLog(LOG_TAG, "Total insert for $category->$insertCount", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            if (insertCount > 0) {
                LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info successful. Total insert for $category->$insertCount", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            } else {
                LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info failed. Insert count for $category->$insertCount", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            }
        } else {
            LogDisplay.callLog(LOG_TAG,'cv is null. JsonParse.parseMovieListJson returned null',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
    }

    /**
     * Load movie details for the movies which are used for Home page (Now playing & Upcoming)
     */
    private void loadMovieDetailsForHomePageItems() {
        LogDisplay.callLog(LOG_TAG,'loadMovieDetailsForHomePageItems is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        Cursor movieDataCursor
        final ArrayList<Integer> mMovieIdList = new ArrayList<>()
        final ArrayList<Integer> mMovieRowIdList = new ArrayList<>()
        final String todayDate = Long.toString(MovieMagicContract.convertMovieReleaseDate(Utility.getSimpleTodayDate()))
        //First finalise the data to be loaded for now playing
        movieDataCursor = mContentResolver.query(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                MOVIE_BASIC_INFO_COLUMNS,
                /**The conditions used here are same as what used in Home Fragment loader (except COLUMN_CREATE_TIMESTAMP
                 * which is used to consider the new data only). If that changes then change this **/
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE <= ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP >= ? """,
                [GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING, todayDate, '', '', mDateTimeStamp] as String[],
                "$MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE desc limit $GlobalStaticVariables.HOME_PAGE_MAX_MOVIE_SHOW_COUNTER")

        if(movieDataCursor.moveToFirst()) {
            for (final i in 0..(movieDataCursor.getCount() - 1)) {
                mMovieIdList.add(i, movieDataCursor.getInt(COL_MOVIE_BASIC_MOVIE_ID))
                mMovieRowIdList.add(i, movieDataCursor.getInt(COL_MOVIE_BASIC_ID))
                movieDataCursor.moveToNext()
            }
            //Close the cursor
            movieDataCursor.close()
            LogDisplay.callLog(LOG_TAG, 'Add data for now playing movies', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Now playing.Movie ID list-> $mMovieIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Now playing.Movie row id list-> $mMovieRowIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG, 'Empty cursor returned by movie_basic_info for now playing', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
        //Now finalise the data to be loaded for upcoming
        movieDataCursor = mContentResolver.query(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                MOVIE_BASIC_INFO_COLUMNS,
                /**The conditions used here are same as what used in Home Fragment loader (except COLUMN_CREATE_TIMESTAMP
                 * which is used to consider they new data only). If that changes then change this **/
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE > ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP >= ? """,
                   [GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING, todayDate, '', '', mDateTimeStamp] as String[],
                  "$MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE desc limit $GlobalStaticVariables.HOME_PAGE_MAX_MOVIE_SHOW_COUNTER")

        if(movieDataCursor.moveToFirst()) {
            for (final i in 0..(movieDataCursor.getCount() - 1)) {
                mMovieIdList.add(i, movieDataCursor.getInt(COL_MOVIE_BASIC_MOVIE_ID))
                mMovieRowIdList.add(i, movieDataCursor.getInt(COL_MOVIE_BASIC_ID))
                movieDataCursor.moveToNext()
            }
            //Close the cursor
            movieDataCursor.close()
            LogDisplay.callLog(LOG_TAG, 'Add data for up coming movies', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Appended Up coming.Movie ID list-> $mMovieIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Appended Up coming.Movie row id list-> $mMovieRowIdList", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG, 'Empty cursor returned by movie-basic_info for up coming', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }

        //Now go and load the detail data for the home screen movies
        if(mMovieIdList.size() > 0 && mMovieRowIdList.size() > 0) {
            LogDisplay.callLog(LOG_TAG, 'Now go and load the details of the movies for home page..', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            final ArrayList<Integer> isForHomeList = new ArrayList<>(1)
            final ArrayList<Integer> categoryFlag = new ArrayList<>(1)
            //Set this flag to true as the Home page videos are retrieved based on this indicator
            isForHomeList.add(0,GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE)
            categoryFlag.add(0,GlobalStaticVariables.NULL_CATEGORY_FLAG)
            final ArrayList<Integer>[] loadMovieDetailsArg = [mMovieIdList, mMovieRowIdList, isForHomeList, categoryFlag] as ArrayList<Integer>[]
            new LoadMovieDetails(mContext).execute(loadMovieDetailsArg)
        }
    }

    /**
     * This method does the housekeeping of the application's data
     */
    private void performHouseKeeping() {
        LogDisplay.callLog(LOG_TAG,'performHouseKeeping is called',LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG,"performHouseKeeping: Today's DateTimeStamp->$mDateTimeStamp",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        if(!mPublicTmdbProcessingException) {
            // Delete old data except user's records from movie_basic_info and recommendations movies (recommendations are deleted in the next step)
            final int movieBasicInfoDeleteCount = mContentResolver.delete(MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE != ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY != ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP < ? """,
                    /** To ensure newly inserted records are not deleted, mDateTimeStamp is used and it ensures **/
                    /** that all old records except the one which just inserted as part of this execution are deleted **/
                    [GlobalStaticVariables.MOVIE_LIST_TYPE_USER_LOCAL_LIST, GlobalStaticVariables.MOVIE_CATEGORY_RECOMMENDATIONS, mDateTimeStamp] as String[])
            LogDisplay.callLog(LOG_TAG, "Total records deleted from movie_basic_info (except recommendations) -> $movieBasicInfoDeleteCount", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }

        /** Delete recommendation records which are more than 10 days old **/
        final String tenDayPriorTimestamp = Utility.getTenDayPriorDate()
        LogDisplay.callLog(LOG_TAG,"performHouseKeeping: Ten day's prior DateTimeStamp->$tenDayPriorTimestamp",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        final int movieBasicInfoRecommendDeleteCount = mContentResolver.delete(MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP < ? """,
                [GlobalStaticVariables.MOVIE_CATEGORY_RECOMMENDATIONS, tenDayPriorTimestamp] as String [] )
        LogDisplay.callLog(LOG_TAG,"Total recommended records deleted from movie_basic_info -> $movieBasicInfoRecommendDeleteCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        /** Reset the data already present flags for user local lists movies, so that it's get updated next time it accessed by user  **/
        final ContentValues userListContentValues = new ContentValues()
        userListContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,0)
        final int userListMovieBasicInfoUpdateCount = mContentResolver.update(MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                userListContentValues,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE = ?""",
                [Integer.toString(GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE), GlobalStaticVariables.MOVIE_LIST_TYPE_USER_LOCAL_LIST] as String[] )
        LogDisplay.callLog(LOG_TAG,"Total records updated for user list in movie_basic_info-> $userListMovieBasicInfoUpdateCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        /** Delete old data from movie_person_info  **/
        final int personInfoDeleteCount = mContentResolver.delete(MovieMagicContract.MoviePersonInfo.CONTENT_URI,
                "$MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_CREATE_TIMESTAMP < ? ",
                [mDateTimeStamp] as String [] )
        LogDisplay.callLog(LOG_TAG,"Total records deleted from movie_person_info -> $personInfoDeleteCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        /** Delete old data from movie_collection  **/
        final int collectionDeleteCount = mContentResolver.delete(MovieMagicContract.MovieCollection.CONTENT_URI,
                "$MovieMagicContract.MovieCollection.COLUMN_COLLECTION_CREATE_TIMESTAMP < ? ",
                [mDateTimeStamp] as String [] )
        LogDisplay.callLog(LOG_TAG,"Total records deleted from movie_collection -> $collectionDeleteCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

        /** Delete old data from search_movie_basic_info_table virtual (temporary) table  **/
        final SQLiteDatabase sqLiteDatabase = new SearchDatabaseTable.SearchDatabaseOpenHelper(mContext).getWritableDatabase()
        final int searchMovieDeleteCount = sqLiteDatabase.delete(SearchDatabaseTable.SEARCH_FTS_VIRTUAL_TABLE_NAME, null, null)
        // Close the database
        sqLiteDatabase.close()
        LogDisplay.callLog(LOG_TAG,"Total records deleted from virtual search_movie_basic_info_table -> $searchMovieDeleteCount",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
    }

    /**
     * This method creates the notification (only considers release date today or five days ahead)
     */
    private void createNotification() {
        LogDisplay.callLog(LOG_TAG, 'createNotification is called', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        // Read the SharedPreferenc and see if notification is on or off
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        final boolean notificationFlag = sharedPreferences.getBoolean(mContext.getString(R.string.pref_notification_key),false)
        if(notificationFlag) {
            final String releaseDateCondOne = Long.toString(MovieMagicContract.convertMovieReleaseDate(Utility.getSimpleTodayDate()))
            final String releaseDateCondTwo = Long.toString(MovieMagicContract.convertMovieReleaseDate(Utility.getSimpleFiveDayFutureDate()))
            LogDisplay.callLog(LOG_TAG,"Date range -> $releaseDateCondOne to $releaseDateCondTwo",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
            final boolean distinct = true
            final SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getReadableDatabase()
            // Get the data to create the notification (to get unique record query with distinct is used)
            final Cursor notificationDataCursor = sqLiteDatabase.query(
                    distinct,
                    MovieMagicContract.MovieBasicInfo.TABLE_NAME,
                    MOVIE_BASIC_INFO_COLUMNS,
                    """$MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH <> ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE >= ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE < ? """,
                    ['', '', releaseDateCondOne,releaseDateCondTwo] as String[],
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID", // Distinct on movie id
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY desc",
                    "$GlobalStaticVariables.MAX_NOTIFICATION_COUNTER")

            if(notificationDataCursor.moveToFirst()) {
                for (final i in 0..(notificationDataCursor.getCount() - 1)) {
                    // Prepare data for notification
                    final String releaseDayName = Utility.getDayNameForNotification(mContext, Utility.convertMilliSecsToOrigReleaseDate(notificationDataCursor.getLong(COL_MOVIE_BASIC_RELEASE_DATE)))
                    final String releaseDate = Utility.formatFriendlyDate(Utility.convertMilliSecsToOrigReleaseDate(notificationDataCursor.getLong(COL_MOVIE_BASIC_RELEASE_DATE)))
                    final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W92" +
                            "${notificationDataCursor.getString(COL_MOVIE_BASIC_POSTER_PATH)}"
                    final String backdropPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
                            "${notificationDataCursor.getString(COL_MOVIE_BASIC_BACKDROP_PATH)}"
                    final Bitmap posterBitmap = Picasso.with(mContext).load(posterPath).get()
                    final Bitmap backdropBitmap = Picasso.with(mContext).load(backdropPath).get()

                    LogDisplay.callLog(LOG_TAG,"Total records count: -> ${notificationDataCursor.getCount()}. " +
                            "Movie name -> ${notificationDataCursor.getString(COL_MOVIE_BASIC_TITLE)}. " +
                            "Release Date -> $releaseDate",LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)

                    // Create the style object with BigPictureStyle subclass.
                    final NotificationCompat.BigPictureStyle notificationStyle = new NotificationCompat.BigPictureStyle()
                    notificationStyle.setBigContentTitle(notificationDataCursor.getString(COL_MOVIE_BASIC_TITLE))
                    notificationStyle.setSummaryText(String.format(mContext.getString(R.string.format_release_date_notification), releaseDayName, releaseDate))
                    notificationStyle.bigPicture(backdropBitmap)

                    // Creates an explicit intent for an ResultActivity to receive.
                    final Intent resultIntent = new Intent(mContext, DetailMovieActivity.class)
                    final Bundle bundle = new Bundle()
                    bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,notificationDataCursor.getInt(COL_MOVIE_BASIC_MOVIE_ID))
                    bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,notificationDataCursor.getString(COL_MOVIE_BASIC_MOVIE_CATEGORY))
                    resultIntent.putExtras(bundle)

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    final TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext)
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(DetailMovieActivity.class)
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent)
                    final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(i,PendingIntent.FLAG_UPDATE_CURRENT)

                    // Build the Notification
                    final Notification notification = new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.drawable.ic_stat_notify_movie_magic)
                            .setAutoCancel(true)
                            .setLargeIcon(posterBitmap)
                            .setContentIntent(resultPendingIntent)
                            .setContentTitle(notificationDataCursor.getString(COL_MOVIE_BASIC_TITLE))
                            .setContentText(String.format(mContext.getString(R.string.format_release_date_notification),releaseDayName, releaseDate))
                            .setStyle(notificationStyle).build()

                    // Now send the notification
                    final NotificationManager notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    // mId allows you to update the notification later on.
                    notificationManager.notify(i, notification)

                    // Move to the next record
                    notificationDataCursor.moveToNext()
                }
                // Close the cursor
                notificationDataCursor.close()
                // Close the database
                sqLiteDatabase.close()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Empty cursor returned by movie_basic_info for notification data', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                // Close the database
                sqLiteDatabase.close()
            }
        } else {
            LogDisplay.callLog(LOG_TAG, 'User not selected notification, so skipped', LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
        }
    }
}