package com.drovik.player.weather.data;

import com.drovik.player.R;
import com.drovik.player.weather.IWeatherResponse;

import java.util.List;

public class BannerData implements BaseAdapterData {

    public List<IWeatherResponse.Data.LifeStyle> lifeIndexesData;

    public BannerData(List<IWeatherResponse.Data.LifeStyle> lifeIndexesData) {
        this.lifeIndexesData = lifeIndexesData;
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_ad_banner;
    }
}
