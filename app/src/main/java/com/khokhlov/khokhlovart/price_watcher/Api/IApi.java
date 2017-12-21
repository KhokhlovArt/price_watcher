package com.khokhlov.khokhlovart.price_watcher.Api;

import com.khokhlov.khokhlovart.price_watcher.AddResult;
import com.khokhlov.khokhlovart.price_watcher.AuthRes;
import com.khokhlov.khokhlovart.price_watcher.Item;
import com.khokhlov.khokhlovart.price_watcher.PriceHistoryItem;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    @POST("rest/price/add")
    Call<AddResult> add_link(@Body HashMap<String, Integer> id);

    @GET("rest/getPriceHistory/{user_id}/{price_id}")
    Call<List<PriceHistoryItem>> getPriceHistory(@Path(value = "user_id", encoded = true) String userId, @Path(value = "price_id", encoded = true) int price_id);
}
