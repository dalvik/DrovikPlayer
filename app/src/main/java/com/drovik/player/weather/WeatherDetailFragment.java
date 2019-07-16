package com.drovik.player.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.audiorecorder.ui.SettingsActivity;
import com.android.audiorecorder.utils.DateUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;
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
import com.iflytek.voiceads.IFLYBannerAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.listener.IFLYAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cdc.sed.yff.nm.bn.BannerManager;
import cdc.sed.yff.nm.bn.BannerViewListener;
import cdc.sed.yff.nm.cm.ErrorCode;
import cdc.sed.yff.nm.sp.SpotListener;
import cdc.sed.yff.nm.sp.SpotManager;

import static android.content.Context.MODE_PRIVATE;

public class WeatherDetailFragment extends BasePager {

    private final static int MSG_REQUEST_AD = 1000;
    private final static int MSG_HIDE_AD = 1001;
    private final static String adUnitId = "A0491E423BADCABA1FDFD786B3DA9260";

    private SharedPreferences mSettings;

    private RecyclerView mWeatherItemList;

    private BaseRecyclerAdapter mMoreInfoAdapter;

    private List<DailyWeatherData> mDailyWeatherDataList;

    private List<IWeatherResponse.Data.LifeStyle> mStyleDataList;

    private AqiData mAqiData;

    private LifeIndexData mLifeIndexData;

    private IFLYBannerAd bannerView;

    private LinearLayout bannerAdLayout;

    private Context mContext;
    private String TAG = WeatherDetailFragment.class.getSimpleName();

