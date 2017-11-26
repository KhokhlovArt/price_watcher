package com.example.khokhlovart.price_watcher;

import android.app.Application;
import android.text.TextUtils;

import com.example.khokhlovart.price_watcher.Api.IApi;
import com.google.android.gms.common.api.Api;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dom on 26.11.2017.
 */

public class App extends Application {
    private IApi api;
    private static final String PREFERENCES_SESSION = "session";
    private static final String KEY_AUTH_TOKEN = "userToken";
    public static final String KEY_AUTH_USER_EMAIL = "userEmail";
    @Override
    public void onCreate() {
        super.onCreate();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
//                .addInterceptor(new AuthInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://price-watcher.ru/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        api = retrofit.create(IApi.class);
    }

    public IApi getApi() {
        return api;
    }

    public void setPreferences(String preferences, String text) {
        getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE).edit().putString(preferences, text).apply();
    }

    public void deletePreferences(String preferences) {
        getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE).edit().remove(preferences).commit();
    }

    public String getPreferences(String preferences) {
        return getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE).getString(preferences, "");
    }

    public void setAuthToken(String authToken) {
        getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE).edit().putString(KEY_AUTH_TOKEN, authToken).apply();
    }

    public void deleteAuthToken() {
        getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE).edit().remove(KEY_AUTH_TOKEN).commit();
    }

    public String getAuthToken() {
        return getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE).getString(KEY_AUTH_TOKEN, "");
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getAuthToken());
    }

//    private class AuthInterceptor implements Interceptor {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Request originalRequest = chain.request();
//            HttpUrl url = originalRequest.url().newBuilder().addQueryParameter("userToken", getAuthToken()).build();
//            return chain.proceed(originalRequest.newBuilder().url(url).build());
//        }
//    }
}
