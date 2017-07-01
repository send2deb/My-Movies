/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import groovy.json.internal.ArrayUtils
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.ArrayUtil

@CompileStatic
class MovieMagicProvider extends ContentProvider {
    private static final String LOG_TAG = MovieMagicProvider.class.getSimpleName()

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher()
    private MovieMagicDbHelper mOpenHelper

    static final int MOVIE_BASIC_INFO = 101
    //To access a single item we shall use the primary key (_ID) of movie_basic_info
    static final int MOVIE_BASIC_INFO_WITH_MOVIE_ID = 102
    static final int MOVIE_BASIC_INFO_WITH_CATEGORY = 103
    static final int MOVIE_BASIC_INFO_WITH_CATEGORY_AND_COLLECTION_ID = 104
    static final int MOVIE_CAST = 105
    static final int MOVIE_CAST_WITH_MOVIE_ID = 106
    static final int MOVIE_CREW = 107
    static final int MOVIE_CREW_WITH_MOVIE_ID = 108
    static final int MOVIE_IMAGE = 109
    static final int MOVIE_IMAGE_WITH_MOVIE_ID = 110
    static final int MOVIE_VIDEO = 111
    static final int MOVIE_VIDEO_WITH_MOVIE_ID = 112
    static final int MOVIE_REVIEW = 113
    static final int MOVIE_REVIEW_WITH_MOVIE_ID = 114
    static final int MOVIE_RELEASE_DATE = 115
    static final int MOVIE_RELEASE_DATE_WITH_MOVIE_ID = 116
    static final int MOVIE_RELEASE_DATE_WITH_MOVIE_ID_AND_COUNTRY_ISO = 117
    static final int MOVIE_USER_LIST_FLAG = 118
    static final int MOVIE_USER_LIST_FLAG_WITH_MOVIE_ID = 119
    static final int MOVIE_PERSON_INFO = 120
    static final int MOVIE_PERSON_INFO_WITH_PERSON_ID = 121
    static final int MOVIE_PERSON_CAST = 122
    static final int MOVIE_PERSON_CAST_WITH_PERSON_ID = 123
    static final int MOVIE_PERSON_CREW = 124
    static final int MOVIE_PERSON_CREW_WITH_PERSON_ID = 125
    static final int MOVIE_PERSON_IMAGE = 126
    static final int MOVIE_PERSON_IMAGE_WITH_PERSON_ID = 127
    static final int MOVIE_COLLECTION = 128
    static final int MOVIE_COLLECTION_WITH_COLECTION_ID = 129

    private static final SQLiteQueryBuilder sMovieMagicQueryBuilder

    static {
        sMovieMagicQueryBuilder = new SQLiteQueryBuilder()
    }

    //movie_basic_info.movie_id = ?
    private static final String sMovieBasicInfoWithMovieIdSelection =
            "$MovieMagicContract.MovieBasicInfo.TABLE_NAME.$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? "

    //movie_basic_info.movie_category = ?
    private static final String sMovieBasicInfoWithCategorySelection =
            "$MovieMagicContract.MovieBasicInfo.TABLE_NAME.$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? "

    //movie_basic_info.movie_category = ?
    private static final String sMovieBasicInfoWithCategoryAndCollectionIdSelection =
            "$MovieMagicContract.MovieBasicInfo.TABLE_NAME.$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = ? " +
            " and $MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_ID = ?"

    //movie_cast.cast_orig_movie_id = ?
    private static final String sMovieCastWithMovieIdSelection =
            "$MovieMagicContract.MovieCast.TABLE_NAME.$MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID = ? "

    //movie_crew.crew_orig_movie_id = ?
    private static final String sMovieCrewWithMovieIdSelection =
            "$MovieMagicContract.MovieCrew.TABLE_NAME.$MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID = ? "

    //movie_image.image_orig_movie_id = ?
    private static final String sMovieImageWithMovieIdSelection =
            "$MovieMagicContract.MovieImage.TABLE_NAME.$MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID = ? "

    //movie_video.video_orig_movie_id = ?
    private static final String sMovieVideoWithMovieIdSelection =
            "$MovieMagicContract.MovieVideo.TABLE_NAME.$MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID = ? "

