package com.drovik.player.weather;

import android.support.v4.util.Pair;

import com.drovik.player.R;

import java.util.ArrayList;
import java.util.List;

public class HeaderData implements BaseAdapterData {


    private List<Pair<String, String>> mHotCities;

    public HeaderData() {
        mHotCities = new ArrayList<>();
        mHotCities.add(new Pair<>("北京", "CN101010100"));
        mHotCities.add(new Pair<>("上海", "CN101020100"));
        mHotCities.add(new Pair<>("广州", "CN101280101"));
        mHotCities.add(new Pair<>("深圳", "CN101280601"));
        mHotCities.add(new Pair<>("杭州", "CN101210101"));
        mHotCities.add(new Pair<>("南京", "CN101190101"));
        mHotCities.add(new Pair<>("武汉", "CN101200101"));
        mHotCities.add(new Pair<>("重庆", "CN101040100"));

    }

    List<Pair<String, String>> getCities() {
        return mHotCities;
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_city_city_select_header;
    }
}
