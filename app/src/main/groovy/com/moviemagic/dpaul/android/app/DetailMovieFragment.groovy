/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorInt
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
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.*
import android.support.v7.widget.RecyclerView.LayoutManager
import android.view.*
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.*
import com.moviemagic.dpaul.android.app.adapter.*
import com.moviemagic.dpaul.android.app.backgroundmodules.*
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.youtube.MovieMagicYoutubeFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import groovy.transform.CompileStatic

@CompileStatic
class DetailMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    //LOG_TAG is customised, so do not define this as static final
    private String LOG_TAG = DetailMovieFragment.class.getSimpleName()

    private TextView mMovieTitleTextView, mGenreTextView, mRunTimeTextView, mReleaseDateTextView, mBudgetTextView,
                     mRevenueTextView, mPopularityTextView, mTotalVoteCountTextView, mTaglineTextView, mSynopsisTextView,
                     mProdCompanyTextView, mProdCountryTextView, mCollectionNameTextView, mExternalLinkHeader

    private TextView mReleaseDateHeaderTextView, mBudgetHeaderTextView, mRevenueHeaderTextView, mPopularityHeaderTextView,
                     mTmdbRatingHeaderTextView, mTmdbTotalVoteCountHeaderTextView, mTmdbTotalVoteCountTrailerTextView,
                     mUserRatingHeaderTextView, mTaglineHeaderTextView, mSynopsisHeaderTextView, mMovieTrailerHeaderTextView,
                     mProdCompanyHeaderTextView, mProdCountryHeaderTextView, mCastHeaderTextView, mCrewHeaderTextView,
                     mSimilarMovieHeaderTextView, mCollectionNameHeaderTextView, mReviewHeaderTextView, mRecyclerViewEmptyMsgTextView,
                     mUserListDrawableTitle, mUserTmdbListDrawableTitle, mCastGridEmptyMsgTextView, mCrewGridEmptyMsgTextView,
                     mSimilarMovieGridEmptyMsgTextView, mMovieTrailerEmptyMsgTextView
    private ImageView mMpaaRatingImageView, mPosterImageView, mCollectionBackdropImageView
    private RelativeLayout mUserListDrawableLayout, mUserTmdbListDrawableLayout, mDetailMovieLayout
    private LinearLayout mBackdropDotHolderLayout
    private ImageButton mImageButtonWatched, mImageButtonWishList, mImageButtonFavourite, mImageButtonCollection
    private ImageButton mTmdbImageButtonWatchlist, mTmdbImageButtonFavourite, mTmdbImageButtonRated
    private RatingBar mTmdbRatingBar, mUserRatingBar
    private Button mHomePageButton, mImdbLinkButton
    private NestedScrollView mNestedScrollView
    private CoordinatorLayout mCoordinatorLayout
    private int _ID_movie_basic_info
    private int mMovieId
    private int mCollectionId
    private String[] mMovieIdArg
    private String[] mMovieIdCategoryArg
    private String[] mVideoArg
    private String[] mReleaseInfoArg
    private String[] mMovieImageArg
    private String mMovieTitle, mOriginalBackdropPath, mMovieHomePageUrl, mMovieImdbId
    private SimilarMovieAdapter mSimilarMovieAdapter
    private MovieCastAdapter mMovieCastAdapter
    private MovieCrewAdapter mMovieCrewAdapter
    private HorizontalGridView mHorizontalSimilarMovieGridView, mHorizontalMovieCastGridView, mHorizontalMovieCrewGridView
    private String mLocale
    private int mPalletePrimaryColor
    private int mPalletePrimaryDarkColor
    private int mPalleteTitleColor
    private int mPalleteBodyTextColor
    private int mPalleteAccentColor
    private RecyclerView mMovieReviewRecyclerView
    private MovieReviewAdapter mMovieReviewAdapter
    private String mMovieCategory
    private boolean mUserListWatchedFlag = false
    private boolean mUserListWishListdFlag = false
    private boolean mUserListFavouriteFlag = false
    private boolean mUserListCollectionFlag = false
    private boolean mUserTmdbListWatchlistFlag = false
    private boolean mUserTmdbListFavouriteFlag = false
    private boolean mUserTmdbListRatedFlag = false
    private CollapsingToolbarLayout mCollapsingToolbar
    private Toolbar mToolbar
    private AppBarLayout mAppBarLayout
    private ViewPager mBackdropViewPager
    private GridLayoutManager mSimilarMovieGridLayoutManager, mCastGridLayoutManager, mCrewGridLayoutManager
    private LayoutManager mReviewLinearLayoutManager
    private List<String> mBackdropList
    private CallbackForBackdropImageClick mCallbackForBackdropImageClick
    private int mBackdropViewPagerPos = 0
    private boolean firstTimeLocalRatingUpdateWithTmdbRating = true
    private boolean mMoviDataLoaded = false
    private boolean mImageButtonClickForcedOnLoadFinished = false
    private View mRootView
    private CallbackForSimilarMovieClick mCallbackForSimilarMovieClick
    private DetailFragmentPagerAdapter mDetailFragmentPagerAdapter
    private AppBarLayout.OnOffsetChangedListener mAppbarOnOffsetChangeListener
    private OnPageChangeListener mOnPageChangeListener
    private SimilarMovieAdapter.SimilarMovieAdapterOnClickHandler mSimilarMovieAdapterOnClickHandler
    private DetailFragmentPagerAdapter.DetailFragmentPagerAdapterOnClickHandler mDetailFragmentPagerAdapterOnClickHandler
    private String DETAIL_YOUTUBE_FRAGMENT_TAG = ''

    private static final int MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID = 0
    private static final int MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID = 1
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID = 2
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID = 3
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID = 4
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID = 5
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID = 6
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_REVIEW_LOADER_ID = 7
    private static final int MOVIE_DETAIL_FRAGMENT_MOVIE_USER_LIST_FLAG_LOADER_ID = 8
    private static final int MOVIE_DETAIL_FRAGMENT_BASIC_TMDB_DATA_LOADER_ID = 9

    //Columns to fetch from movie_basic_info table
    private static final String[] MOVIE_BASIC_INFO_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_ADULT_FLAG,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_BACKDROP_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_ORIGINAL_TITLE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_OVERVIEW,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_DATE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_POPULARITY,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_VIDEO_FLAG,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_AVG,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_VOTE_COUNT,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_NAME,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_POSTER_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_COLLECTION_BACKDROP_PATH,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_BUDGET,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_GENRE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_HOME_PAGE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_IMDB_ID,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COMPANIES,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_PRODUCTION_COUNTRIES,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_REVENUE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RUNTIME,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_RELEASE_STATUS,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_TAGLINE,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP,
                                                              MovieMagicContract.MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_BASIC_ID = 0
    final static int COL_MOVIE_BASIC_MOVIE_ID = 1
    final static int COL_MOVIE_BASIC_ADULT_FLAG = 2
    final static int COL_MOVIE_BASIC_BACKDROP_PATH = 3
    final static int COL_MOVIE_BASIC_ORIG_TITLE = 4
    final static int COL_MOVIE_BASIC_OVERVIEW = 5
    final static int COL_MOVIE_BASIC_RELEASE_DATE = 6
    final static int COL_MOVIE_BASIC_POSTER_PATH = 7
    final static int COL_MOVIE_BASIC_POPULARITY = 8
    final static int COL_MOVIE_BASIC_TITLE = 9
    final static int COL_MOVIE_BASIC_VIDEO_FLAG = 10
    final static int COL_MOVIE_BASIC_VOTE_AVG = 11
    final static int COL_MOVIE_BASIC_VOTE_COUNT = 12
    final static int COL_MOVIE_BASIC_PAGE_NUM = 13
    final static int COL_MOVIE_BASIC_MOVIE_CATEGORY = 14
    final static int COL_MOVIE_BASIC_MOVIE_LIST_TYPE = 15
    final static int COL_MOVIE_BASIC_DETAIL_DATA_PRESENT_FLAG = 16
    final static int COL_MOVIE_BASIC_SIMILAR_MOVIE_LINK_ID = 17
    final static int COL_MOVIE_BASIC_COLLECTION_ID = 18
    final static int COL_MOVIE_BASIC_COLLECTION_NAME = 19
    final static int COL_MOVIE_BASIC_COLLECTION_POSTER_PATH = 20
    final static int COL_MOVIE_BASIC_COLLECTION_BACKDROP_PATH = 21
    final static int COL_MOVIE_BASIC_BUDGET = 22
    final static int COL_MOVIE_BASIC_GENRE = 23
    final static int COL_MOVIE_BASIC_HOME_PAGE = 24
    final static int COL_MOVIE_BASIC_IMDB_ID = 25
    final static int COL_MOVIE_BASIC_PRODUCTION_COMPANIES = 26
    final static int COL_MOVIE_BASIC_PRODUCTION_COUNTRIES = 27
    final static int COL_MOVIE_BASIC_REVENUE = 28
    final static int COL_MOVIE_BASIC_RUNTIME = 29
    final static int COL_MOVIE_BASIC_RELEASE_STATUS = 30
    final static int COL_MOVIE_BASIC_TAGLINE = 31
    final static int COL_MOVIE_BASIC_CREATE_TIMESTAMP = 32
    final static int COL_MOVIE_BASIC_UPDATE_TIMESTAMP = 33

    //Columns to fetch from movie_basic_info table for similar movies
    private static final String[] SIMILAR_MOVIE_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_POSTER_PATH,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_TITLE,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,
                                                           MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_SIMILAR_MOVIE_ID = 0
    final static int COL_SIMILAR_MOVIE_MOVIE_ID = 1
    final static int COL_SIMILAR_MOVIE_POSTER_PATH = 2
    final static int COL_SIMILAR_MOVIE_TITLE = 3
    final static int COL_SIMILAR_MOVIE_CATEGORY = 4
    final static int COL_SIMILAR_MOVIE_LINK_ID = 5

    //Columns to fetch from movie_video table
    private static final String[] MOVIE_VIDEO_COLUMNS = [MovieMagicContract.MovieVideo._ID,
                                                         MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID,
                                                         MovieMagicContract.MovieVideo.COLUMN_VIDEO_KEY,
                                                         MovieMagicContract.MovieVideo.COLUMN_VIDEO_NAME,
                                                         MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE,
                                                         MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_VIDEO_ID = 0
    final static int COL_MOVIE_VIDEO_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_VIDEO_KEY = 2
    final static int COL_MOVIE_VIDEO_NAME = 3
    final static int COL_MOVIE_VIDEO_SITE = 4
    final static int COL_MOVIE_VIDEO_TYPE = 5

    //Columns to fetch from movie_cast table
    private static final String[] MOVIE_CAST_COLUMNS = [MovieMagicContract.MovieCast._ID,
                                                        MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID,
                                                        MovieMagicContract.MovieCast.COLUMN_CAST_CHARACTER,
                                                        MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_ID,
                                                        MovieMagicContract.MovieCast.COLUMN_CAST_PERSON_NAME,
                                                        MovieMagicContract.MovieCast.COLUMN_CAST_PROFILE_PATH]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_CAST_ID = 0
    final static int COL_MOVIE_CAST_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_CAST_CHARACTER = 2
    final static int COL_MOVIE_CAST_PERSON_ID = 3
    final static int COL_MOVIE_CAST_PERSON_NAME = 4
    final static int COL_MOVIE_CAST_PROFILE_PATH = 5

    //Columns to fetch from movie_crew table
    private static final String[] MOVIE_CREW_COLUMNS = [MovieMagicContract.MovieCrew._ID,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_ID,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_PERSON_NAME,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_JOB,
                                                        MovieMagicContract.MovieCrew.COLUMN_CREW_PROFILE_PATH]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_CREW_ID = 0
    final static int COL_MOVIE_CREW_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_CREW_PERSON_ID = 2
    final static int COL_MOVIE_CREW_PERSON_NAME = 3
    final static int COL_MOVIE_CREW_CREW_JOB = 4
    final static int COL_MOVIE_CREW_PROFILE_PATH = 5

    //Columns to fetch from movie_release_date_info table
    private static
    final String[] MOVIE_RELEASE_INFO_COLUMNS = [MovieMagicContract.MovieReleaseDate._ID,
                                                 MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID,
                                                 MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY,
                                                 MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_CERTIFICATION]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_RELEASE_INFO_ID = 0
    final static int COL_MOVIE_RELEASE_INFO_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_RELEASE_INFO_ISO_COUNTRY = 2
    final static int COL_MOVIE_RELEASE_INFO_CERTIFICATION = 3

    //Columns to fetch from movie_image table
    private static final String[] MOVIE_IMAGE_COLUMNS = [MovieMagicContract.MovieImage._ID,
                                                         MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID,
                                                         MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE,
                                                         MovieMagicContract.MovieImage.COLUMN_IMAGE_FILE_PATH]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_IMAGE_ID = 0
    final static int COL_MOVIE_IMAGE_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_IMAGE_TYPE = 2
    final static int COL_MOVIE_IMAGE_FILE_PATH = 3

    //Columns to fetch from movie_review table
    private static final String[] MOVIE_REVIEW_COLUMNS = [MovieMagicContract.MovieReview._ID,
                                                          MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID,
                                                          MovieMagicContract.MovieReview.COLUMN_REVIEW_AUTHOR,
                                                          MovieMagicContract.MovieReview.COLUMN_REVIEW_CONTENT]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_REVIEW_ID = 0
    final static int COL_MOVIE_REVIEW_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_REVIEW_AUTHOR = 2
    final static int COL_MOVIE_REVIEW_CONTENT = 3

    //Columns to fetch from movie_user_list_flag table
    private static
    final String[] MOVIE_USER_LIST_FLAG_COLUMNS = [MovieMagicContract.MovieUserListFlag._ID,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WATCHED,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_WISH_LIST,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_FAVOURITE,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_COLLECTION,
                                                   MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_USER_RATING]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_USER_LIST_FLAG_ID = 0
    final static int COL_MOVIE_USER_LIST_FLAG_ORIG_MOVIE_ID = 1
    final static int COL_MOVIE_USER_LIST_FLAG_WATCHED_FLAG = 2
    final static int COL_MOVIE_USER_LIST_FLAG_WISH_LIST_FLAG = 3
    final static int COL_MOVIE_USER_LIST_FLAG_FAVOURITE_FLAG = 4
    final static int COL_MOVIE_USER_LIST_FLAG_COLLECTION_FLAG = 5
    final static int COL_MOVIE_USER_LIST_FLAG_USER_RATING = 6

    //Columns to fetch from movie_basic_info for user's Tmdb list table
    private static
    final String[] MOVIE_BASIC_INFO_TMDB_COLUMNS = [MovieMagicContract.MovieBasicInfo._ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_TMDB_USER_RATED_RATING,
                                                   MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE]
    //These are indices of the above columns, if projection array changes then this needs to be changed
    final static int COL_MOVIE_BASIC_TMDB_ID = 0
    final static int COL_MOVIE_BASIC_TMDB_MOVIE_ID = 1
    final static int COL_MOVIE_BASIC_TMDB_MOVIE_CATEGORY = 2
    final static int COL_MOVIE_BASIC_TMDB_MOVIE_RATING = 3
    final static int COL_MOVIE_BASIC_TMDB_MOVIE_LIST_TYPE = 4

    //An empty constructor is needed so that lifecycle is properly handled
    public DetailMovieFragment() {
        LogDisplay.callLog(LOG_TAG, 'DetailMovieFragment empty constructor is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
    }

    @Override
    public void onAttach(final Context context) {
        // Modify the LOG_TAG for better debugging as the fragment use for multiple times,
        // all subsequent log will now show fragment number
        LOG_TAG = LOG_TAG + '->Fragment#' + getActivity().getSupportFragmentManager().getBackStackEntryCount()
        DETAIL_YOUTUBE_FRAGMENT_TAG = 'detail_youtube_fragment' + getActivity().getSupportFragmentManager().getBackStackEntryCount()
        LogDisplay.callLog(LOG_TAG,'onAttach is called',LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if(context instanceof AppCompatActivity) {
                mCallbackForBackdropImageClick = (CallbackForBackdropImageClick) context
            }
        } catch (final ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CallbackForBackdropImageClick interface")
        }
        try {
            if(context instanceof AppCompatActivity) {
                mCallbackForSimilarMovieClick = (CallbackForSimilarMovieClick) context
            }
        } catch (final ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement CallbackForSimilarMovieClick interface")
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreate is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onCreate(savedInstanceState)
        //Following line needed to let android know that Fragment has options menu
        //If this line is not added then associated method (e.g. OnCreateOptionsMenu) does not get supported
        //even in auto code completion
        setHasOptionsMenu(true)
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreateView is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        //Get the bundle from the Fragment
        final Bundle args = getArguments()
        if (args) {
            mMovieId = args.getInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID)
            mMovieCategory = args.getString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY)
            LogDisplay.callLog(LOG_TAG, "Fragment arguments.Movie ID -> $mMovieId", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            LogDisplay.callLog(LOG_TAG, "Fragment arguments.Movie Category -> $mMovieCategory", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        } else {
            LogDisplay.callLog(LOG_TAG, 'Could not parse fragment data', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        }
        //Inflate the view before referring any view using id
        mRootView = inflater.inflate(R.layout.fragment_detail_movie, container, false)

        mCollapsingToolbar = mRootView.findViewById(R.id.movie_detail_collapsing_toolbar) as CollapsingToolbarLayout
        if (mCollapsingToolbar) {
            //Just clear off to be on the safe side
            mCollapsingToolbar.setTitle(" ")
        }

        mToolbar = mRootView.findViewById(R.id.movie_detail_toolbar) as Toolbar
        if (mToolbar) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar)
//            //Enable back to home button
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        }

        mAppBarLayout = mRootView.findViewById(R.id.movie_detail_app_bar_layout) as AppBarLayout

        mBackdropViewPager = mRootView.findViewById(R.id.movie_detail_backdrop_viewpager) as ViewPager
        mBackdropDotHolderLayout = mRootView.findViewById(R.id.view_pager_dots_holder) as LinearLayout

        //All the layouts
        mDetailMovieLayout = mRootView.findViewById(R.id.fragment_detail_movie_layout) as RelativeLayout
        mUserListDrawableLayout = mRootView.findViewById(R.id.movie_detail_user_list_drawable_layout) as RelativeLayout
        mUserTmdbListDrawableLayout = mRootView.findViewById(R.id.movie_detail_user_tmdb_list_drawable_layout) as RelativeLayout
        mNestedScrollView = mRootView.findViewById(R.id.movie_detail_scroll) as NestedScrollView
        mCoordinatorLayout = mRootView.findViewById(R.id.movie_detail_coordinator_layout) as CoordinatorLayout

        //All the header (fixed text) fields & buttons
        mReleaseDateHeaderTextView = mRootView.findViewById(R.id.movie_detail_poster_release_date_header) as TextView
        mBudgetHeaderTextView = mRootView.findViewById(R.id.movie_detail_poster_budget_header) as TextView
        mRevenueHeaderTextView = mRootView.findViewById(R.id.movie_detail_poster_revenue_header) as TextView
        mPopularityHeaderTextView = mRootView.findViewById(R.id.movie_detail_poster_popularity_header) as TextView
        mTmdbRatingHeaderTextView = mRootView.findViewById(R.id.movie_detail_tmdb_rating_header) as TextView
        mTmdbTotalVoteCountHeaderTextView = mRootView.findViewById(R.id.movie_detail_tmdb_rating_vote_count_header) as TextView
        mTmdbTotalVoteCountTrailerTextView = mRootView.findViewById(R.id.movie_detail_tmdb_rating_vote_count_trailer) as TextView
        mUserRatingHeaderTextView = mRootView.findViewById(R.id.movie_detail_user_rating_header) as TextView
        mUserListDrawableTitle = mRootView.findViewById(R.id.movie_detail_user_list_drawable_title) as TextView
        mUserTmdbListDrawableTitle = mRootView.findViewById(R.id.movie_detail_user_tmdb_list_drawable_title) as TextView
        mTaglineHeaderTextView = mRootView.findViewById(R.id.movie_detail_synopsis_tagline_header) as TextView
        mSynopsisHeaderTextView = mRootView.findViewById(R.id.movie_detail_synopsis_header) as TextView
        mMovieTrailerHeaderTextView = mRootView.findViewById(R.id.movie_detail_trailer_header) as TextView
        mMovieTrailerEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_trailer_empty_msg_text_view) as TextView
        mProdCompanyHeaderTextView = mRootView.findViewById(R.id.movie_detail_production_info_cmpy_header) as TextView
        mProdCountryHeaderTextView = mRootView.findViewById(R.id.movie_detail_production_info_country_header) as TextView
        mCastHeaderTextView = mRootView.findViewById(R.id.movie_detail_cast_header) as TextView
        mCrewHeaderTextView = mRootView.findViewById(R.id.movie_detail_crew_header) as TextView
        mSimilarMovieHeaderTextView = mRootView.findViewById(R.id.movie_detail_similar_movie_header) as TextView
        mCollectionNameHeaderTextView = mRootView.findViewById(R.id.movie_detail_collection_header) as TextView
        mCollectionNameHeaderTextView = mRootView.findViewById(R.id.movie_detail_collection_header) as TextView
        mReviewHeaderTextView = mRootView.findViewById(R.id.movie_detail_review_header) as TextView
        mRecyclerViewEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_review_recycler_view_empty_msg_text_view) as TextView
        mExternalLinkHeader = mRootView.findViewById(R.id.movie_detail_web_links_header) as TextView
        /**
         * User list button handling
         */
        mImageButtonWatched = mRootView.findViewById(R.id.movie_detail_user_list_drawable_watched) as ImageButton
        mImageButtonWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'ImageButton Watched Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mImageButtonClickForcedOnLoadFinished = true
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, true)
                    final String[] updateUserListArgs
                    //If full opaque then already selected, so remove it
                    if (mImageButtonWatched.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_WATCHED, GlobalStaticVariables.USER_LIST_REMOVE_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonWatched.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                        mImageButtonWatched.setColorFilter(null)
                        mUserListWatchedFlag = false
                    } else { //If 40% opaque then not selected, so add it
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_WATCHED, GlobalStaticVariables.USER_LIST_ADD_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonWatched.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                        mImageButtonWatched.setColorFilter(mPalleteAccentColor)
                        mUserListWatchedFlag = true
                    }
                } else {
                    Snackbar.make(mNestedScrollView.findViewById(R.id.movie_detail_user_list_drawable_layout),
                            getString(R.string.cannot_perform_operation_msg), Snackbar.LENGTH_LONG).show()
                }
            }
        })
        mImageButtonWishList = mRootView.findViewById(R.id.movie_detail_user_list_drawable_wish_list) as ImageButton
        mImageButtonWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'ImageButton WishList Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mImageButtonClickForcedOnLoadFinished = true
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, true)
                    final String[] updateUserListArgs
                    //If full opaque then already selected, so remove it
                    if (mImageButtonWishList.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_WISH_LIST, GlobalStaticVariables.USER_LIST_REMOVE_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonWishList.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                        mImageButtonWishList.setColorFilter(null)
                        mUserListWishListdFlag = false
                    } else { //If 40% opaque then not selected, so add it
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_WISH_LIST, GlobalStaticVariables.USER_LIST_ADD_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonWishList.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                        mImageButtonWishList.setColorFilter(mPalleteAccentColor)
                        mUserListWishListdFlag = true
                    }
                } else {
                    Snackbar.make(mNestedScrollView.findViewById(R.id.movie_detail_user_list_drawable_layout),
                            getString(R.string.cannot_perform_operation_msg), Snackbar.LENGTH_LONG).show()
                }
            }
        })
        mImageButtonFavourite = mRootView.findViewById(R.id.movie_detail_user_list_drawable_favourite) as ImageButton
        mImageButtonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'ImageButton Favourite Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mImageButtonClickForcedOnLoadFinished = true
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, true)
                    final String[] updateUserListArgs
                    //If full opaque then already selected, so remove it
                    if (mImageButtonFavourite.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_FAVOURITE, GlobalStaticVariables.USER_LIST_REMOVE_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonFavourite.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                        mImageButtonFavourite.setColorFilter(null)
                        mUserListFavouriteFlag = false
                    } else { //If 40% opaque then not selected, so add it
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_FAVOURITE, GlobalStaticVariables.USER_LIST_ADD_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonFavourite.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                        mImageButtonFavourite.setColorFilter(mPalleteAccentColor)
                        mUserListFavouriteFlag = true
                    }
                } else {
                    Snackbar.make(mNestedScrollView.findViewById(R.id.movie_detail_user_list_drawable_layout),
                            getString(R.string.cannot_perform_operation_msg), Snackbar.LENGTH_LONG).show()
                }
            }
        })
        mImageButtonCollection = mRootView.findViewById(R.id.movie_detail_user_list_drawable_collection) as ImageButton
        mImageButtonCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'ImageButton Collection Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mImageButtonClickForcedOnLoadFinished = true
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, true)
                    final String[] updateUserListArgs
                    //If full opaque then already selected, so remove it
                    if (mImageButtonCollection.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_COLLECTION, GlobalStaticVariables.USER_LIST_REMOVE_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonCollection.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                        mImageButtonCollection.setColorFilter(null)
                        mUserListCollectionFlag = false
                    } else { //If 40% opaque then not selected, so add it
                        //Pass the third parameter as "0.0" (i.e. user rating param)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_COLLECTION, GlobalStaticVariables.USER_LIST_ADD_FLAG, 0.0]
                        updateUserList.execute(updateUserListArgs)
                        mImageButtonCollection.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                        mImageButtonCollection.setColorFilter(mPalleteAccentColor)
                        mUserListCollectionFlag = true
                    }
                } else {
                    Snackbar.make(mNestedScrollView.findViewById(R.id.movie_detail_user_list_drawable_layout),
                            getString(R.string.cannot_perform_operation_msg), Snackbar.LENGTH_LONG).show()
                }
            }
        })
        /**
         * User's TMDb list button handling
         */
        mTmdbImageButtonWatchlist = mRootView.findViewById(R.id.movie_detail_user_tmdb_list_drawable_watchlist) as ImageButton
        mTmdbImageButtonWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'Tmdb user watchlist button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mImageButtonClickForcedOnLoadFinished = true
                if(Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                    //If full opaque then already selected, so remove it
                    if (mTmdbImageButtonWatchlist.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        new UploadTmdbRequest(getActivity(), GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST, 0,
                                false, mMovieCategory, mUserTmdbListDrawableLayout, mPalleteAccentColor).execute([mMovieId] as Integer[])
                        mUserTmdbListWatchlistFlag = false
                    } else { //If 40% opaque then not selected, so add the movie to TMDb list
                        LogDisplay.callLog(LOG_TAG, 'User wants to add to TMDb Watchlist, go ahead and do that', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                        new UploadTmdbRequest(getActivity(), GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST, 0,
                                true, mMovieCategory, mUserTmdbListDrawableLayout, mPalleteAccentColor).execute([mMovieId] as Integer[])
                        mUserTmdbListWatchlistFlag = true
                    }
                } else {
                    Snackbar.make(mNestedScrollView, getString(R.string.no_internet_cannot_perform_operation_message), Snackbar.LENGTH_LONG).show()
                }
            }
        })
        mTmdbImageButtonFavourite = mRootView.findViewById(R.id.movie_detail_user_tmdb_list_drawable_favourite) as ImageButton
        mTmdbImageButtonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'Tmdb user favourite button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mImageButtonClickForcedOnLoadFinished = true
                if(Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                    //If full opaque then already selected, so remove it
                    if (mTmdbImageButtonFavourite.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        new UploadTmdbRequest(getActivity(), GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE, 0,
                                false, mMovieCategory, mUserTmdbListDrawableLayout, mPalleteAccentColor).execute([mMovieId] as Integer[])
                        mUserTmdbListFavouriteFlag = false
                    } else { //If 40% opaque then not selected, so add the movie to TMDb list
                        LogDisplay.callLog(LOG_TAG, 'User wants to add to TMDb Favourite, go ahead and do that', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                        new UploadTmdbRequest(getActivity(), GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE, 0,
                                true, mMovieCategory, mUserTmdbListDrawableLayout, mPalleteAccentColor).execute([mMovieId] as Integer[])
                        mUserTmdbListFavouriteFlag = true
                    }
                } else {
                    Snackbar.make(mNestedScrollView, getString(R.string.no_internet_cannot_perform_operation_message), Snackbar.LENGTH_LONG).show()
                }

            }
        })
        mTmdbImageButtonRated = mRootView.findViewById(R.id.movie_detail_user_tmdb_list_drawable_rated) as ImageButton
        mTmdbImageButtonRated.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'Tmdb user rated button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mImageButtonClickForcedOnLoadFinished = true
                if(Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                    //If full opaque then already selected, so show user that they need to change the rating value
                    if (mTmdbImageButtonRated.getAlpha() == GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE) {
                        Snackbar.make(mNestedScrollView, getString(R.string.tmdb_rating_user_prompt_msg), Snackbar.LENGTH_LONG).show()
                    } else { //If 40% opaque then not selected, so add the movie to TMDb list
                        LogDisplay.callLog(LOG_TAG, 'User wants to add to TMDb Rated, try for that..', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                        final float userRatingVal = mUserRatingBar.getRating()
                        if (userRatingVal > 0.0) {
                            LogDisplay.callLog(LOG_TAG, 'Tmdb user rating is greater than zero, so post that to Tmdb', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                            // Rating is driven by value, so fourth parameter does not matter
                            new UploadTmdbRequest(getActivity(), GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED, userRatingVal,
                                    true, mMovieCategory, mUserTmdbListDrawableLayout, mPalleteAccentColor).execute([mMovieId] as Integer[])
                            mUserTmdbListRatedFlag = true
                        } else {
                            Snackbar.make(mNestedScrollView, getString(R.string.tmdb_rating_user_prompt_empty_msg), Snackbar.LENGTH_LONG).show()
                            mUserTmdbListRatedFlag = false
                        }
                    }
                } else {
                    Snackbar.make(mNestedScrollView, getString(R.string.no_internet_cannot_perform_operation_message), Snackbar.LENGTH_LONG).show()
                }
            }
        })

        //All the dynamic fields (data fields) & ratingbar
        mMovieTitleTextView = mRootView.findViewById(R.id.movie_detail_title) as TextView
        // For land mode no need to display the title as that is already displayed as part of collapsing toolbar
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mMovieTitleTextView.setVisibility(TextView.GONE)
        }
        mMpaaRatingImageView = mRootView.findViewById(R.id.movie_detail_mpaa_image) as ImageView
        mGenreTextView = mRootView.findViewById(R.id.movie_detail_title_genre) as TextView
        mRunTimeTextView = mRootView.findViewById(R.id.movie_detail_title_runtime) as TextView
        mPosterImageView = mRootView.findViewById(R.id.movie_detail_poster_image) as ImageView
        mReleaseDateTextView = mRootView.findViewById(R.id.movie_detail_poster_release_date) as TextView
        mBudgetTextView = mRootView.findViewById(R.id.movie_detail_poster_budget) as TextView
        mRevenueTextView = mRootView.findViewById(R.id.movie_detail_poster_revenue) as TextView
        mPopularityTextView = mRootView.findViewById(R.id.movie_detail_poster_popularity) as TextView
        mTmdbRatingBar = mRootView.findViewById(R.id.movie_detail_tmdb_rating_bar) as RatingBar
        /**
         * User rating bar handling
         */
        mUserRatingBar = mRootView.findViewById(R.id.movie_detail_user_rating_bar) as RatingBar
        mUserRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            void onRatingChanged(final RatingBar ratingBar, final float rating, final boolean fromUser) {
                LogDisplay.callLog(LOG_TAG, "onRatingChanged:User rating bar value->$rating", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (mMovieTitle && mMovieId) {
                    final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                            mMovieId, mMovieTitle, true)
                    final String[] updateUserListArgs
                    //If the rating value is zero then remove it
                    if (rating == 0.0) {
                        //Pass the third parameter as rating param
                        LogDisplay.callLog(LOG_TAG, 'onRatingChanged:User rating remove', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_USER_RATING, GlobalStaticVariables.USER_RATING_REMOVE_FLAG, String.valueOf(rating)]
                        updateUserList.execute(updateUserListArgs)
                    } else { //Else if rating > 0.0 then add / update it
                        //Pass the third parameter as rating param
                        LogDisplay.callLog(LOG_TAG, 'onRatingChanged:User rating add', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                        updateUserListArgs = [GlobalStaticVariables.USER_LIST_USER_RATING, GlobalStaticVariables.USER_RATING_ADD_FLAG, String.valueOf(rating)]
                        updateUserList.execute(updateUserListArgs)
                    }
                    // Create alert dialog and take action only if the movie is part of user's TMDb Rated list
                    if(mUserTmdbListRatedFlag) {
                        createDialogForTmdbRatingConfirmation(rating)
                    }
                }
            }
        })
        mTotalVoteCountTextView = mRootView.findViewById(R.id.movie_detail_tmdb_rating_vote_count_val) as TextView
        mTaglineTextView = mRootView.findViewById(R.id.movie_detail_synopsis_tagline) as TextView
        mSynopsisTextView = mRootView.findViewById(R.id.movie_detail_synopsis) as TextView
        mProdCompanyTextView = mRootView.findViewById(R.id.movie_detail_production_info_cmpy) as TextView
        mProdCountryTextView = mRootView.findViewById(R.id.movie_detail_production_info_country) as TextView
        mCollectionNameTextView = mRootView.findViewById(R.id.movie_detail_collection_name) as TextView
        /**
         * Collection backdrop image handling
         */
        mCollectionBackdropImageView = mRootView.findViewById(R.id.movie_detail_collection_image) as ImageView
        mCollectionBackdropImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                if(mCollectionId > 0) {
                    LogDisplay.callLog(LOG_TAG, 'Collection backdrop image is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    //Create an intent for collection activity
                    final Uri uri = MovieMagicContract.MovieCollection.buildMovieCollectionUriWithCollectionId(mCollectionId)
                    final Intent intent = new Intent(getActivity(),CollectionMovieActivity.class)
                    intent.setData(uri)
                    getActivity().startActivity(intent)
                    //Start the animation
                    getActivity().overridePendingTransition(R.anim.slide_bottom_in_animation,0)
                } else {
                    LogDisplay.callLog(LOG_TAG, "Invalid collection id.id->$mCollectionId", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                }
            }
        })
        /**
         * Movie Cast Grid handling
         */
        mHorizontalMovieCastGridView = mRootView.findViewById(R.id.movie_detail_cast_grid) as HorizontalGridView
        mCastGridEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_cast_grid_empty_msg_text_view) as TextView
        mCastGridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mHorizontalMovieCastGridView.setLayoutManager(mCastGridLayoutManager)
        mMovieCastAdapter = new MovieCastAdapter(getActivity(), mCastGridEmptyMsgTextView,
                        new MovieCastAdapter.MovieCastAdapterOnClickHandler(){
                            @Override
                            void onClick(final int personId) {
                                launchPersonActivity(personId)
                            }
                        })
        mHorizontalMovieCastGridView.setAdapter(mMovieCastAdapter)

        /**
         * Movie Crew Grid handling
         */
        mHorizontalMovieCrewGridView = mRootView.findViewById(R.id.movie_detail_crew_grid) as HorizontalGridView
        mCrewGridEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_crew_grid_empty_msg_text_view) as TextView
        mCrewGridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mHorizontalMovieCrewGridView.setLayoutManager(mCrewGridLayoutManager)
        mMovieCrewAdapter = new MovieCrewAdapter(getActivity(), mCrewGridEmptyMsgTextView,
                new MovieCrewAdapter.MovieCrewAdapterOnClickHandler(){
                    @Override
                    void onClick(final int personId) {
                        launchPersonActivity(personId)
                    }
                })
        mHorizontalMovieCrewGridView.setAdapter(mMovieCrewAdapter)

        /**
         * Similar movie Grid handling
         */
        mHorizontalSimilarMovieGridView = mRootView.findViewById(R.id.movie_detail_similar_movie_grid) as HorizontalGridView
        mSimilarMovieGridEmptyMsgTextView = mRootView.findViewById(R.id.movie_detail_similar_movie_grid_empty_msg_text_view) as TextView
        mSimilarMovieGridLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false)
        mHorizontalSimilarMovieGridView.setLayoutManager(mSimilarMovieGridLayoutManager)
        mSimilarMovieAdapterOnClickHandler = new SimilarMovieAdapter.SimilarMovieAdapterOnClickHandler(){
            @Override
            void onClick(final int movieId) {
                mCallbackForSimilarMovieClick.onSimilarMovieItemSelected(movieId)
            }
        }

        mSimilarMovieAdapter = new SimilarMovieAdapter(getActivity(), mSimilarMovieGridEmptyMsgTextView,
                mSimilarMovieAdapterOnClickHandler)
        mHorizontalSimilarMovieGridView.setAdapter(mSimilarMovieAdapter)

        /**
         * External web link button handling
         */
        mHomePageButton = mRootView.findViewById(R.id.movie_detail_web_links_home_page_button) as Button
        mHomePageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'Home Page Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                startHomePageIntent()
            }
        })
        mImdbLinkButton = mRootView.findViewById(R.id.movie_detail_web_links_imdb_link_button) as Button
        mImdbLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG, 'IMDb Button is clicked', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                startImdbIntent()
            }
        })
        /**
         * Review recycler view handling
         */
        mMovieReviewRecyclerView = mRootView.findViewById(R.id.movie_detail_review_recycler_view) as RecyclerView
        //Set this to false for smooth scrolling of recyclerview
        mMovieReviewRecyclerView.setNestedScrollingEnabled(false)
        mReviewLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
        mReviewLinearLayoutManager.setAutoMeasureEnabled(true)
        mMovieReviewRecyclerView.setLayoutManager(mReviewLinearLayoutManager)
        mMovieReviewAdapter = new MovieReviewAdapter(getActivity(), mRecyclerViewEmptyMsgTextView)
        mMovieReviewRecyclerView.setAdapter(mMovieReviewAdapter)

        /** If the user is not logged in to Tmdb account then hide the Tmdb user list layout **/
        if (MovieMagicMainActivity.isUserLoggedIn) {
            mUserTmdbListDrawableLayout.setVisibility(RelativeLayout.VISIBLE)
        } else {
            mUserTmdbListDrawableLayout.setVisibility(RelativeLayout.GONE)
        }
        return mRootView
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onActivityCreated is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onActivityCreated(savedInstanceState)

        // Determine locale
        mLocale = context.getResources().getConfiguration().locale.getCountry()
