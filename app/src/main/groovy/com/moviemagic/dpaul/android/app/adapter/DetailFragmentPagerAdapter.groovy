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
import android.widget.ImageView
import android.widget.RelativeLayout
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.GlobalStaticVariables
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay
import com.moviemagic.dpaul.android.app.backgroundmodules.PicassoLoadImage;
import groovy.transform.CompileStatic

@CompileStatic
class DetailFragmentPagerAdapter extends PagerAdapter {
    private static final String LOG_TAG = DetailFragmentPagerAdapter.class.getSimpleName()
    private final Context mContext
    private final String[] mBackdropimageFilePaths
    private DetailFragmentPagerAdapterOnClickHandler mDetailFragmentPagerAdapterOnClickHandler
    private LayoutInflater mLayoutInflater

    public DetailFragmentPagerAdapter() {
        LogDisplay.callLog(LOG_TAG,'DetailFragmentPagerAdapter empty constructor is called',LogDisplay.DETAIL_FRAGMENT_PAGER_ADAPTER_LOG_FLAG)
    }

    public DetailFragmentPagerAdapter(final Context context, final String[] filePaths,
                                      final DetailFragmentPagerAdapterOnClickHandler clickHandler) {
        LogDisplay.callLog(LOG_TAG,'DetailFragmentPagerAdapter non-empty constructor is called',LogDisplay.DETAIL_FRAGMENT_PAGER_ADAPTER_LOG_FLAG)
        mContext = context
        mBackdropimageFilePaths = filePaths
        mDetailFragmentPagerAdapterOnClickHandler = clickHandler
    }

    @Override
    int getCount() {
        return mBackdropimageFilePaths.size()
    }

    @Override
    boolean isViewFromObject(final View view, final Object object) {
        return view == ((RelativeLayout) object)
    }

    @Override
    Object instantiateItem(final ViewGroup container, final int position) {
        LogDisplay.callLog(LOG_TAG,"instantiateItem is called:$position",LogDisplay.DETAIL_FRAGMENT_PAGER_ADAPTER_LOG_FLAG)
        mLayoutInflater = LayoutInflater.from(mContext)
        final View view = mLayoutInflater.inflate(R.layout.single_backdrop_viewpager_item, container, false)
        final ImageView imageView = view.findViewById(R.id.backdrop_viewpager_image) as ImageView
        final String imagePath = "$GlobalStaticVariables.TMDB_IMAGE_BASE_URL/$GlobalStaticVariables.TMDB_IMAGE_SIZE_W500" +
                "${mBackdropimageFilePaths[position]}"
        LogDisplay.callLog(LOG_TAG,"instantiateItem:imagePath-> $imagePath",LogDisplay.DETAIL_FRAGMENT_PAGER_ADAPTER_LOG_FLAG)
        PicassoLoadImage.loadDetailFragmentPagerAdapterImage(mContext,imagePath,imageView)

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            void onClick(final View v) {
                LogDisplay.callLog(LOG_TAG,"view pager adapter item is clicked:$position",LogDisplay.DETAIL_FRAGMENT_PAGER_ADAPTER_LOG_FLAG)
                mDetailFragmentPagerAdapterOnClickHandler.onClick(position)
            }
        })

        ((ViewPager) container).addView(view)
        return view
    }

    @Override
    void destroyItem(final ViewGroup container, final int position, final Object object) {
        LogDisplay.callLog(LOG_TAG,'destroyItem is called',LogDisplay.DETAIL_FRAGMENT_PAGER_ADAPTER_LOG_FLAG)
        ((ViewPager) container).removeView((RelativeLayout) object)
    }

    /**
     * This is the interface which will be implemented by the host of this adapter
     */
    public interface DetailFragmentPagerAdapterOnClickHandler {
        public void onClick(int position)
    }
}