package com.android.audiorecorder.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.audiorecorder.DebugConfig;
import com.android.audiorecorder.RecorderFile;
import com.android.audiorecorder.engine.MultiMediaService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBHelper implements BaseColumns {
    
    public final static String FILE_COLUMN_PATH = "path";// file path
    public final static String FILE_COLUMN_LENGTH = "length";// file length  (byte)
    public final static String FILE_COLUMN_DURATION = "duration";// file total time  (second)
    public final static String FILE_COLUMN_TIME = "_time";
    public final static String FILE_COLUMN_MIME_TYPE = "mime_type";//3gpp amr
    public final static String FILE_COLUMN_TYPE = "type";//manly tel auto (0 1 2)
    public final static String FILE_COLUMN_PROGRESS = "progress";//
    public final static String FILE_COLUMN_BACKUP = "backup";
    
    
    private SQLiteDatabase sqLiteDatabase;
    
    public DBHelper(Context context) {
        SqliteHelper helper = new SqliteHelper(context);
        sqLiteDatabase = helper.getWritableDatabase();
    }
    
    
    public long insertRecorderFile(RecorderFile file) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.FILE_COLUMN_PATH, file.getPath());
        contentValues.put(DBHelper.FILE_COLUMN_LENGTH, file.getSize());
        contentValues.put(DBHelper.FILE_COLUMN_DURATION, file.getDuration());
        contentValues.put(DBHelper.FILE_COLUMN_MIME_TYPE, file.getMimeType());
        contentValues.put(DBHelper.FILE_COLUMN_TYPE, file.getLaunchType());
        contentValues.put(DBHelper.FILE_COLUMN_TIME, file.getTime());
        contentValues.put(DBHelper.FILE_COLUMN_PROGRESS, 0);
        contentValues.put(DBHelper.FILE_COLUMN_BACKUP, 0);
        return sqLiteDatabase.insert(SqliteHelper.TABLE_NAME_FILE, null, contentValues);
    }
    
    public void delete(long id) {
        sqLiteDatabase.delete(SqliteHelper.TABLE_NAME_FILE, "_id = '" + id +"'",  null);
    }
    
    
    public void updateUpLoadProgress(long progress, long id) {//0 sms 1 contacts 2 calllog 3 backup
        ContentValues values = new ContentValues();
        values.put(DBHelper.FILE_COLUMN_PROGRESS, progress);
        sqLiteDatabase.update(SqliteHelper.TABLE_NAME_FILE, values, "_id = ?",  new String[]{String.valueOf(id)});
    }
    
    
    public List<RecorderFile> queryPublicFileList(int page, int pageNumber) {
        List<RecorderFile> list = new ArrayList<RecorderFile>();
        String[] columns = {BASE_COLUMN_ID, DBHelper.FILE_COLUMN_PATH, DBHelper.FILE_COLUMN_LENGTH, DBHelper.FILE_COLUMN_DURATION,
                DBHelper.FILE_COLUMN_MIME_TYPE, DBHelper.FILE_COLUMN_TYPE, DBHelper.FILE_COLUMN_TIME,
                DBHelper.FILE_COLUMN_PROGRESS, DBHelper.FILE_COLUMN_BACKUP};
        Cursor cursor = sqLiteDatabase.query(SqliteHelper.TABLE_NAME_FILE, columns, DBHelper.FILE_COLUMN_TYPE + " != " + MultiMediaService.LUNCH_MODE_AUTO, null, null, null, DBHelper.FILE_COLUMN_TIME +" desc limit " + (page * pageNumber) + "," + pageNumber);
        if(cursor != null) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                int index = 0;
                RecorderFile file = new RecorderFile();
                file.setId(cursor.getInt(index++));
                String path = cursor.getString(index++);
                file.setPath(path);
                String name = path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));
                file.setName(name);
                file.setSize(cursor.getInt(index++));
                file.setDuration(cursor.getInt(index++));
                file.setMimeType(cursor.getString(index++));
                file.setLaunchType(cursor.getInt(index++));
                file.setTime(cursor.getLong(index++));
                file.setProgress(cursor.getInt(index++));
                File f = new File(path);
                if(f.exists()){
                   list.add(file);
                }else{
                	delete(file.getId());
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }
    
    public List<RecorderFile> queryPrivateFileList(int page, int pageNumber) {
        List<RecorderFile> list = new ArrayList<RecorderFile>();
        String[] columns = {BASE_COLUMN_ID, DBHelper.FILE_COLUMN_PATH, DBHelper.FILE_COLUMN_LENGTH, DBHelper.FILE_COLUMN_DURATION,
                DBHelper.FILE_COLUMN_MIME_TYPE, DBHelper.FILE_COLUMN_TYPE, DBHelper.FILE_COLUMN_TIME,
                DBHelper.FILE_COLUMN_PROGRESS, DBHelper.FILE_COLUMN_BACKUP};
        Cursor cursor = sqLiteDatabase.query(SqliteHelper.TABLE_NAME_FILE, columns, DBHelper.FILE_COLUMN_TYPE + " = " + MultiMediaService.LUNCH_MODE_AUTO, null, null, null, DBHelper.FILE_COLUMN_TIME +" desc limit " + (page * pageNumber) + "," + pageNumber);
        if(cursor != null) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                int index = 0;
                RecorderFile file = new RecorderFile();
                file.setId(cursor.getInt(index++));
                String path = cursor.getString(index++);
                String name = path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));
                file.setPath(path);
                file.setName(name);
                file.setSize(cursor.getInt(index++));
                file.setDuration(cursor.getInt(index++));
                file.setMimeType(cursor.getString(index++));
                file.setLaunchType(cursor.getInt(index++));
                file.setTime(cursor.getLong(index++));
                file.setProgress(cursor.getInt(index++));
                File f = new File(file.getPath());
                if(f.exists()){
                   list.add(file);
                }else{
                	delete(file.getId());
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }
 
     public List<RecorderFile> queryAllFileList(int page, int pageNumber) {
        List<RecorderFile> list = new ArrayList<RecorderFile>();
        String[] columns = {BASE_COLUMN_ID, DBHelper.FILE_COLUMN_PATH, DBHelper.FILE_COLUMN_LENGTH, DBHelper.FILE_COLUMN_DURATION,
                DBHelper.FILE_COLUMN_MIME_TYPE, DBHelper.FILE_COLUMN_TYPE, DBHelper.FILE_COLUMN_TIME,
                DBHelper.FILE_COLUMN_PROGRESS, DBHelper.FILE_COLUMN_BACKUP};
        Cursor cursor = sqLiteDatabase.query(SqliteHelper.TABLE_NAME_FILE, columns, null, null, null, null, DBHelper.FILE_COLUMN_TIME +" desc limit " + (page * pageNumber) + "," + pageNumber);
        if(cursor != null) {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                int index = 0;
                RecorderFile file = new RecorderFile();
                file.setId(cursor.getInt(index++));
                String path = cursor.getString(index++);
                String name = path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));
                file.setPath(path);
                file.setName(name);
                file.setSize(cursor.getInt(index++));
                file.setDuration(cursor.getInt(index++));
                file.setMimeType(cursor.getString(index++));
                file.setLaunchType(cursor.getInt(index++));
                file.setTime(cursor.getLong(index++));
                file.setProgress(cursor.getInt(index++));
                File f = new File(file.getPath());
                if(f.exists()){
                   list.add(file);
                }else{
                	delete(file.getId());
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }
 
 	public int getCount(int type){
 		int count = 0;
 		String where = null;
 		if(type != -1){
 			where = DBHelper.FILE_COLUMN_TYPE + " = " + type;
 		}
    	String[] columns = {"count(*) as a_count"};
        Cursor cursor = sqLiteDatabase.query(SqliteHelper.TABLE_NAME_FILE, columns, where, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
            if(!cursor.isAfterLast()) {
            	count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
 	}
 	
    public void closeDB() {
        if(sqLiteDatabase != null) {
            sqLiteDatabase.close();
            sqLiteDatabase = null;
        }
    }
    
    private class SqliteHelper extends SQLiteOpenHelper {
        
        private final static String DATABASE_NAME = "recorder.db";

        private final static int DATABASE_VERSION = 1;

        public final static String TABLE_NAME_FILE = "files";
        

        private final String createFilesTable = "create table "
                + TABLE_NAME_FILE
                + "("
                + "'" + BASE_COLUMN_ID + "' integer primary key autoincrement,"
                + " '"+FILE_COLUMN_PATH +"' text, '"
                + FILE_COLUMN_LENGTH +"' long, '"
                + FILE_COLUMN_DURATION + "' int , '" 
                + FILE_COLUMN_MIME_TYPE +"' text, '"
                + FILE_COLUMN_TYPE +"' int, '"
                + FILE_COLUMN_TIME +"' long, '"
                + FILE_COLUMN_PROGRESS +"' long, '" 
                +FILE_COLUMN_BACKUP + "' byte "
                + ");";// sync 0 unsync 1 synced, back 0 unbackup 1 backuped, show 0 unshow 1 show
        
        private String TAG = "SqliteHelper";
        
        public SqliteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(createFilesTable);
            if(DebugConfig.DEBUG){
            	Log.d(TAG, "createFilesTable = " + createFilesTable);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String dropTableSms = "DROP TABLE IF EXISTS " + TABLE_NAME_FILE;
            db.execSQL(dropTableSms);
            onCreate(db);
        }
    }
}
