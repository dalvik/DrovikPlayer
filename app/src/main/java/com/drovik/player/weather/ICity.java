package com.drovik.player.weather;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ICity {

    @GET("top?")
    Call<ICityResponse> topCity(@Query("key") String key, @Query("group") String group, @QueryMap Map<String, String> map);

    @GET("find?")
    Call<ICityResponse> findCity(@Query("key") String key, @Query("group") String group, @QueryMap Map<String, String> map);

}
