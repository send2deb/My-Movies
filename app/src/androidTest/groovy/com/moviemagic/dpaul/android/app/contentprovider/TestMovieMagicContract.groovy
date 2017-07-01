/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.contentprovider

import android.net.Uri
import android.test.AndroidTestCase
import groovy.transform.CompileStatic

@CompileStatic
class TestMovieMagicContract extends AndroidTestCase {

    public void testBuildMovieMagicContract() {
        final String TEST_BASE_URI = 'content://com.moviemagic.dpaul.android.app/movie_basic_info'
        final String TEST_MOVIE_CAST_URI = 'content://com.moviemagic.dpaul.android.app/movie_cast'
        final String TEST_MOVIE_CREW_URI = 'content://com.moviemagic.dpaul.android.app/movie_crew'
        final String TEST_MOVIE_IMAGE_URI = 'content://com.moviemagic.dpaul.android.app/movie_image'
        final String TEST_MOVIE_VIDEO_URI = 'content://com.moviemagic.dpaul.android.app/movie_video'
        final String TEST_MOVIE_REVIEW_URI = 'content://com.moviemagic.dpaul.android.app/movie_review'
        final String TEST_MOVIE_RELEASE_URI = 'content://com.moviemagic.dpaul.android.app/movie_release_date'
        final String TEST_MOVIE_COLLECTION_URI = 'content://com.moviemagic.dpaul.android.app/movie_collection'
        final String TEST_MOVIE_PERSON_URI = 'content://com.moviemagic.dpaul.android.app/movie_person_info'
        final String TEST_MOVIE_PERSON_CAST_URI = 'content://com.moviemagic.dpaul.android.app/movie_person_cast'
        final String TEST_MOVIE_PERSON_CREW_URI = 'content://com.moviemagic.dpaul.android.app/movie_person_crew'
        final String TEST_MOVIE_PERSON_IMAGE_URI = 'content://com.moviemagic.dpaul.android.app/movie_person_image'
        final String TEST_MOVIE_USER_LIST_FLAG_URI = 'content://com.moviemagic.dpaul.android.app/movie_user_list_flag'
        final long TEST_MOVIE_TABLE_ID = 101
        final long TEST_MOVIE_TABLE_CAST_ID = 102
        final long TEST_MOVIE_TABLE_CREW_ID = 103
        final long TEST_MOVIE_TABLE_IMAGE_ID = 104
        final long TEST_MOVIE_TABLE_VIDEO_ID = 105
        final long TEST_MOVIE_TABLE_REVIEW_ID = 106
        final long TEST_MOVIE_TABLE_RELEASE_ID = 107
        final long TEST_MOVIE_TABLE_COLLECTION_ID = 108
        final long TEST_MOVIE_TABLE_PERSON_ID = 109
        final long TEST_MOVIE_TABLE_PERSON_CAST_ID = 110
        final long TEST_MOVIE_TABLE_PERSON_CREW_ID = 111
        final long TEST_MOVIE_TABLE_PERSON_IMAGE_ID = 112
        final long TEST_MOVIE_TABLE_USER_LIST_FLAG_ID = 113
        final int TEST_MOVIE_ID = 1001
        final int TEST_MOVIE_CAST_ID = 1002
        final int TEST_MOVIE_CREW_ID = 1003
        final int TEST_MOVIE_IMAGE_ID = 1004
        final int TEST_MOVIE_VIDEO_ID = 1005
        final int TEST_MOVIE_REVIEW_ID = 1006
        final int TEST_MOVIE_RELEASE_ID = 1007
        final int TEST_MOVIE_COLLECTION_ID = 1008
        final int TEST_MOVIE_PERSON_ID = 1009
        final int TEST_MOVIE_PERSON_CAST_ID = 1010
        final int TEST_MOVIE_PERSON_CREW_ID = 1011
        final int TEST_MOVIE_PERSON_IMAGE_ID = 1012
        final int TEST_MOVIE_USER_LIST_FLAG_ID = 1013
        final String TEST_COUNTRY_ISO = 'IN'
  // intentionally includes a slash to make sure Uri is getting quoted correctly (i.e. getting %2F or '/' & %20 for space)
        final String TEST_MOVIE_CATEGORY = "/ic_drawer_now_popular movie"
        final long TEST_DATE = 1471042800000L //2016-08-13

        //test movie_basic_info methods
        final Uri movieTableIdUri = MovieMagicContract.MovieBasicInfo.buildMovieUri(TEST_MOVIE_TABLE_ID)
        assertEquals(TEST_BASE_URI+'/101',movieTableIdUri.toString())
        final Uri movieIdUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieId(TEST_MOVIE_ID)
        assertEquals(TEST_BASE_URI+'/1001',movieIdUri.toString())
        final Uri movieCategoryUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategory(TEST_MOVIE_CATEGORY)
        assertEquals(TEST_BASE_URI+'/%2Fpopular%20movie', movieCategoryUri.toString())
        final Uri movieCategoryAndCollIdUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategoryAndCollectionId(TEST_MOVIE_CATEGORY,TEST_MOVIE_COLLECTION_ID)
        assertEquals(TEST_BASE_URI+'/%2Fpopular%20movie/1008', movieCategoryAndCollIdUri.toString())
        assertEquals(new Date(TEST_DATE).toString(), new Date(MovieMagicContract.convertMovieReleaseDate('2016-08-13')).toString())

        assertEquals(TEST_MOVIE_ID,MovieMagicContract.MovieBasicInfo.getMovieIdFromUri(movieIdUri))
        assertEquals(TEST_MOVIE_CATEGORY,MovieMagicContract.MovieBasicInfo.getMovieCategoryFromMovieUri(movieCategoryUri))
        assertEquals(TEST_MOVIE_CATEGORY,MovieMagicContract.MovieBasicInfo.getMovieCategoryFromMovieAndCollectionIdUri(movieCategoryAndCollIdUri))
        assertEquals(TEST_MOVIE_COLLECTION_ID,MovieMagicContract.MovieBasicInfo.getCollectionIdFromMovieAndCollectionIdUri(movieCategoryAndCollIdUri))

        //test movie_casts methods
        final Uri movieCastTableIdUri = MovieMagicContract.MovieCast.buildMovieCastUri(TEST_MOVIE_TABLE_CAST_ID)
        assertEquals(TEST_MOVIE_CAST_URI+'/102',movieCastTableIdUri.toString())
        final Uri movieIdCastUri = MovieMagicContract.MovieCast.buildMovieCastUriWithMovieId(TEST_MOVIE_CAST_ID)
        assertEquals(TEST_MOVIE_CAST_URI+'/1002',movieIdCastUri.toString())
        assertEquals(TEST_MOVIE_CAST_ID,MovieMagicContract.MovieCast.getMovieIdFromMovieCastUri(movieIdCastUri))

        //test movie_crews methods
        final Uri movieCrewTableIdUri = MovieMagicContract.MovieCrew.buildMovieCrewUri(TEST_MOVIE_TABLE_CREW_ID)
        assertEquals(TEST_MOVIE_CREW_URI+'/103',movieCrewTableIdUri.toString())
        final Uri movieIdCrewUri = MovieMagicContract.MovieCrew.buildMovieCrewUriWithMovieId(TEST_MOVIE_CREW_ID)
        assertEquals(TEST_MOVIE_CREW_URI+'/1003',movieIdCrewUri.toString())
        assertEquals(TEST_MOVIE_CREW_ID,MovieMagicContract.MovieCrew.getMovieIdFromMovieCrewUri(movieIdCrewUri))

        //test movie_images methods
        final Uri movieImageTableIdUri = MovieMagicContract.MovieImage.buildMovieImageUri(TEST_MOVIE_TABLE_IMAGE_ID)
        assertEquals(TEST_MOVIE_IMAGE_URI+'/104',movieImageTableIdUri.toString())
        final Uri movieIdImageUri = MovieMagicContract.MovieImage.buildMovieImageUriWithMovieId(TEST_MOVIE_IMAGE_ID)
        assertEquals(TEST_MOVIE_IMAGE_URI+'/1004',movieIdImageUri.toString())
        assertEquals(TEST_MOVIE_IMAGE_ID,MovieMagicContract.MovieImage.getMovieIdFromMovieImageUri(movieIdImageUri))

        //test movie_videos methods
        final Uri movieVideoTableIdUri = MovieMagicContract.MovieVideo.buildMovieVideoUri(TEST_MOVIE_TABLE_VIDEO_ID)
        assertEquals(TEST_MOVIE_VIDEO_URI+'/105',movieVideoTableIdUri.toString())
        final Uri movieIdVideoUri = MovieMagicContract.MovieVideo.buildMovieVideoUriWithMovieId(TEST_MOVIE_VIDEO_ID)
        assertEquals(TEST_MOVIE_VIDEO_URI+'/1005',movieIdVideoUri.toString())
        assertEquals(TEST_MOVIE_VIDEO_ID,MovieMagicContract.MovieVideo.getMovieIdFromMovieVideoUri(movieIdVideoUri))

        //test movie_reviews methods
        final Uri movieReviewTableIdUri = MovieMagicContract.MovieReview.buildMovieReviewUri(TEST_MOVIE_TABLE_REVIEW_ID)
        assertEquals(TEST_MOVIE_REVIEW_URI+'/106',movieReviewTableIdUri.toString())
        final Uri movieIdReviewUri = MovieMagicContract.MovieReview.buildMovieReviewUriWithMovieId(TEST_MOVIE_REVIEW_ID)
        assertEquals(TEST_MOVIE_REVIEW_URI+'/1006',movieIdReviewUri.toString())
        assertEquals(TEST_MOVIE_REVIEW_ID,MovieMagicContract.MovieReview.getMovieIdFromMovieReviewUri(movieIdReviewUri))

        //test movie_release_dates methods
        final Uri movieReleaseTableIdUri = MovieMagicContract.MovieReleaseDate.buildMovieReleasewUri(TEST_MOVIE_TABLE_RELEASE_ID)
        assertEquals(TEST_MOVIE_RELEASE_URI+'/107',movieReleaseTableIdUri.toString())
        final Uri movieIdReleaseUri = MovieMagicContract.MovieReleaseDate.buildMovieReleaseUriWithMovieId(TEST_MOVIE_RELEASE_ID)
        assertEquals(TEST_MOVIE_RELEASE_URI+'/1007',movieIdReleaseUri.toString())
        final Uri movieReleaseCountryUri = MovieMagicContract.MovieReleaseDate.
                buildMovieReleaseUriWithMovieIdAndCountryIso(TEST_MOVIE_RELEASE_ID,TEST_COUNTRY_ISO)
        assertEquals(TEST_MOVIE_RELEASE_URI+ '/1007/IN',movieReleaseCountryUri.toString())
        assertEquals(TEST_MOVIE_RELEASE_ID,MovieMagicContract.MovieReleaseDate.getMovieIdFromMovieReleaseUri(movieReleaseCountryUri))
        assertEquals(TEST_COUNTRY_ISO,MovieMagicContract.MovieReleaseDate.getCountryIsoFromMovieReleaseUri(movieReleaseCountryUri))

        //test movie_collection methods
        final Uri movieCollectionTableIdUri = MovieMagicContract.MovieCollection.buildMovieCollectionUri(TEST_MOVIE_TABLE_COLLECTION_ID)
        assertEquals(TEST_MOVIE_COLLECTION_URI+'/108',movieCollectionTableIdUri.toString())
        final Uri movieIdCollectionIdUri = MovieMagicContract.MovieCollection.buildMovieCollectionUriWithCollectionId(TEST_MOVIE_COLLECTION_ID)
        assertEquals(TEST_MOVIE_COLLECTION_URI+'/1008',movieIdCollectionIdUri.toString())
        assertEquals(TEST_MOVIE_COLLECTION_ID,MovieMagicContract.MovieCollection.getCollectionIdFromMovieCollectionUri(movieIdCollectionIdUri))
        final Uri movieIdCollectionRowIdUri = MovieMagicContract.MovieCollection.buildMovieCollectionUri(10001)
        assertEquals(TEST_MOVIE_COLLECTION_URI+'/10001',movieIdCollectionRowIdUri.toString())
        assertEquals(10001,MovieMagicContract.MovieCollection.getCollectionRpwIdFromMovieCollectionUri(movieIdCollectionRowIdUri))

        //test movie_person_info methods
        final Uri moviePersonTableIdUri = MovieMagicContract.MoviePersonInfo.buildMoviePersonInfoUri(TEST_MOVIE_TABLE_PERSON_ID)
        assertEquals(TEST_MOVIE_PERSON_URI+'/109',moviePersonTableIdUri.toString())
        final Uri personIdUri = MovieMagicContract.MoviePersonInfo.buildMoviePersonInfoUriWithPersonId(TEST_MOVIE_PERSON_ID)
        assertEquals(TEST_MOVIE_PERSON_URI+'/1009',personIdUri.toString())
        assertEquals(TEST_MOVIE_PERSON_ID,MovieMagicContract.MoviePersonInfo.getPersonIdFromMoviePersonInfoUri(personIdUri))

        //test movie_person_casts methods
        final Uri moviePersonCastTableIdUri = MovieMagicContract.MoviePersonCast.buildMoviePersonCastUri(TEST_MOVIE_TABLE_PERSON_CAST_ID)
        assertEquals(TEST_MOVIE_PERSON_CAST_URI+'/110',moviePersonCastTableIdUri.toString())
        final Uri personCastIdUri = MovieMagicContract.MoviePersonCast.buildMoviePersonCastUriWithPersonId(TEST_MOVIE_PERSON_CAST_ID)
        assertEquals(TEST_MOVIE_PERSON_CAST_URI+'/1010',personCastIdUri.toString())
        assertEquals(TEST_MOVIE_PERSON_CAST_ID,MovieMagicContract.MoviePersonCast.getPersonIdFromMoviePersonCastUri(personCastIdUri))

        //test movie_person_crews methods
        final Uri moviePersonCrewTableIdUri = MovieMagicContract.MoviePersonCrew.buildMoviePersonCrewUri(TEST_MOVIE_TABLE_PERSON_CREW_ID)
        assertEquals(TEST_MOVIE_PERSON_CREW_URI+'/111',moviePersonCrewTableIdUri.toString())
        final Uri personCrewIdUri = MovieMagicContract.MoviePersonCrew.buildMoviePersonCrewUriWithPersonId(TEST_MOVIE_PERSON_CREW_ID)
        assertEquals(TEST_MOVIE_PERSON_CREW_URI+'/1011',personCrewIdUri.toString())
        assertEquals(TEST_MOVIE_PERSON_CREW_ID,MovieMagicContract.MoviePersonCrew.getPersonIdFromMoviePersonCrewUri(personCrewIdUri))

        //test movie_person_image methods
        final Uri moviePersonImageTableIdUri = MovieMagicContract.MoviePersonImage.buildMoviePersonImageUri(TEST_MOVIE_TABLE_PERSON_IMAGE_ID)
        assertEquals(TEST_MOVIE_PERSON_IMAGE_URI+'/112',moviePersonImageTableIdUri.toString())
        final Uri personImageIdUri = MovieMagicContract.MoviePersonImage.buildMoviePersonImageUriWithPersonId(TEST_MOVIE_PERSON_IMAGE_ID)
        assertEquals(TEST_MOVIE_PERSON_IMAGE_URI+'/1012',personImageIdUri.toString())
        assertEquals(TEST_MOVIE_PERSON_IMAGE_ID,MovieMagicContract.MoviePersonImage.getPersonIdFromMoviePersonImageUri(personImageIdUri))

        //test movie_user_list_flag methods
        final Uri movieUserListFlagIdUri = MovieMagicContract.MovieUserListFlag.buildMovieUserListFlagUri(TEST_MOVIE_TABLE_USER_LIST_FLAG_ID)
        assertEquals(TEST_MOVIE_USER_LIST_FLAG_URI+'/113',movieUserListFlagIdUri.toString())
        final Uri movieUserListWithMovieIdUri = MovieMagicContract.MovieUserListFlag.buildMovieUserListFlagUriWithMovieId(TEST_MOVIE_USER_LIST_FLAG_ID)
        assertEquals(TEST_MOVIE_USER_LIST_FLAG_URI+'/1013',movieUserListWithMovieIdUri.toString())
        assertEquals(TEST_MOVIE_USER_LIST_FLAG_ID,MovieMagicContract.MovieUserListFlag.getMovieIdFromMovieUserListFlagUri(movieUserListWithMovieIdUri))
    }
}