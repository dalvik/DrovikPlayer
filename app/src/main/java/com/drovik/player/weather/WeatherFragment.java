package com.drovik.player.weather;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.library.ui.pager.BasePager;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.drovik.player.R;
import com.drovik.player.location.LocationService;
import com.silencedut.taskscheduler.TaskScheduler;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class WeatherFragment extends BasePager {

    @BindView(R.id.weather_info_recyclerView)
    RecyclerView mWeatherItemList;

    private BaseRecyclerAdapter mMoreInfoAdapter;

    public static WeatherFragment newInstance() {
        WeatherFragment weatherFragment;
        weatherFragment = new WeatherFragment();
        return weatherFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_weather_detail, container, false);
        initViews();
        return view;
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    public void initBeforeView() {
    }

    public void initViews() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mWeatherItemList.setLayoutManager(linearLayoutManager);
        mWeatherItemList.setBackgroundResource(R.color.dark_background);
        mMoreInfoAdapter = new BaseRecyclerAdapter(getActivity());

        onMoreInfo( null, null);
    }


    public void onMoreInfo( List<IWeatherResponse.Data.DailyForecast> dailyForecastDatas, IWeatherResponse.Data.LifestyleForecast lifeIndexData) {

        if (!isVisible() || !isAdded()) {
            return;
        }

        mMoreInfoAdapter.clear();

        if (dailyForecastDatas != null) {
            GuideData guideData1 = new GuideData(getString(R.string.weather_future_weather));
            mMoreInfoAdapter.registerHolder(GuideHolder.class, guideData1);
            //mMoreInfoAdapter.registerHolder(DailyWeatherHolder.class, dailyForecastDatas);

        }

        /*if (aqiData != null) {

            GuideData guideData2 = new GuideData(getString(R.string.weather_aqi_guide));
            mMoreInfoAdapter.addData(guideData2);
            mMoreInfoAdapter.registerHolder(AqiViewHolder.class, aqiData);
        }

        if (lifeIndexData != null) {
            LifeIndexGuideData lifeIndexGuideData = new LifeIndexGuideData(getString(R.string.weather_lifeIndexes));
            mMoreInfoAdapter.registerHolder(LifeGuideHolder.class, lifeIndexGuideData);
            mMoreInfoAdapter.registerHolder(LifeIndexesHolder.class, lifeIndexData);
        }*/

        mWeatherItemList.setAdapter(mMoreInfoAdapter);
    }

    @Override
    public void reload() {

    }
}