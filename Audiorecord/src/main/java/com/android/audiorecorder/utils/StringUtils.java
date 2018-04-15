package com.android.audiorecorder.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.android.audiorecorder.provider.FileColumn;
import com.android.audiorecorder.provider.FileProvider;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class StringUtils {
	
	public final static String KEY_RONG_TOKEN = "rong_token";
	public final static String KEY_USER_ID = "user_id";
	public final static String KEY_USER_NAME = "user_name";
	public final static String KEY_USER_PASSWORD = "user_password";
	public final static String KEY_USER_HEADER_ICON = "user_header_icon";
	public final static String ACTION_USER_LOGIN = "android.intent.action.User_Login";
	
	public final static String KEY_USER_LOGIN_STATUS = "user_login_status";
	
	private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * ���ַ�תλ��������
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if(time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();
		
		String curDate = dateFormater2.format(cal.getTime());
		String paramDate = dateFormater2.format(time);
		if(curDate.equals(paramDate)){
			int hour = (int)((cal.getTimeInMillis() - time.getTime())/3600000);
			if(hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000,1)+"����";
			else 
				ftime = hour+"Сʱ";
			return ftime;
		}
		
		long lt = time.getTime()/86400000;
		long ct = cal.getTimeInMillis()/86400000;
		int days = (int)(ct - lt);		
		if(days == 0){
			int hour = (int)((cal.getTimeInMillis() - time.getTime())/3600000);
			if(hour == 0)
				ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000,1)+"����";
			else 
				ftime = hour+"Сʱ";
		}
		else if(days == 1){
			ftime = "����";
		}
		else if(days == 2){
			ftime = "ǰ��";
		}
		else if(days > 2 && days <= 10){ 
			ftime = days+"��ǰ";			
		}
		else if(days > 10){			
			ftime = dateFormater2.format(time);
		}
		return ftime;
	}
	
	public static boolean isToday(String sdate){
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if(time != null){
			String nowDate = dateFormater2.format(today);
			String timeDate = dateFormater2.format(time);
			if(nowDate.equals(timeDate)){
				b = true;
			}
		}
		return b;
	}
	
	public static boolean isEmpty( String input )
	{
		if ( input == null || "".equals( input ) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * �ж��ǲ���һ���Ϸ��ĵ����ʼ���ַ
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		if(email == null || email.trim().length()==0) 
			return false;
	    return emailer.matcher(email).matches();
	}
	/**
	 * �ַ�ת����
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try{
			return Integer.parseInt(str);
		}catch(Exception e){}
		return defValue;
	}

	public static int toInt(Object obj) {
		if(obj==null) return 0;
		return toInt(obj.toString(),0);
	}


	public static long toLong(String obj) {
		try{
			return Long.parseLong(obj);
		}catch(Exception e){}
		return 0;
	}


	public static boolean toBool(String b) {
		try{
			return Boolean.parseBoolean(b);
		}catch(Exception e){}
		return false;
	}
	
	public static String formatProgress(long progress, long totalSize){
        DecimalFormat df = new DecimalFormat("00 %");
        if(progress<=totalSize){
            return df.format((double) progress / totalSize);
        }
        return null;
    }
	
	public static String getString(Context context, String key, String defaultValue) {
		String[] pro = {FileColumn.COLUMN_SETTING_VALUE};
    	String where = FileColumn.COLUMN_SETTING_KEY + "='" + key + "'";
    	Cursor cursor = context.getContentResolver().query(FileProvider.SETTINGS_URI, pro, where, null, null);
    	String value = null;
    	if(cursor != null){
    		if(cursor.moveToNext()){
    			value = cursor.getString(0);
    		} else {
    			value = defaultValue;
    		}
    		cursor.close();
    	}
    	return value;
	}

	public static int getInt(Context context, String key, int defaultValue) {
		String[] pro = {FileColumn.COLUMN_SETTING_VALUE};
		String where = FileColumn.COLUMN_SETTING_KEY + "='" + key + "'";
		Cursor cursor = context.getContentResolver().query(FileProvider.SETTINGS_URI, pro, where, null, null);
		int value = defaultValue;
		if(cursor != null){
			if(cursor.moveToNext()){
				value = cursor.getInt(0);
			}
			cursor.close();
		}
		return value;
	}

	public static void putValue(Context context, String key, Object value){
		String[] pro = {FileColumn.COLUMN_ID};
    	String where = FileColumn.COLUMN_SETTING_KEY + "='" + key + "'";
    	Cursor cursor = context.getContentResolver().query(FileProvider.SETTINGS_URI, pro, where, null, null);
    	int _id = 0;
    	if(cursor != null){
    		if(cursor.moveToNext()){
    			_id = cursor.getInt(0);
    		}
    		cursor.close();
    	}
    	ContentValues values = new ContentValues();
    	values.put(FileColumn.COLUMN_SETTING_VALUE, String.valueOf(value));
    	if(_id>0){
    		String selection = FileColumn.COLUMN_ID + " = '" + _id + "'";
    		context.getContentResolver().update(FileProvider.SETTINGS_URI, values, selection, null);
    	} else {
    		values.put(FileColumn.COLUMN_SETTING_KEY, key);
    		context.getContentResolver().insert(FileProvider.SETTINGS_URI, values);
    	}
	}
}