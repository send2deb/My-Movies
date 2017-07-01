/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.youtube

import android.os.Bundle
import android.widget.Toast
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.moviemagic.dpaul.android.app.BuildConfig
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import groovy.transform.CompileStatic

/**
 * This YouTube fragment is built based on a blog post written by Scott Cooper. Many thanks to Scott Cooper.
 * (reference http://createdineden.com/blog/post/android-tutorial-how-to-integrate-youtube-videos-into-your-app/)
 */

@CompileStatic
class MovieMagicYoutubeFragment extends YouTubePlayerSupportFragment implements YouTubePlayer.OnInitializedListener,
                   YouTubePlayer.OnFullscreenListener {
    private static final String LOG_TAG = MovieMagicYoutubeFragment.class.getSimpleName()

    //Error dialog id
    private static final int RECOVERY_ERROR_DIALOG_ID = 1
    public static final String YOUTUBE_VIDEO_ID_KEY = 'youtube_video_id_key'
    private List<String> mVideoIds
    private YouTubePlayer mYouTubePlayer

    //Empty constructor, to be used by the system while creating the fragment when embedded in XML
    MovieMagicYoutubeFragment () {
        LogDisplay.callLog(LOG_TAG,'MovieMagicYoutubeFragment empty constructor is called',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
    }

    /**
     * Returns a new instance of MovieMagicYoutubeFragment Fragment
     *
     * @param videoIds The IDs of the YouTube videos to play
     */
    public static MovieMagicYoutubeFragment createMovieMagicYouTubeFragment(final List<String> videoIds) {
        LogDisplay.callLog(LOG_TAG,'createMovieMagicYouTubeFragment is called',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        MovieMagicYoutubeFragment movieMagicYouTubeFragment = new MovieMagicYoutubeFragment()
        final Bundle bundle = new Bundle()
        bundle.putStringArrayList(YOUTUBE_VIDEO_ID_KEY, videoIds as ArrayList<String>)
        movieMagicYouTubeFragment.setArguments(bundle)
        return movieMagicYouTubeFragment
    }

    @Override
    public void onCreate(final Bundle bundle) {
        LogDisplay.callLog(LOG_TAG,'onCreate is called',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        super.onCreate(bundle)

        final Bundle arguments = getArguments()

        if (bundle != null && bundle.containsKey(YOUTUBE_VIDEO_ID_KEY)) { // Restore case, so retrieve it from bundle
            LogDisplay.callLog(LOG_TAG,'onCreate: restore case..',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
            mVideoIds = bundle.getStringArrayList(YOUTUBE_VIDEO_ID_KEY)
        } else if (arguments != null && arguments.containsKey(YOUTUBE_VIDEO_ID_KEY)) { // First start
            LogDisplay.callLog(LOG_TAG,'onCreate: first initial case..',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
            mVideoIds = arguments.getStringArrayList(YOUTUBE_VIDEO_ID_KEY)
        }
    }

    @Override
    void onStart() {
        LogDisplay.callLog(LOG_TAG,"onStart is called. mVideoIds -> $mVideoIds",LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        super.onStart()

        if(mYouTubePlayer) {
            LogDisplay.callLog(LOG_TAG,"onStart: mYouTubePlayer is not null-> $mYouTubePlayer",LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
            // This is used when the fragment is started on the event of pressing back button on the last detailed activity
            // Releasing the resources ensures that the OnInitializedListener interface is invoked and videos get reloaded
            mYouTubePlayer.release()
        } else {
            LogDisplay.callLog(LOG_TAG,'onStart: mYouTubePlayer is null',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        }
        initialize(BuildConfig.YOUTUBE_API_KEY, this)
    }

    /**
     * Set the video ids and initialize the player
     * This can be used when including the Fragment in an XML layout
     * @param videoIds The IDs of the YouTube videos to play
     */
    public void setVideoId(final List<String> videoIds) {
        LogDisplay.callLog(LOG_TAG,'setVideoId is called',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        mVideoIds = videoIds
        initialize(BuildConfig.YOUTUBE_API_KEY, this)
    }

    @Override
    void onInitializationSuccess(
            final YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, final boolean restored) {
        LogDisplay.callLog(LOG_TAG,"onInitializationSuccess is called. mVideoIds -> $mVideoIds",LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        //This flag tells the player to switch to landscape when in fullscreen, it will also return to portrait
        //when leaving fullscreen
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION)

        //This flag controls the system UI such as the status and navigation bar, hiding and showing them
        //alongside the player UI
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI)

        //Set FullscreenListener
        youTubePlayer.setOnFullscreenListener(this)

        if (mVideoIds) {
            if (restored) {
//                Let Youtube handle it automatically
            } else {
                youTubePlayer.cueVideos(mVideoIds)
            }
        }
        mYouTubePlayer = youTubePlayer
    }

    @Override
    void onInitializationFailure(final YouTubePlayer.Provider provider, final YouTubeInitializationResult youTubeInitializationResult) {
        LogDisplay.callLog(LOG_TAG,"onInitializationFailure is called. mVideoIds -> $mVideoIds",LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        if (youTubeInitializationResult.isUserRecoverableError()) {
            LogDisplay.callLog(LOG_TAG,'onInitializationFailure:user recoverable',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
            youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_ERROR_DIALOG_ID).show()
        } else {
            //Handle the failure
            Toast.makeText(getActivity(), getString(R.string.youtube_initialization_error), Toast.LENGTH_LONG).show()
            LogDisplay.callLog(LOG_TAG,'onInitializationFailure:non-user recoverable',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        }
    }

    @Override
    void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser)
        LogDisplay.callLog(LOG_TAG,'setUserVisibleHint is called',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
    }

    @Override
    public void onSaveInstanceState(final Bundle bundle) {
        LogDisplay.callLog(LOG_TAG,'onSaveInstanceState is called',LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        if(mVideoIds) {
            bundle.putStringArrayList(YOUTUBE_VIDEO_ID_KEY, new ArrayList<String>(mVideoIds))
        }
        super.onSaveInstanceState(bundle)
    }

    @Override
    void onFullscreen(boolean isFullscreen) {
        LogDisplay.callLog(LOG_TAG,"onFullscreen is called.Boolean value ->> $isFullscreen",LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
    }

    @Override
    void onStop() {
        LogDisplay.callLog(LOG_TAG,"onStop is called. mVideoIds -> $mVideoIds",LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        super.onStop()
    }

    @Override
    void onDestroyView() {
        LogDisplay.callLog(LOG_TAG,"onDestroyView is called. mVideoIds -> $mVideoIds",LogDisplay.MOVIE_MAGIC_YOUTUBE_FRAGMENT_LOG_FLAG)
        super.onDestroyView()
    }
}