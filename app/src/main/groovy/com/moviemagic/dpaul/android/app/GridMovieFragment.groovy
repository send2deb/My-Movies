/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.moviemagic.dpaul.android.app.adapter.MovieGridRecyclerAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.Utility
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LoadMoreMovies
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class GridMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    //LOG_TAG is customised, so do not define this as static final
    private String LOG_TAG = GridMovieFragment.class.getSimpleName()
    private int mCurrentPage = 0
    //To hold the previous count of the total records
    private int mPreviousRecordCount = 0
    //To hold the threashHold to load next pag  e. Currently set to 20 (i.e. one page worth of data)
    private final int mThreasholdCount = 20
    //Boolean to indicate if more data is being loaded
    private boolean isMoreDataToLoad = true
    private mSortItemNumber = -1
    private String mSortParam
    private boolean mValidMenuSelection = false
    private boolean mSortIsOn = false
    private boolean mFilterIsOn = false
    private String mQuerySelectionClause
    private String[] mQuerySelectionArguments
    private Drawable mSortDrawableIcon
    private Drawable mFilterDrawableIcon
    private CallbackForGridItemClick mCallbackForGridItemClick
    private CollectionColorChangeCallback mCollectionColorChangeCallback
    private boolean mCollectionGridFlag = false
    private RecyclerView mRecyclerView
    private MovieGridRecyclerAdapter mGridRecyclerAdapter
    private String mMovieCategory
    private int mMovieCollectionId
    private Uri mMovieCategoryAndCollectionIdUri
    private String mMovieListType
    private boolean mShowSubTitleForUserLocalAndTmdbList = false
    private RecyclerView.OnScrollListener mRecyclerViewOnScrollListener
    private static final int MOVIE_GRID_FRAGMENT_LOADER_ID = 0
    private static final String SORT_MENU_FLAG = 'sort_menu_flag'
    private static final String FILTER_MENU_FLAG = 'filter_menu_flag'
    private static final String CURSOR_SELECTION_CLAUSE = 'selection_clause'
    private static final String CURSOR_SELECTION_ARGUMENTS = 'selection_arguments'
    private static final String CURSOR_SORT_CRITERIA = 'sort_Criteria'

    //Projection for movie_basic_info table
    private static final String[] MOVIE_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_ROW_ID = 0
    final static int COL_MOVIE_TITLE = 1
    final static int COL_MOVIE_POSTER = 2
    final static int COL_MOVIE_PAGE_NUM = 3
    final static int COL_MOVIE_ID = 4
    final static int COL_MOVIE_LIST_TYPE = 5

    //An empty constructor is needed so that lifecycle is properly handled
    public GridMovieFragment(){
        LogDisplay.callLog(LOG_TAG,'GridMovieFragment empty constructor is called',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
    }

    @Override
    public void onAttach(final Context context) {
        LogDisplay.callLog(LOG_TAG,'onAttach is called',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if(context instanceof AppCompatActivity) {
                mCallbackForGridItemClick = (CallbackForGridItemClick) context
            }
        } catch (final ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CallbackForGridItemClick interface")
        }
        try {
            if(context instanceof AppCompatActivity) {
                mCollectionColorChangeCallback = (CollectionColorChangeCallback) context
            }
        } catch (final ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CollectionColorChangeCallback interface")
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreate is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        super.onCreate(savedInstanceState)
        //Following line needed to let android know that Fragment has options menu
        //If this line is not added then associated method (e.g. OnCreateOptionsMenu) does not get supported
        //even in auto code completion
        setHasOptionsMenu(true)
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG,'onCreateView is called',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        //Get the bundle from the Fragment
        final Bundle args = getArguments()
        if (args) {
            mMovieCategoryAndCollectionIdUri = args.getParcelable(GlobalStaticVariables.MOVIE_CATEGORY_AND_COLL_ID_URI) as Uri
            mMovieCategory = MovieMagicContract.MovieBasicInfo.getMovieCategoryFromMovieAndCollectionIdUri(mMovieCategoryAndCollectionIdUri)
            mMovieCollectionId = MovieMagicContract.MovieBasicInfo.getCollectionIdFromMovieAndCollectionIdUri(mMovieCategoryAndCollectionIdUri)
            LogDisplay.callLog(LOG_TAG,"Grid Fragment arguments.Uri -> $mMovieCategoryAndCollectionIdUri",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG,"Grid Fragment arguments.Movie Category -> $mMovieCategory",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG,"Grid Fragment arguments.Collection ID -> $mMovieCollectionId",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        }

        // Modify the LOG_TAG for better debugging as the fragment use for multiple movie categories,
        // all subsequent log will now show category this grid is handling
        LOG_TAG = "$LOG_TAG->$mMovieCategory"
        //Inflate the view before referring any view using id
        final View mRootView = inflater.inflate(R.layout.fragment_grid_movie,container,false)
        mRecyclerView = mRootView.findViewById(R.id.auto_grid_recycler_view) as RecyclerView
        //Set this to false for smooth scrolling of recyclerview
        mRecyclerView.setNestedScrollingEnabled(false)
        //Create a new interface member variable for MovieGridRecyclerAdapterOnClickHandler and the same is passed as
        //parameter to Adapter, this onClick method is called whenever onClick is called from MovieGridRecyclerAdapter
        mGridRecyclerAdapter = new MovieGridRecyclerAdapter(getActivity(),
                new MovieGridRecyclerAdapter.MovieGridRecyclerAdapterOnClickHandler(){
                    @Override
                    void onClick(final int movieId) {
                        mCallbackForGridItemClick.onMovieGridItemSelected(movieId, mMovieCategory)
                    }
                })
        mRecyclerView.setAdapter(mGridRecyclerAdapter)
        LogDisplay.callLog(LOG_TAG,"Movie Category->$mMovieCategory",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        // The more load feature is needed for TMDb public list only
        if(mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_POPULAR ||
           mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED ||
           mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING ||
           mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING) {
            mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
                    LogDisplay.callLog(LOG_TAG, "state=$newState",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                    //Pause / resume Picasso based on scroll state
                    if(newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        Picasso.with(getActivity()).resumeTag(GlobalStaticVariables.PICASSO_POSTER_IMAGE_TAG)
                    } else {
                        Picasso.with(getActivity()).pauseTag(GlobalStaticVariables.PICASSO_POSTER_IMAGE_TAG)
                    }
                }

                @Override
                void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                    final GridLayoutManager gridLayoutManager = recyclerView.getLayoutManager() as GridLayoutManager
                    final int totalItemCount = gridLayoutManager.getItemCount()
                    final int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition()

                    LogDisplay.callLog(LOG_TAG, "Last visible item=$lastVisibleItemPosition : ToatlItemCount = $totalItemCount",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                    // If the total item count is zero and the previous isn't, assume the
                    // list is invalidated and should be reset back to initial state
                    if (totalItemCount < mPreviousRecordCount) {
                        LogDisplay.callLog(LOG_TAG, 'List invalidated and reset took place.', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                        mCurrentPage = getCurrentTmdbPage()
                        mPreviousRecordCount = totalItemCount
                        if (totalItemCount == 0) {
                            isMoreDataToLoad = true
                        }
                    }
                    // If it's still loading, we check to see if the dataset count has
                    // changed, if so we conclude it has finished loading and update the current page
                    // number and total item count.
                    if (isMoreDataToLoad && (totalItemCount > mPreviousRecordCount)) {
                        LogDisplay.callLog(LOG_TAG, 'Just started or loaded a new page and cursor updated.', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                        isMoreDataToLoad = false
                        mPreviousRecordCount = totalItemCount
                        mCurrentPage++
                    }
                    // If it isn’t currently loading, we check to see if we have breached
                    // the visibleThreshold and need to reload more data.
                    // If we do need to reload some more data, we execute LoadMoreMovies to fetch the data.
                    // threshold should reflect how many total columns there are too
                    if (!isMoreDataToLoad && (lastVisibleItemPosition + mThreasholdCount) >= totalItemCount) {
                        isMoreDataToLoad = true
                        if (mMovieCategory != 'error') {
                            final String[] movieCategory = [mMovieCategory] as String[]
                            LogDisplay.callLog(LOG_TAG, 'Going to load more data...', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                            if(Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                                new LoadMoreMovies(getActivity().getApplicationContext(), mCurrentPage).execute(movieCategory)
                            }
                        }
                    }
                }
            }
            mRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener)
        }  else {
            LogDisplay.callLog(LOG_TAG, "User list or collection grid view, so load more logic is skipped.Movie Category->$mMovieCategory",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        }
        return mRootView
    }

    @Override
    void onActivityCreated(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onActivityCreated is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        super.onActivityCreated(savedInstanceState)
        //If savedInstanceState (i.e. in case of restore), restore the value of mMovieCategory
        if (savedInstanceState) {
            mSortIsOn = savedInstanceState.getBoolean(SORT_MENU_FLAG)
            mFilterIsOn = savedInstanceState.getBoolean(FILTER_MENU_FLAG)
            mQuerySelectionClause = savedInstanceState.getString(CURSOR_SELECTION_CLAUSE)
            mQuerySelectionArguments = savedInstanceState.getStringArray(CURSOR_SELECTION_ARGUMENTS)
            mSortParam = savedInstanceState.getString(CURSOR_SORT_CRITERIA)
        } else { // Not restore case (i.e. first time load)
            // Default selection clause, selection arguments & sort order
            mQuerySelectionClause = null
            mQuerySelectionArguments = null
            mSortParam = "$MovieMagicContract.MovieBasicInfo._ID ASC"
        }
        LogDisplay.callLog(LOG_TAG, "mSortIsOn -> $mSortIsOn", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mFilterIsOn -> $mFilterIsOn", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mQuerySelectionClause -> $mQuerySelectionClause", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mQuerySelectionArguments -> $mQuerySelectionArguments", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mSortParam -> $mSortParam", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)

        // setHasOptionsMenu(true) is usually called from onCreate but due to the logic of the program it is being called
        // from here. We want os show the GridFragment menu only when it's attached to main activity and NOT collection activity
        if(mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_COLLECTION) {
            setHasOptionsMenu(false)
        } else {
            setHasOptionsMenu(true)
        }

        //If it's a fresh start then call init loader
        if(savedInstanceState == null) {
            LogDisplay.callLog(LOG_TAG, 'onActivityCreated:first time, so init loaders', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
            getLoaderManager().initLoader(MOVIE_GRID_FRAGMENT_LOADER_ID, null, this)
        } else {        //If it's restore then restart the loader
            if(mSortIsOn || mFilterIsOn) {
                // While restore restart loader is not working properly when sort or filter is on, so init it
                LogDisplay.callLog(LOG_TAG, 'onActivityCreated:not first time but sort or filter on, so init loaders', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                getLoaderManager().initLoader(MOVIE_GRID_FRAGMENT_LOADER_ID, null, this)
            } else {
                LogDisplay.callLog(LOG_TAG, 'onActivityCreated:not first time and without sort or filter on, so restart loaders', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                getLoaderManager().restartLoader(MOVIE_GRID_FRAGMENT_LOADER_ID, null, this)
            }
        }
    }

    @Override
    void onStart() {
        LogDisplay.callLog(LOG_TAG, 'onStart is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        super.onStart()
        // Check if the user is online or not, if not then show a message
        // This is not needed for Collection grid as Collection Fragment itself has a Snackbar message for this condition
        if(mMovieCategory != GlobalStaticVariables.MOVIE_CATEGORY_COLLECTION) {
            final boolean isOnline = Utility.isOnline(getActivity().getApplicationContext())
            if (!isOnline) {
                Snackbar.make(mRecyclerView, getString(R.string.no_internet_connection_message), Snackbar.LENGTH_LONG).show()
            } else if (Utility.isOnlyWifi(getActivity().getApplicationContext()) & !GlobalStaticVariables.WIFI_CONNECTED) {
                // If user has selected only WiFi but user is online without WiFi then show a dialog
                Snackbar.make(mRecyclerView, getString(R.string.internet_connection_without_wifi_message), Snackbar.LENGTH_LONG).show()
            } else if (Utility.isReducedDataOn(getActivity())) {
                // If user has selected reduced data
                Snackbar.make(mRecyclerView, getString(R.string.reduced_data_use_on_message), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    @Override
    public void onCreateOptionsMenu (final Menu menu, final MenuInflater inflater) {
        LogDisplay.callLog(LOG_TAG, 'onCreateOptionsMenu is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        // Inflate the menu, this adds items to the action bar if it is present.
        inflater.inflate(R.menu.grid_fragment_menu, menu)
        final MenuItem sortMenuItem = menu.getItem(1)
        mSortDrawableIcon = sortMenuItem.getIcon()
        final MenuItem filterMenuItem = menu.getItem(2)
        mFilterDrawableIcon = filterMenuItem.getIcon()
        LogDisplay.callLog(LOG_TAG, "onCreateOptionsMenu: mSortDrawableIcon->$mSortDrawableIcon & mFilterDrawableIcon-> $mFilterDrawableIcon", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        // Reset first so that if user moves to different screen without resetting it then it gets reset
        setIconColor(mSortDrawableIcon, false)
        setIconColor(mFilterDrawableIcon,false)

        // When restores then following logic ensure search or filter icon color is set properly
        if(mSortIsOn && mFilterIsOn) {
            setIconColor(mSortDrawableIcon, true)
            setIconColor(mFilterDrawableIcon,true)
        } else if(mSortIsOn) {
            setIconColor(mSortDrawableIcon, true)
        } else if(mFilterIsOn) {
            setIconColor(mFilterDrawableIcon,true)
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        LogDisplay.callLog(LOG_TAG, 'onOptionsItemSelected is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        mValidMenuSelection = true
        switch (item.getItemId()) {
            case R.id.menu_action_sort:
                mSortDrawableIcon = item.getIcon()
                sortAlertDialog()
                return true
                break
            case R.id.menu_action_filter:
                // It's the top level menu, no action required just get the icon
                mFilterDrawableIcon = item.getIcon()
                return true
                break
            case R.id.menu_action_filter_reset:
                mQuerySelectionClause = null
                mQuerySelectionArguments = null
                mFilterIsOn = false
                setIconColor(mFilterDrawableIcon, false)
                ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle('')
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_release_year:
                filterReleaseYearAlertDialog()
                return true
                break
            case R.id.menu_action_filter_genre:
                // First level under filter, so set the selection clause here
                mQuerySelectionClause = "$MovieMagicContract.MovieBasicInfo.COLUMN_GENRE  like ?"
                Toast.makeText(getContext(), getString(R.string.movie_genre_user_notification_msg), Toast.LENGTH_LONG).show()
                return true
                break
            case R.id.menu_action_filter_genre_action:
                // Second level, set the selection argument based on menu item & restart loader
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Action%'] as String[]
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_adventure:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Adventure%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_animation:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Animation%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_comedy:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Comedy%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_crime:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Crime%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_documentary:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Documentary%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_drama:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Drama%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_family:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Family%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_fantasy:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Fantasy%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_history:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%History%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_horror:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Horror%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_music:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Music%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_mystery:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Mystery%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_romance:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Romance%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_science_fiction:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Science Fiction%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_tv_movie:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%TV Movies%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_thriller:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Thriller%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_war:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%War%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_genre_western:
                mFilterIsOn = true
                mQuerySelectionArguments = ['%Western%']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_rating:
                // First level under filter, so set the selection clause here
                mQuerySelectionClause = "$MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG  >= ?"
                return true
                break
            case R.id.menu_action_filter_rating_greater_than_equal_9:
                // Second level, set the selection argument based on menu item & restart loader
                mFilterIsOn = true
                mQuerySelectionArguments = ['9'] as String[]
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_rating_greater_than_equal_8:
                mFilterIsOn = true
                mQuerySelectionArguments = ['8']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_rating_greater_than_equal_7:
                mFilterIsOn = true
                mQuerySelectionArguments = ['7']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_rating_greater_than_equal_6:
                mFilterIsOn = true
                mQuerySelectionArguments = ['6']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_rating_greater_than_equal_5:
                mFilterIsOn = true
                mQuerySelectionArguments = ['5']
                restartCursorLoader()
                return true
                break
            case R.id.menu_action_filter_rating_less_than_5:
                mFilterIsOn = true
                mQuerySelectionClause = "$MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG  < ?"
                mQuerySelectionArguments = ['5']
                restartCursorLoader()
                return true
                break
            default:
                mValidMenuSelection = false
                LogDisplay.callLog(LOG_TAG,'Unknown menu item',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        LogDisplay.callLog(LOG_TAG, "onCreateLoader is called.loader id->$id", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        //Build the URI with movie category
        final Uri movieCategoryUri = MovieMagicContract.MovieBasicInfo.buildMovieUriWithMovieCategory(mMovieCategory)
        //Decide the uri based on request type (i.e. collection movies or rest)
        final Uri uri
        if(mMovieCollectionId == 0) {
            uri = movieCategoryUri
        } else {
            uri = mMovieCategoryAndCollectionIdUri
            mCollectionGridFlag = true
        }

        switch (id) {
            case MOVIE_GRID_FRAGMENT_LOADER_ID:
                return new CursorLoader(
                        getActivity(),            //Parent Activity Context
                        uri,                      //Table to query
                        MOVIE_COLUMNS,            //Projection to return
                        mQuerySelectionClause,          //Selection Clause, null->will return all data
                        mQuerySelectionArguments, //Selection Arg, null-> will return all data
                        mSortParam)               //Sort order, will be sorted by date in ascending order
            default:
                return null
        }
    }

    @Override
    void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        LogDisplay.callLog(LOG_TAG,"onLoadFinished is called. Total record count -> ${data.getCount()}",LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        determineSubTitleShow(mMovieCategory)
        data.moveToLast()
        if(data.moveToFirst()) {
            mCurrentPage = getCurrentTmdbPage()
            mMovieListType = data.getString(COL_MOVIE_LIST_TYPE)
            if(mCollectionGridFlag) {
                //Call CollectionColorChangeCallback which Collection activity will use to change the grid color
                mCollectionColorChangeCallback.notifyCollectionColorChange()
                mCollectionGridFlag = false
            }
        } else {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle((getString(R.string.user_local_and_tmdb_list_subtitle_no_movie_msg)))
            LogDisplay.callLog(LOG_TAG, 'Empty cursor returned by loader', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        }
          mGridRecyclerAdapter.swapCursor(data)
        // Show subTitle - should always be called before the below filter menu so that filter subtitle can override it
        if(mShowSubTitleForUserLocalAndTmdbList) {
            if(data.getCount() > 1) {
                ((AppCompatActivity)getActivity()).getSupportActionBar()
                        .setSubtitle(String.format(getString(R.string.user_local_and_tmdb_list_subtitle_record_count),data.getCount()))

            } else if(data.getCount() == 1) {
                ((AppCompatActivity)getActivity()).getSupportActionBar()
                        .setSubtitle(String.format(getString(R.string.user_local_and_tmdb_list_subtitle_record_count_single),data.getCount()))
            } else {
                ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle((getString(R.string.user_local_and_tmdb_list_subtitle_no_movie_msg)))
            }
            mShowSubTitleForUserLocalAndTmdbList = false
        }

        // For filter menu - if in use then show the count and change the icon color
        if(mFilterIsOn) {
            setIconColor(mFilterDrawableIcon, true)
            if(data.getCount() > 1) {
                ((AppCompatActivity)getActivity()).getSupportActionBar()
                        .setSubtitle(String.format(getString(R.string.grid_fragment_subtitle_record_count),data.getCount()))

            } else if(data.getCount() == 1) {
                ((AppCompatActivity)getActivity()).getSupportActionBar()
                        .setSubtitle(String.format(getString(R.string.grid_fragment_subtitle_record_count_single),data.getCount()))
            } else {
                ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle((getString(R.string.filter_no_movie_msg)))
            }
        }
        if(mSortIsOn) {
            setIconColor(mSortDrawableIcon, true)
        }
    }

    @Override
    void onLoaderReset(final Loader<Cursor> loader) {
        LogDisplay.callLog(LOG_TAG,'onLoaderReset is called',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        if (mGridRecyclerAdapter) mGridRecyclerAdapter.swapCursor(null)
    }

    /**
     * Determines if a subtitle will be shown or not
     * @param category Movie Category used to determine if subtitle to be shown
     */
    protected void determineSubTitleShow(final String category) {
        switch (category) {
            case GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WATCHED:
            case GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_FAVOURITE:
            case GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_WISH_LIST:
            case GlobalStaticVariables.MOVIE_CATEGORY_LOCAL_USER_COLLECTION:
            case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST:
            case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE:
            case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED:
                mShowSubTitleForUserLocalAndTmdbList = true
                break
            default:
                mShowSubTitleForUserLocalAndTmdbList = false
        }
    }

    /**
     * Get the TMDb public list pager number from sharedPreference based on movie category
     * @return current TMDb movie category's page number
     */
    protected int getCurrentTmdbPage() {
        final SharedPreferences sharedPref = getActivity().getApplicationContext().getSharedPreferences(
                getString(R.string.app_pref_tmdb_movie_page_number_file), Context.MODE_PRIVATE)
        int currentPage = 0
        switch (mMovieCategory) {
            case GlobalStaticVariables.MOVIE_CATEGORY_POPULAR:
                currentPage = sharedPref.getInt(getString(R.string.app_pref_tmdb_popular_page_key), 0)
                break
            case GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED:
                currentPage = sharedPref.getInt(getString(R.string.app_pref_tmdb_toprated_page_key), 0)
                break
            case GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING:
                currentPage = sharedPref.getInt(getString(R.string.app_pref_tmdb_nowplaying_page_key), 0)
                break
            case GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING:
                currentPage = sharedPref.getInt(getString(R.string.app_pref_tmdb_upcoming_page_key), 0)
                break
            default:
                LogDisplay.callLog(LOG_TAG, "Not TMDb public category. Category -> $mMovieCategory", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        }
        return currentPage
    }

    /**
     * Show a dialog when user is clicked on sort menu
     */
    private void sortAlertDialog() {
        LogDisplay.callLog(LOG_TAG, "sortAlertDialog: user list type->$mMovieListType", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        mSortItemNumber = -1
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogTheme)
        builder.setTitle(getString(R.string.sort_dialog_title))
        // Build the dialog items based on list type (for user list a new menu item is shown)
        if(mMovieListType == GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_PUBLIC ||
            mMovieListType == GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_USER) {
            builder.setSingleChoiceItems(getResources().getStringArray(R.array.sort_elements_public_or_tmdb_user), -1, new DialogInterface.OnClickListener() {
                @Override
                void onClick(final DialogInterface dialog, final int which) {
                    LogDisplay.callLog(LOG_TAG, "iten selected is -> $which", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                    mSortItemNumber = which
                }
            })
        } else if (mMovieListType == GlobalStaticVariables.MOVIE_LIST_TYPE_USER_LOCAL_LIST) {
            builder.setSingleChoiceItems(getResources().getStringArray(R.array.sort_elements_user_local), -1, new DialogInterface.OnClickListener() {
                @Override
                void onClick(final DialogInterface dialog, final int which) {
                    LogDisplay.callLog(LOG_TAG, "iten selected is -> $which", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                    mSortItemNumber = which
                }
            })
        }
        builder.setPositiveButton(getString(R.string.sort_dialog_ascending_button), new DialogInterface.OnClickListener() {
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, 'Dialog ascending is clicked.', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                mSortIsOn = true
                mSortParam = "${determineSortType()} ASC"
                restartCursorLoader()

                // Dismiss the dialog
                dialog.dismiss()
            }
        })
        // Doing a bit of hacking to use alert dialog's cancel button as an action button
        builder.setNegativeButton(getString(R.string.sort_dialog_descending_button), new DialogInterface.OnClickListener(){
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, 'Dialog descending is clicked.', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                mSortIsOn = true
                mSortParam = "${determineSortType()} DESC"
                restartCursorLoader()

                // Dismiss the dialog
                dialog.dismiss()
            }
        })

        //Show the reset button only if the sort is on
        if(mSortIsOn) {
            builder.setNeutralButton(getString(R.string.sort_dialog_reset_button), new DialogInterface.OnClickListener() {
                @Override
                void onClick(final DialogInterface dialog, final int which) {
                    LogDisplay.callLog(LOG_TAG, 'Dialog reset is clicked. Reset the list.', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                    // Reset the sort
                    mSortParam = "$MovieMagicContract.MovieBasicInfo._ID ASC"
                    mSortIsOn = false
                    setIconColor(mSortDrawableIcon, false)
                    restartCursorLoader()

                    // Dismiss the dialog
                    dialog.dismiss()
                }
            })
        }

        // Create the AlertDialog & show
        final AlertDialog dialog = builder.create()
        dialog.show()
    }

    /**
     * Determines the sort criteria based on the field user selection
     * @return Sort parameter
     */
    protected String determineSortType() {
        String sortField = null
        switch (mSortItemNumber) {
            case 0:
                sortField = MovieMagicContract.MovieBasicInfo.COLUMN_TITLE
                break
            case 1:
                sortField = MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE
                break
            case 2:
                sortField = MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG
                break
            case 3: // This one is for user local list only
                sortField = MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP
                break
            default:
                mValidMenuSelection = false
                mSortIsOn = false
                setIconColor(mSortDrawableIcon, false)
                Snackbar.make(mRecyclerView,getString(R.string.no_sort_selection_msg),Snackbar.LENGTH_LONG).show()
                LogDisplay.callLog(LOG_TAG, "Unknwon item number -> $mSortItemNumber", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        }
        return sortField
    }

    /**
     * Show a dialog when user is clicked on release year filter
     */
    private void filterReleaseYearAlertDialog() {
        LogDisplay.callLog(LOG_TAG, "filterReleaseYearAlertDialog is called", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogTheme)
        builder.setTitle(getString(R.string.filter_release_year_dialog_title))
        // Get the current year
        final Calendar calendar = Calendar.getInstance()
        final int year = calendar.get(Calendar.YEAR)
        // Create an array with current and last 99 years
        final String[] items = new String[100]
        for(final i in 0..99) {
            items[i] = year - i
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, "Item ${items[which]} is clicked", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                final String selectedYearStartBound = "${items[which]}-01-01"
                final String selectedYearEndBound = "${items[which]}-12-31"
                LogDisplay.callLog(LOG_TAG, "selectedYearStartBound -> $selectedYearStartBound & selectedYearEndBound -> $selectedYearEndBound", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                mQuerySelectionClause = """$MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE >= ? and
                                     $MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE <= ? """

                mQuerySelectionArguments = [Long.toString(MovieMagicContract.convertMovieReleaseDate(selectedYearStartBound))
                                            , Long.toString(MovieMagicContract.convertMovieReleaseDate(selectedYearEndBound))]
                LogDisplay.callLog(LOG_TAG, "mQuerySelectionArguments -> $mQuerySelectionArguments", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                mFilterIsOn = true
                restartCursorLoader()
            }
        })
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, 'Dialog filter cancel is clicked. No action needed.', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
                dialog.dismiss()
            }
        })

        // Create the AlertDialog & show
        final AlertDialog dialog = builder.create()
        dialog.show()
    }

    /**
     * User has used sort or filter menu, so restart the loader to refresh data
     */
    protected void restartCursorLoader() {
        LogDisplay.callLog(LOG_TAG, 'restartCursorLoader is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "restartCursorLoader: mQuerySelectionClause -> $mQuerySelectionClause", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "restartCursorLoader: mQuerySelectionArguments -> $mQuerySelectionArguments", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "restartCursorLoader: mSortParam -> $mSortParam", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)

        if(mValidMenuSelection) {
            // Set the values for the fields which are used for more data load logic
            isMoreDataToLoad = true
            mPreviousRecordCount = 0
            getLoaderManager().restartLoader(MOVIE_GRID_FRAGMENT_LOADER_ID, null, this)
        }
    }

    /**
     * This method changes the color of the Sort or Filter menu icon if it visible based on the boolean value (true -> set color or flase -> reset color)
     * @param drawable Drawable of either Sort or Filter icon
     * @param setColor Boolean flag (True to set color or False to reset color)
     */
    protected void setIconColor(final Drawable drawable, final boolean setColor) {
        LogDisplay.callLog(LOG_TAG, 'setIconColor is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        if(drawable) {
            if(setColor) {
                drawable.setColorFilter(ContextCompat.getColor(getActivity(), R.color.accent),
                        PorterDuff.Mode.SRC_ATOP)
            } else {
                drawable.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white_color),
                        PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

    @Override
    void onSaveInstanceState(final Bundle outState) {
        LogDisplay.callLog(LOG_TAG, 'onSaveInstanceState is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mSortIsOn -> $mSortIsOn", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mFilterIsOn -> $mFilterIsOn", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mQuerySelectionClause -> $mQuerySelectionClause", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mQuerySelectionArguments -> $mQuerySelectionArguments", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mSortParam -> $mSortParam", LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        outState.putBoolean(SORT_MENU_FLAG,mSortIsOn)
        outState.putBoolean(FILTER_MENU_FLAG,mFilterIsOn)
        outState.putString(CURSOR_SELECTION_CLAUSE,mQuerySelectionClause)
        outState.putStringArray(CURSOR_SELECTION_ARGUMENTS,mQuerySelectionArguments)
        outState.putString(CURSOR_SORT_CRITERIA,mSortParam)
        // Now call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    @Override
    void onResume() {
        LogDisplay.callLog(LOG_TAG, 'onResume is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        super.onResume()
    }

    @Override
    void onPause() {
        LogDisplay.callLog(LOG_TAG, 'onPause is called', LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        super.onPause()
    }

    @Override
    void onStop() {
        super.onStop()
        LogDisplay.callLog(LOG_TAG,'onStop is called',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
    }

    @Override
    void onDestroy() {
        super.onDestroy()
        LogDisplay.callLog(LOG_TAG,'onDestroy is called',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
    }

    @Override
    void onDestroyView() {
        LogDisplay.callLog(LOG_TAG,'onDestroyView is called->Release the resources',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        // Destroy the loader
        getLoaderManager().destroyLoader(MOVIE_GRID_FRAGMENT_LOADER_ID)
        // Set the adapter to null after loaders are destroyed
        mGridRecyclerAdapter = null
        // Remove the listener
        mRecyclerView.removeOnScrollListener(mRecyclerViewOnScrollListener)
        // Remove adapter from recycler view
        mRecyclerView.setAdapter(null)
        // Set recycler view to null
        mRecyclerView = null
        // Set the Drawables to null
        mSortDrawableIcon = null
        mFilterDrawableIcon = null
        // Detach the interface reference for GC
        mCallbackForGridItemClick = null
        mCollectionColorChangeCallback = null
        super.onDestroyView()
    }

    @Override
    void onDetach() {
        LogDisplay.callLog(LOG_TAG,'onDetach is called',LogDisplay.GRID_MOVIE_FRAGMENT_LOG_FLAG)
        super.onDetach()
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selection.
     */
    public interface CallbackForGridItemClick {
        /**
         * GridFragment Callback when an item has been selected.
         */
        public void onMovieGridItemSelected(int movieId, String movieCategory)
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selection.
     */
    public interface CollectionColorChangeCallback {
        /**
         * GridFragment Callback to change color for collection grid when data is loaded
         */
        public void notifyCollectionColorChange()
    }
}