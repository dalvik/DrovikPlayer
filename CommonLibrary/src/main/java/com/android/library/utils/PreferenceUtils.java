package com.android.library.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    private static String mSharePreferName = "HFileStation";
    private static SharedPreferences mSharedPreferences = null;
    private boolean isRememberPassword = false;

    public final static String REMEMBER_PASSWORD = "RememberPassword";//是否记住密码

    public static final String INDEX = "index";
    public static final String WIFI = "wifi";
    public static final String AUTO_LOGIN = "auto_login";
    public static final String INIT = "init";

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        mSharedPreferences = context.getSharedPreferences(mSharePreferName, 0);
    }

    /**
     * 获取sp对象
     *
     * @return
     */
    public static SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public static boolean getRemember(String remember) {
        return mSharedPreferences.getBoolean(remember, true);
    }

    public static void putRemember(String remember, boolean isRememberPassword) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(remember, isRememberPassword);
        edit.commit();
    }


    public static String getRememberPassword(String ip) {
        return mSharedPreferences.getString(ip, null);
    }

    public static void deleteRememberPassword(String ip) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.remove(ip);
        edit.commit();
    }

    public static void putRememberPassword(String ip, String password) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(ip.trim(), password.trim());
        edit.commit();
    }

    public static int getFloderSize(String index) {
        return mSharedPreferences.getInt(index, 0);
    }

    public static void putFloderSize(String index, int i) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(index, i);
        edit.commit();
    }

    public static boolean getWifiOpen(String wifiOpen) {
        return mSharedPreferences.getBoolean(wifiOpen, false);
    }

    public static void putWifiState(String wifiOpen, boolean open) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(wifiOpen, open);
        edit.commit();
    }


    public static boolean getAutoLogin(String autoLogin) {
        return mSharedPreferences.getBoolean(autoLogin, false);
    }

    public static void putAutoLogin(String autoLogin, boolean auto) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(autoLogin, auto);
        edit.commit();
    }

    public static boolean isInit() {
        return mSharedPreferences.getBoolean(INIT, false);
    }

    public static void setInit() {
        mSharedPreferences.edit().putBoolean(INIT, true).apply();
    }
}

