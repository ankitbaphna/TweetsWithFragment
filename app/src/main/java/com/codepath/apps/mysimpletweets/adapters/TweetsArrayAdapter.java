package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.activities.TweetDetailActivity;
import com.codepath.apps.mysimpletweets.application.TwitterApplication;
import com.codepath.apps.mysimpletweets.fragments.NewTweetFragment;
import com.codepath.apps.mysimpletweets.models.ExtendedEntities;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.PatternEditableBuilder;
import com.codepath.apps.mysimpletweets.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by baphna on 10/27/2016.
 */

public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder> {

    private Context mContext;
    private List<Tweet> tweets;


    public TweetsArrayAdapter(Context mContext, List<Tweet> tweets) {
        this.mContext = mContext;
        this.tweets = tweets;
    }

    @Override
    public TweetsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Tweet tweet = tweets.get(position);
        holder.tvUserHandle.setText("@"+tweet.getUser().getProfileName());
        holder.tvTweetBody.setText(tweet.getBody());
        holder.btnRetweet.setText(Integer.toString(tweet.getRetweet_count()));
        holder.btnFav.setText(Integer.toString(tweet.getFavorite_count()));
        holder.tvUserName.setText(tweet.getUser().getName());
        if(tweet.getCreatedAt() != null) {
            holder.tvTweetTime.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
        } else {
            holder.tvTweetTime.setText("0s");
        }

        if(!tweet.isRetweeted()){
            holder.bntRetweetBy.setVisibility(View.GONE);
        } else{
            //TODO
            holder.btnRetweet.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.drawable.ic_retweet_blue_24dp), null, null, null);
        }

        if(tweet.isFavorited()){
            holder.btnFav.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.drawable.ic_star_golden_24dp), null, null, null);
        }


        String profileImageUrl  = tweet.getUser().getProfileImageUrl();
        ExtendedEntities entities = tweet.getExtendedEntities();
        if(entities != null) {
            String mediaUrl = entities.getMediaUrl();
            Glide.with(mContext).load(mediaUrl).into(holder.ivTweetImage);
        } else {
            holder.ivTweetImage.setVisibility(View.GONE);
        }
        //TODO: round it
        Glide.with(mContext).load(profileImageUrl)
                .into(holder.ivUserImage);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(mContext, "Clicked username: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).into(holder.tvTweetBody);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindDrawable(R.drawable.ic_star_golden_24dp)
        Drawable icGoldStar;
        @BindDrawable(R.drawable.ic_star_golden_24dp)
        Drawable ic_star_golden;
        @BindDrawable(R.drawable.ic_star_border_grey_24dp)
        Drawable ic_star_grey;
        @BindDrawable(R.drawable.ic_retweet_grey_24dp)
        Drawable ic_retweet_grey;
        @BindDrawable(R.drawable.ic_retweet_blue_24dp)
        Drawable ic_retweet_blue;

        @BindView(R.id.rlTweetContainer)
        RelativeLayout rlTweetContainer;
        @BindView(R.id.tvTweetBody)
        TextView tvTweetBody;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvUserHandle)
        TextView tvUserHandle;
        @BindView(R.id.tvTweetTime)
        TextView tvTweetTime;

        @BindView(R.id.ivUserImage)
        ImageView ivUserImage;
        @BindView(R.id.ivTweetImage)
        ImageView ivTweetImage;

        @BindView(R.id.btnFav)
        Button btnFav;
        @BindView(R.id.btnReply)
        Button btnReply;
        @BindView(R.id.btnRetweet)
        Button btnRetweet;
        @BindView(R.id.bntRetweetBy)
        Button bntRetweetBy;


        //TODO: notify data change instead of changing UI directly

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rlTweetContainer)
        public void tweetClick(View view){
            Tweet  tweet = tweets.get(getLayoutPosition());
            Intent intent = new Intent(mContext, TweetDetailActivity.class);
            intent.putExtra("Tweet", Parcels.wrap(tweet));
            mContext.startActivity(intent);
        }

        @OnClick(R.id.ivUserImage)
        public void userImageClicked(View view){
            Intent profileActivity = new Intent(mContext, ProfileActivity.class);
            String screenName = tweets.get(getLayoutPosition()).getUser().getProfileName();
            profileActivity.putExtra("screenName", screenName);
            profileActivity.putExtra("user", Parcels.wrap(tweets.get(getLayoutPosition()).getUser()));
            mContext.startActivity(profileActivity);
        }

        @OnClick(R.id.btnFav)
        public void favClicked(final View view){
            if(Utils.isNetworkAvailable(mContext.getApplicationContext())) {
                TwitterClient client = TwitterApplication.getRestClient();
                Tweet  tweet = tweets.get(getLayoutPosition());
                final int count = tweets.get(getLayoutPosition()).getFavorite_count();
                long id = tweet.getId();
                if (tweet.isFavorited()) {
                    //Already favorite..make un favorite
                    client.postMakeUnFavorite(id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            btnFav.setCompoundDrawablesWithIntrinsicBounds(ic_star_grey, null, null, null);
                            //btnFav.setText(Integer.toString(count-1));
                            tweets.get(getLayoutPosition()).setFavorited(false);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Snackbar.make(view, responseString , Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    client.postMakeFavorite(id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            btnFav.setCompoundDrawablesWithIntrinsicBounds(ic_star_golden, null, null, null);
                            btnFav.setText(Integer.toString(count+1));
                            tweets.get(getLayoutPosition()).setFavorited(true);
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Snackbar.make(view, responseString , Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }


            } else {
                Snackbar.make(view, R.string.msg_no_internet, Snackbar.LENGTH_SHORT).show();
            }
        }

        @OnClick(R.id.btnReply)
        public void replyClicked(View view){
            if(!Utils.isNetworkAvailable(mContext)) {
                Snackbar.make(view, R.string.msg_no_internet, Snackbar.LENGTH_SHORT).show();
            } else{
                /*NewTweetFragment filterDialogFragment = new NewTweetFragment();
                filterDialogFragment.show(getSupportFragmentManager(), "NewTweet");*/
            }
        }

        @OnClick(R.id.btnRetweet)
        public void reTweetClicked(final View view){
            if(Utils.isNetworkAvailable(mContext)) {
                TwitterClient client = TwitterApplication.getRestClient();
                final Tweet tweet = tweets.get(getLayoutPosition());
                long id = tweet.getId();
                final int currentCount = tweet.getFavorite_count();
                if(tweet.isRetweeted()){
                    //already retweeted
                    client.postUnRetweet(id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            btnRetweet.setCompoundDrawablesWithIntrinsicBounds(ic_retweet_grey, null, null, null);
                            tweets.get(getLayoutPosition()).setRetweeted(false);
                            //btnRetweet.setText(Integer.toString(currentCount-1));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Snackbar.make(view, responseString, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //Not already retweeted
                    client.postRetweet(id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            btnRetweet.setCompoundDrawablesWithIntrinsicBounds(ic_retweet_blue, null, null, null);
                            btnRetweet.setText(Integer.toString(currentCount+1));
                            tweets.get(getLayoutPosition()).setRetweeted(true);

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            Snackbar.make(view, responseString, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Snackbar.make(view, R.string.msg_no_internet, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
