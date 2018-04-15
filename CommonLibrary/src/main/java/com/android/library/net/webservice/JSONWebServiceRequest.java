/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: JSONWebServiceRequest.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年1月5日 下午3:13:23 
 */
package com.android.library.net.webservice;

import android.util.Log;

import com.android.library.net.req.DataReq;
import com.android.library.net.utils.JSONUtil;

import org.ksoap2.serialization.SoapObject;

import java.util.Map;

/** 
 * @description: 基于JSON的webservice请求
 * @author: 23536
 * @date: 2016年1月5日 下午3:13:23  
 */
public abstract class JSONWebServiceRequest<T extends DataReq> extends WebServiceRequest {

    private T data;

    private String TAG = "JSONWebServiceRequest";
    
    public JSONWebServiceRequest(T t) {
        data = t;
    }

    public String getJsonData(){
        if (data == null) {
            return "{}";
        }
        return JSONUtil.obj2Json(data);
    }

    @Override
    public SoapObject getSoapObject() {
        SoapObject object = new SoapObject(getNamespace(), getMethod());
        Map<String, Object> map = JSONUtil.json2Map(getJsonData());
        for(String key:map.keySet()){
            object.addProperty(key, map.get(key));
            Log.d(TAG, key + "=" + map.get(key));
        }
        return object;
    }
}
