package com.drovik.player.weather.data;

import android.support.v4.util.Pair;

import com.drovik.player.R;
import com.drovik.player.weather.data.BaseAdapterData;

import java.util.ArrayList;
import java.util.List;

public class HeaderData implements BaseAdapterData {


    private List<Pair<String, String>> mHotCities;

    public HeaderData() {
        mHotCities = new ArrayList<>();
    }

    public List<Pair<String, String>> getCities() {
        return mHotCities;
    }

    public void setData(ArrayList<Pair<String, String>> hotCities) {
        mHotCities.clear();
        mHotCities.addAll(hotCities);
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_city_city_select_header;
    }
}
