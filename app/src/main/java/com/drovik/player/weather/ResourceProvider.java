package com.drovik.player.weather;

import android.content.Context;

import com.android.library.utils.TextUtils;
import com.drovik.player.R;

import java.util.HashMap;
import java.util.Map;

public class ResourceProvider {

    private ResourceProvider() {
    }


    public static final int DELT_DURATION = 50 * 60;//50minute
    //SharedPreferences KEY
    public static final String ALARM_ALLOW = "ALARM_ALLOW";
    public static final String NOTIFICATION_ALLOW = "NOTIFICATION_ALLOW";
    public static final String NOTIFICATION_THEME = "NOTIFICATION_THEME";

    public static final String PUBLISH_TIME = "publish_time";
    public static final String ADMIN_AREA = "admin_area";
    public static final String LOCATION = "location";
    public static final String WEEK = "week";
    public static final String TMP = "tmp";
    public static final String COND_TXT = "cond_txt";
    public static final String WIND_DIR = "wind_dir";
    public static final String WIND_SC = "wind_sc";
    public static final String TOP_CITY_JSON = "top_city_json";
    public static final String CITY_DATA = "city_data";
    public static final String TYPE = "type";
    public static final String HOURLY_FORECAST = "hourly_forecast";
    public static final String DAILY_FORECAST = "daily_forecast";
    public static final String LIFE_STYLE = "life_style";
    public static final String QLTY = "qlty";
    public static final String AIR_NOW_CITY = "air_now_city";
    public static final String AIR_NOW_STATION = "air_now_station";

    private static Map<String, Integer> sWeatherIcons = new HashMap<>();

    private static final long[] SCHEDULES = {30 * 60, 60 * 60, 3 * 60 * 60, 0};
    private static final String[] SUNNY = {"晴", "多云"};
    private static final String[] WEATHERS = {"阴", "晴", "多云", "大雨", "雨", "雪", "风", "雾霾", "雨夹雪"};
    private static final int[] ICONS_ID = {R.mipmap.core_weather_clouds, R.mipmap.core_weather_sunny, R.mipmap.core_weather_few_clouds, R.mipmap.core_weather_big_rain, R.mipmap.core_weather_rain,
            R.mipmap.core_weather_snow, R.mipmap.core_weather_wind, R.mipmap.core_weather_haze, R.mipmap.core_weather_rain_snow};

    static {
        for (int index = 0; index < WEATHERS.length; index++) {
            sWeatherIcons.put(WEATHERS[index], ICONS_ID[index]);
        }
    }

    public static long getSchedule(int which) {
        return SCHEDULES[which];
    }

    public static boolean sunny(String weather) {
        for (String weatherKey : SUNNY) {
            if (weatherKey.contains(weather) || weather.contains(weatherKey)) {
                return true;
            }
        }
        return false;
    }

    public static int getIconId(String weather) {
        if (TextUtils.isEmpty(weather)) {
            return R.mipmap.core_weather_none_available;
        }

        if (sWeatherIcons.get(weather) != null) {
            return sWeatherIcons.get(weather);
        }

        for (String weatherKey : sWeatherIcons.keySet()) {
            if (weatherKey.contains(weather) || weather.contains(weatherKey)) {
                return sWeatherIcons.get(weatherKey);
            }
        }

        return R.mipmap.core_weather_none_available;
    }

    public static int getResource(Context context, String imageName) {
        int resId = context.getResources().getIdentifier(imageName, "mipmap", context.getPackageName());
        if(resId == 0) {
            resId = R.mipmap.core_weather_none_available;
        }
        return resId;
    }
}