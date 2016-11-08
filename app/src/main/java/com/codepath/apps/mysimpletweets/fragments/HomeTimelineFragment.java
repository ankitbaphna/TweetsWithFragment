package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.application.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.EndlessRecyclerViewScrollListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.codepath.apps.mysimpletweets.R.id.rvList;

/**
 * Created by baphna on 11/3/2016.
 */

public class HomeTimelineFragment extends TweetsListFragment {
    private TwitterClient client;


    private EndlessRecyclerViewScrollListener scrollListener;

    @BindView(rvList)
    RecyclerView rvTweets;
    private StaggeredGridLayoutManager staggeredLayoutManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeline(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    protected void populateTimeline(final boolean getLatestTweets) {
        long useThisMaxId = maxId;
        if(getLatestTweets == true){
            useThisMaxId = -1;
        }
        client.getHomeTimeline(useThisMaxId, mResponseHandler);
    }

    public static Fragment newInstance(int i, String s) {
        HomeTimelineFragment homeTimelineFragment = new HomeTimelineFragment();
        return homeTimelineFragment;
    }
}
