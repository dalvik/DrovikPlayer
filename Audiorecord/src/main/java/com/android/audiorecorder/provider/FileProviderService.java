package com.android.audiorecorder.provider;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.audiorecorder.R;
import com.android.audiorecorder.provider.FileTransport.ITransportListener;
import com.android.audiorecorder.ui.SettingsActivity;
import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;

public class FileProviderService extends Service {

    public static final String ROOT_OLD = "BlueRecorder";
    public static final String ROOT = "MediaFile";
    public static final String CATE_RECORD = "Record";
    public static final String CATE_UPLOAD = "Upload";
    public static final String CATE_DOWNLOAD = "Download";
    public static final String TYPE_JPEG = "Jpeg";
    public static final String TYPE_AUDIO = "Audio";
    public static final String TYPE_VIDEO = "Video";
    public static final String TYPE_Other = "Other";
    public static final String THUMBNAIL = "Thumbnail";
    
    private static final int MSG_LOAD_TASK = 0x10;
    private static final int MSG_ANALIZE_FILE = 0x20;
    private static final int MSG_REBUILD_DATABASE = 0x30;
    private static final int MSG_CLEAR_DATABASE = 0x31;
    private static final int MSG_DELETE_FILES = 0x41;
    
    private static final int MSG_UPDATE_NOTIFICATION = 0x51;
    
    private int CUSTOM_VIEW_IMAGE_ID = 1746208400;
    
    public static final String TAG = "FileProviderService";

    private UpDownloadHandlerCallback mUpDownloadHandlerCallback;
    private HandlerThread mUpDownloadThread;
    private Handler mUpDownloadHandler;
    

    private FileTransport mFileTransport;
    private MediaFileProducer mediaFileProducer;
    
    private NotificationManager mNotificationManager;
    private BroadcastReceiver exteranalStorageStateReceiver = null;
    private BroadcastReceiver commandRecv = null;
    
    private SharedPreferences mPreferences;

    private ConnectivityManager mConnectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mPreferences = getSharedPreferences(SettingsActivity.class.getName(), Context.MODE_PRIVATE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mUpDownloadThread = new HandlerThread("DOWN_UP_LOAD_THREAD");
        mUpDownloadHandlerCallback = new UpDownloadHandlerCallback();
        mUpDownloadThread.start();
        mUpDownloadHandler = new Handler(mUpDownloadThread.getLooper(), mUpDownloadHandlerCallback);
        mFileTransport = new FileTransport(this, mTransportListener);
        initReceiver();
        mediaFileProducer = new MediaFileProducer(this);
        mFileTransport.registerObserver();
        mFileTransport.resetTaskState();
        Log.i(TAG, "---> onCreate.");
    }

    private class UpDownloadHandlerCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_TASK:
                	mFileTransport.setUUid(mPreferences.getString(SettingsActivity.KEY_MAC_ADDRESS, "default").replace("_", ""));
                    mFileTransport.loadTransportTask();
                    break;
                case MSG_ANALIZE_FILE:
                    String path = (String)msg.obj;
                    int arg1 = msg.arg1;
                    int id = msg.arg2;
                    mediaFileProducer.updateFileDetail(arg1, path, id);
                    break;
                case MSG_REBUILD_DATABASE:
                    /**
                     * sdcard remount, rebuild database
                     */
                    mediaFileProducer.cleanMediaFile();
                    LogUtil.d(TAG, "==> cleanMediaFile.");
                    mediaFileProducer.loadExistsMediaFiles();
                    LogUtil.d(TAG, "==> loadExistsMediaFiles.");
                    break;
                case MSG_CLEAR_DATABASE:
                    LogUtil.d(TAG, "==> MSG_CLEAR_DATABASE cleanMediaFile.");
                    mediaFileProducer.cleanMediaFile();
                    break;
                case MSG_DELETE_FILES:
                    Bundle bundle = msg.getData();
                    String[] fileList = bundle.getStringArray("_list");
                    mediaFileProducer.deleteFiles(fileList);
                    break;
                case MSG_UPDATE_NOTIFICATION:
                    Bundle data = msg.getData();
                    boolean isUpload = data.getBoolean("is_upload");
                    long current = data.getLong("current");
                    long total = data.getLong("total");
                    boolean result = data.getBoolean("result");
                    String title = null;
                    String contentText = null;
                    if(isUpload){
                        title = getString(R.string.notification_upload_title);
                    } else {
                        title = getString(R.string.notification_download_title);
                    }
                    if(result){
                        if(current == total) {
                            if(isUpload){
                                contentText = getString(R.string.notification_upload_result_success);
                            } else {
                                contentText = getString(R.string.notification_upload_result_success);
                            }
                        } else {
                            contentText = title + StringUtils.formatProgress(current, total);
                        }
                    } else {
                        if(isUpload){
                            contentText = getString(R.string.notification_upload_result_fail);
                        } else {
                            contentText = getString(R.string.notification_download_result_fail);
                        }
                    }
                    updateNotifiaction(isUpload, total, current, title, contentText);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    private ITransportListener mTransportListener = new ITransportListener(){
        
        public void onProgress(boolean isUpload, long total, long current) {
            Log.d(TAG, "onProgress isupload = " + isUpload + " total = " + total + " current = " + current);
            sendNotificationMessage(isUpload, total, current, true);
        }
        
        @Override
        public void onResult(boolean isUpload, boolean success, String path) {
            Log.d(TAG, "onResult isupload = " + isUpload + " path = " + path);
            if(!isUpload && success) {
                Message message = mUpDownloadHandler.obtainMessage(MSG_ANALIZE_FILE);
                message.obj = path;
                message.arg1 = 1;
                message.arg2 = 0;
                mUpDownloadHandler.sendMessage(message);    
            }
            sendNotificationMessage(isUpload, 100, 100, success);
        }
    };
    
