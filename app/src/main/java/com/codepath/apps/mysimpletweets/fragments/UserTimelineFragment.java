package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.application.TwitterApplication;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.mysimpletweets.R.id.rvList;

/**
 * Created by baphna on 11/3/2016.
 */

public class UserTimelineFragment extends TweetsListFragment {

    private TwitterClient client;
    private long maxId = -1;

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
        staggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                populateTimeline(false);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        return view;
    }

    @Override
    protected void populateTimeline(final boolean getLatestTweets) {
        long useThisMaxId = maxId;
        if(getLatestTweets == true){
            useThisMaxId = -1;
        }
        String screenName = getArguments().getString("screen_name");
        client.getUserTimeLine(screenName , useThisMaxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                ArrayList<Tweet> newTweets = (ArrayList<Tweet>) Tweet.fromJsonArray(response);
                /*List<Tweet> fromDb = SQLite.select()
                        .from(Tweet.class)
                        .orderBy(id, false)
                        .queryList();*/

                addAll(newTweets);

                if(getLatestTweets == false){
                    maxId = newTweets.get(newTweets.size()-1).getId();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(Constants.TAG, "Twitter failure " + responseString);
                //Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                //swipeContainer.setRefreshing(false);
            }
        });
    }

    public static Fragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }
}
