
package com.codepath.apps.mysimpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.apps.mysimpletweets.database.TweetDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

@Table(database = TweetDatabase.class)
public class User extends BaseModel implements Parcelable {
    @Column
    private String name;
    @Column
    private String profileName;
    @Column
    private String profileImageUrl;
    @Column
    @PrimaryKey
    private long uid;
    private String profileBannerUrl;
    private String description;
    private int followersCount;
    private int followingCount;
    private boolean isFollowing;

    public static User fromJson(JSONObject jsonObject){
        User user = new User();

        try {
            user.name = jsonObject.getString("name");
            user.profileName = jsonObject.getString("screen_name");
            user.uid = jsonObject.getLong("id");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.description = jsonObject.getString("description");
            user.followersCount = jsonObject.getInt("followers_count");
            user.followingCount = jsonObject.getInt("friends_count");
            user.isFollowing = jsonObject.getBoolean("following");
            user.profileBannerUrl = jsonObject.getString("profile_banner_url") + "/mobile";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getProfileBannerUrl() {
        return profileBannerUrl;
    }

    public void setProfileBannerUrl(String profileBannerUrl) {
        this.profileBannerUrl = profileBannerUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }


    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.profileName);
        dest.writeString(this.profileImageUrl);
        dest.writeLong(this.uid);
        dest.writeString(this.profileBannerUrl);
        dest.writeString(this.description);
        dest.writeInt(this.followersCount);
        dest.writeInt(this.followingCount);
        dest.writeByte(this.isFollowing ? (byte) 1 : (byte) 0);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.profileName = in.readString();
        this.profileImageUrl = in.readString();
        this.uid = in.readLong();
        this.profileBannerUrl = in.readString();
        this.description = in.readString();
        this.followersCount = in.readInt();
        this.followingCount = in.readInt();
        this.isFollowing = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
