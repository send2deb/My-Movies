/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import groovy.transform.CompileStatic


@CompileStatic
final class MovieMagicContract {
    private static final String LOG_TAG = MovieMagicContract.class.getSimpleName()
    // To make it easy to sort the list by movie release date, we store the date
    // in the database in milliseconds format using SimpleDateFormat and Date
    // The value is the number of milliseconds since Jan. 1, 1970, midnight GMT.
    static long convertMovieReleaseDate(final String releaseDate) {
        //Split the date string which is of format yyyy-mm-dd
        try {
            final Date simpleReleaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(releaseDate)
            final long timeInMilliSeconds = simpleReleaseDate.getTime()
            return timeInMilliSeconds
        } catch (final ParseException e) {
            Log.e(LOG_TAG,"Release date ->$releaseDate")
            Log.e(LOG_TAG, e.message, e)
            e.printStackTrace()
        }
        return null
    }

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    static final String CONTENT_AUTHORITY = 'com.moviemagic.dpaul.android.app'

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    static final Uri BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

    // Possible paths (appended to base content URI for possible URI's) for all tables
    static final String PATH_MOVIE_BASIC_INFO = 'movie_basic_info'
    static final String PATH_MOVIE_CAST = 'movie_cast'
    static final String PATH_MOVIE_CREW = 'movie_crew'
    static final String PATH_MOVIE_IMAGE = 'movie_image'
    static final String PATH_MOVIE_VIDEO = 'movie_video'
    static final String PATH_MOVIE_REVIEW = 'movie_review'
    static final String PATH_MOVIE_RELEASE_DATE = 'movie_release_date'
    static final String PATH_MOVIE_USER_LIST_FLAG = 'movie_user_list_flag'
    static final String PATH_MOVIE_PERSON_INFO = 'movie_person_info'
    static final String PATH_MOVIE_PERSON_CAST = 'movie_person_cast'
    static final String PATH_MOVIE_PERSON_CREW = 'movie_person_crew'
    static final String PATH_MOVIE_PERSON_IMAGE = 'movie_person_image'
    static final String PATH_MOVIE_COLLECTION = 'movie_collection'

    /**
        Inner class that defines the table contents of the movie_basic_info table
     */
    public static final class MovieBasicInfo implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_BASIC_INFO

        //Following fields till COLUMN_VOTE_COUNT are fetched along with initial list of movies
        //Column to store movie id
        static final String COLUMN_MOVIE_ID = 'movie_id'
        //Column to store adult flag
        static final String COLUMN_ADULT_FLAG = 'adult_flag'
        //Column to store movie backdrop path
        static final String COLUMN_BACKDROP_PATH = 'backdrop_path'
        //Column to store movie original title
        static final String COLUMN_ORIGINAL_TITLE = 'original_title'
        //Column to store movie overview
        static final String COLUMN_OVERVIEW = 'overview'
        //Column to store movie release date
        static final String COLUMN_RELEASE_DATE = 'release_date'
        //Column to store movie poster path
        static final String COLUMN_POSTER_PATH = 'poster_path'
        //Column to store movie popularity
        static final String COLUMN_POPULARITY = 'popularity'
        //Column to store movie title
        static final String COLUMN_TITLE = 'title'
        //Column to store movie ic_drawer_now_playing flag
        static final String COLUMN_VIDEO_FLAG = 'video_flag'
        //Column to store movie voting average value
        static final String COLUMN_VOTE_AVG = 'vote_average'
        //Column to store movie vote count
        static final String COLUMN_VOTE_COUNT = 'vote_count'
        //Column to store movie page number of the API response
        static final String COLUMN_PAGE_NUMBER = 'page_number'
        //Column to store movie category (Not fetched from API)
        static final String COLUMN_MOVIE_CATEGORY = 'movie_category'
        //Column to indicate the type (tmdb_public / tmdb_user / tmdb_similar/ user_local_list)
        static final String COLUMN_MOVIE_LIST_TYPE = 'movie_list_type'

        //Column to track if the detail data is loaded or not (0 - false / 1 - true)
        static final String COLUMN_DETAIL_DATA_PRESENT_FLAG = 'detail_data_present_flag'
        //Column to track similar movies (Zero for original movie id)
        static final String COLUMN_SIMILAR_MOVIE_LINK_ID = 'similar_movie_link_id'
        //Column to track recommendation movies (Zero for original movie id)
        static final String COLUMN_RECOMMENDATION_MOVIE_LINK_ID = 'recommendation_movie_link_id'

