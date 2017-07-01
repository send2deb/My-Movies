/*
 * Copyright (c) 2017 Debashis Paul. All Rights Reserved.
 */

package com.moviemagic.dpaul.android.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.DividerItemDecoration
import com.moviemagic.dpaul.android.app.adapter.DonateLinkAdapter
import com.moviemagic.dpaul.android.app.backgroundmodules.LogDisplay;
import groovy.transform.CompileStatic

@CompileStatic
class DonateActivity extends AppCompatActivity {
    private static final String LOG_TAG = DonateActivity.class.getSimpleName()

    private RecyclerView mRecyclerView
    private LinearLayoutManager mLayoutManager
    private DonateLinkAdapter mDonateLinkAdapter

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        LogDisplay.callLog(LOG_TAG, 'onCreate is called', LogDisplay.DONATE_ACTIVITY_LOG_FLAG)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)
        mRecyclerView = findViewById(R.id.donate_link_recycler_view) as RecyclerView

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this)
        mLayoutManager.setAutoMeasureEnabled(true)
        mRecyclerView.setLayoutManager(mLayoutManager)

        // Set item decorator
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation())
        mRecyclerView.addItemDecoration(dividerItemDecoration)

        final String[] donateHeader = getResources().getStringArray(R.array.donate_header)
        final String[] donateLinkAddress = getResources().getStringArray(R.array.donate_link_address)
        // specify an adapter (see also next example)
        mDonateLinkAdapter = new DonateLinkAdapter(this, donateHeader, donateLinkAddress,
                new DonateLinkAdapter.DonateLinkAdapterOnClickHandler(){
                    @Override
                    void onClick(final int position) {
                        openWebPage(position, donateLinkAddress)
                    }
                })
        mRecyclerView.setAdapter(mDonateLinkAdapter)
    }

    /**
     * Open the web page of the donate page in browser
     * @param position Position of the clicked item
     * @param linkAddress Arraylist of web addresses
     */
    protected void openWebPage(final int position, final String[] linkAddress) {
        final String url = linkAddress[position]
        final Intent intent = new Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent)
        } else {
            LogDisplay.callLog(LOG_TAG,'No browser client installed in this device',LogDisplay.DONATE_ACTIVITY_LOG_FLAG)
        }
    }

    @Override
    void onBackPressed() {
        LogDisplay.callLog(LOG_TAG, 'onBackPressed is called', LogDisplay.DONATE_ACTIVITY_LOG_FLAG)
        super.onBackPressed()
        //Start the animation
        overridePendingTransition(0, R.anim.slide_bottom_out_animation)
    }
}