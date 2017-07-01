/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import android.accounts.Account
import android.accounts.AccountManager
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.json.JsonException
import groovy.json.JsonOutput
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class UploadTmdbRequest extends AsyncTask<Integer, Void, String> {
    private static final String LOG_TAG = UploadTmdbRequest.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext
    private int mTmdbMovieId
    private final String mTmdbMovieType
    private boolean mOperationFlag
    private final String mMovieCategory
    private float mTmdbRatingValue
    private final ProgressDialog mProgressDialog
    private final RelativeLayout mTmdbUserDrawableLayout
    private final int mAccentColor
    private boolean mSuccessPostFlag = false

    public UploadTmdbRequest(
            final Context ctx, final String movieType, final float ratingVal, final boolean addOrDelete,
            final String movieCategory, final RelativeLayout tmdbUserDrawableLayout, final int accentColor) {
        mContext = ctx
        mContentResolver = mContext.getContentResolver()
        mTmdbMovieType = movieType
        mTmdbRatingValue = ratingVal
        mOperationFlag = addOrDelete
        mMovieCategory = movieCategory
        mTmdbUserDrawableLayout = tmdbUserDrawableLayout
        mAccentColor = accentColor
        mProgressDialog = new ProgressDialog(mContext, ProgressDialog.STYLE_SPINNER)
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute()
        mProgressDialog.setMessage(mContext.getString(R.string.progress_dialog_wait_title))
        mProgressDialog.show()
    }

    @Override
    protected String doInBackground(final Integer... params) {
        mTmdbMovieId = params[0]
        String movieTypePath = null
        String bodyJsonData = null
        String authToken= null
        String accountId = null

        // Get the account information - if not found then return null
        final AccountManager accountManager = AccountManager.get(mContext)
        final Account[] accounts = accountManager.getAccountsByType(mContext.getString(R.string.authenticator_account_type))
        if(accounts.size() == 1) {
            authToken = accountManager.blockingGetAuthToken(accounts[0],GlobalStaticVariables.AUTHTOKEN_TYPE_FULL_ACCESS,true)
            accountId = accountManager.getUserData(accounts[0], GlobalStaticVariables.TMDB_USERDATA_ACCOUNT_ID)
        } else {
            LogDisplay.callLog(LOG_TAG,"Error.More than one account, number of accounts -> ${accounts.size()}",LogDisplay.MOVIE_MAGIC_MAIN_LOG_FLAG)
            return null
        }

        // Determine the path based on the movie type & build the JSON
        switch (mTmdbMovieType) {
            case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST:
                movieTypePath = GlobalStaticVariables.TMDB_WATCHLIST_PATH
                bodyJsonData = JsonOutput.toJson([media_type: 'movie', media_id: mTmdbMovieId, watchlist: mOperationFlag])
                break

            case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE:
                movieTypePath = GlobalStaticVariables.TMDB_FAVOURITE_PATH
                bodyJsonData = JsonOutput.toJson([media_type: 'movie', media_id: mTmdbMovieId, favorite: mOperationFlag])
                break

            case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED:
                movieTypePath = GlobalStaticVariables.TMDB_RATED_PATH
                if(mTmdbRatingValue > 0) {
                    bodyJsonData = JsonOutput.toJson([value: mTmdbRatingValue])
                } else {
                    bodyJsonData = ''
                }
                break
            default:
                LogDisplay.callLog(LOG_TAG, "Unknown movieType-> $mTmdbMovieType", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
        }

        LogDisplay.callLog(LOG_TAG, "bodyJsonData-> $bodyJsonData", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)

        HttpURLConnection conn = null
        InputStream inputStream = null
        OutputStream outputStream = null

        //TMDB api example for watchlist & favourite
        //https://api.themoviedb.org/3/account/{account_id}/watchlist?api_key=key
        //TMDB api example for rated
        //https://api.themoviedb.org/3/movie/{movie_id}/rating?api_key=key
        try {
            final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()
            final Uri tmdbUri
            if(mTmdbMovieType == GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED) {
                tmdbUri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                        .appendPath(Integer.toString(mTmdbMovieId))
                        .appendPath(movieTypePath)
                        .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY, BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter(GlobalStaticVariables.TMDB_SESSION_ID_KEY, authToken)
                        .build()
            } else {
                tmdbUri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_ACCOUNT_PATH)
                        .appendPath(accountId)
                        .appendPath(movieTypePath)
                        .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY, BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter(GlobalStaticVariables.TMDB_SESSION_ID_KEY, authToken)
                        .build()
            }
            LogDisplay.callLog(LOG_TAG, "TMDb POST/DELETE request url-> ${tmdbUri.toString()}", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
            final URL tmdbUrl = new URL(tmdbUri.toString())
            conn = (HttpURLConnection) tmdbUrl.openConnection()

            conn.setReadTimeout( 10000 /*milliseconds*/ )
            conn.setConnectTimeout( 15000 /* milliseconds */ )
            if(mTmdbMovieType == GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED && mTmdbRatingValue == 0.0) {
                conn.setRequestMethod("DELETE") //TMDb uses "DELETE" method to remove movie from Rated list when rating = 0
            } else {
                conn.setRequestMethod("POST")
            }
            conn.setDoInput(true)
            conn.setDoOutput(true)
            conn.setFixedLengthStreamingMode(bodyJsonData.getBytes().length)

            // Build the https request body
            conn.setRequestProperty("content-Type", "application/json;charset=utf-8")
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest")

            // Now let's open the connection
            conn.connect()

            // Build the output stream
            outputStream = new BufferedOutputStream(conn.getOutputStream())
            outputStream.write(bodyJsonData.getBytes())
            // Clean up output stream
            outputStream.flush()

            // Get the response code
            final int respCode = conn.getResponseCode()
            LogDisplay.callLog(LOG_TAG, "TMDb POST/DELETE request resp code-> $respCode", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
            // Get the data and parse it
            if(respCode == 200 || respCode == 201) {
                inputStream = conn.getInputStream()
                mSuccessPostFlag = true
                addUpdateDeleteTmdbUserRecord()
            } else {
                inputStream = conn.getErrorStream()
            }
            final def tokenJsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(inputStream)
            LogDisplay.callLog(LOG_TAG, "TMDb POST request resp JSON-> $tokenJsonData", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)

            final String statusMessage = JsonParse.parseTmdbUserListPostResponse(tokenJsonData, respCode)
            LogDisplay.callLog(LOG_TAG, "TMDb parsed status message-> $statusMessage", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)

            // Return the TMDb message
            return  statusMessage
        } catch (final URISyntaxException e) {
            Log.e(LOG_TAG, "URISyntaxException: ${e.message}", e)
        } catch (final JsonException e) {
            Log.e(LOG_TAG, "JsonException: ${e.message}", e)
        } catch (final IOException e) {
            Log.e(LOG_TAG, "IOException: ${e.message}", e)
        } finally {
            // Close the connection and input stream
            conn.disconnect()
            outputStream.close()
            if(inputStream)
                inputStream.close()
        }
    }

    @Override
    protected void onPostExecute(final String msg) {
        if(mProgressDialog.isShowing()) {
            mProgressDialog.dismiss()
        }
        if(msg && mSuccessPostFlag) {
            // Handle the button enable & disable
            switch (mTmdbMovieType) {
                case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST:
                    toggleImageButton(mTmdbUserDrawableLayout.findViewById(R.id.movie_detail_user_tmdb_list_drawable_watchlist) as ImageButton)
                    break

                case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE:
                    toggleImageButton(mTmdbUserDrawableLayout.findViewById(R.id.movie_detail_user_tmdb_list_drawable_favourite) as ImageButton)
                    break

                case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED:
                    toggleRatedImageButton(mTmdbUserDrawableLayout.findViewById(R.id.movie_detail_user_tmdb_list_drawable_rated) as ImageButton)
                    break

                default:
                    LogDisplay.callLog(LOG_TAG, "Unknown movieType-> $mTmdbMovieType", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
            }
            Snackbar.make(mTmdbUserDrawableLayout, msg, Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(mTmdbUserDrawableLayout, mContext.getString(R.string.upload_tmdb_account_not_found_msg),
                    Snackbar.LENGTH_LONG).show()
        }
    }

    /**
     * This method is called to determine if the Tmdb movie needs to be added or deleted from the local database
     * and then appropriately take the action
     */
    protected void addUpdateDeleteTmdbUserRecord() {
        // Whenever the POST/DELETE method is successful the updates are reflected in TMDb site but
        // User's TMDb list gets refreshed when only SyncAdapter runs, so to keep the TMDb site data
        // and local TMDb user data in Sync, following operation is done
        if(mTmdbMovieType == GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST ||
                mTmdbMovieType == GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE) {
            if(mOperationFlag) { // Add a new record
                final ContentValues contentValues = getMovieBasicData()
                if(contentValues) {
                    addTmdbUserRecord(contentValues)
                } else {
                    LogDisplay.callLog(LOG_TAG,'1->Not able to retrieve data from movie basic info table',LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
                }
            } else { // Delete the existing record
                deleteTmdbUserRecord()
            }
        } else if (mTmdbMovieType == GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED) {
            if(mTmdbRatingValue > 0) { // Add or update rated Tmdb record
                final long rowId = checkAndGetRowIdOfTmdbRatedMovie()
                if (rowId > 0) { // Record exists - update rated Tmdb record
                    updateTmdbUserRecord(rowId)
                } else { // Insert a new record for rated Tmdb
                    final ContentValues contentValues = getMovieBasicData()
                    if(contentValues) {
                        addTmdbUserRecord(contentValues)
                    } else {
                        LogDisplay.callLog(LOG_TAG,'2->Not able to retrieve data from movie basic info table',LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
                    }
                }
            } else { // Delete the rated Tmdb record
                deleteTmdbUserRecord()
            }
        }
    }

    /**
     * This method retrieves the details of the movie from movie basic info table
     * @return The retrieved data in ContentValues format
     */
    protected ContentValues getMovieBasicData() {
        final ContentValues movieBasicInfoContentValues = new ContentValues()
        final Cursor movieBasicInfoCursor = mContentResolver.query(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                null,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? """,
                [Integer.toString(mTmdbMovieId), mMovieCategory] as String[],
                null)
        //Position the cursor then convert the cursor to content values
        if(movieBasicInfoCursor.moveToFirst()) {
            //Convert the cursor to content values
            DatabaseUtils.cursorRowToContentValues(movieBasicInfoCursor, movieBasicInfoContentValues)
        } else {
            LogDisplay.callLog(LOG_TAG,"Bad cursor from movie_basic_info, this can happen when a movie is removed then attempted to add again. So try and see if we can grab the orphaned record whic just got created using movie id->$mTmdbMovieId",LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
            // Since it can happen when user remove then add the movie again - so try to get the details from orphaned (i.e. just removed, so marked as orphaned)
            final Cursor movieBasicInfoCursorRetry = mContentResolver.query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? """,
                    [Integer.toString(mTmdbMovieId), GlobalStaticVariables.MOVIE_CATEGORY_ORPHANED] as String[],
                    null)
            //Position the cursor then convert the cursor to content values
            if(movieBasicInfoCursorRetry.moveToFirst()) {
                //Convert the cursor to content values
                DatabaseUtils.cursorRowToContentValues(movieBasicInfoCursorRetry, movieBasicInfoContentValues)
            } else {
                LogDisplay.callLog(LOG_TAG, "Again bad cursor from movie_basic_info, please investigate. Movie id->$mTmdbMovieId", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
            }
        }
        //Close the cursor
        if(movieBasicInfoCursor) {
            movieBasicInfoCursor.close()
        }

        return movieBasicInfoContentValues
    }

    /**
     * This method checks if a particular user TMDb rated movie is present in local database
     * @return Row id of the record if found or zero
     */
    protected long checkAndGetRowIdOfTmdbRatedMovie() {
        final Cursor tmdbRatedMovieCursor = mContentResolver.query(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                null,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? """,
                [Integer.toString(mTmdbMovieId), GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED] as String[],
                null)
        long rowId = 0
        //Position the cursor - it it's a success then the record exists, return the row_id
        if(tmdbRatedMovieCursor.moveToFirst()) {
            rowId = tmdbRatedMovieCursor.getLong(tmdbRatedMovieCursor.getColumnIndex(MovieMagicContract.MovieBasicInfo._ID))
            LogDisplay.callLog(LOG_TAG,"TMDb rated movie found for movie id->$mTmdbMovieId and row id is $rowId",LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG,"TMDb rated movie not found for movie id->$mTmdbMovieId",LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
        }

        //Close the cursor
        if(tmdbRatedMovieCursor) {
            tmdbRatedMovieCursor.close()
        }

        return rowId
    }

    /**
     * This method creates a new record in database for User TMDb movie
     * @param movieBasicInfoContentValues The Contentvalues of the record to be created
     */
    protected void addTmdbUserRecord(final ContentValues movieBasicInfoContentValues) {
        movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,mTmdbMovieType)
        movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE,
                GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_USER)
        movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP,Utility.getTodayDate())
        movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP,Utility.getTodayDate())
        //Since the program logic is written in a way where the release date is expected as yyyy-MM-dd
        //so convert release date (which is stored as milli seconds) to that format
        final long releaseDate = movieBasicInfoContentValues.getAsLong(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE)
        final String formattedReleaseDate = Utility.convertMilliSecsToOrigReleaseDate(releaseDate)
        movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,formattedReleaseDate)
        //Need to remove the "_ID" as that is system generated
        movieBasicInfoContentValues.remove(MovieMagicContract.MovieBasicInfo._ID)
        //If it's Tmdb rated movie then add the TMDb rating also
        if(mTmdbMovieType == GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED) {
            movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_TMDB_USER_RATED_RATING,mTmdbRatingValue)
        }
        final Uri uri = mContentResolver.insert(MovieMagicContract.MovieBasicInfo.CONTENT_URI,movieBasicInfoContentValues)
        if(ContentUris.parseId(uri) == -1) {
            LogDisplay.callLog(LOG_TAG,"Insert of TMDb user movie in movie_basic_info failed. Uri->$uri",LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG,"Insert of TMDb user movie in movie_basic_info successful. Uri->$uri",LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
        }
    }

    /**
     * This method updates an existing user TMDb rated movie record in database
     * @param rowId The row id of the movie to be updated
     */
    protected void updateTmdbUserRecord(final long rowId) {
        final ContentValues movieBasicInfoContentValues = new ContentValues()
        movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_TMDB_USER_RATED_RATING, mTmdbRatingValue)
        final int updateCount = mContentResolver.update(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                movieBasicInfoContentValues,
                "$MovieMagicContract.MovieBasicInfo._ID = ?",
                [Long.toString(rowId)] as String[])
        if(updateCount != 1) {
            LogDisplay.callLog(LOG_TAG,"Update of TMDb rated movie failed. Update Count->$updateCount",LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
        } else { //If the return value to 1, indicate successful insert
            LogDisplay.callLog(LOG_TAG,"Update of TMDb rated movie is successful. Update Count->$updateCount",LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
        }
    }

    /**
     * This method does the housekeeping of user TMDb movies which are no longer needed.
     */
    protected void deleteTmdbUserRecord() {
        //When user remove the movie from the list it should be ideally deleted but due to the logic
        //of the application, the user can still see the details of the movie even after the delete. So in order
        //to achieve that we need to keep that row in the table otherwise application will crash as the loader 0 of
        //detail fragment will not find the the corresponding movie and would return null. So instead of delete
        //update the record with category "orphaned" which will ensure that it will not come in the user list but
        //record will remain there in the table and later will be cleaned up by the sync adapter while loading new
        //data as the sync adapter has logic to delete anything which is not user local list
        //Remove the record from movie_basic_info is needed for user list and NOT for rating operation
        final ContentValues movieOrphanContentValue = new ContentValues()
        movieOrphanContentValue.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY, GlobalStaticVariables.MOVIE_CATEGORY_ORPHANED)
        movieOrphanContentValue.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE, GlobalStaticVariables.MOVIE_LIST_TYPE_ORPHANED)
        final int rowCount = mContentResolver.update(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                movieOrphanContentValue,
                """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? and
                   $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? """,
                [Integer.toString(mTmdbMovieId), mTmdbMovieType] as String[])
        //Expecting just one record to be updated in movie_basic_info
        if (rowCount != 1) {
            LogDisplay.callLog(LOG_TAG, "Update movie_basic_info record to orphaned failed. Update Count->$rowCount", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG, "Update movie_basic_info record to orphaned successful. Update Count->$rowCount", LogDisplay.UPLOAD_TMDB_REQUEST_LOG_FLAG)
        }
    }

    /**
     * This method enables / disables user's TMDb list button for Watchlist & Favourite
     * @param button The button to be enabled or disabled
     */
    protected void toggleImageButton(final ImageButton button) {
        if(mOperationFlag) {
            button.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
            button.setColorFilter(mAccentColor)
        } else {
            button.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
            button.setColorFilter(null)
        }
    }

    /**
     * This method enables / disables user's TMDb list Rated
     * @param button The rated button to be enabled or disabled
     */
    protected toggleRatedImageButton(final ImageButton button) {
        if(mTmdbRatingValue > 0) {
            button.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
            button.setColorFilter(mAccentColor)
        } else {
            button.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
            button.setColorFilter(null)
        }
    }
}