        //Following fields are fetched for each movie (part of detail) and added later
        //Column to store ic_drawer_user_collection id
        static final String COLUMN_COLLECTION_ID = 'collection_id'
        //Column to store ic_drawer_user_collection name
        static final String COLUMN_COLLECTION_NAME = 'collection_name'
        //Column to store ic_drawer_user_collection poster path
        static final String COLUMN_COLLECTION_POSTER_PATH = 'collection_poster_path'
        //Column to store collection backdrop path
        static final String COLUMN_COLLECTION_BACKDROP_PATH = 'collection_backdrop_path'
        //Column to store movie budget
        static final String COLUMN_BUDGET = 'budget'
        //Column to store movie genre
        static final String COLUMN_GENRE = 'genre'
        //Column to store movie ic_drawer_home page
        static final String COLUMN_HOME_PAGE= 'home_page'
        //Column to store imdb movie id
        static final String COLUMN_IMDB_ID = 'imdb_id'
        //Column to store movie production companies
        static final String COLUMN_PRODUCTION_COMPANIES = 'production_companies'
        //Column to store movie production conuntries
        static final String COLUMN_PRODUCTION_COUNTRIES = 'production_countries'
        //Column to store movie revenue
        static final String COLUMN_REVENUE = 'revenue'
        //Column to store movie runtime
        static final String COLUMN_RUNTIME = 'runtime'
        //Column to store movie release status
        static final String COLUMN_RELEASE_STATUS = 'release_status'
        //Column to store movie tagline
        static final String COLUMN_TAGLINE = 'tagline'
        //Column to store rating - applicable for only Tmdb user rated movies
        static final String COLUMN_TMDB_USER_RATED_RATING = 'tmdb_user_rated_rating'

        //Column to store record creation date
        static final String COLUMN_CREATE_TIMESTAMP = 'create_timestamp'
        //Column to store record update date
        static final String COLUMN_UPDATE_TIMESTAMP = 'update_timestamp'

        //Column to store export flag - future use
        static final String COLUMN_USER_EXPORTED = 'user_export_flag'
        //Future columns - String
        static final String COLUMN_FUTURE_USE_1 = 'movie_column_future_use_1'
        static final String COLUMN_FUTURE_USE_2 = 'movie_column_future_use_2'
        static final String COLUMN_FUTURE_USE_3 = 'movie_column_future_use_3'
        //Future columns - Integer
        static final String COLUMN_FUTURE_USE_4 = 'movie_column_future_use_4'
        static final String COLUMN_FUTURE_USE_5 = 'movie_column_future_use_5'

        //Uri for movie_basic_info table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_BASIC_INFO).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_BASIC_INFO"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_BASIC_INFO"

