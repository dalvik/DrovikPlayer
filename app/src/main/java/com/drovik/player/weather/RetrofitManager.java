package com.drovik.player.weather;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    public static String BASE_URL_WEATHER = "https://free-api.heweather.com/";
    public static String URL_CITY = "https://search.heweather.com/";
    public static String KEY = "80150e4b086847ee9fc4dbdc40035f4b";
    public static String GROUP = "cn";

    private static Map<String, Retrofit> retrofits = Collections.synchronizedMap(new WeakHashMap<String, Retrofit>());
    private static OkHttpClient client;

    private RetrofitManager() {

    }
    /**
     * 获取Retrofit实例
     * @param url
     * @return
     */
    public static Retrofit getDefaultRetrofit(String url) {
        Retrofit instance = retrofits.get(url);
        if (instance == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build();
            instance = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            retrofits.put(url, instance);
        }
        return instance;
    }

    public static Retrofit getRetrofit(String url, long timeOut) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
