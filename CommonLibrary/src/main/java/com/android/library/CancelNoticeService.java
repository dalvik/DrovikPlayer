package com.android.library;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

public class CancelNoticeService extends Service {

    public static int NOTICE_ID = 188080;


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
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.drawable.ic_launche);
            startForeground(NOTICE_ID, builder.build());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(2000);
                    // 取消CancelNoticeService的前台
                    stopForeground(false);
                    // 移除DaemonService弹出的通知
                    NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(NOTICE_ID);
                    // 任务完成，终止自己
                    Log.i("MqttDaemonService","==> CancelNoticeService---->onStartCommand");
                    stopSelf();
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MqttDaemonService","==> CancelNoticeService---->onDestroy");
    }
}
