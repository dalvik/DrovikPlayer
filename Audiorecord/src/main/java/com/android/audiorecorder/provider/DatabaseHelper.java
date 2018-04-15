package com.android.audiorecorder.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String ACTION_PROVIDER_ONCREATE = FileProvider.ACTION_PROVIDER_ONCREATE;
    
    private static final String DB_NAME = "files.db";

    private static final int DB_VERSION_FIRST = 1;
    private static final int DB_VERSION_SECONT = 3;
    private static final int DB_VERSION_THIRD = 4;
    private static final int DB_VERSION = DB_VERSION_SECONT;

    private static final String DB_TABLE_FILES = FileProvider.DB_TABLE_FILES;
    private static final String DB_TABLE_TASKS = FileProvider.DB_TABLE_TASKS;//up or download tasks
    private static final String DB_TABLE_SETTINGS = FileProvider.DB_TABLE_SETTINGS;
    private static final String DB_TABLE_USER = FileProvider.DB_TABLE_USER;
    private static final String DB_TABLE_PERSONAL_NEWS = FileProvider.DB_TABLE_PERSONAL_NEWS;
    
    private final static int FILE_TYPE_OTHER = FileProvider.FILE_TYPE_OTHER;
    private final static int FILE_TYPE_JEPG = FileProvider.FILE_TYPE_JEPG;
    private final static int FILE_TYPE_AUDIO = FileProvider.FILE_TYPE_AUDIO;
    private final static int FILE_TYPE_VIDEO = FileProvider.FILE_TYPE_VIDEO;
    
    private static final String TABLE_JPEG_FILES = FileProvider.TABLE_JPEG_FILES;
    private static final String TABLE_AUDIO_FILES = FileProvider.TABLE_AUDIO_FILES;
    private static final String TABLE_VIDEO_FILES = FileProvider.TABLE_VIDEO_FILES;
    
    private String TAG = "DatabaseHelper";
    
    private Context mContext;
    
    public DatabaseHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
        Log.i(TAG, "===> SQLiteOpenHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        installDatabase(db);//first intall database
        mContext.sendStickyBroadcast(new Intent(ACTION_PROVIDER_ONCREATE));
        Log.i(TAG, "===> SQLiteOpenHelper Oncreate.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "===> onUpgrade " + oldVersion + " to " + newVersion);
        for(int i=oldVersion; i<newVersion; i++){
            switch(i){
                case DB_VERSION_FIRST:
                    upgradeToSecond(db);
                    break;
                case DB_VERSION_SECONT:
                    upgradeToThird(db);
                    break;
                case DB_VERSION_THIRD:
                	 installDatabase(db);
                	break;
                    default:
                        break;
            }
        }
    }
    
    /**
     * TODO
     * @param db
     */
    private void upgradeToThird(SQLiteDatabase db){
        
    }
    /**
     * 1. add COLUMN_LAUNCH_MODE column on task view
     * 2. add new settings table
     * 3. add COLUMN_ID COLUMN_FILE_INIT COLUMN_SERVER_UPLOAD_URL column
     * @param db
     */
    private void upgradeToSecond(SQLiteDatabase db){
        db.execSQL("DROP VIEW IF EXISTS " + DB_TABLE_TASKS);
        String createTasksView = "CREATE VIEW " + DB_TABLE_TASKS + " AS SELECT " 
        + FileColumn.COLUMN_ID + ", " + FileColumn.COLUMN_LOCAL_PATH + ", " + FileColumn.COLUMN_REMOTE_PATH + ", "  + FileColumn.COLUMN_UP_DOWN_LOAD_STATUS +", " + FileColumn.COLUMN_LAUNCH_MODE +", "
        + FileColumn.COLUMN_UP_OR_DOWN + ", " + FileColumn.COLUMN_SHOW_NOTIFICATION + ", " + FileColumn.COLUMN_UP_LOAD_BYTE + ", " + FileColumn.COLUMN_UP_LOAD_MESSAGE
        + " FROM " + DB_TABLE_FILES + " WHERE " + FileColumn.COLUMN_UP_OR_DOWN + " !=0 ";
        db.execSQL(createTasksView);
        
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SETTINGS);
        db.execSQL("CREATE TABLE " + DB_TABLE_SETTINGS +
                "('" + FileColumn.COLUMN_ID+"' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "'" + FileColumn.COLUMN_FILE_INIT+"' INTEGER NOT NULL DEFAULT 0 , " +
                "'" + FileColumn.COLUMN_UUID +"' TEXT , " +
                "'" + FileColumn.COLUMN_SERVER_UPLOAD_URL + "' TEXT );" );
        
        ContentValues values = new ContentValues();
        values.put(FileColumn.COLUMN_FILE_INIT, 0);//db onreate, 0:frist 1:is create before
        values.put(FileColumn.COLUMN_SERVER_UPLOAD_URL, "http://10.0.2.2:80/test/action/api/file_recv.php");
        long id = db.insert(DB_TABLE_SETTINGS, null, values);
        Log.i(TAG, "===> db upgrade to version " + DB_VERSION + "  success,  init id : " + id);
    }
    
    private void installDatabase(SQLiteDatabase db){
        Log.i(TAG, "===> installDatabase.");
        try {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_FILES);
            db.execSQL("CREATE TABLE " + DB_TABLE_FILES +
                "('" + FileColumn.COLUMN_ID+"' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "'" + FileColumn.COLUMN_FILE_TYPE+"' INTEGER NOT NULL DEFAULT " + FILE_TYPE_OTHER + ", " +
                "'" + FileColumn.COLUMN_MIME_TYPE+"' TEXT, " +
                
                "'" + FileColumn.COLUMN_LOCAL_PATH + "' TEXT NOT NULL, " +
                "'" + FileColumn.COLUMN_REMOTE_PATH + "' TEXT, " +
                "'" + FileColumn.COLUMN_UP_OR_DOWN + "' INTEGER DEFAULT 0, " +
                "'" + FileColumn.COLUMN_THUMB_NAME + "' TEXT, " +
                
                "'" + FileColumn.COLUMN_FILE_SIZE + "' INTEGER DEFAULT 0, " +
                "'" + FileColumn.COLUMN_FILE_DURATION + "' INTEGER DEFAULT 0, " +
                "'" + FileColumn.COLUMN_LAUNCH_MODE + "' INTEGER, " +
                
                "'" + FileColumn.COLUMN_FILE_RESOLUTION_X + "' INTEGER, " +
                "'" + FileColumn.COLUMN_FILE_RESOLUTION_Y + "' INTEGER, " +
                "'" + FileColumn.COLUMN_FILE_THUMBNAIL + "' TEXT, " +
                
                "'" + FileColumn.COLUMN_UP_DOWN_LOAD_STATUS + "' INTEGER NOT NULL DEFAULT 0, " +
                "'" + FileColumn.COLUMN_DOWN_LOAD_TIME + "' LONG, " +
                "'" + FileColumn.COLUMN_UP_LOAD_TIME + "' LONG DEFAULT 0, " +
                "'" + FileColumn.COLUMN_UP_LOAD_BYTE + "' LONG DEFAULT 0, " +
                "'" + FileColumn.COLUMN_SHOW_NOTIFICATION + "' INTEGER DEFAULT 0, " +
                "'" + FileColumn.COLUMN_UP_LOAD_MESSAGE + "' TEXT, " +
                "'" + FileColumn.COLUMN_CALL_NUMBER + "' TEXT, " +
                "'" + FileColumn.COLUMN_CALL_TYPE + "' INTEGER DEFAULT 1);");
            
            //init upload and download task view
            db.execSQL("DROP VIEW IF EXISTS " + DB_TABLE_TASKS);
            String createTasksView = "CREATE VIEW " + DB_TABLE_TASKS + " AS SELECT " 
            + FileColumn.COLUMN_ID + ", " + FileColumn.COLUMN_LOCAL_PATH + ", " + FileColumn.COLUMN_REMOTE_PATH + ", "  + FileColumn.COLUMN_UP_DOWN_LOAD_STATUS +", " + FileColumn.COLUMN_LAUNCH_MODE +", " + FileColumn.COLUMN_FILE_TYPE +", "
            + FileColumn.COLUMN_UP_OR_DOWN + ", " + FileColumn.COLUMN_SHOW_NOTIFICATION + ", " + FileColumn.COLUMN_UP_LOAD_BYTE + ", " + FileColumn.COLUMN_UP_LOAD_MESSAGE
            + " FROM " + DB_TABLE_FILES + " WHERE " + FileColumn.COLUMN_UP_OR_DOWN + " !=0 ";
            db.execSQL(createTasksView);
            
            //init jpeg audio video view
            db.execSQL("DROP VIEW IF EXISTS " + TABLE_JPEG_FILES);
            String createJpegView = "CREATE VIEW " + TABLE_JPEG_FILES + " AS SELECT " 
            + FileColumn.COLUMN_ID + ", " + FileColumn.COLUMN_FILE_TYPE + ", " + FileColumn.COLUMN_MIME_TYPE + ", " + FileColumn.COLUMN_LOCAL_PATH + ", " + FileColumn.COLUMN_THUMB_NAME + ", " + FileColumn.COLUMN_LAUNCH_MODE + ", "
            + FileColumn.COLUMN_FILE_THUMBNAIL + ", " + FileColumn.COLUMN_FILE_RESOLUTION_X + ", " + FileColumn.COLUMN_FILE_RESOLUTION_Y + ", " + FileColumn.COLUMN_UP_OR_DOWN + ", " + FileColumn.COLUMN_SHOW_NOTIFICATION + ", "
            + FileColumn.COLUMN_FILE_SIZE + ", " + FileColumn.COLUMN_FILE_DURATION + ", " + FileColumn.COLUMN_DOWN_LOAD_TIME + ", "  + FileColumn.COLUMN_UP_DOWN_LOAD_STATUS +", "  + FileColumn.COLUMN_UP_LOAD_TIME + " FROM " + DB_TABLE_FILES
            + " WHERE " + FileColumn.COLUMN_FILE_TYPE + "=" + FILE_TYPE_JEPG;
            db.execSQL(createJpegView);
            
            db.execSQL("DROP VIEW IF EXISTS " + TABLE_AUDIO_FILES);
            String createAudioView = "CREATE VIEW " + TABLE_AUDIO_FILES + " AS SELECT " 
            + FileColumn.COLUMN_ID + ", " + FileColumn.COLUMN_FILE_TYPE + ", " + FileColumn.COLUMN_MIME_TYPE + ", " + FileColumn.COLUMN_LOCAL_PATH +", " + FileColumn.COLUMN_THUMB_NAME + ", " + FileColumn.COLUMN_LAUNCH_MODE + ", "
            + FileColumn.COLUMN_FILE_THUMBNAIL + ", " + FileColumn.COLUMN_FILE_RESOLUTION_X + ", " + FileColumn.COLUMN_FILE_RESOLUTION_Y + ", " + FileColumn.COLUMN_UP_OR_DOWN + ", " + FileColumn.COLUMN_SHOW_NOTIFICATION + ", "
            + FileColumn.COLUMN_FILE_SIZE + ", " + FileColumn.COLUMN_FILE_DURATION + ", " + FileColumn.COLUMN_DOWN_LOAD_TIME + ", "  + FileColumn.COLUMN_UP_DOWN_LOAD_STATUS +", "  + FileColumn.COLUMN_UP_LOAD_TIME  + ", "
            + FileColumn.COLUMN_CALL_NUMBER + ", " + FileColumn.COLUMN_CALL_TYPE
            +  " FROM " + DB_TABLE_FILES + " WHERE " + FileColumn.COLUMN_FILE_TYPE + " = " + FILE_TYPE_AUDIO;
            db.execSQL(createAudioView);
            
            db.execSQL("DROP VIEW IF EXISTS " + TABLE_VIDEO_FILES);
            String createVideoView = "CREATE VIEW " + TABLE_VIDEO_FILES + " AS SELECT " 
            + FileColumn.COLUMN_ID + ", " + FileColumn.COLUMN_FILE_TYPE + ", " + FileColumn.COLUMN_MIME_TYPE + ", " + FileColumn.COLUMN_LOCAL_PATH +", " + FileColumn.COLUMN_THUMB_NAME + ", " + FileColumn.COLUMN_LAUNCH_MODE + ", "
            + FileColumn.COLUMN_FILE_THUMBNAIL + ", " + FileColumn.COLUMN_FILE_RESOLUTION_X + ", " + FileColumn.COLUMN_FILE_RESOLUTION_Y + ", " + FileColumn.COLUMN_UP_OR_DOWN + ", " + FileColumn.COLUMN_SHOW_NOTIFICATION + ", "
            + FileColumn.COLUMN_FILE_SIZE + ", " + FileColumn.COLUMN_FILE_DURATION + ", " + FileColumn.COLUMN_DOWN_LOAD_TIME + ", "  + FileColumn.COLUMN_UP_DOWN_LOAD_STATUS +", "  + FileColumn.COLUMN_UP_LOAD_TIME +  " FROM " + DB_TABLE_FILES
            + " WHERE " + FileColumn.COLUMN_FILE_TYPE + "=" + FILE_TYPE_VIDEO;
            db.execSQL(createVideoView);
            
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_SETTINGS);
            String CREATE_SETTINGS = "CREATE TABLE " + DB_TABLE_SETTINGS +
                    " ('" + FileColumn.COLUMN_ID+"' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "'" + FileColumn.COLUMN_SETTING_KEY +"' TEXT , " +
                    "'" + FileColumn.COLUMN_SETTING_VALUE + "' TEXT );";
            db.execSQL(CREATE_SETTINGS);
            Log.i(TAG, "==> " + CREATE_SETTINGS);
            //ContentValues values = new ContentValues();
            //values.put(FileColumn.COLUMN_SETTING_KEY, FileColumn.COLUMN_FILE_INIT);//db onreate, 0:frist 1:is create before
            //values.put(FileColumn.COLUMN_SETTING_VALUE, 0);//"http://10.0.2.2:80/test/action/api/file_recv.php"
            //long id = db.insert(DB_TABLE_SETTINGS, null, values);
            createUser(db);
            createPersonalNews(db);
            Log.i(TAG, "===> db int db version " + DB_VERSION + "  success.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void createUser(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_USER);
        String CREATE_USER = "CREATE TABLE " + DB_TABLE_USER +
                "('" + UserColumn.COLUMN_ID+"' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "'" + UserColumn.COLUMN_USER_CODE+"' TEXT NOT NULL, " +
                "'" + UserColumn.COLUMN_TELEPHONE+"' TEXT, " +
                
                "'" + UserColumn.COLUMN_EMAIL + "' TEXT, " +
                "'" + UserColumn.COLUMN_NICK_NAME + "' TEXT, " +
                "'" + UserColumn.COLUMN_SEX + "' INTEGER DEFAULT 0, " +
                "'" + UserColumn.COLUMN_BIRTHDAY + "' LONG, " +
                
                "'" + UserColumn.COLUMN_HEIGHT + "' INTEGER DEFAULT 0, " +
                "'" + UserColumn.COLUMN_WEIGHT + "' INTEGER DEFAULT 0, " +
                "'" + UserColumn.COLUMN_HEAD_ICON + "' TEXT, " +
                
                "'" + UserColumn.COLUMN_COMPANY + "' TEXT, " +
                "'" + UserColumn.COLUMN_VOCATION + "' TEXT, " +
                "'" + UserColumn.COLUMN_SCHOOL + "' TEXT, " +
                
                "'" + UserColumn.COLUMN_SIGNATURE + "' TEXT, " +
                "'" + UserColumn.COLUMN_INTEREST + "' TEXT, " +
                "'" + UserColumn.COLUMN_BALANCE + "' LONG DEFAULT 0, " +
                "'" + UserColumn.COLUMN_TECHNIQUE + "' TEXT, " +
                "'" + UserColumn.COLUMN_RONG_YUN_TOKEN + "' TEXT, " +
                "'" + UserColumn.COLUMN_CITY + "' TEXT, " +
                "'" + UserColumn.COLUMN_PROVINCE + "' TEXT, " +
                "'" + UserColumn.COLUMN_RICH + "' LONG DEFAULT 0, " +
                "'" + UserColumn.COLUMN_STATUS + "' INTEGER DEFAULT 0, " +
                "'" + UserColumn.COLUMN_TYPE + "' INTEGER DEFAULT 0, " +
                "'" + UserColumn.COLUMN_FIRST_LOGIN + "' INTEGER DEFAULT 0);";
        db.execSQL(CREATE_USER);
        Log.i(TAG, "==> " + CREATE_USER);
    }
    
    private void createPersonalNews(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_PERSONAL_NEWS);
        String CREATE_PERSONAL_NEWS = "CREATE TABLE " + DB_TABLE_PERSONAL_NEWS +
                "('" + PersonalNewsColumn.COLUMN_ID+"' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "'" + PersonalNewsColumn.COLUMN_USER_CODE+"' TEXT NOT NULL, " +
                "'" + PersonalNewsColumn.COLUMN_NEWS_TYPE+"' INTEGER DEFAULT 0, " +
                "'" + PersonalNewsColumn.COLUMN_NEWS_CONTENT + "' TEXT, " +
                "'" + PersonalNewsColumn.COLUMN_NEWS_SUMMARY + "' TEXT, " +
                "'" + PersonalNewsColumn.COLUMN_NEWS_TIME + "' LONG );";
        db.execSQL(CREATE_PERSONAL_NEWS);
        Log.i(TAG, "==> " + CREATE_PERSONAL_NEWS);
    }

}