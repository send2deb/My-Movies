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
import com.moviemagic.dpaul.android.app.PersonMovieFragment
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage;
import groovy.transform.CompileStatic

@CompileStatic
class PersonImageAdapter extends RecyclerView.Adapter<PersonImageAdapter.PersonImageAdapterViewHolder> {
    private static final String LOG_TAG = PersonImageAdapter.class.getSimpleName()

    private Cursor mCursor
    private final Context mContext
    private final TextView mPersonImageGridEmptyTextView
    private int mPrimaryDarkColor, mBodyTextColor
    private final PersonImageAdapterOnClickHandler mMoviePersonImageAdapterOnClickHandler

    //Empty constructor
    public PersonImageAdapter(){
        LogDisplay.callLog(LOG_TAG,'PersonImageAdapter empty constructor is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
    }

    public PersonImageAdapter(final Context ctx, final TextView emptyView, final PersonImageAdapterOnClickHandler clickHandler){
        LogDisplay.callLog(LOG_TAG,'PersonImageAdapter non-empty constructor is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        mContext = ctx
        mPersonImageGridEmptyTextView = emptyView
        mMoviePersonImageAdapterOnClickHandler = clickHandler
    }

    public class PersonImageAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView personImagesImageView

        public PersonImageAdapterViewHolder(final View view) {
            super(view)
            personImagesImageView = view.findViewById(R.id.person_image_grid_movie_image) as ImageView
            view.setOnClickListener(this)
        }

        @Override
        public void onClick(final View v) {
            LogDisplay.callLog(LOG_TAG,"onClick is called.LayoutPos=${getLayoutPosition()}.AdapterPos=${getAdapterPosition()}",LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
            final String[] imagePath = new String[mCursor.getCount()]
            LogDisplay.callLog(LOG_TAG,"onClick:Cursor count ${mCursor.getCount()}",LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
            mCursor.moveToFirst()
            for(final i in 0..mCursor.getCount()-1) {
                imagePath[i] = mCursor.getString(PersonMovieFragment.COL_PERSON_IMAGE_FILE_PATH)
                mCursor.moveToNext()
            }
            LogDisplay.callLog(LOG_TAG,"onClick:imagePath array count ${imagePath.size()}",LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
            final int adapterPosition = getAdapterPosition()
            mMoviePersonImageAdapterOnClickHandler.onClick(adapterPosition, imagePath, this)
        }
    }

    @Override
    PersonImageAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LogDisplay.callLog(LOG_TAG,'onCreateViewHolder is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_person_image_grid,parent,false)
        view.setFocusable(true)
        return new PersonImageAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(final PersonImageAdapterViewHolder holder, final int position) {
        //Move the cursor to correct position
        mCursor.moveToPosition(position)
        LogDisplay.callLog(LOG_TAG,'onBindViewHolder is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        final String imagePath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W185" +
                "${mCursor.getString(PersonMovieFragment.COL_PERSON_IMAGE_FILE_PATH)}"
        PicassoLoadImage.loadMoviePersonImage(mContext,imagePath,holder.personImagesImageView)
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
            mPersonImageGridEmptyTextView.setVisibility(TextView.VISIBLE)
        } else {
            mPersonImageGridEmptyTextView.setVisibility(TextView.INVISIBLE)
            notifyDataSetChanged()
        }
    }

    //Since the color is decided once the poster is downloaded by Picasso
    //but by then adapter might got loaded with data. Hence call notifyDataSetChanged
    //so that it get's recreated with correct color
    public void changeColor(final int primaryDarkColor, final int bodyTextColor) {
        LogDisplay.callLog(LOG_TAG,'changeColor is called',LogDisplay.PERSON_IMAGE_ADAPTER_LOG_FLAG)
        mPrimaryDarkColor = primaryDarkColor
        mBodyTextColor = bodyTextColor
        if (getItemCount() == 0) {
            mPersonImageGridEmptyTextView.setTextColor(mBodyTextColor)
        } else {
            notifyDataSetChanged()
        }
    }

    /**
     * This is the interface which will be implemented by the host PersonMovieFragment
     */
    public interface PersonImageAdapterOnClickHandler {
        public void onClick(int adapterPosition, String[] imageFilePath, PersonImageAdapterViewHolder viewHolder)
    }
}