package com.android.audiorecorder.engine;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.android.audiorecorder.dao.BaseColumns;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

public class MediaProvider extends ContentProvider implements BaseColumns {

    public final static String FILE_COLUMN_PATH = "path";// file path
    public final static String FILE_COLUMN_LENGTH = "length";// file length  (byte)
    public final static String FILE_COLUMN_DURATION = "duration";// file total time  (second)
    public final static String FILE_COLUMN_TIME = "_time";
    public final static String FILE_COLUMN_MIME_TYPE = "mime_type";//3gpp amr
    public final static String FILE_COLUMN_LAUNCH_TYPE = "launch_type";//manly tel auto (0 1 2)
    public final static String FILE_COLUMN_PROGRESS = "progress";//
    public final static String FILE_COLUMN_BACKUP = "backup";
    
    public final static String FILE_COLUMN_MEDIA_TYPE = "media_type";// 0 img 1 video 2 audio
    public final static String FILE_COLUMN_WIDTH = "width";
    public final static String FILE_COLUMN_HEIGHT = "height";
    public final static String FILE_COLUMN_SUMMARY = "summary";
    
    public final static String AUTHORITY = "com.android.audiorecorder.engine.MediaProvider";
    public final static String SCHEME = "content://";
    private static final String TYPE_LIST = "vnd.android.cursor.dir/";
    private static final String TYPE_ITEM = "vnd.android.cursor.item/";
    public final static String TABLE_NAME_FILE = "medias";
    private final static String TABLE_ALL = "files";
    private final static String TABLE_IMAGE = "images";
    private final static String TABLE_VIDEO = "videos";
    private final static String TABLE_AUDIO = "audios";
    
    public final static Uri Content_URI = Uri.parse(SCHEME + AUTHORITY + TABLE_ALL);
    public final static String ALL_Content_URI = SCHEME + AUTHORITY +"/"+ TABLE_ALL;
    public final static String IMAGE_Content_URI = SCHEME + AUTHORITY +"/"+ TABLE_IMAGE;
    public final static String VIDEO_Content_URI = SCHEME + AUTHORITY +"/"+ TABLE_VIDEO;
    public final static String AUDIO_Content_URI = SCHEME + AUTHORITY +"/"+ TABLE_AUDIO;
    
    private final static UriMatcher URI_MATCHER;
    
    
    private final static int TYPE_ALL = 0;
    private final static int TYPE_IMAGE = 1;
    private final static int TYPE_VIDEO = 2;
    private final static int TYPE_AUDIO = 3;
    
    private DatabaseHelper mDatabaseHelper;
    
    private String TAG = "MediaProvider";
    
