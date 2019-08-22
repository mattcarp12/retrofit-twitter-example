package org.matt.restapi;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

public class TwitterApiCall {

    private static final String TWITTER_API_CONSUMER_KEY = "ko9A8JKUa1HFALBY1LwLYICWq";
    private static final String TWITTER_API_CONSUMER_SECRET_KEY = "k8bVfThsns8392bvdbFF2Ule2dFEKfMO7PjEwOfE1bWgleEnNI";
    private String credentials = Credentials.basic(TWITTER_API_CONSUMER_KEY, TWITTER_API_CONSUMER_SECRET_KEY);
    public TwitterApi twitterApi, authApi;
    public OAuthToken token;

    public void createAuthApi() {
        OkHttpClient okHttpClient = new OkHttpClient
            .Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

        Retrofit rf = new Retrofit.Builder()
                .baseUrl(TwitterApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authApi = rf.create(TwitterApi.class);

        //token = authApi.postCredentials("client_credentials")

    }

    public void createTwitterApi() {


        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                        token != null ? token.getAuthorization() : credentials);

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        })
        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TwitterApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        twitterApi = retrofit.create(TwitterApi.class);
    }

    Callback<OAuthToken> tokenCallback = new Callback<OAuthToken>() {
        @Override
        public void onResponse(Call<OAuthToken> call, Response<OAuthToken> response) {
            if (response.isSuccessful()) {
                token = response.body();
            } else {
                System.err.println("Authorization request not successful");
            }
        }

        @Override
        public void onFailure(Call<OAuthToken> call, Throwable t) {
            t.printStackTrace();
        }
    };

    Callback<UserDetails> userDetailsCallback = new Callback<UserDetails>() {
        @Override
        public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
            if (response.isSuccessful()) {
                UserDetails userDetails = response.body();
                System.out.println(userDetails.getName());
                System.out.println(userDetails.getDescription());
                System.out.println(userDetails.getLocation());
                System.out.println(userDetails.getUrl());

            } else {
                System.err.println("User detail request not successful");
            }
        }

        @Override
        public void onFailure(Call<UserDetails> call, Throwable t) {
            t.printStackTrace();
        }
    };

    public static void main(String... args) {
        TwitterApiCall tweets = new TwitterApiCall();
        tweets.createTwitterApi();
        try{
            tweets.token = tweets.twitterApi.postCredentials("client_credentials").execute().body();
        } catch(IOException e) {
            e.printStackTrace();
        }
        tweets.twitterApi.getUserDetails("twitterdev").enqueue(tweets.userDetailsCallback);
    }
}
