/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.contentprovider

import android.content.ComponentName
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.test.AndroidTestCase
import com.moviemagic.dpaul.android.app.TestUtilities
import com.moviemagic.dpaul.android.app.javamodule.TestContentObserverUtilities
import groovy.transform.CompileStatic

@CompileStatic
class TestMovieMagicProvider extends AndroidTestCase {

    static final String TEST_UPDATED_MOVIE_CATEGORY = 'ic_drawer_upcoming'
    static final String TEST_UPDATED_MOVIE_ID = 90809
    static final String TEST_UPDATED_COLL_ID = 10987
    static final String TEST_UPDATED_PERSON_ID = 89704
    static final String TEST_RELEASE_DATE = '2015-08-13'

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        Cursor delCursor
        TestContentObserverUtilities.TestContentObserver tco

        //Delete all records from movie_basic_info
        // Register a content observer for our data delete.
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieBasicInfo.CONTENT_URI, true, tco)
        mContext.getContentResolver().delete(MovieMagicContract.MovieBasicInfo.CONTENT_URI, null, null)
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieBasicInfo.CONTENT_URI,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_basic_info during delete', 0, delCursor.getCount())
        // Did the content observer get called?  If this fails, then delete isn't calling getContext().getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Since CASCADE delete is in effect, so deleting records from movie_basic_info should automatically
        //delete records from all other tables where MovieMagicContract.MovieBasicInfo._ID is used as foreign key
        //Ensure all records are deleted from movie_cast
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCast.CONTENT_URI,null,null,null,null)
        assertEquals('Error: All records not deleted from movie_cast during delete', 0, delCursor.getCount())
        //Ensure all records are deleted from movie_crew
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCrew.CONTENT_URI,null,null,null,null)
        assertEquals('Error: All records not deleted from movie_crew during delete', 0, delCursor.getCount())
        //Ensure all records are deleted from movie_image
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieImage.CONTENT_URI,null,null,null,null)
        assertEquals('Error: All records not deleted from movie_image during delete', 0, delCursor.getCount())
        //Ensure all records are deleted from movie_video
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieVideo.CONTENT_URI,null,null,null,null)
        assertEquals('Error: All records not deleted from movie_video during delete', 0, delCursor.getCount())
        //Ensure all records are deleted from movie_review
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReview.CONTENT_URI,null,null,null,null)
        assertEquals('Error: All records not deleted from movie_review during delete', 0, delCursor.getCount())
        //Ensure all records are deleted from movie_release_date
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReleaseDate.CONTENT_URI,null,null,null,null)
        assertEquals('Error: All records not deleted from movie_release_date during delete', 0, delCursor.getCount())
        //Ensure all records are deleted from movie_user_list_flag
        //Cascade delete is no more done for movie_user_list_flag
