package com.drovik.player.weather.data;


import com.drovik.player.R;
import com.drovik.player.weather.IWeatherResponse;
import com.drovik.player.weather.data.BaseAdapterData;

public class HoursForecastData implements BaseAdapterData {

    public IWeatherResponse.Data.Hourly hourly;

    public HoursForecastData(IWeatherResponse.Data.Hourly hourly) {
        this.hourly = hourly;
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_hour_forecast;
    }
}
