/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage
import groovy.transform.CompileStatic

@CompileStatic
class ImagePagerAdapter extends PagerAdapter {
    private static final String LOG_TAG = ImagePagerAdapter.class.getSimpleName()
    private final Context mContext
    private final String mTitle
    private final String[] mImageFilePaths
    private boolean visibilityFlag = true
    private final ImagePagerAdapterOnClickHandler mImagePagerAdapterOnClickHandler
    private LayoutInflater mLayoutInflater
    private final boolean mBackdropImageFlag
    private int mPosition
    public static final String PAGER_CURRENT_IMAGE_TAG = 'pager_current_image_tag'

    public ImagePagerAdapter() {
        LogDisplay.callLog(LOG_TAG,'ImagePagerAdapter empty constructor is called',LogDisplay.IMAGE_PAGER_ADAPTER_LOG_FLAG)
    }

    public ImagePagerAdapter(final Context context, final String title, final String[] imageFilePaths,
                             final ImagePagerAdapterOnClickHandler clickHandler, final boolean backdropImageFlag) {
        LogDisplay.callLog(LOG_TAG,'ImagePagerAdapter non-empty constructor is called',LogDisplay.IMAGE_PAGER_ADAPTER_LOG_FLAG)
        mContext = context
        mTitle = title
        mImageFilePaths = imageFilePaths
        mImagePagerAdapterOnClickHandler = clickHandler
        mBackdropImageFlag = backdropImageFlag
    }

    @Override
    int getCount() {
        return mImageFilePaths.size()
    }

    @Override
    boolean isViewFromObject(final View view, final Object object) {
        return view == ((FrameLayout) object)
    }

    @Override
    Object instantiateItem(final ViewGroup container, final int position) {
        LogDisplay.callLog(LOG_TAG,"instantiateItem is called:$position",LogDisplay.IMAGE_VIEWER_ACTIVITY_LOG_FLAG)
        mPosition = position
        mLayoutInflater = LayoutInflater.from(mContext)
        final View view = mLayoutInflater.inflate(R.layout.single_image_viewer_item, container, false)
        final ImageView imageView = view.findViewById(R.id.image_viewer_image) as ImageView
        // Set tag which will be used to retrieve this image while saving
        imageView.setTag(PAGER_CURRENT_IMAGE_TAG+position)
        if(mBackdropImageFlag) {
            final LayoutParams layoutParams = imageView.getLayoutParams()
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            imageView.setLayoutParams(layoutParams)
        }
        final TextView footerTextView = view.findViewById(R.id.image_viewer_pager_footer) as TextView
        final String imagePath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
                "${mImageFilePaths[position]}"
        LogDisplay.callLog(LOG_TAG,"instantiateItem:imagePath-> $imagePath",LogDisplay.IMAGE_PAGER_ADAPTER_LOG_FLAG)
        PicassoLoadImage.loadViewPagerImage(mContext,imagePath,imageView)
        final int totCount = mImageFilePaths.size()
        footerTextView.setText(String.format(mContext.getString(R.string.image_view_footer_value),(position + 1),totCount))

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG,"view pager adapter item is clicked:$position",LogDisplay.IMAGE_VIEWER_ACTIVITY_LOG_FLAG)
                if(visibilityFlag) {
                    visibilityFlag = false
                    notifyDataSetChanged()
                } else {
                    visibilityFlag = true
                    notifyDataSetChanged()
                }
                mImagePagerAdapterOnClickHandler.onClick(position)
            }
        })

        if(visibilityFlag) {
            footerTextView.setVisibility(TextView.VISIBLE)
            final Animation animIn = AnimationUtils.loadAnimation(mContext,R.anim.abc_slide_in_bottom)
            animIn.setDuration(80)
            footerTextView.startAnimation(animIn)
        } else {
            final Animation animOut = AnimationUtils.loadAnimation(mContext,R.anim.abc_slide_out_bottom)
            animOut.setDuration(100)
            animOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                void onAnimationStart(final Animation animation) {}
                @Override
                void onAnimationEnd(final Animation animation) {
                    footerTextView.setVisibility(TextView.GONE)
                }
                @Override
                void onAnimationRepeat(final Animation animation) {}
            })
            footerTextView.startAnimation(animOut)
        }

        ((ViewPager) container).addView(view)
        return view
    }

    @Override
    void destroyItem(final ViewGroup container, final int position, final Object object) {
        LogDisplay.callLog(LOG_TAG,'destroyItem is called',LogDisplay.IMAGE_PAGER_ADAPTER_LOG_FLAG)
        ((ViewPager) container).removeView((FrameLayout) object)
    }

    @Override
    int getItemPosition(final Object object) {
        LogDisplay.callLog(LOG_TAG,'getItemPosition is called',LogDisplay.IMAGE_PAGER_ADAPTER_LOG_FLAG)
        return POSITION_NONE
    }

    /**
     * This is the interface which will be implemented by the host ImageViewerActivity
     */
    public interface ImagePagerAdapterOnClickHandler {
        public void onClick(int position)
    }
}