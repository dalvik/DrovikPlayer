package com.android.library;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.library.utils.NotificationUtil;

public class CancelNoticeService extends Service {

    public static int NOTICE_ID = 188080;
    private static final int MSG_EXIT = 1000;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            mHandler.removeMessages(MSG_EXIT);
            mHandler.sendEmptyMessageDelayed(MSG_EXIT, 2000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MqttDaemonService","==> CancelNoticeService---->onDestroy");
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_EXIT:
                    exitSelf();
                    break;
                    default:
                        break;

            }
        }
    };

    private void exitSelf() {
        // 移除DaemonService弹出的通知
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            manager.deleteNotificationChannel(NotificationUtil.CHANNEL_ONE_ID);
        } else {
            Notification.Builder builder = new Notification.Builder(CancelNoticeService.this);
            builder.setSmallIcon(R.drawable.ic_launche);
            startForeground(NOTICE_ID, builder.build());
            stopForeground(false);
            manager.cancel(NOTICE_ID);
        }
        // 任务完成，终止自己
        Log.i("MqttDaemonService","==> CancelNoticeService---->onStartCommand");
        stopSelf();
    }
}
