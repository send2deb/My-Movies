/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.backgroundmodules

import android.util.Log
import groovy.transform.CompileStatic

@CompileStatic
/**
 * Helper method to display log
 * All logs can be switched on or off by changing the SHOW_LOG boolean variable
 *
 * @param tag The tag to be used for this log
 * @param msg The log message to be displayed
 * @param logFlag The flag to indicate if log needs to be printed
 */

class LogDisplay {
    private static final boolean SHOW_ALL_LOG_FLAG = false

    public static final boolean MOVIE_MAGIC_CONTRACT_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_DB_HELPER_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_PROVIDER_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_AUTHENTICATOR_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_AUTHENTICATOR_SERVICE_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_AUTHENTICATOR_ACTIVITY_LOG_FLAG = false
    public static final boolean TMDB_SERVER_AUTHENTICATE_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_SYNC_ADAPTER_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_SYNC_ADAPTER_UTILITY_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_SYNC_SERVICE_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_MAIN_LOG_FLAG = false
    public static final boolean JSON_PARSE_LOG_FLAG = false
    public static final boolean GRID_MOVIE_FRAGMENT_LOG_FLAG = false
    public static final boolean GRID_ADAPTER_LOG_FLAG = false
    public static final boolean GRID_RECYCLER_ADAPTER_LOG_FLAG = false
    public static final boolean LOAD_MORE_MOVIES_LOG_FLAG = false
    public static final boolean DETAIL_MOVIE_ACTIVITY_LOG_FLAG = false
    public static final boolean DETAIL_MOVIE_FRAGMENT_LOG_FLAG = false
    public static final boolean LOAD_MOVIE_DETAILS_LOG_FLAG = false
    public static final boolean MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG = false
    public static final boolean SIMILAR_MOVIE_ADAPTER_LOG_FLAG = false
    public static final boolean MOVIE_CAST_ADAPTER_LOG_FLAG = false
    public static final boolean MOVIE_CREW_ADAPTER_LOG_FLAG = false
    public static final boolean MOVIE_REVIEW_ADAPTER_LOG_FLAG = false
    public static final boolean UPDATE_USER_LIST_LOG_FLAG = false
    public static final boolean UTILITY_LIST_LOG_FLAG = false
    public static final boolean COLLECTION_MOVIE_ACTIVITY_LOG_FLAG = false
    public static final boolean COLLECTION_MOVIE_FRAGMENT_LOG_FLAG = false
    public static final boolean LOAD_COLLECTION_DATA_LOG_FLAG = false
    public static final boolean PERSON_MOVIE_ACTIVITY_LOG_FLAG = false
    public static final boolean PERSON_MOVIE_FRAGMENT_LOG_FLAG = false
    public static final boolean LOAD_PERSON_DATA_LOG_FLAG = false
    public static final boolean PERSON_CAST_ADAPTER_LOG_FLAG = false
    public static final boolean PERSON_CREW_ADAPTER_LOG_FLAG = false
    public static final boolean PERSON_IMAGE_ADAPTER_LOG_FLAG = false
    public static final boolean IMAGE_VIEWER_ACTIVITY_LOG_FLAG = false
    public static final boolean IMAGE_PAGER_ADAPTER_LOG_FLAG = false
    public static final boolean DETAIL_FRAGMENT_PAGER_ADAPTER_LOG_FLAG = false
    public static final boolean HOME_MOVIE_FRAGMENT_LOG_FLAG = false
    public static final boolean HOME_MOVIE_ADAPTER_LOG_FLAG = false
    public static final boolean UPLOAD_TMDB_REQUEST_LOG_FLAG = false
    public static final boolean SETTINGS_FRAGMENT_LOG_FLAG = false
    public static final boolean SETTINGS_ACTIVITY_LOG_FLAG = false
    public static final boolean NETWORK_RECEIVER_LOG_FLAG = false
    public static final boolean SEARCH_ACTIVITY_LOG_FLAG = false
    public static final boolean SEARCH_DATABASE_TABLE_LOG_FLAG = false
    public static final boolean DONATE_ACTIVITY_LOG_FLAG = false
    public static final boolean DONATE_ADAPTER_LOG_FLAG = false

    static void callLog(final String tag, final String msg, final Boolean logFlag) {
        if (SHOW_ALL_LOG_FLAG) {
            if (logFlag) {
                Log.v(tag, msg)
            }
        }
    }
}