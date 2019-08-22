package org.matt.restapi;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TwitterApi {

    String BASE_URL = "https://api.twitter.com/";

    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<OAuthToken> postCredentials(@Field("grant_type") String grantType);

    @GET("/1.1/users/show.json")
    Call<UserDetails> getUserDetails(@Query("screen_name") String name);

    @GET("/1.1/search/tweets.json")
    Call<Tweet> getTweets(@Query("q") String query);
}