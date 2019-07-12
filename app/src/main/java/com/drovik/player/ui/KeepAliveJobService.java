package com.drovik.player.ui;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.audiorecorder.engine.MultiMediaService;

import java.util.List;

@TargetApi(21)
public class KeepAliveJobService extends JobService {
    private final static String TAG = "KeepAliveService";
    private volatile static Service mKeepAliveService = null;

    public static boolean isJobServiceAlive(){
        return mKeepAliveService != null;
    }

    private static final int MESSAGE_ID_TASK = 0x01;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // 具体任务逻辑
            if(!isAPPALive(getApplicationContext(), getPackageName())){
                Intent intent = new Intent(getApplicationContext(), MultiMediaService.class);
                startService(intent);
            }
            // 通知系统任务执行结束
            jobFinished( (JobParameters) msg.obj, false );
            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG,"==> onStartJob");
        mKeepAliveService = this;
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        Message msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params);
        mHandler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeMessages(MESSAGE_ID_TASK);
        Log.i(TAG,"==> onStopJob");
        return false;
    }

    private boolean isAPPALive(Context mContext, String packageName){
        boolean isAPPRunning = false;
        // 获取activity管理对象
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取所有正在运行的app
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        // 遍历，进程名即包名
        for(ActivityManager.RunningAppProcessInfo appInfo : appProcessInfoList){
            if(packageName.equals(appInfo.processName)){
                isAPPRunning = true;
                break;
            }
        }
        return isAPPRunning;
    }
}
