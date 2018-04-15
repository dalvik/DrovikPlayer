/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: JSONUtil.java
 * @description: TODO
 * @author: 23536   
 * @date: 2015年12月23日 下午4:53:05 
 */
package com.android.library.net.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

/** 
 * @description: 字符对象转换工具
 * @author: 23536
 * @date: 2015年12月23日 下午4:53:05  
 */
public class JSONUtil {
    
    private final static Gson gson;

    static {
        GsonBuilder gb = new GsonBuilder();
        gson = gb.create();
    }
    
    /**
     * 将json字符串转换为对象
     *
     * @param json
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T json2Obj(String json, JSONType<T> type) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return (T)gson.fromJson(json, type.getType());
    }
    
    /**
     * 将json字符串转换为对象
     *
     * @param json
     * @param c
     * @return
     */
    public static <T> T json2Obj(String json, Class<T> c) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return gson.fromJson(json, c);
    }

    /**
     * 将对象转换成字符串
     *
     * @param t
     * @return
     */
    public static <T> String obj2Json(T t) {
        String json = gson.toJson(t);
        return json;
    }
    
    /**
     * 将对象转换成字符串
     *
     * @param t
     * @return
     */
    public static <T> String obj2JsonString(T t) {
        Gson gson = new Gson();
        return gson.toJson(t);
    }
    
    /**
     * 将字符串转换成MAP
     *
     * @param t
     * @return
     */
    public static Map<String, Object> json2Map(String json) {
      return gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
    }

    
}
