package com.codepath.apps.mysimpletweets.network;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "VpHUu1kCCKPJurQZwD7m3MnaB";       // Change this
	public static final String REST_CONSUMER_SECRET = "sarlZYz2eH1AzqWt7YmqBeAP2R0KNtDppSpPfZP0akvDH9fDsD"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler){
        String url = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if(maxId != -1) {
            params.put("max_id", maxId - 1);
        } else {
            params.put("since_id", 1);
        }
        params.put("include_entities", true);
        client.get(url, params, handler);
    }

    public void getMentionsTimeline(long useThisMaxId, JsonHttpResponseHandler jsonHttpResponseHandler) {
        String url = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if(useThisMaxId != -1) {
            params.put("max_id", useThisMaxId - 1);
        } else {
            params.put("since_id", 1);
        }
        params.put("include_entities", true);
        client.get(url, params, jsonHttpResponseHandler);

    }


    public void postToTwitterHomeTimeline(String tweetBody, long replyToStatusId,
                                          AsyncHttpResponseHandler handler){
		String url = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetBody);
        if(replyToStatusId != -1) {
            params.put("in_reply_to_status_id", replyToStatusId);
        }
		client.post(url, params, handler);
	}


    public void postMakeFavorite(long id, AsyncHttpResponseHandler handler){
        String url = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(url, params, handler);
    }

    public void postMakeUnFavorite(long id, AsyncHttpResponseHandler handler){
        String url = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", id);
        client.post(url, params, handler);
    }

    public void postRetweet(long id, AsyncHttpResponseHandler handler){
        String tweetId = String.valueOf(id);
        String url = getApiUrl(String.format("statuses/retweet/%s.json", tweetId));
        RequestParams params = new RequestParams();
        client.post(url, params, handler);
    }

    public void postUnRetweet(long id, AsyncHttpResponseHandler handler){
        String tweetId = String.valueOf(id);
        String url = getApiUrl(String.format("statuses/unretweet/%s.json", tweetId));
        RequestParams params = new RequestParams();
        client.post(url, params, handler);
    }

    public void getUserTimeLine(String screenName, long maxId, AsyncHttpResponseHandler handler){
        String url = getApiUrl(String.format("statuses/user_timeline.json"));
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        if (maxId != -1) {
            params.put("max_id", maxId);
        } else {
            params.put("since_id", 1);
        }
        getClient().get(url, params, handler);
    }

    public void getUserInfo(AsyncHttpResponseHandler handler){
        String url = getApiUrl(String.format("account/verify_credentials.json"));
        getClient().get(url, handler);
    }

    public void getFriendsList(long userId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        getClient().get(apiUrl, params, handler);
    }

    public void getFollowersList(long userId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", userId);
        getClient().get(apiUrl, params, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}