    public static WeatherDetailFragment newInstance() {
        WeatherDetailFragment weatherFragment;
        weatherFragment = new WeatherDetailFragment();
        return weatherFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.activity_weather_detail, container, false);
        mSettings = getActivity().getSharedPreferences(SettingsActivity.class.getName(), MODE_PRIVATE);
        mDailyWeatherDataList = new ArrayList<DailyWeatherData>();
        mStyleDataList = new ArrayList<>();
        initViews(view);
        onMoreInfo(mAqiData, mDailyWeatherDataList, mLifeIndexData);
        //sendHandlerMessage(MSG_REQUEST_AD, 3000);
        return view;
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeMessages(MSG_REQUEST_AD);
        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
    }

    private void initViews(View view) {
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
        bannerAdLayout = view.findViewById(R.id.ad_container);
        mWeatherItemList = (RecyclerView) view.findViewById(R.id.weather_info_recyclerView);
        mWeatherItemList.setLayoutManager(linearLayoutManager);
        mWeatherItemList.setBackgroundResource(R.color.dark_background);
        mMoreInfoAdapter = new BaseRecyclerAdapter(mContext);
        setupNativeSpotAd();
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


    private void onMoreInfo(AqiData aqiData, List<DailyWeatherData> dailyForecastDatas, LifeIndexData lifeIndexData) {
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
        mWeatherItemList.setAdapter(mMoreInfoAdapter);
    }

    private void initAd() {
        bannerView = IFLYBannerAd.createBannerAd(getActivity().getApplicationContext(), adUnitId);
        bannerView.setParameter(AdKeys.APP_VER, StringUtils.getVersionName(getActivity()));
        //广告容器添加bannerView
        bannerAdLayout.removeAllViews();
        bannerAdLayout.addView(bannerView);
        //请求广告，添加监听器
        bannerView.loadAd(mAdListener);
    }

    private void hideAd() {
        if (bannerView != null) {
            bannerView.destroy();
        }
        bannerAdLayout.setVisibility(View.GONE);
    }

    private void sendHandlerMessage(int what, long delayed) {
        mHandler.removeMessages(what);
        mHandler.sendEmptyMessageDelayed(what, delayed);
    }

    //3秒钟后请求，显示1分钟自动隐藏，15分钟重新请求
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_REQUEST_AD:
                    initAd();
                    break;
                case MSG_HIDE_AD:
                    hideAd();
                    sendHandlerMessage(MSG_REQUEST_AD, 10*60*1000);//十分钟后重新请求
                    break;
                    default:
                        break;
            }
        }
    };

    private IFLYAdListener mAdListener = new IFLYAdListener() {

        /**
         * 广告请求成功
         */
        @Override
        public void onAdReceive() {
            //展示广告
            bannerAdLayout.setVisibility(View.VISIBLE);
            bannerView.showAd();
            sendHandlerMessage(MSG_HIDE_AD, 60*1000);//1分钟后自动隐藏
            LogUtil.d(TAG, "==> onAdReceive");
            //Toast.makeText(getActivity(), "onAdReceive", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onAdFailed(AdError error) {
            //获取广告失败
            //Toast.makeText(getActivity(), "onAdFailed", Toast.LENGTH_SHORT).show();
            LogUtil.d(TAG, "==> onAdFailed: " + error.getErrorDescription() + " " + error.getErrorCode());
            sendHandlerMessage(MSG_REQUEST_AD, 10*60*1000);//十分钟后重新请求
        }

        /**
         * 广告被点击
         */
        @Override
        public void onAdClick() {
            LogUtil.d(TAG, "==> onAdClick");
        }

        /**
         * 广告被关闭
         */
        @Override
        public void onAdClose() {
            LogUtil.d(TAG, "==> onAdClose");
        }

        /**
         * 广告曝光
         */
        @Override
        public void onAdExposure() {
            LogUtil.d(TAG, "==> onAdExposure");
        }

        /**
         * 下载确认
         */
        @Override
        public void onConfirm() {
            LogUtil.d(TAG, "==> onConfirm");
        }

        /**
         * 下载取消
         */
        @Override
        public void onCancel() {
            LogUtil.d(TAG, "==> onCancel");
        }
    };

    public void setupNativeSpotAd() {
        //	设置插屏图片类型，默认竖图
        //		//	横图
        //		SpotManager.getInstance(mContext).setImageType(SpotManager
        // .IMAGE_TYPE_HORIZONTAL);
        // 竖图
        SpotManager.getInstance(mContext).setImageType(SpotManager.IMAGE_TYPE_HORIZONTAL);

        // 设置动画类型，默认高级动画
        //		// 无动画
        //		SpotManager.getInstance(mContext).setAnimationType(SpotManager
        //				.ANIMATION_TYPE_NONE);
        //		// 简单动画
        //		SpotManager.getInstance(mContext)
        //		                    .setAnimationType(SpotManager.ANIMATION_TYPE_SIMPLE);
        // 高级动画
        SpotManager.getInstance(mContext).setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);

        // 获取原生插屏控件
        /*View nativeSpotView = SpotManager.getInstance(mContext)
                .getNativeSpot(mContext, new SpotListener() {

                    @Override
                    public void onShowSuccess() {
                        Log.i(TAG,"原生插屏展示成功");
                    }

                    @Override
                    public void onShowFailed(int errorCode) {
                        Log.e(TAG, "原生插屏展示失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                               // showShortToast("网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                //showShortToast("暂无原生插屏广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                                //showShortToast("原生插屏资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                                //showShortToast("请勿频繁展示");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                //showShortToast("请设置插屏为可见状态");
                                break;
                            default:
                               // showShortToast("请稍后再试");
                                break;
                        }
                    }

                    @Override
                    public void onSpotClosed() {
                        //logDebug("原生插屏被关闭");
                    }

                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        //logDebug("原生插屏被点击");
                        //logInfo("是否是网页广告？%s", isWebPage ? "是" : "不是");
                    }
                });
        if (nativeSpotView != null) {
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            if (bannerAdLayout != null) {
                bannerAdLayout.removeAllViews();
                // 添加原生插屏控件到容器中
                bannerAdLayout.addView(nativeSpotView, layoutParams);
                if (bannerAdLayout.getVisibility() != View.VISIBLE) {
                    bannerAdLayout.setVisibility(View.VISIBLE);
                }
            }
        }*/
        BannerManager bannerManager = BannerManager.getInstance(mContext);
        View bannerBiew = bannerManager.getBannerView(mContext, new BannerViewListener(){

            @Override
            public void onRequestSuccess() {
                if (bannerAdLayout.getVisibility() != View.VISIBLE) {
                    bannerAdLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSwitchBanner() {

            }

            @Override
            public void onRequestFailed() {

            }
        });
        if (bannerAdLayout != null) {
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            bannerAdLayout.removeAllViews();
            // 添加原生插屏控件到容器中
            bannerAdLayout.addView(bannerBiew, layoutParams);
        }
    }

    BannerViewListener listener = new BannerViewListener(){

        @Override
        public void onRequestSuccess() {
            LogUtil.d(TAG, "==> onRequestSuccess");
        }

        @Override
        public void onSwitchBanner() {
            LogUtil.d(TAG, "==> onSwitchBanner");
        }

        @Override
        public void onRequestFailed() {
            LogUtil.d(TAG, "==> onRequestFailed");
        }
    };
}