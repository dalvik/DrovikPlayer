package com.android.audiorecorder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.Spanned;
import android.view.OrientationEventListener;
import android.view.Surface;

import java.io.File;
import java.util.Calendar;


public class StringUtil {
    
    public static int toInt(Object obj) {
        if(obj==null) return 0;
        return toInt(obj.toString(),0);
    }
    
    public static int toInt(String str, int defValue) {
        try{
            return Integer.parseInt(str);
        }catch(Exception e){}
        return defValue;
    }
    
    public static Spanned loadHtmlText(Context context, int resId){
       return Html.fromHtml(context.getString(resId));
    }
    
    public static byte[] getWaveFileHeader(long totalAudioLen, 
            long totalDataLen, long longSampleRate, int channels, long byteRate){
        byte[] header = new byte[44]; 
        header[0] = 'R'; // RIFF/WAVE header 
        header[1] = 'I'; 
        header[2] = 'F'; 
        header[3] = 'F'; 
        header[4] = (byte) (totalDataLen & 0xff); 
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0; 
        header[34] = 16; 
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        return header;
    }
    
    public static final int ORIENTATION_HYSTERESIS = 5;
    
    public static int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation = false;
        if (orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - orientationHistory);
            dist = Math.min( dist, 360 - dist );
            changeOrientation = ( dist >= 45 + ORIENTATION_HYSTERESIS );
        }
        if (changeOrientation) {
            return ((orientation + 45) / 90 * 90) % 360;
        }
        return orientationHistory;
    }
    
    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0: return 0;
            case Surface.ROTATION_90: return 90;
            case Surface.ROTATION_180: return 180;
            case Surface.ROTATION_270: return 270;
        }
        return 0;
    }
    
    public static String getYearMonthWeek(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONDAY)+1;
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        return String.valueOf(year) + File.separator + String.valueOf(month) + File.separator + String.valueOf(week);
    }
    
    public static String handTime(long time, Resources res) {
		long tm = System.currentTimeMillis();
		long d = (tm - time) / 1000;
		/*if ((d / (60 * 60 * 24)) > 0) {
			return d / (60 * 60 * 24) + res.getString(R.string.friend_circle_message_time_days_ago);
		} else if ((d / (60 * 60)) > 0) {
			return d / (60 * 60) + res.getString(R.string.friend_circle_message_time_hours_ago);
		} else if ((d / 60) > 0) {
			return d / 60 + res.getString(R.string.friend_circle_message_time_minute_ago);
		} else {
			return res.getString(R.string.friend_circle_message_time_second_ago);
		}*/
        return "";
	}
    
    public static String getDateString(long date, Resources res) {
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
        /*if (year == 0) {//同一年
            int month = nowMonth - dateMonth;
            if (month == 0) {//同一月
                int week = nowWeek - dateWeek;
                if (week == 0) {//同一周
                    int day = nowDay - dateDay;
                    if (day == 0) {//同一天
                        return handTime(date, res);
                    } else if (day == 1) {
                        return res.getString(R.string.friend_circle_message_time_yestorday);
                    } else {
                        return day + res.getString(R.string.friend_circle_message_time_day_ago);
                    }
                } else {
                    return week + res.getString(R.string.friend_circle_message_time_week_ago);
                }
            } else {
                return month + res.getString(R.string.friend_circle_message_time_month_ago);
            }
        } else {
            return year + res.getString(R.string.friend_circle_message_time_year_ago);
        }*/
        return  "";
    }
}
