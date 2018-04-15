package com.android.audiorecorder.engine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class TimeSchedule {
    
   
    public static final String ACTION_TIMER_ALARM = "android.recorder.action.TIMER_ALARM";
    
    private Context mContext;
    private PendingIntent mTimerPendingIntent;
    private AlarmManager mAlarmManager;
    
    public TimeSchedule(Context context){
        this.mContext = context;
    }

    /**
     * trigger at once
     */
    public void start(long millisDelayed){
        setRtcTimerAlarm(millisDelayed);
    }
    
    private void setRtcTimerAlarm(long millisDelayed) {
    	cancle();
        Intent intent = new Intent(ACTION_TIMER_ALARM);
        mTimerPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent , PendingIntent.FLAG_ONE_SHOT);
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        }
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ millisDelayed,  AlarmManager.INTERVAL_FIFTEEN_MINUTES/3, mTimerPendingIntent);
    }
    
    public void cancle(){
        if ((mAlarmManager != null) && (mTimerPendingIntent != null)) {
            mAlarmManager.cancel(mTimerPendingIntent);
        }
    }
    
}
