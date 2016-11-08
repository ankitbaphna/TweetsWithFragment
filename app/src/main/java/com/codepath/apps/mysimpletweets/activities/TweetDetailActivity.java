package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.Utils;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetDetailActivity extends AppCompatActivity {

    @BindView(R.id.ivProfilePhoto)
    ImageView ivProfilePhoto;
    @BindView(R.id.tvText)
    TextView tvText;
    @BindView(R.id.etReplyText)
    TextInputEditText etReplyText;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.ivMedia)
    ImageView ivMedia;
    @BindView(R.id.tvRetweetCount)
    TextView tvRetweetCount;
    @BindView(R.id.tvNumLikes)
    TextView tvNumLikes;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvScreenName)
    TextView tvScreenName;

    private TwitterClient mClient;
    private Tweet mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeButtonEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);

        mTweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));
        Glide.with(getApplicationContext()).load(mTweet.getUser().getProfileImageUrl()).into(ivProfilePhoto);
        tvName.setText(mTweet.getUser().getName());
        tvScreenName.setText(String.format("@%s", mTweet.getUser().getProfileName()));
        tvText.setText(mTweet.getBody());
        if(mTweet.getCreatedAt() != null) {
            tvTime.setText(Utils.getRelativeTimeAgo(mTweet.getCreatedAt()));
        } else {
            tvTime.setText("0s");
        }
        tvNumLikes.setText(String.format("%d", mTweet.getFavorite_count()));
        tvRetweetCount.setText(String.format("%d", mTweet.getRetweet_count()));

        etReplyText.setHint("Reply to " + mTweet.getUser().getName());

    }
}
