/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
class DetailMovieActivity extends AppCompatActivity implements DetailMovieFragment.CallbackForBackdropImageClick,
                DetailMovieFragment.CallbackForSimilarMovieClick {
    private static final String LOG_TAG = DetailMovieActivity.class.getSimpleName()

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreate is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_movie)

        // Nullify the theme's background to reduce overdraw
        getWindow().setBackgroundDrawable(null)

        // Show the EULA - first install or any update to the software
        // This is to ensure that if user declined and tried to view movie detail from notification menu
        new MyMoviesEULA(this).checkAndShowEula()

        if (savedInstanceState == null) {
            LogDisplay.callLog(LOG_TAG, 'onCreate: initial run', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
            //Get the arguments from the intent
            final Bundle extras = getIntent().getExtras()
            if (extras) {
                final DetailMovieFragment detailMovieFragment = new DetailMovieFragment()
                detailMovieFragment.setArguments(extras)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_movie_fragment_container,detailMovieFragment)
                        .addToBackStack(null)
                        .commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Could not parse intent data', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
            }
        } else {
            LogDisplay.callLog(LOG_TAG, 'onCreate: restore case', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        }
    }

    @Override
    protected void onResume() {
        LogDisplay.callLog(LOG_TAG, 'onResume is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        super.onResume()
    }

    @Override
    protected void onPause() {
        LogDisplay.callLog(LOG_TAG, 'onPause is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        super.onPause()
    }

    @Override
    protected void onStop() {
        LogDisplay.callLog(LOG_TAG, 'onStop is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        super.onStop()
    }

    @Override
    protected void onDestroy() {
        LogDisplay.callLog(LOG_TAG, 'onDestroy is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        super.onDestroy()
    }

    @Override
    Object onRetainCustomNonConfigurationInstance() {
        LogDisplay.callLog(LOG_TAG, 'onRetainCustomNonConfigurationInstance is called', LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        return super.onRetainCustomNonConfigurationInstance()
    }

    @Override
    void onBackPressed() {
        LogDisplay.callLog(LOG_TAG, "onBackPressed is called.FragmentBackstackCount:${getSupportFragmentManager().getBackStackEntryCount()}", LogDisplay.DETAIL_MOVIE_ACTIVITY_LOG_FLAG)
        //Start the animation
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
        //If this is the last fragment then finish the activity
        if(getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    // Override the callback method of DetailMovieFragment
    // Once an item is clicked then it will be called and it will launch the image viewer activity
    @Override
    void onBackdropImageClicked(final String title, final int position, final ArrayList<String> backdropImageFilePath) {
        final Bundle bundle = new Bundle()
        bundle.putString(GlobalStaticVariables.IMAGE_VIEWER_TITLE,title)
        bundle.putInt(GlobalStaticVariables.IMAGE_VIEWER_ADAPTER_POSITION, position)
        bundle.putStringArrayList(GlobalStaticVariables.IMAGE_VIEWER_IMAGE_PATH_ARRAY,backdropImageFilePath)
        bundle.putBoolean(GlobalStaticVariables.IMAGE_VIEWER_BACKDROP_IMAGE_FLAG, true)
        final Intent intent = new Intent(this, ImageViewerActivity.class)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    // Override the callback method of DetailMovieFragment
    // Once an item is clicked then it will be called and it will replace the fragment with the new movie
    @Override
    void onSimilarMovieItemSelected(final int movieId) {
        //Create an intent for DetailMovieActivity
        final Bundle bundle = new Bundle()
        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,GlobalStaticVariables.MOVIE_CATEGORY_SIMILAR)
        final DetailMovieFragment movieDetailFragment = new DetailMovieFragment()
        movieDetailFragment.setArguments(bundle)
        getSupportFragmentManager().beginTransaction()
        //Used the method enter,exit,popEnter,popExit custom animation. Our cases are enter & popExit
                .setCustomAnimations(R.anim.slide_bottom_in_animation,0,0,R.anim.slide_bottom_out_animation)
                .replace(R.id.detail_movie_fragment_container,movieDetailFragment)
        //Add this transaction to the back stack
                .addToBackStack(null) //Parameter is optional, so used null
                .commit()
    }
}