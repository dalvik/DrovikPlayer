package com.android.audiorecorder.ui.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.android.audiorecorder.provider.UserColumn;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.library.net.utils.LogUtil;

public class UserDao {

    private final static String authority = "com.android.audiorecorder.provider.FileProvider";
    private Uri userUri = Uri.parse("content://" + authority + "/user");
    
    public void insertOrUpdateUser(Context context, UserResp user){
        String[] pro = {UserColumn.COLUMN_ID};
        String where = UserColumn.COLUMN_USER_CODE + "='" + user.userCode + "' or " + UserColumn.COLUMN_EMAIL + "='" + user.email + "' or "+ UserColumn.COLUMN_TELEPHONE + "='" + user.telephone + "'";
        Cursor cursor = context.getContentResolver().query(userUri, pro, where, null, null);
        int _id = 0;
        if(cursor != null){
            if(cursor.moveToNext()){
                _id = cursor.getInt(0);
            }
            cursor.close();
        }
        ContentValues values = new ContentValues();
        values.put(UserColumn.COLUMN_USER_CODE, user.userCode);
        values.put(UserColumn.COLUMN_EMAIL, user.email);
        values.put(UserColumn.COLUMN_TELEPHONE, user.telephone);
        values.put(UserColumn.COLUMN_NICK_NAME, user.nickName);
        values.put(UserColumn.COLUMN_SEX, user.sex);
        values.put(UserColumn.COLUMN_BIRTHDAY, user.birthday);
        values.put(UserColumn.COLUMN_HEIGHT, user.height);
        values.put(UserColumn.COLUMN_WEIGHT, user.weight);
        values.put(UserColumn.COLUMN_CITY, user.cityCode);
        values.put(UserColumn.COLUMN_HEAD_ICON, user.headIcon);
        values.put(UserColumn.COLUMN_COMPANY, user.company);
        values.put(UserColumn.COLUMN_VOCATION, user.vocation);
        values.put(UserColumn.COLUMN_SCHOOL, user.school);
        values.put(UserColumn.COLUMN_SIGNATURE, user.signature);
        values.put(UserColumn.COLUMN_INTEREST, user.interest);
        values.put(UserColumn.COLUMN_BALANCE, user.balance);
        values.put(UserColumn.COLUMN_TECHNIQUE, user.technique);
        values.put(UserColumn.COLUMN_RONG_YUN_TOKEN, user.rongYunToken);
        values.put(UserColumn.COLUMN_FIRST_LOGIN, user.isFristLogin);
        values.put(UserColumn.COLUMN_RICH, user.rich);
        values.put(UserColumn.COLUMN_STATUS, user.userStatus);
        values.put(UserColumn.COLUMN_TYPE, user.type);
        if(_id>0){//update
            String selection = UserColumn.COLUMN_ID + "='" + _id + "'";
            context.getContentResolver().update(userUri, values, selection, null);
        } else {//insert
            context.getContentResolver().insert(userUri, values);
        }
    }
    
    public UserResp getUser(Context context){
        UserResp resp = new UserResp();
        String[] pro = {UserColumn.COLUMN_USER_CODE, UserColumn.COLUMN_EMAIL, UserColumn.COLUMN_TELEPHONE, UserColumn.COLUMN_NICK_NAME, UserColumn.COLUMN_SEX,
                UserColumn.COLUMN_BIRTHDAY, UserColumn.COLUMN_HEIGHT, UserColumn.COLUMN_WEIGHT, UserColumn.COLUMN_CITY, UserColumn.COLUMN_HEAD_ICON, UserColumn.COLUMN_COMPANY, 
                UserColumn.COLUMN_VOCATION, UserColumn.COLUMN_SCHOOL, UserColumn.COLUMN_SIGNATURE, UserColumn.COLUMN_INTEREST,UserColumn.COLUMN_BALANCE, 
                UserColumn.COLUMN_TECHNIQUE, UserColumn.COLUMN_RONG_YUN_TOKEN, UserColumn.COLUMN_FIRST_LOGIN, UserColumn.COLUMN_RICH, UserColumn.COLUMN_STATUS, UserColumn.COLUMN_TYPE};
        Cursor cursor = context.getContentResolver().query(userUri, pro, null, null, null);
        if(cursor != null){
            int index = 0;
            if(cursor.moveToNext()){
                resp.userCode = cursor.getInt(index++);
                resp.email = cursor.getString(index++);
                resp.telephone = cursor.getString(index++);;
                resp.nickName = cursor.getString(index++);
                resp.sex = cursor.getInt(index++);
                resp.birthday = cursor.getLong(index++);
                resp.height = cursor.getInt(index++);
                resp.weight = cursor.getInt(index++);
                resp.cityCode = cursor.getString(index++);
                resp.headIcon = cursor.getString(index++);
                resp.company = cursor.getString(index++);
                resp.vocation = cursor.getInt(index++);
                resp.school = cursor.getString(index++);
                resp.signature = cursor.getString(index++);
                resp.interest = cursor.getLong(index++);
                resp.balance = cursor.getInt(index++);
                resp.technique = cursor.getInt(index++);
                resp.rongYunToken = cursor.getString(index++);
                resp.firstLogin = cursor.getInt(index++) == 1;
                resp.rich = cursor.getInt(index++);
                resp.type = cursor.getInt(index++);
            }
            cursor.close();
        }
        return resp;
    }
    
    public int clearLoginStatus(Context context){
        String where = UserColumn.COLUMN_USER_CODE + ">0";
        int result = context.getContentResolver().delete(userUri, where, null);
        LogUtil.d("UserDao", "==>clear login status : result = " + result);
        return result;
    }
}
