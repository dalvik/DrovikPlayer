package com.drovik.player.weather;

import android.content.Intent;
import android.os.Bundle;

import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.drovik.player.R;
import com.drovik.player.ui.fragment.HomeFragment;

public class WeatherActivity extends BaseCompatActivity {

    private int type;

    private String TAG = "WeatherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        String topCityJson = intent.getStringExtra(ResourceProvider.TOP_CITY_JSON);
        LogUtil.d(TAG, "==> " + topCityJson);
        replaceFragment(R.id.weather_content, CityFragment.newInstance());
    }
}