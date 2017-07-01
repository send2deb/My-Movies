/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

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
import android.widget.RelativeLayout
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.transform.CompileStatic

@CompileStatic
class UpdateUserListChoiceAndRating extends AsyncTask<String, Void, Integer> {
    private static final String LOG_TAG = UpdateUserListChoiceAndRating.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext
    private final RelativeLayout mUserDrawableLayout
    private final int mMovieId
    private final String mMovieTitle
    private String mUserListMsg
    private int mUserFlag = 0
    private float mUserRating = 0.0
    private final ProgressDialog mProgressDialog
    private final boolean mShowNotification

    //Columns to fetch from movie_user_list_flag table
    private static final String[] MOVIE_USER_LIST_FLAG_COLUMNS = [MovieMagicContract.MovieUserListFlag._ID,
                                                                  MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID,
                                                                  MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WATCHED,
                                                                  MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WISH_LIST,
                                                                  MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_FAVOURITE,
                                                                  MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_COLLECTION,
                                                                  MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_USER_RATING]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    private final static int COL_MOVIE_USER_LIST_FLAG_ID = 0
    private final static int COL_MOVIE_USER_LIST_FLAG_ORIG_MOVIE_ID = 1
    private final static int COL_MOVIE_USER_LIST_FLAG_WATCHED_FLAG = 2
    private final static int COL_MOVIE_USER_LIST_FLAG_WISH_LIST_FLAG = 3
    private final static int COL_MOVIE_USER_LIST_FLAG_FAVOURITE_FLAG = 4
    private final static int COL_MOVIE_USER_LIST_FLAG_COLLECTION_FLAG = 5
    private final static int COL_MOVIE_USER_LIST_FLAG_USER_RATING = 6

    public UpdateUserListChoiceAndRating(
            final Context ctx, final RelativeLayout userDrawableLayout, final int movieId, final String movieTitle,
            final boolean showNotification) {
        mContext = ctx
        mContentResolver = mContext.getContentResolver()
        mUserDrawableLayout = userDrawableLayout
        mMovieId = movieId
        mMovieTitle = movieTitle
        mProgressDialog = new ProgressDialog(mContext, ProgressDialog.STYLE_SPINNER)
        mShowNotification = showNotification
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute()
        mProgressDialog.setMessage(mContext.getString(R.string.progress_dialog_wait_title))
        mProgressDialog.show()
    }

