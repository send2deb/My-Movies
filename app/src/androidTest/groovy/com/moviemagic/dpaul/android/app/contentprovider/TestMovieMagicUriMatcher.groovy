/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.contentprovider

import android.content.UriMatcher
import android.net.Uri
import android.test.AndroidTestCase
import groovy.transform.CompileStatic

@CompileStatic
class TestMovieMagicUriMatcher extends AndroidTestCase {
    private static final int TEST_MOVIE_ID = 123
    private static final int TEST_COLL_ID = 456
    private static final int TEST_PERSON_ID = 789
    private static final String TEST_MOVIE_CATEGORY = 'popular'
    private static final String TEST_ISO_CNTRY = 'US'
    private static final long TEST_RELEASE_DATE = 1471042800385L //2016-08-13

    // content://com.moviemagic.dpaul.android.app/<table name>"
    private static final Uri TEST_MOVIE_BASIC_INFO_DIR = MovieMagicContract.MovieBasicInfo.CONTENT_URI
    private static final Uri TEST_MOVIE_BASIC_INFO_WITH_ID_ITEM = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieId(TEST_MOVIE_ID)
    private static final Uri TEST_MOVIE_BASIC_INFO_WITH_CATEGORY_DIR = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategory(TEST_MOVIE_CATEGORY)
    private static final Uri TEST_MOVIE_BASIC_INFO_WITH_CATEGORY_AND_COLL_ID_ITEM = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategoryAndCollectionId(TEST_MOVIE_CATEGORY,TEST_COLL_ID)
    private static final Uri TEST_MOVIE_CAST_DIR = MovieMagicContract.MovieCast.CONTENT_URI
    private static final Uri TEST_MOVIE_CAST_WITH_ID_DIR = MovieMagicContract.MovieCast.buildMovieCastUriWithMovieId(TEST_MOVIE_ID)
    private static final Uri TEST_MOVIE_CREW_DIR = MovieMagicContract.MovieCrew.CONTENT_URI
    private static final Uri TEST_MOVIE_CREW_WITH_ID_DIR = MovieMagicContract.MovieCrew.buildMovieCrewUriWithMovieId(TEST_MOVIE_ID)
    private static final Uri TEST_MOVIE_IMAGE_DIR = MovieMagicContract.MovieImage.CONTENT_URI
    private static final Uri TEST_MOVIE_IMAGE_WITH_ID_DIR = MovieMagicContract.MovieImage.buildMovieImageUriWithMovieId(TEST_MOVIE_ID)
    private static final Uri TEST_MOVIE_VIDEO_DIR = MovieMagicContract.MovieVideo.CONTENT_URI
    private static final Uri TEST_MOVIE_VIDEO_WITH_ID_DIR = MovieMagicContract.MovieVideo.buildMovieVideoUriWithMovieId(TEST_MOVIE_ID)
    private static final Uri TEST_MOVIE_REVIEW_DIR = MovieMagicContract.MovieReview.CONTENT_URI
    private static final Uri TEST_MOVIE_REVIEW_WITH_ID_DIR = MovieMagicContract.MovieReview.buildMovieReviewUriWithMovieId(TEST_MOVIE_ID)
    private static final Uri TEST_MOVIE_RELEASE_DIR = MovieMagicContract.MovieReleaseDate.CONTENT_URI
    private static final Uri TEST_MOVIE_RELEASE_WITH_ID_DIR = MovieMagicContract.MovieReleaseDate.buildMovieReleaseUriWithMovieId(TEST_MOVIE_ID)
    private static final Uri TEST_MOVIE_RELEASE_WITH_ID_AND_ISO_DIR = MovieMagicContract.MovieReleaseDate.buildMovieReleaseUriWithMovieIdAndCountryIso(TEST_MOVIE_ID,TEST_ISO_CNTRY)
    private static final Uri TEST_MOVIE_USER_LIST_FLAG_DIR = MovieMagicContract.MovieUserListFlag.CONTENT_URI
    private static final Uri TEST_MOVIE_USER_LIST_FLAG_WITH_ID_DIR = MovieMagicContract.MovieUserListFlag.buildMovieUserListFlagUriWithMovieId(TEST_MOVIE_ID)
    private static final Uri TEST_MOVIE_PERSON_INFO_DIR = MovieMagicContract.MoviePersonInfo.CONTENT_URI
    private static final Uri TEST_MOVIE_PERSON_INFO_WITH_ID_ITEM = MovieMagicContract.MoviePersonInfo.buildMoviePersonInfoUriWithPersonId(TEST_PERSON_ID)
    private static final Uri TEST_MOVIE_PERSON_CAST_DIR = MovieMagicContract.MoviePersonCast.CONTENT_URI
    private static final Uri TEST_MOVIE_PERSON_CAST_WITH_ID_DIR = MovieMagicContract.MoviePersonCast.buildMoviePersonCastUriWithPersonId(TEST_PERSON_ID)
    private static final Uri TEST_MOVIE_PERSON_CREW_DIR = MovieMagicContract.MoviePersonCrew.CONTENT_URI
    private static final Uri TEST_MOVIE_PERSON_CREW_WITH_ID_DIR = MovieMagicContract.MoviePersonCrew.buildMoviePersonCrewUriWithPersonId(TEST_PERSON_ID)
    private static final Uri TEST_MOVIE_PERSON_IMAGE_DIR = MovieMagicContract.MoviePersonImage.CONTENT_URI
    private static final Uri TEST_MOVIE_PERSON_IMAGE_WITH_ID_DIR = MovieMagicContract.MoviePersonImage.buildMoviePersonImageUriWithPersonId(TEST_PERSON_ID)
    private static final Uri TEST_MOVIE_COLL_DIR = MovieMagicContract.MovieCollection.CONTENT_URI
    private static final Uri TEST_MOVIE_COLL_WITH_ID_ITEM = MovieMagicContract.MovieCollection.buildMovieCollectionUriWithCollectionId(TEST_COLL_ID)

