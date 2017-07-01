/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper;
import groovy.transform.CompileStatic

@CompileStatic
class LoadCollectionData extends AsyncTask<Integer, Void, Void> {
    private static final String LOG_TAG = LoadCollectionData.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext

    public LoadCollectionData(final Context ctx) {
        mContext = ctx
        mContentResolver = mContext.getContentResolver()
    }
    @Override
    protected Void doInBackground(final Integer... params) {
        final int collectionId = params[0]
        final int cleanupFlag = params[1]
        LogDisplay.callLog(LOG_TAG,"Parms-> collectionId: $collectionId & cleanupFlag: $cleanupFlag",LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
        //TMDB api example
        //https://api.themoviedb.org/3/collection/10?api_key=key
        try {
            final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

            final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_COLLECTION_PATH)
                    .appendPath(Integer.toString(collectionId))
                    .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                    .build()

            final URL url = new URL(uri.toString())
            LogDisplay.callLog(LOG_TAG,"Collection url-> ${uri.toString()}",LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)

            final def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)

            /**
             * There could be cases where the collection record exists but the flag is not updated due to some error
             * So clean up the records and exit. The cleanup will automatically force notifydatachange() in collection table
             * which will force in a fresh load by CollectionMovieFragment
             */
            if(cleanupFlag == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                //Clean up collection record
                final String[] collArgs = [Integer.toString(collectionId)]
                final int collDelCount = mContentResolver.delete(
                                         MovieMagicContract.MovieCollection.CONTENT_URI,
                                         "$MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID = ?",
                                          collArgs)
                LogDisplay.callLog(LOG_TAG,"Total record deleted from collection table.->$collDelCount",LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                if (collDelCount == 1) {
                    LogDisplay.callLog(LOG_TAG, "Delete from collection table successful. Total deleted fcount->$collDelCount", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Delete from collection table failed. Total deleted fcount->$collDelCount", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                }
                //Clean up records from movie basic info table
                final String[] movieBasicArgs = [Integer.toString(collectionId),GlobalStaticVariables.MOVIE_CATEGORY_COLLECTION]
                final int movieBasicDelCount = mContentResolver.delete(
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                        """$MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_ID = ? AND
                           $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY= ? """,
                        movieBasicArgs)
                if (movieBasicDelCount == 1) {
                    LogDisplay.callLog(LOG_TAG, "Delete from movie basic info table successful. Total deleted count->$movieBasicDelCount", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Delete from movie basic info table failed. Total deleted count->$movieBasicDelCount", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                }
            } else {
                /**
                 * Process and load (insert) the collection data
                 * **/
                final ContentValues collectionDataContentValue = JsonParse.parseCollectionDataJson(jsonData) as ContentValues
                final Uri collectionDataUri
                if(collectionDataContentValue) {
                    collectionDataUri = mContentResolver.insert(MovieMagicContract.MovieCollection.CONTENT_URI, collectionDataContentValue)
                    LogDisplay.callLog(LOG_TAG, "Collection data inserted.Uri->$collectionDataUri", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                    if (ContentUris.parseId(collectionDataUri) == -1) {
                        LogDisplay.callLog(LOG_TAG, "Collection data insert failed. Uri->$collectionDataUri", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Collection data insert successful. Uri->$collectionDataUri", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG,'JsonParse.parseCollectionDataJson returned null',LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                }

                /**
                 * Process and load (insert) the collection movies
                 * **/
                final ContentValues[] collectionMoviesContentValues = JsonParse.parseCollectionMovieJson(jsonData) as ContentValues[]
                int collectionMovieCount = 0
                if(collectionMoviesContentValues) {
                    collectionMovieCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, collectionMoviesContentValues)
                    LogDisplay.callLog(LOG_TAG, "Total collection movie inserted.->$collectionMovieCount", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                    if (collectionMovieCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Collection movie insert in movie_basic_info is successful. Insert count.->$collectionMovieCount", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Collection movie insert in movie_basic_info failed. Insert count.->$collectionMovieCount", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG,'JsonParse.parseCollectionMovieJson returned null',LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                }

                /**
                 * Update the flag of collection table to false to indicate that collection movies are not inserted
                 * (This is done to ensure if collection record got inserted but the associated movies are not inserted)
                 * **/
                if (collectionDataUri && collectionMovieCount <= 0) {
                    final ContentValues collectionData = new ContentValues()
                    collectionData.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_MOVIE_PRESENT_FLAG, GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE)
                    final long collectionRowId = MovieMagicContract.MovieCollection.getCollectionRpwIdFromMovieCollectionUri(collectionDataUri)
                    final String[] args = [Long.toString(collectionRowId)]
                    final int updateCount = mContentResolver.update(
                            MovieMagicContract.MovieCollection.CONTENT_URI,
                            collectionData,
                            MovieMagicContract.MovieCollection._ID + "= ?",
                            args)
                    if (updateCount == 1) {
                        LogDisplay.callLog(LOG_TAG, 'Collection movie present flag update successful', LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Collection movie present flag update failed.Update count->$updateCount", LogDisplay.LOAD_COLLECTION_DATA_LOG_FLAG)
                    }
                }
            }
        } catch (final URISyntaxException e) {
            Log.e(LOG_TAG, "URISyntaxException: ${e.message}", e)
        } catch (final JsonException e) {
            Log.e(LOG_TAG, "JsonException: ${e.message}", e)
        } catch (final IOException e) {
            Log.e(LOG_TAG, "IOException: ${e.message}", e)
        }
        return null
    }
}