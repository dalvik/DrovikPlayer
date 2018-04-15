package com.android.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;

import java.io.IOException;
import java.io.InputStream;

public class SharedPreferencesUtil {
    // ---
    public static final String FILE_NAME = "wqk";
    /**
     * 通用 true
     */
    public static final String SP_TRUE = "true";
    /**
     * 通用 false
     */
    public static final String SP_FALSE = "false";

    public static final String DEFAULT_VALUE = "";

    private static SharedPreferences share;

    // 保存本地信息
    public static void saveLocalInfo(Context context, String name, String value) {
        if (share == null) {
            share = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        if (share != null) {
            share.edit().putString(name, value).commit();
        }
        share = null;
    }

    // 读取本地信息
    public static String readLocalInfo(Context context, String name) {
        if (share == null) {
            share = context.getSharedPreferences(FILE_NAME, 0);
        }
        if (share != null) {
            return share.getString(name, DEFAULT_VALUE);
        }
        share = null;
        return DEFAULT_VALUE;
    }

    /**
     * 从asserts 目录下读取图片文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static BitmapDrawable readImgFromAssert(Context context, String imgFileName) {
        InputStream inputStream = null;
        BitmapDrawable drawable = null;

        if (null == imgFileName) {
            return null;
        }

        try {
            inputStream = context.getResources().getAssets().open(imgFileName);
            drawable = new BitmapDrawable(context.getResources(), inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return drawable;
    }
}
