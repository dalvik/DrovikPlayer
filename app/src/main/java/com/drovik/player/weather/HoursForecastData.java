package com.drovik.player.weather;


import com.drovik.player.R;

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
