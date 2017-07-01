/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.test.AndroidTestCase
import com.moviemagic.dpaul.android.app.TestUtilities
import groovy.transform.CompileStatic

@CompileStatic
class TestMovieMagicDatabase extends AndroidTestCase {

    //public static final String LOG_TAG = TestDb.class.getSimpleName()

    private static long movieBasicInfoRowId
    private static long moviePersonRowId

    // Delete the database so that each test starts with a clean state
    def deleteTheDatabase() {
        mContext.deleteDatabase(MovieMagicDbHelper.DATABASE_NAME)
    }
    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    void setUp() {
        deleteTheDatabase()
    }

    void testCreateDb() throws Throwable {
        mContext.deleteDatabase(MovieMagicDbHelper.DATABASE_NAME)
        final SQLiteDatabase db = new MovieMagicDbHelper(this.mContext).getWritableDatabase()
        assertEquals(true, db.isOpen())

        // have we created all the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)

        assertTrue('Error: This means that the database has not been created correctly',c.moveToFirst())

        // verify that all the tables have been created.
        //Create a list with all the table names
        final def tables = []
        tables << MovieMagicContract.MovieBasicInfo.TABLE_NAME
        tables << MovieMagicContract.MovieCast.TABLE_NAME
        tables << MovieMagicContract.MovieCrew.TABLE_NAME
        tables << MovieMagicContract.MovieImage.TABLE_NAME
        tables << MovieMagicContract.MovieVideo.TABLE_NAME
        tables << MovieMagicContract.MovieReview.TABLE_NAME
        tables << MovieMagicContract.MovieReleaseDate.TABLE_NAME
        tables << MovieMagicContract.MovieUserListFlag.TABLE_NAME
        tables << MovieMagicContract.MoviePersonInfo.TABLE_NAME
        tables << MovieMagicContract.MoviePersonCast.TABLE_NAME
        tables << MovieMagicContract.MoviePersonCrew.TABLE_NAME
        tables << MovieMagicContract.MoviePersonImage.TABLE_NAME
        tables << MovieMagicContract.MovieCollection.TABLE_NAME
        // System creates another metadata table "android_metadata" which stores db version info
        // So at the end of the following loop, the tables list should be empty
        // verify that the tables have been created
        for(final i in 1..c.getCount()) {
            tables.remove(c.getString(0))
            c.moveToNext()
        }

        // if this fails, it means that the database doesn't contain all the required tables
        assertTrue('Error: The database does not contain all the tables', tables.isEmpty())

        // Now, do our tables contain the correct columns?
        //Check all the tables one at a time
        //Check the movie_basic_info table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieBasicInfo.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_basic_info table information.',c.moveToFirst())
        // Build a list of all of the columns
        def columeList = []
        columeList << MovieMagicContract.MovieBasicInfo._ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_ADULT_FLAG
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_ORIGINAL_TITLE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_TITLE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_VIDEO_FLAG
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_RECOMMENDATION_MOVIE_LINK_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_NAME
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_POSTER_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_BACKDROP_PATH
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_BUDGET
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_GENRE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_HOME_PAGE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_IMDB_ID
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COMPANIES
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COUNTRIES
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_REVENUE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_RUNTIME
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_STATUS
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_TAGLINE
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_TMDB_USER_RATED_RATING
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_USER_EXPORTED
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_1
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_2
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_3
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_4
        columeList << MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_5

        int columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_basic_info does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_basic_info table does not contain all the fields',columeList.isEmpty())

        //Check the movie_cast table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieCast.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_cast table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieCast._ID
        columeList << MovieMagicContract.MovieCast.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_CHARACTER
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_CREDIT_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_ID
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_NAME
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_ORDER
        columeList << MovieMagicContract.MovieCast.COLUMN_CAST_PROFILE_PATH

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_cast does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_cast table does not contain all the fields',columeList.isEmpty())

        //Check the movie_crew table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieCrew.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_crew table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieCrew._ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_CREDIT_ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_DEPARTMENT
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_ID
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_JOB
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_NAME
        columeList << MovieMagicContract.MovieCrew.COLUMN_CREW_PROFILE_PATH

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_cast does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_crew table does not contain all the fields',columeList.isEmpty())

        //Check the movie_image table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieImage.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_image table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieImage._ID
        columeList << MovieMagicContract.MovieImage.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_HEIGHT
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_WIDTH
        columeList << MovieMagicContract.MovieImage.COLUMN_IMAGE_FILE_PATH

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_image does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_image table does not contain all the fields',columeList.isEmpty())

        //Check the movie_video table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieVideo.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_video table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieVideo._ID
        columeList << MovieMagicContract.MovieVideo.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_ID
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_KEY
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_NAME
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_SIZE
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE
        columeList << MovieMagicContract.MovieVideo.COLUMN_VIDEO_FOR_HOME_PAGE_USE_FLAG

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_video does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_video table does not contain all the fields',columeList.isEmpty())

        //Check the movie_review table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieReview.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_review table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieReview._ID
        columeList << MovieMagicContract.MovieReview.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_ID
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_AUTHOR
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_CONTENT
        columeList << MovieMagicContract.MovieReview.COLUMN_REVIEW_URL

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_review does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_review table does not contain all the fields',columeList.isEmpty())

        //Check the movie_release_date table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieReleaseDate.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_release_date table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieReleaseDate._ID
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_CERTIFICATION
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_LANGUAGE
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_NOTE
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_DATE
        columeList << MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_TYPE

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_release_date does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_release_date table does not contain all the fields',columeList.isEmpty())

        //Check the movie_user_list_flag table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieUserListFlag.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_person_crew table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieUserListFlag._ID
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WATCHED
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WISH_LIST
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_FAVOURITE
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_COLLECTION
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_USER_RATING
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_FUTURE_USE_1
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_FUTURE_USE_2
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_FUTURE_USE_3
        columeList << MovieMagicContract.MovieUserListFlag.COLUMN_FUTURE_USE_4

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_person_crew does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_user_list_flag table does not contain all the fields',columeList.isEmpty())

        //Check the movie_person_info table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MoviePersonInfo.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_person_info table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MoviePersonInfo._ID
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ADULT_FLAG
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ALSO_KNOWN_AS
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_BIOGRAPHY
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_BIRTHDAY
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_DEATHDAY
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_HOMEPAGE
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_NAME
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PLACE_OF_BIRTH
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PROFILE_PATH
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_IMDB_ID
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_POPULARITY
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PRESENT_FLAG
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_CREATE_TIMESTAMP
        columeList << MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_UPDATE_TIMESTAMP

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_person_info does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_person_info table does not contain all the fields',columeList.isEmpty())

        //Check the movie_person_cast table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MoviePersonCast.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_person_cast table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MoviePersonCast._ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ADULT_FLAG
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CHARACTER
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CREDIT_ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_MOVIE_ID
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_TITLE
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_POSTER_PATH
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_RELEASE_DATE
        columeList << MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_TITLE

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_person_cast does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_person_cast table does not contain all the fields',columeList.isEmpty())

        //Check the movie_person_crew table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MoviePersonCrew.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_person_crew table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MoviePersonCrew._ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ADULT_FLAG
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_CREDIT_ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_DEPARTMENT
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_MOVIE_ID
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_JOB
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_TITLE
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_POSTER_PATH
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_RELEASE_DATE
        columeList << MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_TITLE

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_person_crew does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_person_crew table does not contain all the fields',columeList.isEmpty())

        //Check the movie_person_image table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MoviePersonImage.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_person_image table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MoviePersonImage._ID
        columeList << MovieMagicContract.MoviePersonImage.COLUMN_FOREIGN_KEY_ID
        columeList << MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID
        columeList << MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ASPECT_RATIO
        columeList << MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_FILE_PATH
        columeList << MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_HEIGHT
        columeList << MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ISO_639_1
        columeList << MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_VOTE_AVERAGE
        columeList << MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_VOTE_COUNT
        columeList << MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_WIDTH

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_person_crew does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_person_image table does not contain all the fields',columeList.isEmpty())

        //Check the movie_collection table
        c = db.rawQuery("PRAGMA table_info($MovieMagicContract.MovieCollection.TABLE_NAME)", null)
        assertTrue('Error: Unable to query the database for movie_collection table information.',c.moveToFirst())
        // Build a list of all of the columns
        columeList = []
        columeList << MovieMagicContract.MovieCollection._ID
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_NAME
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_OVERVIEW
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_POSTER_PATH
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_BACKDROP_PATH
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_MOVIE_PRESENT_FLAG
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_CREATE_TIMESTAMP
        columeList << MovieMagicContract.MovieCollection.COLUMN_COLLECTION_UPDATE_TIMESTAMP

        columnNameIndex = c.getColumnIndex('name')
        for(final i in 1..c.getCount()) {
            columeList.remove(c.getString(columnNameIndex))
            c.moveToNext()
        }
        // if this fails, it means that movie_collection does not contain all the fields
        // entry columns
        assertTrue('Error: The movie_collection table does not contain all the fields',columeList.isEmpty())

        //Close the database
        db.close()
    }

