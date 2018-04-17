package com.android.audiorecorder.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.android.audiorecorder.engine.AudioRecordSystem;
import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.utils.DateUtil;
import com.android.audiorecorder.utils.FileUtils;
import com.android.audiorecorder.utils.StringUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MediaFileProducer {

    private Context mContext;
    private String TAG = "MediaFileProducer";
    
    public MediaFileProducer(Context context){
        this.mContext = context;
    }

    public void loadExistsMediaFiles(){
    	StringUtils.putValue(mContext, FileColumn.COLUMN_FILE_INIT, 0);
        String PREFIX = Environment.getExternalStorageDirectory().getPath();
        Log.d(TAG, "==> external path: " + PREFIX);
        String completeDirectory = PREFIX + File.separator + FileProviderService.ROOT + File.separator;
        File parentDirectory = new File(completeDirectory);
        List<String> filePaths = new ArrayList<String>();
        String oldDirectory = PREFIX + File.separator + FileProviderService.ROOT_OLD + File.separator;
        File oldParentDirectory = new File(oldDirectory);
        if(oldParentDirectory.exists()){
            putFilePathToList(filePaths, oldParentDirectory);
        }
        insertFileListDetail(filePaths);
        filePaths.clear();
        if(parentDirectory.exists()){
            putFilePathToList(filePaths, parentDirectory);
        }
        insertFileListDetail(filePaths);
        StringUtils.putValue(mContext, FileColumn.COLUMN_FILE_INIT, 1);
    }
    
    public void updateFileDetail(int arg1, String path, int id){
        ContentValues values = new ContentValues();
        values.put(FileColumn.COLUMN_UP_DOWN_LOAD_STATUS, FileColumn.STATE_FILE_UP_DOWN_SUCCESS);
        if(arg1 == 1){//download
            putContentValues(path, values);
        } else if(arg1 == 2){//upload
        }
        mContext.getContentResolver().update(FileProvider.TASK_URI, values, FileColumn.COLUMN_ID + "=" + id, null);
    }
    
    public void deleteFiles(String[] fileList) {
        if(fileList != null && fileList.length>0){
            for(String name:fileList){
                File file = new File(name);
                if(file.delete()){
                    Log.i(TAG, "---> delete " + name);
                }
                FileUtils.deleteEmptyDirectory(file.getParent());
            }
        }
    }
    
    public void cleanMediaFile(){
        mContext.getContentResolver().delete(FileProvider.JPEGS_URI, null, null);
    }
    
    private void putFilePathToList(List<String> filePaths, File file){
        File[] files = file.listFiles();
        if(files == null) {
            return ;
        }
        for(File f:files){
            if(f.isFile()){
                if(!f.getParent().contains(FileProviderService.THUMBNAIL) && !f.getName().contains(".nomedia")){
                    String fileAbsolutePath = f.getAbsolutePath();
                    Log.i(TAG, "==> putFilePathToList " + fileAbsolutePath);
                    if(!isRecordExists(fileAbsolutePath)){
                		filePaths.add(fileAbsolutePath);
                	} else {
                        Log.d(TAG, "==> path: " + fileAbsolutePath + " not exists.");
                    }
                }
            } else {
                putFilePathToList(filePaths, f);
            }
        }
    }
    
    private void insertFileListDetail(List<String> filePaths){
        if(filePaths.size()>0){
            mContext.getContentResolver().bulkInsert(FileProvider.JPEGS_URI, generalFileDetails(filePaths));
            Log.d(TAG, "==> initMediaFile number = " + filePaths.size());
        } else {
            Log.w(TAG, "==> initMediaFile none." );
        }
    }
    
    private ContentValues[] generalFileDetails(List<String> filePaths){
        int length = filePaths.size();
        ContentValues[] valueArray = new ContentValues[length];
        for (int i=0; i<length; i++) {
            ContentValues values = new ContentValues();
            putContentValuesDefault(filePaths.get(i), values);
            valueArray[i] = values;
        }
        return valueArray;
    }
    
    private void putContentValuesDefault(String path, ContentValues values){
        FileDetail detail = new FileDetail(path);
        values.put(FileColumn.COLUMN_LOCAL_PATH, path);
        values.put(FileColumn.COLUMN_FILE_TYPE, detail.getFileType());
        values.put(FileColumn.COLUMN_MIME_TYPE, detail.getMimeType());
        values.put(FileColumn.COLUMN_FILE_SIZE, detail.getLength());
        if(path.contains(FileProviderService.CATE_DOWNLOAD)){
            values.put(FileColumn.COLUMN_UP_OR_DOWN, 1);
            values.put(FileColumn.COLUMN_UP_DOWN_LOAD_STATUS, 2);
        }
        values.put(FileColumn.COLUMN_THUMB_NAME, StringUtil.getYearMonthWeek(detail.getLastModifyTime()));
        AudioRecordInfo info = null;
        boolean isExternal = false;
        if(path.startsWith(Environment.getExternalStorageDirectory().getPath())) {
        	isExternal = true;
        	info = queryFile(path, isExternal);
        } else {
        	info = queryFile(path, isExternal);
        }
        if(info != null){
        	values.put(FileColumn.COLUMN_FILE_DURATION, info.duration);
        	LogUtil.i(TAG, "==>path:" + path + " duration: " + info.duration + " extrnal: " + isExternal );
        } else {
        	int duration = getMediaDuration(path);
        	values.put(FileColumn.COLUMN_FILE_DURATION, duration);
        }
        values.put(FileColumn.COLUMN_DOWN_LOAD_TIME, detail.getLastModifyTime());
        values.put(FileColumn.COLUMN_UP_LOAD_TIME, detail.getLastModifyTime());
        values.put(FileColumn.COLUMN_FILE_RESOLUTION_X, detail.getFileResolutionX());
        values.put(FileColumn.COLUMN_FILE_RESOLUTION_Y, detail.getFileResolutionY());
        values.put(FileColumn.COLUMN_FILE_THUMBNAIL, detail.getThumbnailPath());
        String fileName = detail.getFileName();
        if(fileName.startsWith(MultiMediaService.PRE_MIC)){//M
        	values.put(FileColumn.COLUMN_LAUNCH_MODE, MultiMediaService.LUNCH_MODE_MANLY);
        } else if(fileName.startsWith(MultiMediaService.PRE_TELI)){//TI
            putPhoneNumberAndTime(values, fileName);
            values.put(FileColumn.COLUMN_CALL_TYPE, AudioRecordSystem.PHONE_CALL_IN);
        	values.put(FileColumn.COLUMN_LAUNCH_MODE, MultiMediaService.LUNCH_MODE_CALL);
        } else if(fileName.startsWith(MultiMediaService.PRE_TELO)){//TO
            putPhoneNumberAndTime(values, fileName);
            values.put(FileColumn.COLUMN_CALL_TYPE, AudioRecordSystem.PHONE_CALL_OUT);
            values.put(FileColumn.COLUMN_LAUNCH_MODE, MultiMediaService.LUNCH_MODE_CALL);
        } else if(fileName.startsWith(MultiMediaService.PRE_AUT)){//A
        	values.put(FileColumn.COLUMN_LAUNCH_MODE, MultiMediaService.LUNCH_MODE_AUTO);
        } else if(fileName.startsWith(MultiMediaService.PRE_TEL)){//contains old
            putPhoneRecordTime(values, fileName);
            values.put(FileColumn.COLUMN_CALL_TYPE, AudioRecordSystem.PHONE_CALL_UNKNOWN);
            values.put(FileColumn.COLUMN_LAUNCH_MODE, MultiMediaService.LUNCH_MODE_CALL);
        } else {
        	if(path.contains(FileProviderService.TYPE_AUDIO)){//audo recoder
        		values.put(FileColumn.COLUMN_LAUNCH_MODE, MultiMediaService.LUNCH_MODE_MANLY);
        	} else if(path.contains(mContext.getPackageName())){//package name
        		values.put(FileColumn.COLUMN_LAUNCH_MODE, MultiMediaService.LUNCH_MODE_AUTO);
        	}
        }
    }
    
    private void putContentValues(String path, ContentValues values){
        String[] projection = { MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.SIZE,
                MediaStore.Audio.Media.DURATION, MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.Audio.Media.WIDTH, MediaStore.Audio.Media.HEIGHT, MediaStore.Files.FileColumns._ID};
        String where = MediaStore.Files.FileColumns.DATA + " like '%" + path + "'";
        Uri uri = MediaStore.Files.getContentUri("external");
        Cursor cursor = mContext.getContentResolver().query(uri, projection, where, null, null);
        values.put(FileColumn.COLUMN_LOCAL_PATH, path);
        if(cursor != null){
        	try{
        		if(cursor.moveToNext()){
                    int index = 0;
                    int mediaType = cursor.getInt(index++);
                    if(mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_NONE){
                        mediaType = FileProvider.FILE_TYPE_OTHER;
                    } else if(mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                        mediaType = FileProvider.FILE_TYPE_JEPG;
                    } else if(mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO){
                        mediaType = FileProvider.FILE_TYPE_AUDIO;
                    } else if(mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO){
                        mediaType = FileProvider.FILE_TYPE_VIDEO;
                    } else if(mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST){
                        mediaType = FileProvider.FILE_TYPE_AUDIO;
                    }
                    values.put(FileColumn.COLUMN_FILE_TYPE, mediaType);
                    values.put(FileColumn.COLUMN_MIME_TYPE, cursor.getString(index++));
                    values.put(FileColumn.COLUMN_FILE_SIZE, cursor.getInt(index++));
                    values.put(FileColumn.COLUMN_FILE_DURATION, cursor.getInt(index++));
                    long createTime = cursor.getInt(index++) * 1000;
                    values.put(FileColumn.COLUMN_DOWN_LOAD_TIME, createTime);
                    values.put(FileColumn.COLUMN_THUMB_NAME, StringUtil.getYearMonthWeek(createTime));
                    values.put(FileColumn.COLUMN_FILE_RESOLUTION_X, cursor.getInt(index++));
                    values.put(FileColumn.COLUMN_FILE_RESOLUTION_Y, cursor.getInt(index++));
                    
                    if(mediaType == FileProvider.FILE_TYPE_VIDEO){
                        int id = cursor.getInt(index++);
                        String selection = MediaStore.Video.Thumbnails.VIDEO_ID +"=?";
                        String[] selectionArgs = new String[]{String.valueOf(id)};
                        Cursor thumbCursor = mContext.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Thumbnails.DATA}, selection, selectionArgs, null);
                        if(thumbCursor != null){
                            if(thumbCursor.moveToNext()){
                                values.put(FileColumn.COLUMN_FILE_THUMBNAIL, thumbCursor.getString(0));
                            }
                            thumbCursor.close();
                        }
                    }
                    if(path.contains(FileProviderService.CATE_DOWNLOAD)){
                        values.put(FileColumn.COLUMN_UP_LOAD_TIME, createTime);
                        values.put(FileColumn.COLUMN_UP_OR_DOWN, 1);
                        values.put(FileColumn.COLUMN_UP_DOWN_LOAD_STATUS, 2);
                    }
                    values.put(FileColumn.COLUMN_LAUNCH_MODE, 2);
                }
        	} catch (Exception e){
        		e.printStackTrace();
        	} finally {
        		if(cursor != null){
        			cursor.close();
        		}
        	}
        } else {
            putContentValuesDefault(path, values);
        }
    }
    
    private int getMediaDuration(String path) {
        /* try {
            URL url = new URL(Uri.encode(path, "-![.:/,%?&=]"));
            Log.d(TAG,"==> play url2: " + url);
            mMediaPlayer.setDataSource(mContext, url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
        int duration = 0;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            File file = new File(path);
            long fileSize = file.length();
            if(file.exists() && fileSize>0) {
                FileInputStream fis = new FileInputStream(file);
                retriever.setDataSource(fis.getFD());
                String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                duration = StringUtil.toInt(durationStr);
                retriever.release();
                Log.i(TAG, "==> durationStr " + durationStr + " duration: " + duration );
            } else {
                Log.w(TAG, "==> remove path: " + path + " size: " + fileSize);
                file.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duration;
    }
    
    private boolean isRecordExists(String path){
    	String[] pro = {FileColumn.COLUMN_ID};
    	String where = FileColumn.COLUMN_LOCAL_PATH + "='" + path +"'";
    	boolean isExists = false;
    	Cursor cursor = mContext.getContentResolver().query(FileProvider.ALL_URI, pro, where, null, null);
    	if(cursor != null){
            if(cursor.getCount()>0) {
                isExists = true;
            }
            cursor.close();
    	}
    	return isExists;
    }
    
    private AudioRecordInfo queryFile(String path, boolean isExternal){
        String[] pro = {MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED};
        String selection = MediaStore.Audio.Media.DATA + "='" + path + "'";
        Uri uri = isExternal ? MediaStore.Audio.Media.EXTERNAL_CONTENT_URI : MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        Cursor cursor = mContext.getContentResolver().query(uri, pro, selection, null, null);
        AudioRecordInfo info = null;
        if(cursor != null){
            if(cursor.moveToNext()) {
            	info = new AudioRecordInfo();
                int index = 0;
                info.size = cursor.getInt(index++);
                info.duration = cursor.getInt(index++);
                info.createTime = cursor.getLong(index++);
            }
            cursor.close();
                
        }
        return info;
    }

    /**
     * 根据文件名解析电话号码和时间戳
     * @param values
     * @param fileName
     */
    private void putPhoneNumberAndTime(ContentValues values, String fileName) {
        int endIndex = fileName.indexOf("_");
        if(endIndex>2) {
            String number = fileName.substring(2, endIndex);
            values.put(FileColumn.COLUMN_CALL_NUMBER, number);
            int dotIndex = fileName.lastIndexOf(".");
            if(endIndex+1 < dotIndex) {
                String time = fileName.substring(endIndex + 1, dotIndex);
                Date date = DateUtil.getDateByYMDHMS(time);
                if(date != null) {
                    values.put(FileColumn.COLUMN_UP_LOAD_TIME, date.getTime());
                    Log.i(TAG, "==> putPhoneNumberAndTime: " + number + " : " + time);
                } else {
                    Log.e(TAG, "==> putPhoneNumberAndTime fileName: " + fileName + " time: " + time);
                }
            } else {
                Log.i(TAG, "==> putPhoneNumberAndTime: " + number);
            }
        } else {
            Log.w(TAG, "==> putPhoneNumberAndTime: '_' endIndex " + endIndex + " name: " + fileName);
        }
    }

    /**
     * 根据录音文件名解析时间戳
     * @param values
     * @param fileName
     */
    private void putPhoneRecordTime(ContentValues values, String fileName) {
        int startKeyIndex = fileName.indexOf(MultiMediaService.PRE_TEL);
        if(startKeyIndex>=0) {
            int dotIndex = fileName.lastIndexOf(".");
            if(startKeyIndex+1 < dotIndex) {
                String time = fileName.substring(startKeyIndex + 1, dotIndex);
                Date date = DateUtil.getDateByYMDHMS(time, "yyyyMMddHHmmss");
                if(date != null) {
                    values.put(FileColumn.COLUMN_CALL_NUMBER, AudioRecordSystem.PHONE_NUMBER_UNKNOWN);
                    values.put(FileColumn.COLUMN_UP_LOAD_TIME, date.getTime());
                    Log.i(TAG, "putPhoneRecordTime time: " + time);
                } else {
                    Log.e(TAG, "putPhoneRecordTime: " + fileName + " time: " + time);
                }
            } else {
                Log.i(TAG, "putPhoneRecordTime fail.");
            }
        } else {
            Log.w(TAG, "putPhoneRecordTime: " +fileName + " index " + startKeyIndex);
        }
    }

    static class AudioRecordInfo {
    	public int size;
    	public int duration;
    	public long createTime;
    }
}