        static Uri buildMovieUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMovieUriWithMovieId(final int movieId) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).build()
        }

        static Uri buildMovieUriWithMovieCategory (final String movieCategory) {
            CONTENT_URI.buildUpon().appendPath(movieCategory).build()
        }

        static Uri buildMovieUriWithMovieCategoryAndCollectionId (final String movieCategory, final int collectionId) {
            CONTENT_URI.buildUpon().appendPath(movieCategory)
                    .appendPath(Integer.toString(collectionId)).build()
        }

        static long getRowIdFromUri (final Uri uri) {
            uri.getPathSegments().get(1).toLong()
        }

        static int getMovieIdFromUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }

        static String getMovieCategoryFromMovieUri (final Uri uri) {
            uri.getPathSegments().get(1)
        }

        static String getMovieCategoryFromMovieAndCollectionIdUri (final Uri uri) {
            uri.getPathSegments().get(1)
        }

        static int getCollectionIdFromMovieAndCollectionIdUri (final Uri uri) {
            uri.getPathSegments().get(2).toInteger()
        }
    }

    /**
        Inner class that defines the table contents of the movie_cast table
     */

    public static final class MovieCast implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_CAST
        //Define the columns
        //Define a Foreign key. We do not use movie id as primary kye in movie_basic_info and due to the complexity of multiple tables
        //individual table will be accessed instead of INNER JOIN. The purpose of the Foreign key is to maintain data integrity. Hence
        //use the _id field of movie_basic_info as Foreign key but a separate field will be used for movie id for reference.
        //Foreign key can be used but to avoid INNER JOIN as it will not serve the purpose because of multiple tables
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        //ORIG_MOVIE_ID column is the reference(i.e. dummy Foreign key) and populated with the movie id of movie_basic_info
        static final String COLUMN_CAST_ORIG_MOVIE_ID = 'cast_orig_movie_id'
        static final String COLUMN_CAST_ID = 'cast_id'
        static final String COLUMN_CAST_CHARACTER = 'cast_character'
        static final String COLUMN_CAST_CREDIT_ID = 'cast_credit_id'
        static final String COLUMN_CAST_PERSON_ID = 'cast_person_id'
        static final String COLUMN_CAST_PERSON_NAME = 'cast_person_name'
        static final String COLUMN_CAST_ORDER = 'cast_order'
        static final String COLUMN_CAST_PROFILE_PATH = 'cast_profile_path'

        //Uri for movie_cast table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_CAST).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_CAST"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_CAST"

        static Uri buildMovieCastUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMovieCastUriWithMovieId (final int movieId) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).build()
        }

        static int getMovieIdFromMovieCastUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
        Inner class that defines the table contents of the movie_crew table
     */
    public static final class MovieCrew implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_CREW
        //Define the columns
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        //ORIG_MOVIE_ID column is the reference(i.e. dummy Foreign key) and populated with the movie id of movie_basic_info
        static final String COLUMN_CREW_ORIG_MOVIE_ID = 'crew_orig_movie_id'
        static final String COLUMN_CREW_CREDIT_ID = 'crew_credit_id'
        static final String COLUMN_CREW_DEPARTMENT = 'crew_department'
        static final String COLUMN_CREW_PERSON_ID = 'crew_person_id'
        static final String COLUMN_CREW_JOB = 'crew_job'
        static final String COLUMN_CREW_PERSON_NAME = 'crew_person_name'
        static final String COLUMN_CREW_PROFILE_PATH = 'crew_profile_path'

        //Uri for movie_crew table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_CREW).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_CREW"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_CREW"

        static Uri buildMovieCrewUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMovieCrewUriWithMovieId (final int movieId) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).build()
        }

        static int getMovieIdFromMovieCrewUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
        Inner class that defines the table contents of the movie_image table
    */
    public static final class MovieImage implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_IMAGE
        //Define the columns
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        //ORIG_MOVIE_ID column is the reference(i.e. dummy Foreign key) and populated with the movie id of movie_basic_info
        static final String COLUMN_IMAGE_ORIG_MOVIE_ID = 'image_orig_movie_id'
        static final String COLUMN_IMAGE_TYPE = 'image_type'
        static final String COLUMN_IMAGE_HEIGHT = 'image_height'
        static final String COLUMN_IMAGE_WIDTH = 'image_width'
        static final String COLUMN_IMAGE_FILE_PATH = 'image_file_path'

        //Uri for movie_image table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_IMAGE).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_IMAGE"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_IMAGE"

        static Uri buildMovieImageUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMovieImageUriWithMovieId (final int movieId) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).build()
        }

        static int getMovieIdFromMovieImageUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
        Inner class that defines the table contents of the movie_video table
    */
    public static final class MovieVideo implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_VIDEO
        //Define the columns
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        // ORIG_MOVIE_ID column is the reference(i.e. dummy Foreign key) and populated with the movie id of movie_basic_info
        static final String COLUMN_VIDEO_ORIG_MOVIE_ID = 'video_orig_movie_id'
        static final String COLUMN_VIDEO_ID = 'video_id'
        static final String COLUMN_VIDEO_KEY = 'video_key'
        static final String COLUMN_VIDEO_NAME = 'video_name'
        static final String COLUMN_VIDEO_SITE = 'video_site'
        static final String COLUMN_VIDEO_SIZE = 'video_size'
        static final String COLUMN_VIDEO_TYPE = 'video_type'
        static final String COLUMN_VIDEO_FOR_HOME_PAGE_USE_FLAG = 'video_for_home_page_use'

        //Uri for movie_video table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_VIDEO).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_VIDEO"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_VIDEO"

        static Uri buildMovieVideoUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMovieVideoUriWithMovieId (final int movieId) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).build()
        }

        static int getMovieIdFromMovieVideoUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
        Inner class that defines the table contents of the movie_review table
    */
    public static final class MovieReview implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_REVIEW
        //Define the columns
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        //ORIG_MOVIE_ID column is the reference(i.e. dummy Foreign key) and populated with the movie id of movie_basic_info
        static final String COLUMN_REVIEW_ORIG_MOVIE_ID = 'review_orig_movie_id'
        static final String COLUMN_REVIEW_ID = 'review_id'
        static final String COLUMN_REVIEW_AUTHOR = 'review_author'
        static final String COLUMN_REVIEW_CONTENT = 'review_content'
        static final String COLUMN_REVIEW_URL = 'review_url'

        //Uri for movie_review table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_REVIEW).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_REVIEW"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_REVIEW"

        static Uri buildMovieReviewUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMovieReviewUriWithMovieId (final int movieId) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).build()
        }

        static int getMovieIdFromMovieReviewUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
        Inner class that defines the table contents of the movie_release_date table
    */
    public static final class MovieReleaseDate implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_RELEASE_DATE
        //Define the columns
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        //ORIG_MOVIE_ID column is the reference(i.e. dummy Foreign key) and populated with the movie id of movie_basic_info
        static final String COLUMN_RELEASE_ORIG_MOVIE_ID = 'release_orig_movie_id'
        static final String COLUMN_RELEASE_ISO_COUNTRY = 'release_iso_country'
        static final String COLUMN_RELEASE_CERTIFICATION = 'release_certification'
        static final String COLUMN_RELEASE_ISO_LANGUAGE = 'release_iso_language'
        static final String COLUMN_RELEASE_NOTE = 'release_note'
        static final String COLUMN_RELEASE_DATE = 'release_date'
        static final String COLUMN_RELEASE_TYPE = 'release_type'

        //Uri for movie_release_date table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_RELEASE_DATE).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_RELEASE_DATE"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_RELEASE_DATE"

        static Uri buildMovieReleasewUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMovieReleaseUriWithMovieId (final int movieId) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).build()
        }

        static Uri buildMovieReleaseUriWithMovieIdAndCountryIso (final int movieId, final String countryIso) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).appendPath(countryIso).build()
        }

        static int getMovieIdFromMovieReleaseUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }

        static String getCountryIsoFromMovieReleaseUri (final Uri uri) {
            uri.getPathSegments().get(2)
        }
    }

    /**
        Inner class that defines the table contents of the movie_user_list_flag table
    */
    public static final class MovieUserListFlag implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_USER_LIST_FLAG
        //Define the columns
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        //ORIG_PERSON_ID column is the reference(i.e. dummy Foreign key) and populated with the movie id of movie_person_info
        static final String COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID = 'user_list_flag_orig_movie_id'
        //All flags are stored as integer ( 0- false & 1 - true)
        static final String COLUMN_USER_LIST_FLAG_WATCHED = 'user_list_flag_watched'
        static final String COLUMN_USER_LIST_FLAG_WISH_LIST = 'user_list_flag_wish_list'
        static final String COLUMN_USER_LIST_FLAG_FAVOURITE = 'user_list_flag_favourite'
        static final String COLUMN_USER_LIST_FLAG_COLLECTION = 'user_list_flag_collection'
        //Column to store user rating value (This is not a flag)
        static final String COLUMN_USER_LIST_USER_RATING = 'user_rating'
        //Future columns - String
        static final String COLUMN_FUTURE_USE_1 = 'movie_column_future_use_1'
        static final String COLUMN_FUTURE_USE_2 = 'movie_column_future_use_2'
        //Future columns - Integer
        static final String COLUMN_FUTURE_USE_3 = 'movie_column_future_use_3'
        static final String COLUMN_FUTURE_USE_4 = 'movie_column_future_use_4'


        //Uri for movie_user_list_flag table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_USER_LIST_FLAG).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_USER_LIST_FLAG"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_USER_LIST_FLAG"

        static Uri buildMovieUserListFlagUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMovieUserListFlagUriWithMovieId (final int movieId) {
            CONTENT_URI.buildUpon().appendPath(movieId.toString()).build()
        }

        static int getMovieIdFromMovieUserListFlagUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
        Inner class that defines the table contents of the movie_person_info table
    */
    public static final class MoviePersonInfo implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_PERSON_INFO
        //Define the columns
        static final String COLUMN_PERSON_ADULT_FLAG = 'person_adult_flag'
        static final String COLUMN_PERSON_ALSO_KNOWN_AS = 'person_also_known_as'
        static final String COLUMN_PERSON_BIOGRAPHY = 'person_biography'
        static final String COLUMN_PERSON_BIRTHDAY = 'person_birthday'
        static final String COLUMN_PERSON_DEATHDAY = 'person_deathday'
        static final String COLUMN_PERSON_HOMEPAGE = 'person_homepage'
        static final String COLUMN_PERSON_ID = 'person_id'
        static final String COLUMN_PERSON_NAME = 'person_name'
        static final String COLUMN_PERSON_PLACE_OF_BIRTH = 'person_place_of_birth'
        static final String COLUMN_PERSON_PROFILE_PATH = 'person_profile_path'
        static final String COLUMN_PERSON_IMDB_ID = 'person_imdb_id'
        static final String COLUMN_PERSON_POPULARITY = 'person_popularity'
        static final String COLUMN_PERSON_PRESENT_FLAG = 'person_info_present_flag'
        //Column to store record creation date
        static final String COLUMN_PERSON_CREATE_TIMESTAMP = 'create_timestamp'
        //Column to store record update date
        static final String COLUMN_PERSON_UPDATE_TIMESTAMP = 'update_timestamp'

        //Uri for movie_person_info table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_PERSON_INFO).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_PERSON_INFO"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_PERSON_INFO"

        static Uri buildMoviePersonInfoUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMoviePersonInfoUriWithPersonId (final int personId) {
            CONTENT_URI.buildUpon().appendPath(personId.toString()).build()
        }

        static long getRowIdFromMoviePersonInfoUri (final Uri uri) {
            uri.getPathSegments().get(1).toLong()
        }

        static int getPersonIdFromMoviePersonInfoUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
        Inner class that defines the table contents of the movie_person_cast table
    */
    public static final class MoviePersonCast implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_PERSON_CAST
        //Define the columns
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        //ORIG_PERSON_ID column is the reference(i.e. dummy Foreign key) and populated with the person id of movie_person_info
        static final String COLUMN_PERSON_CAST_ORIG_PERSON_ID = 'person_cast_orig_person_id'
        static final String COLUMN_PERSON_CAST_ADULT_FLAG = 'person_cast_adult_flag'
        static final String COLUMN_PERSON_CAST_CHARACTER = 'person_cast_character'
        static final String COLUMN_PERSON_CAST_CREDIT_ID = 'person_cast_credit_id'
        static final String COLUMN_PERSON_CAST_MOVIE_ID = 'person_cast_movie_id'
        static final String COLUMN_PERSON_CAST_ORIG_TITLE = 'person_cast_orig_title'
        static final String COLUMN_PERSON_CAST_POSTER_PATH = 'person_cast_poster_path'
        static final String COLUMN_PERSON_CAST_RELEASE_DATE = 'person_cast_release_date'
        static final String COLUMN_PERSON_CAST_TITLE = 'person_cast_title'

        //Uri for movie_person_cast table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_PERSON_CAST).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_PERSON_CAST"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_PERSON_CAST"

        static Uri buildMoviePersonCastUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMoviePersonCastUriWithPersonId (final int personId) {
            CONTENT_URI.buildUpon().appendPath(personId.toString()).build()
        }

        static int getPersonIdFromMoviePersonCastUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
        Inner class that defines the table contents of the movie_person_crew table
    */
    public static final class MoviePersonCrew implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_PERSON_CREW
        //Define the columns
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        //ORIG_PERSON_ID column is the reference(i.e. dummy Foreign key) and populated with the person id of movie_person_info
        static final String COLUMN_PERSON_CREW_ORIG_PERSON_ID = 'person_crew_orig_person_id'
        static final String COLUMN_PERSON_CREW_ADULT_FLAG = 'person_crew_adult_flag'
        static final String COLUMN_PERSON_CREW_CREDIT_ID = 'person_crew_credit_id'
        static final String COLUMN_PERSON_CREW_DEPARTMENT = 'person_crew_department'
        static final String COLUMN_PERSON_CREW_MOVIE_ID = 'person_crew_movie_id'
        static final String COLUMN_PERSON_CREW_JOB = 'person_crew_job'
        static final String COLUMN_PERSON_CREW_ORIG_TITLE = 'person_crew_orig_title'
        static final String COLUMN_PERSON_CREW_POSTER_PATH = 'person_crew_poster_path'
        static final String COLUMN_PERSON_CREW_RELEASE_DATE = 'person_crew_release_date'
        static final String COLUMN_PERSON_CREW_TITLE = 'person_crew_title'

        //Uri for movie_person_crew table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_PERSON_CREW).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_PERSON_CREW"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_PERSON_CREW"

        static Uri buildMoviePersonCrewUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMoviePersonCrewUriWithPersonId (final int personId) {
            CONTENT_URI.buildUpon().appendPath(personId.toString()).build()
        }

        static int getPersonIdFromMoviePersonCrewUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
     Inner class that defines the table contents of the movie_person_image table
     */
    public static final class MoviePersonImage implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_PERSON_IMAGE
        //Define the columns
        static final String COLUMN_FOREIGN_KEY_ID = 'foreign_key'
        //ORIG_PERSON_ID column is the reference(i.e. dummy Foreign key) and populated with the person id of movie_person_info
        static final String COLUMN_PERSON_IMAGE_ORIG_PERSON_ID = 'person_image_orig_person_id'
        static final String COLUMN_PERSON_IMAGE_ASPECT_RATIO = 'person_image_aspect_ratio'
        static final String COLUMN_PERSON_IMAGE_FILE_PATH = 'person_image_file_path'
        static final String COLUMN_PERSON_IMAGE_HEIGHT = 'person_image_height'
        static final String COLUMN_PERSON_IMAGE_ISO_639_1 = 'person_image_iso_639_1'
        static final String COLUMN_PERSON_IMAGE_VOTE_AVERAGE = 'person_image_vote_average'
        static final String COLUMN_PERSON_IMAGE_VOTE_COUNT = 'person_image_vote_count'
        static final String COLUMN_PERSON_IMAGE_WIDTH = 'person_image_width'


        //Uri for movie_person_crew table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_PERSON_IMAGE).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_PERSON_IMAGE"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_PERSON_IMAGE"

        static Uri buildMoviePersonImageUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMoviePersonImageUriWithPersonId (final int personId) {
            CONTENT_URI.buildUpon().appendPath(personId.toString()).build()
        }

        static int getPersonIdFromMoviePersonImageUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }

    /**
     Inner class that defines the table contents of the movie_collection table
     */
    public static final class MovieCollection implements BaseColumns {
        static final String TABLE_NAME = PATH_MOVIE_COLLECTION
        //Define the columns
        static final String COLUMN_COLLECTION_ID = 'collection_id'
        static final String COLUMN_COLLECTION_NAME = 'collection_name'
        static final String COLUMN_COLLECTION_OVERVIEW = 'collection_overview'
        static final String COLUMN_COLLECTION_POSTER_PATH = 'collection_poster_path'
        static final String COLUMN_COLLECTION_BACKDROP_PATH = 'collection_backdrop_path'
        static final String COLUMN_COLLECTION_MOVIE_PRESENT_FLAG = 'collection_movie_present_flag'
        //Column to store record creation date
        static final String COLUMN_COLLECTION_CREATE_TIMESTAMP = 'create_timestamp'
        //Column to store record update date
        static final String COLUMN_COLLECTION_UPDATE_TIMESTAMP = 'update_timestamp'

        //Uri for movie_collection table
        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_COLLECTION).build()

        static final String CONTENT_TYPE =
                "$ContentResolver.CURSOR_DIR_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_COLLECTION"
        static final String CONTENT_ITEM_TYPE =
                "$ContentResolver.CURSOR_ITEM_BASE_TYPE/$CONTENT_AUTHORITY/$PATH_MOVIE_COLLECTION"

        static Uri buildMovieCollectionUri(final long id) {
            ContentUris.withAppendedId(CONTENT_URI, id)
        }

        static Uri buildMovieCollectionUriWithCollectionId (final int collectionId) {
            CONTENT_URI.buildUpon().appendPath(collectionId.toString()).build()
        }

        static long getCollectionRpwIdFromMovieCollectionUri (final Uri uri) {
            uri.getPathSegments().get(1).toLong()
        }

        static int getCollectionIdFromMovieCollectionUri (final Uri uri) {
            uri.getPathSegments().get(1).toInteger()
        }
    }
}