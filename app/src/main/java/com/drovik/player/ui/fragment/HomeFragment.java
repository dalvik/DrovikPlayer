package com.drovik.player.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.audiorecorder.ui.FileExplorerActivity;
import com.android.audiorecorder.ui.SettingsActivity;
import com.android.audiorecorder.ui.SoundRecorder;
import com.android.audiorecorder.ui.activity.LoginActivity;
import com.android.audiorecorder.utils.DateUtil;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.pager.BasePager;
import com.drovik.player.AppApplication;
import com.drovik.player.R;
import com.drovik.player.audio.ui.MusicActivity;
import com.drovik.player.ui.HomeActivity;
import com.drovik.player.weather.BaseRecyclerAdapter;
import com.drovik.player.weather.CityProvider;
import com.drovik.player.weather.event.AirNowEvent;
import com.drovik.player.weather.holder.HourWeatherHolder;
import com.drovik.player.weather.data.HoursForecastData;
import com.drovik.player.weather.ICityResponse;
import com.drovik.player.weather.IWeatherResponse;
import com.drovik.player.weather.event.LocationEvent;
import com.drovik.player.weather.ResourceProvider;
import com.drovik.player.weather.WeatherActivity;
import com.drovik.player.weather.WeatherManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.DataUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends BasePager implements View.OnClickListener, IHomeView {

    private static final int LOGIN_SUCCESS = 1001;
    private static final int UPDATE_WEATHER = 2000;
    public static final int LOAD_CITYS_SUCCESS = 3000;
    private LinearLayout mNoDevice;
    private ImageView gifBg;
    private TextView mDeviceSize;
    private TextView mDeviceName;
    private LinearLayout mOperate;
    private FrameLayout mLoginImg;
    private RelativeLayout mContainer;
    private Context mContext;
    private RelativeLayout mHeader;
    private AnimationDrawable frameAnim;
   // private HomeController mHomeController;
    private String free;
    private String device_total;
   // private ArrayList<Device> devices;
   // private Device mSelectDevice;//最后一个登陆的设备
    //private LoginHandle loginHandle;
    private boolean mLoginSucess;

    private RelativeLayout mNativeSpotAdLayout;
    private SharedPreferences mSettings;
    private RecyclerView mHoursForecastRecyclerView;
    private BaseRecyclerAdapter mHoursForecastAdapter;
    private WeatherManager mWeatherManager;
    private TextView mWeatherLocation;
    private TextView mWeek;
    private TextView mWeatherTemperature;
    private TextView mWeatherInfo;
    private CityProvider mCityProvider;
    private boolean mLoadCitySuccess;
    private String TAG = "HomeFragment";

    private ImageView mHomeMenu;


    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initData();
        initHome();
        return view;
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void reload() {

    }

    private void initData() {
        EventBus.getDefault().register(this);
        mSettings = getActivity().getSharedPreferences(SettingsActivity.class.getName(), MODE_PRIVATE);
        mWeatherManager = new WeatherManager();
        mCityProvider = new CityProvider(getActivity(), mHandler);
        mLoadCitySuccess = false;
        mCityProvider.loadCitys();
        loadWeather();
    }

    private void initView(View view) {
        mContainer = (RelativeLayout) view.findViewById(R.id.home_container);
        mHeader = (RelativeLayout) view.findViewById(R.id.home_header);
        mHomeMenu = (ImageView) view.findViewById(R.id.home_menu);
        mHomeMenu.setOnClickListener(this);
        mLoginImg = (FrameLayout) view.findViewById(R.id.home_login);
        mLoginImg.setOnClickListener(this);
        mDeviceSize = (TextView) view.findViewById(R.id.home_device_size);
        mOperate = (LinearLayout) view.findViewById(R.id.home_operate);
        gifBg = (ImageView) view.findViewById(R.id.home_login_gif_bg);
        frameAnim = new AnimationDrawable();
        // 为AnimationDrawable添加动画帧
        frameAnim.addFrame(getResources().getDrawable(R.drawable.home_body_device_gif_bg_h), 300);
        frameAnim.addFrame(getResources().getDrawable(R.drawable.home_body_device_gif_bg_h), 300);
        // 设置为循环播放
        frameAnim.setOneShot(false);
        gifBg.setBackgroundDrawable(frameAnim);

        mDeviceName = (TextView) view.findViewById(R.id.home_device_name);
        view.findViewById(R.id.home_file).setOnClickListener(this);
        view.findViewById(R.id.home_photo).setOnClickListener(this);
        view.findViewById(R.id.home_video).setOnClickListener(this);
        //view.findViewById(R.id.home_video).setVisibility(View.GONE);
        view.findViewById(R.id.home_music).setOnClickListener(this);
        view.findViewById(R.id.home_recorder).setOnClickListener(this);
        view.findViewById(R.id.home_sur).setOnClickListener(this);

        mNoDevice = (LinearLayout) view.findViewById(R.id.home_no_device);
        mNoDevice.setOnClickListener(this);
        mNativeSpotAdLayout = (RelativeLayout) view.findViewById(R.id.home_rl_native_spot_ad);

        mWeatherLocation = (TextView) view.findViewById(R.id.weather_location);
        mWeatherLocation.setOnClickListener(this);
        mWeek = (TextView) view.findViewById(R.id.weather_week);
        mWeek.setOnClickListener(this);
        mWeatherTemperature = (TextView) view.findViewById(R.id.weather_temperature);
        mWeatherTemperature.setOnClickListener(this);
        mWeatherInfo = (TextView) view.findViewById(R.id.weather_info);
        mWeatherInfo.setOnClickListener(this);
        mHoursForecastRecyclerView = (RecyclerView) view.findViewById(R.id.home_hours_forecast_recyclerView);
        mHoursForecastRecyclerView.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHoursForecastRecyclerView.setLayoutManager(linearLayoutManager);
        mHoursForecastAdapter = new BaseRecyclerAdapter(getActivity());
        mHoursForecastAdapter.registerHolder(HourWeatherHolder.class, R.layout.item_weather_hour_forecast);
        mHoursForecastRecyclerView.setAdapter(mHoursForecastAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        /*if (UIUtility.isFastDoubleClick()) {
            return;
        }*/
        switch (v.getId()) {
            case R.id.home_menu:
                HomeActivity activity = (HomeActivity) getActivity();
                activity.openLeftMenu();
                break;
            case R.id.home_file:
                Intent intentFile = new Intent(mContext, FileExplorerActivity.class);
                startActivity(intentFile);
                break;
            case R.id.home_photo:
                Intent intentPhoto = new Intent(mContext, SoundRecorder.class);
                startActivity(intentPhoto);
                break;
            case R.id.home_video:
                Intent intentVideo = new Intent(mContext, com.drovik.player.video.ui.MovieHomeActivity.class);
                startActivity(intentVideo);
                break;
            case R.id.home_music:
                Intent intentMusic = new Intent(mContext, MusicActivity.class);
                startActivity(intentMusic);
                break;
            case R.id.home_recorder:
                Intent intentRecord = new Intent(mContext, SoundRecorder.class);
                startActivity(intentRecord);
                break;
            /*case R.id.home_sur:
                break;
            case R.id.home_no_device:
                LogUtil.d(HomeFragment.class, "home_no_device is enter");
                Intent intentDownload = new Intent(mContext, ScanSerialActivity.class);
                startActivity(intentDownload);
                break;*/
            case R.id.home_login:
                Intent loginActivity = new Intent(mContext, LoginActivity.class);
                startActivity(loginActivity);
                /*if (!mLoginSucess) {
                    startLoginAnim();
                    autoLogin();
                }*/
                break;
            case R.id.weather_location:
                launchWeatherActivity(0);
                break;
            case R.id.weather_week:
            case R.id.weather_temperature:
            case R.id.home_hours_forecast_recyclerView:
            case R.id.weather_info:
                String postTime = mSettings.getString(ResourceProvider.PUBLISH_TIME, "");
                if(!android.text.TextUtils.isEmpty(postTime)) {
                    launchWeatherActivity(1);
                }
                break;
                default:
                    break;
        }
    }

    @Override
    public void showDeviceFree(int available, int total) {
        if (available > 1024) {
            free = (float) (available / 1024) + "GB";
        } else {
            free = available + "MB";
        }
        if (total > 1024) {
            device_total = (float) (total / 1024) + "GB";
        } else {
            device_total = total + "MB";
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationEvent(LocationEvent locationEvent) {
        LogUtil.d(TAG, "==> onLocationEvent : " + locationEvent.getCity());
        if(!TextUtils.isEmpty(locationEvent.getCity())) {
            mWeatherManager.topCity("cn", "9");
            String location = mSettings.getString(ResourceProvider.LOCATION, "");
            if(!TextUtils.isEmpty(location)) {//初次加载
                if(location.equalsIgnoreCase(locationEvent.getCity())) {//同一个城市
                    long delt = DateUtil.deltPublish(mSettings.getString(ResourceProvider.PUBLISH_TIME, "2018-11-30 21:20:12"));
                    LogUtil.d(TAG, "==> onLocationEvent delt: " + delt);
                    if(delt >= ResourceProvider.DELT_DURATION) {//间隔不小于50分钟
                        getWeather(locationEvent.getCity());
                    }
                } else {
                    getWeather(locationEvent.getCity());
                }
            } else {
                getWeather(locationEvent.getCity());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTopCity(ICityResponse weatherResponse) {
        ArrayList<ICityResponse.Data> data =  weatherResponse.getHeWeather6();
        if(data != null && data.size()>0) {
            for(ICityResponse.Data temp:data) {
                ArrayList<ICityResponse.Data.BasicData> basics = temp.getBasic();
                JSONArray jsonArray = new JSONArray();
                for(ICityResponse.Data.BasicData basicData:basics) {
                    LogUtil.d(TAG, "==> cityBasic: " + basicData.toJSONString());
                    try {
                        JSONObject jsonItem = new JSONObject(basicData.toJSONString());
                        jsonArray.put(jsonItem);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mSettings.edit().putString(ResourceProvider.TOP_CITY_JSON, jsonArray.toString()).apply();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWeatherEvent(IWeatherResponse weatherResponse) {
        ArrayList<IWeatherResponse.Data> data =  weatherResponse.getHeWeather6();
        if(data != null && data.size()>0) {
            for(IWeatherResponse.Data temp:data) {
                ArrayList<IWeatherResponse.Data.DailyForecast> dailyForecasts = temp.getDaily_forecast();
                JSONArray dailyJonArray = new JSONArray();
                for(IWeatherResponse.Data.DailyForecast dailyForecast: dailyForecasts) {
                    dailyJonArray.put(dailyForecast.toJsonObject());
                }
                mSettings.edit().putString(ResourceProvider.PUBLISH_TIME, temp.getUpdate().getLoc())
                        .putString(ResourceProvider.DAILY_FORECAST, dailyJonArray.toString()).apply();
                ArrayList<IWeatherResponse.Data.LifeStyle> lifeStyle = temp.getLifestyle();
                JSONArray lifeStyleJonArray = new JSONArray();
                for(IWeatherResponse.Data.LifeStyle styleForecast: lifeStyle) {
                    lifeStyleJonArray.put(styleForecast.toJsonObject());
                }
                mSettings.edit().putString(ResourceProvider.LIFE_STYLE, lifeStyleJonArray.toString()).apply();
                List<IWeatherResponse.Data.Hourly> hourlyForecasts = temp.getHourly();
                JSONArray hourlyJsonArray = new JSONArray();
                for(IWeatherResponse.Data.Hourly hourly:hourlyForecasts) {
                    hourlyJsonArray.put(hourly.getJSONObject());
                }
                mSettings.edit().putString(ResourceProvider.HOURLY_FORECAST, hourlyJsonArray.toString()).apply();
            }
            Message msg = mHandler.obtainMessage(UPDATE_WEATHER, data);
            mHandler.sendMessage(msg);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAqiEvent(AirNowEvent aqiEvent) {
        IWeatherResponse weatherResponse = aqiEvent.getIWeatherResponse();
        if(weatherResponse != null) {
            ArrayList<IWeatherResponse.Data> data =  weatherResponse.getHeWeather6();
            if(data != null && data.size()>0) {
                for(IWeatherResponse.Data temp:data) {
                    IWeatherResponse.Data.AirNowCity airNowCity = temp.getAir_now_city();
                    if(airNowCity != null) {
                        mSettings.edit().putString(ResourceProvider.AIR_NOW_CITY, airNowCity.getJsonObject().toString())
                                .putString(ResourceProvider.QLTY, airNowCity.getQlty()).apply();
                    }
                    ArrayList<IWeatherResponse.Data.AirNowStation> stationList =  temp.getAir_now_station();
                    if(stationList != null) {
                        JSONArray array = new JSONArray();
                        for(IWeatherResponse.Data.AirNowStation station: stationList) {
                            JSONObject object = station.getJsonObject();
                            array.put(object);
                        }
                        mSettings.edit().putString(ResourceProvider.AIR_NOW_STATION, array.toString()).apply();
                    }
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    stopLoginAnim();
                    break;
                case UPDATE_WEATHER:
                    ArrayList<IWeatherResponse.Data> weatherInfoList = (ArrayList<IWeatherResponse.Data>) msg.obj;
                    for(IWeatherResponse.Data data:weatherInfoList) {
                        IWeatherResponse.Data.Now nowWeather = data.getNow();
                        IWeatherResponse.Data.Basic basic = data.getBasic();
                        mSettings.edit().putString(ResourceProvider.ADMIN_AREA, basic.getAdmin_area()).putString(ResourceProvider.LOCATION, basic.getLocation())
                                .putString(ResourceProvider.WEEK, DateUtil.getWeek(DateUtil.getMopnthDay())).putString(ResourceProvider.TMP, nowWeather.getTmp()).putString(ResourceProvider.COND_TXT, nowWeather.getCond_txt())
                                .putString(ResourceProvider.WIND_DIR, nowWeather.getWind_dir()).putString(ResourceProvider.WIND_SC, nowWeather.getWind_sc()).apply();
                        loadWeather();
                    }
                    break;
                case LOAD_CITYS_SUCCESS:
                    mLoadCitySuccess = true;
                    break;
                    default:
                        break;
            }
        }
    };

    private void initHome() {
        LogUtil.d(TAG, "initHome LoginHandle:" + AppApplication.getInstance());
        /*if (devices != null && devices.size() > 0) {
            LogUtil.d(HomeFragment.class, " devices.size():" + devices.size() + "--" + NasApplication.getInstance().getLoginHandle());
            if (NasApplication.getInstance().getLoginHandle() != null && NasApplication.getInstance().getLoginHandle().handle != 0) {
                showDevice(true, true);
            } else {
                showDevice(true, false);
                if (NasApplication.getInstance().isFirstInHome()) {
                    autoLogin();
                    NasApplication.getInstance().setFirstInHome(false);
                }
            }
        } else {
            showDevice(false, false);
        }*/

    }

    private void loadWeather() {
        String city = mSettings.getString(ResourceProvider.ADMIN_AREA, "");
        String country = mSettings.getString(ResourceProvider.LOCATION, "");
        if(city.equalsIgnoreCase(country)) {
            mWeatherLocation.setText(getResources().getString(R.string.china) + " • " + country);
        } else {
            mWeatherLocation.setText(city + " • " + country);
        }
        mWeek.setText(mSettings.getString(ResourceProvider.WEEK, ""));
        mWeatherTemperature.setText(mSettings.getString(ResourceProvider.TMP, "") + "°");
        mWeatherInfo.setText(getResources().getString(R.string.weather_status_info, mSettings.getString(ResourceProvider.WEEK, ""),
                mSettings.getString(ResourceProvider.COND_TXT, ""), mSettings.getString(ResourceProvider.WIND_DIR, ""), mSettings.getString(ResourceProvider.WIND_SC, ""),
                mSettings.getString(ResourceProvider.QLTY, "")));
        mWeatherInfo.setVisibility(View.VISIBLE);
        mWeek.setVisibility(View.GONE);
        String hourlyJson = mSettings.getString(ResourceProvider.HOURLY_FORECAST, "");
        if(!android.text.TextUtils.isEmpty(hourlyJson)) {
            try {
                JSONArray jsonArrayArray = new JSONArray(hourlyJson);
                int length = jsonArrayArray.length();
                List<HoursForecastData> hoursForecastDataList = new ArrayList<HoursForecastData>();
                for(int i=0; i<length; i++) {
                    JSONObject jsonObject = jsonArrayArray.getJSONObject(i);
                    IWeatherResponse.Data.Hourly hourly = new IWeatherResponse.Data.Hourly(jsonObject.toString());
                    HoursForecastData hoursForecastData = new HoursForecastData(hourly);
                    hoursForecastDataList.add(hoursForecastData);
                }
                mHoursForecastAdapter.setData(hoursForecastDataList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void autoLogin() {
        startLoginAnim();
         /*if (devices != null) {
            mSelectDevice = devices.get(devices.size() - 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                   LoginModule.instance().logOutAll();
                    loginHandle = LoginModule.instance().getLoginHandle(mSelectDevice);
                    Message msg = mHandler.obtainMessage();
                    msg.what = LOGIN_SUCCESS;
                    msg.sendToTarget();
                }
            }).start();
        }*/
    }

    private void showDevice(boolean isShow, boolean loginSucess) {
        /*LogUtil.d(HomeFragment.class, "showDevice isShow:" + isShow + "--loginSucess:" + loginSucess);
        if (isShow) {
            mNoDevice.setVisibility(View.GONE);
            mLoginImg.setVisibility(View.VISIBLE);
            mDeviceSize.setVisibility(View.VISIBLE);
            mOperate.setVisibility(View.VISIBLE);
            mContainer.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.home_bg));
            mLoginSucess = loginSucess;
            if (loginSucess) {
                gifBg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.home_body_device_gif_bg_h));
            } else {
                gifBg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.home_body_device_gif_bg_n));
                mDeviceSize.setText(mContext.getResources().getText(R.string.offline_tip));
            }
            mHomeMenu.setBackgroundDrawable(mContext.getApplicationContext().getResources().getDrawable(R.drawable.home_menu_selector));
        } else {
            mNoDevice.setVisibility(View.VISIBLE);
            mLoginImg.setVisibility(View.GONE);
            mDeviceSize.setVisibility(View.GONE);
            mOperate.setVisibility(View.GONE);
            mDeviceName.setTextColor(mContext.getResources().getColor(R.color.common_title));
            mDeviceName.setText(mContext.getResources().getText(R.string.home));
            mContainer.setBackgroundColor(mContext.getResources().getColor(R.color.common_bg));
            mHeader.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            //mContext.getApplicationContext().getResources().getDrawable(R.drawable.common_menu_selector)
            mHomeMenu.setBackgroundDrawable(mContext.getApplicationContext().getResources().getDrawable(R.drawable.common_menu_selector));
        }*/

    }

    protected void startLoginAnim() {
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.start();
            //mDeviceSize.setText(mContext.getResources().getText(R.string.logining_device));
        }
    }

    protected void stopLoginAnim() {
        if (frameAnim != null && frameAnim.isRunning()) {
            frameAnim.stop();
        }
    }

    private void getWeather(String city) {
        mWeatherManager.weather(city);
        mWeatherManager.getAirNow(city);
    }

    private void launchWeatherActivity(int type) {
        Intent weatherActivity = new Intent(mContext, WeatherActivity.class);
        if(type ==0) {
            weatherActivity.putExtra(ResourceProvider.TOP_CITY_JSON, mSettings.getString(ResourceProvider.TOP_CITY_JSON,""));
            weatherActivity.putParcelableArrayListExtra(ResourceProvider.CITY_DATA, mCityProvider.getCitys());
        }
        weatherActivity.putExtra(ResourceProvider.TYPE, type);
        startActivity(weatherActivity);
    }
}
