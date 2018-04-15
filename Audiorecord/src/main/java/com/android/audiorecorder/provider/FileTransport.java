package com.android.audiorecorder.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileTransport {

    private Context mContext;

    private FileObserver mFileObserver;
    
    private List<TaskInfo> taskList;
    
    private boolean isTaskTransporting;
    
    private String TAG = "FileTransport";
    
    private ITransportListener mTransportListener;

    //private HttpUtils mHttpUtils;

	private String mUUid;
	
	private int mNetworkType;
    
    public FileTransport(Context context, ITransportListener listener){
        this.mContext = context;
        this.mTransportListener = listener;
        taskList = new ArrayList<TaskInfo>();
        //mHttpUtils = new HttpUtils();
    }
    
    public void resetTaskState(){
        ContentValues values = new ContentValues();
        values.put(FileColumn.COLUMN_UP_DOWN_LOAD_STATUS, FileColumn.STATE_FILE_UP_DOWN_WAITING);
        mContext.getContentResolver().update(FileProvider.TASK_URI, values, FileColumn.COLUMN_UP_DOWN_LOAD_STATUS + " = " + FileColumn.STATE_FILE_UP_DOWN_ING, null);
    }
    
    public void registerObserver(){
        if (mFileObserver == null) {
            mFileObserver = new FileObserver(new Handler());
            mContext.getContentResolver().registerContentObserver(FileProvider.TASK_URI, true, mFileObserver);
        }
    }
    
    public void unRegisterObserver(){
        if (mFileObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(mFileObserver);
            mFileObserver = null;
        }
    }
    
    private class FileObserver extends ContentObserver {

        public FileObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(TAG, "onChange:" + uri.toString());
            loadTransportTask();
        }

    }
    
    private void popTransportTask(){
        isTaskTransporting = true;
        int size = taskList.size(); 
        if(size == 0){
            isTaskTransporting = false;
            Log.i(TAG, "---> no transport task.");
        } else {
            Collections.sort(taskList, new FileComparator());
            Log.i(TAG, "---> start transport.");
            TaskInfo info = taskList.get(0);
            System.out.println(info.mode + " mode.");
            startTransport(info.id, info.isUpload, info.path, info.isShowNotification, info.mode, info.type);
        }
    }
    
    private void startTransport(final int id, final boolean isUpload, String path, final boolean isShowNotifiaction, int mode, int type) {
    	if(mNetworkType != ConnectivityManager.TYPE_WIFI){
    		Log.w(TAG, "==> not work type wifi.");
    		return;
    	}
        if(isUpload){
            uploadRequest(id, path, isShowNotifiaction, mode, type);
        } else {
            downloadRequest(id, path, isShowNotifiaction);   
        }
    }
    
    private void downloadRequest(final int id, String path, final boolean isShowNotifiaction){
        /*final String fileName = path.substring(path.lastIndexOf("/")+1);
        RequestCallBack<File> requestCallBack = new RequestCallBack<File>() {
            @Override
            public void onStart() {
                super.onStart();
                Log.d(TAG, "start...");
                ContentValues values = new ContentValues();
                values.put(FileColumn.COLUMN_UP_DOWN_LOAD_STATUS, FileColumn.STATE_FILE_UP_DOWN_ING);
                mContext.getContentResolver().update(FileProvider.TASK_URI, values, FileColumn.COLUMN_ID + "=" + id, null);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                if(isShowNotifiaction && mTransportListener != null){
                    mTransportListener.onProgress(isUploading, total, current);
                }
                if(DebugConfig.DEBUG){
                    if (isUploading) {
                        Log.d(TAG, "onUploading: " + current + "/" + total);
                    } else {
                        Log.d(TAG, "onDownLoading: " + current + "/" + total);
                    }
                }
                ContentValues values = new ContentValues();
                values.put(FileColumn.COLUMN_UP_LOAD_BYTE, current);
                mContext.getContentResolver().update(FileProvider.TASK_URI, values, FileColumn.COLUMN_ID + "=" + id, null);
            }

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                Log.d(TAG, "onSuccess reply: " + responseInfo.result);
                if(isShowNotifiaction && mTransportListener != null){
                    mTransportListener.onResult(false, true, responseInfo.result.getPath());
                }
                clearTop();
            }

            @Override
            public void onFailure(HttpException arg0, String msg) {
                Log.w(TAG, "onFailure = " + msg);
                ContentValues values = new ContentValues();
                values.put(FileColumn.COLUMN_UP_DOWN_LOAD_STATUS, FileColumn.STATE_FILE_UP_DOWN_FAILED);
                values.put(FileColumn.COLUMN_UP_LOAD_TIME, System.currentTimeMillis());
                values.put(FileColumn.COLUMN_UP_LOAD_MESSAGE, msg);
                mContext.getContentResolver().update(FileProvider.TASK_URI, values, FileColumn.COLUMN_ID + "=" + id, null);
                if(isShowNotifiaction && mTransportListener != null){
                    mTransportListener.onResult(false, false, "");
                }
                clearTop();
            }
        };
        mHttpUtils.download(path, Environment.getExternalStorageDirectory().getPath() + "/" + FileProviderService.ROOT + "/" + FileProviderService.CATE_DOWNLOAD +"/"+fileName, requestCallBack);*/
    }
    
    private void uploadRequest(final int id, String path, final boolean isShowNotifiaction, int mode, int type){
        /*RequestParams params = new RequestParams();
        params.addBodyParameter("file", new File(path));
        params.addBodyParameter("uuid", mUUid);
        params.addBodyParameter("mode", String.valueOf(mode));
        params.addBodyParameter("type", String.valueOf(type));
        mHttpUtils.send(HttpMethod.POST, getRemoteUrl(), params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                Log.d(TAG, "start...");
                ContentValues values = new ContentValues();
                values.put(FileColumn.COLUMN_UP_DOWN_LOAD_STATUS, 1);
                mContext.getContentResolver().update(FileProvider.TASK_URI, values, FileColumn.COLUMN_ID + "=" + id, null);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                if(isShowNotifiaction && mTransportListener != null){
                    mTransportListener.onProgress(isUploading, total, current);
                }
                if (isUploading) {
                    Log.d(TAG, "onUploading: " + current + "/" + total);
                } else {
                    Log.d(TAG, "onUploading: " + current + "/" + total);
                }
                ContentValues values = new ContentValues();
                values.put(FileColumn.COLUMN_UP_LOAD_BYTE, current);
                mContext.getContentResolver().update(FileProvider.TASK_URI, values, FileColumn.COLUMN_ID + "=" + id, null);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.d(TAG, "onSuccess reply: " + responseInfo.result);
                ContentValues values = new ContentValues();
                values.put(FileColumn.COLUMN_UP_DOWN_LOAD_STATUS, 2);
                values.put(FileColumn.COLUMN_UP_LOAD_TIME, System.currentTimeMillis());
                String result = responseInfo.result;
                values.put(FileColumn.COLUMN_REMOTE_PATH, result);
                mContext.getContentResolver().update(FileProvider.TASK_URI, values, FileColumn.COLUMN_ID + "=" + id, null);
                if(isShowNotifiaction && mTransportListener != null){
                    mTransportListener.onResult(true, true, result);
                }
                clearTop();
            }

            @Override
            public void onFailure(HttpException arg0, String msg) {
                Log.e(TAG, msg);
                ContentValues values = new ContentValues();
                values.put(FileColumn.COLUMN_UP_DOWN_LOAD_STATUS, 3);
                values.put(FileColumn.COLUMN_UP_LOAD_TIME, System.currentTimeMillis());
                values.put(FileColumn.COLUMN_UP_LOAD_MESSAGE, msg);
                mContext.getContentResolver().update(FileProvider.TASK_URI, values, FileColumn.COLUMN_ID + "=" + id, null);
                if(isShowNotifiaction && mTransportListener != null){
                    mTransportListener.onResult(true, false, msg);
                };
                clearTop();
            }
        });*/
    }
    
    public void loadTransportTask(){
        String[] pro = {FileColumn.COLUMN_ID, FileColumn.COLUMN_LOCAL_PATH, FileColumn.COLUMN_REMOTE_PATH, FileColumn.COLUMN_UP_OR_DOWN, FileColumn.COLUMN_SHOW_NOTIFICATION,
        		FileColumn.COLUMN_LAUNCH_MODE, FileColumn.COLUMN_FILE_TYPE};
        String selection = FileColumn.COLUMN_UP_DOWN_LOAD_STATUS + " == " + FileColumn.STATE_FILE_UP_DOWN_WAITING;
        String sortOrder = FileColumn.COLUMN_SHOW_NOTIFICATION + " desc ";
        Cursor cursor = mContext.getContentResolver().query(FileProvider.TASK_URI, pro, selection, null, sortOrder);
        if(cursor != null){
            while(cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(FileColumn.COLUMN_ID));
                String remote = cursor.getString(cursor.getColumnIndex(FileColumn.COLUMN_REMOTE_PATH));
                int upOrDown = cursor.getInt(cursor.getColumnIndex(FileColumn.COLUMN_UP_OR_DOWN));//0 up 1 down
                String local = cursor.getString(cursor.getColumnIndex(FileColumn.COLUMN_LOCAL_PATH));
                int notifiaction = cursor.getInt(cursor.getColumnIndex(FileColumn.COLUMN_SHOW_NOTIFICATION));
                int mode = cursor.getInt(cursor.getColumnIndex(FileColumn.COLUMN_LAUNCH_MODE));
                int type = cursor.getInt(cursor.getColumnIndex(FileColumn.COLUMN_FILE_TYPE));
                if(!checkExistsTask(id)){// not exists transport list
                    TaskInfo info = new TaskInfo();
                    info.id = id;
                    info.isUpload = (upOrDown == FileColumn.FILE_UP_LOAD);
                    info.path = info.isUpload ? local: remote;
                    info.isShowNotification = (notifiaction == 1);
                    info.mode = mode;
                    info.type = type;
                    taskList.add(info);
                }
                if(!isTaskTransporting){
                    popTransportTask();
                }
            }
            cursor.close();
        }
    }
    
    public void setUUid(String uuid){
    	this.mUUid = uuid;
    }
    
    public int addTransportTask(int id){
        
        return 0;
    }
    
    public void removeTransportTask(int id){
        
    }
    
    public List<TaskInfo> getTransportTaskList(){
        return taskList;
    }
    
    public void setNetworkType(int type) {
    	mNetworkType = type;
    	Log.d(TAG, "==> setNetworkType: " + mNetworkType);
    }
    
    private String getRemoteUrl(){
    	return StringUtils.getString(mContext, FileColumn.COLUMN_SERVER_UPLOAD_URL, "");
    }
    
    private void clearTop(){
        if(taskList.size()>0){
            taskList.remove(0);
            popTransportTask();
        }
    }
    //avoid load repead
    private boolean checkExistsTask(int id){
        for(TaskInfo info:taskList){
            if(info.id == id){
                return true;
            }
        }
        return false;
    }
    
    private class TaskInfo {
        int id;
        String path;
        boolean isUpload;
        boolean isShowNotification;
        int mode;
        int type;
    }
    
    class FileComparator implements Comparator<TaskInfo> {

        public int compare(TaskInfo file1, TaskInfo file2) {
            if(file1.mode == MultiMediaService.LUNCH_MODE_AUTO){
                return 1000;
            } else {
                return -1000;
            }
        }
    }
    
    public interface ITransportListener{
       void onProgress(boolean isUpload, long total, long current);
       void onResult(boolean isUpload, boolean success, String path);
    }
}