    void testMovieBasicInfoAndRelatedTables() {
        // First step: Get reference to writable database
        final SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMovieValues()
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_basic_info data into the Database', rowId != -1)
        //Populate movieBaiscInfoRowId which is used as Foreign key in other tables
        movieBasicInfoRowId = rowId
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieBasicInfo.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieBasicInfo.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieBasicInfo.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie cast table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMovieCastValues(movieBasicInfoRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCast.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_cast data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MovieCast.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieCast.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieCast.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieCast.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie crew table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMovieCrewValues(movieBasicInfoRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCrew.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_crew data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MovieCrew.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieCrew.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieCrew.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieCrew.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie image table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMovieImageValues(movieBasicInfoRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieImage.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_image data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MovieImage.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieImage.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieImage.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieImage.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie video table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMovieVideoValues(movieBasicInfoRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieVideo.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_video data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MovieVideo.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieVideo.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieVideo.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieVideo.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie review table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMovieReviewValues(movieBasicInfoRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReview.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_review data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MovieReview.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieReview.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieReview.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieReview.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie release date table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMovieReleaseDateValues(movieBasicInfoRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_release_date data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieReleaseDate.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieReleaseDate.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieReleaseDate.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie_user_list_flag table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMovieUserListFlagValues(movieBasicInfoRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieUserListFlag.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_user_list_flag data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MovieUserListFlag.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieUserListFlag.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieUserListFlag.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieUserListFlag.TABLE_NAME query",cursor.moveToNext())

        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testMoviePersonInfoAndRelatedTables() {
        // First step: Get reference to writable database
        final SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        ContentValues contentValues = TestUtilities.createMoviePersonInfoValues()
        // Insert ContentValues into database table and get a row ID back
        long rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_person_info data into the Database', rowId != -1)
        //Populate moviePersonRowId which is used as Foreign key in other tables
        moviePersonRowId = rowId
        // Query the database and receive a Cursor back
        Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MoviePersonInfo.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MoviePersonInfo.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MoviePersonInfo.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie person cast table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMoviePersonCastValues(moviePersonRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_person_cast data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MoviePersonCast.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MoviePersonCast.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MoviePersonCast.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie person crew table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMoviePersonCrewValues(moviePersonRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_person_crew data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MoviePersonCrew.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MoviePersonCrew.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MoviePersonCrew.TABLE_NAME query",cursor.moveToNext())

        //Now test the movie person image table
        // Create ContentValues of what we want to insert
        contentValues = TestUtilities.createMoviePersonImageValues(moviePersonRowId)
        // Insert ContentValues into database table and get a row ID back
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonImage.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_person_image data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        cursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonImage.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MoviePersonImage.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MoviePersonImage.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only one record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MoviePersonImage.TABLE_NAME query",cursor.moveToNext())

        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }


    void testMovieCollectionTable() {
        // First step: Get reference to writable database
        final SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        // Create ContentValues of what we want to insert
        final ContentValues contentValues = TestUtilities.createMovieCollectionValues()
        // Insert ContentValues into database table and get a row ID back
        final long rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCollection.TABLE_NAME,null,contentValues)
        //Verify record inserted and record received
        assertTrue('Error: Unable to Insert movie_collection data into the Database', rowId != -1)
        // Query the database and receive a Cursor back
        final Cursor cursor = sqLiteDatabase.query(MovieMagicContract.MovieCollection.TABLE_NAME,null,null,null,null,null,null)
        // Move the cursor to a valid database row
        assertTrue("Error: No record returned from the $MovieMagicContract.MovieCollection.TABLE_NAME table",cursor.moveToFirst())
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: $MovieMagicContract.MovieCollection.TABLE_NAME query data verification failed",cursor,contentValues)
        //Move the cursor to ensure there is only once record returned
        assertFalse("Error: more than one record returned by the $MovieMagicContract.MovieCollection.TABLE_NAME query",cursor.moveToNext())
        // Finally, close the cursor and database
        cursor.close()
        sqLiteDatabase.close()
    }

    void testCascadeDelete() {
        Cursor delCursor

        //First insert records to all tables which are to be tested
        testMovieBasicInfoAndRelatedTables()
        testMoviePersonInfoAndRelatedTables()

        // Now Get reference to writable database
        final SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()
        //Now delete the records from movie_basic_info which in turn should delete records from other
        //child tables
        sqLiteDatabase.delete(MovieMagicContract.MovieBasicInfo.TABLE_NAME, null, null)
        //Query the main and child tables and ensure all records are deleted
        delCursor = sqLiteDatabase.query(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_basic_info during delete', 0, delCursor.getCount())
        delCursor = sqLiteDatabase.query(MovieMagicContract.MovieCast.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_cast during delete', 0, delCursor.getCount())
        delCursor = sqLiteDatabase.query(MovieMagicContract.MovieCrew.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_crew during delete', 0, delCursor.getCount())
        delCursor = sqLiteDatabase.query(MovieMagicContract.MovieImage.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_image during delete', 0, delCursor.getCount())
        delCursor = sqLiteDatabase.query(MovieMagicContract.MovieVideo.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_video during delete', 0, delCursor.getCount())
        delCursor = sqLiteDatabase.query(MovieMagicContract.MovieReview.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_review during delete', 0, delCursor.getCount())
        delCursor = sqLiteDatabase.query(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_release_date during delete', 0, delCursor.getCount())
        //Changed the database schema - cascade delete is no more done for movie_user_list_flag table
//        delCursor = sqLiteDatabase.query(MovieMagicContract.MovieUserListFlag.TABLE_NAME,null,null,null,null,null,null)
//        assertEquals('Error: Records not deleted from movie_user_list_flag during delete', 0, delCursor.getCount())

        //Now delete the records from movie_person_info which in turn should delete records from other
        //child tables
        sqLiteDatabase.delete(MovieMagicContract.MoviePersonInfo.TABLE_NAME, null, null)
        //Query the main and child tables and ensure all records are deleted
        delCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_person_info during delete', 0, delCursor.getCount())
        delCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_person_cast during delete', 0, delCursor.getCount())
        delCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_person_cast during delete', 0, delCursor.getCount())
        delCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonImage.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_person_image during delete', 0, delCursor.getCount())

        // Finally, close the cursor and database
        delCursor.close()
        sqLiteDatabase.close()
    }

    void testUniqueReplace() {
        Cursor queryCursor
        ContentValues contentValues
        long rowId

        //First delete the database so that we always have a clean start
        deleteTheDatabase()

        //Get reference to writable database
        SQLiteDatabase sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()

        //First test the replace (and also cascade) of movie_basic_info & movie_person_info tables
        //Let's insert one record in all the main and child tables
        contentValues = TestUtilities.createMovieValues()
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_basic_info data into the Database', rowId != -1)
        //Populate movieBaiscInfoRowId which is used as Foreign key in other tables
        movieBasicInfoRowId = rowId
        contentValues = TestUtilities.createMovieCastValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCast.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_cast data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieCrewValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCrew.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_crew data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieImageValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieImage.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_image data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieVideoValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieVideo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_video data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieReviewValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReview.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_review data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieReleaseDateValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_release_date data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieUserListFlagValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieUserListFlag.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_user_list_flag data into the Database', rowId != -1)
        contentValues = TestUtilities.createMoviePersonInfoValues()
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_info data into the Database', rowId != -1)
        //Populate moviePersonRowId which is used as Foreign key in other tables
        moviePersonRowId = rowId
        contentValues = TestUtilities.createMoviePersonCastValues(moviePersonRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_cast data into the Database', rowId != -1)
        contentValues = TestUtilities.createMoviePersonCrewValues(moviePersonRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_crew data into the Database', rowId != -1)
        contentValues = TestUtilities.createMoviePersonImageValues(moviePersonRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonImage.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_image data into the Database', rowId != -1)
        //Now insert same record in movie_basic_info and ensure replace works - there should be only one record in movie_basic_info
        //and all records should get deleted from the child tables because of cascade effect
        contentValues = TestUtilities.createMovieValues()
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_basic_info data into the Database', rowId != -1)
        //Query the movie_basic_info and child tables and ensure there is only one record in movie_basic_info
        //and all child tables have no records
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_basic_info during insert', 1, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieCast.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not deleted from movie_cast during movie_basic_info replace', 0, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieCrew.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not deleted from movie_crew during movie_basic_info replace', 0, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieImage.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not deleted from movie_image during movie_basic_info replace', 0, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieVideo.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not deleted from movie_video during movie_basic_info replace', 0, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieReview.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not deleted from movie_review during movie_basic_info replace', 0, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not deleted from movie_release_date during movie_basic_info replace', 0, queryCursor.getCount())
        //Unique replace is not used for movie_user_list_flag,but CASCADE delete is there so count should be zero
        //Changed the database schema - cascade delete is no more done for movie_user_list_flag table
//        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieUserListFlag.TABLE_NAME,null,null,null,null,null,null)
//        assertEquals('Error: Record not deleted from movie_user_flag_list during movie_basic_info replace', 0, queryCursor.getCount())
        //Now insert same record movie_person_info and ensure replace works - there should be only one record in movie_person_info
        //and all records should get deleted from the child tables because of cascade effect
        contentValues = TestUtilities.createMoviePersonInfoValues()
        sqLiteDatabase.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,contentValues)
        //Query the movie_person_info and child tables and ensure there is only one record in movie_person_info
        //and all child tables have no records
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_person_info during insert', 1, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not deleted from movie_person_cast during movie_person_info replace', 0, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not deleted from movie_person_cast during movie_person_info replace', 0, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonImage.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not deleted from movie_person_image during movie_person_info replace', 0, queryCursor.getCount())

        //This is a logical end of the testing and will delete the database before next test, so close the database
        sqLiteDatabase.close()

        //Let's delete the database again so that we always have a clean start for next test
        deleteTheDatabase()
        //Get reference to writable database
        sqLiteDatabase = new MovieMagicDbHelper(mContext).getWritableDatabase()

        //Now make two inserts to all tables which are to be tested, since it's same data so replace should
        //work and there should only be one record
        //** First Insert **
        contentValues = TestUtilities.createMovieValues()
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_basic_info data into the Database', rowId != -1)
        //Populate movieBaiscInfoRowId which is used as Foreign key in other tables
        movieBasicInfoRowId = rowId
        contentValues = TestUtilities.createMovieCastValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCast.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_cast data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieCrewValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCrew.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_crew data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieImageValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieImage.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_image data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieVideoValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieVideo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_video data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieReviewValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReview.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_review data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieReleaseDateValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_release_date data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieUserListFlagValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieUserListFlag.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_user_list_flag data into the Database', rowId != -1)
        contentValues = TestUtilities.createMoviePersonInfoValues()
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_info data into the Database', rowId != -1)
        //Populate moviePersonRowId which is used as Foreign key in other tables
        moviePersonRowId = rowId
        contentValues = TestUtilities.createMoviePersonCastValues(moviePersonRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_cast data into the Database', rowId != -1)
        contentValues = TestUtilities.createMoviePersonCrewValues(moviePersonRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_crew data into the Database', rowId != -1)
        contentValues = TestUtilities.createMoviePersonImageValues(moviePersonRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonImage.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_image data into the Database', rowId != -1)

        //** Second Insert **
        contentValues = TestUtilities.createMovieValues()
        //Change the movie id, so that it is considered as new record in movie_basic_info (i.e. doesn't get replaced)
        contentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,999)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_basic_info data into the Database', rowId != -1)
        //Populate movieBaiscInfoRowId which is used as Foreign key in other tables
        movieBasicInfoRowId = rowId
        contentValues = TestUtilities.createMovieCastValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCast.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_cast data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieCrewValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieCrew.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_crew data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieImageValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieImage.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_image data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieVideoValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieVideo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_video data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieReviewValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReview.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_review data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieReleaseDateValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_release_date data into the Database', rowId != -1)
        contentValues = TestUtilities.createMovieUserListFlagValues(movieBasicInfoRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MovieUserListFlag.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_user_list_flag data into the Database', rowId != -1)
        contentValues = TestUtilities.createMoviePersonInfoValues()
        //Change the person id, so that it is considered as new record in movie_person_info (i.e. doesn't get replaced)
        contentValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID,999)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_info data into the Database', rowId != -1)
        //Populate moviePersonRowId which is used as Foreign key in other tables
        moviePersonRowId = rowId
        contentValues = TestUtilities.createMoviePersonCastValues(moviePersonRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_cast data into the Database', rowId != -1)
        contentValues = TestUtilities.createMoviePersonCrewValues(moviePersonRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_crew data into the Database', rowId != -1)
        contentValues = TestUtilities.createMoviePersonImageValues(moviePersonRowId)
        rowId = sqLiteDatabase.insert(MovieMagicContract.MoviePersonImage.TABLE_NAME,null,contentValues)
        assertTrue('Error: Unable to Insert movie_person_image data into the Database', rowId != -1)

        //Query the main_activity_menu (count in main_activity_menu should be 2) and child tables and ensure there is only one record
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieBasicInfo.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_basic_info during insert', 2, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieCast.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_cast during insert', 1, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieCrew.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_crew during insert', 1, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieImage.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_image during insert', 1, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieVideo.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_video during insert', 1, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieReview.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_review during insert', 1, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieReleaseDate.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_release_date during insert', 1, queryCursor.getCount())
        //Unique replace is not used for movie_user_list_flag, so the record count should be 2
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MovieUserListFlag.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_user_list_flag during insert', 2, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonInfo.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_person_info during insert', 2, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCast.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_person_cast during insert', 1, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonCrew.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_person_crew during insert', 1, queryCursor.getCount())
        queryCursor = sqLiteDatabase.query(MovieMagicContract.MoviePersonImage.TABLE_NAME,null,null,null,null,null,null)
        assertEquals('Error: Record not replaced in movie_person_image during insert', 1, queryCursor.getCount())

        // Finally, close the cursor and database
        queryCursor.close()
        sqLiteDatabase.close()
    }
}