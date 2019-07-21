package com.android.library.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

public class NotificationUtil {

    public static final String CHANNEL_ONE_ID = "com.drovik.player";
    public static final String CHANNEL_ONE_NAME = "Notify";
    public static final String title = "卓维影音";
    public static final String content = "卓维影音正在运行";

    private NotificationUtil() {

    }

    public static Notification getNotification(Context context, String title, String body, int icon, PendingIntent intent, String channelId){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notification notification = new Notification.Builder(context, channelId)
                    .setTicker(title)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(intent)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(icon)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),icon))
                    .build();
            return notification;
        } else {
            Notification notification = new Notification.Builder(context)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setTicker(title)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(intent)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(icon)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),icon))
                    .build();
            return notification;
        }
    }

    public static Intent makeTarget(String target) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
