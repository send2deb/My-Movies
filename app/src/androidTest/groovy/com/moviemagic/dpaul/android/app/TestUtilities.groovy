/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.ContentValues
import android.database.Cursor
import android.test.AndroidTestCase
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.Utility
import groovy.transform.CompileStatic

@CompileStatic
class TestUtilities extends AndroidTestCase {

    static final long TEST_DATE = 1471042800385 //2016-08-13
    public static int TEST_MOVIE_ID = 101
    public static int TEST_COLL_ID = 202
    public static int TEST_PERSON_ID = 303
    public static final String TEST_MOVIE_CATEGORY = 'ic_drawer_now_popular'
    public static final String TEST_COUNTRY_ISO = 'US'

    static void validateCursor(final String error, final Cursor valueCursor, final ContentValues expectedValues) {
        assertTrue("Empty cursor returned for $error", valueCursor.moveToFirst())
        validateCurrentRecord(error, valueCursor, expectedValues)
        valueCursor.close()
    }

    static void validateCurrentRecord(final String error, final Cursor valueCursor, final ContentValues expectedValues) {
        final Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet()
        for (final Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey()
            int idx = valueCursor.getColumnIndex(columnName)
            assertFalse("Column '$columnName' not found in $error", idx == -1)
            String expectedValue = entry.getValue().toString()
            assertEquals("Value '${entry.getValue().toString()}' did not match the expected value '$expectedValue.$error",
                    expectedValue, valueCursor.getString(idx))
        }
    }