    @Override
    protected Integer doInBackground(final String... params) {
        final String listType = params[0]
        final String operationType = params[1]
        final float ratingValue = params[2] as Float
        int retValue = 0
        String userListCategory = null
        final ContentValues movieBasicInfoContentValues = new ContentValues()
        final ContentValues movieUserListFlagContentValues = new ContentValues()
        Cursor movieBasicInfoCursor = null
        //Build the URIs
        final Uri movieUserListFlagUri = MovieMagicContract.MovieUserListFlag.buildMovieUserListFlagUriWithMovieId(mMovieId)
        //Get the record from movie_user_list_flag
        final Cursor movieUSerListFlagCursor = mContentResolver.query(movieUserListFlagUri,MOVIE_USER_LIST_FLAG_COLUMNS,null,null,null)

        LogDisplay.callLog(LOG_TAG,"listType->$listType",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG,"operationType->$operationType",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG,"ratingValue->$ratingValue",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
        if(operationType == GlobalStaticVariables.USER_LIST_ADD_FLAG) {
            mUserFlag = GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE
            //Get the movie basic details which will be used to create user record
            //This is needed for "ADD" case only as a new user record is to be created
            movieBasicInfoCursor = mContentResolver.query(
                    MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                    null,
                    "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? ",
                    [Integer.toString(mMovieId)] as String[],
                    null)
            //Position the cursor then convert the cursor to content values
            if(movieBasicInfoCursor.moveToFirst()) {
                //Convert the cursor to content values
                DatabaseUtils.cursorRowToContentValues(movieBasicInfoCursor, movieBasicInfoContentValues)
            } else {
                LogDisplay.callLog(LOG_TAG,"Bad cursor from movie_basic_info.Movie id->$mMovieId",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
            }
        } else if(operationType == GlobalStaticVariables.USER_LIST_REMOVE_FLAG) {
            mUserFlag = GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE
        } else if(operationType == GlobalStaticVariables.USER_RATING_ADD_FLAG) {
            //Store the user rating to be updated
            mUserRating = ratingValue
            //"-1" indicates that it's rating and we need to deal with (later use for SnackBar message)
            mUserFlag = -1
        } else if(operationType == GlobalStaticVariables.USER_RATING_REMOVE_FLAG) {
            //Just update mUserFlag with "-1" (indicates that it's rating) and we need to deal with (later use for SnackBar message)
            mUserFlag = -1
        } else {
            LogDisplay.callLog(LOG_TAG,"Unknown operation type->$operationType",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
        }

        //Based on listType prepare the ContentValues and other parameters
        switch (listType) {
            case GlobalStaticVariables.USER_LIST_WATCHED:
                movieUserListFlagContentValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WATCHED,mUserFlag)
                userListCategory = GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WATCHED
                mUserListMsg = mContext.getString(R.string.drawer_menu_user_watched)
                break
            case GlobalStaticVariables.USER_LIST_WISH_LIST:
                movieUserListFlagContentValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WISH_LIST,mUserFlag)
                userListCategory = GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WISH_LIST
                mUserListMsg = mContext.getString(R.string.drawer_menu_user_wishlist)
                break
            case GlobalStaticVariables.USER_LIST_FAVOURITE:
                movieUserListFlagContentValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_FAVOURITE,mUserFlag)
                userListCategory = GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_FAVOURITE
                mUserListMsg = mContext.getString(R.string.drawer_menu_user_favourite)
                break
            case GlobalStaticVariables.USER_LIST_COLLECTION:
                movieUserListFlagContentValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_COLLECTION,mUserFlag)
                userListCategory = GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_COLLECTION
                mUserListMsg = mContext.getString(R.string.drawer_menu_user_collection)
                break
            case GlobalStaticVariables.USER_LIST_USER_RATING:
                movieUserListFlagContentValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_USER_RATING,mUserRating)
                break
            default:
                LogDisplay.callLog(LOG_TAG,"Unknown user list type->$listType",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
        }