    //movie_review.review_orig_movie_id = ?
    private static final String sMovieReviewWithMovieIdSelection =
            "$MovieMagicContract.MovieReview.TABLE_NAME.$MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID = ? "

    //movie_release_date.release_orig_movie_id = ?
    private static final String sMovieReleaseWithMovieIdSelection =
            "$MovieMagicContract.MovieReleaseDate.TABLE_NAME.$MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID = ? "

    //movie_release_date.release_orig_movie_id = ? and release_iso_country = ?
    private static final String sMovieReleaseWithMovieIdAndCountryISOSelection =
            "$MovieMagicContract.MovieReleaseDate.TABLE_NAME.$MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID = ? " +
                    " and $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY = ?"

    //movie_user_list_flag.user_list_flag_orig_movie_id = ?
    private static final String sMovieUserListFlagWithMovieIdSelection =
            "$MovieMagicContract.MovieUserListFlag.TABLE_NAME.$MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID = ? "

    //movie_person_info.person_id = ?
    private static final String sMoviePersonInfoWithPersonIdSelection =
            "$MovieMagicContract.MoviePersonInfo.TABLE_NAME.$MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID = ? "

    //movie_person_cast.person_cast_orig_person_id = ?
    private static final String sMoviePersonCastWithPersonIdSelection =
            "$MovieMagicContract.MoviePersonCast.TABLE_NAME.$MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID = ? "

    //movie_person_crew.person_crew_orig_person_id = ?
    private static final String sMoviePersonCrewWithPersonIdSelection =
            "$MovieMagicContract.MoviePersonCrew.TABLE_NAME.$MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID = ? "

    //movie_person_image.person_crew_orig_person_id = ?
    private static final String sMoviePersonImageWithPersonIdSelection =
            "$MovieMagicContract.MoviePersonImage.TABLE_NAME.$MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID = ? "

    //movie_collection.collection_id = ?
    private static final String sMovieCollectionWithCollectionIdSelection =
            "$MovieMagicContract.MovieCollection.TABLE_NAME.$MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID = ? "


