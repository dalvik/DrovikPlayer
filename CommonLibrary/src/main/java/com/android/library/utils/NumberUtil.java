package com.android.library.utils;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtil {
    private static final int DEFAULT_VALUE = 0;

    /**
     * 转换成 整数
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static int convertString(String value, int defaultValue) {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long convertLongString(String value, int defaultValue) {
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换成 整数
     *
     * @param value
     * @return
     */
    public static int convertString2Int(String value) {
        return convertString(value, DEFAULT_VALUE);
    }

    public static long convertString2Long(String value) {
        return convertLongString(value, DEFAULT_VALUE);
    }

    public static double convertDouble(String value) {
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            return DEFAULT_VALUE;
        }
    }

    public static boolean isSerailNumber(String value) {
        if (!value.matches("\\d{6,10}")) {
            return false;
        }
        boolean continuity = true;
        String[] data = value.split("");
        for (int i = 0; i < data.length - 1; i++) {
            int a = Integer.parseInt(data[i]);
            int b = Integer.parseInt(data[i + 1]);
            continuity = continuity && (a + 1 == b || a - 1 == b);
        }
        return continuity;
    }

    public static boolean isSameNumber(String value) {
        if (value.matches("\\d{6,20}")) {
            String[] values = value.split("");
            for (int i = 0; i < value.length() - 1; i++) {
                if (Integer.valueOf(values[i]) != Integer.valueOf(values[i + 1])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 手机号验证
     *
     * @param mobile
     * @return 验证通过返回true
     */
    public static boolean isMobile(String mobile) {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号;
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 将seconds时长切换成mm:ss
     *
     * @param seconds
     * @return
     */
    public static String timeIntMToMM(@NonNull int seconds) {
        String result = (seconds / 60 >= 10 ? seconds / 60 : "0" + seconds / 60) + ":" + (seconds % 60 >= 10 ? seconds % 60 : "0" + seconds % 60);
        return result;
    }
}