        if(operationType == GlobalStaticVariables.USER_LIST_ADD_FLAG ||
                operationType == GlobalStaticVariables.USER_RATING_ADD_FLAG) {
            //If the movie_user_list_flag already present then just update the record
            if(movieUSerListFlagCursor.moveToFirst()) {
                retValue = mContentResolver.update(
                        MovieMagicContract.MovieUserListFlag.CONTENT_URI,
                        movieUserListFlagContentValues,
                        "$MovieMagicContract.MovieUserListFlag._ID = ?",
                        [Long.toString(movieUSerListFlagCursor.getLong(COL_MOVIE_USER_LIST_FLAG_ID))] as String[])
                if(retValue != 1) {
                    LogDisplay.callLog(LOG_TAG,"Update in movie_user_list_flag failed. Update Count->$retValue",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                } else { //If the return value to 1, indicate successful insert
                    LogDisplay.callLog(LOG_TAG,"Update in movie_user_list_flag successful. Update Count->$retValue",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                }
            } else { //Insert the record in the movie_user_list_flag table
                //Same record of movie_user_list_flag can be updated from public category or user category, so COLUMN_FOREIGN_KEY_ID usage
                //is irrelevant here. So updating it as 0 (could have been removed also but kept - feeling lazy :) )
                movieUserListFlagContentValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_FOREIGN_KEY_ID,0)
                movieUserListFlagContentValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID,mMovieId)
                final Uri uri = mContentResolver.insert(MovieMagicContract.MovieUserListFlag.CONTENT_URI,movieUserListFlagContentValues)
                if(ContentUris.parseId(uri) == -1) {
                    LogDisplay.callLog(LOG_TAG,"Insert in movie_user_list_flag failed. Uri->$uri",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                } else { //set the return value to 1, indicate successful insert
                    LogDisplay.callLog(LOG_TAG,"Insert in movie_user_list_flag successful. Uri->$uri",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                    retValue = 1
                }
            }
            //Now create the user record in movie_basic_info table (do not needed if it's rating)
            //Add the record to movie_basic_info is needed for user list and NOT for rating operation
            if(operationType == GlobalStaticVariables.USER_LIST_ADD_FLAG){
                movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,userListCategory)
                movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE,
                        GlobalStaticVariables.MOVIE_LIST_TYPE_USER_LOCAL_LIST)
                movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP,Utility.getTodayDate())
                movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP,Utility.getTodayDate())
                //Since the program logic is written in a way where the release date is expected as yyyy-MM-dd
                //so convert release date (which is stored as milli seconds) to that format
                movieBasicInfoCursor.moveToFirst()
                final int colIndex = movieBasicInfoCursor.getColumnIndex(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE)
                final long releaseDate = movieBasicInfoCursor.getLong(colIndex)
                final String formattedReleaseDate = Utility.convertMilliSecsToOrigReleaseDate(releaseDate)
                movieBasicInfoContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,formattedReleaseDate)
                //Need to remove the "_ID" as that is system generated
                movieBasicInfoContentValues.remove(MovieMagicContract.MovieBasicInfo._ID)
                final Uri uri = mContentResolver.insert(MovieMagicContract.MovieBasicInfo.CONTENT_URI,movieBasicInfoContentValues)
                if(ContentUris.parseId(uri) == -1) {
                    LogDisplay.callLog(LOG_TAG,"Insert in movie_basic_info failed. Uri->$uri",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG,"Insert in movie_basic_info successful. Uri->$uri",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                }
            }
        } else if (operationType == GlobalStaticVariables.USER_LIST_REMOVE_FLAG ||
                    operationType == GlobalStaticVariables.USER_RATING_REMOVE_FLAG) {
            boolean deleteUserListRecordFlag = false
            //It's remove operation (i.e. record is already present in the movie_user_list_flag table
            if(movieUSerListFlagCursor.moveToFirst()) {
                if(listType == GlobalStaticVariables.USER_LIST_WATCHED) {
                    if( movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_WISH_LIST_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_FAVOURITE_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_COLLECTION_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_USER_RATING) == 0.0) {
                        deleteUserListRecordFlag = true
                    }
                } else if(listType == GlobalStaticVariables.USER_LIST_WISH_LIST) {
                    if( movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_WATCHED_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_FAVOURITE_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_COLLECTION_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_USER_RATING) == 0.0) {
                        deleteUserListRecordFlag = true
                    }
                } else if(listType == GlobalStaticVariables.USER_LIST_FAVOURITE) {
                    if( movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_WATCHED_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_WISH_LIST_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_COLLECTION_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_USER_RATING) == 0.0) {
                        deleteUserListRecordFlag = true
                    }
                } else if(listType == GlobalStaticVariables.USER_LIST_COLLECTION) {
                    if( movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_WATCHED_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_WISH_LIST_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_FAVOURITE_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_USER_RATING) == 0.0) {
                        deleteUserListRecordFlag = true
                    }
                } else if(listType == GlobalStaticVariables.USER_LIST_USER_RATING) {
                    if( movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_WATCHED_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_WISH_LIST_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_FAVOURITE_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE &&
                        movieUSerListFlagCursor.getInt(COL_MOVIE_USER_LIST_FLAG_COLLECTION_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE) {
                        deleteUserListRecordFlag = true
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG,"Unknown user list type->$listType",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                }
                //Delete the record from movie_user_list_flag if deleteUserListRecordFlag flag value satisfies (i.e. single flag or only rating which is being removed)
                if(deleteUserListRecordFlag) {
                    retValue = mContentResolver.delete(
                            MovieMagicContract.MovieUserListFlag.CONTENT_URI,
                            "$MovieMagicContract.MovieUserListFlag._ID = ?",
                            [Long.toString(movieUSerListFlagCursor.getLong(COL_MOVIE_USER_LIST_FLAG_ID))] as String[])
                    if(retValue != 1) {
                        LogDisplay.callLog(LOG_TAG,"Delete from movie_user_list_flag failed. Delete Count->$retValue",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG,"Delete from movie_user_list_flag successful. Delete Count->$retValue",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                    }
                } else { // update the record in movie_user_list_flag
                    retValue = mContentResolver.update(
                            MovieMagicContract.MovieUserListFlag.CONTENT_URI,
                            movieUserListFlagContentValues,
                            "$MovieMagicContract.MovieUserListFlag._ID = ?",
                            [Long.toString(movieUSerListFlagCursor.getLong(COL_MOVIE_USER_LIST_FLAG_ID))] as String[])
                    if(retValue != 1) {
                        LogDisplay.callLog(LOG_TAG,"Update in movie_user_list_flag failed. Update Count->$retValue",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG,"Update in movie_user_list_flag successful. Update Count->$retValue",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                    }
                }

                //When user remove the movie from the list it should be ideally deleted but due to the logic
                //of the application, the user can still see the details of the movie even after the delete. So in order
                //to achieve that we need to keep that row in the table otherwise application will crash as the loader 0 of
                //detail fragment will not find the the corresponding movie and would return null. So instead of delete,
                //update the record with category "orphaned" which will ensure that it will not come in the user list but
                //record will remain there in the table and later will be cleaned up by the sync adapter while loading new
                //data as the sync adapter has logic to delete anything which is not user local list
                //Remove the record from movie_basic_info is needed for user list and NOT for rating operation
                if(operationType == GlobalStaticVariables.USER_LIST_REMOVE_FLAG) {
                    final ContentValues movieOrphanContentValue = new ContentValues()
                    movieOrphanContentValue.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY, GlobalStaticVariables.MOVIE_CATEGORY_ORPHANED)
                    movieOrphanContentValue.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE, GlobalStaticVariables.MOVIE_LIST_TYPE_ORPHANED)
                    final int rowCount = mContentResolver.update(
                            MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                            movieOrphanContentValue,
                            """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? and
                            $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? """,
                            [Integer.toString(mMovieId), userListCategory] as String[])
                    //Expecting just one record to be updated in movie_basic_info
                    if (rowCount != 1) {
                        LogDisplay.callLog(LOG_TAG, "Update movie_basic_info record to orphaned failed. Update Count->$rowCount", LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Update movie_basic_info record to orphaned successful. Update Count->$rowCount", LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
                    }
                }
            } else {
                LogDisplay.callLog(LOG_TAG,"Record not present in movie_user_list_flag. Movie Id->$mMovieId",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
            }
        }
        //Close the cursors
        if(movieBasicInfoCursor) {
            movieBasicInfoCursor.close()
        }
        if(movieUSerListFlagCursor) {
            movieUSerListFlagCursor.close()
        }
        //Return the value
        return retValue
    }

    @Override
    protected void onPostExecute(final Integer result) {
        if(mProgressDialog) {
            mProgressDialog.dismiss()
        }
        String snackBarMsg = null
        if(mUserFlag == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
            snackBarMsg = String.format(mContext.getString(R.string.user_list_add_message),mMovieTitle,mUserListMsg)
        } else if(mUserFlag == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE) {
            snackBarMsg = String.format(mContext.getString(R.string.user_list_del_message),mMovieTitle,mUserListMsg)
        } else if (mUserFlag == -1) { //"-1" indicates user rating
            snackBarMsg = String.format(mContext.getString(R.string.user_rating_add_message,mMovieTitle))
        } else {
            LogDisplay.callLog(LOG_TAG,"Unknown user flag value.User flag value->$mUserFlag",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
        }
        //Expecting a single row update or insert only
        if(result == 1) {
            if(mShowNotification) {
                Snackbar.make(mUserDrawableLayout, snackBarMsg, Snackbar.LENGTH_LONG).show()
            } else {
                LogDisplay.callLog(LOG_TAG,"Show notification flag is not set.mShowNotification value->$mShowNotification",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
            }
        } else {
            LogDisplay.callLog(LOG_TAG,"Something went wrong during user list update.Result value->$result",LogDisplay.UPDATE_USER_LIST_LOG_FLAG)
        }
    }
}