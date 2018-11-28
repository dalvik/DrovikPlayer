package com.drovik.player.weather.data;

import com.drovik.player.R;
import com.drovik.player.weather.IWeatherResponse;
import com.drovik.player.weather.data.BaseAdapterData;

public class DailyWeatherData implements BaseAdapterData {

    public IWeatherResponse.Data.DailyForecast dailyForecastData;

    public DailyWeatherData(IWeatherResponse.Data.DailyForecast dailyForecastData) {
        this.dailyForecastData = dailyForecastData;
    }


    @Override
    public int getContentViewId() {
        return R.layout.item_weather_daily_forecast;
    }
}
