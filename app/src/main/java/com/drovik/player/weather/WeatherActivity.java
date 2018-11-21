package com.drovik.player.weather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.drovik.player.R;
import com.drovik.player.ui.fragment.HomeFragment;

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
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        String topCityJson = intent.getStringExtra(ResourceProvider.TOP_CITY_JSON);
        ArrayList<CityInfoData> citys = intent.getParcelableArrayListExtra(ResourceProvider.CITY_DATA);
        LogUtil.d(TAG, "==> " + topCityJson);
        mCityFragment = CityFragment.newInstance(citys);
        replaceFragment(R.id.weather_content, mCityFragment);
        /*if(citys != null) {
            mCityFragment.onAllCities(citys);
        }*/
    }
}