//        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieUserListFlag.CONTENT_URI,null,null,null,null)
//        assertEquals('Error: All records not deleted from movie_user_list_flag during delete', 0, delCursor.getCount())

        //Delete all records from movie_user_list_flag
        // Register a content observer for our data delete.
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieUserListFlag.CONTENT_URI, true, tco)
        mContext.getContentResolver().delete(MovieMagicContract.MovieUserListFlag.CONTENT_URI, null, null)
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieUserListFlag.CONTENT_URI,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_user_list_flag during delete', 0, delCursor.getCount())
        // Did the content observer get called?  If this fails, then delete isn't calling getContext().getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)

        //Delete all records from movie_person_info
        // Register a content observer for our data delete.
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MoviePersonInfo.CONTENT_URI, true, tco)
        mContext.getContentResolver().delete(MovieMagicContract.MoviePersonInfo.CONTENT_URI, null, null)
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonInfo.CONTENT_URI,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_person_info during delete', 0, delCursor.getCount())
        // Did the content observer get called?  If this fails, then delete isn't calling getContext().getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Since CASCADE delete is in effect, so deleting records from movie_person_info should automatically
        //delete records from all other tables where MovieMagicContract.MoviePersonInfo._ID is used as foreign key
        //Ensure all records are deleted from movie_person_cast
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCast.CONTENT_URI,null,null,null,null)
        assertEquals('Error: All records not deleted from movie_person_cast during delete', 0, delCursor.getCount())
        //Ensure all records are deleted from movie_person_crew
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCrew.CONTENT_URI,null,null,null,null)
        assertEquals('Error: All records not deleted from movie_person_cast during delete', 0, delCursor.getCount())
        //Ensure all records are deleted from movie_person_image
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonImage.CONTENT_URI,null,null,null,null)
        assertEquals('Error: All records not deleted from movie_person_image during delete', 0, delCursor.getCount())

        //Delete all records from movie_collection
        // Register a content observer for our data delete.
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieCollection.CONTENT_URI, true, tco)
        mContext.getContentResolver().delete(MovieMagicContract.MovieCollection.CONTENT_URI, null, null)
        delCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCollection.CONTENT_URI,null,null,null,null)
        assertEquals('Error: Records not deleted from movie_collection during delete', 0, delCursor.getCount())
        // Did the content observer get called?  If this fails, then delete isn't calling getContext().getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)

        //Close the cursor
        delCursor.close()
    }

    /*
        This helper function deletes all records from the database tables using the database,
        specially needed when the provider has not implemented the delete function yet
    */
    void deleteAllRecordsFromDB() {
        //Since the CASCADE delete is in effect, so only delete the main primary tables
        //Commented out the rest, however PLEASE DO NOT DELETE THE LINES as those may be needed later
        final MovieMagicDbHelper dbHelper = new MovieMagicDbHelper(mContext)
        final SQLiteDatabase db = dbHelper.getWritableDatabase()
        db.delete(MovieMagicContract.MovieBasicInfo.TABLE_NAME, null, null)
//        db.delete(MovieMagicContract.MovieCast.TABLE_NAME, null, null)
//        db.delete(MovieMagicContract.MovieCrew.TABLE_NAME, null, null)
//        db.delete(MovieMagicContract.MovieImage.TABLE_NAME, null, null)
//        db.delete(MovieMagicContract.MovieVideo.TABLE_NAME, null, null)
//        db.delete(MovieMagicContract.MovieReview.TABLE_NAME, null, null)
//        db.delete(MovieMagicContract.MovieReleaseDate.TABLE_NAME, null, null)
        //Cascade delete is no more done for movie_user_list_flag
        db.delete(MovieMagicContract.MovieUserListFlag.TABLE_NAME, null, null)
        db.delete(MovieMagicContract.MoviePersonInfo.TABLE_NAME, null, null)
//        db.delete(MovieMagicContract.MoviePersonCast.TABLE_NAME, null, null)
//        db.delete(MovieMagicContract.MoviePersonCrew.TABLE_NAME, null, null)
//        db.delete(MovieMagicContract.MoviePersonImage.TABLE_NAME, null, null)
        db.delete(MovieMagicContract.MovieCollection.TABLE_NAME, null, null)
        db.close()
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp()
        //Now that delete function is implemented, so no more required
        deleteAllRecordsFromDB()
        //Instead use this one
//        deleteAllRecordsFromProvider()
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
     */
    void testProviderRegistry() {
        final PackageManager pm = mContext.getPackageManager()

        // We define the component name based on the package name from the context and the
        // MovieMagicProvider class.
        final ComponentName componentName = new ComponentName(mContext.getPackageName(),MovieMagicProvider.class.getName())
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            final ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0)

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieMagicProvider registered with authority: $providerInfo.authority instead of authority: $MovieMagicContract.CONTENT_AUTHORITY",
                    providerInfo.authority, MovieMagicContract.CONTENT_AUTHORITY)
        } catch (final PackageManager.NameNotFoundException e) {
            // If reaches here it means the provider isn't registered correctly.
            assertTrue('Error: MovieMagicProvider not registered at ' + mContext.getPackageName(),false)
        }
    }

    /*
       This test doesn't touch the database.  It verifies that the ContentProvider returns
       the correct type for each type of URI that it can handle.
    */
    void testGetType() {
        final int testMovieId = 43546
        final int testCollectionId = 7890
        final int testPersonId = 2468
        final String testMovieCategory = 'ic_drawer_now_popular'
        final String testCntryISO = 'US'

        //Test the type for movie_basic_info
        // content://com.moviemagic.dpaul.android.app/movie_basic_info
        def type = mContext.getContentResolver().getType(MovieMagicContract.MovieBasicInfo.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_basic_info
        assertEquals('Error: movie_basic_info should return MovieBasicInfo.CONTENT_TYPE',MovieMagicContract.MovieBasicInfo.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_basic_info/43546
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieId(testMovieId))
        // vnd.android.cursor.item/com.moviemagic.dpaul.android.app/movie_basic_info
        assertEquals('Error: movie_basic_info with movie id should return MovieBasicInfo.CONTENT_ITEM_TYPE',MovieMagicContract.MovieBasicInfo.CONTENT_ITEM_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_basic_info/popular
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategory(testMovieCategory))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_basic_info
        assertEquals('Error: movie_basic_info with category should return MovieBasicInfo.CONTENT_TYPE',MovieMagicContract.MovieBasicInfo.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_basic_info/popular/7890
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategoryAndCollectionId(testMovieCategory,testCollectionId))
        // vnd.android.cursor.item/com.moviemagic.dpaul.android.app/movie_basic_info
        assertEquals('Error: movie_basic_info with category should return MovieBasicInfo.CONTENT_ITEM_TYPE',MovieMagicContract.MovieBasicInfo.CONTENT_ITEM_TYPE, type)

        //Test the type for movie_cast
        // content://com.moviemagic.dpaul.android.app/movie_cast
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieCast.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_cast
        assertEquals('Error: movie_cast should return MovieCast.CONTENT_TYPE',MovieMagicContract.MovieCast.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_cast/43546
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieCast.buildMovieCastUriWithMovieId(testMovieId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_cast
        assertEquals('Error: movie_cast with movie id should return MovieCast.CONTENT_TYPE',MovieMagicContract.MovieCast.CONTENT_TYPE, type)

        //Test the type for movie_crew
        // content://com.moviemagic.dpaul.android.app/movie_crew
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieCrew.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_crew
        assertEquals('Error: movie_crew should return MovieCrew.CONTENT_TYPE',MovieMagicContract.MovieCrew.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_crew/43546
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieCrew.buildMovieCrewUriWithMovieId(testMovieId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_crew
        assertEquals('Error: movie_crew with movie id should return MovieCrew.CONTENT_ITEM_TYPE',MovieMagicContract.MovieCrew.CONTENT_TYPE, type)

        //Test the type for movie_image
        // content://com.moviemagic.dpaul.android.app/movie_image
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieImage.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_image
        assertEquals('Error: movie_image should return MovieImage.CONTENT_TYPE',MovieMagicContract.MovieImage.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_image/43546
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieImage.buildMovieImageUriWithMovieId(testMovieId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_image
        assertEquals('Error: movie_image with movie id should return MovieImage.CONTENT_TYPE',MovieMagicContract.MovieImage.CONTENT_TYPE, type)

        //Test the type for movie_video
        // content://com.moviemagic.dpaul.android.app/movie_video
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieVideo.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_video
        assertEquals('Error: movie_video should return MovieVideo.CONTENT_TYPE',MovieMagicContract.MovieVideo.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_video/43546
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieVideo.buildMovieVideoUriWithMovieId(testMovieId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_video
        assertEquals('Error: movie_video with movie id should return MovieVideo.CONTENT_TYPE',MovieMagicContract.MovieVideo.CONTENT_TYPE, type)

        //Test the type for movie_review
        // content://com.moviemagic.dpaul.android.app/movie_review
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieReview.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_review
        assertEquals('Error: movie_review should return MovieReview.CONTENT_TYPE',MovieMagicContract.MovieReview.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_review/43546
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieReview.buildMovieReviewUriWithMovieId(testMovieId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_review
        assertEquals('Error: movie_review with movie id should return MovieReview.CONTENT_TYPE',MovieMagicContract.MovieReview.CONTENT_TYPE, type)

        //Test the type for movie_release_date
        // content://com.moviemagic.dpaul.android.app/movie_release_date
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieReleaseDate.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_release_Date
        assertEquals('Error: movie_release_date should return MovieReleaseDate.CONTENT_TYPE',MovieMagicContract.MovieReleaseDate.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_release_date/43546
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieReleaseDate.buildMovieReleaseUriWithMovieId(testMovieId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_release
        assertEquals('Error: movie_release_date with movie id should return MovieReleaseDate.CONTENT_TYPE',MovieMagicContract.MovieReleaseDate.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_release_date/43546/US
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieReleaseDate.buildMovieReleaseUriWithMovieIdAndCountryIso(testMovieId,testCntryISO))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_release
        assertEquals('Error: movie_release_date with movie id should return MovieReleaseDate.CONTENT_TYPE',MovieMagicContract.MovieReleaseDate.CONTENT_TYPE, type)

        //Test the type for movie_image
        // content://com.moviemagic.dpaul.android.app/movie_user_list_flag
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieUserListFlag.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_user_list_flag
        assertEquals('Error: movie_user_list_flag should return MovieUserListFlag.CONTENT_TYPE',MovieMagicContract.MovieUserListFlag.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_user_list_flag/43546
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieUserListFlag.buildMovieUserListFlagUriWithMovieId(testMovieId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_user_list_flag
        assertEquals('Error: MovieUserListFlag with movie id should return MovieUserListFlag.CONTENT_TYPE',MovieMagicContract.MovieUserListFlag.CONTENT_ITEM_TYPE, type)


        //Test the type for movie_person_info
        // content://com.moviemagic.dpaul.android.app/movie_person_info
        type = mContext.getContentResolver().getType(MovieMagicContract.MoviePersonInfo.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_person_info
        assertEquals('Error: movie_person_info should return MoviePersonInfo.CONTENT_TYPE',MovieMagicContract.MoviePersonInfo.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_person_info/2468
        type = mContext.getContentResolver().getType(MovieMagicContract.MoviePersonInfo.buildMoviePersonInfoUriWithPersonId(testPersonId))
        // vnd.android.cursor.item/com.moviemagic.dpaul.android.app/movie_person_info
        assertEquals('Error: movie_person_info with person id should return MoviePersonInfo.CONTENT_ITEM_TYPE',MovieMagicContract.MoviePersonInfo.CONTENT_ITEM_TYPE, type)

        //Test the type for movie_person_cast
        // content://com.moviemagic.dpaul.android.app/movie_person_cast
        type = mContext.getContentResolver().getType(MovieMagicContract.MoviePersonCast.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_person_cast
        assertEquals('Error: movie_person_cast should return MoviePersonCast.CONTENT_TYPE',MovieMagicContract.MoviePersonCast.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_person_cast/2468
        type = mContext.getContentResolver().getType(MovieMagicContract.MoviePersonCast.buildMoviePersonCastUriWithPersonId(testPersonId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_person_cast
        assertEquals('Error: movie_person_cast with person id should return MoviePersonCast.CONTENT_TYPE',MovieMagicContract.MoviePersonCast.CONTENT_TYPE, type)

        //Test the type for movie_person_crew
        // content://com.moviemagic.dpaul.android.app/movie_person_crew
        type = mContext.getContentResolver().getType(MovieMagicContract.MoviePersonCrew.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_person_crew
        assertEquals('Error: movie_person_crew should return MoviePersonCrew.CONTENT_TYPE',MovieMagicContract.MoviePersonCrew.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_person_crew/2468
        type = mContext.getContentResolver().getType(MovieMagicContract.MoviePersonCrew.buildMoviePersonCrewUriWithPersonId(testPersonId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_person_crew
        assertEquals('Error: movie_person_crew with person id should return MoviePersonCrew.CONTENT_TYPE',MovieMagicContract.MoviePersonCrew.CONTENT_TYPE, type)

        //Test the type for movie_person_image
        // content://com.moviemagic.dpaul.android.app/movie_person_image
        type = mContext.getContentResolver().getType(MovieMagicContract.MoviePersonImage.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_person_image
        assertEquals('Error: movie_person_image should return MoviePersonImage.CONTENT_TYPE',MovieMagicContract.MoviePersonImage.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_person_image/2468
        type = mContext.getContentResolver().getType(MovieMagicContract.MoviePersonImage.buildMoviePersonImageUriWithPersonId(testPersonId))
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_person_image
        assertEquals('Error: movie_person_image with person id should return MoviePersonImage.CONTENT_TYPE',MovieMagicContract.MoviePersonImage.CONTENT_TYPE, type)

        //Test the type for movie_collection
        // content://com.moviemagic.dpaul.android.app/movie_collection
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieCollection.CONTENT_URI)
        // vnd.android.cursor.dir/com.moviemagic.dpaul.android.app/movie_collection
        assertEquals('Error: movie_collection should return MovieCollection.CONTENT_TYPE',MovieMagicContract.MovieCollection.CONTENT_TYPE, type)
        // content://com.moviemagic.dpaul.android.app/movie_collection/7890
        type = mContext.getContentResolver().getType(MovieMagicContract.MovieCollection.buildMovieCollectionUriWithCollectionId(testCollectionId))
        // vnd.android.cursor.item/com.moviemagic.dpaul.android.app/movie_collection
        assertEquals('Error: movie_collection with ic_drawer_user_collection id should return MovieCollection.CONTENT_ITEM_TYPE',MovieMagicContract.MovieCollection.CONTENT_ITEM_TYPE, type)
    }

    /*
        This is to test that the query method of the provider is working fine for all the tables.
        This test uses the database directly to insert (i.e. not using provider's insert method) and then uses the ContentProvider to
        read out the data using query method.
    */
    void testBasicMovieMagicQuery() {
        final MovieMagicDbHelper dbHelper = new MovieMagicDbHelper(mContext)
        final SQLiteDatabase db = dbHelper.getWritableDatabase()
        ContentValues testValues
        Cursor testCursor
        final int testMovieId = TestUtilities.TEST_MOVIE_ID
        final String testMovieCategory = TestUtilities.TEST_MOVIE_CATEGORY
        final int testCollId = TestUtilities.TEST_COLL_ID
        final int testPersonID = TestUtilities.TEST_PERSON_ID
        final String testCntryISO = TestUtilities.TEST_COUNTRY_ISO
        long rowId
        final long movieBasicInfoForeignKey
        final long personInfoForeignKey
        //Insert test records to movie_basic_info
        testValues = TestUtilities.createMovieValues()
        rowId = db.insert(MovieMagicContract.MovieBasicInfo.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_basic_info data into the Database', rowId != -1)
        movieBasicInfoForeignKey = rowId
        // Test the basic content provider query for movie_basic_info
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieBasicInfo.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_basic_info', testCursor, testValues)
        // Test the rowId id content provider query for movie_basic_info
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieId(testMovieId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_basic_info', testCursor, testValues)
        // Test the movie category content provider query for movie_basic_info
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategory(testMovieCategory),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_basic_info', testCursor, testValues)
        // Test the movie category and collection id content provider query for movie_basic_info
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategoryAndCollectionId(testMovieCategory, testCollId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_basic_info', testCursor, testValues)

        //Insert test records to movie_cast
        testValues = TestUtilities.createMovieCastValues(movieBasicInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MovieCast.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_cast data into the Database', rowId != -1)
        // Test the basic content provider query for movie_cast
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCast.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_cast', testCursor, testValues)
        // Test the movie id content provider query for movie_cast
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCast.buildMovieCastUriWithMovieId(testMovieId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_cast', testCursor, testValues)

        //Insert test records to movie_crew
        testValues = TestUtilities.createMovieCrewValues(movieBasicInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MovieCrew.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_crew data into the Database', rowId != -1)
        // Test the basic content provider query for movie_crew
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCrew.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_crew', testCursor, testValues)
        // Test the movie id content provider query for movie_crew
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCrew.buildMovieCrewUriWithMovieId(testMovieId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_crew', testCursor, testValues)

        //Insert test records to movie_image
        testValues = TestUtilities.createMovieImageValues(movieBasicInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MovieImage.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_image data into the Database', rowId != -1)
        // Test the basic content provider query for movie_image
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieImage.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_image', testCursor, testValues)
        // Test the movie id content provider query for movie_image
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieImage.buildMovieImageUriWithMovieId(testMovieId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_image', testCursor, testValues)

        //Insert test records to movie_video
        testValues = TestUtilities.createMovieVideoValues(movieBasicInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MovieVideo.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_video data into the Database', rowId != -1)
        // Test the basic content provider query for movie_video
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieVideo.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_video', testCursor, testValues)
        // Test the movie id content provider query for movie_video
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieVideo.buildMovieVideoUriWithMovieId(testMovieId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_video', testCursor, testValues)

        //Insert test records to movie_review
        testValues = TestUtilities.createMovieReviewValues(movieBasicInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MovieReview.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_review data into the Database', rowId != -1)
        // Test the basic content provider query for movie_review
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReview.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_review', testCursor, testValues)
        // Test the movie id content provider query for movie_review
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReview.buildMovieReviewUriWithMovieId(testMovieId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_review', testCursor, testValues)

        //Insert test records to movie_release_date
        testValues = TestUtilities.createMovieReleaseDateValues(movieBasicInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MovieReleaseDate.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_release_date data into the Database', rowId != -1)        // Test the basic content provider query for movie_review
        // Test the basic content provider query for movie_release_date
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReleaseDate.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_release_date', testCursor, testValues)
        // Test the movie id content provider query for movie_release_date
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReleaseDate.buildMovieReleaseUriWithMovieId(testMovieId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_release_date', testCursor, testValues)
        // Test the movie id and ISO country content provider query for movie_release_date
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReleaseDate.buildMovieReleaseUriWithMovieIdAndCountryIso(testMovieId,testCntryISO),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_release_date', testCursor, testValues)


        //Insert test records to movie_user_list_flag
        testValues = TestUtilities.createMovieUserListFlagValues(movieBasicInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MovieUserListFlag.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert MovieUserListFlag data into the Database', rowId != -1)
        // Test the basic content provider query for MovieUserListFlag
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieUserListFlag.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: MovieUserListFlag', testCursor, testValues)
        // Test the movie id content provider query for MovieUserListFlag
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieUserListFlag.buildMovieUserListFlagUriWithMovieId(testMovieId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: MovieUserListFlag', testCursor, testValues)

        //Insert test records to movie_person_info
        testValues = TestUtilities.createMoviePersonInfoValues()
        rowId = db.insert(MovieMagicContract.MoviePersonInfo.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_person_info data into the Database', rowId != -1)
        personInfoForeignKey = rowId
        // Test the basic content provider query for movie_person_info
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonInfo.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_person_info', testCursor, testValues)
        // Test the movie id content provider query for movie_person_info
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonInfo.buildMoviePersonInfoUriWithPersonId(testPersonID),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_person_info', testCursor, testValues)

        //Insert test records to movie_person_cast
        testValues = TestUtilities.createMoviePersonCastValues(personInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MoviePersonCast.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_person_cast data into the Database', rowId != -1)
        // Test the basic content provider query for movie_person_cast
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCast.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_person_cast', testCursor, testValues)
        // Test the movie id content provider query for movie_person_cast
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCast.buildMoviePersonCastUriWithPersonId(testPersonID),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_person_cast', testCursor, testValues)

        //Insert test records to movie_person_crew
        testValues = TestUtilities.createMoviePersonCrewValues(personInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MoviePersonCrew.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_person_crew data into the Database', rowId != -1)
        // Test the basic content provider query for movie_person_crew
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCrew.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_person_crew', testCursor, testValues)
        // Test the movie id content provider query for movie_person_crew
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCrew.buildMoviePersonCrewUriWithPersonId(testPersonID),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_person_crew', testCursor, testValues)

        //Insert test records to movie_person_image
        testValues = TestUtilities.createMoviePersonImageValues(personInfoForeignKey)
        rowId = db.insert(MovieMagicContract.MoviePersonImage.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_person_image data into the Database', rowId != -1)
        // Test the basic content provider query for movie_person_image
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonImage.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_person_image', testCursor, testValues)
        // Test the movie id content provider query for movie_person_image
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonImage.buildMoviePersonImageUriWithPersonId(testPersonID),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_person_image', testCursor, testValues)

        //Insert test records to movie_collection
        testValues = TestUtilities.createMovieCollectionValues()
        rowId = db.insert(MovieMagicContract.MovieCollection.TABLE_NAME, null, testValues)
        assertTrue('Error: Unable to Insert movie_collection data into the Database', rowId != -1)
        // Test the basic content provider query for movie_collection
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCollection.CONTENT_URI,null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_collection', testCursor, testValues)
        // Test the collection id content provider query for movie_collection
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCollection.buildMovieCollectionUriWithCollectionId(testCollId),null,null,null,null)
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor('testBasicMovieMagicQuery: movie_collection', testCursor, testValues)

        //Now close the database
        db.close()
    }

    /*
        This test uses the provider to insert and then update the data.
     */
    void testUpdateMovieMagic() {
        ContentValues testValues
        ContentValues updatedValues
        Uri testUri
        final long movieMagicRowId
        final long movieMagicCollRowId
        final long movieMagicPersonRowId
        int rowCount
        Cursor queryCursor
        Cursor updateCursor
        TestContentObserverUtilities.TestContentObserver tco
        final String[] testMovieId
        String[] testId

        //Test the update for movie_basic_info
        // Create a new record
        testValues = TestUtilities.createMovieValues()
        //TestUtilities uses the date in long format which is used to test raw database and table
        //but this test goes via provider where the expected date is yyyy-mm-dd format, so overriding the date
        testValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,TEST_RELEASE_DATE)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, testValues)
        movieMagicRowId = ContentUris.parseId(testUri)
        // Verify a valid insertion
        assertTrue(movieMagicRowId != -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY, TEST_UPDATED_MOVIE_CATEGORY)
        //again update the date as provider expects it in yyyy-mm-dd format
        updatedValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,TEST_RELEASE_DATE)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MovieBasicInfo.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testMovieId =  [Integer.toString(TestUtilities.TEST_MOVIE_ID)]
        rowCount = mContext.getContentResolver().update(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                updatedValues,
                MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID + "= ?",
                testMovieId)
        assertEquals('testUpdateMovieMagic: movie_basic_info', rowCount, 1)
        // Test to make sure our observer is called.
        // If the code is failing here, it means that the content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(
                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                null,   // projection
                "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY = '$TEST_UPDATED_MOVIE_CATEGORY'",
                null,   // Values for the "where" clause
                null    // sort order
        )
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_basic_info', updateCursor, updatedValues)

        //Test the update for movie_cast
        //Create and insert new record
        testValues = TestUtilities.createMovieCastValues(movieMagicRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieCast.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID, TEST_UPDATED_MOVIE_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCast.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MovieCast.CONTENT_URI,updatedValues,MovieMagicContract.MovieCast._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_cast', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCast.CONTENT_URI,null,"$MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID = '$TEST_UPDATED_MOVIE_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_cast', updateCursor, updatedValues)

        //Test the update for movie_crew
        //Create and insert new record
        testValues = TestUtilities.createMovieCrewValues(movieMagicRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieCrew.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID, TEST_UPDATED_MOVIE_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCrew.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MovieCrew.CONTENT_URI,updatedValues,MovieMagicContract.MovieCrew._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_crew', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCrew.CONTENT_URI,null,"$MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID = '$TEST_UPDATED_MOVIE_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_crew', updateCursor, updatedValues)

        //Test the update for movie_image
        //Create and insert new record
        testValues = TestUtilities.createMovieImageValues(movieMagicRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieImage.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID, TEST_UPDATED_MOVIE_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MovieImage.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MovieImage.CONTENT_URI,updatedValues,MovieMagicContract.MovieImage._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_image', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MovieImage.CONTENT_URI,null,"$MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID = '$TEST_UPDATED_MOVIE_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_image', updateCursor, updatedValues)

        //Test the update for movie_video
        //Create and insert new record
        testValues = TestUtilities.createMovieVideoValues(movieMagicRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieVideo.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID, TEST_UPDATED_MOVIE_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MovieVideo.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MovieVideo.CONTENT_URI,updatedValues,MovieMagicContract.MovieVideo._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_video', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MovieVideo.CONTENT_URI,null,"$MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID = '$TEST_UPDATED_MOVIE_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_video', updateCursor, updatedValues)

        //Test the update for movie_review
        //Create and insert new record
        testValues = TestUtilities.createMovieReviewValues(movieMagicRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieReview.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID, TEST_UPDATED_MOVIE_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReview.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MovieReview.CONTENT_URI,updatedValues,MovieMagicContract.MovieReview._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_review', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReview.CONTENT_URI,null,"$MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID = '$TEST_UPDATED_MOVIE_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_review', updateCursor, updatedValues)

        //Test the update for movie_release_date
        //Create and insert new record
        testValues = TestUtilities.createMovieReleaseDateValues(movieMagicRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieReleaseDate.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID, TEST_UPDATED_MOVIE_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReleaseDate.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MovieReleaseDate.CONTENT_URI,updatedValues,MovieMagicContract.MovieReleaseDate._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_release_date', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReleaseDate.CONTENT_URI,null,"$MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID = '$TEST_UPDATED_MOVIE_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_release_date', updateCursor, updatedValues)

        //Test the update for movie_user_list_flag
        //Create and insert new record
        testValues = TestUtilities.createMovieUserListFlagValues(movieMagicRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieUserListFlag.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID, TEST_UPDATED_MOVIE_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MovieUserListFlag.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MovieUserListFlag.CONTENT_URI,updatedValues,MovieMagicContract.MovieUserListFlag._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_user_list_flag', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MovieUserListFlag.CONTENT_URI,null,"$MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID = '$TEST_UPDATED_MOVIE_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_user_list_flag', updateCursor, updatedValues)

        //Test the update for movie_person_info
        //Create and insert new record
        testValues = TestUtilities.createMoviePersonInfoValues()
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MoviePersonInfo.CONTENT_URI, testValues)
        movieMagicPersonRowId = ContentUris.parseId(testUri)
        // Verify a valid insertion
        assertTrue(movieMagicPersonRowId!= -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID, TEST_UPDATED_PERSON_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonInfo.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicPersonRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MoviePersonInfo.CONTENT_URI,updatedValues,MovieMagicContract.MoviePersonInfo._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_person_info', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonInfo.CONTENT_URI,null,"$MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID = '$TEST_UPDATED_PERSON_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_person_info', updateCursor, updatedValues)

        //Test the update for movie_person_cast
        //Create and insert new record
        testValues = TestUtilities.createMoviePersonCastValues(movieMagicPersonRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MoviePersonCast.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri)!= -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID, TEST_UPDATED_PERSON_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCast.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicPersonRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MoviePersonCast.CONTENT_URI,updatedValues,MovieMagicContract.MoviePersonCast._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_person_cast', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCast.CONTENT_URI,null,"$MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID = '$TEST_UPDATED_PERSON_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_person_cast', updateCursor, updatedValues)

        //Test the update for movie_person_crew
        //Create and insert new record
        testValues = TestUtilities.createMoviePersonCrewValues(movieMagicPersonRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MoviePersonCrew.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri)!= -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID, TEST_UPDATED_PERSON_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCrew.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicPersonRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MoviePersonCrew.CONTENT_URI,updatedValues,MovieMagicContract.MoviePersonCrew._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_person_crew', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCrew.CONTENT_URI,null,"$MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID = '$TEST_UPDATED_PERSON_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_person_crew', updateCursor, updatedValues)

        //Test the update for movie_person_image
        //Create and insert new record
        testValues = TestUtilities.createMoviePersonImageValues(movieMagicPersonRowId)
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MoviePersonImage.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri)!= -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID, TEST_UPDATED_PERSON_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonImage.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicPersonRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MoviePersonImage.CONTENT_URI,updatedValues,MovieMagicContract.MoviePersonImage._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_person_image', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonImage.CONTENT_URI,null,"$MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID = '$TEST_UPDATED_PERSON_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_person_image', updateCursor, updatedValues)

        //Test the update for movie_collection
        //Create and insert new record
        testValues = TestUtilities.createMovieCollectionValues()
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieCollection.CONTENT_URI, testValues)
        movieMagicCollRowId = ContentUris.parseId(testUri)
        // Verify a valid insertion
        assertTrue(movieMagicCollRowId!= -1)
        //Now update a field
        updatedValues = new ContentValues(testValues)
        updatedValues.put(MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID, TEST_UPDATED_COLL_ID)
        // Create a cursor with observer to make sure that the content provider is notifying the observers as expected
        queryCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCollection.CONTENT_URI, null, null, null, null)
        tco = TestContentObserverUtilities.getTestContentObserver()
        queryCursor.registerContentObserver(tco)
        testId =  [Long.toString(movieMagicCollRowId)]
        rowCount = mContext.getContentResolver().update(MovieMagicContract.MovieCollection.CONTENT_URI,updatedValues,MovieMagicContract.MovieCollection._ID + "= ?", testId)
        assertEquals('testUpdateMovieMagic: movie_collection', rowCount, 1)
        // Test to make sure our observer is called.
        tco.waitForNotificationOrFail()
        queryCursor.unregisterContentObserver(tco)
        // A cursor is the primary interface to the query results.
        updateCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCollection.CONTENT_URI,null,"$MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID = '$TEST_UPDATED_COLL_ID'",null,null)
        TestUtilities.validateCursor('testUpdateMovieMagic: movie_collection', updateCursor, updatedValues)

        //Close the cursors
        queryCursor.close()
        updateCursor.close()
    }

    /*
        Test the insert of all types of tables of the MovieMagic provider
     */
    void testInsertMovieMagicProvider() {
        ContentValues testValues
        Uri testUri
        Cursor testCursor
        final TestContentObserverUtilities.TestContentObserver tco = TestContentObserverUtilities.getTestContentObserver()

        testValues = TestUtilities.createMovieValues()
        //TestUtilities uses the date in long format which is used to test raw database and table
        //but this test goes via provider where the expected date is yyyy-mm-dd format, so overriding the date
        testValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,TEST_RELEASE_DATE)

        // Register a content observer for the insert, directly with the content resolver
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieBasicInfo.CONTENT_URI, true, tco)

        //Insert the test record
         testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, testValues)

        // Did the content observer get called?  If this fails, then insert
        // isn't calling getContext().getContentResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)

        final long movieMagicRowId = ContentUris.parseId(testUri)

        // Verify a valid insertion
        assertTrue(movieMagicRowId != -1)

        // Data's inserted. Now pull some out to ensure it got added properly
        // A cursor is your primary interface to the query results.
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieBasicInfo.CONTENT_URI, null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_basic_info', testCursor, testValues)

        //Now that the data is inserted to movie_basic_info, let's add to other related tables
        testValues = TestUtilities.createMovieCastValues(movieMagicRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieCast.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieCast.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCast.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_cast', testCursor, testValues)

        testValues = TestUtilities.createMovieCrewValues(movieMagicRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieCrew.CONTENT_URI,true,tco)
        //Insert the data
         testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieCrew.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCrew.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_crew', testCursor, testValues)

        testValues = TestUtilities.createMovieImageValues(movieMagicRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieImage.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieImage.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieImage.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_image', testCursor, testValues)

        testValues = TestUtilities.createMovieVideoValues(movieMagicRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieVideo.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieVideo.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieVideo.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_video', testCursor, testValues)

        testValues = TestUtilities.createMovieReviewValues(movieMagicRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieReview.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieReview.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReview.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_review', testCursor, testValues)

        testValues = TestUtilities.createMovieReleaseDateValues(movieMagicRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieReleaseDate.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieReleaseDate.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReleaseDate.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_release_date', testCursor, testValues)

        testValues = TestUtilities.createMovieUserListFlagValues(movieMagicRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieUserListFlag.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieUserListFlag.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieUserListFlag.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_user_list_flag', testCursor, testValues)
        //<<<<<<<<<<<<<----------------------------------------------------------------------------------------------->>>>>>>//
        //Insert test record to movie_person_info table
        testValues = TestUtilities.createMoviePersonInfoValues()
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MoviePersonInfo.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MoviePersonInfo.CONTENT_URI, testValues)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Get the _id of the inserted record
        final long moviePersonRowId = ContentUris.parseId(testUri)

        // Verify a valid insertion
        assertTrue(moviePersonRowId != -1)

        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonInfo.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_person_info', testCursor, testValues)

        //Now that the data is inserted to movie_person_info, let's add to other related tables
        testValues = TestUtilities.createMoviePersonCastValues(moviePersonRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MoviePersonCast.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MoviePersonCast.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCast.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_person_cast', testCursor, testValues)

        testValues = TestUtilities.createMoviePersonCrewValues(moviePersonRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MoviePersonCrew.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MoviePersonCrew.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCrew.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_person_crew', testCursor, testValues)

        testValues = TestUtilities.createMoviePersonImageValues(moviePersonRowId)
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MoviePersonImage.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MoviePersonImage.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonImage.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_person_image', testCursor, testValues)
        //<<<<<<<<<<<<<----------------------------------------------------------------------------------------------->>>>>>>//
        //Insert test record to movie_collection table
        testValues = TestUtilities.createMovieCollectionValues()
        //Register the content observer
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieCollection.CONTENT_URI,true,tco)
        //Insert the data
        testUri = mContext.getContentResolver().insert(MovieMagicContract.MovieCollection.CONTENT_URI, testValues)
        // Verify a valid insertion
        assertTrue(ContentUris.parseId(testUri) != -1)
        //Test that the content observer is called, if it fails it means content observer was not called
        tco.waitForNotificationOrFail()
        //Unregister the observer
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Pull the data and verify
        testCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCollection.CONTENT_URI,null, null, null, null)
        TestUtilities.validateCursor('testInsertMovieMagicProvider: movie_collection', testCursor, testValues)
    }

    /*
    Test that provider can delete the record properly
     */
    void testDeleteRecords() {
        //First insert a record before delete
        testInsertMovieMagicProvider()

        //delete records from all tables
        deleteAllRecordsFromProvider()
    }

    /*
   Test that provider can do the bulk insert for all tables
    */
    void testBulkInsert() {
        //first delete all existing records to give it a fresh start
        //this is important because UNIQUE with REPLACE is used in the tables
        //so this will ensure we have all new data and count should not have any abrupt mismatch
//        MovieMagicDbHelper dbHelper = new MovieMagicDbHelper(mContext)
//        SQLiteDatabase db = dbHelper.getWritableDatabase()
//        db.delete(MovieMagicContract.MovieBasicInfo.TABLE_NAME, null, null)

        final int BULK_INSERT_COUNT = 10
        int bulkCount
        Cursor bulkCursor
        final ContentValues[] testValues = new ContentValues[BULK_INSERT_COUNT]
        TestContentObserverUtilities.TestContentObserver tco

        //Bulk insert for movie_basic_info
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMovieValues(i)
            //TestUtilities uses the date in long format which is used to test raw database and table
            //but this test goes via provider where the expected date is yyyy-mm-dd format, so overriding the date
            testValues[i].put(MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE, TEST_RELEASE_DATE)

        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieBasicInfo.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_basic_info',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MovieBasicInfo.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID ASC" // sort order == Movie id, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_basic_info',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_basic_info - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Bulk insert for movie_cast
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            //Since there is UNIQUE REPLACE is in effect in the child table, so in order to ensure the primary
            //and foreign key match, instead of sending 'i' as is '1' is added so that foreign key starts from 1
            //and ends at 10 (which ideally the numbers in primary table as we insert records in empty tables).
            //Same rules applied for all child tables
            testValues[i] = TestUtilities.createBulkMovieCastValues(i+1)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieCast.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MovieCast.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_cast',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCast.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MovieCast.COLUMN_FOREIGN_KEY_ID ASC" // sort order == foreign key, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_cast',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_cast - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Bulk insert for movie_crew
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMovieCrewValues(i+1)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieCrew.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MovieCrew.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_crew',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCrew.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MovieCrew.COLUMN_FOREIGN_KEY_ID ASC" // sort order == foreign key, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_crew',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_crew - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Bulk insert for movie_image
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMovieImageValues(i+1)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieImage.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MovieImage.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_image',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MovieImage.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MovieImage.COLUMN_FOREIGN_KEY_ID ASC" // sort order == foreign key, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_image',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_image - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Bulk insert for movie_video
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMovieVideoValues(i+1)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieVideo.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MovieVideo.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_video',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MovieVideo.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MovieVideo.COLUMN_FOREIGN_KEY_ID ASC" // sort order == foreign key, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_video',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_video - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Bulk insert for movie_review
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMovieReviewValues(i+1)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieReview.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MovieReview.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_review',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReview.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MovieReview.COLUMN_FOREIGN_KEY_ID" // sort order == foreign key, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_review',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_review - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Bulk insert for movie_release_date
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMovieReleaseDateValues(i+1)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieReleaseDate.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MovieReleaseDate.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_release_date',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MovieReleaseDate.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MovieReleaseDate.COLUMN_FOREIGN_KEY_ID ASC" // sort order == foreign key, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_release_date',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_release_date - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //** Bulk insert is not valid for movie_user_list_flag, so no testing done

        //Bulk insert for movie_person_info
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMoviePersonInfoValues(i)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MoviePersonInfo.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MoviePersonInfo.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_person_info',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonInfo.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID ASC" // sort order == person id, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_person_info',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_person_info - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Bulk insert for movie_person_cast
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMoviePersonCastValues(i+1)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MoviePersonCast.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MoviePersonCast.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_person_cast',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCast.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MoviePersonCast.COLUMN_FOREIGN_KEY_ID ASC" // sort order == foreign key, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_person_cast',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_person_cast - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Bulk insert for movie_person_crew
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMoviePersonCrewValues(i+1)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MoviePersonCrew.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MoviePersonCrew.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_person_crew',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonCrew.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MoviePersonCrew.COLUMN_FOREIGN_KEY_ID ASC" // sort order == foreign key, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_person_crew',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_person_crew - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }


        //Bulk insert for movie_person_image
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMoviePersonImageValues(i+1)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MoviePersonImage.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MoviePersonImage.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_person_image',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MoviePersonImage.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MoviePersonImage.COLUMN_FOREIGN_KEY_ID ASC" // sort order == foreign key, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_person_image',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_person_image - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Bulk insert for movie_collection
        //Create some bulk data
        for (final i in 0..(BULK_INSERT_COUNT - 1)) {
            testValues[i] = TestUtilities.createBulkMovieCollectionValues(i)
        }
        //Register a content observer
        tco = TestContentObserverUtilities.getTestContentObserver()
        mContext.getContentResolver().registerContentObserver(MovieMagicContract.MovieCollection.CONTENT_URI, true, tco)
        //Do a bulk insert
        bulkCount = mContext.getContentResolver().bulkInsert(MovieMagicContract.MovieCollection.CONTENT_URI, testValues)
        //If this fails, it means that we are not calling the getContext().getContentResolver().notifyChange(uri, null) in your BulkInsert
        tco.waitForNotificationOrFail()
        mContext.getContentResolver().unregisterContentObserver(tco)
        //Validate the bulkInsert count matches
        assertEquals('Error:bulk insert count not matched for movie_collection',bulkCount, BULK_INSERT_COUNT)
        // A cursor is the primary interface to the query results.
        bulkCursor = mContext.getContentResolver().query(MovieMagicContract.MovieCollection.CONTENT_URI, null, null, null,
                "$MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID ASC" // sort order == collection id, so that below cursor validation works
        )
        //should have as many records in the database as we've inserted
        assertEquals('Error:bulk inserted query rec count not matched for movie_collection',bulkCursor.getCount(), BULK_INSERT_COUNT)
        // and let's make sure they match the ones we created
        bulkCursor.moveToFirst()
        for (final int i in 0..(BULK_INSERT_COUNT - 1)) {
            TestUtilities.validateCurrentRecord("testBulkInsert: movie_collection - #$i", bulkCursor, testValues[i])
            bulkCursor.moveToNext()
        }

        //Close the cursor
        bulkCursor.close()
    }
}