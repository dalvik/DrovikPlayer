package com.android.library.net.utils;

import android.util.Log;


/**
 * 统一的打印日志入口，便于后面维护
 */
public class LogUtil {
    // 日志开关，默认打开
    public static boolean isDebug = true;

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isDebug)
            Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void info(String msg) {
        if (isDebug)
            Log.e("LogUtil", msg);
    }
}
