package com.android.library.utils;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil extends DateUtils {

    protected final static long MINUTE = 60 * 1000;
    protected final static long HOUR = 60 * MINUTE;
    protected final static long DAY = 24 * HOUR;


    /**
     * 获取格式化后的时间字符串<br/>
     * yyyy-MM-dd EE HH:mm
     */
    public static String formatYMDEHM(Date date) {
        return getFormatDate(date, "yyyy-MM-dd EE HH:mm");
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
     * 小时
     * @param date
     * @return
     */
    public static int getHour(Date date){
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

    public static String getDateString(long date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int dateYear = calendar.get(Calendar.YEAR);
        int dateMonth = calendar.get(Calendar.MONTH)+1;
        int dateWeek = calendar.get(Calendar.WEEK_OF_MONTH);
        int dateDay = calendar.get(Calendar.DAY_OF_WEEK);
        Calendar now = Calendar.getInstance();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH)+1;
        int nowWeek = now.get(Calendar.WEEK_OF_MONTH);
        int nowDay = now.get(Calendar.DAY_OF_WEEK);
        int deltYear = nowYear - dateYear;
        if(deltYear == 0){//同一年
            int deltMonth = nowMonth - dateMonth;
            if(deltMonth == 0){//同一月
                int deltWeek = nowWeek - dateWeek;
                if(deltWeek == 0){//同一周
                    int deltDay = nowDay - dateDay;
                    if(deltDay == 0){//同一天
                        return "今天";
                    } else if(deltDay == 1){
                        return "昨天";
                    } else if(deltDay == 2){
                        return "前天";
                    } else {
                        return dateDay + "天前";
                    }
                } else{
                    return deltWeek + "周前";  
                }
            } else {
                return deltMonth + "月前";    
            }
        } else {
            return deltYear + "年前";
        }
    }

    public static String paserTimeToYMD(long time) {
        /*System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(new Date(time * 1000L));*/
        Date date  = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }

    /**
     * 毫秒转时分秒
     *
     * @param duration
     * @return
     */
    public static String timeParse(long duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }
}
