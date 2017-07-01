/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.contentprovider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

/**
 * Manages a local database for movie and related data.
 */

@CompileStatic
class MovieMagicDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = MovieMagicDbHelper.class.getSimpleName()

    private static final int DATABASE_VERSION = 1

    //Define as public as used by TestMovieMagicDatabase.groovy
    public static final String DATABASE_NAME = 'movie_magic.db'

    public MovieMagicDbHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION)
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {

        //Create the SQL to create movie_basic_info table
        final  String SQL_CREATE_MOVIE_BASIC_INFO_TABLE = """
                CREATE TABLE $MovieMagicContract.MovieBasicInfo.TABLE_NAME (
                $MovieMagicContract.MovieBasicInfo._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_ADULT_FLAG TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_ORIGINAL_TITLE TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE INTEGER NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY REAL NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_TITLE TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_VIDEO_FLAG TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG REAL NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT INTEGER NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER INTEGER NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG INTEGER DEFAULT 0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID INTEGER DEFAULT 0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_RECOMMENDATION_MOVIE_LINK_ID INTEGER DEFAULT 0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_ID INTEGER DEFAULT 0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_NAME TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_POSTER_PATH TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_BACKDROP_PATH TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_BUDGET INTEGER DEFAULT 0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_GENRE TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_HOME_PAGE TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_IMDB_ID TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COMPANIES TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COUNTRIES TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_REVENUE INTEGER DEFAULT 0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_RUNTIME INTEGER DEFAULT 0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_STATUS TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_TAGLINE TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_TMDB_USER_RATED_RATING REAL DEFAULT 0.0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP TEXT NOT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_USER_EXPORTED INTEGER DEFAULT 0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_1 TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_2 TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_3 TEXT NULL,
                $MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_4 INTEGER DEFAULT 0,
                $MovieMagicContract.MovieBasicInfo.COLUMN_FUTURE_USE_5 INTEGER DEFAULT 0,
                UNIQUE ($MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,
                $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE,
                $MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID) ON CONFLICT REPLACE)
                """
                /** Using COLUMN_RECOMMENDATION_MOVIE_LINK_ID caused duplicate entries for recommended movies
                 * on the ic_drawer_home page, so removed it from the UNIQUE replace logic. Since recommended movies
                 * are not used in detail movie page, so there is no harm if one gets replaced by other**/

        //Create the SQL to create movie_cast table
        final  String SQL_CREATE_MOVIE_CAST_TABLE = """
                CREATE TABLE $MovieMagicContract.MovieCast.TABLE_NAME (
                $MovieMagicContract.MovieCast._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MovieCast.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieCast.COLUMN_CAST_ID INTEGER DEFAULT 0,
                $MovieMagicContract.MovieCast.COLUMN_CAST_CHARACTER TEXT NOT NULL,
                $MovieMagicContract.MovieCast.COLUMN_CAST_CREDIT_ID TEXT NULL,
                $MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_NAME TEXT NOT NULL,
                $MovieMagicContract.MovieCast.COLUMN_CAST_ORDER INTEGER DEFAULT 0,
                $MovieMagicContract.MovieCast.COLUMN_CAST_PROFILE_PATH TEXT NULL,
                FOREIGN KEY ($MovieMagicContract.MovieCast.COLUMN_FOREIGN_KEY_ID) REFERENCES
                $MovieMagicContract.MovieBasicInfo.TABLE_NAME ($MovieMagicContract.MovieBasicInfo._ID)
                ON DELETE CASCADE,
                UNIQUE ($MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID,
                $MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_ID) ON CONFLICT REPLACE)
                """

        //Create the SQL to create movie_crew table
        final  String SQL_CREATE_MOVIE_CREW_TABLE = """
                CREATE TABLE $MovieMagicContract.MovieCrew.TABLE_NAME (
                $MovieMagicContract.MovieCrew._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MovieCrew.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieCrew.COLUMN_CREW_CREDIT_ID TEXT NULL,
                $MovieMagicContract.MovieCrew.COLUMN_CREW_DEPARTMENT TEXT NULL,
                $MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieCrew.COLUMN_CREW_JOB TEXT NOT NULL,
                $MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_NAME TEXT NOT NULL,
                $MovieMagicContract.MovieCrew.COLUMN_CREW_PROFILE_PATH TEXT NULL,
                FOREIGN KEY ($MovieMagicContract.MovieCrew.COLUMN_FOREIGN_KEY_ID) REFERENCES
                $MovieMagicContract.MovieBasicInfo.TABLE_NAME ($MovieMagicContract.MovieBasicInfo._ID)
                ON DELETE CASCADE,
                UNIQUE ($MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID,
                $MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_ID) ON CONFLICT REPLACE)
                """

        //Create the SQL to create movie_image table
        final  String SQL_CREATE_MOVIE_IMAGE_TABLE = """
                CREATE TABLE $MovieMagicContract.MovieImage.TABLE_NAME (
                $MovieMagicContract.MovieImage._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MovieImage.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE TEXT NOT NULL,
                $MovieMagicContract.MovieImage.COLUMN_IMAGE_HEIGHT INTEGER DEFAULT 0,
                $MovieMagicContract.MovieImage.COLUMN_IMAGE_WIDTH INTEGER DEFAULT 0,
                $MovieMagicContract.MovieImage.COLUMN_IMAGE_FILE_PATH TEXT NOT NULL,
                FOREIGN KEY ($MovieMagicContract.MovieImage.COLUMN_FOREIGN_KEY_ID) REFERENCES
                $MovieMagicContract.MovieBasicInfo.TABLE_NAME ($MovieMagicContract.MovieBasicInfo._ID)
                ON DELETE CASCADE,
                UNIQUE ($MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID,
                $MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE,
                $MovieMagicContract.MovieImage.COLUMN_IMAGE_FILE_PATH) ON CONFLICT REPLACE)
                """

        //Create the SQL to create movie_video table
        final  String SQL_CREATE_MOVIE_VIDEO_TABLE = """
                CREATE TABLE $MovieMagicContract.MovieVideo.TABLE_NAME (
                $MovieMagicContract.MovieVideo._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MovieVideo.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_ID TEXT NULL,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_KEY TEXT NOT NULL,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_NAME TEXT NULL,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE TEXT NOT NULL,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_SIZE TINTEGER NULL,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE TEXT NOT NULL,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_FOR_HOME_PAGE_USE_FLAG INTEGER DEFAULT 0,
                FOREIGN KEY ($MovieMagicContract.MovieVideo.COLUMN_FOREIGN_KEY_ID) REFERENCES
                $MovieMagicContract.MovieBasicInfo.TABLE_NAME ($MovieMagicContract.MovieBasicInfo._ID)
                ON DELETE CASCADE,
                UNIQUE ($MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_KEY,
                $MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE) ON CONFLICT REPLACE)
                """

        //Create the SQL to create movie_review table
        final  String SQL_CREATE_MOVIE_REVIEW_TABLE = """
                CREATE TABLE $MovieMagicContract.MovieReview.TABLE_NAME (
                $MovieMagicContract.MovieReview._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MovieReview.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieReview.COLUMN_REVIEW_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieReview.COLUMN_REVIEW_AUTHOR TEXT NOT NULL,
                $MovieMagicContract.MovieReview.COLUMN_REVIEW_CONTENT TEXT NOT NULL,
                $MovieMagicContract.MovieReview.COLUMN_REVIEW_URL TEXT NULL,
                FOREIGN KEY ($MovieMagicContract.MovieReview.COLUMN_FOREIGN_KEY_ID) REFERENCES
                $MovieMagicContract.MovieBasicInfo.TABLE_NAME ($MovieMagicContract.MovieBasicInfo._ID)
                ON DELETE CASCADE,
                UNIQUE ($MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID,
                $MovieMagicContract.MovieReview.COLUMN_REVIEW_ID) ON CONFLICT REPLACE)
                """

        //Create the SQL to create movie_release_date_info table
        final  String SQL_CREATE_MOVIE_RELEASE_TABLE = """
                CREATE TABLE $MovieMagicContract.MovieReleaseDate.TABLE_NAME (
                $MovieMagicContract.MovieReleaseDate._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MovieReleaseDate.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY TEXT NOT NULL,
                $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_CERTIFICATION TEXT NULL,
                $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_LANGUAGE TEXT NULL,
                $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_NOTE TEXT NULL,
                $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_DATE TEXT NOT NULL,
                $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_TYPE INTEGER DEFAULT 0,
                FOREIGN KEY ($MovieMagicContract.MovieReleaseDate.COLUMN_FOREIGN_KEY_ID) REFERENCES
                $MovieMagicContract.MovieBasicInfo.TABLE_NAME ($MovieMagicContract.MovieBasicInfo._ID)
                ON DELETE CASCADE,
                UNIQUE ($MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID,
                $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY,
                $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_DATE) ON CONFLICT REPLACE)
                """

        //Create the SQL to create movie_user_list_flag table
        final  String SQL_CREATE_MOVIE_USER_LIST_FLAG_TABLE = """
                CREATE TABLE $MovieMagicContract.MovieUserListFlag.TABLE_NAME (
                $MovieMagicContract.MovieUserListFlag._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MovieUserListFlag.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WATCHED INTEGER DEFAULT 0,
                $MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WISH_LIST INTEGER DEFAULT 0,
                $MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_FAVOURITE INTEGER DEFAULT 0,
                $MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_COLLECTION INTEGER DEFAULT 0,
                $MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_USER_RATING REAL DEFAULT 0.0,
                $MovieMagicContract.MovieUserListFlag.COLUMN_FUTURE_USE_1 TEXT DEFAULT NULL,
                $MovieMagicContract.MovieUserListFlag.COLUMN_FUTURE_USE_2 TEXT DEFAULT NULL,
                $MovieMagicContract.MovieUserListFlag.COLUMN_FUTURE_USE_3 INTEGER DEFAULT 0,
                $MovieMagicContract.MovieUserListFlag.COLUMN_FUTURE_USE_4 INTEGER DEFAULT 0)
                """

        //Create the SQL to create movie_person_info table
        final  String SQL_CREATE_MOVIE_PERSON_INFO_TABLE = """
                CREATE TABLE $MovieMagicContract.MoviePersonInfo.TABLE_NAME (
                $MovieMagicContract.MoviePersonInfo._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ADULT_FLAG TEXT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ALSO_KNOWN_AS TEXT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_BIOGRAPHY TEXT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_BIRTHDAY TEXT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_DEATHDAY TEXT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_HOMEPAGE TEXT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID INTEGER NOT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_NAME TEXT NOT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PLACE_OF_BIRTH TEXT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PROFILE_PATH TEXT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_IMDB_ID TEXT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_POPULARITY REAL DEFAULT 0.0,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PRESENT_FLAG INTEGER DEFAULT 0,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_CREATE_TIMESTAMP TEXT NOT NULL,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_UPDATE_TIMESTAMP TEXT NOT NULL,
                UNIQUE ($MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID,
                $MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_NAME) ON CONFLICT REPLACE)
                """

        //Create the SQL to create movie_person_cast table
        final  String SQL_CREATE_MOVIE_PERSON_CAST_TABLE = """
                CREATE TABLE $MovieMagicContract.MoviePersonCast.TABLE_NAME (
                $MovieMagicContract.MoviePersonCast._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MoviePersonCast.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID INTEGER NOT NULL,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ADULT_FLAG TEXT NULL,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CHARACTER TEXT NOT NULL,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CREDIT_ID TEXT NULL,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_TITLE TEXT NULL,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_POSTER_PATH TEXT NULL,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_RELEASE_DATE TEXT NULL,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_TITLE TEXT NOT NULL,
                FOREIGN KEY ($MovieMagicContract.MoviePersonCast.COLUMN_FOREIGN_KEY_ID) REFERENCES
                $MovieMagicContract.MoviePersonInfo.TABLE_NAME ($MovieMagicContract.MoviePersonInfo._ID)
                ON DELETE CASCADE,
                UNIQUE ($MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_MOVIE_ID,
                $MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CHARACTER) ON CONFLICT REPLACE)
                """

        //Create the SQL to create movie_person_crew table
        final  String SQL_CREATE_MOVIE_PERSON_CREW_TABLE = """
                CREATE TABLE $MovieMagicContract.MoviePersonCrew.TABLE_NAME (
                $MovieMagicContract.MoviePersonCrew._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MoviePersonCrew.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID INTEGER NOT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ADULT_FLAG TEXT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_CREDIT_ID TEXT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_DEPARTMENT TEXT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_MOVIE_ID INTEGER NOT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_JOB TEXT NOT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_TITLE TEXT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_POSTER_PATH TEXT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_RELEASE_DATE TEXT NULL,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_TITLE TEXT NOT NULL,
                FOREIGN KEY ($MovieMagicContract.MoviePersonCrew.COLUMN_FOREIGN_KEY_ID) REFERENCES
                $MovieMagicContract.MoviePersonInfo.TABLE_NAME ($MovieMagicContract.MoviePersonInfo._ID)
                ON DELETE CASCADE,
                UNIQUE ( $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_MOVIE_ID,
                $MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_JOB) ON CONFLICT REPLACE)
                """
        //Create the SQL to create movie_person_image table
        final  String SQL_CREATE_MOVIE_PERSON_IMAGE_TABLE = """
                CREATE TABLE $MovieMagicContract.MoviePersonImage.TABLE_NAME (
                $MovieMagicContract.MoviePersonImage._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MoviePersonImage.COLUMN_FOREIGN_KEY_ID INTEGER NOT NULL,
                $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID INTEGER NOT NULL,
                $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ASPECT_RATIO REAL DEFAULT 0.0,
                $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_FILE_PATH TEXT NOT NULL,
                $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_HEIGHT INTEGER DEFAULT 0,
                $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ISO_639_1 INTEGER DEFAULT 0,
                $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_VOTE_AVERAGE REAL DEFAULT 0.0,
                $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_VOTE_COUNT INTEGER DEFAULT 0,
                $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_WIDTH TINTEGER DEFAULT 0,
                FOREIGN KEY ($MovieMagicContract.MoviePersonImage.COLUMN_FOREIGN_KEY_ID) REFERENCES
                $MovieMagicContract.MoviePersonInfo.TABLE_NAME ($MovieMagicContract.MoviePersonInfo._ID)
                ON DELETE CASCADE,
                UNIQUE ( $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID,
                $MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_FILE_PATH) ON CONFLICT REPLACE)
                """

        //Create the SQL to create movie_collection table
        final  String SQL_CREATE_MOVIE_COLLECTION_TABLE = """
                CREATE TABLE $MovieMagicContract.MovieCollection.TABLE_NAME (
                $MovieMagicContract.MovieCollection._ID INTEGER PRIMARY KEY,
                $MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID INTEGER NOT NULL,
                $MovieMagicContract.MovieCollection.COLUMN_COLLECTION_NAME TEXT NOT NULL,
                $MovieMagicContract.MovieCollection.COLUMN_COLLECTION_OVERVIEW TEXT NULL,
                $MovieMagicContract.MovieCollection.COLUMN_COLLECTION_POSTER_PATH TEXT NULL,
                $MovieMagicContract.MovieCollection.COLUMN_COLLECTION_BACKDROP_PATH TEXT NULL,
                $MovieMagicContract.MovieCollection.COLUMN_COLLECTION_MOVIE_PRESENT_FLAG INTEGER DEFAULT 0,
                $MovieMagicContract.MovieCollection.COLUMN_COLLECTION_CREATE_TIMESTAMP TEXT NOT NULL,
                $MovieMagicContract.MovieCollection.COLUMN_COLLECTION_UPDATE_TIMESTAMP TEXT NOT NULL,
                UNIQUE ($MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID,
                $MovieMagicContract.MovieCollection.COLUMN_COLLECTION_NAME) ON CONFLICT REPLACE)
                """

        //Now create all the tables
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_BASIC_INFO_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_CAST_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_CREW_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_IMAGE_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_VIDEO_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_REVIEW_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_RELEASE_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_USER_LIST_FLAG_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_PERSON_INFO_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_PERSON_CAST_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_PERSON_CREW_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_PERSON_IMAGE_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_COLLECTION_TABLE)
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {
        // This database is cache for online data and also stores user data, so its upgrade policy is
        // not straight forward. So  simple drop and create won't work. Need to take care before upgrade.
        // As of now, upgrade strategy is not yet thought of!
        // Too update the schema without wiping data, commenting out the following DROP lines
        // should be the top priority before modifying this method.
        // Note that this only fires if the version number of database is changed.
        // It does NOT depend on the version number for the application.

        LogDisplay.callLog(LOG_TAG,"Upgrading database from version $oldVersion to $newVersion," +
                " which will destroy all old data",LogDisplay.MOVIE_MAGIC_DB_HELPER_LOG_FLAG)

        //Drop all the tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MovieBasicInfo.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MovieCast.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MovieCrew.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MovieImage.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MovieVideo.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MovieReview.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MovieReleaseDate.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MovieUserListFlag.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MoviePersonInfo.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MoviePersonCast.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MoviePersonCrew.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MoviePersonImage.TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $MovieMagicContract.MovieCollection.TABLE_NAME")

        //Call onCreate to re-create the tables
        onCreate(sqLiteDatabase)
    }

    @Override
    void onOpen(final SQLiteDatabase db) {
        super.onOpen(db)
        //Sqlite disables foreign key constrain by default, so need to enable it
        db.execSQL("PRAGMA foreign_keys=ON")
    }
}