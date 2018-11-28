package com.drovik.player.weather.data;

import com.drovik.player.R;
import com.drovik.player.weather.IWeatherResponse;

public class AqiData implements BaseAdapterData {

    public IWeatherResponse.Data.AirNowCity aqiData;

    public AqiData(IWeatherResponse.Data.AirNowCity aqiData) {
        this.aqiData = aqiData;
    }


    @Override
    public int getContentViewId() {
        return R.layout.item_weather_aqi;
    }
}
