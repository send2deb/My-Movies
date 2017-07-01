/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.app.ProgressDialog
import android.app.SearchManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import com.moviemagic.dpaul.android.app.adapter.MovieGridRecyclerAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.JsonParse
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.SearchDatabaseTable
import com.moviemagic.dpaul.android.app.backgroundmodules.Utility
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper;
import groovy.transform.CompileStatic

@CompileStatic
class SearchActivity extends AppCompatActivity {
    private static final String LOG_TAG = SearchActivity.class.getSimpleName()

    private SearchDatabaseTable mSearchDatabaseTable = new SearchDatabaseTable(this)
    private RecyclerView mRecyclerView
    private MovieGridRecyclerAdapter mGridRecyclerAdapter

    //Projection for search_movie_basic_info table (items are same as GridFragment except COLUMN_MOVIE_LIST_TYPE)
    //This is important as MovieGridRecyclerAdapter is driven by GridFragment projection column and the same
    //adapter is used for search activity result
    private static final String[] SEARCH_MOVIE_COLUMNS = ["docid",
                                                   SearchDatabaseTable.SEARCH_FTS_COLUMN_TITLE,
                                                   SearchDatabaseTable.SEARCH_FTS_COLUMN_POSTER_PATH,
                                                   SearchDatabaseTable.SEARCH_FTS_COLUMN_PAGE_NUMBER,
                                                   SearchDatabaseTable.SEARCH_FTS_COLUMN_MOVIE_ID]

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        final Toolbar toolbar = findViewById(R.id.search_activity_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        getSupportActionBar().setTitle(getString(R.string.title_activity_search))

        mRecyclerView = findViewById(R.id.auto_grid_recycler_view) as RecyclerView
        //Set this to false for smooth scrolling of recyclerview
        mRecyclerView.setNestedScrollingEnabled(false)
        //Create a new interface member variable for MovieGridRecyclerAdapterOnClickHandler and the same is passed as
        //parameter to Adapter, this onClick method is called whenever onClick is called from MovieGridRecyclerAdapter
        mGridRecyclerAdapter = new MovieGridRecyclerAdapter(this,
                new MovieGridRecyclerAdapter.MovieGridRecyclerAdapterOnClickHandler(){
                    @Override
                    void onClick(final int movieId) {
                        handleMovieClick(movieId)
                    }
                })
        mRecyclerView.setAdapter(mGridRecyclerAdapter)
        // Get the intent, verify the action and get the query
        final Intent intent = getIntent()
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String rawQuery = intent.getStringExtra(SearchManager.QUERY)
            final String query = rawQuery.trim()
            LogDisplay.callLog(LOG_TAG,"Query string -> $query",LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
            // Check if the user is online or not, if not then show a message
            final boolean isOnline = Utility.isOnline(getApplicationContext())
            if(isOnline) {
                new SearchMoviesOnline(this).execute([query] as String[])
            } else {
                LogDisplay.callLog(LOG_TAG,'No internet connection, cannot perform search',LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) { // Press appbar back button to go to previous activity
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    void onStart() {
        super.onStart()
        // Check if the user is online or not, if not then show a message
        final boolean isOnline = Utility.isOnline(getApplicationContext())
        if(!isOnline) {
            Snackbar.make(mRecyclerView, getString(R.string.no_internet_connection_message), Snackbar.LENGTH_LONG).show()
        } else if(Utility.isOnlyWifi(getApplicationContext()) & !GlobalStaticVariables.WIFI_CONNECTED) {
            // If user has selected only WiFi but user is online without WiFi then show a dialog
            Snackbar.make(mRecyclerView, getString(R.string.internet_connection_without_wifi_message), Snackbar.LENGTH_LONG).show()
        } else if (Utility.isReducedDataOn(this)) {
            // If user has selected reduced data
            Snackbar.make(mRecyclerView, getString(R.string.reduced_data_use_on_message), Snackbar.LENGTH_LONG).show()
        }
    }

    public void handleMovieClick(final int movieId) {
        final Intent intent = new Intent(this, DetailMovieActivity.class)
        final Bundle bundle = new Bundle()
        bundle.putInt(GlobalStaticVariables.MOVIE_BASIC_INFO_MOVIE_ID,movieId)
        bundle.putString(GlobalStaticVariables.MOVIE_BASIC_INFO_CATEGORY,GlobalStaticVariables.MOVIE_CATEGORY_SEARCH)
        intent.putExtras(bundle)
        startActivity(intent)
        //Start the animation
        overridePendingTransition(R.anim.slide_bottom_in_animation,0)
    }

    protected class SearchMoviesOnline extends AsyncTask<String, Void, String> {
        private final Context mContext
        private int mTotalPage = 1
        private final int MAX_PAGE_DOWNLOAD = 10
        private boolean searchSuccess = false
        private boolean firstTotalPageRead = true
        private final ProgressDialog mProgressDialog
        private String queryString

        public SearchMoviesOnline(final Context ctx) {
            LogDisplay.callLog(LOG_TAG, 'SearchMoviesOnline constructor is called', LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
            mContext = ctx
            mProgressDialog = new ProgressDialog(mContext, ProgressDialog.STYLE_SPINNER)
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute()
            mProgressDialog.setMessage(mContext.getString(R.string.progress_dialog_wait_title))
            mProgressDialog.show()
        }

        @Override
        protected String doInBackground(final String... params) {
            queryString = params[0]
            List<ContentValues> contentValues = []

            for (final i in 1..MAX_PAGE_DOWNLOAD) {
                if(i <= mTotalPage) {
                    contentValues = downloadSearchMovies(queryString, i)
                    if (contentValues && contentValues.size() > 0) {
                        final recCount = mSearchDatabaseTable.bulkInsert(contentValues as ContentValues[])
                        LogDisplay.callLog(LOG_TAG, "Total record inserted -> $recCount", LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
                        contentValues = []
                        searchSuccess = true
                    } else {
                        LogDisplay.callLog(LOG_TAG, "No movie data found for search string -> $queryString", LogDisplay.MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG)
                    }
                }
            }

            if(searchSuccess) {
                return queryString
            } else {
                return null
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            LogDisplay.callLog(LOG_TAG, 'onPostExecute is called', LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
            if(mProgressDialog) {
                mProgressDialog.dismiss()
            }
            int totalRecordCount = 0
            if(result) {
                final Cursor cursor = mSearchDatabaseTable.getSearchResult(result,SEARCH_MOVIE_COLUMNS)
                if(cursor && cursor.moveToFirst()) {
                    LogDisplay.callLog(LOG_TAG, "Total search result -> ${cursor.getCount()}",LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
                    mGridRecyclerAdapter.swapCursor(cursor)
                    totalRecordCount = cursor.getCount()
                } else {
                    LogDisplay.callLog(LOG_TAG, 'onPostExecute: empty search cursor', LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
                    mGridRecyclerAdapter.swapCursor(null)
                    totalRecordCount = 0
                    Snackbar.make(mRecyclerView, String.format(getString(R.string.search_no_movie_found_message),queryString), Snackbar.LENGTH_LONG).show()
                }
            } else {
                LogDisplay.callLog(LOG_TAG, 'onPostExecute: result is null', LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
                mGridRecyclerAdapter.swapCursor(null)
                totalRecordCount = 0
                Snackbar.make(mRecyclerView, String.format(getString(R.string.search_no_movie_found_message),queryString), Snackbar.LENGTH_LONG).show()
            }
            if(totalRecordCount > 1) {
                getSupportActionBar().setSubtitle(String.format(getString(R.string.search_activity_subtitle_record_count), totalRecordCount))
            } else {
                getSupportActionBar().setSubtitle(String.format(getString(R.string.search_activity_subtitle_record_count_single_or_zero), totalRecordCount))
            }
        }

        protected List<ContentValues> downloadSearchMovies(final String queryString, final int page) {
            //TMDB api example
            // https://api.themoviedb.org/3/search/movie?api_key=key&query=<query_string>

            final List<ContentValues> movieList

            try {
                if (page <= mTotalPage) {
                    final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()
                    final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_SEARCH_PATH)
                            .appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                            .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY,BuildConfig.TMDB_API_KEY)
                            .appendQueryParameter(GlobalStaticVariables.TMDB_PARAMETER_QUERY,queryString)
                            .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_PAGE,page.toString())
                            .build()

                    final URL url = new URL(uri.toString())
                    LogDisplay.callLog(LOG_TAG,"Movie url for search string $queryString -> ${uri.toString()}",LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
                    final def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)
                    LogDisplay.callLog(LOG_TAG, "JSON DATA for $queryString -> $jsonData", LogDisplay.SEARCH_ACTIVITY_LOG_FLAG)
                    movieList = JsonParse.parseSearchMovieListJson(jsonData, queryString)
                    if(firstTotalPageRead) {
                        mTotalPage = JsonParse.getTotalPages(jsonData)
                        firstTotalPageRead = false
                    }
                    return movieList
                }
            } catch (final URISyntaxException e) {
                Log.e(LOG_TAG, "URISyntaxException Error: ${e.message}", e)
            } catch (final JsonException e) {
                Log.e(LOG_TAG, " JsonException Error: ${e.message}", e)
            } catch (final IOException e) {
                Log.e(LOG_TAG, "IOException Error: ${e.message}", e)
            }
            return null
        }
    }
}