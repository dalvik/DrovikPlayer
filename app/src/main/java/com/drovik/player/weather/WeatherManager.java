package com.drovik.player.weather;

import android.util.Log;

import com.drovik.player.weather.event.AirNowEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

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
        final Call<IWeatherResponse> fetcherWeaherCall = service.weather(RetrofitManager.KEY, location);
        fetcherWeaherCall.enqueue(new Callback<IWeatherResponse>() {
            @Override
            public void onResponse(Call<IWeatherResponse> call, Response<IWeatherResponse> response) {
                IWeatherResponse fetchWeatherResponse = response.body();
                if(fetchWeatherResponse != null) {
                    EventBus.getDefault().post(fetchWeatherResponse);
                } else {
                    Log.d(TAG, "==> fetchWeatherResponse null.");
                }
            }

            @Override
            public void onFailure(Call<IWeatherResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void topCity(String group, String number) {
        Retrofit retrofit = RetrofitManager.getDefaultRetrofit(RetrofitManager.URL_CITY);
        ICity service = retrofit.create(ICity.class);
        Call<ICityResponse> fetcherWeaherCall = service.topCity(RetrofitManager.KEY, group, number);
        fetcherWeaherCall.enqueue(new Callback<ICityResponse>() {
            @Override
            public void onResponse(Call<ICityResponse> call, Response<ICityResponse> response) {
                ICityResponse fetchWeatherResponse = response.body();
                if(fetchWeatherResponse != null) {
                    EventBus.getDefault().post(fetchWeatherResponse);
                } else {
                    Log.d(TAG, "==> fetchWeatherResponse null.");
                }
            }

            @Override
            public void onFailure(Call<ICityResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getAirNow(String location) {
        Retrofit retrofit = RetrofitManager.getDefaultRetrofit(RetrofitManager.BASE_URL_WEATHER);
        IWeather service = retrofit.create(IWeather.class);
        Map<String,String> params = new HashMap<String,String>();
        params.put("location", location);
        Call<IWeatherResponse> fetcherWeaherCall = service.getAirNow(RetrofitManager.KEY, params);
        fetcherWeaherCall.enqueue(new Callback<IWeatherResponse>() {
            @Override
            public void onResponse(Call<IWeatherResponse> call, Response<IWeatherResponse> response) {
                IWeatherResponse fetchWeatherResponse = response.body();
                if(fetchWeatherResponse != null) {
                    AirNowEvent aqiEvent = new AirNowEvent(fetchWeatherResponse);
                    EventBus.getDefault().post(aqiEvent);
                } else {
                    Log.d(TAG, "==> fetch aqi WeatherResponse null.");
                }
            }

            @Override
            public void onFailure(Call<IWeatherResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
