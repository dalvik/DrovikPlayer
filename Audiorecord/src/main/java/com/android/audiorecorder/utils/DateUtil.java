package com.android.audiorecorder.utils;


import android.text.format.DateUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil extends DateUtils {

    private static SimpleDateFormat MONTH_DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat WEEK = new SimpleDateFormat("EEEE");

    public static String formatyyyyMMDDHHmmss(long time){
        Date date  = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

	public static String formatyyMMDDHHmmss(long time){
		Date date  = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(date);
	}
	
	public static String formatyyMMDDHHmm(long time){
		Date date  = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date);
	}
	
	public static String formatyyMMDDHHmm2(long time){
        Date date  = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }
	
	public static String formatMMDD(long time){
        Date date  = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        return sdf.format(date);
    }
	
	public static String getYearMonthWeek(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONDAY)+1;
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        return String.valueOf(year) + File.separator + String.valueOf(month) + File.separator + String.valueOf(week);
    }
	
	/**
     * 获取格式化后的时间字符串<br/>
     * MM-dd EE HH:mm
     */
    public static String formatMDEHM(Date date) {
        return getFormatDate(date, "MM-dd EE HH:mm");
    }

    /**
     * 获取格式化后的时间字符串<br/>
     * EE MM-dd
     */
    public static String formatEMD(Date date) {
        return getFormatDate(date, "EE MM-dd");
    }

    /**
     * 获取格式化后的时间字符串<br/>
     * yyyy-MM-dd HH:mm
     */
    public static String formatYMDHM(Date date) {
        return getFormatDate(date, "yyyy-MM-dd HH:mm");
    }

    /**
     * 获取格式化后的时间字符串<br/>
     * yyyy-MM-dd HH:mm:ss
     */
    public static String formatYMDHms(Date date) {
        return getFormatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取格式化后的时间字符串<br/>
     * yyyy-MM-dd HH:mm:ss.SSSZ
     */
    public static String formatYMDHmsSZ(Date date) {
        return getFormatDate(date, "yyyy-MM-dd HH:mm:ss.SSSZ");
    }

    public static String formatYMDHmsSZ4FileName(Date date) {
        return getFormatDate(date, "yyyy-MM-dd HH-mm-ssSSSZ");
    }

    /**
     * 获取格式化后的时间字符串<br/>
     * yyyy-MM-dd
     */
    public static String formatYMD(Date date) {
        return getFormatDate(date, "yyyy-MM-dd");
    }

    /**
     * 获取格式化后的时间字符串<br/>
     * EE HH:mm
     */
    public static String formatEHM(Date date) {
        return getFormatDate(date, "EE HH:mm");
    }

    /**
     * 获取格式化后的时间字符串<br/>
     * HH:mm
     */
    public static String formatHM(Date date) {
        return getFormatDate(date, "HH:mm");
    }

    /**
     * 获取时间
     *
     * @param date
     * @param template
     * @return
     */
    public static String getFormatDate(Date date, String template) {
        SimpleDateFormat formatter = new SimpleDateFormat(template, Locale.SIMPLIFIED_CHINESE);
        String strDate = formatter.format(date);
        return strDate;
    }

    /**
     * YMD 时间
     *
     * @param ymd
     * @return
     */
    public static Date getDateByYMD(String ymd) {
        return getDate(ymd, "yyyy-MM-dd");
    }

    /**
     * YMD 时间
     *
     * @param ymdhms
     * @return
     */
    public static Date getDateByYMDHMS(String ymdhms) {
        return getDate(ymdhms, "yyyy-MM-ddHH:mm:ss");
    }

    public static Date getDateByYMDHMS(String ymdhms, String format) {
        return getDate(ymdhms, format);
    }

    /**
     * 转化时间
     *
     * @param date
     * @param template
     * @return
     */
    public static Date getDate(String date, String template) {
        SimpleDateFormat formatter = new SimpleDateFormat(template, Locale.SIMPLIFIED_CHINESE);
        try {
            return formatter.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 小时
     *
     * @param date
     * @return
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 是否同一天
     *
     * @param cal1
     * @param cal2
     * @return
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if ((cal1 == null) || (cal2 == null)) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)) && (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) && (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static String getMopnthDay() {
        return MONTH_DAY_FORMAT.format(new Date());
    }

    public static String getWeek(String dataStr) {
        try {
            return WEEK.format(MONTH_DAY_FORMAT.parse(dataStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
