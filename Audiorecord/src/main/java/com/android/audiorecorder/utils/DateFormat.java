package com.android.audiorecorder.utils;

import com.android.library.BaseApplication;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Created by Sean.xie on 2015/11/19.
 */
public class DateFormat extends DateUtil {

    // 星座数据
    public static final String[] constellationArr = {"水瓶", "双鱼", "牧羊", "金牛", "双子", "巨蟹", "狮子", "处女", "天秤", "天蝎", "射手", "魔羯"};
    public static final int[] constellationEdgeDay = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};

    public static String getTreatListDate(long time) {
        String result;
        // 判读是否过期
        Calendar realTime = Calendar.getInstance();
        realTime.setTime(new Date(BaseApplication.getRealTime()));
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        String hm = formatHM(new Date(time));
        int realDay = realTime.get(Calendar.DAY_OF_MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        switch (day - realDay) {
            case 0:
                result = "今天 " + hm;
                break;
            case 1:
                result = "明天 " + hm;
                break;
            case 2:
                result = "后天 " + hm;
                break;
            default:
                result = formatMDEHM(new Date(time));
                break;
        }

        return result;
    }

    public static String getCenterCountListDate(long time) {
        String result = "";
        Calendar realTime = Calendar.getInstance();
        realTime.setTime(new Date(BaseApplication.getRealTime()));
        Calendar date = Calendar.getInstance();
        date.setTime(new Date(time));
        String hm = formatHM(new Date(time));
        int realDay = realTime.get(Calendar.DAY_OF_MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        switch (realDay - day) {
            case 0:
                result = "今天 " + hm;
                break;
            case 1:
                result = "昨天 " + hm;
                break;
            default:
                result = formatEMD(new Date(time));
                break;
        }

        return result;
    }


    /**
     * 多长时间后开始活动
     *
     * @param date
     * @return
     */
    public static String getRemainTime(long date) {
        return formatTime("已开始", date, "后");
    }

    public static String getRemainTimeNoSuffix(long date) {
        return formatTime("已开始", date, "");
    }

    /**
     * 根据时间返回 分钟 半小时 几个小时
     */
    public static String getLastLoginTime(long lastLoginTime) {
        String defaultStr = "刚刚";
        String suffix = "前";
        if (lastLoginTime == 0) {
            return defaultStr;
        }
        long sub = BaseApplication.getRealTime() - lastLoginTime;
        if (sub < HOUR_IN_MILLIS) {
            sub = sub / MINUTE_IN_MILLIS;
            if (sub <= 0) {
                return defaultStr;
            } else {
                return sub + "分钟" + suffix;
            }
        } else if (sub < DAY_IN_MILLIS) {
            long hour = sub / HOUR_IN_MILLIS;
            return hour + "小时" + suffix;
        } else {
            sub = sub / DAY_IN_MILLIS;
            if (sub > 30) {
                sub = 30;
            }
            return sub + "天" + suffix;
        }
    }

    /**
     * 格式化时间
     *
     * @param defaultStr
     * @param time
     * @param suffix
     * @return
     */
    public static String formatTime(String defaultStr, long time, String suffix) {
        if (time == 0) {
            return defaultStr;
        }
        long sub = time - BaseApplication.getRealTime();
        if (sub <= 0) {
            return defaultStr;
        } else if (sub < MINUTE_IN_MILLIS) {
            sub = sub / SECOND_IN_MILLIS;
            if (sub == 0) {
                return defaultStr;
            } else {
                return sub + "秒" + suffix;
            }
        } else if (sub < HOUR_IN_MILLIS) {
            sub = sub / MINUTE_IN_MILLIS;
            return sub + "分钟" + suffix;
        } else if (sub < DAY_IN_MILLIS) {
            long hour = sub / HOUR_IN_MILLIS;
            long minite = sub % HOUR_IN_MILLIS / MINUTE_IN_MILLIS;
            if (minite > 0) {
                return hour + "小时" + minite + "分钟" + suffix;
            } else {
                return hour + "小时" + suffix;
            }
        } else if (sub < WEEK_IN_MILLIS) {
            sub = sub / DAY_IN_MILLIS;
            return sub + "天" + suffix;
        } else if (sub < YEAR_IN_MILLIS) {
            sub = sub / WEEK_IN_MILLIS;
            return sub + "周" + suffix;
        } else {
            sub = sub / YEAR_IN_MILLIS;
            return sub + "年" + suffix;
        }
    }

    /**
     * 根据日期获取星座
     *
     * @param date
     * @return
     */
    public static String getStarSign(Date date) {
        Calendar time = Calendar.getInstance();
        time.setTime(date);
        int month = time.get(Calendar.MONTH);
        int day = time.get(Calendar.DAY_OF_MONTH);
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        return constellationArr[11];
    }

    /**
     * 根据日期获取星座图片
     *
     * @param date
     * @return
     */
    public static int getStarSignIndex(Date date) {

        Calendar time = Calendar.getInstance();
        time.setTime(date);
        int month = time.get(Calendar.MONTH);
        int day = time.get(Calendar.DAY_OF_MONTH);
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return month;
        }
        return 11;
    }

    /**
     * 根据日期返回年龄
     *
     * @param birthday
     * @return
     */
    public static int getAge(Date birthday) {
        if (birthday == null) {
            return 18;
        }
        Calendar realTime = Calendar.getInstance();
        realTime.setTime(new Date(BaseApplication.getRealTime()));
        int realYear = realTime.get(Calendar.YEAR);

        Calendar time = Calendar.getInstance();
        time.setTime(birthday);
        int year = time.get(Calendar.YEAR);
        return realYear - year < 0 ? 0 : realYear - year;
    }

    public static String twoDateDistance(long time) {
        String suffix = "前";
        long sub = System.currentTimeMillis() - time;
        if (sub < DAY_IN_MILLIS) {
            return "今天";
        } else if (sub < DAY_IN_MILLIS * 2 && sub >= DAY_IN_MILLIS) {
            return "昨天";
        } else if (sub >= DAY_IN_MILLIS * 2 && sub < DAY_IN_MILLIS * 3) {
            return "前天";
        } else if (sub < WEEK_IN_MILLIS) {
            return "本周";
        } else if (sub < YEAR_IN_MILLIS) {
            sub = sub / WEEK_IN_MILLIS;
            return sub + "周" + suffix;
        } else {
            sub = sub / YEAR_IN_MILLIS;
            return sub + "年" + suffix;
        }
    }

    public static String getDateString(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        int dateYear = calendar.get(Calendar.YEAR);
        int dateMonth = calendar.get(Calendar.MONTH) + 1;
        int dateWeek = calendar.get(Calendar.WEEK_OF_MONTH);
        int dateDay = calendar.get(Calendar.DAY_OF_WEEK);
        Calendar now = Calendar.getInstance();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;
        int nowWeek = now.get(Calendar.WEEK_OF_MONTH);
        int nowDay = now.get(Calendar.DAY_OF_WEEK);

        int year = nowYear - dateYear;
        if (year == 0) {//同一年
            int month = nowMonth - dateMonth;
            if (month == 0) {//同一月
                int week = nowWeek - dateWeek;
                if (week == 0) {//同一周
                    int day = nowDay - dateDay;
                    if (day == 0) {//同一天
                        return "今天";
                    } else if (day == 1) {
                        return "昨天";
                    } else if (day == 2) {
                        return "前天";
                    } else {
                        return day + "天前";
                    }
                } else {
                    return week + "周前";
                }
            } else {
                return month + "月前";
            }
        } else {
            return year + "年前";
        }
    }


    /**
     * 获取是周几或者上周几
     *
     * @param time
     * @return
     */
    public static String WeekData(Long time) {
        String[] weeks = new String[]{"周日之星", "周一之星", "周二之星", "周三之星", "周四之星", "周五之星", "周六之星"};
        int mWay;//周几
        String weekStr;
        if (time == null) {
            return "";
        }
        Calendar current = Calendar.getInstance();
        current.setTime(new Date(BaseApplication.getRealTime()));
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(time));
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        current.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        //设置每周从周一开始
        c.setFirstDayOfWeek(Calendar.MONDAY);
        current.setFirstDayOfWeek(Calendar.MONDAY);

        int realDay = c.get(Calendar.DAY_OF_MONTH);
        int day = current.get(Calendar.DAY_OF_MONTH);
        if ((day - realDay) == 0) {
            return "今日之星";
        } else if ((day - realDay) == 1) {
            return "昨日之星";
        }
        mWay = c.get(Calendar.DAY_OF_WEEK) - 1;//返回周几，从1 开始，1是周日
        if (mWay >= 0 && mWay <= 6) {
            weekStr = weeks[mWay];
        } else {
            return "";
        }
        if (c.get(Calendar.WEEK_OF_YEAR) == current.get(Calendar.WEEK_OF_YEAR)) {
            return weekStr;
        } else if (c.get(Calendar.WEEK_OF_YEAR) == (current.get(Calendar.WEEK_OF_YEAR) - 1)) {
            return "上" + weekStr;
        } else {
            return formatYMD(c.getTime());
        }
    }
}
