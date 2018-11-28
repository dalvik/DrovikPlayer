package com.drovik.player.weather.data;

import com.drovik.player.R;
import com.drovik.player.weather.data.BaseAdapterData;

public class LifeIndexGuideData implements BaseAdapterData {

    public String guide;

    public LifeIndexGuideData(String guide) {
        this.guide = guide;
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_index_guide;
    }
}
