package com.drovik.player.weather;

import com.drovik.player.R;

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
