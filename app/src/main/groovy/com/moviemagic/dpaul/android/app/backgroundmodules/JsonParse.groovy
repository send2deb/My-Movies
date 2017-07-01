/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieBasicInfo
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieCast
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieCrew
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieImage
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieReleaseDate
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieReview
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieVideo
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MovieCollection
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MoviePersonInfo
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MoviePersonCast
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MoviePersonCrew
import com.moviemagic.dpaul.android.app.contentprovider.MovieMagicContract.MoviePersonImage

//Since the json field is used dynamically, so this class is not compiled as CompileStatic
//@CompileStatic
class JsonParse {
    private static final String LOG_TAG = JsonParse.class.getSimpleName()

    /**
     * Helper method to determine the total number of pages of particular movie category
     * @param jsonData JSON data to be parsed
     * @return total page count
     */
    static int getTotalPages(final def jsonData) {
        final int currentPage = jsonData.page
        final int totalPage = jsonData.total_pages
        LogDisplay.callLog(LOG_TAG, "CurrentPage -> $currentPage", LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "TotalPage -> $totalPage", LogDisplay.JSON_PARSE_LOG_FLAG)
        return totalPage
    }

    /**
     * Helper method to parse movie JSON data
     * @param jsonData JSON data to be parsed
     * @param category Movie category (i.e. popular, now playing, etc)
     * @param movieListType Indicate the type (i.e. public tmdb or user list or user Tmdb list)
     * @param dateTimeStamp The Date & Time stamp to use while inserting the records
     * @return formatted list of movies as content values
     */
    static List<ContentValues> parseMovieListJson(
            final Context ctx, final def jsonData, final String category, final String movieListType, final String dateTimeStamp) {
        final List<ContentValues> movieList = []
        final def cnt = jsonData.results.size() - 1
        //Ensure that the results is not Null
        if (jsonData.results) {
            for (final i in 0..cnt) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.results[i].title}", LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues movieValue = new ContentValues()
                //if-else is used for all json fields for null safe
                if (jsonData.results[i].id)
                    movieValue.put(MovieBasicInfo.COLUMN_MOVIE_ID, jsonData.results[i].id)
                else {
                    movieValue.put(MovieBasicInfo.COLUMN_MOVIE_ID, 0)
                    LogDisplay.callLog(LOG_TAG, 'Not a valid movie id', LogDisplay.JSON_PARSE_LOG_FLAG)
                }
                if (jsonData.results[i].adult)
                    movieValue.put(MovieBasicInfo.COLUMN_ADULT_FLAG, jsonData.results[i].adult)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_ADULT_FLAG, '')
                if (jsonData.results[i].backdrop_path)
                    movieValue.put(MovieBasicInfo.COLUMN_BACKDROP_PATH, jsonData.results[i].backdrop_path)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_BACKDROP_PATH, '')
                if (jsonData.results[i].original_title)
                    movieValue.put(MovieBasicInfo.COLUMN_ORIGINAL_TITLE, jsonData.results[i].original_title)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_ORIGINAL_TITLE, '')
                if (jsonData.results[i].overview)
                    movieValue.put(MovieBasicInfo.COLUMN_OVERVIEW, jsonData.results[i].overview)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_OVERVIEW, '')
                //date format -> yyyy-mm-dd
                if (jsonData.results[i].release_date)
                    movieValue.put(MovieBasicInfo.COLUMN_RELEASE_DATE, jsonData.results[i].release_date)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_RELEASE_DATE, '1900-01-01')
                if (jsonData.results[i].poster_path)
                    movieValue.put(MovieBasicInfo.COLUMN_POSTER_PATH, jsonData.results[i].poster_path)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_POSTER_PATH, '')
                if (jsonData.results[i].popularity)
                    movieValue.put(MovieBasicInfo.COLUMN_POPULARITY, jsonData.results[i].popularity)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_POPULARITY, 0.0)
                if (jsonData.results[i].title)
                    movieValue.put(MovieBasicInfo.COLUMN_TITLE, jsonData.results[i].title)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_TITLE, '')
                if (jsonData.results[i].video)
                    movieValue.put(MovieBasicInfo.COLUMN_VIDEO_FLAG, jsonData.results[i].video)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_VIDEO_FLAG, 'false')
                if (jsonData.results[i].vote_average)
                    movieValue.put(MovieBasicInfo.COLUMN_VOTE_AVG, jsonData.results[i].vote_average)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_VOTE_AVG, 0.0)
                if (jsonData.results[i].vote_count)
                    movieValue.put(MovieBasicInfo.COLUMN_VOTE_COUNT, jsonData.results[i].vote_count)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_VOTE_COUNT, 0)
                if (jsonData.page)
                    movieValue.put(MovieBasicInfo.COLUMN_PAGE_NUMBER, jsonData.page)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_PAGE_NUMBER, 0)
                // This one is for only Tmdb user Rated movies - for rest this filed does not exist
                if(jsonData.results[i].rating)
                    movieValue.put(MovieBasicInfo.COLUMN_TMDB_USER_RATED_RATING, jsonData.results[i].rating)
                else
                    movieValue.put(MovieBasicInfo.COLUMN_TMDB_USER_RATED_RATING, 0)

                //category and movieListType are supplied in the program, so always null safe
                movieValue.put(MovieBasicInfo.COLUMN_MOVIE_CATEGORY, category)
                movieValue.put(MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE, movieListType)
                //Set create date and update date - Same value used for all so that house keeping can work properly
                movieValue.put(MovieBasicInfo.COLUMN_CREATE_TIMESTAMP,dateTimeStamp)
                movieValue.put(MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP,dateTimeStamp)

                // Save the page number in SharedPreference
                if(jsonData.page && jsonData.page > 0) {
                    final SharedPreferences sharedPref = ctx.getSharedPreferences(
                            ctx.getString(R.string.app_pref_tmdb_movie_page_number_file), Context.MODE_PRIVATE)
                    final SharedPreferences.Editor editor = sharedPref.edit()
                    switch (category) {
                        case GlobalStaticVariables.MOVIE_CATEGORY_POPULAR:
                            editor.putInt(ctx.getString(R.string.app_pref_tmdb_popular_page_key), jsonData.page)
                            break
                        case GlobalStaticVariables.MOVIE_CATEGORY_TOP_RATED:
                            editor.putInt(ctx.getString(R.string.app_pref_tmdb_toprated_page_key), jsonData.page)
                            break
                        case GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING:
                            editor.putInt(ctx.getString(R.string.app_pref_tmdb_nowplaying_page_key), jsonData.page)
                            break
                        case GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING:
                            editor.putInt(ctx.getString(R.string.app_pref_tmdb_upcoming_page_key), jsonData.page)
                            break
                        default:
                            LogDisplay.callLog(LOG_TAG, "Not TMDb public category. Category -> $category", LogDisplay.JSON_PARSE_LOG_FLAG)
                    }
                    editor.commit()
                }

                // Add a filter to add data correctly for now_playing and upcoming lists
                switch (category) {
                    case GlobalStaticVariables.MOVIE_CATEGORY_NOW_PLAYING:
                        // Add the movie only if the release date matches - TMDb does send incorrect data
                        if(MovieMagicContract.convertMovieReleaseDate(movieValue.get(MovieBasicInfo.COLUMN_RELEASE_DATE))
                            <= MovieMagicContract.convertMovieReleaseDate(Utility.getSimpleTodayDate())) {
                            movieList << movieValue
                        }
                        break
                    case GlobalStaticVariables.MOVIE_CATEGORY_UPCOMING:
                        // Add the movie only if the release date matches - TMDb does send incorrect data
                        if(MovieMagicContract.convertMovieReleaseDate(movieValue.get(MovieBasicInfo.COLUMN_RELEASE_DATE))
                                > MovieMagicContract.convertMovieReleaseDate(Utility.getSimpleTodayDate())) {
                            movieList << movieValue
                        }
                        break
                    default:
                        //add to the list - this is for other category
                        movieList << movieValue
                }
            }
        }
        if(movieList)
            return movieList
        else
            return null
    }

    /**
     * Helper method to parse additional movie JSON data
     * @param jsonData JSON data to be parsed
     * @return formatted updated movie details as content value
     */
    static ContentValues parseAdditionalBasicMovieData(final def jsonData) {
        final def genreCount = jsonData.genres.size()
        def genreVal = null
        if (genreCount == 1) {
            genreVal = jsonData.genres[0].name
        } else if (genreCount == 2) {
            genreVal = jsonData.genres[0].name + " | " + jsonData.genres[1].name
        } else if (genreCount > 2) {
            genreVal = jsonData.genres[0].name + " | "
            for (final i in 1..(genreCount - 2)) {
                genreVal = genreVal + jsonData.genres[i].name + " | "
            }
            genreVal += jsonData.genres[genreCount - 1].name
        }
        LogDisplay.callLog(LOG_TAG, "Genre -> $genreVal", LogDisplay.JSON_PARSE_LOG_FLAG)

        final def prodCompanyCount = jsonData.production_companies.size()
        LogDisplay.callLog(LOG_TAG, "Production company Count -> $prodCompanyCount", LogDisplay.JSON_PARSE_LOG_FLAG)

        def prodCompanyVal = null
        if (prodCompanyCount == 1) {
            prodCompanyVal = jsonData.production_companies[0].name
            LogDisplay.callLog(LOG_TAG, "Production Companies 1-> $prodCompanyVal", LogDisplay.JSON_PARSE_LOG_FLAG)
        } else if (prodCompanyCount == 2) {
            prodCompanyVal = jsonData.production_companies[0].name + " | " + jsonData.production_companies[1].name
            LogDisplay.callLog(LOG_TAG, "Production Companies 1A-> $prodCompanyVal", LogDisplay.JSON_PARSE_LOG_FLAG)
        } else if (prodCompanyCount > 2) {
            prodCompanyVal = jsonData.production_companies[0].name + " | "
            LogDisplay.callLog(LOG_TAG, "Production Companies 2-> $prodCompanyVal", LogDisplay.JSON_PARSE_LOG_FLAG)
            for (final i in 1..(prodCompanyCount - 2)) {
                prodCompanyVal = prodCompanyVal + jsonData.production_companies[i].name + " | "
                LogDisplay.callLog(LOG_TAG, "Production Companies 3-> $prodCompanyVal", LogDisplay.JSON_PARSE_LOG_FLAG)
            }
            prodCompanyVal += jsonData.production_companies[prodCompanyCount - 1].name
        }
        LogDisplay.callLog(LOG_TAG, "Production Companies 4-> $prodCompanyVal", LogDisplay.JSON_PARSE_LOG_FLAG)

        final def prodCountryCount = jsonData.production_countries.size()
        def prodCountryVal = null
        if (prodCountryCount == 1) {
            prodCountryVal = jsonData.production_countries[0].name
        } else if (prodCountryCount == 2) {
            prodCountryVal = jsonData.production_countries[0].name + " | " + jsonData.production_countries[1].name
        } else if (prodCountryCount > 2) {
            prodCountryVal = jsonData.production_countries[0].name + " | "
            for (final i in 1..(prodCountryCount - 2)) {
                prodCountryVal = prodCountryVal + jsonData.production_countries[i].name + " | "
            }
            prodCountryVal += jsonData.production_countries[prodCountryCount - 1].name
        }
        LogDisplay.callLog(LOG_TAG, "Production Countries -> $prodCountryVal", LogDisplay.JSON_PARSE_LOG_FLAG)

        final ContentValues movieAllValues = new ContentValues()
        //if check is used for all json fields for null safe
        if (jsonData.belongs_to_collection) {
            if (jsonData.belongs_to_collection.id)
                movieAllValues.put(MovieBasicInfo.COLUMN_COLLECTION_ID, jsonData.belongs_to_collection.id)
            if (jsonData.belongs_to_collection.name)
                movieAllValues.put(MovieBasicInfo.COLUMN_COLLECTION_NAME, jsonData.belongs_to_collection.name)
            if (jsonData.belongs_to_collection.poster_path)
                movieAllValues.put(MovieBasicInfo.COLUMN_COLLECTION_POSTER_PATH, jsonData.belongs_to_collection.poster_path)
            if (jsonData.belongs_to_collection.backdrop_path)
                movieAllValues.put(MovieBasicInfo.COLUMN_COLLECTION_BACKDROP_PATH, jsonData.belongs_to_collection.backdrop_path)
        }
        if (jsonData.status)
            movieAllValues.put(MovieBasicInfo.COLUMN_RELEASE_STATUS, jsonData.status)
        if (jsonData.tagline)
            movieAllValues.put(MovieBasicInfo.COLUMN_TAGLINE, jsonData.tagline)
        if (genreVal)
            movieAllValues.put(MovieBasicInfo.COLUMN_GENRE, genreVal)
        if (jsonData.homepage)
            movieAllValues.put(MovieBasicInfo.COLUMN_HOME_PAGE, jsonData.homepage)
        if (jsonData.runtime)
            movieAllValues.put(MovieBasicInfo.COLUMN_RUNTIME, jsonData.runtime)
        if (jsonData.budget)
            movieAllValues.put(MovieBasicInfo.COLUMN_BUDGET, jsonData.budget)
        if (jsonData.revenue)
            movieAllValues.put(MovieBasicInfo.COLUMN_REVENUE, jsonData.revenue)
        if (prodCompanyVal)
            movieAllValues.put(MovieBasicInfo.COLUMN_PRODUCTION_COMPANIES, prodCompanyVal)
        if (prodCountryVal)
            movieAllValues.put(MovieBasicInfo.COLUMN_PRODUCTION_COUNTRIES, prodCountryVal)
        if (jsonData.imdb_id)
            movieAllValues.put(MovieBasicInfo.COLUMN_IMDB_ID, jsonData.imdb_id)

        //Following mandatory fields exist for existing record but still populated because
        //we need these when a insert is needed (click on person cast / crew grid)
        if (jsonData.id)
            movieAllValues.put(MovieBasicInfo.COLUMN_MOVIE_ID, jsonData.id)
        else {
            movieAllValues.put(MovieBasicInfo.COLUMN_MOVIE_ID, 0)
            LogDisplay.callLog(LOG_TAG, 'Not a valid movie id', LogDisplay.JSON_PARSE_LOG_FLAG)
        }
        if (jsonData.adult)
            movieAllValues.put(MovieBasicInfo.COLUMN_ADULT_FLAG, jsonData.adult)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_ADULT_FLAG, '')
        if (jsonData.backdrop_path)
            movieAllValues.put(MovieBasicInfo.COLUMN_BACKDROP_PATH, jsonData.backdrop_path)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_BACKDROP_PATH, '')
        if (jsonData.roriginal_title)
            movieAllValues.put(MovieBasicInfo.COLUMN_ORIGINAL_TITLE, jsonData.original_title)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_ORIGINAL_TITLE, '')
        if (jsonData.overview)
            movieAllValues.put(MovieBasicInfo.COLUMN_OVERVIEW, jsonData.overview)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_OVERVIEW, '')
        if (jsonData.release_date)
            movieAllValues.put(MovieBasicInfo.COLUMN_RELEASE_DATE, jsonData.release_date)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_RELEASE_DATE, '1900-01-01')
        if (jsonData.poster_path)
            movieAllValues.put(MovieBasicInfo.COLUMN_POSTER_PATH, jsonData.poster_path)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_POSTER_PATH, '')
        if (jsonData.popularity)
            movieAllValues.put(MovieBasicInfo.COLUMN_POPULARITY, jsonData.popularity)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_POPULARITY, 0.0)
        if (jsonData.title)
            movieAllValues.put(MovieBasicInfo.COLUMN_TITLE, jsonData.title)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_TITLE, '')
        if (jsonData.video)
            movieAllValues.put(MovieBasicInfo.COLUMN_VIDEO_FLAG, jsonData.video)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_VIDEO_FLAG, 'false')
        if (jsonData.vote_average)
            movieAllValues.put(MovieBasicInfo.COLUMN_VOTE_AVG, jsonData.vote_average)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_VOTE_AVG, 0.0)
        if (jsonData.vote_count)
            movieAllValues.put(MovieBasicInfo.COLUMN_VOTE_COUNT, jsonData.vote_count)
        else
            movieAllValues.put(MovieBasicInfo.COLUMN_VOTE_COUNT, 0)

        //Update the data present to indicate data is loaded
        movieAllValues.put(MovieMagicContract.MovieBasicInfo.COLUMN_DETAIL_DATA_PRESENT_FLAG,GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE)

        //Set update date
        movieAllValues.put(MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP,Utility.getTodayDate())

        if(movieAllValues)
            return movieAllValues
        else
            return null
    }

    /**
     * Helper method to parse similar movie JSON data
     * @param jsonData JSON data to be parsed
     * @param movieId Original movie id for which similar movies are fetched
     * @return formatted list of similar movies as content values
     */
    static List<ContentValues> parseSimilarMovieListJson(final Context ctx, final def jsonData, final int movieId) {
        final List<ContentValues> similarMovies
        LogDisplay.callLog(LOG_TAG, "Similar -> $jsonData.similar", LogDisplay.JSON_PARSE_LOG_FLAG)
        similarMovies = parseMovieListJson(ctx, jsonData.similar,GlobalStaticVariables.MOVIE_CATEGORY_SIMILAR,
                GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_SIMILAR, Utility.getTodayDate())
        //Some groovy magic - Closure
        similarMovies.each {it.put(MovieBasicInfo.COLUMN_SIMILAR_MOVIE_LINK_ID,movieId)}

        if(similarMovies)
            return similarMovies
        else
            return null
    }

    /**
     * Helper method to parse movie cast JSON data
     * @param jsonData JSON data to be parsed
     * @param movieId Original movie id for which movie casts are fetched
     * @param foreignKey Row id of primary movie table (movie_basic_info)
     * @return formatted list of movie cast as content values
     */
    static List<ContentValues> parseMovieCastJson(final def jsonData, final int movieId, final int foreignKey) {
        final List<ContentValues> movieCastList = []
        final def castCounter = jsonData.credits.cast.size() - 1
        if (jsonData.credits.cast) {
            for (final i in 0..castCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.credits.cast[i].name}", LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues movieCast = new ContentValues()
                //if is used for all non-mandatory json fields for null safe
                //if-else is used for all mandatory json fields for null safe
                if (jsonData.credits.cast[i].cast_id)
                    movieCast.put(MovieCast.COLUMN_CAST_ID, jsonData.credits.cast[i].cast_id)
                if (jsonData.credits.cast[i].character)
                    movieCast.put(MovieCast.COLUMN_CAST_CHARACTER, jsonData.credits.cast[i].character)
                else
                    movieCast.put(MovieCast.COLUMN_CAST_CHARACTER, '')
                if (jsonData.credits.cast[i].credit_id)
                    movieCast.put(MovieCast.COLUMN_CAST_CREDIT_ID, jsonData.credits.cast[i].credit_id)
                if (jsonData.credits.cast[i].id)
                    movieCast.put(MovieCast.COLUMN_CAST_PERSON_ID, jsonData.credits.cast[i].id)
                else
                    movieCast.put(MovieCast.COLUMN_CAST_PERSON_ID, 0)
                if (jsonData.credits.cast[i].name)
                    movieCast.put(MovieCast.COLUMN_CAST_PERSON_NAME, jsonData.credits.cast[i].name)
                else
                    movieCast.put(MovieCast.COLUMN_CAST_PERSON_NAME, ' ')
                if (jsonData.credits.cast[i].order)
                    movieCast.put(MovieCast.COLUMN_CAST_ORDER, jsonData.credits.cast[i].order)
                if (jsonData.credits.cast[i].profile_path)
                    movieCast.put(MovieCast.COLUMN_CAST_PROFILE_PATH, jsonData.credits.cast[i].profile_path)
                //Following two fields are populated internally, so null check not needed
                movieCast.put(MovieCast.COLUMN_CAST_ORIG_MOVIE_ID,movieId)
                movieCast.put(MovieCast.COLUMN_FOREIGN_KEY_ID,foreignKey)
                //Add item to the list now
                movieCastList << movieCast
            }
            return movieCastList
        }
        return null
    }

    /**
     * Helper method to parse movie crew JSON data
     * @param jsonData JSON data to be parsed
     * @param movieId Original movie id for which movie crews are fetched
     * @param foreignKey Row id of primary movie table (movie_basic_info)
     * @return formatted list of movie crew as content values
     */
    static List<ContentValues> parseMovieCrewJson(final def jsonData, final int movieId, final int foreignKey) {
        final List<ContentValues> movieCrewList = []
        final def crewCounter = jsonData.credits.crew.size() - 1
        if (jsonData.credits.crew) {
            for (final i in 0..crewCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.credits.crew[i].name}", LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues movieCrew = new ContentValues()
                //if is used for all non-mandatory json fields for null safe
                //if-else is used for all mandatory json fields for null safe
                if (jsonData.credits.crew[i].credit_id)
                    movieCrew.put(MovieCrew.COLUMN_CREW_CREDIT_ID, jsonData.credits.crew[i].credit_id)
                if (jsonData.credits.crew[i].department)
                    movieCrew.put(MovieCrew.COLUMN_CREW_DEPARTMENT, jsonData.credits.crew[i].department)
                if (jsonData.credits.crew[i].id)
                    movieCrew.put(MovieCrew.COLUMN_CREW_PERSON_ID, jsonData.credits.crew[i].id)
                else
                    movieCrew.put(MovieCrew.COLUMN_CREW_PERSON_ID, 0)
                if (jsonData.credits.crew[i].job)
                    movieCrew.put(MovieCrew.COLUMN_CREW_JOB, jsonData.credits.crew[i].job)
                else
                    movieCrew.put(MovieCrew.COLUMN_CREW_JOB, ' ')
                if (jsonData.credits.crew[i].name)
                    movieCrew.put(MovieCrew.COLUMN_CREW_PERSON_NAME, jsonData.credits.crew[i].name)
                else
                    movieCrew.put(MovieCrew.COLUMN_CREW_PERSON_NAME, ' ')
                if (jsonData.credits.crew[i].profile_path)
                    movieCrew.put(MovieCrew.COLUMN_CREW_PROFILE_PATH, jsonData.credits.crew[i].profile_path)
                //Following two fields are populated internally, so null check not needed
                movieCrew.put(MovieCrew.COLUMN_CREW_ORIG_MOVIE_ID,movieId)
                movieCrew.put(MovieCrew.COLUMN_FOREIGN_KEY_ID,foreignKey)
                //Add item to the list now
                movieCrewList << movieCrew
            }
            return movieCrewList
        }
        return null
    }

    /**
     * Helper method to parse movie image JSON data
     * @param jsonData JSON data to be parsed
     * @param movieId Original movie id for which movie images are fetched
     * @param foreignKey Row id of primary movie table (movie_basic_info)
     * @return formatted list of movie image as content values
     */
    static List<ContentValues> parseMovieImageJson(final def jsonData, final int movieId, final int foreignKey) {
        final List<ContentValues> movieImageList = []
        def imageCounter = jsonData.images.backdrops.size() - 1
        if (jsonData.images.backdrops) {
            for (final i in 0..imageCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.images.backdrops[i].file_path}", LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues movieImage = new ContentValues()
                //if is used for all non-mandatory json fields for null safe
                //if-else is used for all mandatory json fields for null safe
                if (jsonData.images.backdrops[i].file_path)
                    movieImage.put(MovieImage.COLUMN_IMAGE_FILE_PATH, jsonData.images.backdrops[i].file_path)
                else
                    movieImage.put(MovieImage.COLUMN_IMAGE_FILE_PATH, ' ')
                if (jsonData.images.backdrops[i].height)
                    movieImage.put(MovieImage.COLUMN_IMAGE_HEIGHT, jsonData.images.backdrops[i].height)
                if (jsonData.images.backdrops[i].width)
                    movieImage.put(MovieImage.COLUMN_IMAGE_WIDTH, jsonData.images.backdrops[i].width)
                //Following three fields are populated internally, so null check not needed
                movieImage.put(MovieImage.COLUMN_FOREIGN_KEY_ID,foreignKey)
                movieImage.put(MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID,movieId)
                movieImage.put(MovieImage.COLUMN_IMAGE_TYPE,GlobalStaticVariables.IMAGE_TYPE_BACKDROP)
                //Add item to the list now
                movieImageList << movieImage
            }
        }

        imageCounter = jsonData.images.posters.size() - 1
        if (jsonData.images.posters) {
            for (final i in 0..imageCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.images.posters[i].file_path}", LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues movieImage = new ContentValues()
                //if is used for all non-mandatory json fields for null safe
                //if-else is used for all mandatory json fields for null safe
                if (jsonData.images.posters[i].file_path)
                    movieImage.put(MovieImage.COLUMN_IMAGE_FILE_PATH, jsonData.images.posters[i].file_path)
                else
                    movieImage.put(MovieImage.COLUMN_IMAGE_FILE_PATH, ' ')
                if (jsonData.images.posters[i].height)
                    movieImage.put(MovieImage.COLUMN_IMAGE_HEIGHT, jsonData.images.posters[i].height)
                if (jsonData.images.posters[i].width)
                    movieImage.put(MovieImage.COLUMN_IMAGE_WIDTH, jsonData.images.posters[i].width)
                //Following three fields are populated internally, so null check not needed
                movieImage.put(MovieImage.COLUMN_FOREIGN_KEY_ID,foreignKey)
                movieImage.put(MovieImage.COLUMN_IMAGE_ORIG_MOVIE_ID,movieId)
                movieImage.put(MovieImage.COLUMN_IMAGE_TYPE,GlobalStaticVariables.IMAGE_TYPE_POSTER)
                //Add item to the list now
                movieImageList << movieImage
            }
        }
        if(movieImageList)
            return movieImageList
        else
            return null
    }

    /**
     * Helper method to parse movie now_playing JSON data
     * @param jsonData JSON data to be parsed
     * @param movieId Original movie id for which movie images are fetched
     * @param foreignKey Row id of primary movie table (movie_basic_info)
     * @return formatted list of movie now_playing as content values
     */
    static List<ContentValues> parseMovieVideoJson(
            final def jsonData, final int movieId, final int foreignKey, final int isForHomePage) {
        final List<ContentValues> movieVideoList = []
        final def videoCounter = jsonData.videos.results.size() - 1
        if (jsonData.videos.results) {
            for (final i in 0..videoCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.videos.results[i].name}", LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues movieVideo = new ContentValues()
                //if is used for all non-mandatory json fields for null safe
                //if-else is used for all mandatory json fields for null safe
                if (jsonData.videos.results[i].id)
                    movieVideo.put(MovieVideo.COLUMN_VIDEO_ID, jsonData.videos.results[i].id)
                if (jsonData.videos.results[i].key)
                    movieVideo.put(MovieVideo.COLUMN_VIDEO_KEY, jsonData.videos.results[i].key)
                else
                    movieVideo.put(MovieVideo.COLUMN_VIDEO_KEY, ' ')
                if (jsonData.videos.results[i].name)
                    movieVideo.put(MovieVideo.COLUMN_VIDEO_NAME, jsonData.videos.results[i].name)
                if (jsonData.videos.results[i].site)
                    movieVideo.put(MovieVideo.COLUMN_VIDEO_SITE, jsonData.videos.results[i].site)
                else
                    movieVideo.put(MovieVideo.COLUMN_VIDEO_SITE, ' ')
                if (jsonData.videos.results[i].size)
                    movieVideo.put(MovieVideo.COLUMN_VIDEO_SIZE, jsonData.videos.results[i].size)
                if (jsonData.videos.results[i].type)
                    movieVideo.put(MovieVideo.COLUMN_VIDEO_TYPE, jsonData.videos.results[i].type)
                else
                    movieVideo.put(MovieVideo.COLUMN_VIDEO_TYPE, ' ')
                //Following two fields are populated internally, so null check not needed
                movieVideo.put(MovieVideo.COLUMN_VIDEO_FOR_HOME_PAGE_USE_FLAG, isForHomePage)
                movieVideo.put(MovieVideo.COLUMN_FOREIGN_KEY_ID,foreignKey)
                movieVideo.put(MovieVideo.COLUMN_VIDEO_ORIG_MOVIE_ID,movieId)
                //Add item to the list now
                movieVideoList << movieVideo
            }
            return movieVideoList
        }
        return null
    }

    /**
     * Helper method to parse movie release date JSON data
     * @param jsonData JSON data to be parsed
     * @param movieId Original movie id for which movie images are fetched
     * @param foreignKey Row id of primary movie table (movie_basic_info)
     * @return formatted list of movie release date as content values
     */
    static List<ContentValues> parseMovieReleaseDateJson(final def jsonData, final int movieId, final int foreignKey) {
        final List<ContentValues> movieReleaseDateList = []
        final def releaseDateCounter = jsonData.release_dates.results.size() - 1
        if (jsonData.release_dates.results) {
            for (final i in 0..releaseDateCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.release_dates.results[i].iso_3166_1}", LogDisplay.JSON_PARSE_LOG_FLAG)
                //if is used for all non-mandatory json fields for null safe
                //if-else is used for all mandatory json fields for null safe
                if (jsonData.release_dates.results[i].iso_3166_1) {
                    def isoCountry = jsonData.release_dates.results[i].iso_3166_1
                    def releaseDateInnerCounter = jsonData.release_dates.results[i].release_dates.size() - 1
                    for(final j in 0..releaseDateInnerCounter) {
                        ContentValues movieReleaseDate = new ContentValues()
                        if(isoCountry)
                            movieReleaseDate.put(MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY,isoCountry)
                        else
                            movieReleaseDate.put(MovieReleaseDate.COLUMN_RELEASE_ISO_COUNTRY,' ')
                        if (jsonData.release_dates.results[i].release_dates[j].certification)
                            movieReleaseDate.put(MovieReleaseDate.COLUMN_RELEASE_CERTIFICATION, jsonData.release_dates.results[i].release_dates[j].certification)
                        if (jsonData.release_dates.results[i].release_dates[j].iso_639_1)
                            movieReleaseDate.put(MovieReleaseDate.COLUMN_RELEASE_ISO_LANGUAGE, jsonData.release_dates.results[i].release_dates[j].iso_639_1)
                        if (jsonData.release_dates.results[i].release_dates[j].note)
                            movieReleaseDate.put(MovieReleaseDate.COLUMN_RELEASE_NOTE, jsonData.release_dates.results[i].release_dates[j].note)
                        if (jsonData.release_dates.results[i].release_dates[j].release_date) {
                            //System detecting the data as Date when put directly from the JSON data,
                            //hence converting to String forcefully
                            String relDate = jsonData.release_dates.results[i].release_dates[j].release_date
                            movieReleaseDate.put(MovieReleaseDate.COLUMN_RELEASE_DATE, relDate)
                        }
                        else
                            movieReleaseDate.put(MovieReleaseDate.COLUMN_RELEASE_DATE, ' ')
                        if (jsonData.release_dates.results[i].release_dates[j].type)
                            movieReleaseDate.put(MovieReleaseDate.COLUMN_RELEASE_TYPE, jsonData.release_dates.results[i].release_dates[j].type)
                        //Following two fields are populated internally, so null check not needed
                        movieReleaseDate.put(MovieReleaseDate.COLUMN_FOREIGN_KEY_ID,foreignKey)
                        movieReleaseDate.put(MovieReleaseDate.COLUMN_RELEASE_ORIG_MOVIE_ID,movieId)
                        movieReleaseDateList << movieReleaseDate
                    }
                }
            }
            return movieReleaseDateList
        }
        return null
    }

    /**
     * Helper method to parse movie review JSON data
     * @param jsonData JSON data to be parsed
     * @param movieId Original movie id for which movie images are fetched
     * @param foreignKey Row id of primary movie table (movie_basic_info)
     * @return formatted list of movie review as content values
     */
    static List<ContentValues> parseMovieReviewJson(final def jsonData, final int movieId, final int foreignKey) {
        final List<ContentValues> movieReviewList = []
        final def reviewCounter = jsonData.reviews.results.size() - 1
        if (jsonData.reviews.results) {
            for (final i in 0..reviewCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.reviews.results[i].author}", LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues movieReview = new ContentValues()
                //if is used for all non-mandatory json fields for null safe
                //if-else is used for all mandatory json fields for null safe
                if (jsonData.reviews.results[i].id)
                    movieReview.put(MovieReview.COLUMN_REVIEW_ID, jsonData.reviews.results[i].id)
                else {
                    movieReview.put(MovieReview.COLUMN_REVIEW_ID, 0)
                    LogDisplay.callLog(LOG_TAG, 'Not a valid review id', LogDisplay.JSON_PARSE_LOG_FLAG)
                }
                if (jsonData.reviews.results[i].author)
                    movieReview.put(MovieReview.COLUMN_REVIEW_AUTHOR, jsonData.reviews.results[i].author)
                else
                    movieReview.put(MovieReview.COLUMN_REVIEW_AUTHOR, ' ')
                if (jsonData.reviews.results[i].content)
                    movieReview.put(MovieReview.COLUMN_REVIEW_CONTENT, jsonData.reviews.results[i].content)
                else
                    movieReview.put(MovieReview.COLUMN_REVIEW_CONTENT, ' ')
                if (jsonData.reviews.results[i].url)
                    movieReview.put(MovieReview.COLUMN_REVIEW_URL, jsonData.reviews.results[i].url)
                //Following two fields are populated internally, so null check not needed
                movieReview.put(MovieReview.COLUMN_FOREIGN_KEY_ID,foreignKey)
                movieReview.put(MovieReview.COLUMN_REVIEW_ORIG_MOVIE_ID,movieId)
                //Add item to the list now
                movieReviewList << movieReview
            }
            return movieReviewList
        }
        return null
    }

    /**
     * Helper method to parse recommendations movie JSON data
     * @param jsonData JSON data to be parsed
     * @param movieId Original movie id for which recommendations movies are fetched
     * @return formatted list of recommendations movies as content values
     */
    static List<ContentValues> parseRecommendationsMovieListJson(
            final Context ctx, final def jsonData, final int movieId) {
        LogDisplay.callLog(LOG_TAG, "Recommendations -> $jsonData.recommendations", LogDisplay.JSON_PARSE_LOG_FLAG)
        final List<ContentValues> recommendationsMovies = parseMovieListJson(ctx, jsonData.recommendations,
                GlobalStaticVariables.MOVIE_CATEGORY_RECOMMENDATIONS, GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_RECOMMENDATIONS,
                Utility.getTodayDate())
        // We just need 2 items of the recommended movies, so remove rest of the items
        // We could have just parsed two records, but better to reuse the code which is already tested and proven
        final List<ContentValues> returnValue = []
        if (recommendationsMovies) {
            if(recommendationsMovies.size() > 1) {
                for (final i in 0..1) {
                    LogDisplay.callLog(LOG_TAG, "Index $i and value ${recommendationsMovies[i]}", LogDisplay.JSON_PARSE_LOG_FLAG)
                    returnValue << recommendationsMovies.get(i)
                }
            } else {
                returnValue << recommendationsMovies.get(0)
            }
            //Some groovy magic - Closure
            returnValue.each {it.put(MovieBasicInfo.COLUMN_RECOMMENDATION_MOVIE_LINK_ID,movieId)}

            return returnValue
        } else {
            return null
        }
    }

    /**
     * Helper method to parse collection JSON data
     * @param jsonData JSON data to be parsed
     * @return formatted collection data as content values
     */
    static ContentValues parseCollectionDataJson(final def jsonData) {
        final ContentValues collectionData = new ContentValues()
        if(jsonData) {
            if (jsonData.id)
                collectionData.put(MovieCollection.COLUMN_COLLECTION_ID, jsonData.id)
            else {
                collectionData.put(MovieCollection.COLUMN_COLLECTION_ID, 0)
                LogDisplay.callLog(LOG_TAG, 'Not a valid collection id', LogDisplay.JSON_PARSE_LOG_FLAG)
            }
            if (jsonData.name)
                collectionData.put(MovieCollection.COLUMN_COLLECTION_NAME, jsonData.name)
            else
                collectionData.put(MovieCollection.COLUMN_COLLECTION_NAME, '')
            if(jsonData.overview)
                collectionData.put(MovieCollection.COLUMN_COLLECTION_OVERVIEW, jsonData.overview)
            else
                collectionData.put(MovieCollection.COLUMN_COLLECTION_OVERVIEW, '')
            if(jsonData.poster_path)
                collectionData.put(MovieCollection.COLUMN_COLLECTION_POSTER_PATH, jsonData.poster_path)
            else
                collectionData.put(MovieCollection.COLUMN_COLLECTION_POSTER_PATH, '')
            if(jsonData.backdrop_path)
                collectionData.put(MovieCollection.COLUMN_COLLECTION_BACKDROP_PATH, jsonData.backdrop_path)
            else
                collectionData.put(MovieCollection.COLUMN_COLLECTION_BACKDROP_PATH, '')

            collectionData.put(MovieCollection.COLUMN_COLLECTION_MOVIE_PRESENT_FLAG, GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE)
            collectionData.put(MovieCollection.COLUMN_COLLECTION_CREATE_TIMESTAMP, Utility.getTodayDate())
            collectionData.put(MovieCollection.COLUMN_COLLECTION_UPDATE_TIMESTAMP, Utility.getTodayDate())
            return collectionData
        }
        else return null
    }

    /**
     * Helper method to parse collection movie JSON
     * @param jsonData JSON data to be parsed
     * @return formatted list of collection movies as content values
     */
    static List<ContentValues> parseCollectionMovieJson(final def jsonData) {
        final List<ContentValues> collectionMovieList = []
        final def cnt = jsonData.parts.size() - 1
        //Ensure that the parts is not Null
        if (jsonData.parts) {
            for (final i in 0..cnt) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.parts[i].title}", LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues collectionMovieValue = new ContentValues()
                //if-else is used for all json fields for null safe
                if (jsonData.parts[i].id)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_MOVIE_ID, jsonData.parts[i].id)
                else {
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_MOVIE_ID, 0)
                    LogDisplay.callLog(LOG_TAG, 'Not a valid movie id', LogDisplay.JSON_PARSE_LOG_FLAG)
                }
                if (jsonData.parts[i].adult)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_ADULT_FLAG, jsonData.parts[i].adult)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_ADULT_FLAG, '')
                if (jsonData.parts[i].backdrop_path)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_BACKDROP_PATH, jsonData.parts[i].backdrop_path)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_BACKDROP_PATH, '')
                if (jsonData.parts[i].original_title)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_ORIGINAL_TITLE, jsonData.parts[i].original_title)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_ORIGINAL_TITLE, '')
                if (jsonData.parts[i].overview)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_OVERVIEW, jsonData.parts[i].overview)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_OVERVIEW, '')
                if (jsonData.parts[i].release_date)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_RELEASE_DATE, jsonData.parts[i].release_date)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_RELEASE_DATE, '1900-01-01')
                //date format -> yyyy-mm-dd
                if (jsonData.parts[i].poster_path)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_POSTER_PATH, jsonData.parts[i].poster_path)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_POSTER_PATH, '')
                if (jsonData.parts[i].popularity)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_POPULARITY, jsonData.parts[i].popularity)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_POPULARITY, 0.0)
                if (jsonData.parts[i].title)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_TITLE, jsonData.parts[i].title)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_TITLE, '')
                if (jsonData.parts[i].video)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_VIDEO_FLAG, jsonData.parts[i].video)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_VIDEO_FLAG, 'false')
                if (jsonData.parts[i].vote_average)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_VOTE_AVG, jsonData.parts[i].vote_average)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_VOTE_AVG, 0.0)
                if (jsonData.parts[i].vote_count)
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_VOTE_COUNT, jsonData.parts[i].vote_count)
                else
                    collectionMovieValue.put(MovieBasicInfo.COLUMN_VOTE_COUNT, 0)
                //Page number not used in collection movie, so populate as 0
                collectionMovieValue.put(MovieBasicInfo.COLUMN_PAGE_NUMBER, 0)
                //category and movieListType are collection and tmdb_collection
                collectionMovieValue.put(MovieBasicInfo.COLUMN_MOVIE_CATEGORY, GlobalStaticVariables.MOVIE_CATEGORY_COLLECTION)
                collectionMovieValue.put(MovieBasicInfo.COLUMN_MOVIE_LIST_TYPE, GlobalStaticVariables.MOVIE_LIST_TYPE_TMDB_COLLECTION)
                //This is important, populate the data with collection id because it's used to extract data in the program
                collectionMovieValue.put(MovieBasicInfo.COLUMN_COLLECTION_ID,jsonData.id)
                //Set create date and update date
                collectionMovieValue.put(MovieBasicInfo.COLUMN_CREATE_TIMESTAMP,Utility.getTodayDate())
                collectionMovieValue.put(MovieBasicInfo.COLUMN_UPDATE_TIMESTAMP,Utility.getTodayDate())

                //add to the list
                collectionMovieList << collectionMovieValue
            }
            return collectionMovieList
        }
        return null
    }

    /**
     * Helper method to parse person info JSON data
     * @param jsonData JSON data to be parsed
     * @param personId Person id
     * @return formatted person info data as content values
     */
    static ContentValues parsePersonInfoDataJson(final def jsonData, final int personId) {
        final ContentValues personInfoData = new ContentValues()
        if(jsonData) {
            if(jsonData.adult)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_ADULT_FLAG,jsonData.adult)

            final def alsoKnownAsCount = jsonData.also_known_as.size()
            def alsoKnownAsVal = null
            if (alsoKnownAsCount == 1) {
                alsoKnownAsVal = jsonData.also_known_as[0]
            } else if (alsoKnownAsCount == 2) {
                alsoKnownAsVal = jsonData.also_known_as[0] + " | " + jsonData.also_known_as[1]
            } else if (alsoKnownAsCount > 2) {
                alsoKnownAsVal = jsonData.also_known_as[0] + " | "
                for (final i in 1..(alsoKnownAsCount - 2)) {
                    alsoKnownAsVal = alsoKnownAsVal + jsonData.also_known_as[i] + " | "
                }
                alsoKnownAsVal += jsonData.also_known_as[alsoKnownAsCount - 1]
            }
            LogDisplay.callLog(LOG_TAG, "Also known as -> $alsoKnownAsVal", LogDisplay.JSON_PARSE_LOG_FLAG)

            if(alsoKnownAsVal)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_ALSO_KNOWN_AS,alsoKnownAsVal)

            if(jsonData.biography)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_BIOGRAPHY,jsonData.biography)
            if(jsonData.birthday)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_BIRTHDAY,jsonData.birthday)
            if(jsonData.deathday)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_DEATHDAY,jsonData.deathday)
            if(jsonData.homepage)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_HOMEPAGE,jsonData.homepage)
            if(jsonData.id)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_ID,jsonData.id)
            else {
                LogDisplay.callLog(LOG_TAG, 'Not a valid person id', LogDisplay.JSON_PARSE_LOG_FLAG)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_ID, personId)
            }
            if(jsonData.imdb_id)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_IMDB_ID,jsonData.imdb_id)
            if(jsonData.name)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_NAME,jsonData.name)
            else {
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_NAME, ' ')
                LogDisplay.callLog(LOG_TAG, 'Not a valid person name', LogDisplay.JSON_PARSE_LOG_FLAG)
            }
            if(jsonData.place_of_birth)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_PLACE_OF_BIRTH,jsonData.place_of_birth)
            if(jsonData.popularity)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_POPULARITY,jsonData.popularity)
            if(jsonData.profile_path)
                personInfoData.put(MoviePersonInfo.COLUMN_PERSON_PROFILE_PATH,jsonData.profile_path)
            personInfoData.put(MoviePersonInfo.COLUMN_PERSON_PRESENT_FLAG,GlobalStaticVariables.MOVIE_MAGIC_FLAG_TRUE)
            personInfoData.put(MoviePersonInfo.COLUMN_PERSON_CREATE_TIMESTAMP,Utility.getTodayDate())
            personInfoData.put(MoviePersonInfo.COLUMN_PERSON_UPDATE_TIMESTAMP,Utility.getTodayDate())
            return personInfoData
        }
        else return null
    }

    /**
     * Helper method to parse person cast JSON data
     * @param jsonData JSON data to be parsed
     * @param personId Person id
     * @param foreignKey Row id of person info table
     * @return formatted list of person cast data as content values
     */
    static List<ContentValues> parsePersonCastDataJson(final def jsonData, final int personId, final long foreignKey) {
        final List<ContentValues> personCastList = []
        final def castCounter = jsonData.movie_credits.cast.size() - 1
        if (jsonData.movie_credits.cast) {
            for (final i in 0..castCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.movie_credits.cast[i].character} -> " +
                        "${jsonData.movie_credits.cast[i].original_title}", LogDisplay.JSON_PARSE_LOG_FLAG)

                ContentValues personCastData = new ContentValues()
                if (foreignKey) {
                    personCastData.put(MoviePersonCast.COLUMN_FOREIGN_KEY_ID, foreignKey)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person info row id', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personCastData.put(MoviePersonCast.COLUMN_FOREIGN_KEY_ID, 0)
                }
                if (personId) {
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID, personId)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person id', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_ORIG_PERSON_ID, 0)
                }
                if (jsonData.movie_credits.cast[i].adult)
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_ADULT_FLAG, jsonData.movie_credits.cast[i].adult)
                if (jsonData.movie_credits.cast[i].character) {
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_CHARACTER, jsonData.movie_credits.cast[i].character)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person character', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_CHARACTER, ' ')
                }
                if (jsonData.movie_credits.cast[i].credit_id)
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_CREDIT_ID, jsonData.movie_credits.cast[i].credit_id)
                if (jsonData.movie_credits.cast[i].id) {
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_MOVIE_ID, jsonData.movie_credits.cast[i].id)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person cast movie id', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_MOVIE_ID, 0)
                }
                if (jsonData.movie_credits.cast[i].original_title)
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_ORIG_TITLE, jsonData.movie_credits.cast[i].original_title)
                if (jsonData.movie_credits.cast[i].poster_path)
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_POSTER_PATH, jsonData.movie_credits.cast[i].poster_path)
                if (jsonData.movie_credits.cast[i].release_date)
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_RELEASE_DATE, jsonData.movie_credits.cast[i].release_date)
                if (jsonData.movie_credits.cast[i].title)
                    personCastData.put(MoviePersonCast.COLUMN_PERSON_CAST_TITLE, jsonData.movie_credits.cast[i].title)

                personCastList << personCastData
            }
            return personCastList
        }
        return null
    }

    /**
     * Helper method to parse person crew JSON data
     * @param jsonData JSON data to be parsed
     * @param personId Person id
     * @param foreignKey Row id of person info table
     * @return formatted list of person crew data as content values
     */
    static List<ContentValues> parsePersonCrewDataJson(final def jsonData, final int personId, final long foreignKey) {
        final List<ContentValues> personCrewList = []
        final def crewCounter = jsonData.movie_credits.crew.size() - 1
        if (jsonData.movie_credits.crew) {
            for (final i in 0..crewCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.movie_credits.crew[i].job} -> " +
                        "${jsonData.movie_credits.crew[i].original_title}", LogDisplay.JSON_PARSE_LOG_FLAG)

                ContentValues personCrewData = new ContentValues()
                if (foreignKey) {
                    personCrewData.put(MoviePersonCrew.COLUMN_FOREIGN_KEY_ID, foreignKey)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person info row id', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personCrewData.put(MoviePersonCrew.COLUMN_FOREIGN_KEY_ID, 0)
                }
                if (personId) {
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID, personId)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person id', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_PERSON_ID, 0)
                }
                if (jsonData.movie_credits.crew[i].adult)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_ADULT_FLAG, jsonData.movie_credits.crew[i].adult)
                if (jsonData.movie_credits.crew[i].credit_id)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_CREDIT_ID, jsonData.movie_credits.crew[i].credit_id)
                if (jsonData.movie_credits.crew[i].department) {
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_DEPARTMENT, jsonData.movie_credits.crew[i].department)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person department', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_DEPARTMENT, ' ')
                }
                if (jsonData.movie_credits.crew[i].id) {
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_MOVIE_ID, jsonData.movie_credits.crew[i].id)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person crew movie id', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_MOVIE_ID, 0)
                }
                if (jsonData.movie_credits.crew[i].job)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_JOB, jsonData.movie_credits.crew[i].job)
                if (jsonData.movie_credits.crew[i].original_title)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_ORIG_TITLE, jsonData.movie_credits.crew[i].original_title)
                if (jsonData.movie_credits.crew[i].poster_path)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_POSTER_PATH, jsonData.movie_credits.crew[i].poster_path)
                if (jsonData.movie_credits.crew[i].release_date)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_RELEASE_DATE, jsonData.movie_credits.crew[i].release_date)
                if (jsonData.movie_credits.crew[i].title)
                    personCrewData.put(MoviePersonCrew.COLUMN_PERSON_CREW_TITLE, jsonData.movie_credits.crew[i].title)

                personCrewList << personCrewData
            }
            return personCrewList
        }
        return null
    }

    /**
     * Helper method to parse person images JSON data
     * @param jsonData JSON data to be parsed
     * @param personId Person id
     * @param foreignKey Row id of person info table
     * @return formatted list of person image data as content values
     */
    static List<ContentValues> parsePersonImageDataJson(final def jsonData, final int personId, final long foreignKey) {
        final List<ContentValues> personImageList = []
        final def imageCounter = jsonData.profiles.size() - 1
        if (jsonData.profiles) {
            for (final i in 0..imageCounter) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.profiles[i].file_path}", LogDisplay.JSON_PARSE_LOG_FLAG)

                ContentValues personImageData = new ContentValues()
                if (foreignKey) {
                    personImageData.put(MoviePersonImage.COLUMN_FOREIGN_KEY_ID, foreignKey)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person info row id', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personImageData.put(MoviePersonImage.COLUMN_FOREIGN_KEY_ID, 0)
                }
                if (personId) {
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID, personId)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid person id', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_ORIG_PERSON_ID, 0)
                }
                if (jsonData.profiles[i].aspect_ratio)
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_ASPECT_RATIO, jsonData.profiles[i].aspect_ratio)
                if (jsonData.profiles[i].file_path) {
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_FILE_PATH, jsonData.profiles[i].file_path)
                } else {
                    LogDisplay.callLog(LOG_TAG, 'Not a valid image file path', LogDisplay.JSON_PARSE_LOG_FLAG)
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_FILE_PATH, ' ')
                }
                if (jsonData.profiles[i].height)
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_HEIGHT, jsonData.profiles[i].height)
                if (jsonData.profiles[i].iso_639_1)
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_ISO_639_1, jsonData.profiles[i].iso_639_1)
                if (jsonData.profiles[i].vote_average)
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_VOTE_AVERAGE, jsonData.profiles[i].vote_average)
                if (jsonData.profiles[i].vote_count)
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_VOTE_COUNT, jsonData.profiles[i].vote_count)
                if (jsonData.profiles[i].width)
                    personImageData.put(MoviePersonImage.COLUMN_PERSON_IMAGE_WIDTH, jsonData.profiles[i].width)

                personImageList << personImageData
            }
            return personImageList
        }
        return null
    }

    /**
     * Helper method to parse JSON data for TMDb request token for login
     * @param jsonData The JSON data to be parsed
     * @param respCode The response code of Http call
     * @return The Bundle with parsed data or error message & error flag
     */
    static Bundle parseTmdbToken(final def jsonData, final int respCode) {
        LogDisplay.callLog(LOG_TAG, 'parseTmdbToken is called', LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "respCode -> $respCode", LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "jsonData -> $jsonData", LogDisplay.JSON_PARSE_LOG_FLAG)
        final Bundle bundle = new Bundle()
        if(respCode == 200) {
            bundle.putString(GlobalStaticVariables.TMDB_REQ_TOKEN,jsonData.request_token)
            bundle.putBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG,false)
            return bundle
        } else {
            bundle.putString(GlobalStaticVariables.TMDB_AUTH_ERROR_MSG, jsonData.status_message)
            bundle.putBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG,true)
            return bundle
        }
    }

    /**
     * Helper method to parse JSON data for TMDb authenticated token after successful login
     * @param jsonData The JSON data to be parsed
     * @param respCode The response code of Http call
     * @return The Bundle with parsed data or error message & error flag
     */
    static Bundle parseTmdbAuthenticatedToken(final def jsonData, final int respCode) {
        LogDisplay.callLog(LOG_TAG, 'parseTmdbAuthenticatedToken is called', LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "respCode -> $respCode", LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "jsonData -> $jsonData", LogDisplay.JSON_PARSE_LOG_FLAG)
        final Bundle bundle = new Bundle()
        if(respCode == 200) {
            bundle.putString(GlobalStaticVariables.TMDB_AUTHENTICATED_TOKEN,jsonData.request_token)
            bundle.putBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG,false)
            return bundle
        } else {
            bundle.putString(GlobalStaticVariables.TMDB_AUTH_ERROR_MSG, jsonData.status_message)
            bundle.putBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG,true)
            return bundle
        }
    }

    /**
     * Helper method to parse JSON data for TMDb session id
     * @param jsonData The JSON data to be parsed
     * @param respCode The response code of Http call
     * @return The Bundle with parsed data or error message & error flag
     */
    static Bundle parseTmdbSessionId(final def jsonData, final int respCode) {
        LogDisplay.callLog(LOG_TAG, 'parseTmdbSessionId is called', LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "respCode -> $respCode", LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "jsonData -> $jsonData", LogDisplay.JSON_PARSE_LOG_FLAG)
        final Bundle bundle = new Bundle()
        if(respCode == 200) {
            bundle.putString(GlobalStaticVariables.TMDB_SESSION_ID,jsonData.session_id)
            bundle.putBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG,false)
            return bundle
        } else {
            bundle.putString(GlobalStaticVariables.TMDB_AUTH_ERROR_MSG, jsonData.status_message)
            bundle.putBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG,true)
            return bundle
        }
    }

    /**
     * Helper method to parse JSON data for TMDb account information
     * @param jsonData The JSON data to be parsed
     * @param respCode The response code of Http call
     * @return The Bundle with parsed data or error message & error flag
     */
    static Bundle parseTmdbAccountInfo(final def jsonData, final int respCode) {
        LogDisplay.callLog(LOG_TAG, 'parseTmdbAccountInfo is called', LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "respCode -> $respCode", LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "jsonData -> $jsonData", LogDisplay.JSON_PARSE_LOG_FLAG)
        final Bundle bundle = new Bundle()
        if(respCode == 200) {
            if(jsonData.name)
                bundle.putString(GlobalStaticVariables.TMDB_USER_NAME,jsonData.name)
            else
                bundle.putString(GlobalStaticVariables.TMDB_USER_NAME,'')
            bundle.putBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG,false)
            bundle.putString(GlobalStaticVariables.TMDB_USER_ACCOUNT_ID,Integer.toString(jsonData.id))
            return bundle
        } else {
            bundle.putString(GlobalStaticVariables.TMDB_AUTH_ERROR_MSG, jsonData.status_message)
            bundle.putBoolean(GlobalStaticVariables.TMDB_AUTH_ERROR_FLAG,true)
            return bundle
        }
    }

    /**
     * Helper method to parse JSON data for TMDb's POST request for user list
     * @param jsonData The JSON data to be parsed
     * @param respCode The response code of Http call
     * @return The Status meesage of the request
     */
    static String parseTmdbUserListPostResponse(final def jsonData, final int respCode) {
        LogDisplay.callLog(LOG_TAG, 'parseTmdbUserListPostResponse is called', LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "respCode -> $respCode", LogDisplay.JSON_PARSE_LOG_FLAG)
        LogDisplay.callLog(LOG_TAG, "jsonData -> $jsonData", LogDisplay.JSON_PARSE_LOG_FLAG)
        return jsonData.status_message
    }

    /**
     * Helper method to parse movie JSON data
     * @param jsonData JSON data to be parsed
     * @return formatted list of movies as content values
     */
    static List<ContentValues> parseSearchMovieListJson(final def jsonData, final String queryString) {
        final List<ContentValues> movieList = []
        final def cnt = jsonData.results.size() - 1
        //Ensure that the results is not Null
        if (jsonData.results) {
            for (final i in 0..cnt) {
                LogDisplay.callLog(LOG_TAG, "$i -> ${jsonData.results[i].title}", LogDisplay.JSON_PARSE_LOG_FLAG)
                ContentValues movieValue = new ContentValues()
                //if-else is used for all json fields for null safe
                if (jsonData.results[i].id)
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_MOVIE_ID, jsonData.results[i].id)
                else {
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_MOVIE_ID, 0)
                    LogDisplay.callLog(LOG_TAG, 'Not a valid movie id', LogDisplay.JSON_PARSE_LOG_FLAG)
                }
                if (jsonData.results[i].adult)
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_ADULT_FLAG, jsonData.results[i].adult)
                else
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_ADULT_FLAG, '')
                if (jsonData.results[i].backdrop_path)
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_BACKDROP_PATH, jsonData.results[i].backdrop_path)
                else
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_BACKDROP_PATH, '')
                if (jsonData.results[i].original_title)
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_ORIGINAL_TITLE, jsonData.results[i].original_title)
                else
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_ORIGINAL_TITLE, '')
                //date format -> yyyy-mm-dd
                if (jsonData.results[i].release_date)
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_RELEASE_DATE, jsonData.results[i].release_date)
                else
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_RELEASE_DATE, '1900-01-01')
                if (jsonData.results[i].poster_path)
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_POSTER_PATH, jsonData.results[i].poster_path)
                else
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_POSTER_PATH, '')
                if (jsonData.results[i].title)
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_TITLE, jsonData.results[i].title)
                else
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_TITLE, '')
                if (jsonData.page)
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_PAGE_NUMBER, jsonData.page)
                else
                    movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_PAGE_NUMBER, 0)

                //category is supplied in the program, so always null safe
                movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_MOVIE_CATEGORY, GlobalStaticVariables.MOVIE_CATEGORY_SEARCH)
                movieValue.put(SearchDatabaseTable.SEARCH_FTS_COLUMN_QUERY_STRING, queryString)

                //add to the list
                movieList << movieValue
            }
        }
        if(movieList)
            return movieList
        else
            return null
    }
}