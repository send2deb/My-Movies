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
import com.moviemagic.dpaul.android.app.PersonMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage
import groovy.transform.CompileStatic

@CompileStatic
class PersonCrewAdapter extends RecyclerView.Adapter<PersonCrewAdapter.PersonCrewAdapterViewHolder> {
    private static final String LOG_TAG = PersonCrewAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext
    private final TextView mPersonCrewGridEmptyTextView
    private int mPrimaryDarkColor, mBodyTextColor
    private final PersonCrewAdapterOnClickHandler mMoviePersonCrewAdapterOnClickHandler

    //Empty constructor
    public PersonCrewAdapter(){
        LogDisplay.callLog(LOG_TAG,'PersonCrewAdapter empty constructor is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
    }

    public PersonCrewAdapter(final Context ctx, final TextView emptyView, final PersonCrewAdapterOnClickHandler clickHandler){
        LogDisplay.callLog(LOG_TAG,'PersonCrewAdapter non-empty constructor is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        mContext = ctx
        mPersonCrewGridEmptyTextView = emptyView
        mMoviePersonCrewAdapterOnClickHandler = clickHandler
    }

    public class PersonCrewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView movieImageView
        public final TextView movieTextView, jobTextView

        public PersonCrewAdapterViewHolder(final View view) {
            super(view)
            movieImageView = view.findViewById(R.id.person_crew_grid_movie_image) as ImageView
            movieTextView = view.findViewById(R.id.person_crew_grid_movie_name) as TextView
            jobTextView = view.findViewById(R.id.person_crew_grid_job_name) as TextView
            view.setOnClickListener(this)
        }

        @Override
        public void onClick(final View v) {
            LogDisplay.callLog(LOG_TAG,"onClick is called.LayoutPos=${getLayoutPosition()}.AdapterPos=${getAdapterPosition()}",LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
            final int adapterPosition = getAdapterPosition()
            mCursor.moveToPosition(adapterPosition)
            final int movieId = mCursor.getInt(PersonMovieFragment.COL_PERSON_CREW_MOVIE_ID)
            mMoviePersonCrewAdapterOnClickHandler.onClick(movieId, this)
        }
    }

    @Override
    PersonCrewAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LogDisplay.callLog(LOG_TAG,'onCreateViewHolder is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_person_crew_grid,parent,false)
        view.setFocusable(true)
        return new PersonCrewAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(final PersonCrewAdapterViewHolder holder, final int position) {
        //Move the cursor to correct position
        mCursor.moveToPosition(position)
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        final String posterPath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(PersonMovieFragment.COL_PERSON_CREW_MOVIE_POSTER_PATH)}"
        PicassoLoadImage.loadMoviePosterImage(mContext,posterPath,holder.movieImageView)
        holder.jobTextView.setText(mCursor.getString(PersonMovieFragment.COL_PERSON_CREW_JOB_NAME))
        holder.movieTextView.setText(mCursor.getString(PersonMovieFragment.COL_PERSON_CREW_MOVIE_TITLE))
        //Apply color only it has got a value
        if(mPrimaryDarkColor && mBodyTextColor) {
            holder.movieTextView.setBackgroundColor(mPrimaryDarkColor)
            holder.jobTextView.setBackgroundColor(mPrimaryDarkColor)
            holder.jobTextView.setTextColor(mBodyTextColor)
            holder.movieTextView.setTextColor(mBodyTextColor)
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
            mPersonCrewGridEmptyTextView.setVisibility(TextView.VISIBLE)
        } else {
            mPersonCrewGridEmptyTextView.setVisibility(TextView.INVISIBLE)
            notifyDataSetChanged()
        }
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor(final int primaryDarkColor, final int bodyTextColor) {
        LogDisplay.callLog(LOG_TAG,'changeColor is called',LogDisplay.PERSON_CREW_ADAPTER_LOG_FLAG)
        mPrimaryDarkColor = primaryDarkColor
        mBodyTextColor = bodyTextColor
        if (getItemCount() == 0) {
            if(mContext.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                mPersonCrewGridEmptyTextView.setTextColor(mBodyTextColor)
            }
        } else {
            notifyDataSetChanged()
        }
    }

    /**
     * This is the interface which will be implemented by the host PersonMovieFragment
     */
    public interface PersonCrewAdapterOnClickHandler {
        public void onClick(int movieId, PersonCrewAdapterViewHolder viewHolder)
    }
}