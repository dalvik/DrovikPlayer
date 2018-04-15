package com.android.audiorecorder.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.android.audiorecorder.RecorderFile;
import com.android.audiorecorder.provider.FileColumn;
import com.android.audiorecorder.provider.FileDetail;
import com.android.audiorecorder.provider.FileProvider;
import com.android.audiorecorder.provider.FileProviderService;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.audiorecorder.utils.UIHelper;
import com.android.library.utils.DateUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileManagerImp extends MediaFileManager {

    //private DBHelper dbHelper;
    private Context mContext;
    private Map<String, Integer> mSectionMap;
    private static int section = 1;
    private SharedPreferences mThumbCache;
    private String TAG = "FileManagerImp";
    public FileManagerImp(Context context) {
        //dbHelper = new DBHelper(context);
        mContext = context;
        mSectionMap = new HashMap<String, Integer>();
        mThumbCache = mContext.getSharedPreferences(FileProviderService.THUMBNAIL, Context.MODE_PRIVATE);
    }
    
    @Override
    public long delete(int mimeType, long id) {
        //dbHelper.delete(id);
        return deleteItem(mimeType, id);
    }
    
    @Override
    public int getFileCount(int mimeType, int type) {
        return getFileNumber(mimeType, null, null);//dbHelper.getCount(type);
    }
    
    @Override
    public void insertRecorderFile(RecorderFile file) {
        //dbHelper.insertRecorderFile(file);
    }
    
    @Override
    public List<RecorderFile> queryAllFileList(int mimeType, int page, int pageNumber) {
        return loadFileList(mimeType, page, pageNumber);//dbHelper.queryAllFileList(page, pageNumber);
    }
    
    @Override
    public List<RecorderFile> queryPrivateFileList(int mimeType, int page, int pageNumber) {
        return loadFileList(mimeType, page, pageNumber);//dbHelper.queryPrivateFileList(page, pageNumber);
    }
    
    @Override
    public List<RecorderFile> queryPublicFileList(int mimeType, int page, int pageNumber) {
        return loadFileList(mimeType, page, pageNumber);//dbHelper.queryPublicFileList(page, pageNumber);
    }
    
    @Override
    public void updateUpLoadProgress(int mimeType, long progress, long id) {
        //dbHelper.updateUpLoadProgress(progress, id);
    }
    
    @Override
    public long addTask(long id, boolean download) {
        return addDownUploadTask(id, download);
    }

    @Override
    public List<FileThumb> loadFileThumbList(boolean isLocal, int mediaType, int pageIndex, int offset, Set<Integer> launchType) {
        if(isLocal){
            return getFileThumbList(getFileType(mediaType), pageIndex, offset, launchType);
        } else {
            return loadFileThumbFromNetwork(getFileType(mediaType), pageIndex, offset);
        }
    }
    
    @Override
    public List<FileDetail> loadFileList(boolean isLocal, int mediaType,
                                         String thumbName, int pageIndex, int pageNumber, Set<Integer> launchType) {
        if(isLocal){
            return loadFileList(getFileType(mediaType), thumbName, pageIndex, pageNumber, launchType);
        } else {
            return loadFileList(getFileType(mediaType), thumbName, pageIndex, pageNumber, launchType);
        }
    }

    @Override
    public int getFileThumbCount(int fileType, int type, Set<Integer> launchType) {
        return getFileNumber(fileType, null, launchType);
    }
    
    @Override
    public int getFileListCount(int fileType, String thumbName, Set<Integer> launchType) {
        return getFileNumber(fileType, thumbName, launchType);
    }
    
    private List<RecorderFile> loadFileList(int mimeType, int page, int pageNumber){
        Uri uri = getUri(mimeType);
        String[] columns = {
                FileColumn.COLUMN_ID, FileColumn.COLUMN_LOCAL_PATH, FileColumn.COLUMN_FILE_SIZE, FileColumn.COLUMN_FILE_DURATION, 
                FileColumn.COLUMN_MIME_TYPE, FileColumn.COLUMN_FILE_TYPE, 
                FileColumn.COLUMN_DOWN_LOAD_TIME};
        Cursor cursor = mContext.getContentResolver().query(uri, columns, null, null, FileColumn.COLUMN_DOWN_LOAD_TIME +" desc limit " + (page * pageNumber) + "," + pageNumber);
        List<RecorderFile> list = new ArrayList<RecorderFile>();
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
                //file.setLaunchType(cursor.getInt(index++));
                file.setMediaType(cursor.getInt(index++));
                file.setTime(cursor.getLong(index++));
                //file.setProgress(cursor.getInt(index++));
                //file.setWidth(cursor.getInt(index++));
                //file.setHeight(cursor.getInt(index++));
                //file.setSummary(cursor.getString(index++));
                File f = new File(path);
                if(f.exists()){
                   list.add(file);
                }else{
                    delete(mimeType, file.getId());
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return list;
    }

    private int getFileNumber(int fileType, String thumbName, Set<Integer> launchType){// -1 all files
        String[] args = null;
        StringBuffer where = null;
        // select count(*) where thumbName = "aaa" and launch_mod = "1" or launch_mod = "2";
        if(thumbName != null && thumbName.length() > 0){
            where = new StringBuffer(FileColumn.COLUMN_THUMB_NAME + "=?");
            args = new String[]{thumbName};
        }
        if(launchType != null && launchType.size() > 0){
            int index = 1;
            int length = launchType.size();
            if(where == null){
                where = new StringBuffer();
                args = new String[length];
            } else {
                args = new String[length+1];
                args[0] = thumbName;
                where.append(" and (");
            }
            for(Iterator<Integer> set = launchType.iterator(); set.hasNext();){
                args[index++] = String.valueOf(set.next());
                where.append(FileColumn.COLUMN_LAUNCH_MODE + " = ? ");
                if(index<=length){
                    where.append(" or ");
                }
            }
            where.append(")");
        }
        Uri uri = getUri(fileType);
        String[] columns = {"count(*) as a_count"};
        return queryFileCountFromDb(uri, columns, where != null?where.toString():null, args, null);
    }
    
    private long deleteItem(int fileType, long id){
        return mContext.getContentResolver().delete(getUri(fileType), FileColumn.COLUMN_ID + "=?", new String[]{ String.valueOf(id) });
    }
    
    private long addDownUploadTask(long id, boolean download){
        ContentValues values = new ContentValues();
        values.put(FileColumn.COLUMN_UP_OR_DOWN, download?1:2);
        return mContext.getContentResolver().update(FileProvider.ALL_URI, values, FileColumn.COLUMN_ID + "=?", new String[]{ String.valueOf(id) });
    }

    /**
     * 加载本地缩略图库，包括系统图库和本应用图库
     * @param fileType
     * @param pageIndex
     * @param offset
     * @param launchType
     * @return
     */
    private List<FileThumb> getFileThumbList(int fileType, int pageIndex, int offset, Set<Integer> launchType){
        if(fileType == FileProvider.FILE_GALLERY_IMAGE){//load image from system gallery db
            String[] columns = { "count(*) as a_count", MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.TITLE,
                    MediaStore.Images.Media.SIZE};
            String order = MediaStore.Images.Media.DATE_ADDED +" desc limit " + (pageIndex * PERPAGE_NUMBER + offset) + "," + PERPAGE_NUMBER;
            StringBuffer where = new StringBuffer("0=0) group by (" + MediaStore.Images.Media.TITLE);
            String[] args = null;
            Uri uri = getUri(fileType);
            return queryThumbnailListFromGalleryDb(fileType, uri, columns, where.toString(), args, order);
        } else {
            String[] columns = { "count(*) as a_count", FileColumn.COLUMN_ID,
                    FileColumn.COLUMN_THUMB_NAME, FileColumn.COLUMN_LOCAL_PATH,
                    FileColumn.COLUMN_DOWN_LOAD_TIME, FileColumn.COLUMN_LAUNCH_MODE};
            String order = FileColumn.COLUMN_DOWN_LOAD_TIME +" desc limit " + (pageIndex * PERPAGE_NUMBER + offset) + "," + PERPAGE_NUMBER;
            StringBuffer where = null;
            String[] args = null;
            if(launchType == null || launchType.size() == 0){
                where = new StringBuffer("0=0) group by (" + FileColumn.COLUMN_THUMB_NAME);
            } else {
                int index = 0;
                int length = launchType.size();
                args = new String[length];
                where = new StringBuffer();
                for(Iterator<Integer> set = launchType.iterator(); set.hasNext();){
                    args[index++] = String.valueOf(set.next());
                    where.append(FileColumn.COLUMN_LAUNCH_MODE + " = ? ");
                    if(index<length){
                        where.append(" or ");
                    }
                }
                where.append(") group by (" + FileColumn.COLUMN_THUMB_NAME);
            }
            Uri uri = getUri(fileType);
            return queryThumbnailListFromDb(fileType, uri, columns, where.toString(), args, order);
        }
    }
    
    private List<FileDetail> loadFileList(int fileType, String thumbName, int pageIndex, int offset, Set<Integer> launchType){
        Uri uri = getUri(fileType);
        String[] columns = null;
        if(fileType == FileProvider.FILE_GALLERY_IMAGE){//load image from system gallery db
            columns = new String[] {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.TITLE,
                    MediaStore.Images.Media.SIZE};
            String order = MediaStore.Images.Media.DATE_ADDED +" desc limit " + (pageIndex * PERPAGE_NUMBER + offset) + "," + PERPAGE_NUMBER;
            StringBuffer where = null;//new StringBuffer("0=0) group by (" + MediaStore.Images.Media.TITLE);
            String[] args = null;
            Cursor cursor = mContext.getContentResolver().query(uri, columns, null, null, order);
            List<FileDetail> list = new ArrayList<FileDetail>();
            if(cursor != null) {
                while (cursor.moveToNext()) {
                    // 获取图片的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                    FileDetail detail = new FileDetail();
                    detail.setFilePath(path);
                    String ymd = DateUtil.paserTimeToYMD(time * 1000);
                    detail.setTime(ymd);
                    detail.setFileType(FileProvider.FILE_TYPE_JEPG);
                    if(!mSectionMap.containsKey(ymd)) {
                        detail.setSection(section);
                        mSectionMap.put(ymd, section);
                        section ++;
                    } else {
                        detail.setSection(mSectionMap.get(ymd));
                    }
                    list.add(detail);
                }
                cursor.close();
            }
            return list;
        } else if(fileType == FileProvider.FILE_GALLERY_VIDEO){//load image from system gallery db
            columns = new String[] {
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.SIZE
            };
            String order = MediaStore.Video.Media.DATE_ADDED +" desc limit " + (pageIndex * PERPAGE_NUMBER + offset) + "," + PERPAGE_NUMBER;
            StringBuffer where = null;//new StringBuffer("0=0) group by (" + MediaStore.Images.Media.TITLE);
            String[] args = null;
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor cursor = contentResolver.query(uri, columns, null, null, order);
            List<FileDetail> list = new ArrayList<FileDetail>();
            if(cursor != null) {
                while(cursor.moveToNext()) {
                    // 获取视频的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                    String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                    int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    FileDetail detail = new FileDetail();
                    detail.setFilePath(path);
                    detail.setFileName(displayName);
                    String ymd = DateUtil.paserTimeToYMD(time * 1000);
                    detail.setDuration(duration);
                    detail.setTime(ymd);
                    detail.setFileType(FileProvider.FILE_TYPE_VIDEO);
                    if(!mSectionMap.containsKey(ymd)) {
                        detail.setSection(section);
                        mSectionMap.put(ymd, section);
                        section ++;
                    } else {
                        detail.setSection(mSectionMap.get(ymd));
                    }
                    Log.d(TAG, "path:" + path + "--DISPLAY_NAME:" + displayName + " " + id);
                    String thumbPath = mThumbCache.getString(path, "");
                    if(TextUtils.isEmpty(thumbPath)) {
                        String[] TNUMB_COLUMNS = {MediaStore.Video.Thumbnails.DATA,MediaStore.Video.Thumbnails.VIDEO_ID};
                        String whereThumbnail = MediaStore.Video.Thumbnails.VIDEO_ID + "= '" + id + " '";
                        Cursor thumbCursor = contentResolver.query(MediaStore.Video.Thumbnails.INTERNAL_CONTENT_URI, TNUMB_COLUMNS, whereThumbnail, null, null);
                        if (thumbCursor != null) {
                            if(thumbCursor.moveToFirst()) {
                                thumbPath = thumbCursor.getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                            }
                            thumbCursor.close();
                        }
                    }
                    detail.setThumbnailPath(thumbPath);
                    Log.d(TAG, "==> thumbPath:" + thumbPath);
                    list.add(detail);
                }
                cursor.close();
            }
            return list;
        } else if(fileType == FileProvider.FILE_TYPE_AUDIO) {
            StringBuffer where = null;
            String[] args =  null;
            if(launchType == null || launchType.size() == 0){
                where = new StringBuffer(FileColumn.COLUMN_THUMB_NAME + "='" + thumbName + "'");
                args =  new String[]{thumbName};
            } else {
                int index = 1;
                int length = launchType.size();
                args = new String[length+1];
                where = new StringBuffer();
                args[0] = thumbName;
                //where = new StringBuffer(FileColumn.COLUMN_THUMB_NAME +"='"+thumbName+"' and ( ");
                where = new StringBuffer("( ");
                for(Iterator<Integer> set = launchType.iterator(); set.hasNext();){
                    String tmp = String.valueOf(set.next());
                    args[index++] = tmp;
                    where.append(FileColumn.COLUMN_LAUNCH_MODE +" = '"+ tmp + "' ");
                    if(index<=length){
                        where.append(" or ");
                    }
                }
                where.append(")");
            }
            columns = new String[]{FileColumn.COLUMN_ID, FileColumn.COLUMN_LOCAL_PATH, FileColumn.COLUMN_FILE_SIZE, FileColumn.COLUMN_FILE_DURATION,
                    FileColumn.COLUMN_MIME_TYPE, FileColumn.COLUMN_LAUNCH_MODE, FileColumn.COLUMN_FILE_RESOLUTION_X, FileColumn.COLUMN_FILE_RESOLUTION_Y,
                    FileColumn.COLUMN_FILE_THUMBNAIL, FileColumn.COLUMN_SHOW_NOTIFICATION, FileColumn.COLUMN_UP_OR_DOWN, FileColumn.COLUMN_UP_DOWN_LOAD_STATUS,
                    FileColumn.COLUMN_DOWN_LOAD_TIME, FileColumn.COLUMN_UP_LOAD_TIME, FileColumn.COLUMN_CALL_NUMBER, FileColumn.COLUMN_CALL_TYPE};
            String order = FileColumn.COLUMN_ID +" desc limit " + (pageIndex * PERPAGE_NUMBER + offset) + "," + PERPAGE_NUMBER;
            return queryFileListFromDb(fileType, uri, columns, where.toString(), null,  order);
        } else if(fileType == FileProvider.FILE_TYPE_TEL) {// phone record
            StringBuffer where = null;
            String[] args =  null;
            if(launchType == null || launchType.size() == 0){
                where = new StringBuffer(FileColumn.COLUMN_THUMB_NAME + "='" + thumbName + "'");
                args =  new String[]{thumbName};
            } else {
                int index = 1;
                int length = launchType.size();
                args = new String[length+1];
                args[0] = thumbName;
                where = new StringBuffer(FileColumn.COLUMN_CALL_NUMBER + " != '' and (");
                for(Iterator<Integer> set = launchType.iterator(); set.hasNext();){
                    String tmp = String.valueOf(set.next());
                    args[index++] = tmp;
                    where.append(FileColumn.COLUMN_LAUNCH_MODE + " = '" + tmp + "' ");
                    if(index<=length){
                        where.append(" or ");
                    }
                }
                where.append(")) group by (" + FileColumn.COLUMN_CALL_TYPE + " and " + FileColumn.COLUMN_CALL_NUMBER);
            }
            columns = new String[]{FileColumn.COLUMN_ID, FileColumn.COLUMN_LOCAL_PATH, FileColumn.COLUMN_FILE_SIZE, FileColumn.COLUMN_FILE_DURATION,
                    FileColumn.COLUMN_MIME_TYPE, FileColumn.COLUMN_LAUNCH_MODE, FileColumn.COLUMN_FILE_RESOLUTION_X, FileColumn.COLUMN_FILE_RESOLUTION_Y,
                    FileColumn.COLUMN_FILE_THUMBNAIL, FileColumn.COLUMN_SHOW_NOTIFICATION, FileColumn.COLUMN_UP_OR_DOWN, FileColumn.COLUMN_UP_DOWN_LOAD_STATUS,
                    FileColumn.COLUMN_DOWN_LOAD_TIME, FileColumn.COLUMN_UP_LOAD_TIME, FileColumn.COLUMN_CALL_NUMBER,
                    FileColumn.COLUMN_CALL_TYPE, "count (*) as " + FileColumn.COLUMN_CALL_COUNT};
            String order = FileColumn.COLUMN_ID +" desc limit " + (pageIndex * PERPAGE_NUMBER + offset) + ", " + PERPAGE_NUMBER;
            return queryPhoneRecordFromDb(fileType, uri, columns, where.toString(), null,  order);
        } else if(fileType == FileProvider.FILE_TYPE_TEL_LIST) {
            StringBuffer where = null;
            String[] args =  null;
            if(launchType == null || launchType.size() == 0){
                where = new StringBuffer(FileColumn.COLUMN_CALL_NUMBER + "='" + thumbName + "'");
                args =  new String[]{thumbName};
            } else {
                int index = 1;
                int length = launchType.size();
                args = new String[length+1];
                args[0] =  thumbName;
                where = new StringBuffer(FileColumn.COLUMN_CALL_NUMBER + " = '"  + thumbName + "' and ( ");
                for(Iterator<Integer> set = launchType.iterator(); set.hasNext();){
                    String tmp = String.valueOf(set.next());
                    args[index++] = tmp;
                    where.append(FileColumn.COLUMN_LAUNCH_MODE + " = '" + tmp + "' ");
                    if(index<=length){
                        where.append(" or ");
                    }
                }
                where.append(")");
            }
            LogUtil.d(TAG, "where: " + where.toString());
            columns = new String[]{FileColumn.COLUMN_ID, FileColumn.COLUMN_LOCAL_PATH, FileColumn.COLUMN_FILE_SIZE, FileColumn.COLUMN_FILE_DURATION,
                    FileColumn.COLUMN_MIME_TYPE, FileColumn.COLUMN_LAUNCH_MODE, FileColumn.COLUMN_FILE_RESOLUTION_X, FileColumn.COLUMN_FILE_RESOLUTION_Y,
                    FileColumn.COLUMN_FILE_THUMBNAIL, FileColumn.COLUMN_SHOW_NOTIFICATION, FileColumn.COLUMN_UP_OR_DOWN, FileColumn.COLUMN_UP_DOWN_LOAD_STATUS,
                    FileColumn.COLUMN_DOWN_LOAD_TIME, FileColumn.COLUMN_UP_LOAD_TIME, FileColumn.COLUMN_CALL_NUMBER, FileColumn.COLUMN_CALL_TYPE};
            String order = FileColumn.COLUMN_ID +" desc limit " + (pageIndex * PERPAGE_NUMBER + offset) + "," + PERPAGE_NUMBER;
            return queryPhoneRecordFromDb(fileType, uri, columns, where.toString(), null,  order);
        } else {
            StringBuffer where = null;
            String[] args =  null;
            if(launchType == null || launchType.size() == 0){
                where = new StringBuffer(FileColumn.COLUMN_THUMB_NAME + "='" + thumbName + "'");
                args =  new String[]{thumbName};
            } else {
                int index = 1;
                int length = launchType.size();
                args = new String[length+1];
                where = new StringBuffer();
                args[0] = thumbName;
                where = new StringBuffer(FileColumn.COLUMN_THUMB_NAME +"='"+thumbName+"' and ( ");
                for(Iterator<Integer> set = launchType.iterator(); set.hasNext();){
                    String tmp = String.valueOf(set.next());
                    args[index++] = tmp;
                    where.append(FileColumn.COLUMN_LAUNCH_MODE +"='"+ tmp +"' ");
                    if(index<=length){
                        where.append(" or ");
                    }
                }
                where.append(")");
            }
            columns = new String[]{FileColumn.COLUMN_ID, FileColumn.COLUMN_LOCAL_PATH, FileColumn.COLUMN_FILE_SIZE, FileColumn.COLUMN_FILE_DURATION,
                    FileColumn.COLUMN_MIME_TYPE, FileColumn.COLUMN_LAUNCH_MODE, FileColumn.COLUMN_FILE_RESOLUTION_X, FileColumn.COLUMN_FILE_RESOLUTION_Y,
                    FileColumn.COLUMN_FILE_THUMBNAIL, FileColumn.COLUMN_SHOW_NOTIFICATION, FileColumn.COLUMN_UP_OR_DOWN, FileColumn.COLUMN_UP_DOWN_LOAD_STATUS,
                    FileColumn.COLUMN_DOWN_LOAD_TIME, FileColumn.COLUMN_UP_LOAD_TIME, FileColumn.COLUMN_CALL_NUMBER, FileColumn.COLUMN_CALL_TYPE};
            String order = FileColumn.COLUMN_ID +" desc limit " + (pageIndex * PERPAGE_NUMBER) + ", " + offset;
            return queryFileListFromDb(fileType, uri, columns, where.toString(), null,  order);
        }
    }
    
    private List<FileThumb> queryThumbnailListFromDb(int fileType, Uri uri, String[] columns, String where, String[] args, String order){
        List<FileThumb> list = new ArrayList<FileThumb>();
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, args, order);
        if(cursor != null) {
            while(cursor.moveToNext()) {
                int index = 0;
                FileThumb file = new FileThumb();
                file.setFileNumber(cursor.getInt(index++));
                file.setId(cursor.getInt(index++));
                file.setName(cursor.getString(index++));
                file.setCoverPath(cursor.getString(index++));
                file.setModifyTime(cursor.getLong(index++));
                file.setFileType(fileType);
                file.setProvite(false);
                list.add(file);
            }
            cursor.close();
        }
        return list;
    }

    private List<FileThumb> queryThumbnailListFromGalleryDb(int fileType, Uri uri, String[] columns, String where, String[] args, String order){
        List<FileThumb> list = new ArrayList<FileThumb>();
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, args, order);
        if(cursor != null) {
            while(cursor.moveToNext()) {
                int index = 0;
                FileThumb file = new FileThumb();
                file.setFileNumber(cursor.getInt(index++));
                file.setId(cursor.getInt(index++));
                file.setName(cursor.getString(index++));
                file.setCoverPath(cursor.getString(index++));
                file.setModifyTime(cursor.getLong(index++));
                file.setFileType(fileType);
                file.setProvite(false);
                list.add(file);
            }
            cursor.close();
        }
        return list;
    }
    
    private List<FileDetail> queryFileListFromDb(int fileType, Uri uri, String[] columns, String where, String[] args, String order){
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, args, order);
        List<FileDetail> list = new ArrayList<FileDetail>();
        if(cursor != null) {
            while(cursor.moveToNext()) {
                int index = 0;
                FileDetail fileDetail = new FileDetail();
                fileDetail.setId(cursor.getInt(index++));
                String path = cursor.getString(index++);
                fileDetail.setLength(cursor.getInt(index++));
                fileDetail.setDuration(cursor.getInt(index++));
                fileDetail.setMimeType(cursor.getString(index++));
                fileDetail.setLaunchMode(cursor.getInt(index++));
                fileDetail.setFileResolutionX(cursor.getInt(index++));
                fileDetail.setFileResolutionY(cursor.getInt(index++));
                fileDetail.setThumbnailPath(cursor.getString(index++));
                fileDetail.setShowNotification(cursor.getInt(cursor.getInt(index++)));
                fileDetail.setUpload(cursor.getInt(index++));
                fileDetail.setUpDownLoadStatus(cursor.getInt(index++));
                fileDetail.setDownLoadTime(cursor.getLong(index++));
                fileDetail.setUploadTime(cursor.getLong(index++));
                fileDetail.setPhoneNumber(cursor.getString(index++));
                fileDetail.setCallPhoneType(cursor.getInt(index++));
                int callCountColumnIndex = cursor.getColumnIndex(FileColumn.COLUMN_CALL_COUNT);
                if(callCountColumnIndex>0) {
                    fileDetail.setCount(cursor.getInt(callCountColumnIndex));
                }
                fileDetail.setFileType(fileType);
                fileDetail.setFilePath(path);
                String ymd = DateUtil.paserTimeToYMD(fileDetail.getDownLoadTime());
                fileDetail.setTime(ymd);
                if(!mSectionMap.containsKey(ymd)) {
                    fileDetail.setSection(section);
                    mSectionMap.put(ymd, section);
                    section ++;
                } else {
                    fileDetail.setSection(mSectionMap.get(ymd));
                }
                list.add(fileDetail);
            }
            cursor.close();
        }
        return list;
    }

    private List<FileDetail> queryPhoneRecordFromDb(int fileType, Uri uri, String[] columns, String where, String[] args, String order){
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, args, order);
        List<FileDetail> list = new ArrayList<FileDetail>();
        if(cursor != null) {
            while(cursor.moveToNext()) {
                int index = 0;
                FileDetail fileDetail = new FileDetail();
                fileDetail.setId(cursor.getInt(index++));
                String path = cursor.getString(index++);
                fileDetail.setLength(cursor.getInt(index++));
                fileDetail.setDuration(cursor.getInt(index++));
                fileDetail.setMimeType(cursor.getString(index++));
                fileDetail.setLaunchMode(cursor.getInt(index++));
                fileDetail.setFileResolutionX(cursor.getInt(index++));
                fileDetail.setFileResolutionY(cursor.getInt(index++));
                fileDetail.setThumbnailPath(cursor.getString(index++));
                fileDetail.setShowNotification(cursor.getInt(cursor.getInt(index++)));
                fileDetail.setUpload(cursor.getInt(index++));
                fileDetail.setUpDownLoadStatus(cursor.getInt(index++));
                fileDetail.setDownLoadTime(cursor.getLong(index++));
                fileDetail.setUploadTime(cursor.getLong(index++));
                fileDetail.setPhoneNumber(cursor.getString(index++));
                fileDetail.setCallPhoneType(cursor.getInt(index++));
                PersionInfo info = getContactNumber(fileDetail.getPhoneNumber());
                LogUtil.d(TAG, "==> phoneNumber: " + fileDetail.getPhoneNumber());
                if(info != null) {
                    fileDetail.setDisPlayName(info.displayName);
                    fileDetail.setHeader(info.header);
                    Log.d(TAG, "==> load: " + fileDetail.getPhoneNumber() + " " + info.displayName + " " + info.header);
                } else {
                    Log.w(TAG, "==> load: " + fileDetail.getPhoneNumber() + " displayName Fail.");
                }
                int callCountColumnIndex = cursor.getColumnIndex(FileColumn.COLUMN_CALL_COUNT);
                if(callCountColumnIndex>0) {
                    fileDetail.setCount(cursor.getInt(callCountColumnIndex));
                }
                fileDetail.setFileType(fileType);
                fileDetail.setFilePath(path);
                String ymd = DateUtil.paserTimeToYMD(fileDetail.getDownLoadTime());
                fileDetail.setTime(ymd);
                if(!mSectionMap.containsKey(ymd)) {
                    fileDetail.setSection(section);
                    mSectionMap.put(ymd, section);
                    section ++;
                } else {
                    fileDetail.setSection(mSectionMap.get(ymd));
                }
                list.add(fileDetail);
            }
            cursor.close();
        }
        return list;
    }

    private int queryFileCountFromDb(Uri uri, String[] columns, String where, String[] args, String order){
        Cursor cursor = mContext.getContentResolver().query(uri, columns, where, args, order);
        int count = 0;
        if(cursor != null) {
            cursor.moveToFirst();
            if(!cursor.isAfterLast()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    /**
     * 加载网络相册
     * @param mediaType
     * @param pageIndex
     * @param offset
     * @return
     */
    private List<FileThumb> loadFileThumbFromNetwork(int mediaType, int pageIndex, int offset){
        List<FileThumb> list = new ArrayList<FileThumb>();
       /* ResponseStream responseStream = null;
        try {
            responseStream = mHttpUtils.sendSync(HttpMethod.GET, URLS.FILE_LIST+"?type="+mediaType+"&"+"page="+pageIndex+"&offset="+offset);
                System.out.println("responseStream.readString() " +responseStream.getStatusCode());
            if(responseStream.getStatusCode() == HttpStatus.SC_OK) {
                parseThumb(list, new ByteArrayInputStream(responseStream.readString().getBytes()));
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(responseStream != null){
                try {
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                responseStream = null;
            }
        }*/
        return list;
    }
    
    public List<FileThumb> parseThumb(List<FileThumb> list, InputStream inputStream) {
        XmlPullParser xmlPullParser = Xml.newPullParser();
        FileThumb alumb = null;
        try {
            xmlPullParser.setInput(inputStream, "utf-8");
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            int eventType = xmlPullParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch(eventType) {
                case XmlPullParser.START_TAG:
                    String tag = xmlPullParser.getName();
                    if(tag.equalsIgnoreCase("thumb")) {
                        alumb = new FileThumb();
                        list.add(alumb);
                    } else if(alumb != null) {
                        if(tag.equalsIgnoreCase("id")) {
                            alumb.setId(StringUtils.toInt(xmlPullParser.nextText(),0));
                        } else if(tag.equalsIgnoreCase("file_name")) {
                            alumb.setName(xmlPullParser.nextText().trim());
                        } else if(tag.equalsIgnoreCase("file_cover")) {
                            alumb.setCoverPath(xmlPullParser.nextText().trim());
                        } else if(tag.equalsIgnoreCase("file_count")) {
                            alumb.setFileNumber(StringUtils.toInt(xmlPullParser.nextText(),0));
                        } else if(tag.equalsIgnoreCase("file_descrip")) {
                            String desc = xmlPullParser.nextText().trim();
                            alumb.setFileDescribe(desc);
                        } else if(tag.equalsIgnoreCase("file_type")) {
                            alumb.setFileType(StringUtils.toInt(xmlPullParser.nextText(),0));
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据分类获取对应的文件类型
     * @param catalog
     * @return
     */
    private int getFileType(int catalog){
        int type = FileProvider.FILE_TYPE_OTHER;
        switch(catalog){
            case UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE:
            case UIHelper.LISTVIEW_DATATYPE_REMOTE_IMAGE:
                type= FileProvider.FILE_TYPE_JEPG;
                break;
            case UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO:
            case UIHelper.LISTVIEW_DATATYPE_REMOTE_VIDEO:
                type= FileProvider.FILE_TYPE_VIDEO;
                break;
            case UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO:
            case UIHelper.LISTVIEW_DATATYPE_REMOTE_AUDIO:
                type= FileProvider.FILE_TYPE_AUDIO;
                break;
            case UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER:
            case UIHelper.LISTVIEW_DATATYPE_REMOTE_OTHER:
                type= FileProvider.FILE_TYPE_OTHER;
                break;
            case UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE:
                type= FileProvider.FILE_GALLERY_IMAGE;
                break;
            case UIHelper.LISTVIEW_DATATYPE_GALLERY_VIDEO:
                type= FileProvider.FILE_GALLERY_VIDEO;
                break;
            case UIHelper.LISTVIEW_DATATYPE_TEL_AUDIO:
                type= FileProvider.FILE_TYPE_TEL;
                break;
            case UIHelper.LISTVIEW_DATATYPE_TEL_LIST:
                type= FileProvider.FILE_TYPE_TEL_LIST;
                break;
                default:
                    break;
        }
        return type;
    }
    
    private Uri getUri(int mediaType){
        Uri uri = null;
        switch(mediaType){
            case FileProvider.FILE_TYPE_JEPG:
                uri = FileProvider.JPEGS_URI;
                break;
            case FileProvider.FILE_TYPE_VIDEO:
                uri = FileProvider.VIDEOS_URI;
                break;
            case FileProvider.FILE_TYPE_AUDIO:
                uri = FileProvider.AUDIOS_URI;
                break;
            case FileProvider.FILE_GALLERY_IMAGE:
                uri = FileProvider.GALLERY_IMAGE_URI;
                break;
            case FileProvider.FILE_GALLERY_VIDEO:
                uri = FileProvider.GALLERY_VIDEO_URI;
                break;
            case FileProvider.FILE_TYPE_TEL:
                uri = FileProvider.AUDIOS_URI;
                break;
            case FileProvider.FILE_TYPE_TEL_LIST:
                uri = FileProvider.AUDIOS_URI;
                break;
                default:
                    throw new IllegalArgumentException("Unknown/Invalid Media Type " + mediaType);
        }
        return uri;
    }

    private PersionInfo getContactNumber(String phoneNumber) {
        PersionInfo info = null;
        Cursor cursor = null;
        String COLUMN_ID = ContactsContract.PhoneLookup._ID;
        String COLUMN_DISPLAY_NAME = ContactsContract.PhoneLookup.DISPLAY_NAME;
        try {
            ContentResolver resolver = mContext.getContentResolver();
            Uri uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon().appendPath(phoneNumber).build();
            String[] projection = new String[]{COLUMN_ID, COLUMN_DISPLAY_NAME};
            cursor = resolver.query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                info = new PersionInfo();
                int _id = cursor.getColumnIndex(COLUMN_ID);
                int columnIndexName = cursor.getColumnIndex(COLUMN_DISPLAY_NAME);
                int contactId = cursor.getInt(_id);
                String displayName = cursor.getString(columnIndexName);
                Bitmap header = getContactPhoto(mContext, contactId);
                info.displayName = displayName;
                info.header = header;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return info;
    }

    /**
     * 根据联系人的id,获取联系人的头像
     */
    private Bitmap getContactPhoto(Context context, int contactid){
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactid + "");
        //获取联系人的头像,以流的形式返回
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
        //将流转化成bitmap
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        //关流
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private Cursor getLocalVideoThumb(String localPath) {
        Uri localVideoUri = getUri(FileProvider.FILE_TYPE_VIDEO);
        String[] pro = {"thumbnail_path"};
        String localVideoPathWhere = "file_local_path='" + localPath + "'";
        Cursor cursor = mContext.getContentResolver().query(localVideoUri, pro, localVideoPathWhere, null, null);
        return cursor;
    }
    private static class PersionInfo{
        int id;
        String displayName;
        Bitmap header;
    }

}
