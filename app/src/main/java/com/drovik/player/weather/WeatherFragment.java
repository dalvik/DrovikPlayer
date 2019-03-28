package com.drovik.player.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.audiorecorder.ui.SettingsActivity;
import com.android.audiorecorder.utils.DateUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.pager.BasePager;
import com.drovik.player.R;
import com.drovik.player.weather.data.AqiData;
import com.drovik.player.weather.data.BannerData;
import com.drovik.player.weather.data.DailyWeatherData;
import com.drovik.player.weather.data.GuideData;
import com.drovik.player.weather.data.LifeIndexData;
import com.drovik.player.weather.data.LifeIndexGuideData;
import com.drovik.player.weather.holder.AqiViewHolder;
import com.drovik.player.weather.holder.BannerHolder;
import com.drovik.player.weather.holder.DailyWeatherHolder;
import com.drovik.player.weather.holder.GuideHolder;
import com.drovik.player.weather.holder.LifeGuideHolder;
import com.drovik.player.weather.holder.LifeIndexesHolder;
import com.iflytek.voiceads.IFLYBannerAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.listener.IFLYAdListener;

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

    private AqiData mAqiData;

    private LifeIndexData mLifeIndexData;

    private IFLYBannerAd bannerView;

    private LinearLayout bannerAdLayout;

    private String TAG = WeatherFragment.class.getSimpleName();

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
        onMoreInfo(mAqiData, mDailyWeatherDataList, mLifeIndexData);
        initAd();
        return view;
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    public void initViews(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        view.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        ImageView titleIcon = (ImageView)view.findViewById(R.id.title_icon);
        String condTxt = mSettings.getString(ResourceProvider.COND_TXT, "");
        if(!TextUtils.isEmpty(condTxt)) {
            titleIcon.setImageResource(ResourceProvider.getIconId(condTxt));
        } else {
            titleIcon.setImageDrawable(null);
        }
        TextView titleTemp = (TextView)view.findViewById(R.id.title_temp);
        TextView location = (TextView)view.findViewById(R.id.main_location);
        TextView postTime = (TextView)view.findViewById(R.id.main_post_time);
        String city = mSettings.getString(ResourceProvider.ADMIN_AREA, "");
        String country = mSettings.getString(ResourceProvider.LOCATION, "");
        titleTemp.setText(mSettings.getString(ResourceProvider.TMP, "") + "°");
        if(city.equalsIgnoreCase(country)) {
            location.setText(getResources().getString(R.string.china) + " • " + country);
        } else {
            location.setText(city + " • " + country);
        }
        String updateTime = String.format(getString(R.string.weather_post),
                DateUtil.getTimeTips(mSettings.getString(ResourceProvider.PUBLISH_TIME, "2018-11-30 21:20:12")));
        postTime.setText(updateTime);
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
        String airNowCityJsonStrig = mSettings.getString(ResourceProvider.AIR_NOW_CITY, "");
        if(!TextUtils.isEmpty(airNowCityJsonStrig)){
            try {
                JSONObject airNowCityJson = new JSONObject(airNowCityJsonStrig);
                IWeatherResponse.Data.AirNowCity airNowCity = new IWeatherResponse.Data.AirNowCity(airNowCityJson.toString());
                mAqiData = new AqiData(airNowCity);
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
        if (lifeIndexData != null) {
            LifeIndexGuideData lifeIndexGuideData = new LifeIndexGuideData(getString(R.string.weather_lifeIndexes));
            mMoreInfoAdapter.registerHolder(LifeGuideHolder.class, lifeIndexGuideData);
            mMoreInfoAdapter.registerHolder(LifeIndexesHolder.class, lifeIndexData);
        }
        if (aqiData != null) {
            GuideData guideData2 = new GuideData(getString(R.string.weather_aqi_guide));
            mMoreInfoAdapter.addData(guideData2);
            mMoreInfoAdapter.registerHolder(AqiViewHolder.class, aqiData);
        }
        BannerData data = new BannerData(new ArrayList<IWeatherResponse.Data.LifeStyle>());
        mMoreInfoAdapter.registerHolder(BannerHolder.class, data);
        bannerAdLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(data.getContentViewId(), null);
        mWeatherItemList.setAdapter(mMoreInfoAdapter);
    }

    @Override
    public void reload() {

    }

    private void initAd() {
        String adUnitId = "A0491E423BADCABA1FDFD786B3DA9260";
        bannerView = IFLYBannerAd.createBannerAd(getActivity(), adUnitId);
        bannerView.setParameter(AdKeys.APP_VER, StringUtils.getVersionName(getActivity()));
        //广告容器添加bannerView
        if(bannerAdLayout != null) {
            bannerAdLayout.removeAllViews();
            bannerAdLayout.addView(bannerView);
        }
        //请求广告，添加监听器
        bannerView.loadAd(mAdListener);
    }

    private IFLYAdListener mAdListener = new IFLYAdListener() {

        /**
         * 广告请求成功
         */
        @Override
        public void onAdReceive() {
            //展示广告
            bannerView.showAd();
            LogUtil.d(TAG, "onAdReceive");
        }

        @Override
        public void onAdFailed(AdError error) {
            //获取广告失败
            LogUtil.d(TAG, "onAdFailed");
        }

        /**
         * 广告被点击
         */
        @Override
        public void onAdClick() {
            LogUtil.d(TAG, "onAdClick");
        }

        /**
         * 广告被关闭
         */
        @Override
        public void onAdClose() {
            LogUtil.d(TAG, "onAdClose");
        }

        /**
         * 广告曝光
         */
        @Override
        public void onAdExposure() {
            LogUtil.d(TAG, "onAdExposure");
        }

        /**
         * 下载确认
         */
        @Override
        public void onConfirm() {
            LogUtil.d(TAG, "onConfirm");
        }

        /**
         * 下载取消
         */
        @Override
        public void onCancel() {
            LogUtil.d(TAG, "onCancel");
        }
    };
}