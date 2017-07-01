/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.moviemagic.dpaul.android.app.R
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class DonateLinkAdapter extends RecyclerView.Adapter<DonateLinkAdapter.DonateLinkAdapterViewHolder> {
    private static final String LOG_TAG = DonateLinkAdapter.class.getSimpleName()

    final Context mContext
    final String[] mLinkHeader
    final String[] mLinkAddress
    final DonateLinkAdapterOnClickHandler mDonateLinkAdapterOnClickHandler

    //Empty constructor
    public DonateLinkAdapter() {
        LogDisplay.callLog(LOG_TAG, 'DonateLinkAdapter empty constructor is called', LogDisplay.DONATE_ADAPTER_LOG_FLAG)
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DonateLinkAdapter(
            final Context ctx, final String[] linkHeader, final String[] linkAddress, final DonateLinkAdapterOnClickHandler clickHandler) {
        LogDisplay.callLog(LOG_TAG, 'DonateLinkAdapter non-empty constructor is called', LogDisplay.DONATE_ADAPTER_LOG_FLAG)
        mContext = ctx
        mLinkHeader = linkHeader
        mLinkAddress = linkAddress
        mDonateLinkAdapterOnClickHandler = clickHandler
    }

    public class DonateLinkAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView donateHeader
        private final TextView donateLink
        private final Button donateGoButton

        public DonateLinkAdapterViewHolder(final View view) {
            super(view)
            donateHeader = view.findViewById(R.id.single_donate_item_header) as TextView
            donateLink = view.findViewById(R.id.single_donate_item_link_address) as TextView
            donateGoButton = view.findViewById(R.id.single_donate_item_donate_button) as Button
            donateGoButton.setOnClickListener(this)
        }

        @Override
        void onClick(final View view) {
            LogDisplay.callLog(LOG_TAG, 'Donate Go button is clicked', LogDisplay.DONATE_ADAPTER_LOG_FLAG)
            mDonateLinkAdapterOnClickHandler.onClick(getAdapterPosition())
        }
    }

    @Override
    DonateLinkAdapter.DonateLinkAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LogDisplay.callLog(LOG_TAG, 'onCreateViewHolder is called', LogDisplay.DONATE_ADAPTER_LOG_FLAG)
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_donate_item, parent, false)
        view.setFocusable(true)
        return new DonateLinkAdapterViewHolder(view)
    }

    @Override
    void onBindViewHolder(final DonateLinkAdapter.DonateLinkAdapterViewHolder holder, final int position) {
        holder.donateHeader.setText(mLinkHeader[position])
        holder.donateLink.setText(mLinkAddress[position])
    }

    @Override
    int getItemCount() {
        LogDisplay.callLog(LOG_TAG, "getItemCount is called. Size -> ${mLinkHeader.size()}", LogDisplay.DONATE_ADAPTER_LOG_FLAG)
        return mLinkHeader.size()
    }

    /**
     * This is the interface which will be implemented by the host DonateActivity
     */
    public interface DonateLinkAdapterOnClickHandler {
        public void onClick(int position)
    }
}