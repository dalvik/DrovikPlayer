package com.drovik.player.weather;

import com.drovik.player.R;

public class GuideData implements BaseAdapterData {
    public String guide;
    public int guideIconId;

    public GuideData(String guide) {
        this.guide = guide;
    }

    public void setGuideIconId(int guideIconId) {
        this.guideIconId = guideIconId;
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_guide;
    }
}
