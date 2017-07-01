/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import com.moviemagic.dpaul.android.app.authentication.TmdbAuthenticateInterface
import com.moviemagic.dpaul.android.app.authentication.TmdbServerAuthenticate
import groovy.transform.CompileStatic

@CompileStatic
class GlobalStaticVariables {
    //Static variables for movie list type
    public static final String MOVIE_LIST_TYPE_TMDB_PUBLIC = 'tmdb_public'
    public static final String MOVIE_LIST_TYPE_TMDB_USER = 'tmdb_user'
    public static final String MOVIE_LIST_TYPE_TMDB_SIMILAR = 'tmdb_similar'
    public static final String MOVIE_LIST_TYPE_TMDB_RECOMMENDATIONS = 'tmdb_recommendations'
    public static final String MOVIE_LIST_TYPE_TMDB_COLLECTION = 'tmdb_collection'
    public static final String MOVIE_LIST_TYPE_TMDB_PERSON = 'tmdb_person'
    public static final String MOVIE_LIST_TYPE_USER_LOCAL_LIST = 'user_local_list'
    public static final String MOVIE_LIST_TYPE_ORPHANED = 'orphaned_list'
    public static final String MOVIE_LIST_TYPE_SEARCH = 'search_list'

    //Static variables for movie category
    public static final String MOVIE_CATEGORY_POPULAR = 'popular'  //tmdb popular category
    public static final String MOVIE_CATEGORY_TOP_RATED = 'top_rated' //tmdb top_rated category
    public static final String MOVIE_CATEGORY_UPCOMING = 'upcoming' //tmdb upcoming category
    public static final String MOVIE_CATEGORY_NOW_PLAYING = 'now_playing' //tmdb now_playing category
    public static final String MOVIE_CATEGORY_SIMILAR = 'similar_category' //category to store similar movie, internal use only
    public static final String MOVIE_CATEGORY_RECOMMENDATIONS = 'recommendations_category' //category to store similar movie, internal use only
    public static final String MOVIE_CATEGORY_COLLECTION = 'collection_category' //category to store collection movie, internal use only
    public static final String MOVIE_CATEGORY_PERSON = 'person_category' //category to store person cast & crew movie, internal use only
    public static final String MOVIE_CATEGORY_TMDB_USER_FAVOURITE = 'favorite' //category to store tmdb user favourite movies, internal use only
    public static final String MOVIE_CATEGORY_TMDB_USER_WATCHLIST = 'watchlist' //category to store tmdb user watchlist movies, internal use only
    public static final String MOVIE_CATEGORY_TMDB_USER_RATED = 'rated' //category to store tmdb user rated movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_WATCHED = 'local_user_watched_category' //category to store user watched movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_WISH_LIST = 'local_user_wish_list_category' //category to store user wish list movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_FAVOURITE = 'local_user_favourite_category' //category to store user favourite movies, internal use only
    public static final String MOVIE_CATEGORY_LOCAL_USER_COLLECTION = 'local_user_collection_category' //category to store user collection movies, internal use only
    public static final String MOVIE_CATEGORY_ORPHANED = 'orphaned_category' //category for orphaned records (not needed user list movie)
    public static final String MOVIE_CATEGORY_SEARCH = 'search_category' //category for search movie records

    //Static variables for TMDb URL and parameters
    public static final String TMDB_MOVIE_BASE_URL = 'https://api.themoviedb.org/3/'
    public static final String TMDB_WEB_MOVIE_BASE_URL = 'https://www.themoviedb.org/movie/'
    public static final String TMDB_WEB_COLLECTION_BASE_URL = 'https://www.themoviedb.org/collection/'
    public static final String TMDB_MOVIE_PATH = 'movie'
    public static final String TMDB_MOVIE_API_KEY = 'api_key'
    public static final String TMDB_MOVIE_PAGE = 'page'
    public static final String TMDB_APPEND_TO_RESPONSE_KEY = 'append_to_response'
    public static final String TMDB_MOVIE_APPEND_TO_RESPONSE_PARAM = 'similar,credits,images,videos,release_dates,reviews,recommendations'
    public static final String TMDB_PERSON_APPEND_TO_RESPONSE_PARAM = 'movie_credits'
    public static final String TMDB_COLLECTION_PATH = 'collection'
    public static final String TMDB_PERSON_PATH = 'person'
    public static final String TMDB_PERSON_IMAGE_PATH = 'images'
    public static final String TMDB_AUTHENTICATION_PATH = 'authentication'
    public static final String TMDB_TOKEN_PATH = 'token'
    public static final String TMDB_NEW_PATH = 'new'
    public static final String TMDB_SESSION_PATH = 'session'
    public static final String TMDB_ACCOUNT_PATH = 'account'
    public static final String TMDB_VALIDATE_WITH_LOGIN_PATH = 'validate_with_login'
    public static final String TMDB_AUTHENTICATE_TOKEN_KEY = 'request_token'
    public static final String TMDB_AUTHENTICATE_USER_NAME_KEY = 'username'
    public static final String TMDB_AUTHENTICATE_PASSWORD_KEY = 'password'
    public static final String TMDB_SESSION_ID_KEY = 'session_id'
    public static final String TMDB_USER_MOVIES_PATH = 'movies'
    public static final String TMDB_WATCHLIST_PATH = 'watchlist'
    public static final String TMDB_FAVOURITE_PATH = 'favorite'
    public static final String TMDB_RATED_PATH = 'rating'
    public static final String TMDB_SEARCH_PATH = 'search'
    public static final String TMDB_PARAMETER_QUERY = 'query'

