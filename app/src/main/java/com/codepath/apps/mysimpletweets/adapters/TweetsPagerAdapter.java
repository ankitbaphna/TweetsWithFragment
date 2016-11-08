package com.codepath.apps.mysimpletweets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;

/**
 * Created by baphna on 11/6/2016.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    public TweetsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Home Timeline";
            case 1:
                return "Mentions Timeline";
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return  HomeTimelineFragment.newInstance(0,"Home Timeline");

            case 1:
                return MentionsTimelineFragment.newInstance(1, "Mentions Timeline");

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
