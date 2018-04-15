
package com.android.audiorecorder.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FileProvider extends ContentProvider {

    //download /storage/sdcard0/MediaFileManager/DownLoad/YYYY/MONTH/WEEK/*.jpg
    //upload /storage/sdcard0/MediaFileManager/UpLoad/YYYY/MONTH/WEEK/file_name.jpeg
    //record /storage/sdcard0/MediaFileManager/Record/JPG/YYYY/MONTH/WEEK/file_name.jpg
    //record /storage/sdcard0/MediaFileManager/Record/AUDIO/YYYY/MONTH/WEEK/file_name.wav
    //record /storage/sdcard0/MediaFileManager/Record/VIDEO/YYYY/MONTH/WEEK/file_name.wmv
    
    public final static String ACTION_PROVIDER_ONCREATE = "android.intent.action.PROVIDER_ONCREATE";
    //以下类型，更加AppContext中Catalog由FileManagerImp匹配
    public final static int FILE_TYPE_JEPG = 0;
    public final static int FILE_TYPE_AUDIO = 1;
    public final static int FILE_TYPE_VIDEO = 2;
    public final static int FILE_TYPE_TEXT = 3;
    public final static int FILE_TYPE_APK = 4;
    public final static int FILE_TYPE_ZIP = 5;
    public final static int FILE_TYPE_OTHER = 6;
    public final static int FILE_GALLERY_IMAGE = 7;
    public final static int FILE_GALLERY_VIDEO = 8;
    public final static int FILE_TYPE_TEL = 9;
    public final static int FILE_TYPE_TEL_LIST = 10;
    
    protected static final String DB_TABLE_FILES = "files";
    protected static final String DB_TABLE_TASKS = "down_up_load_tasks";//up or download tasks
    protected static final String DB_TABLE_SETTINGS = "settings";
    protected static final String DB_TABLE_USER = "user";
    protected static final String DB_TABLE_FRIEND = "friend";
    protected static final String DB_TABLE_PERSONAL_NEWS = "personal_news";
    
    protected static final String TABLE_JPEG_FILES = "jpeg";
    protected static final String TABLE_AUDIO_FILES = "audio";
    protected static final String TABLE_VIDEO_FILES = "video";
    public static final String TABLE_DELETE_FILES = "deleted";

    private static final int TASK_ID = 5;
    private static final int TASK = 6;
    
    private static final int ALL_FILE_INFO = 7;
    private static final int ALL_FILE_INFO_ID = 8;
    private static final int SETTINGS = 10;
    private static final int SETTINGS_ID = 11;
    
    
    private static final int JPEG_FILES = 15;
    private static final int AUDIO_FILES = 16;
    private static final int VIDEO_FILES = 17;
    private static final int DELETE_FILES = 18;
    
    private static final int JPEG_FILES_ID = 21;
    private static final int AUDIO_FILES_ID = 22;
    private static final int VIDEO_FILES_ID = 23;
    private static final int DELETE_FILES_ID = 24;
    
    private static final int USER = 30;
    private static final int USER_ID = 31;
    
    private static final int FRIEND = 35;
    private static final int FRIEND_ID = 36;
    
    private static final int PERSONAL_NEWS = 37;
    private static final int PERSONAL_NEWS_ID = 38;
    
    private final static String authority = "RecordFileProvider";
    
    public static final Uri ALL_URI = Uri.parse("content://" + authority + "/all_file_info");
    public static final Uri TASK_URI = Uri.parse("content://" + authority + "/tasks");
    public static final Uri SETTINGS_URI = Uri.parse("content://" + authority + "/settings");
    
    public static final Uri JPEGS_URI = Uri.parse("content://" + authority + "/jpeg");
    public static final Uri AUDIOS_URI = Uri.parse("content://" + authority + "/audio");
    public static final Uri VIDEOS_URI = Uri.parse("content://" + authority + "/video");
    public static final Uri GALLERY_IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final Uri GALLERY_VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    
    public static final Uri DELETE_URI = Uri.parse("content://" + authority + "/deleted");
    
    public static final Uri USER_URI = Uri.parse("content://" + authority + "/user");
    public static final Uri FRIEND_URI = Uri.parse("content://" + authority + "/'" + DB_TABLE_FRIEND + "'");
    
    public static final Uri PERSONAL_NEWS_URI = Uri.parse("content://" + authority + "/personal_news");
    //public static final Uri PERSONAL_NEWS_URI = Uri.parse("content://" + authority + "/'" + DB_TABLE_FRIEND + "'");
    
    private DatabaseHelper mDatabaseHelper;

    /** URI matcher used to recognize URIs sent by applications */
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String TAG = "FileProvider";

    static {
        sURIMatcher.addURI(authority, "tasks", TASK);
        sURIMatcher.addURI(authority, "tasks/#", TASK_ID);
        sURIMatcher.addURI(authority, "all_file_info", ALL_FILE_INFO);
        sURIMatcher.addURI(authority, "all_file_info/#", ALL_FILE_INFO_ID);
        sURIMatcher.addURI(authority, "settings", SETTINGS);
        sURIMatcher.addURI(authority, "settings/#", SETTINGS_ID);
        
        sURIMatcher.addURI(authority, "jpeg", JPEG_FILES);
        sURIMatcher.addURI(authority, "jpeg/#", JPEG_FILES_ID);
        sURIMatcher.addURI(authority, "audio", AUDIO_FILES);
        sURIMatcher.addURI(authority, "audio/#", AUDIO_FILES_ID);
        sURIMatcher.addURI(authority, "video", VIDEO_FILES);
        sURIMatcher.addURI(authority, "video/#", VIDEO_FILES_ID);
        sURIMatcher.addURI(authority, "deleted", DELETE_FILES);
        sURIMatcher.addURI(authority, "deleted/#", DELETE_FILES_ID);
        
        sURIMatcher.addURI(authority, "user", USER);
        sURIMatcher.addURI(authority, "user/#", USER_ID);
        sURIMatcher.addURI(authority, "'"+DB_TABLE_FRIEND+"'", FRIEND);
        sURIMatcher.addURI(authority, "'"+DB_TABLE_FRIEND+"'/#", FRIEND_ID);
        sURIMatcher.addURI(authority, "'" + DB_TABLE_PERSONAL_NEWS +"'", PERSONAL_NEWS);
        sURIMatcher.addURI(authority, "'" + DB_TABLE_PERSONAL_NEWS + "'/#", PERSONAL_NEWS_ID);
        
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        Log.i(TAG, "===> FileProvider onCreate.");
        return true;
    }
    
    @Override
    public String getType(Uri uri) {
        int match = sURIMatcher.match(uri);
        Log.i(TAG, "===> getType = " + match);
        switch (match) {
            case TASK_ID:
            case ALL_FILE_INFO_ID:
            case SETTINGS_ID:
            case JPEG_FILES_ID:
            case AUDIO_FILES_ID:
            case VIDEO_FILES_ID:
            case DELETE_FILES_ID:
            case USER_ID:
            case FRIEND_ID:
            case PERSONAL_NEWS_ID:
                return "vnd.android.cursor.item";
            case TASK:
            case ALL_FILE_INFO:
            case SETTINGS:
            case JPEG_FILES:
            case AUDIO_FILES:
            case VIDEO_FILES:
            case DELETE_FILES:
            case USER:
            case FRIEND:
            case PERSONAL_NEWS:
                return "vnd.android.cursor.dir";
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int type = sURIMatcher.match(uri);
        long rowid = 0;
        Uri newUri = null;
        switch(type){
            case JPEG_FILES:
                rowid = db.insert(DB_TABLE_FILES, null, values);
                if (rowid <= 0) {
                    Log.d(TAG, "couldn't insert into jpeg files database. " + uri);
                    return null;
                }
                newUri = ContentUris.withAppendedId(uri, rowid);
                getContext().getContentResolver().notifyChange(newUri, null);
                break;
            case VIDEO_FILES:
                rowid = db.insert(DB_TABLE_FILES, null, values);
                if (rowid <= 0) {
                    Log.d(TAG, "couldn't insert into video files database. " + uri);
                    return null;
                }
                newUri = ContentUris.withAppendedId(uri, rowid);
                getContext().getContentResolver().notifyChange(newUri, null);
                break;
            case AUDIO_FILES:
                rowid = db.insert(DB_TABLE_FILES, null, values);
                if (rowid <= 0) {
                    Log.d(TAG, "couldn't insert into audio files database. " + uri);
                    return null;
                }
                newUri = ContentUris.withAppendedId(uri, rowid);
                getContext().getContentResolver().notifyChange(newUri, null);
                break;
            case TASK:
                int id = 0;
                if(values.containsKey(FileColumn.COLUMN_ID)){
                    id = values.getAsInteger(FileColumn.COLUMN_ID);
                }
                String where = FileColumn.COLUMN_ID + " = " + id;
                rowid =  db.update(DB_TABLE_FILES, values, where, null);
                if (rowid <= 0) {
                    Log.d(TAG, "id = " + id + " couldn't insert into updownloads task database. " + uri);
                    return null;
                }
                newUri = ContentUris.withAppendedId(uri, rowid);
                getContext().getContentResolver().notifyChange(newUri, null);
                break;
            case ALL_FILE_INFO:
                rowid = db.insert(DB_TABLE_FILES, null, values);
                if (rowid <= 0) {
                    Log.d(TAG, "couldn't insert into files database. " + uri);
                    return null;
                }
                newUri = ContentUris.withAppendedId(uri, rowid);
                getContext().getContentResolver().notifyChange(newUri, null);
                break;
            case SETTINGS:
                    rowid = db.insert(DB_TABLE_SETTINGS, null, values);
                    if (rowid <= 0) {
                        Log.d(TAG, "couldn't insert into settings database. " + uri);
                        return null;
                    }
                    newUri = ContentUris.withAppendedId(uri, rowid);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    break;
            case USER:
                rowid = db.insert(DB_TABLE_USER, null, values);
                if (rowid <= 0) {
                    Log.d(TAG, "couldn't insert into user database. " + uri);
                    return null;
                }
                newUri = ContentUris.withAppendedId(uri, rowid);
                getContext().getContentResolver().notifyChange(newUri, null);
                break;
            case FRIEND:
                rowid = db.insert(DB_TABLE_FRIEND, null, values);
                if (rowid <= 0) {
                    Log.d(TAG, "couldn't insert into friend database. " + uri);
                    return null;
                }
                newUri = ContentUris.withAppendedId(uri, rowid);
                getContext().getContentResolver().notifyChange(newUri, null);
                break;
            case PERSONAL_NEWS:
            	rowid = db.insert(DB_TABLE_PERSONAL_NEWS, null, values);
                if (rowid <= 0) {
                    Log.d(TAG, "couldn't insert into personal news database. " + uri);
                    return null;
                }
                newUri = ContentUris.withAppendedId(uri, rowid);
                getContext().getContentResolver().notifyChange(newUri, null);
            	break;
                default:
                    Log.d(TAG, "calling insert on an unknown/invalid URI: " + uri);
                    throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
                
        }
        return newUri;
    }
    
    @Override
    public Cursor query(Uri uri, String[] projectionIn, String selection,
                        String[] selectionArgs, String sort) {
        int type = sURIMatcher.match(uri);
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        List<String> prependArgs = new ArrayList<String>();
        if (uri.getQueryParameter("distinct") != null) {
            qb.setDistinct(true);
        }
        switch (type) {
            case TASK_ID:
                qb.appendWhere(FileColumn.COLUMN_ID + "=?");
                prependArgs.add(uri.getPathSegments().get(1));
                qb.setTables(DB_TABLE_TASKS);
                break;
            case TASK:
                qb.setTables(DB_TABLE_TASKS);
                break;
            case ALL_FILE_INFO_ID:
                qb.appendWhere(FileColumn.COLUMN_ID + "=?");
                prependArgs.add(uri.getPathSegments().get(1));
                qb.setTables(DB_TABLE_FILES);
                break;
            case ALL_FILE_INFO:
                qb.setTables(DB_TABLE_FILES);
                break;
            case SETTINGS:
                qb.setTables(DB_TABLE_SETTINGS);
                break;
            case JPEG_FILES_ID:
                qb.appendWhere(FileColumn.COLUMN_ID + "=?");
                prependArgs.add(uri.getPathSegments().get(1));
                qb.setTables(TABLE_JPEG_FILES);
                break;
            case JPEG_FILES:
                qb.setTables(TABLE_JPEG_FILES);
                break;
            case AUDIO_FILES_ID:
                qb.appendWhere(FileColumn.COLUMN_ID + "=?");
                prependArgs.add(uri.getPathSegments().get(1));
                qb.setTables(TABLE_AUDIO_FILES);
                break;
            case AUDIO_FILES:
                qb.setTables(DB_TABLE_FILES);
                break;
            case VIDEO_FILES_ID:
                qb.appendWhere(FileColumn.COLUMN_ID + "=?");
                prependArgs.add(uri.getPathSegments().get(1));
                qb.setTables(TABLE_VIDEO_FILES);
                break;
            case VIDEO_FILES:
                qb.setTables(TABLE_VIDEO_FILES);
                break;
            case USER_ID:
                qb.appendWhere(FileColumn.COLUMN_ID + "=?");
                prependArgs.add(uri.getPathSegments().get(1));
                qb.setTables(DB_TABLE_USER);
                break;
            case USER:
                qb.setTables(DB_TABLE_USER);
                break;
            case FRIEND_ID:
                qb.appendWhere(FileColumn.COLUMN_ID + "=?");
                prependArgs.add(uri.getPathSegments().get(1));
                qb.setTables(DB_TABLE_FRIEND);
                break;
            case FRIEND:
                qb.setTables(DB_TABLE_FRIEND);
                break;
            case PERSONAL_NEWS:
                qb.setTables(DB_TABLE_PERSONAL_NEWS);
                break;
            default:
                break;
        }
        Cursor c = qb.query(db, projectionIn, selection,
                combine(prependArgs, selectionArgs), null, null, sort, null);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        int match = sURIMatcher.match(uri);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        boolean notification = false;
        String extraSelection = null;
        String finalSelection = null;
        switch (match) {
            case JPEG_FILES_ID:
            case VIDEO_FILES_ID:
            case AUDIO_FILES_ID:
            case DELETE_FILES_ID:
            case TASK_ID:
                extraSelection = FileColumn.COLUMN_ID + "=" + uri.getPathSegments().get(1);
            case JPEG_FILES:
            case VIDEO_FILES:
            case AUDIO_FILES:
            case DELETE_FILES:
            case TASK:
                finalSelection = getSelection(selection, extraSelection);
                count = db.update(DB_TABLE_FILES, values, finalSelection, selectionArgs);
                if (count <= 0) {
                    Log.d(TAG, "couldn't update audio in  files database");
                    return count;
                }
                notification = true;
                break;
            case SETTINGS_ID:
                extraSelection = FileColumn.COLUMN_ID + "=" + uri.getPathSegments().get(1);
			case SETTINGS:
                count = db.update(DB_TABLE_SETTINGS, values, selection, selectionArgs);
                break;
            case USER_ID:
                extraSelection = FileColumn.COLUMN_ID + "=" + uri.getPathSegments().get(1);
            case USER:
                count = db.update(DB_TABLE_USER, values, selection, selectionArgs);
                break;
            case FRIEND_ID:
                extraSelection = FileColumn.COLUMN_ID + "=" + uri.getPathSegments().get(1);
            case FRIEND:
                count = db.update(DB_TABLE_FRIEND, values, selection, selectionArgs);
                break;
            default:
                Log.d(TAG, "calling update on an unknown/invalid URI: " + uri);
                throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
        }
        if (count > 0 && notification) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }
    
    
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        int match = sURIMatcher.match(uri);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String extraSelection = null;
        String finalSelection = null;
        switch (match) {
            case JPEG_FILES_ID:
            case VIDEO_FILES_ID:
            case AUDIO_FILES_ID:
            case DELETE_FILES_ID:
            case TASK_ID:
                extraSelection = FileColumn.COLUMN_ID + "=" + uri.getPathSegments().get(1); 
            case ALL_FILE_INFO:
            case JPEG_FILES:
            case VIDEO_FILES:
            case AUDIO_FILES:
            case DELETE_FILES:
                finalSelection = getSelection(selection, extraSelection);
                sendToTargetService(uri, finalSelection, selectionArgs);
            	count = db.delete(DB_TABLE_FILES, finalSelection, selectionArgs);
                if (count <= 0) {
                    Log.w(TAG, "couldn't delete files from database " + uri);
                    return count;
                }
                getContext().getContentResolver().notifyChange(uri, null);
            	break;
            case SETTINGS_ID:
            	extraSelection = FileColumn.COLUMN_ID + "=" + uri.getPathSegments().get(1); 
            case SETTINGS:
            	finalSelection = getSelection(selection, extraSelection);
                sendToTargetService(uri, finalSelection, selectionArgs);
            	count = db.delete(DB_TABLE_SETTINGS, finalSelection, selectionArgs);
                if (count <= 0) {
                    Log.w(TAG, "couldn't delete settigns from database " + uri);
                    return count;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case USER_ID:
                extraSelection = FileColumn.COLUMN_ID + "=" + uri.getPathSegments().get(1); 
            case USER:
                finalSelection = getSelection(selection, extraSelection);
                count = db.delete(DB_TABLE_USER, finalSelection, selectionArgs);
                if (count <= 0) {
                    Log.w(TAG, "couldn't delete settigns from database " + uri);
                    return count;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case FRIEND_ID:
                extraSelection = FileColumn.COLUMN_ID + "=" + uri.getPathSegments().get(1); 
            case FRIEND:
                finalSelection = getSelection(selection, extraSelection);
                count = db.delete(DB_TABLE_FRIEND, finalSelection, selectionArgs);
                if (count <= 0) {
                    Log.w(TAG, "couldn't delete settigns from database " + uri);
                    return count;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case PERSONAL_NEWS_ID:
                extraSelection = PersonalNewsColumn.COLUMN_ID + "=" + uri.getPathSegments().get(1);
            case PERSONAL_NEWS:
                finalSelection = getSelection(selection, extraSelection);
                count = db.delete(DB_TABLE_PERSONAL_NEWS, finalSelection, selectionArgs);
                if (count <= 0) {
                    Log.w(TAG, "couldn't delete settigns from database " + uri);
                    return count;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                Log.w(TAG, "calling delete on an unknown/invalid URI: " + uri);
                throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
        }
        return count;
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
    
    private void sendToTargetService(Uri uri, String selection, String[] selectionArgs){
        String[] list = queryFilePathList(uri, selection, selectionArgs, null);
        if(list != null){
            Intent intent = new Intent(this.getContext(), FileProviderService.class);
            intent.putExtra("_list", list);
            getContext().startService(intent);
        } else {
            Log.w(TAG, "---> none find will deleted files.");
        }
    }
    
    private String[] queryFilePathList(Uri uri, String selection, String[] selectionArgs, String sort){
        String[] list = null;
        String[] projection = { FileColumn.COLUMN_LOCAL_PATH };
        Cursor cursor = query(uri, projection, selection, selectionArgs, sort);
        if(cursor != null){
            int count = cursor.getCount();
            if(count>0){
                list = new String[count];
                int index = 0;
                while(cursor.moveToNext()){
                    list[index++] = cursor.getString(0);
                }
            }
            cursor.close();
        }
        return list;
    }

    private String getSelection(String selection, String extraSelection  ) {
        String finalSelection = null;
        if (!TextUtils.isEmpty(selection) && !TextUtils.isEmpty(extraSelection)){
            finalSelection = extraSelection + " AND " + selection;
        }else  if (!TextUtils.isEmpty(selection)){
            finalSelection = selection;
        } else if (!TextUtils.isEmpty(extraSelection)){
            finalSelection = extraSelection;
        }
        return finalSelection;
    }
}
