package com.codepath.apps.mysimpletweets.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.application.TwitterApplication;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.utils.PatternEditableBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.regex.Pattern;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ProfileActivity extends AppCompatActivity {

    TwitterClient twitterClient;
    User user;

    @BindColor(R.color.colorAccent)
    int colorAccent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.flContainer)
    FrameLayout flContainer;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.tvTagline)
    TextView tvTagline;
    @BindView(R.id.btnFollowers)
    Button btnFollowers;
    @BindView(R.id.btnFollowing)
    Button btnFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        twitterClient = TwitterApplication.getRestClient();

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(ProfileActivity.this, "Clicked username: " + text,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).into(tvTagline);

        if (savedInstanceState == null) {
            String screenName = getIntent().getStringExtra("screenName");

            user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));

            if(user == null){
                getCurrentUserInfo();
            } else {
                setupViews();
            }

            UserTimelineFragment userTimelineFragment = (UserTimelineFragment) UserTimelineFragment.newInstance(screenName);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    public void getCurrentUserInfo() {
        twitterClient.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                user = User.fromJson(response);
                setupViews();
            }
        });
    }

    private void setupViews() {
        btnFollowers.setText(user.getFollowersCount() + " Followers");
        btnFollowing.setText(user.getFollowingCount() + " Following");
        if(!user.getDescription().equals("")) {
            tvTagline.setText(user.getDescription());
        } else {
            tvTagline.setVisibility(View.GONE);
        }
        collapsingToolbarLayout.setTitle("@" + user.getProfileName());

        Glide.with(ProfileActivity.this).load(user.getProfileBannerUrl())
                .asBitmap().into(new SimpleTarget<Bitmap>(100,100) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(resource);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            collapsingToolbarLayout.setBackground(drawable);
                        }
                    }
            });
    }
}
