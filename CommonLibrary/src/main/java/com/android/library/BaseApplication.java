package com.android.library;

//import org.xutils.BuildConfig;
//import org.xutils.x;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.android.library.net.utils.LogUtil;

public class BaseApplication extends Application {

    public static final String VERSION_CODE = "001.100.010";
    
    public static Context curContext;
    public static Handler handler;
    public static boolean DEBUG = true;
    
    @Override
    public void onCreate() {
        super.onCreate();
       // x.Ext.init(this);
        //x.Ext.setDebug(BuildConfig.DEBUG);
        handler = new Handler();
        curContext = this;
        LogUtil.i("BaseApplication", "==> BaseApplication onCreate.");
    }

    public static long getRealTime() {
        return System.currentTimeMillis();
    }

    public static String getSid() {
        return "sid_001_002_003";
    }
    
}