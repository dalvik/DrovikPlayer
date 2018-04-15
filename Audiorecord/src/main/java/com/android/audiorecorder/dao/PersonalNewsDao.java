package com.android.audiorecorder.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.android.audiorecorder.provider.PersonalNewsColumn;
import com.android.audiorecorder.ui.data.req.PersonalAddNewsReq;
import com.android.audiorecorder.ui.data.resp.PersonalNewsResp;

import java.util.ArrayList;

public class PersonalNewsDao {

    private final static String authority = "com.android.audiorecorder.provider.FileProvider";
    private Uri userUri = Uri.parse("content://" + authority + "/personal_news");
    
    public void insertNewPersonalNews(Context context, PersonalAddNewsReq news){
        ContentValues values = new ContentValues();
        values.put(PersonalNewsColumn.COLUMN_USER_CODE, news.userId);
        values.put(PersonalNewsColumn.COLUMN_NEWS_TYPE, news.newsType);
        values.put(PersonalNewsColumn.COLUMN_NEWS_CONTENT, news.newsContent);
        if(news.newsContent != null && news.newsContent.length()> 50){
        	values.put(PersonalNewsColumn.COLUMN_NEWS_SUMMARY, news.newsContent.substring(0, 50));
        } else {
        	values.put(PersonalNewsColumn.COLUMN_NEWS_SUMMARY, news.newsContent);
        }
        values.put(PersonalNewsColumn.COLUMN_NEWS_TIME, System.currentTimeMillis());
        context.getContentResolver().insert(userUri, values);
    }
    
    // userCode == null || userCode == "" getAll
    public ArrayList<PersonalNewsResp> getPersonalNews(Context context, int userCode){
    	ArrayList<PersonalNewsResp> list = new ArrayList<PersonalNewsResp>();
        String[] pro = {PersonalNewsColumn.COLUMN_USER_CODE, PersonalNewsColumn.COLUMN_NEWS_TYPE, PersonalNewsColumn.COLUMN_NEWS_CONTENT,
        		PersonalNewsColumn.COLUMN_NEWS_SUMMARY, PersonalNewsColumn.COLUMN_NEWS_TIME};
        String where = null;
        if(userCode  > 0){
        	where = PersonalNewsColumn.COLUMN_USER_CODE + " = " + userCode;
        }
        Cursor cursor = context.getContentResolver().query(userUri, pro, where, null, null);
        if(cursor != null){
            int index = 0;
            while(cursor.moveToNext()){
            	PersonalNewsResp resp = new PersonalNewsResp();
                resp.userId = cursor.getString(index++);
                resp.newsType = cursor.getInt(index++);
                resp.newsContent = cursor.getString(index++);;
                resp.newsTime = cursor.getLong(index++);
                list.add(resp);
            }
            cursor.close();
        }
        return list;
    }
}
