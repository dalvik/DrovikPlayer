package com.drovik.player.weather;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WeatherManager {

    private String TAG = "WeatherManager";

    public WeatherManager() {
    }

    public void weather(String location) {
        Retrofit retrofit = RetrofitManager.getDefaultRetrofit(RetrofitManager.BASE_URL_WEATHER);
        IWeather service = retrofit.create(IWeather.class);
        Call<IWeatherResponse> fetcherWeaherCall = service.weather(RetrofitManager.KEY, location);
        fetcherWeaherCall.enqueue(new Callback<IWeatherResponse>() {
            @Override
            public void onResponse(Call<IWeatherResponse> call, Response<IWeatherResponse> response) {
                IWeatherResponse fetchWeatherResponse = response.body();
                EventBus.getDefault().post(fetchWeatherResponse);
            }

            @Override
            public void onFailure(Call<IWeatherResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
