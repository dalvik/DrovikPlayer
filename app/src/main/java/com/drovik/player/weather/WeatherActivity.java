package com.drovik.player.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.utils.Utils;
import com.drovik.player.R;
import com.drovik.player.weather.data.CityInfoData;

import java.util.ArrayList;

public class WeatherActivity extends BaseCompatActivity {

    private int type;

    private CityFragment mCityFragment;

    private String TAG = "WeatherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        setActionBarVisiable(View.GONE);
        Utils.setStatusTextColor(false, this);
        fullScreen(R.color.base_content_background);
        setActionBarBackgroundColor(R.color.base_actionbar_background, R.color.home_actionbar_background);
        Intent intent = getIntent();
        type = intent.getIntExtra(ResourceProvider.TYPE, 0);
        String topCityJson = intent.getStringExtra(ResourceProvider.TOP_CITY_JSON);
        ArrayList<CityInfoData> citys = intent.getParcelableArrayListExtra(ResourceProvider.CITY_DATA);
        LogUtil.d(TAG, "==> " + topCityJson);
        if(type == 0) {
            mCityFragment = CityFragment.newInstance(citys, topCityJson);
            replaceFragment(R.id.weather_content, mCityFragment);
        } else {
            WeatherDetailFragment weatherDetailFragment = WeatherDetailFragment.newInstance();
            replaceFragment(R.id.weather_content, weatherDetailFragment);
        }
    }
}
