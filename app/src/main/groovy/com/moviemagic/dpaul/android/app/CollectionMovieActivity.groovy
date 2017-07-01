/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

@CompileStatic
class CollectionMovieActivity extends AppCompatActivity implements GridMovieFragment.CallbackForGridItemClick, GridMovieFragment.CollectionColorChangeCallback {
    private static final String LOG_TAG = CollectionMovieActivity.class.getSimpleName()

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_movie)
        if (savedInstanceState == null) {
            //Get the arguments from the intent
            final Uri uri = getIntent().getData()
            if (uri) {
                LogDisplay.callLog(LOG_TAG, "Intent Data->${uri.toString()}", LogDisplay.COLLECTION_MOVIE_ACTIVITY_LOG_FLAG)
                final Bundle bundle = new Bundle()
                bundle.putParcelable(GlobalStaticVariables.MOVIE_COLLECTION_URI,uri)
                final CollectionMovieFragment collectionMovieFragment = new CollectionMovieFragment()
                collectionMovieFragment.setArguments(bundle)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.collection_fragment_container, collectionMovieFragment, GlobalStaticVariables.COLLECTION_MOVIE_FRAGMENT_TAG)
                        .commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Could not parse intent data.', LogDisplay.COLLECTION_MOVIE_ACTIVITY_LOG_FLAG)
            }
        }
    }

    @Override
    void onBackPressed() {
        LogDisplay.callLog(LOG_TAG, 'onBackPressed is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onBackPressed()
        //Start the animation
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }

    //Override the GridMovieFragment interface method
    @Override
    public void onMovieGridItemSelected(
            final int movieId, final String movieCategory) {
        LogDisplay.callLog(LOG_TAG, 'onItemSelected is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        final Intent intent = new Intent(this, DetailMovieActivity.class)
        final Bundle bundle = new Bundle()
        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,movieCategory)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    //Override the GridMovieFragment interface method
    @Override
    public void notifyCollectionColorChange() {
        LogDisplay.callLog(LOG_TAG, 'notifyCollectionColorChange is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        final CollectionMovieFragment fragment = (CollectionMovieFragment)getSupportFragmentManager().findFragmentByTag(GlobalStaticVariables.COLLECTION_MOVIE_FRAGMENT_TAG)
        if(CollectionMovieFragment) {
            fragment.loadCollBackdropAndChangeCollectionMovieGridColor()
        } else {
            LogDisplay.callLog(LOG_TAG, 'notifyCollectionColorChange: CollectionMovieFragment reference is null', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }
}