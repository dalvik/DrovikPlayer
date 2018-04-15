/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: LogUtil.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年1月28日 下午1:25:51 
 */
package com.android.audiorecorder.utils;

import android.util.Log;

import com.android.audiorecorder.DebugConfig;

public class LogUtil {

    private static boolean DEBUG = DebugConfig.DEBUG;
    
    public static void i(String tag, String msg){
        if(DEBUG){
            Log.i(tag, msg);
        }
    }
    
    public static void v(String tag, String msg){
        if(DEBUG){
            Log.v(tag, msg);
        }
    }
    
    public static void d(String tag, String msg){
        if(DEBUG){
            Log.d(tag, msg);
        }
    }
    
    public static void w(String tag, String msg){
        if(DEBUG){
            Log.w(tag, msg);
        }
    }
    
    public static void e(String tag, String msg){
        if(DEBUG){
            Log.e(tag, msg);
        }
    }
}
