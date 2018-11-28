package com.drovik.player.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.audiorecorder.ui.SettingsActivity;
import com.android.library.ui.pager.BasePager;
import com.drovik.player.R;
import com.drovik.player.weather.data.AqiData;
import com.drovik.player.weather.data.DailyWeatherData;
import com.drovik.player.weather.data.GuideData;
import com.drovik.player.weather.data.LifeIndexData;
import com.drovik.player.weather.data.LifeIndexGuideData;
import com.drovik.player.weather.holder.AqiViewHolder;
import com.drovik.player.weather.holder.DailyWeatherHolder;
import com.drovik.player.weather.holder.GuideHolder;
import com.drovik.player.weather.holder.LifeGuideHolder;
import com.drovik.player.weather.holder.LifeIndexesHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class WeatherFragment extends BasePager {

    private SharedPreferences mSettings;

    private RecyclerView mWeatherItemList;

    private BaseRecyclerAdapter mMoreInfoAdapter;

    private List<DailyWeatherData> mDailyWeatherDataList;

    private List<IWeatherResponse.Data.LifeStyle> mStyleDataList;

    private LifeIndexData mLifeIndexData;

    public static WeatherFragment newInstance() {
        WeatherFragment weatherFragment;
        weatherFragment = new WeatherFragment();
        return weatherFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather_detail, container, false);
        mSettings = getActivity().getSharedPreferences(SettingsActivity.class.getName(), MODE_PRIVATE);
        mDailyWeatherDataList = new ArrayList<DailyWeatherData>();
        mStyleDataList = new ArrayList<>();
        initViews(view);
        onMoreInfo(null, mDailyWeatherDataList, mLifeIndexData);
        return view;
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    public void initViews(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mWeatherItemList = (RecyclerView) view.findViewById(R.id.weather_info_recyclerView);
        mWeatherItemList.setLayoutManager(linearLayoutManager);
        mWeatherItemList.setBackgroundResource(R.color.dark_background);
        mMoreInfoAdapter = new BaseRecyclerAdapter(getActivity());
        String dailyJsonStrig = mSettings.getString(ResourceProvider.DAILY_FORECAST, "");
        if(!TextUtils.isEmpty(dailyJsonStrig)){
            try {
                JSONArray dailyJson = new JSONArray(dailyJsonStrig);
                int length = dailyJson.length();
                for(int i=0; i<length; i++) {
                    JSONObject object = dailyJson.getJSONObject(i);
                    IWeatherResponse.Data.DailyForecast forecast = new IWeatherResponse.Data.DailyForecast(object.toString());
                    DailyWeatherData data = new DailyWeatherData(forecast);
                    mDailyWeatherDataList.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String styleJsonStrig = mSettings.getString(ResourceProvider.LIFE_STYLE, "");
        if(!TextUtils.isEmpty(styleJsonStrig)){
            try {
                JSONArray styleJson = new JSONArray(styleJsonStrig);
                int length = styleJson.length();
                for(int i=0; i<length; i++) {
                    JSONObject object = styleJson.getJSONObject(i);
                    IWeatherResponse.Data.LifeStyle style = new IWeatherResponse.Data.LifeStyle(object.toString());
                    mStyleDataList.add(style);
                }
                mLifeIndexData = new LifeIndexData(mStyleDataList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void onMoreInfo(AqiData aqiData, List<DailyWeatherData> dailyForecastDatas, LifeIndexData lifeIndexData) {
        mMoreInfoAdapter.clear();
        if (dailyForecastDatas != null) {
            GuideData guideData1 = new GuideData(getString(R.string.weather_future_weather));
            mMoreInfoAdapter.registerHolder(GuideHolder.class, guideData1);
            mMoreInfoAdapter.registerHolder(DailyWeatherHolder.class, dailyForecastDatas);
        }
        if (aqiData != null) {
            GuideData guideData2 = new GuideData(getString(R.string.weather_aqi_guide));
            mMoreInfoAdapter.addData(guideData2);
            mMoreInfoAdapter.registerHolder(AqiViewHolder.class, aqiData);
        }
        if (lifeIndexData != null) {
            LifeIndexGuideData lifeIndexGuideData = new LifeIndexGuideData(getString(R.string.weather_lifeIndexes));
            mMoreInfoAdapter.registerHolder(LifeGuideHolder.class, lifeIndexGuideData);
            mMoreInfoAdapter.registerHolder(LifeIndexesHolder.class, lifeIndexData);
        }
        mWeatherItemList.setAdapter(mMoreInfoAdapter);
    }

    @Override
    public void reload() {

    }
}