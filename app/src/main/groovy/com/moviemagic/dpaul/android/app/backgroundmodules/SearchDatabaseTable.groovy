/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.transform.CompileStatic

@CompileStatic
class SearchDatabaseTable {
    private static final String LOG_TAG = SearchDatabaseTable.class.getSimpleName()

    public static final String SEARCH_FTS_VIRTUAL_TABLE_NAME = "search_movie_basic_info_table"

    public static final String SEARCH_FTS_COLUMN_MOVIE_ID = 'search_fts_movie_id'
    public static final String SEARCH_FTS_COLUMN_ADULT_FLAG = 'search_fts_adult_flag'
    public static final String SEARCH_FTS_COLUMN_BACKDROP_PATH = 'search_fts_backdrop_path'
    public static final String SEARCH_FTS_COLUMN_ORIGINAL_TITLE = 'search_fts_original_title'
    public static final String SEARCH_FTS_COLUMN_RELEASE_DATE = 'search_fts_release_date'
    public static final String SEARCH_FTS_COLUMN_POSTER_PATH = 'search_fts_poster_path'
    public static final String SEARCH_FTS_COLUMN_TITLE = 'search_fts_title'
    public static final String SEARCH_FTS_COLUMN_PAGE_NUMBER = 'search_fts_page_number'
    public static final String SEARCH_FTS_COLUMN_MOVIE_CATEGORY = 'search_fts_movie_category'
    public static final String SEARCH_FTS_COLUMN_QUERY_STRING = 'search_fts_query_string'

    private final SearchDatabaseOpenHelper mSearchDatabaseOpenHelper

    public SearchDatabaseTable (final Context context) {
        mSearchDatabaseOpenHelper = new SearchDatabaseOpenHelper(context)
    }

    public static class SearchDatabaseOpenHelper extends SQLiteOpenHelper {
        private final Context mHelperContext
        private static final String SEARCH_DATABASE_NAME = "search_database.db"
        private static final int SEARCH_DATABASE_VERSION = 1

        //Create the SQL to create search_movie_basic_info table
        //Constraints not worked for fts table, so not used and datatype is also optional but used here
        private final  String SQL_CREATE_SEARCH_FTS_VIRTUAL_TABLE = """
                CREATE VIRTUAL TABLE $SearchDatabaseTable.SEARCH_FTS_VIRTUAL_TABLE_NAME
                USING fts3 (
                $SEARCH_FTS_COLUMN_MOVIE_ID INTEGER,
                $SEARCH_FTS_COLUMN_ADULT_FLAG TEXT,
                $SEARCH_FTS_COLUMN_BACKDROP_PATH TEXT,
                $SEARCH_FTS_COLUMN_ORIGINAL_TITLE TEXT,
                $SEARCH_FTS_COLUMN_RELEASE_DATE INTEGER,
                $SEARCH_FTS_COLUMN_POSTER_PATH TEXT,
                $SEARCH_FTS_COLUMN_TITLE TEXT,
                $SEARCH_FTS_COLUMN_PAGE_NUMBER INTEGER,
                $SEARCH_FTS_COLUMN_MOVIE_CATEGORY TEXT,
                $SEARCH_FTS_COLUMN_QUERY_STRING TEXT)"""

        SearchDatabaseOpenHelper(final Context context) {
            super(context, SEARCH_DATABASE_NAME, null, SEARCH_DATABASE_VERSION)
            mHelperContext = context
        }

        @Override
        void onCreate(final SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_SEARCH_FTS_VIRTUAL_TABLE)
        }

        @Override
        void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {
            LogDisplay.callLog(LOG_TAG,"Upgrading database from version $oldVersion to $newVersion," +
                    " which will destroy all old data",LogDisplay.SEARCH_DATABASE_TABLE_LOG_FLAG)
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $SearchDatabaseTable.SEARCH_FTS_VIRTUAL_TABLE_NAME")

            //Call onCreate to re-create the tables
            onCreate(sqLiteDatabase)
        }
    }

    public int bulkInsert(final ContentValues[] values) {
        final SQLiteDatabase db = mSearchDatabaseOpenHelper.getWritableDatabase()
        db.beginTransaction()
        int returnCount = 0
        try {
            for (final ContentValues value : values) {
                convertReleaseDate(value)
                long _id = db.insert(SEARCH_FTS_VIRTUAL_TABLE_NAME, null, value)
                if (_id != -1) {
                    returnCount++
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return returnCount
    }

    public Cursor getSearchResult(final String query, final String[] columns) {
        final int wordCount = query.isEmpty() ? 0 : query.split("\\s+").length
        LogDisplay.callLog(LOG_TAG,"Query string -> $query & word count -> $wordCount",LogDisplay.SEARCH_DATABASE_TABLE_LOG_FLAG)
        final String[] selectionArgs
        if(wordCount > 1) {
            final String quotedStringWithLastStar = "\"$query*\""
            LogDisplay.callLog(LOG_TAG,"Quoted string after adding \" and last star-> $quotedStringWithLastStar",LogDisplay.SEARCH_DATABASE_TABLE_LOG_FLAG)
            final String starString = quotedStringWithLastStar.replace(' ','* ')
            selectionArgs = [starString] as String[]
        } else {
            selectionArgs = ["$query*"] as String[]
        }
        LogDisplay.callLog(LOG_TAG,"Query string after adding * -> ${selectionArgs.toString()}",LogDisplay.SEARCH_DATABASE_TABLE_LOG_FLAG)

        final String selection = "$SEARCH_FTS_COLUMN_QUERY_STRING MATCH ?"

        return queryResult(selection, selectionArgs, columns)
    }

    private Cursor queryResult(final String selection, final String[] selectionArgs, final String[] columns) {
        final SQLiteDatabase sqLiteDatabase = mSearchDatabaseOpenHelper.getReadableDatabase()
        final Cursor resultSet = sqLiteDatabase.query (true,                            // Indicate distinct records
                                                      SEARCH_FTS_VIRTUAL_TABLE_NAME,    // Table name
                                                      columns,                          // Columns to fetch into
                                                      selection,                        // Selection criteria
                                                      selectionArgs,                    // Selection arguments
                                                      SEARCH_FTS_COLUMN_MOVIE_ID,       // Unique on movie ids
                                                      null,                             // Having - Not used
                                                      null,                             // Order By - Not used
                                                      null)                             // Limit - Not used
        if (resultSet == null) {
            sqLiteDatabase.close()
            return null
        } else if (!resultSet.moveToFirst()) {
            resultSet.close()
            sqLiteDatabase.close()
            return null
        }
        sqLiteDatabase.close()
        return resultSet
    }

    /**
     * Convert the movie release date string to numeric value (milliseconds)
     * @param values The date value to be converted to
     */

    private static void convertReleaseDate(final ContentValues values) {
        // Covert the movie release date
        if (values.containsKey(SEARCH_FTS_COLUMN_RELEASE_DATE)) {
            final String movieReleaseDate = values.getAsString(SEARCH_FTS_COLUMN_RELEASE_DATE)
            values.put(SEARCH_FTS_COLUMN_RELEASE_DATE, MovieMagicContract.convertMovieReleaseDate(movieReleaseDate))
        }
    }
}