    private void sendNotificationMessage(boolean isUpload, long total, long current, boolean result){
        Message msg = mUpDownloadHandler.obtainMessage(MSG_UPDATE_NOTIFICATION);
        Bundle data = msg.getData();
        data.putBoolean("is_upload", isUpload);
        data.putLong("current", current);
        data.putLong("total", total);
        data.putBoolean("result", result);
        //msg.setData(data);
        mUpDownloadHandler.sendMessage(msg); 
    }
    
    private void sendMessage(Handler handler, int msgCode, long delayMillis){
        handler.removeMessages(msgCode);
        handler.sendEmptyMessageDelayed(msgCode, delayMillis);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Notification note = new Notification(0, null, System.currentTimeMillis());
        //note.flags |= Notification.FLAG_NO_CLEAR;
        //startForeground(42, note);
        Log.i(TAG, "onStartCommand.");
        sendMessage(mUpDownloadHandler, MSG_LOAD_TASK, 2000);
        if(intent != null && intent.hasExtra("_list")){
            Message msg = mUpDownloadHandler.obtainMessage(MSG_DELETE_FILES);
            Bundle bundle = msg.getData();
            bundle.putStringArray("_list", intent.getStringArrayExtra("_list"));
            msg.setData(bundle);
            mUpDownloadHandler.sendMessage(msg);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        unregisterReceiver();
    }
    
    private void initReceiver(){
        if(exteranalStorageStateReceiver == null){
            exteranalStorageStateReceiver = new BroadcastReceiver(){
              @Override
                public void onReceive(Context arg0, Intent intent) {
                  Log.i(TAG, "===> Action : " + intent.getAction());
                  if(Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction()) && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                      sendMessage(mUpDownloadHandler, MSG_REBUILD_DATABASE, 3000);
                  } else if ((Intent.ACTION_MEDIA_REMOVED.equals(intent.getAction()) || Intent.ACTION_MEDIA_EJECT.equals(intent.getAction())) && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                      sendMessage(mUpDownloadHandler, MSG_CLEAR_DATABASE, 1000);
                  }
                }  
            };
            IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);
            filter.addAction(Intent.ACTION_MEDIA_SHARED);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            filter.addDataScheme("file");
            registerReceiver(exteranalStorageStateReceiver, filter);
        }
        if(commandRecv == null){
            commandRecv = new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent intent) {
                    if(FileProvider.ACTION_PROVIDER_ONCREATE.equals(intent.getAction())){
                        Log.i(TAG, "===> Recv Provider OnCreate Action.");
                        int initValue = StringUtils.getInt(FileProviderService.this, FileColumn.COLUMN_FILE_INIT, 0);
                        if(initValue == 0) {
                            sendMessage(mUpDownloadHandler, MSG_REBUILD_DATABASE, 10);
                        }
                    } else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                    	mConnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    	NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
                    	if(info != null && info.isAvailable()) {
                    		mFileTransport.setNetworkType(info.getType());
                    		sendMessage(mUpDownloadHandler, MSG_LOAD_TASK, 1000);
                    	}
                    }
                }  
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(FileProvider.ACTION_PROVIDER_ONCREATE);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(commandRecv, filter);
        }
    }
    
    
    private void updateNotifiaction(boolean isUpload, long max, long progress, String title, String contentText) {
        Notification notification = new Notification.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher_recorder)
        .setContentTitle(title)
        .setContentText(contentText)
        .setOngoing(true)
        .setWhen(0)
        //.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, FileUploadTaskActivity.class), PendingIntent.FLAG_UPDATE_CURRENT))
        .build();

        // Notification.Builder will helpfully fill these out for you no
        // matter what you do
        notification.tickerView = null;
        notification.tickerText = null;
        
        notification.priority = Notification.PRIORITY_HIGH;
        //notification.flags |= Notification.FLAG_NO_CLEAR;
        
        //notification.contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, FileManagerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationManager.notify(CUSTOM_VIEW_IMAGE_ID, notification);
    }
    
    private void unregisterReceiver(){
        mFileTransport.unRegisterObserver();
        if(exteranalStorageStateReceiver != null){
            unregisterReceiver(exteranalStorageStateReceiver);
            exteranalStorageStateReceiver = null;
        }
        if(commandRecv != null){
            unregisterReceiver(commandRecv);
            commandRecv = null;
        }
    }
    
    /*
    public static final int MEDIA_TYPE_NONE = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;
    public static final int MEDIA_TYPE_PLAYLIST = 4;
    */
}