    static ContentValues createMovieValues() {
        final ContentValues movieInfoValues = new ContentValues()
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,TEST_MOVIE_ID)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_ORIGINAL_TITLE,'Troy')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW,'Troy is a good movie')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_ADULT_FLAG,'false')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH,'/troy.jpg')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT,20)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG,7.8f)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_VIDEO_FLAG,'True')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY,5.6f)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,'troyposter.jpg')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,TEST_DATE)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,TEST_MOVIE_CATEGORY)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE,GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_PUBLIC)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,'Helen of Troy')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER,1)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_ID,TEST_COLL_ID)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP,Utility.getTodayDate())
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP,Utility.getTodayDate())
        movieInfoValues
    }

    static ContentValues createMovieCastValues(final long fKeyId) {
        final ContentValues movieCastValues = new ContentValues()
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID,TEST_MOVIE_ID)
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_CHARACTER,'Robin')
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_ID,901)
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_NAME,'B Pit')
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_PROFILE_PATH,'/pathOfTheProfile')
        movieCastValues
    }

    static ContentValues createMovieCrewValues(final long fKeyId) {
        final ContentValues movieCrewValues = new ContentValues()
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID,TEST_MOVIE_ID)
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_ID,902)
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_CREW_JOB,'Director')
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_NAME,'R Hood')
        movieCrewValues
    }

    static ContentValues createMovieImageValues(final long fKeyId) {
        final ContentValues movieImageValues = new ContentValues()
        movieImageValues.put(MovieMagicContract.MovieImage.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieImageValues.put(MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID,TEST_MOVIE_ID)
        movieImageValues.put(MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE,'poster')
        movieImageValues.put(MovieMagicContract.MovieImage.COLUMN_IMAGE_FILE_PATH,'/filePath')
        movieImageValues
    }

    static ContentValues createMovieVideoValues(final long fKeyId) {
        final ContentValues movieVideoValues = new ContentValues()
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID,TEST_MOVIE_ID)
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_VIDEO_KEY,'aafgslkhs')
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE,'YouTube')
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE,'Trailer')
        movieVideoValues
    }

    static ContentValues createMovieReviewValues(final long fKeyId) {
        final ContentValues movieReviewValues = new ContentValues()
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID,TEST_MOVIE_ID)
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_REVIEW_ID,105)
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_REVIEW_AUTHOR,'P Rob')
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_REVIEW_CONTENT,'This is a review')
        movieReviewValues
    }

    static ContentValues createMovieReleaseDateValues(final long fKeyId) {
        final ContentValues movieReleaseDateValues = new ContentValues()
        movieReleaseDateValues.put(MovieMagicContract.MovieReleaseDate.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieReleaseDateValues.put(MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID,TEST_MOVIE_ID)
        movieReleaseDateValues.put(MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY,TEST_COUNTRY_ISO)
        movieReleaseDateValues.put(MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_DATE,'2012-03-22')
        movieReleaseDateValues
    }

    static ContentValues createMovieUserListFlagValues(final long fKeyId) {
        final ContentValues movieUserListFlagValues = new ContentValues()
        movieUserListFlagValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieUserListFlagValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID,TEST_MOVIE_ID)
        movieUserListFlagValues
    }

    static ContentValues createMoviePersonInfoValues() {
        final ContentValues moviePersonInfoValues = new ContentValues()
        moviePersonInfoValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID,TEST_PERSON_ID)
        moviePersonInfoValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_NAME,'Person One')
        moviePersonInfoValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_CREATE_TIMESTAMP,Utility.getTodayDate())
        moviePersonInfoValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_UPDATE_TIMESTAMP,Utility.getTodayDate())
        moviePersonInfoValues
    }

    static ContentValues createMoviePersonCastValues(final long fKeyId) {
        final ContentValues moviePersonCastValues = new ContentValues()
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_FOREIGN_KEY_ID,fKeyId)
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID,TEST_PERSON_ID)
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CHARACTER,'Robin Hood')
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_MOVIE_ID,1029)
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_TITLE,'The Robin Hood')
        moviePersonCastValues
    }

    static ContentValues createMoviePersonCrewValues(final long fKeyId) {
        final ContentValues moviePersonCrewValues = new ContentValues()
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_FOREIGN_KEY_ID,fKeyId)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID,TEST_PERSON_ID)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_MOVIE_ID,3009)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_JOB,'Producer')
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_TITLE,'God Father')
        moviePersonCrewValues
    }

    static ContentValues createMoviePersonImageValues(final long fKeyId) {
        final ContentValues moviePersonCrewValues = new ContentValues()
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonImage.COLUMN_FOREIGN_KEY_ID,fKeyId)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID,TEST_PERSON_ID)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_FILE_PATH,'/image_file_path')
        moviePersonCrewValues
    }

    static ContentValues createMovieCollectionValues() {
        final ContentValues movieCollectionValues = new ContentValues()
        movieCollectionValues.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID,TEST_COLL_ID)
        movieCollectionValues.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_NAME,14)
        movieCollectionValues.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_CREATE_TIMESTAMP,Utility.getTodayDate())
        movieCollectionValues.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_UPDATE_TIMESTAMP,Utility.getTodayDate())
        movieCollectionValues
    }

    static ContentValues createBulkMovieValues(final int movieId) {
        final ContentValues movieInfoValues = new ContentValues()
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,movieId)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_ORIGINAL_TITLE,'Troy')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW,'Troy is a good movie')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_ADULT_FLAG,'false')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH,'/troy.jpg')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT,20)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG,7.8f)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_VIDEO_FLAG,'True')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY,5.6f)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,'troyposter.jpg')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,TEST_DATE)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,TEST_MOVIE_CATEGORY)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE,'tmdb_public')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,'Helen of Troy')
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER,1)
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP,Utility.getTodayDate())
        movieInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP,Utility.getTodayDate())
        movieInfoValues
    }

    static ContentValues createBulkMoviePersonInfoValues(final int personId) {
        final ContentValues moviePersonInfoValues = new ContentValues()
        moviePersonInfoValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID,personId)
        moviePersonInfoValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_NAME,'Person One')
        moviePersonInfoValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_CREATE_TIMESTAMP,Utility.getTodayDate())
        moviePersonInfoValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_UPDATE_TIMESTAMP,Utility.getTodayDate())
        moviePersonInfoValues
    }

    //fKeyId is added to ORIG_MOVIE_ID and ORIG_PERSON_ID in this and following methods to ensure they become
    //unique so that bulk insert can be tested successfully (work around for UNIQUE REPLACE used in tables)
    static ContentValues createBulkMovieCastValues(final long fKeyId) {
        final ContentValues movieCastValues = new ContentValues()
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID,TEST_MOVIE_ID+fKeyId)
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_CHARACTER,'Robin')
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_ID,901)
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_NAME,'B Pit')
        movieCastValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_PROFILE_PATH,'/pathOfTheProfile')
        movieCastValues
    }

    static ContentValues createBulkMovieCrewValues(final long fKeyId) {
        final ContentValues movieCrewValues = new ContentValues()
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID,TEST_MOVIE_ID+fKeyId)
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_ID,902)
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_CREW_JOB,'Director')
        movieCrewValues.put(MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_NAME,'R Hood')
        movieCrewValues
    }

    static ContentValues createBulkMovieImageValues(final long fKeyId) {
        final ContentValues movieImageValues = new ContentValues()
        movieImageValues.put(MovieMagicContract.MovieImage.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieImageValues.put(MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID,TEST_MOVIE_ID+fKeyId)
        movieImageValues.put(MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE,'poster')
        movieImageValues.put(MovieMagicContract.MovieImage.COLUMN_IMAGE_FILE_PATH,'/filePath')
        movieImageValues
    }

    static ContentValues createBulkMovieVideoValues(final long fKeyId) {
        final ContentValues movieVideoValues = new ContentValues()
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID,TEST_MOVIE_ID+fKeyId)
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_VIDEO_KEY,'aafgslkhs')
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE,'YouTube')
        movieVideoValues.put(MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE,'Trailer')
        movieVideoValues
    }

    static ContentValues createBulkMovieReviewValues(final long fKeyId) {
        final ContentValues movieReviewValues = new ContentValues()
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID,TEST_MOVIE_ID+fKeyId)
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_REVIEW_ID,105)
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_REVIEW_AUTHOR,'P Rob')
        movieReviewValues.put(MovieMagicContract.MovieReview.COLUMN_REVIEW_CONTENT,'This is a review')
        movieReviewValues
    }

    static ContentValues createBulkMovieReleaseDateValues(final long fKeyId) {
        final ContentValues movieReleaseDateValues = new ContentValues()
        movieReleaseDateValues.put(MovieMagicContract.MovieReleaseDate.COLUMN_FOREIGN_KEY_ID,fKeyId)
        movieReleaseDateValues.put(MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID,TEST_MOVIE_ID+fKeyId)
        movieReleaseDateValues.put(MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY,TEST_COUNTRY_ISO)
        movieReleaseDateValues.put(MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_DATE,'2012-03-22')
        movieReleaseDateValues
    }

    static ContentValues createBulkMoviePersonCastValues(final long fKeyId) {
        final ContentValues moviePersonCastValues = new ContentValues()
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_FOREIGN_KEY_ID,fKeyId)
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID,TEST_PERSON_ID+fKeyId)
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CHARACTER,'Robin Hood')
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_MOVIE_ID,1029)
        moviePersonCastValues.put(MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_TITLE,'The Robin Hood')
        moviePersonCastValues
    }

    static ContentValues createBulkMoviePersonCrewValues(final long fKeyId) {
        final ContentValues moviePersonCrewValues = new ContentValues()
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_FOREIGN_KEY_ID,fKeyId)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID,TEST_PERSON_ID+fKeyId)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_MOVIE_ID,3009)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_JOB,'Producer')
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_TITLE,'God Father')
        moviePersonCrewValues
    }

    static ContentValues createBulkMoviePersonImageValues(final long fKeyId) {
        final ContentValues moviePersonCrewValues = new ContentValues()
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonImage.COLUMN_FOREIGN_KEY_ID,fKeyId)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID,TEST_PERSON_ID+fKeyId)
        moviePersonCrewValues.put(MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_FILE_PATH,'/file_poster_path')
        moviePersonCrewValues
    }

    static ContentValues createBulkMovieCollectionValues(final int collId) {
        final ContentValues movieCollectionValues = new ContentValues()
        movieCollectionValues.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID,collId)
        movieCollectionValues.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_NAME,14)
        movieCollectionValues.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_CREATE_TIMESTAMP,Utility.getTodayDate())
        movieCollectionValues.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_UPDATE_TIMESTAMP,Utility.getTodayDate())
        movieCollectionValues
    }
}