    //To get data from movie_basic_info where movie_basic_info._id = ?
    private Cursor getMovieBasicInfoByMovieId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieBasicInfo.TABLE_NAME")
        final String[] movieId = [Integer.toString(MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieBasicInfoWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_basic_info where movie_basic_info.movie_category = ? and also append passed on clause and arguments
    private Cursor getMovieBasicInfoByMovieCategory(
            final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieBasicInfo.TABLE_NAME")
        final String[] movieCategory = [MovieMagicContract.MovieBasicInfo.getMovieCategoryFromMovieUri(uri)]
        final String querySelection
        final String[] queryArguments
        if(selection) {
            querySelection = "$sMovieBasicInfoWithCategorySelection and $selection"
            queryArguments = movieCategory.plus(selectionArgs) // Groovy magic!!
        } else {
            querySelection = sMovieBasicInfoWithCategorySelection
            queryArguments = movieCategory
        }
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                querySelection,
                queryArguments,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_basic_info where movie_basic_info.movie_category = ? and movie_basic_info.collection_id = ?
    private Cursor getMovieBasicInfoByMovieCategoryAndCollectionId(
            final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieBasicInfo.TABLE_NAME")
        final String category = MovieMagicContract.MovieBasicInfo.getMovieCategoryFromMovieAndCollectionIdUri(uri)
        final String collectionID = Integer.toString(MovieMagicContract.MovieBasicInfo.getCollectionIdFromMovieAndCollectionIdUri(uri))
        final String[] selArgs = [category, collectionID]

        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieBasicInfoWithCategoryAndCollectionIdSelection,
                selArgs,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_cast where movie_cast.cast_orig_movie_id = ?
    private Cursor getMovieCastByMovieId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieCast.TABLE_NAME")
        final String[] movieId = [Integer.toString(MovieMagicContract.MovieCast.getMovieIdFromMovieCastUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieCastWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_crew where movie_crew.crew_orig_movie_id = ?
    private Cursor getMovieCrewByMovieId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieCrew.TABLE_NAME")
        final String[] movieId = [Integer.toString(MovieMagicContract.MovieCrew.getMovieIdFromMovieCrewUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieCrewWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_image where movie_image.image_orig_movie_id = ?
    private Cursor getMovieImageByMovieId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieImage.TABLE_NAME")
        final String[] movieId = [Integer.toString(MovieMagicContract.MovieImage.getMovieIdFromMovieImageUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieImageWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_video where movie_video.video_orig_movie_id = ?
    private Cursor getMovieVideoByMovieId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieVideo.TABLE_NAME")
        final String[] movieId = [Integer.toString(MovieMagicContract.MovieVideo.getMovieIdFromMovieVideoUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieVideoWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_review where movie_review.review_orig_movie_id = ?
    private Cursor getMovieReviewByMovieId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieReview.TABLE_NAME")
        final String[] movieId = [Integer.toString(MovieMagicContract.MovieReview.getMovieIdFromMovieReviewUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieReviewWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_release_date where movie_release_date.release_orig_movie_id = ?
    private Cursor getMovieReleaseByMovieId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieReleaseDate.TABLE_NAME")
        final String[] movieId = [Integer.toString(MovieMagicContract.MovieReleaseDate.getMovieIdFromMovieReleaseUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieReleaseWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_release_date where movie_release_date.release_orig_movie_id = ? and release_iso_country = ?
    private Cursor getMovieReleaseByMovieIdAndCountryISO(
            final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieReleaseDate.TABLE_NAME")
        final String movieId = Integer.toString(MovieMagicContract.MovieReleaseDate.getMovieIdFromMovieReleaseUri(uri))
        final String countryISO = MovieMagicContract.MovieReleaseDate.getCountryIsoFromMovieReleaseUri(uri)
        final String[] selArgs = [movieId,countryISO]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieReleaseWithMovieIdAndCountryISOSelection,
                selArgs,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_user_list_flag where movie_user_list_flag.user_list_flag_orig_movie_id = ?
    private Cursor getMovieUserListFlagByMovieId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieUserListFlag.TABLE_NAME")
        final String[] movieId = [Integer.toString(MovieMagicContract.MovieUserListFlag.getMovieIdFromMovieUserListFlagUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieUserListFlagWithMovieIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_person_info where movie_person_info.person_id = ?
    private Cursor getMoviePersonInfoByPersonId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MoviePersonInfo.TABLE_NAME")
        final String[] personId = [Integer.toString(MovieMagicContract.MoviePersonInfo.getPersonIdFromMoviePersonInfoUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMoviePersonInfoWithPersonIdSelection,
                personId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_person_cast where movie_person_cast.person_cast_orig_person_id = ?
    private Cursor getMoviePersonCastByPersonId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MoviePersonCast.TABLE_NAME")
        final String[] personId = [Integer.toString(MovieMagicContract.MoviePersonCast.getPersonIdFromMoviePersonCastUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMoviePersonCastWithPersonIdSelection,
                personId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_person_crew where movie_person_crew.person_crew_orig_person_id = ?
    private Cursor getMoviePersonCrewByPersonId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MoviePersonCrew.TABLE_NAME")
        final String[] personId = [Integer.toString(MovieMagicContract.MoviePersonCrew.getPersonIdFromMoviePersonCrewUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMoviePersonCrewWithPersonIdSelection,
                personId,
                null,
                null,
                sortOrder
        )
    }


    //To get data from movie_person_image where movie_person_image.person_image_orig_person_id = ?
    private Cursor getMoviePersonImageByPersonId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MoviePersonImage.TABLE_NAME")
        final String[] personId = [Integer.toString(MovieMagicContract.MoviePersonImage.getPersonIdFromMoviePersonImageUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMoviePersonImageWithPersonIdSelection,
                personId,
                null,
                null,
                sortOrder
        )
    }

    //To get data from movie_collection where movie_collection.collection_id = ?
    private Cursor getMovieCollectionByCollectionId(final Uri uri, final String[] projection, final String sortOrder) {
        sMovieMagicQueryBuilder.setTables("$MovieMagicContract.MovieCollection.TABLE_NAME")
        final String[] movieId = [Integer.toString(MovieMagicContract.MovieCollection.getCollectionIdFromMovieCollectionUri(uri))]
        return sMovieMagicQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieCollectionWithCollectionIdSelection,
                movieId,
                null,
                null,
                sortOrder
        )
    }

    /**
       The UriMatcher will contain the URI for all the URIs used for different tables and will be returned when matched
    */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH)

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieMagicContract to help define the types to the UriMatcher.
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_BASIC_INFO,MOVIE_BASIC_INFO)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_BASIC_INFO/#",MOVIE_BASIC_INFO_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_BASIC_INFO/*",MOVIE_BASIC_INFO_WITH_CATEGORY)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_BASIC_INFO/*/#",MOVIE_BASIC_INFO_WITH_CATEGORY_AND_COLLECTION_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_CAST,MOVIE_CAST)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_CAST/#",MOVIE_CAST_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_CREW,MOVIE_CREW)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_CREW/#",MOVIE_CREW_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_IMAGE,MOVIE_IMAGE)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_IMAGE/#",MOVIE_IMAGE_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_VIDEO,MOVIE_VIDEO)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_VIDEO/#",MOVIE_VIDEO_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_REVIEW,MOVIE_REVIEW)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_REVIEW/#",MOVIE_REVIEW_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_RELEASE_DATE,MOVIE_RELEASE_DATE)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_RELEASE_DATE/#",MOVIE_RELEASE_DATE_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_RELEASE_DATE/#/*",MOVIE_RELEASE_DATE_WITH_MOVIE_ID_AND_COUNTRY_ISO)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_USER_LIST_FLAG,MOVIE_USER_LIST_FLAG)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_USER_LIST_FLAG/#",MOVIE_USER_LIST_FLAG_WITH_MOVIE_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_PERSON_INFO,MOVIE_PERSON_INFO)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_PERSON_INFO/#",MOVIE_PERSON_INFO_WITH_PERSON_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_PERSON_CAST,MOVIE_PERSON_CAST)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_PERSON_CAST/#",MOVIE_PERSON_CAST_WITH_PERSON_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_PERSON_CREW,MOVIE_PERSON_CREW)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_PERSON_CREW/#",MOVIE_PERSON_CREW_WITH_PERSON_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_PERSON_IMAGE,MOVIE_PERSON_IMAGE)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_PERSON_IMAGE/#",MOVIE_PERSON_IMAGE_WITH_PERSON_ID)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY, MovieMagicContract.PATH_MOVIE_COLLECTION,MOVIE_COLLECTION)
        uriMatcher.addURI(MovieMagicContract.CONTENT_AUTHORITY,"$MovieMagicContract.PATH_MOVIE_COLLECTION/#",MOVIE_COLLECTION_WITH_COLECTION_ID)
        // 3) Return the new matcher!
        return uriMatcher
    }

    @Override
    boolean onCreate() {
        mOpenHelper = new MovieMagicDbHelper(getContext())
        return true
    }

    /**
     The getType method will return the URI type based on match result
     */
    @Override
    String getType(final Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri)

        switch (match) {
            case MOVIE_BASIC_INFO:
                return MovieMagicContract.MovieBasicInfo.CONTENT_TYPE
            case MOVIE_BASIC_INFO_WITH_MOVIE_ID:
                return MovieMagicContract.MovieBasicInfo.CONTENT_ITEM_TYPE
            case MOVIE_BASIC_INFO_WITH_CATEGORY:
                return MovieMagicContract.MovieBasicInfo.CONTENT_TYPE
            case MOVIE_BASIC_INFO_WITH_CATEGORY_AND_COLLECTION_ID:
                return MovieMagicContract.MovieBasicInfo.CONTENT_ITEM_TYPE
            case MOVIE_CAST:
                return MovieMagicContract.MovieCast.CONTENT_TYPE
            case MOVIE_CAST_WITH_MOVIE_ID:
                return MovieMagicContract.MovieCast.CONTENT_TYPE
            case MOVIE_CREW:
                return MovieMagicContract.MovieCrew.CONTENT_TYPE
            case MOVIE_CREW_WITH_MOVIE_ID:
                return MovieMagicContract.MovieCrew.CONTENT_TYPE
            case MOVIE_IMAGE:
                return MovieMagicContract.MovieImage.CONTENT_TYPE
            case MOVIE_IMAGE_WITH_MOVIE_ID:
                return MovieMagicContract.MovieImage.CONTENT_TYPE
            case MOVIE_VIDEO:
                return MovieMagicContract.MovieVideo.CONTENT_TYPE
            case MOVIE_VIDEO_WITH_MOVIE_ID:
                return MovieMagicContract.MovieVideo.CONTENT_TYPE
            case MOVIE_REVIEW:
                return MovieMagicContract.MovieReview.CONTENT_TYPE
            case MOVIE_REVIEW_WITH_MOVIE_ID:
                return MovieMagicContract.MovieReview.CONTENT_TYPE
            case MOVIE_RELEASE_DATE:
                return MovieMagicContract.MovieReleaseDate.CONTENT_TYPE
            case MOVIE_RELEASE_DATE_WITH_MOVIE_ID:
                return MovieMagicContract.MovieReleaseDate.CONTENT_TYPE
            case MOVIE_RELEASE_DATE_WITH_MOVIE_ID_AND_COUNTRY_ISO:
                return MovieMagicContract.MovieReleaseDate.CONTENT_TYPE
            case MOVIE_USER_LIST_FLAG:
                return MovieMagicContract.MovieUserListFlag.CONTENT_TYPE
            case MOVIE_USER_LIST_FLAG_WITH_MOVIE_ID:
                return MovieMagicContract.MovieUserListFlag.CONTENT_ITEM_TYPE
            case MOVIE_PERSON_INFO:
                return MovieMagicContract.MoviePersonInfo.CONTENT_TYPE
            case MOVIE_PERSON_INFO_WITH_PERSON_ID:
                return MovieMagicContract.MoviePersonInfo.CONTENT_ITEM_TYPE
            case MOVIE_PERSON_CAST:
                return MovieMagicContract.MoviePersonCast.CONTENT_TYPE
            case MOVIE_PERSON_CAST_WITH_PERSON_ID:
                return MovieMagicContract.MoviePersonCast.CONTENT_TYPE
            case MOVIE_PERSON_CREW:
                return MovieMagicContract.MoviePersonCrew.CONTENT_TYPE
            case MOVIE_PERSON_CREW_WITH_PERSON_ID:
                return MovieMagicContract.MoviePersonCrew.CONTENT_TYPE
            case MOVIE_PERSON_IMAGE:
                return MovieMagicContract.MoviePersonImage.CONTENT_TYPE
            case MOVIE_PERSON_IMAGE_WITH_PERSON_ID:
                return MovieMagicContract.MoviePersonImage.CONTENT_TYPE
            case MOVIE_COLLECTION:
                return MovieMagicContract.MovieCollection.CONTENT_TYPE
            case MOVIE_COLLECTION_WITH_COLECTION_ID:
                return MovieMagicContract.MovieCollection.CONTENT_ITEM_TYPE
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    /**
     The query method of the provider returns the query based on URI passed in the query request
     */
    @Override
    Cursor query(
            final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        final Cursor retCursor
        switch(sUriMatcher.match(uri)) {
        // "/movie_basic_info/#"
            case MOVIE_BASIC_INFO_WITH_MOVIE_ID:
                retCursor = getMovieBasicInfoByMovieId(uri, projection, sortOrder)
                break
        // "/movie_basic_info/*"
            case MOVIE_BASIC_INFO_WITH_CATEGORY:
                retCursor = getMovieBasicInfoByMovieCategory(uri, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_basic_info/#/*"
            case MOVIE_BASIC_INFO_WITH_CATEGORY_AND_COLLECTION_ID:
                retCursor = getMovieBasicInfoByMovieCategoryAndCollectionId(uri, projection, sortOrder)
                break
        // "/movie_basic_info"
            case MOVIE_BASIC_INFO:
                final String table = MovieMagicContract.MovieBasicInfo.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_cast/#"
            case MOVIE_CAST_WITH_MOVIE_ID:
                retCursor = getMovieCastByMovieId(uri,projection,sortOrder)
                break
        // "/movie_cast"
            case MOVIE_CAST:
                final String table = MovieMagicContract.MovieCast.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_crew/#"
            case MOVIE_CREW_WITH_MOVIE_ID:
                retCursor = getMovieCrewByMovieId(uri,projection,sortOrder)
                break
        // "/movie_crew"
            case MOVIE_CREW:
                final String table = MovieMagicContract.MovieCrew.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_image/#"
            case MOVIE_IMAGE_WITH_MOVIE_ID:
                retCursor = getMovieImageByMovieId(uri,projection,sortOrder)
                break
        // "/movie_image"
            case MOVIE_IMAGE:
                final String table = MovieMagicContract.MovieImage.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_video/#"
            case MOVIE_VIDEO_WITH_MOVIE_ID:
                retCursor = getMovieVideoByMovieId(uri,projection,sortOrder)
                break
        // "/movie_video"
            case MOVIE_VIDEO:
                final String table = MovieMagicContract.MovieVideo.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_review/#"
            case MOVIE_REVIEW_WITH_MOVIE_ID:
                retCursor = getMovieReviewByMovieId(uri,projection,sortOrder)
                break
        // "/movie_review"
            case MOVIE_REVIEW:
                final String table = MovieMagicContract.MovieReview.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_release_date/#"
            case MOVIE_RELEASE_DATE_WITH_MOVIE_ID:
                retCursor = getMovieReleaseByMovieId(uri,projection,sortOrder)
                break
        // "/movie_release_date/#/*"
            case MOVIE_RELEASE_DATE_WITH_MOVIE_ID_AND_COUNTRY_ISO:
                retCursor = getMovieReleaseByMovieIdAndCountryISO(uri,projection,sortOrder)
                break
        // "/movie_release_date"
            case MOVIE_RELEASE_DATE:
                final String table = MovieMagicContract.MovieReleaseDate.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_user_list_flag/#"
            case MOVIE_USER_LIST_FLAG_WITH_MOVIE_ID:
                retCursor = getMovieUserListFlagByMovieId(uri,projection,sortOrder)
                break
        // "/movie_user_list_flag"
            case MOVIE_USER_LIST_FLAG:
                final String table = MovieMagicContract.MovieUserListFlag.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_person_info/#"
            case MOVIE_PERSON_INFO_WITH_PERSON_ID:
                retCursor = getMoviePersonInfoByPersonId(uri,projection,sortOrder)
                break
        // "/movie_person_info"
            case MOVIE_PERSON_INFO:
                final String table = MovieMagicContract.MoviePersonInfo.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_person_cast/#"
            case MOVIE_PERSON_CAST_WITH_PERSON_ID:
                retCursor = getMoviePersonCastByPersonId(uri,projection,sortOrder)
                break
        // "/movie_person_cast"
            case MOVIE_PERSON_CAST:
                final String table = MovieMagicContract.MoviePersonCast.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_person_crew/#"
            case MOVIE_PERSON_CREW_WITH_PERSON_ID:
                retCursor = getMoviePersonCrewByPersonId(uri,projection,sortOrder)
                break
        // "/movie_person_crew"
            case MOVIE_PERSON_CREW:
                final String table = MovieMagicContract.MoviePersonCrew.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_person_image/#"
            case MOVIE_PERSON_IMAGE_WITH_PERSON_ID:
                retCursor = getMoviePersonImageByPersonId(uri,projection,sortOrder)
                break
        // "/movie_person_image"
            case MOVIE_PERSON_IMAGE:
                final String table = MovieMagicContract.MoviePersonImage.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
        // "/movie_collection/#"
            case MOVIE_COLLECTION_WITH_COLECTION_ID:
                retCursor = getMovieCollectionByCollectionId(uri,projection,sortOrder)
                break
        // "/movie_collection"
            case MOVIE_COLLECTION:
                final String table = MovieMagicContract.MovieCollection.TABLE_NAME
                retCursor = queryHelperMethod(table, projection, selection, selectionArgs, sortOrder)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri)
        return retCursor
    }

    /**
     The query helper method is used to handle the query for all the tables
     */
    private Cursor queryHelperMethod(
            final String table, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        //This is another way for writing the query ()
        return mOpenHelper.getReadableDatabase().query(table,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder)
    }

    /**
     The insert method of the provider inserts a single record to the table corresponding to the URI and returns uri (with row id) of the inserted record
     */
    @Override
    Uri insert(final Uri uri, final ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        final Uri returnUri

        switch (match) {
            case MOVIE_BASIC_INFO:
                convertDate(values)
                final long _id = db.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieBasicInfo.buildMovieUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_CAST:
                final long _id = db.insert(MovieMagicContract.MovieCast.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieCast.buildMovieCastUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_CREW:
                final long _id = db.insert(MovieMagicContract.MovieCrew.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieCrew.buildMovieCrewUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_IMAGE:
                final long _id = db.insert(MovieMagicContract.MovieImage.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieImage.buildMovieImageUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_VIDEO:
                final long _id = db.insert(MovieMagicContract.MovieVideo.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieVideo.buildMovieVideoUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_REVIEW:
                final long _id = db.insert(MovieMagicContract.MovieReview.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieReview.buildMovieReviewUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_RELEASE_DATE:
                final long _id = db.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieReleaseDate.buildMovieReleasewUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_USER_LIST_FLAG:
                final long _id = db.insert(MovieMagicContract.MovieUserListFlag.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieUserListFlag.buildMovieUserListFlagUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_PERSON_INFO:
                final long _id = db.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MoviePersonInfo.buildMoviePersonInfoUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_PERSON_CAST:
                final long _id = db.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MoviePersonCast.buildMoviePersonCastUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_PERSON_CREW:
                final long _id = db.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MoviePersonCrew.buildMoviePersonCrewUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_PERSON_IMAGE:
                final long _id = db.insert(MovieMagicContract.MoviePersonImage.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MoviePersonImage.buildMoviePersonImageUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            case MOVIE_COLLECTION:
                final long _id = db.insert(MovieMagicContract.MovieCollection.TABLE_NAME, null, values)
                if ( _id > 0 )
                    returnUri = MovieMagicContract.MovieCollection.buildMovieCollectionUri(_id)
                else
                    throw new android.database.SQLException("Failed to insert row into $uri")
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
        getContext().getContentResolver().notifyChange(uri, null)
        //Was facing issues while accessing database during inserting data into multiple tables in a single asynctask,
        //found in Stackoverflow that db.close shouldn't be used as content provider handles that
        //automatically, so commenting this here and in other places!!
        //db.close()
        return returnUri
    }

    /**
     The delete method of the provider deletes record(s) from the table corresponding to the URI and returns the delete count.
     If null is passed as selection criteria then all records are deleted
     */
    @Override
    int delete(final Uri uri, String selection, final String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        final int count
        //This makes delete all rows return the number of rows deleted
        if(selection == null) //noinspection GroovyAssignmentToMethodParameter
            selection = "1"
        switch (match) {
            case MOVIE_BASIC_INFO:
                count = db.delete(MovieMagicContract.MovieBasicInfo.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_CAST:
                count = db.delete(MovieMagicContract.MovieCast.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_CREW:
                count = db.delete(MovieMagicContract.MovieCrew.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_IMAGE:
                count = db.delete(MovieMagicContract.MovieImage.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_VIDEO:
                count = db.delete(MovieMagicContract.MovieVideo.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_REVIEW:
                count = db.delete(MovieMagicContract.MovieReview.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_RELEASE_DATE:
                count = db.delete(MovieMagicContract.MovieReleaseDate.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_USER_LIST_FLAG:
                count = db.delete(MovieMagicContract.MovieUserListFlag.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_PERSON_INFO:
                count = db.delete(MovieMagicContract.MoviePersonInfo.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_PERSON_CAST:
                count = db.delete(MovieMagicContract.MoviePersonCast.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_PERSON_CREW:
                count = db.delete(MovieMagicContract.MoviePersonCrew.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_PERSON_IMAGE:
                count = db.delete(MovieMagicContract.MoviePersonImage.TABLE_NAME, selection, selectionArgs)
                break
            case MOVIE_COLLECTION:
                count = db.delete(MovieMagicContract.MovieCollection.TABLE_NAME, selection, selectionArgs)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
        if (count !=0 ) {
            getContext().getContentResolver().notifyChange(uri, null)
            //db.close()
        }
        //Return the actual # of rows deleted
        return count
    }

    /**
     The update method of the provider updates record(s) to the table corresponding to the URI and returns update count
     */
    @Override
    int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        final int count
        switch (match) {
            case MOVIE_BASIC_INFO:
                convertDate(values)
                count = db.update(MovieMagicContract.MovieBasicInfo.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_CAST:
                count = db.update(MovieMagicContract.MovieCast.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_CREW:
                count = db.update(MovieMagicContract.MovieCrew.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_IMAGE:
                count = db.update(MovieMagicContract.MovieImage.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_VIDEO:
                count = db.update(MovieMagicContract.MovieVideo.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_REVIEW:
                count = db.update(MovieMagicContract.MovieReview.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_RELEASE_DATE:
                count = db.update(MovieMagicContract.MovieReleaseDate.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_USER_LIST_FLAG:
                count = db.update(MovieMagicContract.MovieUserListFlag.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_PERSON_INFO:
                count = db.update(MovieMagicContract.MoviePersonInfo.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_PERSON_CAST:
                count = db.update(MovieMagicContract.MoviePersonCast.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_PERSON_CREW:
                count = db.update(MovieMagicContract.MoviePersonCrew.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_PERSON_IMAGE:
                count = db.update(MovieMagicContract.MoviePersonImage.TABLE_NAME,values,selection,selectionArgs)
                break
            case MOVIE_COLLECTION:
                count = db.update(MovieMagicContract.MovieCollection.TABLE_NAME,values,selection,selectionArgs)
                break
            default:
                throw new UnsupportedOperationException("Unknown uri: $uri")
        }
        if (count !=0 ) {
            getContext().getContentResolver().notifyChange(uri, null)
            //db.close()
        }
        return count
    }

    /**
     The bulkInsert method of the provider inserts record in bulk to the table corresponding to the URI and returns insert count
     */
    @Override
    int bulkInsert(final Uri uri, final ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase()
        final int match = sUriMatcher.match(uri)
        switch (match) {
            case MOVIE_BASIC_INFO:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        convertDate(value)
                        long _id = db.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_CAST:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieCast.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_CREW:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieCrew.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_IMAGE:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieImage.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_VIDEO:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieVideo.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_REVIEW:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieReview.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_RELEASE_DATE:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_USER_LIST_FLAG:
                throw new UnsupportedOperationException("Bulk insert not supported for: $uri")
            case MOVIE_PERSON_INFO:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_PERSON_CAST:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_PERSON_CREW:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_PERSON_IMAGE:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MoviePersonImage.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            case MOVIE_COLLECTION:
                db.beginTransaction()
                int returnCount = 0
                try {
                    for (final ContentValues value : values) {
                        long _id = db.insert(MovieMagicContract.MovieCollection.TABLE_NAME, null, value)
                        if (_id != -1) {
                            returnCount++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                getContext().getContentResolver().notifyChange(uri, null)
                return returnCount
            default:
                return super.bulkInsert(uri, values)
        }
    }

    /**
     * Convert the movie release date string to numeric value (milliseconds)
     * @param values The date value to be converted to
     */

    private static void convertDate(final ContentValues values) {
        // Covert the movie release date
        if (values.containsKey(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE)) {
            final String movieReleaseDate = values.getAsString(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE)
            values.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE, MovieMagicContract.convertMovieReleaseDate(movieReleaseDate))
        }
    }
}