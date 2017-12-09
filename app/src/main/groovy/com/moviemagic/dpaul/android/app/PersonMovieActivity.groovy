/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.moviemagic.dpaul.android.app.adapter.PersonCastAdapter
import com.moviemagic.dpaul.android.app.adapter.PersonCrewAdapter
import com.moviemagic.dpaul.android.app.adapter.PersonImageAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class PersonMovieActivity extends AppCompatActivity implements PersonMovieFragment.CallbackForCastClick,
                PersonMovieFragment.CallbackForCrewClick, PersonMovieFragment.CallbackForImageClick {
    private static final String LOG_TAG = PersonMovieActivity.class.getSimpleName()

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_movie)

        // Nullify the theme's background to reduce overdraw
        getWindow().setBackgroundDrawable(null)

        if (savedInstanceState == null) {
            //Get the arguments from the intent
            final Bundle bundle = getIntent().getExtras()
            if (bundle) {
                final PersonMovieFragment personMovieFragment = new PersonMovieFragment()
                personMovieFragment.setArguments(bundle)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.person_fragment_container, personMovieFragment)
                        .commit()
            } else {
                LogDisplay.callLog(LOG_TAG, 'Could not parse intent data sent by DetailMovieFragment.', LogDisplay.PERSON_MOVIE_ACTIVITY_LOG_FLAG)
            }
        }
    }

    @Override
    void onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }

    /**
     * Fragment callback method for PersonCast - called when a movie item is clicked for person cast
     * @param movieId Movie id of the selected movie
     * @param viewHolder PersonCastAdapterViewHolder
     */
    @Override
    void onCastMovieItemSelected(final int movieId, final PersonCastAdapter.PersonCastAdapterViewHolder viewHolder) {
            final Bundle bundle = new Bundle()
            bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
            bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,GlobalStaticVariables.MOVIE_CATEGORY_PERSON)
            final Intent intent = new Intent(this, DetailMovieActivity.class)
            intent.putExtras(bundle)
            startActivity(intent)
            //Start the animation
            overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    /**
     * Fragment callback method for PersonCrew - called when a movie item is clicked for person crew
     * @param movieId Movie id of the selected movie
     * @param viewHolder PersonCrewAdapterViewHolder
     */
    @Override
    void onCrewMovieItemSelected(final int movieId, final PersonCrewAdapter.PersonCrewAdapterViewHolder viewHolder) {
        final Bundle bundle = new Bundle()
        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,GlobalStaticVariables.MOVIE_CATEGORY_PERSON)
        final Intent intent = new Intent(this, DetailMovieActivity.class)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    /**
     * Fragment callback method for PersonImage - called when a image item is clicked for person image
     * @param title person name
     * @param imageFilePath array of image profile path of the person
     * @param viewHolder PersonImageAdapterViewHolder
     */
    @Override
    void onImageMovieItemSelected(
            final String title, final int adapterPosition, final String[] imageFilePath, final PersonImageAdapter.PersonImageAdapterViewHolder viewHolder) {
        final Bundle bundle = new Bundle()
        bundle.putString(GlobalStaticVariables.IMAGE_VIEWER_TITLE,title)
        bundle.putInt(GlobalStaticVariables.IMAGE_VIEWER_ADAPTER_POSITION, adapterPosition)
        bundle.putStringArrayList(GlobalStaticVariables.IMAGE_VIEWER_IMAGE_PATH_ARRAY,imageFilePath as ArrayList<String>)
        bundle.putBoolean(GlobalStaticVariables.IMAGE_VIEWER_BACKDROP_IMAGE_FLAG, false)
        final Intent intent = new Intent(this, ImageViewerActivity.class)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }
}