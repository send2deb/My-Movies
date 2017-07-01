/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.GridMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage;
import groovy.transform.CompileStatic

@CompileStatic
class MovieGridRecyclerAdapter extends RecyclerView.Adapter<MovieGridRecyclerAdapter.MovieGridRecyclerAdapterViewHolder>{
    private static final String LOG_TAG = MovieGridRecyclerAdapter.class.getSimpleName()
    private final Context mContext
    private Cursor mCursor
    private final MovieGridRecyclerAdapterOnClickHandler mMovieGridRecyclerAdapterOnClickHandler
    private int mPrimaryDarkColor, mBodyTextColor
    //This flag is set as true by CollectionMovieFragment in order to apply color
    //And the same is set as false by MovieMagicMainActivity in order to use default color
    public static boolean collectionGridFlag = false

    //Empty constructor
    public MovieGridRecyclerAdapter() {
        LogDisplay.callLog(LOG_TAG, 'MovieGridRecyclerAdapter empty constructor is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
    }

    public MovieGridRecyclerAdapter(final Context ctx, final MovieGridRecyclerAdapterOnClickHandler clickHandler) {
        LogDisplay.callLog(LOG_TAG, 'MovieGridRecyclerAdapter non-empty constructor is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        mContext = ctx
        mMovieGridRecyclerAdapterOnClickHandler = clickHandler
    }

    public class MovieGridRecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ImageView movieImageView
        private final TextView movieNameView

        public MovieGridRecyclerAdapterViewHolder(final View view) {
            super(view)
            movieImageView = view.findViewById(R.id.grid_image_view) as ImageView
            movieNameView = view.findViewById(R.id.grid_text_view) as TextView
            view.setOnClickListener(this)
        }
        @Override
        void onClick(final View v) {
            final int adapterPosition = getAdapterPosition()
            mCursor.moveToPosition(adapterPosition)
            final int movieId = mCursor.getInt(GridMovieFragment.COL_MOVIE_ID)
            mMovieGridRecyclerAdapterOnClickHandler.onClick(movieId)
        }
    }

    @Override
    MovieGridRecyclerAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LogDisplay.callLog(LOG_TAG, 'onCreateViewHolder is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_grid_movie_item, parent, false)
        view.setFocusable(true)
        return new MovieGridRecyclerAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(final MovieGridRecyclerAdapterViewHolder holder, final int position) {
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        // move the cursor to correct position
        mCursor.moveToPosition(position)
        final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(GridMovieFragment.COL_MOVIE_POSTER)}"
        PicassoLoadImage.loadMoviePosterImage(mContext,posterPath,holder.movieImageView)
        holder.movieNameView.setText(mCursor.getString(GridMovieFragment.COL_MOVIE_TITLE))
        //Apply color only it has got a value
        if(mPrimaryDarkColor && mBodyTextColor) {
            holder.movieNameView.setBackgroundColor(mPrimaryDarkColor)
            holder.movieNameView.setTextColor(mBodyTextColor)
        }
    }

    @Override
    int getItemCount() {
        if (null == mCursor) {
            LogDisplay.callLog(LOG_TAG, "Cursor item count = 0", LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
            return 0
        }
        return mCursor.getCount()
    }

    public void swapCursor(final Cursor newCursor) {
        LogDisplay.callLog(LOG_TAG, 'swapCursor is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        mCursor = newCursor
        notifyDataSetChanged()
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor(final int primaryDarkColor, final int bodyTextColor) {
        LogDisplay.callLog(LOG_TAG, 'changeColor is called', LogDisplay.GRID_RECYCLER_ADAPTER_LOG_FLAG)
        mPrimaryDarkColor = primaryDarkColor
        mBodyTextColor = bodyTextColor
        notifyDataSetChanged()
    }
    /**
     * This is the interface which will be implemented by the host GridMovieFragment
     */
    public interface MovieGridRecyclerAdapterOnClickHandler {
        public void onClick(int movieId)
    }
}