    //Static variables for TMDb movie image url and parameters
    public static final String TMDB_IMAGE_BASE_URL = 'http://image.tmdb.org/t/p/'
    public static final String TMDB_IMAGE_SIZE_W92 = 'w92'
    public static final String TMDB_IMAGE_SIZE_W185 = 'w185'
    public static final String TMDB_IMAGE_SIZE_W300 = 'w300'
    public static final String TMDB_IMAGE_SIZE_W500 = 'w500'
    public static final String TMDB_IMAGE_SIZE_W780 = 'w780'

    //Static variables for IMDb URL - used to create the IMDb intent
    public static final String IMDB_BASE_MOVIE_TITLE_URL = 'http://www.imdb.com/title/'
    public static final String IMDB_BASE_PERSON_URL = 'http://www.imdb.com/name/'

    //Static variables for user movie list
    public static final String USER_LIST_WATCHED = 'watched'
    public static final String USER_LIST_WISH_LIST = 'wish_list'
    public static final String USER_LIST_FAVOURITE = 'favourite'
    public static final String USER_LIST_COLLECTION = 'collection'
    public static final String USER_LIST_USER_RATING = 'user_rating'
    public static final String USER_LIST_ADD_FLAG = 'use_list_add'
    public static final String USER_LIST_REMOVE_FLAG = 'user_list_remove'
    public static final String USER_RATING_ADD_FLAG = 'rating_add'
    public static final String USER_RATING_REMOVE_FLAG = 'rating_remove'

    //Static variables for Authentication
    public static final String AUTHTOKEN_TYPE_READ_ONLY = 'Read only'
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = 'Read only access to a MovieMagic account'
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = 'Full access'
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = 'Full access to a MovieMagic account'
    public static final TmdbAuthenticateInterface sTmdbAuthenticateInterface = new TmdbServerAuthenticate()
    public static final String TMDB_AUTH_TOKEN = 'tmdb_auth_token'
    public static final String TMDB_USER_NAME = 'tmdb_user_name'
    public static final String TMDB_AUTH_ERROR_MSG = 'tmdb_error_msg'
    public static final String TMDB_AUTH_ERROR_FLAG = 'tmdb_error_flag'
    public static final String TMDB_REQ_TOKEN = 'tmdb_req_token'
    public static final String TMDB_AUTHENTICATED_TOKEN = 'tmdb_authenticated_token'
    public static final String TMDB_SESSION_ID = 'tmdb_session_id'
    public static final String TMDB_USER_ACCOUNT_ID = 'tmdb_user_account_id'
    public static final String TMDB_USERDATA_ACCOUNT_ID = 'tmdb_userdata_account_id'

    //Misc variables
    public static final String IMAGE_TYPE_BACKDROP = 'backdrop'
    public static final String IMAGE_TYPE_POSTER = 'poster'
    public static final String MOVIE_BASIC_INFO_MOVIE_ID = 'movie_basic_info_movie_id'
    public static final String MOVIE_BASIC_INFO_CATEGORY = 'movie_basic_info_category'
    public static final int MOVIE_MAGIC_FLAG_TRUE = 1
    public static final int MOVIE_MAGIC_FLAG_FALSE = 0
    public static final float MOVIE_MAGIC_ELEVATION = 4f
    public static final float MOVIE_MAGIC_ELEVATION_RESET = 0f
    public static final float MOVIE_MAGIC_ALPHA_FULL_OPAQUE = 1f
    public static final float MOVIE_MAGIC_ALPHA_OPAQUE_40_PERCENT = 0.4f
    public static final String MOVIE_COLLECTION_URI = 'collection_uri'
    public static final String MOVIE_CATEGORY_AND_COLL_ID_URI = 'category_and_collection_id_uri'
    public static final String PICASSO_POSTER_IMAGE_TAG = 'picasso_poster_image_tag'
    public static final String COLLECTION_MOVIE_FRAGMENT_TAG = 'collection_movie_fragment_tag'
    public static final String MOVIE_PERSON_URI = 'person_uri'
    public static final String MOVIE_PERSON_ID = 'person_uri'
    public static final String MOVIE_PERSON_BACKDROP_PATH = 'person_backdrop_path'
    public static final String IMAGE_VIEWER_IMAGE_PATH_ARRAY = 'imageviewer_image_path_array'
    public static final String IMAGE_VIEWER_TITLE = 'imageviewer_title'
    public static final String IMAGE_VIEWER_ADAPTER_POSITION = 'imageviewer_adapter_position'
    public static final String IMAGE_VIEWER_BACKDROP_IMAGE_FLAG = 'imageviewer_backdrop_image_flag'
    public static final String DETAIL_BACKDROP_VIEWPAGER_POSITION = 'detail_backdrop_viewpager_position'
    public static final int HOME_PAGE_MAX_MOVIE_SHOW_COUNTER = 6
    public static final int MAX_NOTIFICATION_COUNTER = 3
    public static final String MOVIE_VIDEO_SITE_YOUTUBE = 'YouTube'
    public static final String MOVIE_VIDEO_SITE_TYPE = 'Trailer'
    public static boolean WIFI_CONNECTED = false
    public static boolean MOBILE_CONNECTED = false
    public static final int NULL_CATEGORY_FLAG = 0
    public static final int PERSON_CATEGORY_FLAG = 13
    public static final int SEARCH_CATEGORY_FLAG = 8
}