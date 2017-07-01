/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v17.leanback.widget.HorizontalGridView
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.*
import com.moviemagic.dpaul.android.app.adapter.PersonCastAdapter
import com.moviemagic.dpaul.android.app.adapter.PersonCrewAdapter
import com.moviemagic.dpaul.android.app.adapter.PersonImageAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.*
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class PersonMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = PersonMovieFragment.class.getSimpleName()

    private Uri mPersonInfoUri
    private int mPersonId
    private Toolbar mToolbar
    private String[] mPersonIdArg
    private AppBarLayout mAppBarLayout
    private ImageView mPosterImageView
    private TextView mNameHdrTextView, mNameTextView, mDobHdrTextView, mDobTextView, mBirthPlaceHdrTextView, mBirthPlaceTextView,
            mAlsoKnownAsHdrTextView, mAlsoKnownAsTextView, mDeathDayHdrTextView, mDeathDayTextView, mPopularityHdrTextView, mPopularityTextView,
            mBiographyHdrTextView, mBiographyTextView,  mCastGridHdrTextView, mCastGridEmptyMsgTextView, mCrewGridHdrTextView, mCrewGridEmptyMsgTextView,
            mImageGridHdrTextView, mImageGridEmptyMsgTextView, mWebLinksHdrTextView
    private HorizontalGridView mCastGridView, mCrewGridView, mImageGridView
    private Button mHomePageButton, mImdbLinkButton
    private ImageButton mShowBiographyImageButton, mHideBiographyImageButton
    private RelativeLayout mPersonRelLayout, mPersonTabletPortPosterDataRelLayout
    private PersonCastAdapter mPersonCastAdapter
    private PersonCrewAdapter mPersonCrewAdapter
    private PersonImageAdapter mPersonImageAdapter
    private String mPersonHomePageUrl, mPersonImdbId
    private View mDeathDayDivider, mAlsoKnownAsDivider
    private int mPalettePrimaryColor
    private int mPalettePrimaryDarkColor
    private int mPaletteTitleColor
    private int mPaletteBodyTextColor
    private int mPaletteAccentColor
    private GridLayoutManager mCrewGridLayoutManager
    private GridLayoutManager mCastGridLayoutManager
    private GridLayoutManager mImageGridLayoutManager
    private CallbackForCastClick mCallbackForCastClick
    private CallbackForCrewClick mCallbackForCrewClick
    private CallbackForImageClick mCallbackForImageClick
    private String mPersonName
    private String mPersonBackdropImage
    private CoordinatorLayout mCoordinatorLayout
    private RelativeLayout mTabletPortImageHolder
    private View mTabletPortImageBase
    private AppBarLayout.OnOffsetChangedListener mAppbarOnOffsetChangeListener
    private CollapsingToolbarLayout mCollapsingToolbar


    private static final int PERSON_MOVIE_FRAGMENT_PERSON_INFO_LOADER_ID = 0
    private static final int PERSON_MOVIE_FRAGMENT_PERSON_CAST_LOADER_ID = 1
    private static final int PERSON_MOVIE_FRAGMENT_PERSON_CREW_LOADER_ID = 2
    private static final int PERSON_MOVIE_FRAGMENT_PERSON_IMAGE_LOADER_ID = 3


    //Columns to fetch from movie_person_info table for similar movies
    private static final String[] PERSON_INFO_COLUMNS = [MovieMagicContract.MoviePersonInfo._ID,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ID,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PROFILE_PATH,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_NAME,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_BIRTHDAY,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_PLACE_OF_BIRTH,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ALSO_KNOWN_AS,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_DEATHDAY,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_BIOGRAPHY,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_HOMEPAGE,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_IMDB_ID,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_POPULARITY,
                                                         MovieMagicContract.MoviePersonInfo.COLUMN_PERSON_ADULT_FLAG]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_PERSON_INFO_ID = 0
    final static int COL_PERSON_INFO_PERSON_ID = 1
    final static int COL_PERSON_INFO_PROFILE_PATH = 2
    final static int COL_PERSON_INFO_PERSON_NAME = 3
    final static int COL_PERSON_INFO_PERSON_BIRTHDAY = 4
    final static int COL_PERSON_INFO_PERSON_PLACE_OF_BIRTH = 5
    final static int COL_PERSON_INFO_PERSON_ALSO_KNOWN_AS = 6
    final static int COL_PERSON_INFO_PERSON_DEATH_DAY = 7
    final static int COL_PERSON_INFO_PERSON_BIOGRAPHY = 8
    final static int COL_PERSON_INFO_PERSON_HOMEPAGE = 9
    final static int COL_PERSON_INFO_PERSON_IMDB_ID = 10
    final static int COL_PERSON_INFO_PERSON_POPULARITY = 11
    final static int COL_PERSON_INFO_PERSON_ADULT_FLAG = 12

    //Columns to fetch from movie_person_cast table for similar movies
    private static final String[] PERSON_CAST_COLUMNS = [MovieMagicContract.MoviePersonCast._ID,
                                                         MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID,
                                                         MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_POSTER_PATH,
                                                         MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_MOVIE_ID,
                                                         MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_TITLE,
                                                         MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_CHARACTER,
                                                         MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_RELEASE_DATE,
                                                         MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ADULT_FLAG]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_PERSON_CAST_ID = 0
    final static int COL_PERSON_CAST_ORIG_PERSON_ID = 1
    final static int COL_PERSON_CAST_MOVIE_POSTER_PATH = 2
    final static int COL_PERSON_CAST_MOVIE_ID = 3
    final static int COL_PERSON_CAST_MOVIE_TITLE = 4
    final static int COL_PERSON_CAST_CHARACTER_NAME = 5
    final static int COL_PERSON_CAST_MOVIE_RELEASE_DATE = 6
    final static int COL_PERSON_CAST_AUDULT_FLAG = 7

    //Columns to fetch from movie_person_crew table for similar movies
    private static final String[] PERSON_CREW_COLUMNS = [MovieMagicContract.MoviePersonCrew._ID,
                                                         MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID,
                                                         MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_POSTER_PATH,
                                                         MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_MOVIE_ID,
                                                         MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_TITLE,
                                                         MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_JOB,
                                                         MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_RELEASE_DATE,
                                                         MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ADULT_FLAG]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_PERSON_CREW_ID = 0
    final static int COL_PERSON_CREW_ORIG_PERSON_ID = 1
    final static int COL_PERSON_CREW_MOVIE_POSTER_PATH = 2
    final static int COL_PERSON_CREW_MOVIE_ID = 3
    final static int COL_PERSON_CREW_MOVIE_TITLE = 4
    final static int COL_PERSON_CREW_JOB_NAME = 5
    final static int COL_PERSON_CREW_MOVIE_RELEASE_DATE = 6
    final static int COL_PERSON_CREW_AUDULT_FLAG = 7

    //Columns to fetch from movie_person_image table for similar movies
    private static final String[] PERSON_IMAGE_COLUMNS = [MovieMagicContract.MoviePersonImage._ID,
                                                         MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID,
                                                         MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_FILE_PATH,
                                                         MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_VOTE_AVERAGE,
                                                         MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_VOTE_COUNT,
                                                         MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ISO_639_1]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_PERSON_IMAGE_ID = 0
    final static int COL_PERSON_IMAGE_ORIG_PERSON_ID = 1
    final static int COL_PERSON_IMAGE_FILE_PATH = 2
    final static int COL_PERSON_IMAGE_VOTE_AVG = 3
    final static int COL_PERSON_IMAGE_VOTE_COUNT = 4
    final static int COL_PERSON_IMAGE_VOTE_ISO = 5

    //An empty constructor is needed so that lifecycle is properly handled
    public PersonMovieFragment() {
        LogDisplay.callLog(LOG_TAG,'PersonMovieFragment empty constructor is called',LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
    }

    @Override
    public void onAttach(final Context context) {
        LogDisplay.callLog(LOG_TAG,'onAttach is called',LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if(context instanceof AppCompatActivity) {
                mCallbackForCastClick = (CallbackForCastClick) context
            }
        } catch (final ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CallbackForCastClick interface")
        }
        try {
            if(context instanceof AppCompatActivity) {
                mCallbackForCrewClick = (CallbackForCrewClick) context
            }
        } catch (final ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CallbackForCrewClick interface")
        }
        try {
            if(context instanceof AppCompatActivity) {
                mCallbackForImageClick = (CallbackForImageClick) context
            }
        } catch (final ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CallbackForImageClick interface")
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreate is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        super.onCreate(savedInstanceState)
        //Following line needed to let android know that Fragment has options menu
        //If this line is not added then associated method (e.g. OnCreateOptionsMenu) does not get supported
        //even in auto code completion
        setHasOptionsMenu(true)
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Press appbar back button to go to previous activity
            getActivity().finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreateView is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        // Get the bundle from the Fragment
        final Bundle args = getArguments()
        if (args) {
            mPersonId = args.getInt(GlobalStaticVariables.MOVIE_PERSON_ID)
            mPersonBackdropImage = args.getString(GlobalStaticVariables.MOVIE_PERSON_BACKDROP_PATH)
            LogDisplay.callLog(LOG_TAG, "Person Fragment arguments.Person id -> $mPersonId", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Person Fragment arguments.Backdrop Image -> $mPersonBackdropImage", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            mPersonInfoUri = MovieMagicContract.MoviePersonInfo.buildMoviePersonInfoUriWithPersonId(mPersonId)
        }
        // Inflate the view before referring any view using id
        final View mRootView = inflater.inflate(R.layout.fragment_person_movie, container, false)
        mAppBarLayout = mRootView.findViewById(R.id.person_app_bar_layout) as AppBarLayout
        mPersonRelLayout = mRootView.findViewById(R.id.person_info_layout) as RelativeLayout
        mPersonTabletPortPosterDataRelLayout = mRootView.findViewById(R.id.person_poster_data_section) as RelativeLayout
        mCoordinatorLayout = mRootView.findViewById(R.id.person_coordinator_layout) as CoordinatorLayout
        mTabletPortImageHolder = mRootView.findViewById(R.id.person_poster_image_section) as RelativeLayout
        mTabletPortImageBase = mRootView.findViewById(R.id.person_poster_port_image_base) as View
        mPosterImageView = mRootView.findViewById(R.id.person_poster_image) as ImageView
        mNameHdrTextView = mRootView.findViewById(R.id.person_name_header) as TextView
        mNameTextView = mRootView.findViewById(R.id.person_name) as TextView
        mDobHdrTextView = mRootView.findViewById(R.id.person_dob_header) as TextView
        mDobTextView = mRootView.findViewById(R.id.person_dob) as TextView
        mBirthPlaceHdrTextView = mRootView.findViewById(R.id.person_place_of_birth_header) as TextView
        mBirthPlaceTextView = mRootView.findViewById(R.id.person_place_of_birth) as TextView
        mAlsoKnownAsDivider = mRootView.findViewById(R.id.person_also_known_as_divider) as View
        mAlsoKnownAsHdrTextView = mRootView.findViewById(R.id.person_also_known_as_header) as TextView
        mAlsoKnownAsTextView = mRootView.findViewById(R.id.person_also_known_as) as TextView
        mDeathDayHdrTextView = mRootView.findViewById(R.id.person_death_day_header) as TextView
        mDeathDayTextView = mRootView.findViewById(R.id.person_death_day) as TextView
        mDeathDayDivider = mRootView.findViewById(R.id.person_death_day_divider) as View
        mPopularityHdrTextView = mRootView.findViewById(R.id.person_popularity_header) as TextView
        mPopularityTextView = mRootView.findViewById(R.id.person_popularity) as TextView
        mBiographyHdrTextView = mRootView.findViewById(R.id.person_biography_header) as TextView
        mBiographyTextView = mRootView.findViewById(R.id.person_biography) as TextView
        /**
         * Biography show button handling
         */
        mShowBiographyImageButton = mRootView.findViewById(R.id.person_biography_show_button) as ImageButton
        mShowBiographyImageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                mShowBiographyImageButton.setVisibility(Button.INVISIBLE)
                mHideBiographyImageButton.setVisibility(Button.VISIBLE)
                mBiographyTextView.setMaxLines(Integer.MAX_VALUE)
            }
        })
        /**
         * Biography hide button handling
         */
        mHideBiographyImageButton = mRootView.findViewById(R.id.person_biography_hide_button) as ImageButton
        mHideBiographyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                mShowBiographyImageButton.setVisibility(Button.VISIBLE)
                mHideBiographyImageButton.setVisibility(Button.INVISIBLE)
                mBiographyTextView.setMaxLines(getActivity().getResources().getString(R.string.person_biography_collapse_line_item_count) as Integer)
            }
        })
        /**
         * Person Cast Grid handling
         */
        mCastGridHdrTextView = mRootView.findViewById(R.id.person_cast_grid_header) as TextView
        mCastGridView = mRootView.findViewById(R.id.person_cast_grid) as HorizontalGridView
        mCastGridEmptyMsgTextView = mRootView.findViewById(R.id.person_cast_grid_empty_msg_text_view) as TextView
        mCastGridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mCastGridView.setLayoutManager(mCastGridLayoutManager)
        //Create a new interface member variable for PersonCastAdapterOnClickHandler and the same is passed as
        //parameter to Adapter, this onClick method is called whenever onClick is called from PersonCastAdapter
        mPersonCastAdapter = new PersonCastAdapter(getActivity(),mCastGridEmptyMsgTextView,
                new PersonCastAdapter.PersonCastAdapterOnClickHandler(){
                    @Override
                    void onClick(final int movieId, final PersonCastAdapter.PersonCastAdapterViewHolder viewHolder) {
                        mCallbackForCastClick.onCastMovieItemSelected(movieId,viewHolder)
                    }
                })
        mCastGridView.setAdapter(mPersonCastAdapter)
        /**
         * Person Crew Grid handling
         */
        mCrewGridHdrTextView = mRootView.findViewById(R.id.person_crew_grid_header) as TextView
        mCrewGridView = mRootView.findViewById(R.id.person_crew_grid) as HorizontalGridView
        mCrewGridEmptyMsgTextView = mRootView.findViewById(R.id.person_crew_grid_empty_msg_text_view) as TextView
        mCrewGridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mCrewGridView.setLayoutManager(mCrewGridLayoutManager)
        mPersonCrewAdapter = new PersonCrewAdapter(getActivity(), mCrewGridEmptyMsgTextView,
                new PersonCrewAdapter.PersonCrewAdapterOnClickHandler(){
                    @Override
                    void onClick(final int movieId, final PersonCrewAdapter.PersonCrewAdapterViewHolder viewHolder) {
                        mCallbackForCrewClick.onCrewMovieItemSelected(movieId,viewHolder)
                    }
                })
        mCrewGridView.setAdapter(mPersonCrewAdapter)
        /**
         * Person Image Grid handling
         */
        mImageGridHdrTextView = mRootView.findViewById(R.id.person_image_grid_header) as TextView
        mImageGridView = mRootView.findViewById(R.id.person_image_grid) as HorizontalGridView
        mImageGridEmptyMsgTextView = mRootView.findViewById(R.id.person_image_grid_empty_msg_text_view) as TextView
        mImageGridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mImageGridView.setLayoutManager(mImageGridLayoutManager)
        mPersonImageAdapter = new PersonImageAdapter(getActivity(), mImageGridEmptyMsgTextView,
                new PersonImageAdapter.PersonImageAdapterOnClickHandler(){
                    @Override
                    void onClick(
                            final int adapterPosition, final String[] imageFilePath, final PersonImageAdapter.PersonImageAdapterViewHolder viewHolder) {
                        mCallbackForImageClick.onImageMovieItemSelected(mPersonName, adapterPosition, imageFilePath, viewHolder)
                    }
                })
        mImageGridView.setAdapter(mPersonImageAdapter)
        mWebLinksHdrTextView = mRootView.findViewById(R.id.person_web_links_header) as TextView
        /**
         * External web link button handling
         */
        mHomePageButton = mRootView.findViewById(R.id.person_web_links_home_page_button) as Button
        mHomePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'Home Page Button is clicked', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                startHomePageIntent()
            }
        })
        mImdbLinkButton = mRootView.findViewById(R.id.person_web_links_imdb_link_button) as Button
        mImdbLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'IMDb Button is clicked', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                startImdbIntent()
            }
        })

        return mRootView
    }

    @Override
    void onActivityCreated(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onActivityCreated is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        super.onActivityCreated(savedInstanceState)
        final AppCompatActivity appCompatActivity = getActivity() as AppCompatActivity
        mToolbar = getView().findViewById(R.id.person_toolbar) as Toolbar
        if (mToolbar) {
            appCompatActivity.setSupportActionBar(mToolbar)
            //Enable back to home button
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        }

        mCollapsingToolbar = getView().findViewById(R.id.person_collapsing_toolbar) as CollapsingToolbarLayout
        if (mCollapsingToolbar) {
            //Just clear off to be on the safe side
            mCollapsingToolbar.setTitle(" ")
        }

        if (mPersonId) {
            mPersonIdArg = [Integer.toString(mPersonId)] as String[]
        } else {
            // This is to safeguard any unwanted data fetch
            mPersonIdArg = ['ZZZZZZ'] as String[]
        }

        //If it's a fresh start then call init loader
        if(savedInstanceState == null) {
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:first time, so init loaders', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().initLoader(PERSON_MOVIE_FRAGMENT_PERSON_INFO_LOADER_ID, null, this)
            getLoaderManager().initLoader(PERSON_MOVIE_FRAGMENT_PERSON_CAST_LOADER_ID, null, this)
            getLoaderManager().initLoader(PERSON_MOVIE_FRAGMENT_PERSON_CREW_LOADER_ID, null, this)
            getLoaderManager().initLoader(PERSON_MOVIE_FRAGMENT_PERSON_IMAGE_LOADER_ID, null, this)
        } else {        //If it's restore then restart the loader
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:not first time, so restart loaders', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().restartLoader(PERSON_MOVIE_FRAGMENT_PERSON_INFO_LOADER_ID, null, this)
            getLoaderManager().restartLoader(PERSON_MOVIE_FRAGMENT_PERSON_CAST_LOADER_ID, null, this)
            getLoaderManager().restartLoader(PERSON_MOVIE_FRAGMENT_PERSON_CREW_LOADER_ID, null, this)
            getLoaderManager().restartLoader(PERSON_MOVIE_FRAGMENT_PERSON_IMAGE_LOADER_ID, null, this)
        }
    }

    @Override
    void onStart() {
        LogDisplay.callLog(LOG_TAG, 'onStart is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        super.onStart()
        // Check if the user is online or not, if not then show a message
        final boolean isOnline = Utility.isOnline(getActivity().getApplicationContext())
        if(!isOnline) {
            Snackbar.make(mAppBarLayout, getString(R.string.no_internet_connection_message), Snackbar.LENGTH_LONG).show()
        } else if(Utility.isOnlyWifi(getActivity().getApplicationContext()) & !GlobalStaticVariables.WIFI_CONNECTED) {
            // If user has selected only WiFi but user is online without WiFi then show a dialog
            Snackbar.make(mAppBarLayout, getString(R.string.internet_connection_without_wifi_message), Snackbar.LENGTH_LONG).show()
        } else if (Utility.isReducedDataOn(getActivity())) {
            // If user has selected reduced data
            Snackbar.make(mAppBarLayout, getString(R.string.reduced_data_use_on_message), Snackbar.LENGTH_LONG).show()
        }

        //Show the title only when image is collapsed
        mAppbarOnOffsetChangeListener = new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false
            int scrollRange = -1
            @Override
            void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                LogDisplay.callLog(LOG_TAG, 'onOffsetChanged is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange()
                }
                LogDisplay.callLog(LOG_TAG, "scrollRange + verticalOffset:$scrollRange & $verticalOffset", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(mPersonName)
                    isShow = true
                } else if (isShow) {
                    mCollapsingToolbar.setTitle(" ")
                    isShow = false
                }
            }
        }
    }

    @Override
    Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        LogDisplay.callLog(LOG_TAG, "onCreateLoader.loader id->$id", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        switch (id) {
            case PERSON_MOVIE_FRAGMENT_PERSON_INFO_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                           //Parent Activity Context
                        mPersonInfoUri,                          //Uri of the person info id
                        PERSON_INFO_COLUMNS,                     //Projection to return
                        null,                                    //null as used person info id Uri
                        null,                                    //null as used person info id Uri
                        null)                                    //Only a single row is expected, so not sorted
            case PERSON_MOVIE_FRAGMENT_PERSON_CAST_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                                //Parent Activity Context
                        MovieMagicContract.MoviePersonCast.CONTENT_URI,                               //Uri of the person info id
                        PERSON_CAST_COLUMNS,                                                          //Projection to return
                        "$MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID = ?",  //Section Clause
                        mPersonIdArg,                                                                 //Selection Arguments
                        "$MovieMagicContract.MoviePersonCast.COLUMN_PERSON_CAST_POSTER_PATH desc")           //Sort on release date
            case PERSON_MOVIE_FRAGMENT_PERSON_CREW_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                                //Parent Activity Context
                        MovieMagicContract.MoviePersonCrew.CONTENT_URI,                               //Uri of the person info id
                        PERSON_CREW_COLUMNS,                                                          //Projection to return
                        "$MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID = ?",  //Section Clause
                        mPersonIdArg,                                                                 //Selection Arguments
                        "$MovieMagicContract.MoviePersonCrew.COLUMN_PERSON_CREW_POSTER_PATH desc")           //Sort on release date
            case PERSON_MOVIE_FRAGMENT_PERSON_IMAGE_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                                //Parent Activity Context
                        MovieMagicContract.MoviePersonImage.CONTENT_URI,                              //Uri of the person info id
                        PERSON_IMAGE_COLUMNS,                                                          //Projection to return
                        "$MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID = ?", //Section Clause
                        mPersonIdArg,                                                                 //Selection Arguments
                        "$MovieMagicContract.MoviePersonImage.COLUMN_PERSON_IMAGE_VOTE_AVERAGE desc")   //Sort on release date
            default:
                return null
        }
    }

    @Override
    void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        final int loaderId = loader.getId()
        LogDisplay.callLog(LOG_TAG, "onLoadFinished.loader id->$loaderId", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        switch (loaderId) {
            case PERSON_MOVIE_FRAGMENT_PERSON_INFO_LOADER_ID:
                handlePersonInfoOnLoadFinished(data)
                break
            case PERSON_MOVIE_FRAGMENT_PERSON_CAST_LOADER_ID:
                handlePersonCastOnLoadFinished(data)
                break
            case PERSON_MOVIE_FRAGMENT_PERSON_CREW_LOADER_ID:
                handlePersonCrewOnLoadFinished(data)
                break
            case PERSON_MOVIE_FRAGMENT_PERSON_IMAGE_LOADER_ID:
                handlePersonImageOnLoadFinished(data)
                break
            default:
                LogDisplay.callLog(LOG_TAG, "Unknown loader id. id->$loaderId", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }

    @Override
    void onLoaderReset(final Loader<Cursor> loader) {
        //Reset the adapter
        LogDisplay.callLog(LOG_TAG, 'onLoaderReset is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        mPersonCastAdapter.swapCursor(null)
        mPersonCrewAdapter.swapCursor(null)
        mPersonImageAdapter.swapCursor(null)
    }

    /**
     * This method is called loader is finished for movie person info table
     * @param data Cursor
     */
    void handlePersonInfoOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handlePersonInfoOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        if(data.moveToFirst()) {
            LogDisplay.callLog(LOG_TAG, "handlePersonInfoOnLoadFinished.Data present for person id $mPersonId", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                    "${data.getString(COL_PERSON_INFO_PROFILE_PATH)}"
            final Callback picassoPosterCallback = new Callback() {
                @Override
                void onSuccess() {
                    LogDisplay.callLog(LOG_TAG, 'Picasso onSuccess is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                    // If user does not select dynamic theme (default value) then do not change the color
                    if (Utility.isDynamicTheme(getActivity())) {
                        final Bitmap bitmapPoster = ((BitmapDrawable) mPosterImageView.getDrawable()).getBitmap()
                        Palette.from(bitmapPoster).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(final Palette p) {
                                LogDisplay.callLog(LOG_TAG, 'onGenerated is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                                final Palette.Swatch vibrantSwatch = p.getVibrantSwatch()
                                final Palette.Swatch lightVibrantSwatch = p.getLightVibrantSwatch()
                                final Palette.Swatch darkVibrantSwatch = p.getDarkVibrantSwatch()
                                final Palette.Swatch mutedSwatch = p.getMutedSwatch()
                                final Palette.Swatch mutedLightSwatch = p.getLightMutedSwatch()
                                final Palette.Swatch mutedDarkSwatch = p.getDarkMutedSwatch()
                                boolean pickSwatchColorFlag = false
                                //Pick primary, primaryDark, title and body text color
                                if (vibrantSwatch) {
                                    mPalettePrimaryColor = vibrantSwatch.getRgb()
                                    mPaletteTitleColor = vibrantSwatch.getTitleTextColor()
                                    mPaletteBodyTextColor = vibrantSwatch.getBodyTextColor()
                                    //Produce Dark color by changing the value (3rd parameter) of HSL value
                                    final float[] primaryHsl = vibrantSwatch.getHsl()
                                    primaryHsl[2] *= 0.9f
                                    mPalettePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                                    pickSwatchColorFlag = true
                                } else if (lightVibrantSwatch) { //Try another swatch
                                    mPalettePrimaryColor = lightVibrantSwatch.getRgb()
                                    mPaletteTitleColor = lightVibrantSwatch.getTitleTextColor()
                                    mPaletteBodyTextColor = lightVibrantSwatch.getBodyTextColor()
                                    //Produce Dark color by changing the value (3rd parameter) of HSL value
                                    final float[] primaryHsl = lightVibrantSwatch.getHsl()
                                    primaryHsl[2] *= 0.9f
                                    mPalettePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                                    pickSwatchColorFlag = true
                                } else if (darkVibrantSwatch) { //Try last swatch
                                    mPalettePrimaryColor = darkVibrantSwatch.getRgb()
                                    mPaletteTitleColor = darkVibrantSwatch.getTitleTextColor()
                                    mPaletteBodyTextColor = darkVibrantSwatch.getBodyTextColor()
                                    //Produce Dark color by changing the value (3rd parameter) of HSL value
                                    final float[] primaryHsl = darkVibrantSwatch.getHsl()
                                    primaryHsl[2] *= 0.9f
                                    mPalettePrimaryDarkColor = Color.HSVToColor(primaryHsl)
                                    pickSwatchColorFlag = true
                                } else { //Fallback to default
                                    LogDisplay.callLog(LOG_TAG, 'onGenerated:not able to pick color, so fallback', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                                    mPalettePrimaryColor = ContextCompat.getColor(getActivity(), R.color.primary)
                                    mPalettePrimaryDarkColor = ContextCompat.getColor(getActivity(), R.color.primary_dark)
                                    mPaletteTitleColor = ContextCompat.getColor(getActivity(), R.color.primary_text)
                                    mPaletteBodyTextColor = ContextCompat.getColor(getActivity(), R.color.secondary_text)
                                    //This is needed as we are not going pick accent colour if falling back
                                    mPaletteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)
                                }
                                //Pick accent color only if Swatch color is picked, otherwise do not pick accent color
                                if (pickSwatchColorFlag) {
                                    if (mutedSwatch) {
                                        mPaletteAccentColor = mutedSwatch.getRgb()
                                    } else if (mutedLightSwatch) { //Try another swatch
                                        mPaletteAccentColor = mutedLightSwatch.getRgb()
                                    } else if (mutedDarkSwatch) { //Try last swatch
                                        mPaletteAccentColor = mutedDarkSwatch.getRgb()
                                    } else { //Fallback to default
                                        mPaletteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)
                                    }
                                }
                                //Apply color to fields in portrait mode only
                                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    mPersonRelLayout.setBackgroundColor(mPalettePrimaryColor)
                                    mNameHdrTextView.setTextColor(mPaletteTitleColor)
                                    mNameTextView.setTextColor(mPaletteBodyTextColor)
                                    mDobHdrTextView.setTextColor(mPaletteTitleColor)
                                    mDobTextView.setTextColor(mPaletteBodyTextColor)
                                    mBirthPlaceHdrTextView.setTextColor(mPaletteTitleColor)
                                    mBirthPlaceTextView.setTextColor(mPaletteBodyTextColor)
                                    mAlsoKnownAsHdrTextView.setTextColor(mPaletteTitleColor)
                                    mAlsoKnownAsTextView.setTextColor(mPaletteBodyTextColor)
                                    mDeathDayHdrTextView.setTextColor(mPaletteTitleColor)
                                    mDeathDayTextView.setTextColor(mPaletteBodyTextColor)
                                    mPopularityHdrTextView.setTextColor(mPaletteTitleColor)
                                    mPopularityTextView.setTextColor(mPaletteBodyTextColor)
                                    mBiographyHdrTextView.setTextColor(mPaletteTitleColor)
                                    mBiographyTextView.setTextColor(mPaletteBodyTextColor)
                                    mCastGridHdrTextView.setTextColor(mPaletteTitleColor)
                                    mCrewGridHdrTextView.setTextColor(mPaletteTitleColor)
                                    mImageGridHdrTextView.setTextColor(mPaletteTitleColor)
                                    mWebLinksHdrTextView.setTextColor(mPaletteTitleColor)
                                    mCoordinatorLayout.setBackgroundColor(mPalettePrimaryDarkColor)
                                    mTabletPortImageBase.setBackgroundColor(mPalettePrimaryColor)
                                    mPersonTabletPortPosterDataRelLayout.setBackgroundColor(mPalettePrimaryColor)
                                    mCollapsingToolbar.setStatusBarScrimColor(mPalettePrimaryDarkColor)
                                    mCollapsingToolbar.setContentScrimColor(mPalettePrimaryColor)
                                    mCollapsingToolbar.setBackgroundColor(mPalettePrimaryColor)
                                    mCollapsingToolbar.setCollapsedTitleTextColor(mPaletteBodyTextColor)
                                }

                                //Apply color to adapter elements irrespective of portrait or landscape mode
                                mPersonCastAdapter.changeColor(mPalettePrimaryDarkColor, mPaletteBodyTextColor)
                                mPersonCrewAdapter.changeColor(mPalettePrimaryDarkColor, mPaletteBodyTextColor)
                                mPersonImageAdapter.changeColor(mPalettePrimaryDarkColor, mPaletteBodyTextColor)
                            }
                        })
                    }
                }

                @Override
                void onError() {
                    LogDisplay.callLog(LOG_TAG, 'Picasso onError is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                }
            }
            PicassoLoadImage.loadDetailFragmentPosterImage(getActivity(),posterPath,mPosterImageView,picassoPosterCallback)
            mPersonName = data.getString(COL_PERSON_INFO_PERSON_NAME)
            // Set the backdrop image
            final ImageView imageView = getView().findViewById(R.id.person_backdrop_image) as ImageView
            if(mPersonBackdropImage) {
                final String imagePath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
                        "$mPersonBackdropImage"
                        LogDisplay.callLog(LOG_TAG, "Person backdrop imagePath-> $imagePath", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                        PicassoLoadImage.loadDetailFragmentPagerAdapterImage(getActivity(), imagePath, imageView)
            } else {
                        LogDisplay.callLog(LOG_TAG, 'Person backdrop image path is null', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
                    }
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // Portrait
                    // Set the onscroll listener
                    mAppBarLayout.addOnOffsetChangedListener(mAppbarOnOffsetChangeListener)
            } else { // Landscape
                // Remove the Collapsing toolbar title
                mCollapsingToolbar.setTitleEnabled(false)
                mToolbar.setTitle(mPersonName)
                // Set a bigger text size & White color for Toolbar in landscape mode
                mToolbar.setTitleTextAppearance(getActivity(),R.style.TextAppearance_AppCompat_Headline)
                mToolbar.setTitleTextColor(ContextCompat.getColor(getContext(), R.color.primary_text))
            }

            mNameTextView.setText(data.getString(COL_PERSON_INFO_PERSON_NAME))
            if(data.getString(COL_PERSON_INFO_PERSON_BIRTHDAY)) {
                mDobTextView.setText(Utility.formatFriendlyDate(data.getString(COL_PERSON_INFO_PERSON_BIRTHDAY)))
            } else {
                mDobTextView.setText(getString(R.string.movie_data_not_available))
            }
            if(data.getString(COL_PERSON_INFO_PERSON_PLACE_OF_BIRTH)) {
                mBirthPlaceTextView.setText(data.getString(COL_PERSON_INFO_PERSON_PLACE_OF_BIRTH))
            } else {
                mBirthPlaceTextView.setText(getString(R.string.movie_data_not_available))
            }
            if(data.getString(COL_PERSON_INFO_PERSON_ALSO_KNOWN_AS)) {
                mAlsoKnownAsHdrTextView.setVisibility(TextView.VISIBLE)
                mAlsoKnownAsTextView.setVisibility(TextView.VISIBLE)
                mAlsoKnownAsTextView.setText(data.getString(COL_PERSON_INFO_PERSON_ALSO_KNOWN_AS))
                mAlsoKnownAsDivider.setVisibility(View.VISIBLE)
            } else {
                mAlsoKnownAsHdrTextView.setVisibility(TextView.GONE)
                mAlsoKnownAsTextView.setVisibility(TextView.GONE)
                mAlsoKnownAsDivider.setVisibility(View.GONE)
            }
            if(data.getString(COL_PERSON_INFO_PERSON_DEATH_DAY)) {
                mDeathDayHdrTextView.setVisibility(TextView.VISIBLE)
                mDeathDayTextView.setVisibility(TextView.VISIBLE)
                mDeathDayTextView.setText(Utility.formatFriendlyDate(data.getString(COL_PERSON_INFO_PERSON_DEATH_DAY)))
                mDeathDayDivider.setVisibility(View.VISIBLE)
            } else {
                mDeathDayHdrTextView.setVisibility(TextView.GONE)
                mDeathDayTextView.setVisibility(TextView.GONE)
                mDeathDayDivider.setVisibility(View.GONE)
            }
            LogDisplay.callLog(LOG_TAG, "popularity:${data.getFloat(COL_PERSON_INFO_PERSON_POPULARITY)}", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            final BigDecimal popularityBigDecimal = new BigDecimal(Float.toString(data.getFloat(COL_PERSON_INFO_PERSON_POPULARITY)))
            popularityBigDecimal = popularityBigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP)
            LogDisplay.callLog(LOG_TAG, "popularityBigDecimal:$popularityBigDecimal", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            if (data.getFloat(COL_PERSON_INFO_PERSON_POPULARITY) > 0) {
                mPopularityTextView.setText(Float.toString(popularityBigDecimal as Float))
            } else {
                mPopularityTextView.setText(getActivity().getString(R.string.movie_data_not_available))
            }
            //Remove all empty lines, blanks and tabs from biography text
            if(data.getString(COL_PERSON_INFO_PERSON_BIOGRAPHY)) {
                final String contentText = data.getString(COL_PERSON_INFO_PERSON_BIOGRAPHY).replaceAll("\n", "")
                mBiographyTextView.setText(contentText)
            } else {
                mBiographyTextView.setText(getString(R.string.movie_data_not_available))
            }
            LogDisplay.callLog(LOG_TAG, "homePage:${data.getString(COL_PERSON_INFO_PERSON_HOMEPAGE)}", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            if (data.getString(COL_PERSON_INFO_PERSON_HOMEPAGE)) {
                mPersonHomePageUrl = data.getString(COL_PERSON_INFO_PERSON_HOMEPAGE)
                mHomePageButton.setText(getActivity().getString(R.string.person_web_links_home_page))
                mHomePageButton.setClickable(true)
                mHomePageButton.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                if (Build.VERSION.SDK_INT >= 21) { // API number 21 -> LOLLIPOP
                    mHomePageButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION)
                }
            } else {
                mHomePageButton.setText(getActivity().getString(R.string.movie_detail_web_links_home_page_not_available))
                mHomePageButton.setClickable(false)
                mHomePageButton.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                if (Build.VERSION.SDK_INT >= 21) { // API number 21 -> LOLLIPOP
                    mHomePageButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION_RESET)
                }
            }
            LogDisplay.callLog(LOG_TAG, "IMDb ID:${data.getString(COL_PERSON_INFO_PERSON_IMDB_ID)}", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            if (data.getString(COL_PERSON_INFO_PERSON_IMDB_ID)) {
                mPersonImdbId = data.getString(COL_PERSON_INFO_PERSON_IMDB_ID)
                mImdbLinkButton.setText(getActivity().getString(R.string.detail_web_links_imdb_link))
                mImdbLinkButton.setClickable(true)
                mImdbLinkButton.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                if (Build.VERSION.SDK_INT >= 21) { // API number 21 -> LOLLIPOP
                    mImdbLinkButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION)
                }
            } else {
                mImdbLinkButton.setText(getActivity().getString(R.string.movie_detail_web_links_imdb_link_not_available))
                mImdbLinkButton.setClickable(false)
                mImdbLinkButton.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                if (Build.VERSION.SDK_INT >= 21) { // API number 21 -> LOLLIPOP
                    mImdbLinkButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION_RESET)
                }
            }
        } else {
            //Load the person info and associated tables
            LogDisplay.callLog(LOG_TAG, "handlePersonInfoOnLoadFinished.Data not present for person id $mPersonId, go and fetch it", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            if(Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                new LoadPersonData(getActivity()).execute([mPersonId] as Integer[])
            } else {
                LogDisplay.callLog(LOG_TAG, 'Device is offline or connected to internet without WiFi and user selected download only on WiFi', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
            }
        }
    }

    /**
     * This method is called loader is finished for movie person cast table
     * @param data Cursor
     */
    void handlePersonCastOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handlePersonCastOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        //Show two rows if the count is greater than 6 otherwise show single row
        if(data.count >= 10) {
            mCastGridLayoutManager.setSpanCount(2)
        }
        mPersonCastAdapter.swapCursor(data)
    }

    /**
     * This method is called loader is finished for movie person crew table
     * @param data Cursor
     */
    void handlePersonCrewOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handlePersonCrewOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        //Show two rows if the count is greater than 6 otherwise show single row
        if(data.count >= 10) {
            mCrewGridLayoutManager.setSpanCount(2)
        }
        mPersonCrewAdapter.swapCursor(data)
    }

    /**
     * This method is called loader is finished for movie person image table
     * @param data Cursor
     */
    void handlePersonImageOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handlePersonImageOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        //Show two rows if the count is greater than 6 otherwise show single row
        if(data.count >= 10) {
            mImageGridLayoutManager.setSpanCount(2)
        }
        mPersonImageAdapter.swapCursor(data)
    }
    /**
     * Intent to open a web browser when user clicks on person home page button
     */
    void startHomePageIntent() {
        if(mPersonHomePageUrl) {
            final Intent intent = new Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(mPersonHomePageUrl))
            startActivity(intent)
        }
    }

    /**
     * Intent to open the movie in imdb app(if installed) or in web browser when user clicks on imdb button
     */
    void startImdbIntent() {
        if(mPersonImdbId) {
            final String imdbUrl = "$GlobalStaticVariables.IMDB_BASE_PERSON_URL$mPersonImdbId/"
            final Intent intent = new Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(imdbUrl))
            startActivity(intent)
        }
    }

    @Override
    void onResume() {
        LogDisplay.callLog(LOG_TAG, 'onResume is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        super.onResume()
    }

    @Override
    void onPause() {
        LogDisplay.callLog(LOG_TAG, 'onPause is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        super.onPause()
    }

    @Override
    void onStop() {
        LogDisplay.callLog(LOG_TAG, 'onStop is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        super.onStop()
        Picasso.with(getActivity()).cancelRequest(mPosterImageView)
    }

    @Override
    void onDestroyView() {
        LogDisplay.callLog(LOG_TAG, 'onDestroyView is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        // Remove listeners
        mAppBarLayout.removeOnOffsetChangedListener(mAppbarOnOffsetChangeListener)
        super.onDestroyView()
    }

    @Override
    void onDestroy() {
        LogDisplay.callLog(LOG_TAG, 'onDestroy is called', LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        super.onDestroy()
    }

    @Override
    void onDetach() {
        LogDisplay.callLog(LOG_TAG,'onDetach is called',LogDisplay.PERSON_MOVIE_FRAGMENT_LOG_FLAG)
        super.onDetach()
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of person cast movie
     * item click.
     */
    public interface CallbackForCastClick {
        /**
         * PersonCastFragmentCallback when a movie item has been clicked for cast.
         */
        public void onCastMovieItemSelected(int movieId, PersonCastAdapter.PersonCastAdapterViewHolder viewHolder)
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of person crew movie
     * item click.
     */
    public interface CallbackForCrewClick {
        /**
         * PersonCrewFragmentCallback when a movie item has been clicked for crew.
         */
        public void onCrewMovieItemSelected(int movieId, PersonCrewAdapter.PersonCrewAdapterViewHolder viewHolder)
    }


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of person image
     * item click.
     */
    public interface CallbackForImageClick {
        /**
         * PersonImageFragmentCallback when a image has been clicked for cast/crew.
         */
        public void onImageMovieItemSelected(String title, int adapterPosition, String[] imageFilePath, PersonImageAdapter.PersonImageAdapterViewHolder viewHolder)
    }
}