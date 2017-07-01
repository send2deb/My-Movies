/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import groovy.json.JsonException
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

@CompileStatic
class LoadMovieDetails extends AsyncTask<ArrayList<Integer>, Void, Void> {
    private static final String LOG_TAG = LoadMovieDetails.class.getSimpleName()
    private final ContentResolver mContentResolver
    private final Context mContext

    public LoadMovieDetails(final Context ctx) {
        mContext = ctx
        mContentResolver = mContext.getContentResolver()
    }

    @SuppressWarnings("GroovyListGetCanBeKeyedAccess")
    @Override
    protected Void doInBackground(ArrayList<Integer>... params) {
        final ArrayList<Integer> movieIdList = new ArrayList<Integer>()
        final ArrayList<Integer> movieBasicRowIdList = new ArrayList<Integer>()
        final ArrayList<Integer> isForHomeList = new ArrayList<Integer>(1)
        final ArrayList<Integer> categoryFlag = new ArrayList<Integer>(1)
        movieIdList = params[0]
        movieBasicRowIdList = params[1]
        isForHomeList = params[2]
        categoryFlag = params[3]
        LogDisplay.callLog(LOG_TAG, "Movie ID list param-> $movieIdList", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "Movie row id list param-> $movieBasicRowIdList", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "isForHomeList param-> ${isForHomeList.get(0)}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "categoryFlag param-> ${categoryFlag.get(0)}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)

        //We expect same number of arguments in both the ArrayList. If that is not the case then return null
        if (movieIdList.size() > 0 && (movieIdList.size() != movieBasicRowIdList.size())) {
            LogDisplay.callLog(LOG_TAG, "Arguments counts not same or zero. Movie id count-> ${movieIdList.size()} . Movie row id count-> ${movieBasicRowIdList.size()}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            return null
        }
        for (i in 0..(movieIdList.size() - 1)) {
            LogDisplay.callLog(LOG_TAG, "i -> $i & item count is -> ${movieIdList.size()}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
            //TMDb api example (movie with appended response)
            //https://api.themoviedb.org/3/movie/240?api_key=key&append_to_response=similar,credits,images,videos,release_dates,reviews,recommendations

            try {
                final Uri.Builder uriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

                final Uri uri = uriBuilder.appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                        .appendPath(Integer.toString(movieIdList.get(i)))
                        .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY, BuildConfig.TMDB_API_KEY)
                        .appendQueryParameter(GlobalStaticVariables.TMDB_APPEND_TO_RESPONSE_KEY, GlobalStaticVariables.TMDB_MOVIE_APPEND_TO_RESPONSE_PARAM)
                        .build()

                final URL url = new URL(uri.toString())
                LogDisplay.callLog(LOG_TAG, "Movie id append response url-> ${uri.toString()}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                def jsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(url)

                /**
                 * Load (update / insert) the additional movie details
                 * **/
                LogDisplay.callLog(LOG_TAG, "JSON DATA for movie id ${movieIdList.get(i)} & row id ${movieBasicRowIdList.get(i)} -> $jsonData", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                final ContentValues contentMovieBasicInfoValues = JsonParse.parseAdditionalBasicMovieData(jsonData)
                //Update the indicator to indicate data is loaded
                if (contentMovieBasicInfoValues) {
                    if (movieIdList.get(i) && movieBasicRowIdList.get(i) == 0) { // rowid = 0 means, record does not exist, so insert
                        if(categoryFlag.get(i) == GlobalStaticVariables.PERSON_CATEGORY_FLAG) {
                            contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY, GlobalStaticVariables.MOVIE_CATEGORY_PERSON)
                            contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE, GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_PERSON)
                        } else if(categoryFlag.get(i) == GlobalStaticVariables.SEARCH_CATEGORY_FLAG) {
                            contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_CATEGORY, GlobalStaticVariables.MOVIE_CATEGORY_SEARCH)
                            contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE, GlobalStaticVariables.MOVIE_LIST_TYPE_SEARCH)
                        } else {
                            LogDisplay.callLog(LOG_TAG, "Shouldn't reach here. Please investigate categoryFlag-> ${categoryFlag.get(i)}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                            return null
                        }
                        // Page number not used for person movie or search, so use 0
                        contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_PAGE_NUMBER, 0)
                        //Populate timestamp for new record
                        contentMovieBasicInfoValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_CREATE_TIMESTAMP, Utility.getTodayDate())
                        final Uri movieBasicInfoUri = mContentResolver.insert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, contentMovieBasicInfoValues)
                        if (ContentUris.parseId(movieBasicInfoUri) == -1) {
                            LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info failed. Uri->$movieBasicInfoUri", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        } else {
                            LogDisplay.callLog(LOG_TAG, "Insert in movie_basic_info successful. Uri->$movieBasicInfoUri", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                            //Populate the movieBasicRowIdList - used while inserting records to other dependant tables
                            movieBasicRowIdList.set(i, ContentUris.parseId(movieBasicInfoUri) as Integer)
                            LogDisplay.callLog(LOG_TAG, "movieBasicRowIdList->$movieBasicRowIdList", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        }
                    } else {
                        final int movieBasicInfoUpdateCount = mContentResolver.update(
                                MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                                contentMovieBasicInfoValues,
                                "$MovieMagicContract.MovieBasicInfo._ID = ?",
                                [Integer.toString(movieBasicRowIdList.get(i))] as String[])
                        if (movieBasicInfoUpdateCount != 1) {
                            LogDisplay.callLog(LOG_TAG, "Additional details update in movie_basic_info failed. Update Count->$movieBasicInfoUpdateCount. Movie row id->${movieBasicRowIdList.get(i)}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        } else { //If the return value is 1, indicate successful update
                            LogDisplay.callLog(LOG_TAG, "Additional details update in movie_basic_info successful. Update Count->$movieBasicInfoUpdateCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        }
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'JsonParse.parseAdditionalBasicMovieData returned null', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }

                boolean allOperationSuccessfulFlag = true
                /**
                 * Process and load (insert) the similar movies
                 * **/
                final ContentValues[] similarMovieContentValues = JsonParse.parseSimilarMovieListJson(mContext, jsonData, movieIdList.get(i)) as ContentValues[]
                if (similarMovieContentValues && allOperationSuccessfulFlag) {
                    final int similarMovieCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, similarMovieContentValues)
                    if (similarMovieCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert similar movie in movie_basic_info successful. Insert count->$similarMovieCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert similar movie in movie_basic_info failed. Insert count->$similarMovieCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        allOperationSuccessfulFlag = false
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'JsonParse.parseSimilarMovieListJson returned null', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }

                /**
                 * Process and load (insert) the movie cast data
                 * **/
                final ContentValues[] movieCastContentValues = JsonParse.parseMovieCastJson(jsonData, movieIdList.get(i), movieBasicRowIdList.get(i)) as ContentValues[]
                if (movieCastContentValues && allOperationSuccessfulFlag) {
                    final int movieCastCount = mContentResolver.bulkInsert(MovieMagicContract.MovieCast.CONTENT_URI, movieCastContentValues)
                    if (movieCastCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_cast successful. Insert count->$movieCastCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_cast failed. Insert count->$movieCastCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        allOperationSuccessfulFlag = false
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'JsonParse.parseMovieCastJson returned null', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }

                /**
                 * Process and load (insert) the movie crew data
                 * **/
                final ContentValues[] movieCrewContentValues = JsonParse.parseMovieCrewJson(jsonData, movieIdList.get(i), movieBasicRowIdList.get(i)) as ContentValues[]
                if (movieCrewContentValues && allOperationSuccessfulFlag) {
                    final int movieCrewCount = mContentResolver.bulkInsert(MovieMagicContract.MovieCrew.CONTENT_URI, movieCrewContentValues)
                    if (movieCrewCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_crew successful. Insert count->$movieCrewCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_crew failed. Insert count->$movieCrewCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        allOperationSuccessfulFlag = false
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'JsonParse.parseMovieCrewJson returned null', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }

                /**
                 * Process and load (insert) the movie image data
                 * **/
                final ContentValues[] movieImageContentValues = JsonParse.parseMovieImageJson(jsonData, movieIdList.get(i), movieBasicRowIdList.get(i)) as ContentValues[]
                if (movieImageContentValues && allOperationSuccessfulFlag) {
                    final int movieImageCount = mContentResolver.bulkInsert(MovieMagicContract.MovieImage.CONTENT_URI, movieImageContentValues)
                    if (movieImageCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_image successful. Insert count->$movieImageCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_image failed. Insert count->$movieImageCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        allOperationSuccessfulFlag = false
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'JsonParse.parseMovieImageJson returned null', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }

                /**
                 * Process and load (insert) the movie video data
                 * **/
                final ContentValues[] movieVideoContentValues = JsonParse.parseMovieVideoJson(jsonData, movieIdList.get(i), movieBasicRowIdList.get(i), isForHomeList.get(0)) as ContentValues[]
                if (movieVideoContentValues && allOperationSuccessfulFlag) {
                    final int movieVideoCount = mContentResolver.bulkInsert(MovieMagicContract.MovieVideo.CONTENT_URI, movieVideoContentValues)
                    if (movieVideoCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_video successful. Insert count->$movieVideoCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_video failed. Insert count->$movieVideoCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        allOperationSuccessfulFlag = false
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'JsonParse.parseMovieVideoJson returned null', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }

                /**
                 * Process and load (insert) the movie release date data
                 * **/
                final ContentValues[] movieReleaseContentValues = JsonParse.parseMovieReleaseDateJson(jsonData, movieIdList.get(i), movieBasicRowIdList.get(i)) as ContentValues[]
                if (movieReleaseContentValues && allOperationSuccessfulFlag) {
                    final int movieReleaseCount = mContentResolver.bulkInsert(MovieMagicContract.MovieReleaseDate.CONTENT_URI, movieReleaseContentValues)
                    if (movieReleaseCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_release_date successful. Insert count->$movieReleaseCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_release_date failed. Insert count->$movieReleaseCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        allOperationSuccessfulFlag = false
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'JsonParse.parseMovieReleaseDateJson returned null', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }

                /**
                 * Process and load (insert) the movie review data
                 * **/
                final ContentValues[] movieReviewContentValues = JsonParse.parseMovieReviewJson(jsonData, movieIdList.get(i), movieBasicRowIdList.get(i)) as ContentValues[]
                if (movieReviewContentValues && allOperationSuccessfulFlag) {
                    final int movieReviewCount = mContentResolver.bulkInsert(MovieMagicContract.MovieReview.CONTENT_URI, movieReviewContentValues)
                    if (movieReviewCount > 0) {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_review successful. Insert count->$movieReviewCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else {
                        LogDisplay.callLog(LOG_TAG, "Insert in movie_review failed. Insert count->$movieReviewCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                        allOperationSuccessfulFlag = false
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, 'JsonParse.parseMovieReviewJson returned null', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }

                /**
                 * Process and load (insert) the recommendations movies only when user check the details of the movie
                 * (i.e. not load anything while loading data for home page. It also ensures we do not download recommendations of recommended movie!)
                 * **/
                if(isForHomeList.get(0) == GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE) {
                    final ContentValues[] recommendationsMovieContentValues = JsonParse.parseRecommendationsMovieListJson(mContext,jsonData, movieIdList.get(i)) as ContentValues[]
                    if (recommendationsMovieContentValues && allOperationSuccessfulFlag) {
                        final int recommendationsMovieCount = mContentResolver.bulkInsert(MovieMagicContract.MovieBasicInfo.CONTENT_URI, recommendationsMovieContentValues)
                        if (recommendationsMovieCount > 0) {
                            LogDisplay.callLog(LOG_TAG, "Insert recommendations movie in movie_basic_info successful. Insert count->$recommendationsMovieCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                            // Now go and load the details of recommendations movies
                            loadRecommendationMovieDetails(recommendationsMovieContentValues)
                        } else {
                            LogDisplay.callLog(LOG_TAG, "Insert recommendations movie in movie_basic_info failed. Insert count->$recommendationsMovieCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                            allOperationSuccessfulFlag = false
                        }
                    } else {
                        LogDisplay.callLog(LOG_TAG, 'JsonParse.parseRecommendationsMovieListJson returned null', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    }
                } else {
                    LogDisplay.callLog(LOG_TAG, "isForHomeList is true (flag value ->${isForHomeList.get(0)}), so skipped recommendations movie processing.", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }

                //Reset the data present flag of movie_basic_info if any problem faced during operation
                if (!allOperationSuccessfulFlag) {
                    final ContentValues movieBasicInfoUpdateData = new ContentValues()
                    movieBasicInfoUpdateData.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG, GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE)
                    final int movieBasicInfoFlagResetCount = mContentResolver.update(
                            MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                            movieBasicInfoUpdateData,
                            "$MovieMagicContract.MovieBasicInfo._ID = ?",
                            [Integer.toString(movieBasicRowIdList.get(i))] as String[])
                    if (movieBasicInfoFlagResetCount != 1) {
                        LogDisplay.callLog(LOG_TAG, "Update to reset COLUMN_DETAIL_DATA_PRESENT_FLAG in movie_basic_info failed. Update Count->$movieBasicInfoFlagResetCount. Movei row id ${movieBasicRowIdList.get(i)}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    } else { //If the return value to 1, indicate successful insert
                        LogDisplay.callLog(LOG_TAG, "Update to reset COLUMN_DETAIL_DATA_PRESENT_FLAG in movie_basic_info successful. Update Count->$movieBasicInfoFlagResetCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                    }
                }

            } catch (URISyntaxException e) {
                Log.e(LOG_TAG, "URISyntaxException: $e.message", e)
            } catch (JsonException e) {
                Log.e(LOG_TAG, "JsonException: $e.message", e)
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException: $e.message")
            } catch (android.database.sqlite.SQLiteConstraintException e) {
                Log.e(LOG_TAG, "SQLiteConstraintException: $e.message")
            } catch (android.database.sqlite.SQLiteException e) {
                Log.e(LOG_TAG, "SQLiteException: $e.message")
            }
        }
        return null
    }

    /**
     * This method loads the details of recommendation movies. Since recommendation movies are only used for home page display purpose,
     * so the details needs to be updated while loading the movies to the table
     * @param contentValues
     */
    private void loadRecommendationMovieDetails(final ContentValues[] contentValues) {
        LogDisplay.callLog(LOG_TAG, 'loadRecommendationMovieDetails is called', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
        // TMDb api example
        // https://api.themoviedb.org/3/movie/240?api_key=key
        for(final i in 0..(contentValues.length - 1)) {
            final int recommMovieId = contentValues[i].getAsInteger(MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID)
            try {
                final Uri.Builder recommUriBuilder = Uri.parse(GlobalStaticVariables.TMDB_MOVIE_BASE_URL).buildUpon()

                final Uri recommUri = recommUriBuilder.appendPath(GlobalStaticVariables.TMDB_MOVIE_PATH)
                        .appendPath(Integer.toString(recommMovieId))
                        .appendQueryParameter(GlobalStaticVariables.TMDB_MOVIE_API_KEY, BuildConfig.TMDB_API_KEY)
                        .build()

                final URL recommUrl = new URL(recommUri.toString())
                LogDisplay.callLog(LOG_TAG, "Recommendation movie id url-> ${recommUri.toString()}", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                def recommJsonData = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY).parse(recommUrl)
                LogDisplay.callLog(LOG_TAG, "JSON DATA for recommendation movie id $recommMovieId -> $recommJsonData", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                final ContentValues recommContentValues = JsonParse.parseAdditionalBasicMovieData(recommJsonData)
                //Reset the detail data present flag because we are not loading other details like similar, cast, crew, etc
                //So when user will click the movie on home screen for detail view it will load all data
                recommContentValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,GlobalStaticVariables.MOVIE_MAGIC_FLAG_FALSE)
                final int recommUpdateCount = mContentResolver.update(
                        MovieMagicContract.MovieBasicInfo.CONTENT_URI,
                        recommContentValues,
                        """$MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_ID = ? and
                        $MovieMagicContract.MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE = ?""",
                        [Integer.toString(recommMovieId), GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_RECOMMENDATIONS] as String[])
                if (recommUpdateCount != 1) {
                    LogDisplay.callLog(LOG_TAG, "Recommendation movie additional details Update in movie_basic_info failed. Update Count->$recommUpdateCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                } else { //If the return value is 1, indicate successful update
                    LogDisplay.callLog(LOG_TAG, "Recommendation movie additional details Update in movie_basic_info successful. Update Count->$recommUpdateCount", LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
                }
            } catch (URISyntaxException e) {
                Log.e(LOG_TAG, "URISyntaxException: $e.message", e)
            } catch (JsonException e) {
                Log.e(LOG_TAG, "JsonException: $e.message", e)
            } catch (IOException e) {
                Log.e(LOG_TAG, "IOException: $e.message")
            } catch (android.database.sqlite.SQLiteConstraintException e) {
                Log.e(LOG_TAG, "SQLiteConstraintException: $e.message")
            } catch (android.database.sqlite.SQLiteException e) {
                Log.e(LOG_TAG, "SQLiteException: $e.message")
            }
        }
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
        super.onPostExecute(aVoid)
        LogDisplay.callLog(LOG_TAG, 'Additional movie data load finished', LogDisplay.LOAD_MOVIE_DETAILS_LOG_FLAG)
    }
}