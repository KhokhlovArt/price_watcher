package com.example.khokhlovart.price_watcher.Api;

import com.example.khokhlovart.price_watcher.AuthRes;
import com.example.khokhlovart.price_watcher.Item;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Dom on 26.11.2017.
 */

public interface IApi {
    @Headers({
            "CONTENT-TYPE: application/json",
    })
//    @POST("rest/auth")
//    Call<AuthRes> auth(@Query("email") String email, @Query("password") String password);

    @POST("rest/auth")
    Call<AuthRes> auth(@Body HashMap<String, String> body);


    @GET("rest/prices/{user_id}")
    Call<List<Item>> prices(@Path(value = "user_id", encoded = true) String userId);

    @POST("rest/price/delete")
    Call<Void> delete(@Body HashMap<String, Integer> id);
}
