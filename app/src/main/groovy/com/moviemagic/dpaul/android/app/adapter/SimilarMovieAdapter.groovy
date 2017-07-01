/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.content.res.Configuration
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.DetailMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage
import groovy.transform.CompileStatic

@CompileStatic
class SimilarMovieAdapter extends RecyclerView.Adapter<SimilarMovieAdapter.SimilarMovieAdapterViewHolder> {
    private static final String LOG_TAG = SimilarMovieAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext
    private final TextView mSimilarMovieGridEmptyTextView
    private int mPrimaryDarkColor, mBodyTextColor
//    private FragmentActivity mFragmentActivity
    private final SimilarMovieAdapterOnClickHandler mSimilarMovieAdapterOnClickHandler


    //Empty constructor
    public SimilarMovieAdapter(){
        LogDisplay.callLog(LOG_TAG,'SimilarMovieAdapter empty constructor is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_LOG_FLAG)
    }
    public SimilarMovieAdapter(final Context ctx, final TextView emptyView, final SimilarMovieAdapterOnClickHandler clickHandler){
        LogDisplay.callLog(LOG_TAG,'SimilarMovieAdapter non-empty constructor is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_LOG_FLAG)
        mContext = ctx
        mSimilarMovieGridEmptyTextView = emptyView
        mSimilarMovieAdapterOnClickHandler = clickHandler
    }

    public class SimilarMovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView similarMovieImageView
        public final TextView similarMovieTextView

        public SimilarMovieAdapterViewHolder(final View view) {
            super(view)
            similarMovieImageView = view.findViewById(R.id.single_similar_movie_grid_image) as ImageView
            similarMovieTextView = view.findViewById(R.id.single_similar_movie_movie_name) as TextView
            view.setOnClickListener(this)
        }

        @Override
        public void onClick(final View v) {
            LogDisplay.callLog(LOG_TAG,"onClick is called.LayoutPos=${getLayoutPosition()}.AdapterPos=${getAdapterPosition()}",LogDisplay.SIMILAR_MOVIE_ADAPTER_LOG_FLAG)
            mCursor.moveToPosition(getAdapterPosition())
            final int movieId = mCursor.getInt(DetailMovieFragment.COL_SIMILAR_MOVIE_MOVIE_ID)
            LogDisplay.callLog(LOG_TAG,"Movie id is $movieId",LogDisplay.SIMILAR_MOVIE_ADAPTER_LOG_FLAG)
            mSimilarMovieAdapterOnClickHandler.onClick(movieId)
        }
    }

    @Override
    SimilarMovieAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LogDisplay.callLog(LOG_TAG,'onCreateViewHolder is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_LOG_FLAG)
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_similar_movie_grid,parent,false)
        view.setFocusable(true)
        return new SimilarMovieAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(final SimilarMovieAdapterViewHolder holder, final int position) {
        // move the cursor to correct position
        mCursor.moveToPosition(position)
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.SIMILAR_MOVIE_ADAPTER_LOG_FLAG)
        final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(DetailMovieFragment.COL_SIMILAR_MOVIE_POSTER_PATH)}"
        PicassoLoadImage.loadMoviePosterImage(mContext,posterPath,holder.similarMovieImageView)
        holder.similarMovieTextView.setText(mCursor.getString(DetailMovieFragment.COL_SIMILAR_MOVIE_TITLE))
        //Apply color only it has got a value
        if(mPrimaryDarkColor && mBodyTextColor) {
            holder.similarMovieTextView.setBackgroundColor(mPrimaryDarkColor)
            holder.similarMovieTextView.setTextColor(mBodyTextColor)
        }
    }

    @Override
    int getItemCount() {
        if (null == mCursor) {
            return 0
        }
        return mCursor.getCount()
    }

    public void swapCursor(final Cursor newCursor) {
        mCursor = newCursor
        if (getItemCount() == 0) {
            mSimilarMovieGridEmptyTextView.setVisibility(TextView.VISIBLE)
        } else {
            mSimilarMovieGridEmptyTextView.setVisibility(TextView.INVISIBLE)
            notifyDataSetChanged()
        }
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor(final int primaryDarkColor, final int bodyTextColor) {
        mPrimaryDarkColor = primaryDarkColor
        mBodyTextColor = bodyTextColor
        if (getItemCount() == 0) {
            if(mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                mSimilarMovieGridEmptyTextView.setTextColor(mBodyTextColor)
            }
        } else {
            notifyDataSetChanged()
        }
    }

    /**
     * This is the interface which will be implemented by the host ImageViewerActivity
     */
    public interface SimilarMovieAdapterOnClickHandler {
        public void onClick(int movieId)
    }
}