package com.drovik.player.weather;

import com.android.library.utils.TextUtils;
import com.drovik.player.R;

import java.util.HashMap;
import java.util.Map;

public class ResourceProvider {

    private ResourceProvider() {
    }

    //SharedPreferences KEY
    public static final String ALARM_ALLOW = "ALARM_ALLOW";
    public static final String NOTIFICATION_ALLOW = "NOTIFICATION_ALLOW";
    public static final String NOTIFICATION_THEME = "NOTIFICATION_THEME";
    public static final String POLLING_TIME = "POLLING_TIME";


    private static Map<String, Integer> sWeatherIcons = new HashMap<>();

    private static final long[] SCHEDULES = {30 * 60, 60 * 60, 3 * 60 * 60, 0};
    private static final String[] SUNNY = {"晴", "多云"};
    private static final String[] WEATHERS = {"阴", "晴", "多云", "大雨", "雨", "雪", "风", "雾霾", "雨夹雪"};
    private static final String[] WEEK = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
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

    public static String getWeek(int dayOfWeek) {
        if(dayOfWeek>=0 && dayOfWeek<WEATHERS.length) {
            return WEEK[dayOfWeek];
        }
        return WEEK[0];
    }

}
