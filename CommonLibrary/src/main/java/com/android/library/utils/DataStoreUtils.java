package com.android.library.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.library.BaseApplication;

public class DataStoreUtils {
    public static final String FILE_NAME = "drovik.db";
    public static final String DEFAULT_VALUE = "";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    /**
     * Session id
     */
    public static final String SID = "a";

    /*****************************/
    /**
     * 用户退出
     */
    public static final String LOGIN_EXIT = "LOGIN_EXIT";
    /**
     * 1:登入状态
     */
    public static final String LOGIN_STATUS_ENTRY = "1";
    /**
     * 2:退出状态
     */
    public static final String LOGIN_STATUS_LOGOUT = "2";
    /**
     * 当前版本
     */
    public static final String VERSION_CODE = "version_code";

    public static final String IS_LAUNCH = "is_launch";
    /**
     * 每天执行一次
     */
    public static final String Once_In_Day = "oid";

    public static SharedPreferences share;

    /**
     * 保存本地信息
     */
    public static void saveLocalInfo(String name, String value) {
        if (share == null) {
            share = BaseApplication.curContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        if(TextUtils.isEmpty(value)){
            value = "";
        }
        share.edit().putString(name, value).commit();
    }

    /**
     * 读取本地信息
     */
    public static String readLocalInfo(String name) {
        if (share == null) {
            share = BaseApplication.curContext.getSharedPreferences(FILE_NAME, 0);
        }
        return share.getString(name, DEFAULT_VALUE);
    }

}
