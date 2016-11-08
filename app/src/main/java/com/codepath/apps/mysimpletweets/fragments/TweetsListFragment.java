package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.utils.Constants;
import com.codepath.apps.mysimpletweets.utils.DividerItemDecoration;
import com.codepath.apps.mysimpletweets.utils.EndlessRecyclerViewScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.mysimpletweets.R.id.rvList;

public abstract class TweetsListFragment extends Fragment  implements NewTweetFragment.NewTweetPostedInterface{

    private ArrayList<Tweet> tweets = new ArrayList<>();
    private TweetsArrayAdapter tweetsArrayAdapter;

    @BindView(rvList)
    RecyclerView rvTweets;
    private StaggeredGridLayoutManager staggeredLayoutManager;
    protected long maxId = -1;
    private EndlessRecyclerViewScrollListener scrollListener;

    protected abstract void populateTimeline(boolean getLatestTweets);

    protected JsonHttpResponseHandler mResponseHandler = new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            super.onSuccess(statusCode, headers, response);
            ArrayList<Tweet> newTweets = (ArrayList<Tweet>) Tweet.fromJsonArray(response);
                /*List<Tweet> fromDb = SQLite.select()
                        .from(Tweet.class)
                        .orderBy(id, false)
                        .queryList();*/

            addAll(newTweets);

            maxId = newTweets.get(newTweets.size()-1).getId();

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            Log.e(Constants.TAG, "Twitter failure " + responseString);
            //Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
            //swipeContainer.setRefreshing(false);
        }
    };

    public TweetsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        ButterKnife.bind(this, view);

        tweetsArrayAdapter = new TweetsArrayAdapter(getActivity(), tweets);
        staggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        scrollListener = new EndlessRecyclerViewScrollListener(staggeredLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                populateTimeline(false);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);


        rvTweets.setLayoutManager(staggeredLayoutManager);
        /*tweets.addAll(SQLite.select()
                .from(Tweet.class)
                .queryList());*/
        rvTweets.setAdapter(tweetsArrayAdapter);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        rvTweets.addItemDecoration(itemDecoration);

        return view;
    }

    @Override
    public void onNewTweetPosted(Tweet newTweet) {
        Log.d(Constants.TAG, "New tweeet added ");
        tweets.add(0, newTweet);
        tweetsArrayAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }

    public void addAll(List<Tweet> tweetsFromDb) {
        //tweets.clear();
        tweets.addAll(tweetsFromDb);
        tweetsArrayAdapter.notifyDataSetChanged();

    }
}
