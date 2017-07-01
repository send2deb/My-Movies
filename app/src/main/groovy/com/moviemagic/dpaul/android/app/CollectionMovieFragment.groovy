/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.moviemagic.dpaul.android.app.adapter.MovieGridRecyclerAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.*
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class CollectionMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = CollectionMovieFragment.class.getSimpleName()

    private Uri mCollectionMovieIdUri
    private int mCollectionId
    private CollapsingToolbarLayout mCollapsingToolbar
    private Toolbar mToolbar
    private AppBarLayout mAppBarLayout
    private ImageView mBackdropImageView
    private TextView mCollectionTitleTextView
    private TextView mCollectionOverviewTextViewHeader
    private TextView mCollectionOverviewTextView
    private View mCollectionTitleDividerView
    private static final int COLLECTION_MOVIE_FRAGMENT_LOADER_ID = 0
    private int mPalletePrimaryColor
    private int mPalletePrimaryDarkColor
    private int mPalleteTitleColor
    private int mPalleteBodyTextColor
    private int mPalleteAccentColor
    private RelativeLayout mCollectionDetailLayout
    private CoordinatorLayout mCollectionCoordLayout
    private NestedScrollView mNestedScrollView
    private String mCollectionBackdropPath
    private boolean mCollectionDataLoaded = false
    private String mCollectionName
    private AppBarLayout.OnOffsetChangedListener mAppbarOnOffsetChangeListener

    //Columns to fetch from movie_collection table for similar movies
    private static final String[] COLLECTION_MOVIE_COLUMNS = [MovieMagicContract.MovieCollection._ID,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_ID,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_NAME,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_OVERVIEW,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_POSTER_PATH,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_BACKDROP_PATH,
                                                           MovieMagicContract.MovieCollection.COLUMN_COLLECTION_MOVIE_PRESENT_FLAG]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_COLLECTION_MOVIE_ID = 0
    final static int COL_COLLECTION_MOVIE_COLLECTION_ID = 1
    final static int COL_COLLECTION_MOVIE_COLLECTION_NAME = 2
    final static int COL_COLLECTION_MOVIE_COLLECTION_OVERVIEW = 3
    final static int COL_COLLECTION_MOVIE_COLLECTION_POSTER_PATH = 4
    final static int COL_COLLECTION_MOVIE_COLLECTION_BACKDROP_PATH = 5
    final static int COL_COLLECTION_MOVIE_PRESENT_FLAG = 6

    //An empty constructor is needed so that lifecycle is properly handled
    public CollectionMovieFragment() {
        LogDisplay.callLog(LOG_TAG,'CollectionMovieFragment empty constructor is called',LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        //Following line needed to let android know that Fragment has options menu
        //If this line is not added then associated method (e.g. OnCreateOptionsMenu) does not get supported
        //even in auto code completion
        setHasOptionsMenu(true)
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreateView is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        //Get the bundle from the Fragment
        final Bundle args = getArguments()
        if (args) {
            mCollectionMovieIdUri = args.getParcelable(GlobalStaticVariables.MOVIE_COLLECTION_URI) as Uri
            LogDisplay.callLog(LOG_TAG, "Collection Fragment arguments.Uri -> $mCollectionMovieIdUri", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            mCollectionId = MovieMagicContract.MovieCollection.getCollectionIdFromMovieCollectionUri(mCollectionMovieIdUri)
        }
        //Inflate the view before referring any view using id
        final View mRootView = inflater.inflate(R.layout.fragment_collection_movie, container, false)
        mBackdropImageView = mRootView.findViewById(R.id.collection_backdrop_image) as ImageView
        mCollectionTitleTextView = mRootView.findViewById(R.id.movie_collection_title) as TextView
        mCollectionOverviewTextViewHeader = mRootView.findViewById(R.id.collection_overview_header) as TextView
        mCollectionOverviewTextView = mRootView.findViewById(R.id.collection_overview) as TextView
        mCollectionDetailLayout = mRootView.findViewById(R.id.movie_detail_collection_layout) as RelativeLayout
        mNestedScrollView = mRootView.findViewById(R.id.collection_nested_scroll) as NestedScrollView
        mAppBarLayout = mRootView.findViewById(R.id.collection_app_bar_layout) as AppBarLayout
        mCollectionCoordLayout = mRootView.findViewById(R.id.collection_coordinator_layout) as CoordinatorLayout
        mCollectionTitleDividerView = mRootView.findViewById(R.id.movie_collection_title_divider) as View

        return mRootView
    }

    @Override
    void onActivityCreated(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onActivityCreated is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onActivityCreated(savedInstanceState)
        final AppCompatActivity appCompatActivity = getActivity() as AppCompatActivity
        mToolbar = getView().findViewById(R.id.collection_toolbar) as Toolbar
        if (mToolbar) {
            appCompatActivity.setSupportActionBar(mToolbar)
            //Enable back to home button
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        }
        mCollapsingToolbar = getView().findViewById(R.id.collection_collapsing_toolbar) as CollapsingToolbarLayout
        if (mCollapsingToolbar) {
            //Just clear off to be on the safe side
            mCollapsingToolbar.setTitle(" ")
        }

        //If it's a fresh start then call init loader
        if(savedInstanceState == null) {
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:first time, so init loaders', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().initLoader(COLLECTION_MOVIE_FRAGMENT_LOADER_ID, null, this)
        } else {        //If it's restore then restart the loader
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:not first time, so restart loaders', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().restartLoader(COLLECTION_MOVIE_FRAGMENT_LOADER_ID, null, this)
        }
    }

    @Override
    void onStart() {
        LogDisplay.callLog(LOG_TAG, 'onStart is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onStart()
        // Check if the user is online or not, if not then show a message
        final boolean isOnline = Utility.isOnline(getActivity().getApplicationContext())
        if(!isOnline) {
            Snackbar.make(mNestedScrollView, getString(R.string.no_internet_connection_message), Snackbar.LENGTH_LONG).show()
        } else if(Utility.isOnlyWifi(getActivity().getApplicationContext()) & !GlobalStaticVariables.WIFI_CONNECTED) {
            // If user has selected only WiFi but user is online without WiFi then show a dialog
            Snackbar.make(mNestedScrollView, getString(R.string.internet_connection_without_wifi_message), Snackbar.LENGTH_LONG).show()
        } else if (Utility.isReducedDataOn(getActivity())) {
            // If user has selected reduced data
            Snackbar.make(mNestedScrollView, getString(R.string.reduced_data_use_on_message), Snackbar.LENGTH_LONG).show()
        }

        //Show the title only when image is collapsed
        mAppbarOnOffsetChangeListener = new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false
            int scrollRange = -1
            @Override
            void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                LogDisplay.callLog(LOG_TAG, 'onOffsetChanged is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange()
                }
                LogDisplay.callLog(LOG_TAG, "scrollRange + verticalOffset:$scrollRange & $verticalOffset", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(mCollectionName)
                    isShow = true
                } else if (isShow) {
                    mCollapsingToolbar.setTitle(" ")
                    isShow = false
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu (final Menu menu, final MenuInflater inflater) {
        LogDisplay.callLog(LOG_TAG, 'onCreateOptionsMenu is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        // Inflate the menu, this adds items to the action bar if it is present.
        inflater.inflate(R.menu.collection_fragment_menu, menu)
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        LogDisplay.callLog(LOG_TAG, 'onOptionsItemSelected is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        final int itemId = item.getItemId()
        switch (itemId) {
            case R.id.menu_action_share:
                if(mCollectionDataLoaded) {
                    shareCollection()
                } else {
                    Snackbar.make(mNestedScrollView, getString(R.string.collection_share_cannot_perform), Snackbar.LENGTH_LONG).show()
                    LogDisplay.callLog(LOG_TAG,'onOptionsItemSelected: collection data not yet loaded. Try again later!',LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                }
                return true
            case android.R.id.home:
                getActivity().finish()
                return true
            default:
                return super.onOptionsItemSelected(item)
        }
    }

    @Override
    Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        LogDisplay.callLog(LOG_TAG, "onCreateLoader is called.loader id->$id", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        switch (id) {
            case COLLECTION_MOVIE_FRAGMENT_LOADER_ID:
                return new CursorLoader(
                        getActivity(),              //Parent Activity Context
                        mCollectionMovieIdUri,      //Table to query
                        COLLECTION_MOVIE_COLUMNS,   //Projection to return
                        null,                       //Selection Clause, null->will return all data
                        null,                       //Selection Arg, null-> will return all data
                        null)                       //Sort order, not required
            default:
                return null
        }
    }

    @Override
    void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "onLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        if(data.moveToFirst()) {
            LogDisplay.callLog(LOG_TAG, "onLoadFinished.Data present for collection id $mCollectionId", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "onLoadFinished.collection movie flag ${data.getInt(COL_COLLECTION_MOVIE_PRESENT_FLAG)}", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            mCollectionBackdropPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
                    "${data.getString(COL_COLLECTION_MOVIE_COLLECTION_BACKDROP_PATH)}"
            mCollectionName = data.getString(COL_COLLECTION_MOVIE_COLLECTION_NAME)
            mCollectionTitleTextView.setText(mCollectionName)
            mCollectionOverviewTextView.setText(data.getString(COL_COLLECTION_MOVIE_COLLECTION_OVERVIEW))
            // If in landscape mode then hide the collection title and associated divider
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mCollectionTitleTextView.setVisibility(TextView.GONE)
                mCollectionTitleDividerView.setVisibility(View.GONE)
            }
            if(data.getInt(COL_COLLECTION_MOVIE_PRESENT_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                //Fragment transaction cannot be done inside onnLoadFinished, so work around is to use a handler as per
                //stackoverflow http://stackoverflow.com/questions/22788684/can-not-perform-this-action-inside-of-onloadfinished
                final int WHAT = 1
                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(final Message msg) {
                        if(msg.what == WHAT) loadFragment()
                    }
                }
                final boolean handlerFlag = handler.sendEmptyMessage(WHAT)
                if(!handlerFlag) {
                    LogDisplay.callLog(LOG_TAG,'Handler could not post the message',LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                }

                // Set the listener if in portrait mode or show the title in Toolbar for landscape
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mAppBarLayout.addOnOffsetChangedListener(mAppbarOnOffsetChangeListener)
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle('')
                } else {
                    mCollapsingToolbar.setTitleEnabled(false)
                    mToolbar.setTitle(data.getString(COL_COLLECTION_MOVIE_COLLECTION_NAME))
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle('')
                }

            } else {
                LogDisplay.callLog(LOG_TAG, "onLoadFinished.Collection movie flag is false for collection id $mCollectionId. So go clean up and re-load", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                if(Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                    new LoadCollectionData(getActivity()).execute([mCollectionId, GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE] as Integer[])
                } else {
                    LogDisplay.callLog(LOG_TAG, '1-> Device is offline or connected to internet without WiFi and user selected download only on WiFi', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                }
            }
            mCollectionDataLoaded = true
        } else {
            //Load the collection details and associated movies
            LogDisplay.callLog(LOG_TAG, "onLoadFinished.Data not present for collection id $mCollectionId, go and fetch it", LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            if(Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                new LoadCollectionData(getActivity()).execute([mCollectionId, GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE] as Integer[])
            } else {
                LogDisplay.callLog(LOG_TAG, '2-> Device is offline or connected to internet without WiFi and user selected download only on WiFi', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            }
        }
    }

    @Override
    void onLoaderReset(final Loader<Cursor> loader) {
        LogDisplay.callLog(LOG_TAG, 'onLoaderReset is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        //Do nothing
    }

    void loadFragment() {
        LogDisplay.callLog(LOG_TAG, 'loadFragment is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        //Set this flag as false so that derived (from collection backdrop) primaryDark color is used in the grid
        MovieGridRecyclerAdapter.collectionGridFlag = true
        final Bundle bundle = new Bundle()
        final Uri uri = MovieMagicContract.MovieBasicInfo
                .buildMovieUriWithMovieCategoryAndCollectionId(GlobalStaticVariables.MOVIE_CATEGORY_COLLECTION,mCollectionId)
        bundle.putParcelable(GlobalStaticVariables.MOVIE_CATEGORY_AND_COLL_ID_URI,uri)
        final GridMovieFragment gridMovieFragment = new GridMovieFragment()
        gridMovieFragment.setArguments(bundle)
        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction()
        fragmentTransaction.replace(R.id.collection_movie_grid, gridMovieFragment)
        fragmentTransaction.commit()
    }

    @Override
    void onResume() {
        LogDisplay.callLog(LOG_TAG, 'onResume is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onResume()
    }

    @Override
    void onPause() {
        LogDisplay.callLog(LOG_TAG, 'onPause is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onPause()
    }

    @Override
    void onStop() {
        LogDisplay.callLog(LOG_TAG, 'onStop is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onStop()
        //Cancel Picasso requests - required where callback (hard reference) is used
        Picasso.with(getActivity()).cancelRequest(mBackdropImageView)
    }

    @Override
    void onDestroyView() {
        LogDisplay.callLog(LOG_TAG, 'onDestroyView is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        // Remove listeners
        mAppBarLayout.removeOnOffsetChangedListener(mAppbarOnOffsetChangeListener)
        super.onDestroyView()
    }

    @Override
    void onDestroy() {
        LogDisplay.callLog(LOG_TAG, 'onDestroy is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onDestroy()
    }

    @Override
    void onDetach() {
        LogDisplay.callLog(LOG_TAG,'onDetach is called',LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        super.onDetach()
    }

    /**
     * This method is called from CollectionMovieActivity
     */
    void loadCollBackdropAndChangeCollectionMovieGridColor() {
        LogDisplay.callLog(LOG_TAG, 'loadCollBackdropAndChangeCollectionMovieGridColor is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
            final Callback picassoCollectionImageCallback = new Callback() {
                @Override
                void onSuccess() {
                    LogDisplay.callLog(LOG_TAG, 'Picasso onSuccess is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                    // If user does not select dynamic theme (default value) then do not change the color
                    if (Utility.isDynamicTheme(getActivity())) {
                        final Bitmap bitmapPoster = ((BitmapDrawable) mBackdropImageView.getDrawable()).getBitmap()
                        Palette.from(bitmapPoster).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(final Palette p) {
                                LogDisplay.callLog(LOG_TAG, 'onGenerated is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                                final Palette.Swatch vibrantSwatch = p.getVibrantSwatch()
                                final Palette.Swatch lightVibrantSwatch = p.getLightVibrantSwatch()
                                final Palette.Swatch darkVibrantSwatch = p.getDarkVibrantSwatch()
                                final Palette.Swatch mutedSwatch = p.getMutedSwatch()
                                final Palette.Swatch mutedLightSwatch = p.getLightMutedSwatch()
                                final Palette.Swatch mutedDarkSwatch = p.getDarkMutedSwatch()
                                boolean pickSwatchColorFlag = false
                                //Pick primary, primaryDark, title and body text color
                                if (vibrantSwatch) {
                                    mPalletePrimaryColor = vibrantSwatch.getRgb()
                                    mPalleteTitleColor = vibrantSwatch.getTitleTextColor()
                                    mPalleteBodyTextColor = vibrantSwatch.getBodyTextColor()
                                    //Produce Dark color by changing the value (3rd parameter) of HSL value
                                    final float[] primaryHsl = vibrantSwatch.getHsl()
                                    primaryHsl[2] *= 0.9f
                                    mPalletePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                                    pickSwatchColorFlag = true
                                } else if (lightVibrantSwatch) { //Try another swatch
                                    mPalletePrimaryColor = lightVibrantSwatch.getRgb()
                                    mPalleteTitleColor = lightVibrantSwatch.getTitleTextColor()
                                    mPalleteBodyTextColor = lightVibrantSwatch.getBodyTextColor()
                                    //Produce Dark color by changing the value (3rd parameter) of HSL value
                                    final float[] primaryHsl = lightVibrantSwatch.getHsl()
                                    primaryHsl[2] *= 0.9f
                                    mPalletePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                                    pickSwatchColorFlag = true
                                } else if (darkVibrantSwatch) { //Try last swatch
                                    mPalletePrimaryColor = darkVibrantSwatch.getRgb()
                                    mPalleteTitleColor = darkVibrantSwatch.getTitleTextColor()
                                    mPalleteBodyTextColor = darkVibrantSwatch.getBodyTextColor()
                                    //Produce Dark color by changing the value (3rd parameter) of HSL value
                                    final float[] primaryHsl = darkVibrantSwatch.getHsl()
                                    primaryHsl[2] *= 0.9f
                                    mPalletePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                                    pickSwatchColorFlag = true
                                } else { //Fallback to default
                                    LogDisplay.callLog(LOG_TAG, 'onGenerated:not able to pick color, so fallback', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                                    mPalletePrimaryColor = ContextCompat.getColor(getActivity(), R.color.primary)
                                    mPalletePrimaryDarkColor = ContextCompat.getColor(getActivity(), R.color.primary_dark)
                                    mPalleteTitleColor = ContextCompat.getColor(getActivity(), R.color.primary_text)
                                    mPalleteBodyTextColor = ContextCompat.getColor(getActivity(), R.color.secondary_text)
                                    //This is needed as we are not going pick accent colour if falling back
                                    mPalleteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)
                                }
                                //Pick accent color only if Swatch color is picked, otherwise do not pick accent color
                                if (pickSwatchColorFlag) {
                                    if (mutedSwatch) {
                                        mPalleteAccentColor = mutedSwatch.getRgb()
                                    } else if (mutedLightSwatch) { //Try another swatch
                                        mPalleteAccentColor = mutedLightSwatch.getRgb()
                                    } else if (mutedDarkSwatch) { //Try last swatch
                                        mPalleteAccentColor = mutedDarkSwatch.getRgb()
                                    } else { //Fallback to default
                                        mPalleteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)
                                    }
                                }
                                // Change the color only if it's portrait mode and we are able to get hold of recyclerview,
                                // otherwise use default color
                                final View view = getView()
                                final AutoGridRecyclerView autoGridRecyclerView = view.findViewById(R.id.auto_grid_recycler_view) as AutoGridRecyclerView
                                if (autoGridRecyclerView) {
                                    LogDisplay.callLog(LOG_TAG, 'onGenerated:recycler view is NOT null!', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                        LogDisplay.callLog(LOG_TAG, 'onGenerated:portrait mode, so change color', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                                        autoGridRecyclerView.setBackgroundColor(mPalletePrimaryColor)
                                        mCollectionDetailLayout.setBackgroundColor(mPalletePrimaryColor)
                                        // For tablet paint with dark color as we use material card like UI
                                        // otherwise paint with primary color
                                        if(getResources().getBoolean(R.bool.is_tablet_port)) {
                                            mCollectionCoordLayout.setBackgroundColor(mPalletePrimaryDarkColor)
                                        } else {
                                            mCollectionCoordLayout.setBackgroundColor(mPalletePrimaryColor)
                                        }
                                        mCollectionTitleTextView.setTextColor(mPalleteBodyTextColor)
                                        mCollectionTitleTextView.setTextColor(mPalleteTitleColor)
                                        mCollectionOverviewTextView.setTextColor(mPalleteBodyTextColor)
                                        mCollectionOverviewTextViewHeader.setTextColor(mPalleteTitleColor)
                                        mCollapsingToolbar.setStatusBarScrimColor(mPalletePrimaryDarkColor)
                                        mCollapsingToolbar.setContentScrimColor(mPalletePrimaryColor)
                                        mCollapsingToolbar.setBackgroundColor(mPalletePrimaryColor)
                                        mCollapsingToolbar.setCollapsedTitleTextColor(mPalleteBodyTextColor)
                                    } else {
                                        LogDisplay.callLog(LOG_TAG, 'onGenerated:landscape mode, so do not change color', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                                        // Override the bodyTextColor, so that GridFragment uses this
                                        mPalleteBodyTextColor = ContextCompat.getColor(getActivity(), R.color.primary_text)
                                    }
                                    final MovieGridRecyclerAdapter movieGridRecyclerAdapter = autoGridRecyclerView.getAdapter() as MovieGridRecyclerAdapter
                                    // Grid color is changed irrespective of portrait or landscape
                                    movieGridRecyclerAdapter.changeColor(mPalletePrimaryDarkColor, mPalleteBodyTextColor)
                                } else {
                                    LogDisplay.callLog(LOG_TAG, 'onGenerated:recycler view is null, so use default color', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                                }
                            }
                        })
                    }
                }

                @Override
                void onError() {
                    LogDisplay.callLog(LOG_TAG, 'Picasso onError is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
                }
            }
            PicassoLoadImage.loadCollectionBackdropImage(getActivity(), mCollectionBackdropPath, mBackdropImageView, picassoCollectionImageCallback)
    }

    /**
     * Share the collection
     */
    protected void shareCollection() {
        LogDisplay.callLog(LOG_TAG, 'shareCollection is called', LogDisplay.COLLECTION_MOVIE_FRAGMENT_LOG_FLAG)
        getActivity().getIntent().removeExtra(Intent.EXTRA_TEXT)
        final String tmdbWebCollectionUrl = "$GlobalStaticVariables.TMDB_WEB_COLLECTION_BASE_URL${Integer.toString(mCollectionId)}"
        final Intent sendIntent = new Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, "$mCollectionName, TMDb link - $tmdbWebCollectionUrl #${getString(R.string.app_name)} app")
        sendIntent.setType("text/plain")
        // Create intent to show the chooser dialog
        final Intent chooser = Intent.createChooser(sendIntent, getString(R.string.collection_chooser_title))
        // Verify that the intent will resolve to an activity
        if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(chooser)
        }
    }
}