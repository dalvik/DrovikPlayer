package com.drovik.player.weather;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface IWeather {

    @GET("s6/weather?")
    Call<IWeatherResponse> weather(@Query("key") String key, @Query("location") String location);

    @GET("s6/weather/forecast?")
    Call<IWeatherResponse> weatherForecast(@Query("key") String key, @Query("location") String location);

    @GET("s6/weather/now?")
    Call<IWeatherResponse> weatherNow(@Query("key") String key, @QueryMap Map<String, String> map);

    @GET("s6/weather/hourly?")
    Call<IWeatherResponse> weatherHourly(@Query("key") String key, @QueryMap Map<String, String> map);

    @GET("s6/weather/lifestyle?")
    Call<IWeatherResponse> weatherLifestyle(@Query("key") String key, @QueryMap Map<String, String> map);

    @GET("s6/air/now?")
    Call<IWeatherResponse> getAirNow(@Query("key") String key, @QueryMap Map<String, String> map);
}
