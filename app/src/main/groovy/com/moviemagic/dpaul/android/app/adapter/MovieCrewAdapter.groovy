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
class MovieCrewAdapter extends RecyclerView.Adapter<MovieCrewAdapter.MovieCrewAdapterViewHolder> {
    private static final String LOG_TAG = MovieCrewAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext
    private final TextView mCrewGridEmptyTextView
    private int mPrimaryDarkColor, mBodyTextColor
    private final MovieCrewAdapterOnClickHandler mMovieCrewAdapterOnClickHandler


    //Empty constructor
    public MovieCrewAdapter(){
        LogDisplay.callLog(LOG_TAG,'MovieCrewAdapter empty constructor is called',LogDisplay.MOVIE_CREW_ADAPTER_LOG_FLAG)
    }

    public MovieCrewAdapter(final Context ctx, final TextView emptyView, final MovieCrewAdapterOnClickHandler clickHandler){
        LogDisplay.callLog(LOG_TAG,'MovieCrewAdapter non-empty constructor is called',LogDisplay.MOVIE_CREW_ADAPTER_LOG_FLAG)
        mContext = ctx
        mCrewGridEmptyTextView = emptyView
        mMovieCrewAdapterOnClickHandler = clickHandler
    }

    public class MovieCrewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView movieCrewImageView
        public final TextView movieCrewJobName
        public final TextView movieCrewName

        public MovieCrewAdapterViewHolder(final View view) {
            super(view)
            movieCrewImageView = view.findViewById(R.id.single_movie_crew_grid_image) as ImageView
            movieCrewJobName = view.findViewById(R.id.single_movie_crew_grid_job_name) as TextView
            movieCrewName = view.findViewById(R.id.single_movie_crew_grid_crew_name) as TextView
            view.setOnClickListener(this)
        }

        @Override
        public void onClick(final View v) {
            LogDisplay.callLog(LOG_TAG,"onClick is called.LayoutPos=${getLayoutPosition()}.AdapterPos=${getAdapterPosition()}",LogDisplay.MOVIE_CREW_ADAPTER_LOG_FLAG)
            mCursor.moveToPosition(getAdapterPosition())
            final int personId = mCursor.getInt(DetailMovieFragment.COL_MOVIE_CREW_PERSON_ID)
            LogDisplay.callLog(LOG_TAG,"Person id is $personId",LogDisplay.MOVIE_CREW_ADAPTER_LOG_FLAG)
            mMovieCrewAdapterOnClickHandler.onClick(personId)
        }
    }
    @Override
    MovieCrewAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LogDisplay.callLog(LOG_TAG,'onCreateViewHolder is called',LogDisplay.MOVIE_CREW_ADAPTER_LOG_FLAG)
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_movie_crew_grid,parent,false)
        view.setFocusable(true)
        return new MovieCrewAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(final MovieCrewAdapterViewHolder holder, final int position) {
        //Move the cursor to correct position
        mCursor.moveToPosition(position)
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.MOVIE_CREW_ADAPTER_LOG_FLAG)
        final String profilePath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(DetailMovieFragment.COL_MOVIE_CREW_PROFILE_PATH)}"
        PicassoLoadImage.loadMoviePersonImage(mContext,profilePath,holder.movieCrewImageView)
        holder.movieCrewJobName.setText(mCursor.getString(DetailMovieFragment.COL_MOVIE_CREW_CREW_JOB))
        holder.movieCrewName.setText(mCursor.getString(DetailMovieFragment.COL_MOVIE_CREW_PERSON_NAME))
        //Apply color only it has got a value
        if(mPrimaryDarkColor && mBodyTextColor) {
            holder.movieCrewJobName.setBackgroundColor(mPrimaryDarkColor)
            holder.movieCrewJobName.setTextColor(mBodyTextColor)
            holder.movieCrewName.setBackgroundColor(mPrimaryDarkColor)
            holder.movieCrewName.setTextColor(mBodyTextColor)
        }
    }

    @Override
    int getItemCount() {
        if ( null == mCursor ) return 0
        return mCursor.getCount()
    }

    public void swapCursor(final Cursor newCursor) {
        mCursor = newCursor
        if (getItemCount() == 0) {
            mCrewGridEmptyTextView.setVisibility(TextView.VISIBLE)
        } else {
            mCrewGridEmptyTextView.setVisibility(TextView.INVISIBLE)
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
                mCrewGridEmptyTextView.setTextColor(mBodyTextColor)
            }
        } else {
            notifyDataSetChanged()
        }
    }

    /**
     * This is the interface which will be implemented by the host DetailMovieFragment
     */
    public interface MovieCrewAdapterOnClickHandler {
        public void onClick(int personId)
    }
}