//        mLocale = 'IN' // Testing line
        // Currently program handles only 'GB' (i.e. UK) and 'US' locales
        // If not any of the above then fallback to US locale
        switch (mLocale) {
            case 'GB':
            case 'US':
            // do nothing
                break
            default:
                mLocale = 'US'
        }

        LogDisplay.callLog(LOG_TAG, "Locale: $mLocale", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (mMovieId && mMovieCategory) {
            mMovieIdArg = [Integer.toString(mMovieId)] as String[]
            mMovieIdCategoryArg = [Integer.toString(mMovieId), mMovieCategory] as String[]
            mVideoArg = [Integer.toString(mMovieId), GlobalStaticVariables.MOVIE_VIDEO_SITE_YOUTUBE, GlobalStaticVariables.MOVIE_VIDEO_SITE_TYPE] as String[]
            mReleaseInfoArg = [Integer.toString(mMovieId), mLocale] as String[]
            mMovieImageArg = [Integer.toString(mMovieId), GlobalStaticVariables.IMAGE_TYPE_BACKDROP] as String[]
        } else {
            //This is to safeguard any unwanted data fetch
            mMovieIdArg = ['ZZZZZZ'] as String[]
            mMovieIdCategoryArg = ['XXXXXX', 'YYYYY'] as String[]
            mVideoArg = ['XXXXXX', 'YYYYY', 'ZZZZZZ'] as String[]
            mReleaseInfoArg = ['YYYYY', 'ZZZZZZ'] as String[]
            mMovieImageArg = ['YYYYY', 'ZZZZZZ'] as String[]
        }

        /** Using of fragment loader (i.e. getLoaderManager() leaks memory when more than one fragment is loaded and
         * orientation changes. So to fix it (a workaround) the activity's loader manager is used instead. But the
         * drawback is that we always need to use restartLoader otherwise second fragment onwards the data will not be
         * loaded because the LoaderManager will think that it's already initiated! another drawback is that no loader id
         * of the fragment should clash with loader id of activity because it's same LoaderManager!! **/
        LogDisplay.callLog(LOG_TAG, 'onActivityCreated:since we use activity loader.so always restart', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID, null, this)
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID, null, this)
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID, null, this)
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID, null, this)
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID, null, this)
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID, null, this)
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID, null, this)
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_REVIEW_LOADER_ID, null, this)
        getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_MOVIE_USER_LIST_FLAG_LOADER_ID, null, this)
        if (savedInstanceState == null) {
            mBackdropViewPagerPos = 0
            // Start Tmdb data loader only if user is logged in
            if(MovieMagicMainActivity.isUserLoggedIn) {
                getActivity().getSupportLoaderManager().initLoader(MOVIE_DETAIL_FRAGMENT_BASIC_TMDB_DATA_LOADER_ID, null, this)
            }
        } else {
            mBackdropViewPagerPos = savedInstanceState.getInt(GlobalStaticVariables.DETAIL_BACKDROP_VIEWPAGER_POSITION,0)
            // Restart Tmdb loader only if user is logged in
            if(MovieMagicMainActivity.isUserLoggedIn) {
                getActivity().getSupportLoaderManager().restartLoader(MOVIE_DETAIL_FRAGMENT_BASIC_TMDB_DATA_LOADER_ID, null, this)
            }
        }
    }

    @Override
    void onStart() {
        super.onStart()
        LogDisplay.callLog(LOG_TAG, 'onStart is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
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

        // Create a fade animation
        final Animation fadeIn = new AlphaAnimation(0, 1)
        fadeIn.setInterpolator(new DecelerateInterpolator())
        fadeIn.setDuration(2000)
        mRootView.setAnimation(fadeIn)

        //Show the title only when image is collapsed
        //Do this only if the collapsing toolbar is available
        if(mCollapsingToolbar) {
            //Show the title only when image is collapsed
            mAppbarOnOffsetChangeListener = new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false
                int scrollRange = -1

                @Override
                void onOffsetChanged(final AppBarLayout appBarLayout, final int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange()
                    }
                    if (scrollRange + verticalOffset == 0) {
                        mCollapsingToolbar.setTitle(mMovieTitle)
                        isShow = true
                    } else if (isShow) {
                        mCollapsingToolbar.setTitle(" ")
                        isShow = false
                    }
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu (final Menu menu, final MenuInflater inflater) {
        LogDisplay.callLog(LOG_TAG, 'onCreateOptionsMenu is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        // Inflate the menu, this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment_menu, menu)
    }

    @Override
    boolean onOptionsItemSelected(final MenuItem item) {
        LogDisplay.callLog(LOG_TAG, 'onOptionsItemSelected is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if(item.getItemId() == R.id.menu_action_share) {
            if(mMoviDataLoaded) {
                shareMovie()
            } else {
                Snackbar.make(mNestedScrollView, getString(R.string.detail_movie_share_cannot_perform), Snackbar.LENGTH_LONG).show()
                LogDisplay.callLog(LOG_TAG,'onOptionsItemSelected: movie detail data not yet loaded. Try again later!',LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        LogDisplay.callLog(LOG_TAG, "onCreateLoader.loader id->$id", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        switch (id) {
            case MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID:
                /** Warning ** -> this cursor can return more than one row for similar movies **/
                return new CursorLoader(
                        getActivity(),                                                        //Parent Activity Context
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,                        //Table to query
                        MOVIE_BASIC_INFO_COLUMNS,                                             //Projection to return
                        /** Orphaned category is used to handle scenario where movie is opened from user or tmdb list
                         * then movie is removed, moreover tried again to add to same list or other**/
                        """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? and
                           $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY in
                          ('$mMovieCategory','$GlobalStaticVariables.MOVIE_CATEGORY_ORPHANED') """,  //Selection Clause
                        mMovieIdArg,                                                          //Selection Arg
                        null)                                                                 //Only a single row is expected, so not sorted

            case MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                          //Parent Activity Context
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,                          //Table to query
                        SIMILAR_MOVIE_COLUMNS,                                                  //Projection to return
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID = ?", //Selection Clause
                        mMovieIdArg,                                                            //Selection Arg
                        null)                                                                   //Not bother on sorting

            case MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                        //Parent Activity Context
                        MovieMagicContract.MovieVideo.CONTENT_URI,                            //Table to query
                        MOVIE_VIDEO_COLUMNS,                                                  //Projection to return
                        """$MovieMagicContract.MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID = ? and
                            $MovieMagicContract.MovieVideo.COLUMN_VIDEO_SITE = ? and
                            $MovieMagicContract.MovieVideo.COLUMN_VIDEO_TYPE = ? """,         //Selection Clause
                        mVideoArg,                                                            //Selection Arg
                        null)                                                                 //Not bother on sorting

            case MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                  //Parent Activity Context
                        MovieMagicContract.MovieCast.CONTENT_URI,                       //Table to query
                        MOVIE_CAST_COLUMNS,                                             //Projection to return
                        "$MovieMagicContract.MovieCast.COLUMN_CAST_ORIG_MOVIE_ID = ?",  //Selection Clause
                        mMovieIdArg,                                                    //Selection Arg
                        MovieMagicContract.MovieCast.COLUMN_CAST_ORDER)                 //Sorted on the order

            case MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                  //Parent Activity Context
                        MovieMagicContract.MovieCrew.CONTENT_URI,                       //Table to query
                        MOVIE_CREW_COLUMNS,                                             //Projection to return
                        "$MovieMagicContract.MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID = ?",  //Selection Clause
                        mMovieIdArg,                                                    //Selection Arg
                        """$MovieMagicContract.MovieCrew.COLUMN_CREW_PROFILE_PATH desc,
                           $MovieMagicContract.MovieCrew.COLUMN_CREW_JOB asc""")        //Sorted on the profile path(desc), job(asc)

            case MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                               //Parent Activity Context
                        MovieMagicContract.MovieReleaseDate.CONTENT_URI,                             //Table to query
                        MOVIE_RELEASE_INFO_COLUMNS,                                                  //Projection to return
                        """$MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID = ? and
                            $MovieMagicContract.MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY = ? """, //Selection Clause
                        mReleaseInfoArg,                                                             //Selection Arg
                        null)                                                                        //Sorting not used

            case MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                     //Parent Activity Context
                        MovieMagicContract.MovieImage.CONTENT_URI,                         //Table to query
                        MOVIE_IMAGE_COLUMNS,                                               //Projection to return
                        """$MovieMagicContract.MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID = ? and
                            $MovieMagicContract.MovieImage.COLUMN_IMAGE_TYPE = ? """,      //Selection Clause
                        mMovieImageArg,                                                    //Selection Arg
                        null)                                                              //Sorting not used

            case MOVIE_DETAIL_FRAGMENT_MOVIE_REVIEW_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                      //Parent Activity Context
                        MovieMagicContract.MovieReview.CONTENT_URI,                         //Table to query
                        MOVIE_REVIEW_COLUMNS,                                               //Projection to return
                        "$MovieMagicContract.MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID = ?",  //Selection Clause
                        mMovieIdArg,                                                        //Selection Arg
                        MovieMagicContract.MovieReview.COLUMN_REVIEW_AUTHOR)                //Sorted on the author

            case MOVIE_DETAIL_FRAGMENT_MOVIE_USER_LIST_FLAG_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                                   //Parent Activity Context
                        MovieMagicContract.MovieUserListFlag.CONTENT_URI,                                //Table to query
                        MOVIE_USER_LIST_FLAG_COLUMNS,                                                    //Projection to return
                        "$MovieMagicContract.MovieUserListFlag.COLUMN_USER_LIST_FLAG_ORIG_MOVIE_ID = ?", //Selection Clause
                        mMovieIdArg,                                                                     //Selection Arg
                        null)                                                                            //Sorting not used

            case MOVIE_DETAIL_FRAGMENT_BASIC_TMDB_DATA_LOADER_ID:
                return new CursorLoader(
                        getActivity(),                                                    //Parent Activity Context
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,                    //Table to query
                        MOVIE_BASIC_INFO_TMDB_COLUMNS,                                    //Projection to return
                        "$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE = ?",  //Selection Clause
                        [GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_USER] as String[],    //Selection Arg (select all Tmdb movies)
                        null)                                                             //Sorting not used
            default:
                return null
        }
    }

    @Override
    void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        final int loaderId = loader.getId()
        LogDisplay.callLog(LOG_TAG, "onLoadFinished.loader id->$loaderId", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        switch (loaderId) {
            case MOVIE_DETAIL_FRAGMENT_BASIC_DATA_LOADER_ID:
                handleMovieBasicOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_SIMILAR_MOVIE_LOADER_ID:
                handleSimilarMovieOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_VIDEO_LOADER_ID:
                initiateYouTubeVideo(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_CAST_LOADER_ID:
                handleMovieCastOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_CREW_LOADER_ID:
                handleMovieCrewOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_RELEASE_INFO_LOADER_ID:
                populateMpaaImage(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_IMAGE_LOADER_ID:
                processBackdropImages(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_REVIEW_LOADER_ID:
                handleMovieReviewOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_MOVIE_USER_LIST_FLAG_LOADER_ID:
                handleMovieUserListFlagOnLoadFinished(data)
                break
            case MOVIE_DETAIL_FRAGMENT_BASIC_TMDB_DATA_LOADER_ID:
                handleTmdbMovieOnLoadFinished(data)
                break
            default:
                LogDisplay.callLog(LOG_TAG, "Unknown loader id. id->$loaderId", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }

    @Override
    void onLoaderReset(final Loader<Cursor> loader) {
        LogDisplay.callLog(LOG_TAG, 'onLoaderReset is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        // Reset the adapters
        // Since the loaders and adapters are released manually in onStop, so the null check is done for all the adapters
        // to ensure that app does not fail when onDestroy forces onLoaderReset call
        if(mSimilarMovieAdapter) mSimilarMovieAdapter.swapCursor(null)
        if(mMovieCastAdapter) mMovieCastAdapter.swapCursor(null)
        if(mMovieCrewAdapter) mMovieCrewAdapter.swapCursor(null)
        if(mMovieReviewAdapter) mMovieReviewAdapter.swapCursor(null)
    }

    /**
     * This method is called when the loader is finished for movie basic info table
     * @param data Cursor The cursor returned by the loader
     */
    void handleMovieBasicOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieBasicOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        // The following IF condition is to ensure page's position does not change due to reloading data (causes by
        // notifydataset change which gets triggered when user list movie or user tmdb list movie gets added/deleted in
        // movie_basic_info table)
        if(!mImageButtonClickForcedOnLoadFinished) {
            if (data.moveToFirst()) {
                // If more than one record found then it's because of orphaned record (check loader section for details)
                // So if the first record of the cursor is orphaned record then move to next and use that for displaying data
                if (data.getCount() > 1 && data.getString(COL_MOVIE_BASIC_MOVIE_CATEGORY) != mMovieCategory) {
                    data.moveToNext()
                }
                _ID_movie_basic_info = data.getInt(COL_MOVIE_BASIC_ID)
                LogDisplay.callLog(LOG_TAG, "handleMovieBasicOnLoadFinished.Movie row id -> $_ID_movie_basic_info", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                LogDisplay.callLog(LOG_TAG, "handleMovieBasicOnLoadFinished.Movie Category -> ${data.getString(COL_MOVIE_BASIC_MOVIE_CATEGORY)}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mOriginalBackdropPath = data.getString(COL_MOVIE_BASIC_BACKDROP_PATH)
                mMovieTitle = data.getString(COL_MOVIE_BASIC_TITLE)
                mMovieTitleTextView.setText(mMovieTitle)
                if (data.getString(COL_MOVIE_BASIC_GENRE)) {
                    mGenreTextView.setText(data.getString(COL_MOVIE_BASIC_GENRE))
                } else {
                    mGenreTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }
                if (data.getInt(COL_MOVIE_BASIC_RUNTIME) > 0) {
                    mRunTimeTextView.setText(Utility.formatRunTime(getActivity(), data.getInt(COL_MOVIE_BASIC_RUNTIME)))
                } else {
                    mRunTimeTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }

                final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                        "${data.getString(COL_MOVIE_BASIC_POSTER_PATH)}"
                //Create a picasso Callback
                final Callback picassoPosterCallback = new Callback() {
                    @Override
                    void onSuccess() {
                        LogDisplay.callLog(LOG_TAG, 'Picasso callback: onSuccess is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                        // If user does not select dynamic theme (default value) then do not change the color
                        if (Utility.isDynamicTheme(getActivity())) {
                            final Bitmap bitmapPoster = ((BitmapDrawable) mPosterImageView.getDrawable()).getBitmap()
                            Palette.from(bitmapPoster).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(final Palette p) {
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
                                        LogDisplay.callLog(LOG_TAG, 'onGenerated:not able to pick color, so fallback', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                                        mPalletePrimaryColor = ContextCompat.getColor(getActivity(), R.color.primary)
                                        mPalletePrimaryDarkColor = ContextCompat.getColor(getActivity(), R.color.primary_dark)
                                        mPalleteTitleColor = ContextCompat.getColor(getActivity(), R.color.primary_text)
                                        mPalleteBodyTextColor = ContextCompat.getColor(getActivity(), R.color.secondary_text)
                                        //This is needed as we are not going to pick up accent colour if falling back
                                        mPalleteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)
                                    }
                                    //Pick accent color only if Swatch color is picked, otherwise do not pick accent color
                                    /** Commenting it out as some times the contrast is not good between accent and Primary Dark **/
                                    /** Let's set the accent color to default values **/
                                    /** Didn't cleanup the lines of code where accent color is applied, so there will be some
                                     * redundant lines of code but kept it as is in case those are needed in future **/
                                    mPalleteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)

                                    // Apply the color based on orientation
                                    if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                                        // Apply layout and text color only for portrait mode
                                        changeLayoutAndTextColor()
                                        // Apply the color for all attached adapters
                                        mMovieCastAdapter.changeColor(mPalletePrimaryDarkColor, mPalleteBodyTextColor)
                                        mMovieCrewAdapter.changeColor(mPalletePrimaryDarkColor, mPalleteBodyTextColor)
                                        mSimilarMovieAdapter.changeColor(mPalletePrimaryDarkColor, mPalleteBodyTextColor)
                                        mMovieReviewAdapter.changeColor(mPalletePrimaryColor, mPalleteTitleColor, mPalleteBodyTextColor)
                                    } else {
                                        mMovieCastAdapter.changeColor(mPalletePrimaryDarkColor, ContextCompat.getColor(getActivity(), R.color.primary_text))
                                        mMovieCrewAdapter.changeColor(mPalletePrimaryDarkColor, ContextCompat.getColor(getActivity(), R.color.primary_text))
                                        mSimilarMovieAdapter.changeColor(mPalletePrimaryDarkColor, ContextCompat.getColor(getActivity(), R.color.primary_text))
                                        mMovieReviewAdapter.changeColor(0, ContextCompat.getColor(getActivity(), R.color.primary_text), ContextCompat.getColor(getActivity(), R.color.secondary_text))
                                    }
                                    setImageButtonBackgroundColor()
                                    initializeTitleAndColor()
                                    LogDisplay.callLog(LOG_TAG, 'calling setActiveImageButtonColor position -> 1', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                                    setActiveImageButtonColor()
                                    setRatingStarColor()
                                }
                            })
                        } else { // To ensure ImageButton & rating star color is set for static theme
                            mPalleteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)
                            LogDisplay.callLog(LOG_TAG, 'calling setActiveImageButtonColor position -> 2', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                            setActiveImageButtonColor()
                            setRatingStarColor()
                        }
                    }

                    @Override
                    void onError() {
                        LogDisplay.callLog(LOG_TAG, 'Picasso callback: onError is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    }
                }

                // When user selects reduce data usage option or application is not ready to download then
                // the Picasso call does not get called as Picasso just add a placeholder
                // So ensure that the ImageButton color, Rating star color and accent color are properly set
                if (Utility.isReducedDataOn(getActivity()) || !Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                    mPalleteAccentColor = ContextCompat.getColor(getActivity(), R.color.accent)
                    LogDisplay.callLog(LOG_TAG, 'calling setActiveImageButtonColor position -> 3', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    setActiveImageButtonColor()
                    setRatingStarColor()
                }

                // Set the listener if in portrait mode pf mobile or show the title in Toolbar for landscape
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mAppBarLayout.addOnOffsetChangedListener(mAppbarOnOffsetChangeListener)
                } else {
                    mCollapsingToolbar.setTitleEnabled(false)
                    mToolbar.setTitle(mMovieTitle)
                }

                //Pass the Picasso Callback and load the image
                PicassoLoadImage.loadDetailFragmentPosterImage(getActivity(), posterPath, mPosterImageView, picassoPosterCallback)
                //Default date is 1900-01-01 which is less than Unix epoc 1st Jan 1970, so converted milliseconds is negative
                if (data.getLong(COL_MOVIE_BASIC_RELEASE_DATE) > 0) {
                    mReleaseDateTextView.setText(Utility.formatMilliSecondsToDate(data.getLong(COL_MOVIE_BASIC_RELEASE_DATE)))
                } else {
                    mReleaseDateTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }
                if (data.getInt(COL_MOVIE_BASIC_BUDGET) > 0) {
                    mBudgetTextView.setText(Utility.formatCurrencyInDollar(data.getInt(COL_MOVIE_BASIC_BUDGET)))
                } else {
                    mBudgetTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }
                if (data.getInt(COL_MOVIE_BASIC_REVENUE)) {
                    mRevenueTextView.setText(Utility.formatCurrencyInDollar(data.getInt(COL_MOVIE_BASIC_REVENUE)))
                } else {
                    mRevenueTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }
                if (data.getFloat(COL_MOVIE_BASIC_POPULARITY) > 0) {
                    final BigDecimal popularityBigDecimal = new BigDecimal(Float.toString(data.getFloat(COL_MOVIE_BASIC_POPULARITY)))
                    popularityBigDecimal = popularityBigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP)
                    mPopularityTextView.setText(Float.toString(popularityBigDecimal as Float))
                } else {
                    mPopularityTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }
                if (data.getFloat(COL_MOVIE_BASIC_VOTE_AVG) > 0) {
                    mTmdbRatingBar.setRating(data.getFloat(COL_MOVIE_BASIC_VOTE_AVG))
                } else {
                    mTmdbRatingBar.setRating(0)
                }
                mTotalVoteCountTextView.setText(data.getString(COL_MOVIE_BASIC_VOTE_COUNT))
                if (data.getString(COL_MOVIE_BASIC_TAGLINE)) {
                    mTaglineTextView.setText(data.getString(COL_MOVIE_BASIC_TAGLINE))
                } else {
                    mTaglineTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }
                if (data.getString(COL_MOVIE_BASIC_OVERVIEW) != '') {
                    mSynopsisTextView.setText(data.getString(COL_MOVIE_BASIC_OVERVIEW))
                } else {
                    mSynopsisTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }
                if (data.getString(COL_MOVIE_BASIC_PRODUCTION_COMPANIES)) {
                    mProdCompanyTextView.setText(data.getString(COL_MOVIE_BASIC_PRODUCTION_COMPANIES))
                } else {
                    mProdCompanyTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }
                if (data.getString(COL_MOVIE_BASIC_PRODUCTION_COUNTRIES)) {
                    mProdCountryTextView.setText(data.getString(COL_MOVIE_BASIC_PRODUCTION_COUNTRIES))
                } else {
                    mProdCountryTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                }
                mCollectionId = data.getInt(COL_MOVIE_BASIC_COLLECTION_ID)
                if (mCollectionId > 0) {
                    mCollectionNameTextView.setText(data.getString(COL_MOVIE_BASIC_COLLECTION_NAME))
                    mCollectionBackdropImageView.setVisibility(ImageView.VISIBLE)
                    final String collectionBackdropPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
                            "${data.getString(COL_MOVIE_BASIC_COLLECTION_BACKDROP_PATH)}"
                    PicassoLoadImage.loadDetailFragmentCollectionBackdropImage(getActivity(), collectionBackdropPath, mCollectionBackdropImageView)
                } else {
                    mCollectionNameTextView.setText(getActivity().getString(R.string.movie_data_not_available))
                    mCollectionBackdropImageView.setVisibility(ImageView.GONE)
                }

                LogDisplay.callLog(LOG_TAG, "homePage:${data.getString(COL_MOVIE_BASIC_HOME_PAGE)}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (data.getString(COL_MOVIE_BASIC_HOME_PAGE)) {
                    mMovieHomePageUrl = data.getString(COL_MOVIE_BASIC_HOME_PAGE)
                    mHomePageButton.setText(getActivity().getString(R.string.movie_detail_web_links_home_page))
                    mHomePageButton.setClickable(true)
                    mHomePageButton.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                    if (Build.VERSION.SDK_INT >= 21) { // Version 21 -> Build.VERSION_CODES.LOLLIPOP
                        mHomePageButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION)
                    }
                } else {
                    mHomePageButton.setText(getActivity().getString(R.string.movie_detail_web_links_home_page_not_available))
                    mHomePageButton.setClickable(false)
                    mHomePageButton.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                    if (Build.VERSION.SDK_INT >= 21) { // Version 21 -> Build.VERSION_CODES.LOLLIPOP
                        mHomePageButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION_RESET)
                    }
                }
                LogDisplay.callLog(LOG_TAG, "IMDb ID:${data.getString(COL_MOVIE_BASIC_IMDB_ID)}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (data.getString(COL_MOVIE_BASIC_IMDB_ID)) {
                    mMovieImdbId = data.getString(COL_MOVIE_BASIC_IMDB_ID)
                    mImdbLinkButton.setText(getActivity().getString(R.string.detail_web_links_imdb_link))
                    mImdbLinkButton.setClickable(true)
                    mImdbLinkButton.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                    if (Build.VERSION.SDK_INT >= 21) { // Version 21 -> Build.VERSION_CODES.LOLLIPOP
                        mImdbLinkButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION)
                    }
                } else {
                    mImdbLinkButton.setText(getActivity().getString(R.string.movie_detail_web_links_imdb_link_not_available))
                    mImdbLinkButton.setClickable(false)
                    mImdbLinkButton.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT)
                    if (Build.VERSION.SDK_INT >= 21) { // Version 21 -> Build.VERSION_CODES.LOLLIPOP
                        mImdbLinkButton.setElevation(GlobalStaticVariables.MOVIE_MAGIC_ELEVATION_RESET)
                    }
                }
                final int detailDataPresentFlag = data.getInt(COL_MOVIE_BASIC_DETAIL_DATA_PRESENT_FLAG)
                //If the flag is zero then all the movie data are not present, so go and fetch it
                if (detailDataPresentFlag == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE) {
                    LogDisplay.callLog(LOG_TAG, 'handleMovieBasicOnLoadFinished.Additional movie data not present, go and fetch it', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    final ArrayList<Integer> movieIdList = new ArrayList<>(1)
                    final ArrayList<Integer> movieRowIdList = new ArrayList<>(1)
                    final ArrayList<Integer> isForHomeList = new ArrayList<>(1)
                    final ArrayList<Integer> categoryFlag = new ArrayList<>(1)
                    movieIdList.add(0, mMovieId)
                    movieRowIdList.add(0, _ID_movie_basic_info)
                    // Set the flag to false to indicate it's not for home page
                    isForHomeList.add(0, GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE)
                    categoryFlag.add(0, GlobalStaticVariables.NULL_CATEGORY_FLAG)
                    final ArrayList<Integer>[] loadMovieDetailsArg = [movieIdList, movieRowIdList, isForHomeList, categoryFlag] as ArrayList<Integer>[]
                    if (Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                        new LoadMovieDetails(getActivity()).execute(loadMovieDetailsArg)
                    } else {
                        LogDisplay.callLog(LOG_TAG, '1-> Device is offline or connected to internet without WiFi and user selected download only on WiFi', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'handleMovieBasicOnLoadFinished.Additional movie data already present', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                }
                mMoviDataLoaded = true
            } else {
                LogDisplay.callLog(LOG_TAG, "handleMovieBasicOnLoadFinished.Record not found, should reach here only when movie is clicked on person or search screen - Movie id:$mMovieId, Category:$mMovieCategory", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if (mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_PERSON || GlobalStaticVariables.MOVIE_CATEGORY_SEARCH) {
                    //Movie does not exists, go and fetch then insert into movie basic info table
                    LogDisplay.callLog(LOG_TAG, 'handleMovieBasicOnLoadFinished.Movie for person or search does not exists, go and fetch then insert into movie basic info table', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    final ArrayList<Integer> movieIdList = new ArrayList<>(1)
                    final ArrayList<Integer> movieRowIdList = new ArrayList<>(1)
                    final ArrayList<Integer> isForHomeList = new ArrayList<>(1)
                    final ArrayList<Integer> categoryFlag = new ArrayList<>(1)
                    movieIdList.add(0, mMovieId)
                    movieRowIdList.add(0, 0)
                    isForHomeList.add(0, GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE)
                    if (mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_PERSON) {
                        categoryFlag.add(0, GlobalStaticVariables.PERSON_CATEGORY_FLAG)
                    } else if (mMovieCategory == GlobalStaticVariables.MOVIE_CATEGORY_SEARCH) {
                        categoryFlag.add(0, GlobalStaticVariables.SEARCH_CATEGORY_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Shouldn't reach here. please investigate -> $mMovieCategory", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                        categoryFlag.add(0, GlobalStaticVariables.NULL_CATEGORY_FLAG)
                    }
                    final ArrayList<Integer>[] loadMovieDetailsArg = [movieIdList, movieRowIdList, isForHomeList, categoryFlag] as ArrayList<Integer>[]
                    if (Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                        new LoadMovieDetails(getActivity()).execute(loadMovieDetailsArg)
                    } else {
                        LogDisplay.callLog(LOG_TAG, '2-> Device is offline or connected to internet without WiFi and user selected download only on WiFi', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, "New scenario, investigate how it reached here - Movie id:$mMovieId, Category:$mMovieCategory", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                }
            }
        } else {
            LogDisplay.callLog(LOG_TAG, 'This is an ImageButton Click Forced OnLoadFinished', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            mImageButtonClickForcedOnLoadFinished = false
        }
    }

    /**
     * This method is called when the loader is finished for similar movies in movie basic info table
     * @param data Cursor The cursor returned by the loader
     */
    void handleSimilarMovieOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleSimilarMovieOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        //Show two rows if the count is greater than 6 otherwise show single row
        if(data.count >= 6) {
            mSimilarMovieGridLayoutManager.setSpanCount(2)
        }
        mSimilarMovieAdapter.swapCursor(data)
    }

    /**
     * This method is called when the loader is finished for movie cast table
     * @param data Cursor The cursor returned by the loader
     */
    void handleMovieCastOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieCastOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mMovieCastAdapter.swapCursor(data)
    }

    /**
     * This method is called when the loader is finished for movie crew table
     * @param data Cursor The cursor returned by the loader
     */
    void handleMovieCrewOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieCrewOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mMovieCrewAdapter.swapCursor(data)
    }

    /**
     * This method is called when the loader is finished for movie video table
     * @param data Cursor The cursor returned by the loader
     */
    void initiateYouTubeVideo(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "initiateYouTubeVideo.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (data.moveToFirst()) {
            final List<String> youtubeVideoKey = new ArrayList<>()
            for (final i in 0..(data.count - 1)) {
                youtubeVideoKey.add(data.getString(COL_MOVIE_VIDEO_KEY))
                LogDisplay.callLog(LOG_TAG, "YouTube now_playing key= $youtubeVideoKey", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                data.moveToNext()
            }
            if(youtubeVideoKey.size() > 0 && !Utility.isReducedDataOn(getActivity())) {
                mMovieTrailerEmptyMsgTextView.setVisibility(TextView.INVISIBLE)
                if(!getChildFragmentManager().findFragmentByTag(DETAIL_YOUTUBE_FRAGMENT_TAG)) {
                    LogDisplay.callLog(LOG_TAG, "initiateYouTubeVideo:Youtube fragment does not exists, so create a new one with tag->$DETAIL_YOUTUBE_FRAGMENT_TAG", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    final MovieMagicYoutubeFragment movieMagicYoutubeFragment = MovieMagicYoutubeFragment
                            .createMovieMagicYouTubeFragment(youtubeVideoKey)
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.youtube_fragment_container, movieMagicYoutubeFragment, DETAIL_YOUTUBE_FRAGMENT_TAG)
                            .commit()
                } else {
                    LogDisplay.callLog(LOG_TAG, "initiateYouTubeVideo:Youtube fragment already exists for the tag $DETAIL_YOUTUBE_FRAGMENT_TAG, so no need to create a new one!", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                }
            } else {
                mMovieTrailerEmptyMsgTextView.setVisibility(TextView.VISIBLE)
                LogDisplay.callLog(LOG_TAG, 'Youtube video id is null or user selected reduced data use', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        }
    }

    /**
     * This method is called when the loader is finished for movie release info table
     * @param data Cursor The cursor returned by the loader
     */
    void populateMpaaImage(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "populateMpaaImage.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (data.moveToFirst()) {
            final String mpaa = data.getString(COL_MOVIE_RELEASE_INFO_CERTIFICATION)
            LogDisplay.callLog(LOG_TAG, "Mpaa certification: $mpaa & Locale: $mLocale", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            final int mpaaIconResId = Utility.getIconResourceForMpaaRating(mpaa, mLocale)
            if (mpaaIconResId != -1) {
                mMpaaRatingImageView.setImageResource(mpaaIconResId)
            } else {
                LogDisplay.callLog(LOG_TAG, 'Utility.getIconResourceForMpaaRating returned -1', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                mMpaaRatingImageView.setImageResource(R.drawable.mpaa_na)
            }
        } else {
            LogDisplay.callLog(LOG_TAG, 'Not able to retrieve mpaa image, set default image', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            mMpaaRatingImageView.setImageResource(R.drawable.mpaa_na)
        }
    }

    /**
     * This method is called when the loader is finished for movie image table
     * @param data Cursor The cursor returned by the loader
     */
    void processBackdropImages(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "processBackdropImages.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (data.moveToFirst()) {
            mBackdropList = new ArrayList<String>()
            // Add the original backdrop image (i.e. which comes along with other movie details)
            if (mOriginalBackdropPath) {
                mBackdropList.add(mOriginalBackdropPath)
            }
            for (final i in 0..(data.count - 1)) {
                mBackdropList.add(data.getString(COL_MOVIE_IMAGE_FILE_PATH))
                data.moveToNext()
            }
            LogDisplay.callLog(LOG_TAG, "backdropImageArray-> $mBackdropList", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            mDetailFragmentPagerAdapterOnClickHandler = new DetailFragmentPagerAdapter.DetailFragmentPagerAdapterOnClickHandler() {
                        @Override
                        void onClick(final int position) {
                            LogDisplay.callLog(LOG_TAG, "DetailFragmentPagerAdapter clicked.Position->$position", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                            mCallbackForBackdropImageClick.onBackdropImageClicked(mMovieTitle, position, mBackdropList as ArrayList<String>)
                        }
                    }
            mDetailFragmentPagerAdapter = new DetailFragmentPagerAdapter(getActivity(), mBackdropList as String[],
                    mDetailFragmentPagerAdapterOnClickHandler)
            mBackdropViewPager.setAdapter(mDetailFragmentPagerAdapter)
            mBackdropViewPager.clearOnPageChangeListeners()
            final int dotsCount = mBackdropList.size()
            final AppCompatImageButton[] dotsImage = new AppCompatImageButton[dotsCount]
            mBackdropDotHolderLayout.removeAllViews()
            setBackDropViewPagerDots(dotsCount, dotsImage)
            mOnPageChangeListener = new OnPageChangeListener() {
                @Override
                void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                }

                @Override
                void onPageSelected(final int position) {
                    final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(18,18)
                    final ColorStateList whiteColorStateList = ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white_color))
                    for (final i in 0..(dotsCount - 1)) {
                        if (i != position) {
                            dotsImage[i].setLayoutParams(layoutParams)
                            ViewCompat.setBackgroundTintList(dotsImage[i], whiteColorStateList)
                        } else {
                            dotsImage[position].setLayoutParams(new LinearLayout.LayoutParams(30,30))
                            final ColorStateList accentColorStateList = ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.accent))
                            ViewCompat.setBackgroundTintList(dotsImage[position], accentColorStateList)
                        }
                    }
                    LogDisplay.callLog(LOG_TAG, "position-> $position", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    mBackdropViewPagerPos = position
                }

                @Override
                void onPageScrollStateChanged(final int state) {
                }
            }
            mBackdropViewPager.addOnPageChangeListener(mOnPageChangeListener)
            mBackdropViewPager.setCurrentItem(mBackdropViewPagerPos)
        }
    }

    /**
     * This method is called when the loader is finished for movie review table
     * @param data Cursor The cursor returned by the loader
     */
    void handleMovieReviewOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieReviewOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mMovieReviewAdapter.swapCursor(data)
    }

    /**
     * This method is called when the loader is finished for movie user list flag table
     * @param data Cursor The cursor returned by the loader
     */
    void handleMovieUserListFlagOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleMovieUserListFlagOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mUserListWatchedFlag = false
        mUserListWishListdFlag = false
        mUserListFavouriteFlag = false
        mUserListCollectionFlag = false
        if (data.moveToFirst()) {
            if (data.getInt(COL_MOVIE_USER_LIST_FLAG_WATCHED_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                mImageButtonWatched.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                mUserListWatchedFlag = true
            }
            if (data.getInt(COL_MOVIE_USER_LIST_FLAG_WISH_LIST_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                mImageButtonWishList.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                mUserListWishListdFlag = true
            }
            if (data.getInt(COL_MOVIE_USER_LIST_FLAG_FAVOURITE_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                mImageButtonFavourite.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                mUserListFavouriteFlag = true
            }
            if (data.getInt(COL_MOVIE_USER_LIST_FLAG_COLLECTION_FLAG) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE) {
                mImageButtonCollection.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                mUserListCollectionFlag = true
            }
            if (data.getFloat(COL_MOVIE_USER_LIST_FLAG_USER_RATING) > 0) {
                mUserRatingBar.setRating(data.getFloat(COL_MOVIE_USER_LIST_FLAG_USER_RATING))
            }
            // Now set the color
            LogDisplay.callLog(LOG_TAG, 'calling setActiveImageButtonColor position -> 4', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            setActiveImageButtonColor()
        }
    }

    /**
     * This method is called when the loader is finished for Tmdb movies
     * @param data Cursor The cursor returned by the loader
     */
    void handleTmdbMovieOnLoadFinished(final Cursor data) {
        LogDisplay.callLog(LOG_TAG, "handleTmdbMovieOnLoadFinished.Cursor rec count -> ${data.getCount()}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mUserTmdbListWatchlistFlag = false
        mUserTmdbListFavouriteFlag = false
        mUserTmdbListRatedFlag = false
        // The associated loader is kicked off only when user is logged on, however the check is done here also to be on the safe side
        if (data.moveToFirst() && MovieMagicMainActivity.isUserLoggedIn) {
            boolean isTmdbMovie = false
            float tmdbUserRating = 0.0
            final ArrayList<String> categories = new ArrayList<>()
            for (final i in 0..(data.getCount() - 1)) {
                if(mMovieId == data.getInt(COL_MOVIE_BASIC_TMDB_MOVIE_ID)) {
                    categories.add(data.getString(COL_MOVIE_BASIC_TMDB_MOVIE_CATEGORY))
                    isTmdbMovie = true
                    if(data.getString(COL_MOVIE_BASIC_TMDB_MOVIE_CATEGORY) == GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED) {
                        tmdbUserRating = data.getFloat(COL_MOVIE_BASIC_TMDB_MOVIE_RATING)
                        LogDisplay.callLog(LOG_TAG, "Tmdb user rating -> $tmdbUserRating", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    }
                }
                data.moveToNext()
            }

            // If it's a Tmdb movie then see what we got and set the flags accordingly
            if (isTmdbMovie) {
                LogDisplay.callLog(LOG_TAG, "Movie is in Tmdb list. Categories -> $categories", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                for (final i in 0..(categories.size() -1)) {
                    switch (categories.get(i)) {
                        case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_WATCHLIST:
                            mTmdbImageButtonWatchlist.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                            mUserTmdbListWatchlistFlag = true
                            break
                        case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_FAVOURITE:
                            mTmdbImageButtonFavourite.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                            mUserTmdbListFavouriteFlag = true
                            break
                        case GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED:
                            mTmdbImageButtonRated.setAlpha(GlobalStaticVariables.MOVIE_MAGIC_ALPHA_FULL_OPAQUE)
                            mUserTmdbListRatedFlag = true
                            // Flag firstTimeLocalRatingUpdateWithTmdbRating is used to restrict the call to one only
                            if(tmdbUserRating > 0 && firstTimeLocalRatingUpdateWithTmdbRating) {
                                mUserRatingBar.setRating(tmdbUserRating)
                                // Now update the rating in the local list
                                LogDisplay.callLog(LOG_TAG, 'Going to update local rating with Tmdb rating', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                                final UpdateUserListChoiceAndRating updateUserList = new UpdateUserListChoiceAndRating(getActivity(), mUserListDrawableLayout,
                                        mMovieId, mMovieTitle, false)
                                final String[] updateUserListArgs = [GlobalStaticVariables.USER_LIST_USER_RATING, GlobalStaticVariables.USER_RATING_ADD_FLAG, String.valueOf(tmdbUserRating)]
                                updateUserList.execute(updateUserListArgs)
                                firstTimeLocalRatingUpdateWithTmdbRating = false
                            }
                            break
                        default:
                            LogDisplay.callLog(LOG_TAG, "Unknown user Tmdb category -> ${categories.get(i)}", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                    }
                }
                // Now set the color
                LogDisplay.callLog(LOG_TAG, 'calling setActiveImageButtonColor position -> 5', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                setActiveImageButtonColor()
            } else {
                LogDisplay.callLog(LOG_TAG, "This movie is not in any user Tmdb list", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        } else {
            LogDisplay.callLog(LOG_TAG, "Either cursor is empty or isUserLoggedIn is false. Cursor size -> ${data.getCount()} & isUserLoggedIn falg is -> $MovieMagicMainActivity.isUserLoggedIn", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        }
    }

    /**
     * This method is called to apply the color once that is determined from the poster image
     */
    protected void changeLayoutAndTextColor() {
        //Change color for all the layouts
        mDetailMovieLayout.setBackgroundColor(mPalletePrimaryColor)
        if(getResources().getBoolean(R.bool.is_tablet_port)) {
            mCoordinatorLayout.setBackgroundColor(mPalletePrimaryDarkColor)
        }

        //Change color for header text fields
        mReleaseDateHeaderTextView.setTextColor(mPalleteTitleColor)
        mBudgetHeaderTextView.setTextColor(mPalleteTitleColor)
        mRevenueHeaderTextView.setTextColor(mPalleteTitleColor)
        mPopularityHeaderTextView.setTextColor(mPalleteTitleColor)
        mTmdbRatingHeaderTextView.setTextColor(mPalleteTitleColor)
        mTmdbTotalVoteCountHeaderTextView.setTextColor(mPalleteTitleColor)
        mTmdbTotalVoteCountTrailerTextView.setTextColor(mPalleteTitleColor)
        mUserRatingHeaderTextView.setTextColor(mPalleteTitleColor)
        mTaglineHeaderTextView.setTextColor(mPalleteTitleColor)
        mSynopsisHeaderTextView.setTextColor(mPalleteTitleColor)
        mMovieTrailerHeaderTextView.setTextColor(mPalleteTitleColor)
        mProdCompanyHeaderTextView.setTextColor(mPalleteTitleColor)
        mProdCountryHeaderTextView.setTextColor(mPalleteTitleColor)
        mCastHeaderTextView.setTextColor(mPalleteTitleColor)
        mCrewHeaderTextView.setTextColor(mPalleteTitleColor)
        mSimilarMovieHeaderTextView.setTextColor(mPalleteTitleColor)
        mCollectionNameHeaderTextView.setTextColor(mPalleteTitleColor)
        mReviewHeaderTextView.setTextColor(mPalleteTitleColor)
        mExternalLinkHeader.setTextColor(mPalleteTitleColor)
        mUserListDrawableTitle.setTextColor(mPalleteTitleColor)
        mUserTmdbListDrawableTitle.setTextColor(mPalleteTitleColor)

        //Change color for data fields
        mMovieTitleTextView.setTextColor(mPalleteTitleColor) //Movie name is Title color
        mGenreTextView.setTextColor(mPalleteBodyTextColor)
        mRunTimeTextView.setTextColor(mPalleteBodyTextColor)
        mReleaseDateTextView.setTextColor(mPalleteBodyTextColor)
        mBudgetTextView.setTextColor(mPalleteBodyTextColor)
        mRevenueTextView.setTextColor(mPalleteBodyTextColor)
        mPopularityTextView.setTextColor(mPalleteBodyTextColor)
        //Since total vote count is part of rating line, hence use same color
        mTotalVoteCountTextView.setTextColor(mPalleteTitleColor)
        mTaglineTextView.setTextColor(mPalleteBodyTextColor)
        mSynopsisTextView.setTextColor(mPalleteBodyTextColor)
        mProdCompanyTextView.setTextColor(mPalleteBodyTextColor)
        mProdCountryTextView.setTextColor(mPalleteBodyTextColor)
        mCollectionNameTextView.setTextColor(mPalleteBodyTextColor)

        //Set the color of empty movie trailer message if it's visible
        if(mMovieTrailerEmptyMsgTextView.getVisibility() == TextView.VISIBLE) {
            mMovieTrailerEmptyMsgTextView.setTextColor(mPalleteBodyTextColor)
        }
    }

    /**
     * This method applies the color to imagebutton background
     */
    protected void setImageButtonBackgroundColor() {
        //Set user list ImageButton color
        mImageButtonWatched.setBackgroundColor(mPalletePrimaryDarkColor)
        mImageButtonWishList.setBackgroundColor(mPalletePrimaryDarkColor)
        mImageButtonFavourite.setBackgroundColor(mPalletePrimaryDarkColor)
        mImageButtonCollection.setBackgroundColor(mPalletePrimaryDarkColor)

        //Set user's Tmdb list ImageButton color
        mTmdbImageButtonWatchlist.setBackgroundColor(mPalletePrimaryDarkColor)
        mTmdbImageButtonFavourite.setBackgroundColor(mPalletePrimaryDarkColor)
        mTmdbImageButtonRated.setBackgroundColor(mPalletePrimaryDarkColor)
    }

    /**
     * This method is called to set the title and apply the appropriate color once that is determined from poster image
     */
    protected void initializeTitleAndColor() {
        LogDisplay.callLog(LOG_TAG, 'initializeTitleAndColor is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        mCollapsingToolbar.setStatusBarScrimColor(mPalletePrimaryDarkColor)
        mCollapsingToolbar.setContentScrimColor(mPalletePrimaryColor)
        mCollapsingToolbar.setBackgroundColor(mPalletePrimaryColor)
        mCollapsingToolbar.setCollapsedTitleTextColor(mPalleteTitleColor)
    }

    /**
     * This method is called to set the color to Rating star
     */
    protected void setRatingStarColor() {
        //Set Ratingbar color.
        final LayerDrawable tmdbRatingBarlayerDrawable = (LayerDrawable) mTmdbRatingBar.getProgressDrawable() as LayerDrawable
        applyRatingStarColor(tmdbRatingBarlayerDrawable.getDrawable(2), mPalleteAccentColor) // Filled stars
        applyRatingStarColor(tmdbRatingBarlayerDrawable.getDrawable(1), ContextCompat.getColor(getContext(), R.color.grey_600_color)) // Half filled stars
        applyRatingStarColor(tmdbRatingBarlayerDrawable.getDrawable(0), ContextCompat.getColor(getContext(), R.color.grey_600_color)) // Empty stars

        final LayerDrawable userRatingBarlayerDrawable = mUserRatingBar.getProgressDrawable() as LayerDrawable
        applyRatingStarColor(userRatingBarlayerDrawable.getDrawable(2), mPalleteAccentColor) // Filled stars
        applyRatingStarColor(userRatingBarlayerDrawable.getDrawable(1), ContextCompat.getColor(getContext(), R.color.grey_600_color)) // Half filled stars
        applyRatingStarColor(userRatingBarlayerDrawable.getDrawable(0), ContextCompat.getColor(getContext(), R.color.grey_600_color)) // Empty stars
    }

    /**
     * This method applies the color to rating star based on the build version
     * @param drawable Rating drawable
     * @param color The color to apply
     */
    private static void applyRatingStarColor(final Drawable drawable, @ColorInt final int color)
    {
        if (Build.VERSION.SDK_INT >= 23) { // Version 23 -> Marshmallow
            // Do nothing as it automatically uses the accent color
        } else {
            // This works 99% times in pre lollipop but there are cases where it's still bit buggy
            // but does the job most of the times, so keeping it
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

    /**
     * This method is called to apply the accent color to the image button which is active (i.e. on)
     */
    protected void setActiveImageButtonColor() {
        LogDisplay.callLog(LOG_TAG, 'setActiveImageButtonColor is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mUserListWatchedFlag -> $mUserListWatchedFlag", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mUserListWishListdFlag -> $mUserListWishListdFlag", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mUserListFavouriteFlag -> $mUserListFavouriteFlag", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mUserListCollectionFlag -> $mUserListCollectionFlag", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mUserTmdbListWatchlistFlag -> $mUserTmdbListWatchlistFlag", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mUserTmdbListFavouriteFlag -> $mUserTmdbListFavouriteFlag", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "mUserTmdbListRatedFlag -> $mUserTmdbListRatedFlag", LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        if (mUserListWatchedFlag) {
            mImageButtonWatched.setColorFilter(mPalleteAccentColor)
        }

        if (mUserListWishListdFlag) {
            mImageButtonWishList.setColorFilter(mPalleteAccentColor)
        }

        if (mUserListFavouriteFlag) {
            mImageButtonFavourite.setColorFilter(mPalleteAccentColor)
        }

        if (mUserListCollectionFlag) {
            mImageButtonCollection.setColorFilter(mPalleteAccentColor)
        }

        if (mUserTmdbListWatchlistFlag) {
            mTmdbImageButtonWatchlist.setColorFilter(mPalleteAccentColor)
        }

        if (mUserTmdbListFavouriteFlag) {
            mTmdbImageButtonFavourite.setColorFilter(mPalleteAccentColor)
        }

        if (mUserTmdbListRatedFlag) {
            mTmdbImageButtonRated.setColorFilter(mPalleteAccentColor)
        }
    }

    /**
     * This method is called to set the dots for backdrop image ViewPager
     * @param dotsCount Total number of dots to be created
     * @param dotsImage Array of Image Buttons
     */
    private void setBackDropViewPagerDots(final int dotsCount, final AppCompatImageButton[] dotsImage) {
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(18,18)
        final ColorStateList whiteColorStateList = ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.white_color))
        layoutParams.setMargins(1,0,1,0)
        for(final i in 0..(dotsCount - 1)) {
            dotsImage[i] = new AppCompatImageButton(getActivity())
            dotsImage[i].setBackgroundResource(R.drawable.view_pager_dot)
            dotsImage[i].setLayoutParams(layoutParams)
            ViewCompat.setBackgroundTintList(dotsImage[i], whiteColorStateList)
            mBackdropDotHolderLayout.addView(dotsImage[i])
        }
        // Infrequently mBackdropViewPagerPos is going out of bound but very hard to replicate, so to avoid the failure
        // following dirty fix is done
        if(mBackdropViewPagerPos >= dotsImage.size()) {
            mBackdropViewPagerPos = dotsImage.size() - 1
        }
        // Set the first one's / selected one's color & size
        dotsImage[mBackdropViewPagerPos].setLayoutParams(new LinearLayout.LayoutParams(30,30))
        final ColorStateList accentColorStateList = ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.accent))
        ViewCompat.setBackgroundTintList(dotsImage[mBackdropViewPagerPos], accentColorStateList)
    }

    /**
     * Intent to open a web browser when user clicks on movie home page button
     */
    void startHomePageIntent() {
        if(mMovieHomePageUrl) {
            final Intent intent = new Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(mMovieHomePageUrl))
            startActivity(intent)
        }
    }

    /**
     * Intent to open the movie in IMDb app(if installed) or in web browser when user clicks on Imdb button
     */
    void startImdbIntent() {
        if(mMovieImdbId) {
            final String imdbUrl = "$GlobalStaticVariables.IMDB_BASE_MOVIE_TITLE_URL$mMovieImdbId/"
            final Intent intent = new Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(imdbUrl))
            startActivity(intent)
        }
    }

    /**
     * Intent to start the person activity
     */
    void launchPersonActivity(final int personId) {
        // Start Person activity
        final Bundle bundle = new Bundle()
        bundle.putInt(GlobalStaticVariables.MOVIE_PERSON_ID,personId)
        bundle.putString(GlobalStaticVariables.MOVIE_PERSON_BACKDROP_PATH,mOriginalBackdropPath)
        final Intent intent = new Intent(getActivity(), PersonMovieActivity.class)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        getActivity().overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    /**
     * Share the detail movie
     */
    protected void shareMovie() {
        LogDisplay.callLog(LOG_TAG, 'shareMovie is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        final String tmdbWebMovieUrl = "$GlobalStaticVariables.TMDB_WEB_MOVIE_BASE_URL${Integer.toString(mMovieId)}"
        final Intent sendIntent = new Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, "$mMovieTitle, TMDb link - $tmdbWebMovieUrl #${getString(R.string.app_name)} app")
        sendIntent.setType("text/plain")
        // Create intent to show the chooser dialog
        final Intent chooser = Intent.createChooser(sendIntent, getString(R.string.detail_movie_chooser_title))
        // Verify that the intent will resolve to an activity
        if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(chooser)
        }
    }

    /**
     * This method is used to create a alert dialog when user changes the rating of a movie which is present in user's
     * TMDb rated movie list
     */
    void createDialogForTmdbRatingConfirmation(final float userRatingVal) {
        LogDisplay.callLog(LOG_TAG, 'createDialogForTmdbRatingConfirmation is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogTheme)
        builder.setTitle(R.string.tmdb_rating_dialog_title)
                .setMessage(R.string.tmdb_rating_dialog_message)

        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, 'Dialog Ok is clicked, go and update the TMDb rating', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
                if(Utility.isReadyToDownload(getActivity().getApplicationContext())) {
                    mImageButtonClickForcedOnLoadFinished = true
                    new UploadTmdbRequest(getActivity(), GlobalStaticVariables.MOVIE_CATEGORY_TMDB_USER_RATED, userRatingVal,
                            false, mMovieCategory, mUserTmdbListDrawableLayout, mPalleteAccentColor).execute([mMovieId] as Integer[])
                    if(userRatingVal == 0) { // Reset the flag if rating is zero
                        mUserTmdbListRatedFlag = false
                    }
                } else {
                    Snackbar.make(mNestedScrollView, getString(R.string.no_internet_cannot_perform_operation_message), Snackbar.LENGTH_LONG).show()
                }
            }
        })

        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
            @Override
            void onClick(final DialogInterface dialog, final int which) {
                LogDisplay.callLog(LOG_TAG, 'Dialog cancel is clicked. No action needed.', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
            }
        })

        // Create the AlertDialog
        final AlertDialog dialog = builder.create()
        dialog.show()
    }

    //Overriding the animation for better performance
    //Reference - http://daniel-codes.blogspot.co.uk/2013/09/smoothing-performance-on-fragment.html
    @Override
    Animation onCreateAnimation(final int transit, final boolean enter, final int nextAnim) {
        LogDisplay.callLog(LOG_TAG, 'onCreateAnimation is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        Animation animation = super.onCreateAnimation(transit, enter, nextAnim)
        //Hardware layer supported above 11
        if (Build.VERSION.SDK_INT >= 11) {
            if (animation == null && nextAnim != 0) {
                animation = AnimationUtils.loadAnimation(getActivity(), nextAnim)
            }
            if (animation != null) {
                getView().setLayerType(View.LAYER_TYPE_HARDWARE, null)
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    void onAnimationStart(final Animation anim) {
                        getView().setLayerType(View.LAYER_TYPE_NONE, null)
                    }
                    @Override
                    void onAnimationEnd(final Animation anim) {
                    }
                    @Override
                    void onAnimationRepeat(final Animation anim) {
                    }
                })
            }
        }
        return animation
    }


    @Override
    void onResume() {
        LogDisplay.callLog(LOG_TAG, 'onResume is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onResume()
    }

    @Override
    void onPause() {
        super.onPause()
        LogDisplay.callLog(LOG_TAG, 'onPause is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
    }


    @Override
    void onSaveInstanceState(final Bundle outState) {
        LogDisplay.callLog(LOG_TAG, 'onSaveInstanceState is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        outState.putInt(GlobalStaticVariables.DETAIL_BACKDROP_VIEWPAGER_POSITION,mBackdropViewPagerPos)
        // Now call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    @Override
    void onStop() {
        LogDisplay.callLog(LOG_TAG, 'onStop is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        // Cancel picasso callback
        Picasso.with(getActivity()).cancelRequest(mPosterImageView)
        super.onStop()
    }

    @Override
    void onDestroyView() {
        LogDisplay.callLog(LOG_TAG, 'onDestroyView is called.->Release resources', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        // Release resources
        releaseResources()
        // Call the super onDestroyView
        super.onDestroyView()
    }

    @Override
    void onDestroy() {
        LogDisplay.callLog(LOG_TAG, 'onDestroy is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onDestroy()
    }

    @Override
    void onDetach() {
        LogDisplay.callLog(LOG_TAG,'onDetach is called',LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
//        // Detach the interface references
        mCallbackForBackdropImageClick = null
        mCallbackForSimilarMovieClick = null
        super.onDetach()
    }

    @Override
    void onConfigurationChanged(final Configuration newConfig) {
        LogDisplay.callLog(LOG_TAG,'onConfigurationChanged is called',LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        super.onConfigurationChanged(newConfig)
    }

    protected void releaseResources() {
        LogDisplay.callLog(LOG_TAG, 'releaseResources is called', LogDisplay.DETAIL_MOVIE_FRAGMENT_LOG_FLAG)
        // Remove listeners
        mBackdropViewPager.removeOnPageChangeListener(mOnPageChangeListener)
        mAppBarLayout.removeOnOffsetChangedListener(mAppbarOnOffsetChangeListener)
        // Remove adapter from backdrop view pager
        mBackdropViewPager.setAdapter(null)
        // Set the pager adapter to null
        mDetailFragmentPagerAdapter = null
        mHorizontalSimilarMovieGridView.setAdapter(null)
        mHorizontalMovieCastGridView.setAdapter(null)
        mHorizontalMovieCrewGridView.setAdapter(null)
        mMovieReviewRecyclerView.setAdapter(null)
        // Set the adapters to null
        mSimilarMovieAdapter = null
        mMovieCastAdapter = null
        mMovieCrewAdapter = null
        mMovieReviewAdapter = null

        mSimilarMovieAdapterOnClickHandler = null
        mDetailFragmentPagerAdapterOnClickHandler = null
        mSimilarMovieGridLayoutManager = null
        mCastGridLayoutManager = null
        mCrewGridLayoutManager = null
        mReviewLinearLayoutManager = null
        mHorizontalSimilarMovieGridView.setLayoutManager(null)
        mHorizontalMovieCastGridView.setLayoutManager(null)
        mHorizontalMovieCrewGridView.setLayoutManager(null)
        mMovieReviewRecyclerView.setLayoutManager(null)
        mHorizontalSimilarMovieGridView = null
        mHorizontalMovieCastGridView = null
        mHorizontalMovieCrewGridView = null
        mMovieReviewRecyclerView = null
        mRootView = null
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of person crew movie
     * item click.
     */
    public interface CallbackForBackdropImageClick {
        /**
         * BackdropImageClickCallback when backdrop image switcher item is clicked
         */
        public void onBackdropImageClicked(String title, int position, ArrayList<String> backdropImageFilePath)
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of home movie
     * item click.
     */
    public interface CallbackForSimilarMovieClick {
        /**
         * HomeMovieFragmentCallback when a movie item has been clicked on home page
         */
        public void onSimilarMovieItemSelected(int movieId)
    }
}