    /*
        Test that the UriMatcher returns the correct integer value
        for each of the Uri types that the ContentProvider can handle.
     */
    public void testUriMatcher() {
        final UriMatcher testMatcher = MovieMagicProvider.buildUriMatcher()

        assertEquals('Error: movie_basic_info matched incorrectly.',testMatcher.match(TEST_MOVIE_BASIC_INFO_DIR), MovieMagicProvider.MOVIE_BASIC_INFO)
        assertEquals('Error: movie_basic_info with movie id matched incorrectly.',testMatcher.match(TEST_MOVIE_BASIC_INFO_WITH_ID_ITEM), MovieMagicProvider.MOVIE_BASIC_INFO_WITH_MOVIE_ID)
        assertEquals('Error: movie_basic_info with category matched incorrectly.',testMatcher.match(TEST_MOVIE_BASIC_INFO_WITH_CATEGORY_DIR), MovieMagicProvider.MOVIE_BASIC_INFO_WITH_CATEGORY)
        assertEquals('Error: movie_basic_info with category and collection id matched incorrectly.',testMatcher.match(TEST_MOVIE_BASIC_INFO_WITH_CATEGORY_AND_COLL_ID_ITEM), MovieMagicProvider.MOVIE_BASIC_INFO_WITH_CATEGORY_AND_COLLECTION_ID)
        assertEquals('Error: movie_cast matched matched incorrectly.',testMatcher.match(TEST_MOVIE_CAST_DIR), MovieMagicProvider.MOVIE_CAST)
        assertEquals('Error: movie_cast with movie id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_CAST_WITH_ID_DIR), MovieMagicProvider.MOVIE_CAST_WITH_MOVIE_ID)
        assertEquals('Error: movie_crew matched matched incorrectly.',testMatcher.match(TEST_MOVIE_CREW_DIR), MovieMagicProvider.MOVIE_CREW)
        assertEquals('Error: movie_crew with movie id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_CREW_WITH_ID_DIR), MovieMagicProvider.MOVIE_CREW_WITH_MOVIE_ID)
        assertEquals('Error: movie_image matched matched incorrectly.',testMatcher.match(TEST_MOVIE_IMAGE_DIR), MovieMagicProvider.MOVIE_IMAGE)
        assertEquals('Error: movie_image with movie id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_IMAGE_WITH_ID_DIR), MovieMagicProvider.MOVIE_IMAGE_WITH_MOVIE_ID)
        assertEquals('Error: movie_video matched matched incorrectly.',testMatcher.match(TEST_MOVIE_VIDEO_DIR), MovieMagicProvider.MOVIE_VIDEO)
        assertEquals('Error: movie_video with movie id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_VIDEO_WITH_ID_DIR), MovieMagicProvider.MOVIE_VIDEO_WITH_MOVIE_ID)
        assertEquals('Error: movie_review matched matched incorrectly.',testMatcher.match(TEST_MOVIE_REVIEW_DIR), MovieMagicProvider.MOVIE_REVIEW)
        assertEquals('Error: movie_review with movie id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_REVIEW_WITH_ID_DIR), MovieMagicProvider.MOVIE_REVIEW_WITH_MOVIE_ID)
        assertEquals('Error: movie_release_date matched matched incorrectly.',testMatcher.match(TEST_MOVIE_RELEASE_DIR), MovieMagicProvider.MOVIE_RELEASE_DATE)
        assertEquals('Error: movie_release_date with movie id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_RELEASE_WITH_ID_DIR), MovieMagicProvider.MOVIE_RELEASE_DATE_WITH_MOVIE_ID)
        assertEquals('Error: movie_release_date with movie id and iso country matched matched incorrectly.',testMatcher.match(TEST_MOVIE_RELEASE_WITH_ID_AND_ISO_DIR), MovieMagicProvider.MOVIE_RELEASE_DATE_WITH_MOVIE_ID_AND_COUNTRY_ISO)
        assertEquals('Error: movie_user_list_flag matched matched incorrectly.',testMatcher.match(TEST_MOVIE_USER_LIST_FLAG_DIR), MovieMagicProvider.MOVIE_USER_LIST_FLAG)
        assertEquals('Error: movie_user_list_flag with movie id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_USER_LIST_FLAG_WITH_ID_DIR), MovieMagicProvider.MOVIE_USER_LIST_FLAG_WITH_MOVIE_ID)
        assertEquals('Error: movie_person_info matched matched incorrectly.',testMatcher.match(TEST_MOVIE_PERSON_INFO_DIR), MovieMagicProvider.MOVIE_PERSON_INFO)
        assertEquals('Error: movie_person_info with person id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_PERSON_INFO_WITH_ID_ITEM), MovieMagicProvider.MOVIE_PERSON_INFO_WITH_PERSON_ID)
        assertEquals('Error: movie_person_cast matched matched incorrectly.',testMatcher.match(TEST_MOVIE_PERSON_CAST_DIR), MovieMagicProvider.MOVIE_PERSON_CAST)
        assertEquals('Error: movie_person_cast with person id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_PERSON_CAST_WITH_ID_DIR), MovieMagicProvider.MOVIE_PERSON_CAST_WITH_PERSON_ID)
        assertEquals('Error: movie_person_crew matched matched incorrectly.',testMatcher.match(TEST_MOVIE_PERSON_CREW_DIR), MovieMagicProvider.MOVIE_PERSON_CREW)
        assertEquals('Error: movie_person_crew with person id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_PERSON_CREW_WITH_ID_DIR), MovieMagicProvider.MOVIE_PERSON_CREW_WITH_PERSON_ID)
        assertEquals('Error: movie_person_image matched matched incorrectly.',testMatcher.match(TEST_MOVIE_PERSON_IMAGE_DIR), MovieMagicProvider.MOVIE_PERSON_IMAGE)
        assertEquals('Error: movie_person_image with person id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_PERSON_IMAGE_WITH_ID_DIR), MovieMagicProvider.MOVIE_PERSON_IMAGE_WITH_PERSON_ID)
        assertEquals('Error: movie_collection matched matched incorrectly.',testMatcher.match(TEST_MOVIE_COLL_DIR), MovieMagicProvider.MOVIE_COLLECTION)
        assertEquals('Error: movie_collection with collection id matched matched incorrectly.',testMatcher.match(TEST_MOVIE_COLL_WITH_ID_ITEM), MovieMagicProvider.MOVIE_COLLECTION_WITH_COLECTION_ID)
    }
}