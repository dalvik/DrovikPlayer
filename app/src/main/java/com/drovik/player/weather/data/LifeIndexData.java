package com.drovik.player.weather.data;

import com.drovik.player.R;
import com.drovik.player.weather.IWeatherResponse;
import com.drovik.player.weather.data.BaseAdapterData;

import java.util.List;

public class LifeIndexData implements BaseAdapterData {

    public List<IWeatherResponse.Data.LifeStyle> lifeIndexesData;

    public LifeIndexData(List<IWeatherResponse.Data.LifeStyle> lifeIndexesData) {
        this.lifeIndexesData = lifeIndexesData;
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_life;
    }
}