    static{
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, TABLE_ALL, TYPE_ALL);
        URI_MATCHER.addURI(AUTHORITY, TABLE_IMAGE, TYPE_IMAGE);
        URI_MATCHER.addURI(AUTHORITY, TABLE_VIDEO, TYPE_VIDEO);
        URI_MATCHER.addURI(AUTHORITY, TABLE_AUDIO, TYPE_AUDIO);
    }
    
    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(this.getContext());
        Log.i(TAG, "MediaProvider OnCreate.");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int matchResult = URI_MATCHER.match(uri);
        List<String> prependArgs = new ArrayList<String>();
        Log.i(TAG, "---> query uri match = " + matchResult);
        String groupBy = null;
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String limit = uri.getQueryParameter("limit");
        String filter = uri.getQueryParameter("filter");
        String[] keywords = null;
        if (filter != null) {
            filter = Uri.decode(filter).trim();
            if (!TextUtils.isEmpty(filter)) {
                String[] searchWords = filter.split(" ");
                keywords = new String[searchWords.length];
                Collator col = Collator.getInstance();
                col.setStrength(Collator.PRIMARY);
                for (int i = 0; i < searchWords.length; i++) {
                    String key = MediaStore.Audio.keyFor(searchWords[i]);
                    key = key.replace("\\", "\\\\");
                    key = key.replace("%", "\\%");
                    key = key.replace("_", "\\_");
                    keywords[i] = key;
                }
            }
        }
        if (uri.getQueryParameter("distinct") != null) {
            qb.setDistinct(true);
        }
        switch(matchResult){
            case TYPE_ALL:
                qb.setTables(TABLE_ALL);
                break;
            case TYPE_IMAGE:
                qb.setTables(TABLE_IMAGE);
                break;
             case TYPE_VIDEO:
                 qb.setTables(TABLE_VIDEO);
                break;
            case TYPE_AUDIO:
                qb.setTables(TABLE_AUDIO);
                break;
            default:
                throw new IllegalStateException("Unknown URL: " + uri.toString());
        }
        Cursor c = qb.query(db, projection, selection,
                combine(prependArgs, selectionArgs), groupBy, null, sortOrder, limit);

        if (c != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return c;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        String type = null;
        switch(match){
            case TYPE_ALL:
                type = TYPE_LIST+AUTHORITY;
                break;
            case TYPE_IMAGE:
                type = TYPE_LIST+"/jpg";
                break;
             case TYPE_VIDEO:
                type = TYPE_LIST+"/video";
                break;
            case TYPE_AUDIO:
                type = TYPE_LIST+"/audio";
                break;
            default:
                throw new IllegalStateException("Not Allowed To Insert With URL: " + uri.toString());
        }
        return type;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = URI_MATCHER.match(uri);
        long insertCount = -1;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        Uri retUri = null;
        switch(match){
            case TYPE_ALL:
                insertCount = db.insert(TABLE_NAME_FILE, null, values);
                if (insertCount >= 0) {
                    retUri = ContentUris.withAppendedId(uri, insertCount);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            case TYPE_IMAGE:
                insertCount = db.insert(TABLE_NAME_FILE, null, values);
                if (insertCount >= 0) {
                    retUri = ContentUris.withAppendedId(uri, insertCount);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
             case TYPE_VIDEO:
                 insertCount = db.insert(TABLE_NAME_FILE, null, values);
                 if (insertCount >= 0) {
                     retUri = ContentUris.withAppendedId(uri, insertCount);
                     getContext().getContentResolver().notifyChange(uri, null);
                 }
                break;
            case TYPE_AUDIO:
                insertCount = db.insert(TABLE_NAME_FILE, null, values);
                if (insertCount >= 0) {
                    retUri = ContentUris.withAppendedId(uri, insertCount);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            default:
                throw new IllegalStateException("Not Allowed To Insert With URL: " + uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = URI_MATCHER.match(uri);
        Log.w(TAG, "---> match = " + match);
        int ret = 0;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        switch(match){
            case TYPE_ALL:
                ret = db.delete(TABLE_NAME_FILE, selection, selectionArgs);
                break;
            case TYPE_IMAGE:
                ret = db.delete(TABLE_NAME_FILE, selection, selectionArgs);
                break;
             case TYPE_VIDEO:
                 ret = db.delete(TABLE_NAME_FILE, selection, selectionArgs);
                break;
            case TYPE_AUDIO:
                ret = db.delete(TABLE_NAME_FILE, selection, selectionArgs);
                break;
            default:
                throw new IllegalStateException("Not Allowed To Insert With URL: " + uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int ret = 0;
        int match = URI_MATCHER.match(uri);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        switch(match){
            case TYPE_ALL:
                ret = db.update(TABLE_NAME_FILE, values, selection, selectionArgs);
                break;
            case TYPE_IMAGE:
                ret = db.update(TABLE_NAME_FILE, values, selection, selectionArgs);
                break;
            case TYPE_VIDEO:
                ret = db.update(TABLE_NAME_FILE, values, selection, selectionArgs);
                break;
            case TYPE_AUDIO:
                ret = db.update(TABLE_NAME_FILE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalStateException("Not Allowed To Insert With URL: " + uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ret;
    }
    
    private String[] combine(List<String> prepend, String[] userArgs) {
        int presize = prepend.size();
        if (presize == 0) {
            return userArgs;
        }

        int usersize = (userArgs != null) ? userArgs.length : 0;
        String[] combined = new String[presize + usersize];
        for (int i = 0; i < presize; i++) {
            combined[i] = prepend.get(i);
        }
        for (int i = 0; i < usersize; i++) {
            combined[presize + i] = userArgs[i];
        }
        return combined;
    }
    
    
    private final class DatabaseHelper extends SQLiteOpenHelper {

        private final static String DATABASE_NAME = "recorder.db";

        private final static int DATABASE_VERSION = 1;

        public final static int MEDIA_TYPE_IMAGE = 0;
        public final static int MEDIA_TYPE_VIDEO = 1;
        public final static int MEDIA_TYPE_AUDIO = 2;
        
        private final String createFilesTable = "create table "
                + TABLE_NAME_FILE
                + "("
                + "'" + BASE_COLUMN_ID + "' integer primary key autoincrement, '"
                + FILE_COLUMN_PATH +"' text, '"
                + FILE_COLUMN_LENGTH +"' long, '"
                + FILE_COLUMN_DURATION + "' int , '" 
                + FILE_COLUMN_MIME_TYPE +"' text, '"
                + FILE_COLUMN_LAUNCH_TYPE +"' int, '"
                + FILE_COLUMN_TIME +"' long, '"
                + FILE_COLUMN_PROGRESS +"' long, '" 
                + FILE_COLUMN_BACKUP + "' byte, '"
                + FILE_COLUMN_MEDIA_TYPE + "' int, '"
                + FILE_COLUMN_WIDTH + "' int, '"
                + FILE_COLUMN_HEIGHT + "' int, '"
                + FILE_COLUMN_SUMMARY + "' text "
                + ");";
        
        private String TAG = "SqliteHelper";
        
        
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "DataBase Helper OnCreate.");
            updateDatabase(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "DataBase Helper onUpgrade.");
            String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME_FILE;
            db.execSQL(dropTable);
            onCreate(db);
        }
        
        private void updateDatabase(SQLiteDatabase db){
            if(db != null){
                Log.i(TAG, createFilesTable);
                String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME_FILE;
                db.execSQL(dropTable);
                db.execSQL(createFilesTable);
                String createAllView = "CREATE VIEW " + TABLE_ALL + "  AS SELECT " + BASE_COLUMN_ID + ", "+FILE_COLUMN_PATH+", " + FILE_COLUMN_LENGTH +", "+ FILE_COLUMN_DURATION + ", " + FILE_COLUMN_MIME_TYPE+
                        ", "+FILE_COLUMN_LAUNCH_TYPE+", "+FILE_COLUMN_TIME+", "+FILE_COLUMN_PROGRESS+", "+FILE_COLUMN_BACKUP+", " + FILE_COLUMN_MEDIA_TYPE+", " +FILE_COLUMN_WIDTH +", " + FILE_COLUMN_HEIGHT +
                        ", " + FILE_COLUMN_SUMMARY +
                        " FROM "+TABLE_NAME_FILE+";"; 
                Log.i(TAG, createAllView);
                db.execSQL("DROP VIEW IF EXISTS " + TABLE_ALL);
                db.execSQL(createAllView);
                String createImageView = "CREATE VIEW " + TABLE_IMAGE + "  AS SELECT " + BASE_COLUMN_ID + ", "+FILE_COLUMN_PATH+", " + FILE_COLUMN_LENGTH +", "+FILE_COLUMN_DURATION + ", " + FILE_COLUMN_MIME_TYPE+
                        ", "+FILE_COLUMN_LAUNCH_TYPE+", "+FILE_COLUMN_TIME+", "+FILE_COLUMN_PROGRESS+", "+FILE_COLUMN_BACKUP+", " + FILE_COLUMN_MEDIA_TYPE+", " +FILE_COLUMN_WIDTH +", " + FILE_COLUMN_HEIGHT +
                        ", " + FILE_COLUMN_SUMMARY +
                        " FROM "+TABLE_NAME_FILE+" WHERE " + FILE_COLUMN_LAUNCH_TYPE + " = " + MEDIA_TYPE_IMAGE;
                Log.i(TAG, createImageView);
                db.execSQL("DROP VIEW IF EXISTS " + TABLE_IMAGE);
                db.execSQL(createImageView);
                String createVieoView = "CREATE VIEW " + TABLE_VIDEO + "  AS SELECT " + BASE_COLUMN_ID + ", "+FILE_COLUMN_PATH+", " + FILE_COLUMN_LENGTH + ", " + FILE_COLUMN_DURATION + ", "+FILE_COLUMN_MIME_TYPE+
                        ", "+FILE_COLUMN_LAUNCH_TYPE+", "+FILE_COLUMN_TIME+", "+FILE_COLUMN_PROGRESS+", "+FILE_COLUMN_BACKUP+", " + FILE_COLUMN_MEDIA_TYPE+", " +FILE_COLUMN_WIDTH +", " + FILE_COLUMN_HEIGHT +
                        ", " + FILE_COLUMN_SUMMARY +
                        " FROM "+TABLE_NAME_FILE+" WHERE " + FILE_COLUMN_LAUNCH_TYPE + " = " + MEDIA_TYPE_VIDEO; 
                Log.i(TAG, createVieoView);
                db.execSQL("DROP VIEW IF EXISTS " + TABLE_VIDEO);
                db.execSQL(createVieoView);
                String createAudioView = "CREATE VIEW " + TABLE_AUDIO + "  AS SELECT " + BASE_COLUMN_ID + ", "+FILE_COLUMN_PATH+", " + FILE_COLUMN_LENGTH + ", " + FILE_COLUMN_DURATION + ", "+FILE_COLUMN_MIME_TYPE+
                        ", "+FILE_COLUMN_LAUNCH_TYPE+", "+FILE_COLUMN_TIME+", "+FILE_COLUMN_PROGRESS+", "+FILE_COLUMN_BACKUP+", " + FILE_COLUMN_MEDIA_TYPE+", " +FILE_COLUMN_WIDTH +", " + FILE_COLUMN_HEIGHT +
                        ", " + FILE_COLUMN_SUMMARY +
                        " FROM "+TABLE_NAME_FILE+" WHERE " + FILE_COLUMN_LAUNCH_TYPE + " = " + MEDIA_TYPE_AUDIO; 
                Log.i(TAG, createAudioView);
                db.execSQL("DROP VIEW IF EXISTS " + TABLE_AUDIO);
                db.execSQL(createAudioView);
            }
        }
    }

}
