/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: HttpPostJSONRequest.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年1月6日 上午10:24:20 
 */
package com.android.library.net.http;

import com.android.library.BaseApplication;
import com.android.library.net.req.DataReq;
import com.android.library.net.utils.JSONUtil;
import com.android.library.utils.SecurityUtils;
import com.android.library.utils.TextUtils;

import java.util.HashMap;

/** 
 * @description: TODO
 * @author: 23536
 * @date: 2016年1月6日 上午10:24:20  
 */
public abstract class AbstractHttpPostJSONRequest<T extends DataReq> extends AbstractHTTPPostRequest {

    private T data;

    public AbstractHttpPostJSONRequest(T t) {
        data = t;
    }
    
    public String getJsonData(){
        if (data == null) {
            return "{}";
        }
        return JSONUtil.obj2Json(data);
    }

    @Override
    public byte[] getData() {
        return getJsonData().getBytes();
    }

    @Override
    public HashMap<String, String> getHeader() {
        HashMap<String, String> headers = new HashMap<String, String>();
        long time = getRealTime();
        //签名
        headers.put("m-sign", getSign(time));
        //SID
        headers.put("m-sid", getSID());
        //协议版本
        headers.put("m-v", getV());
        //APP版本
        headers.put("m-av", "" + BaseApplication.VERSION_CODE);
        //时间
        headers.put("m-t", "" + time);
        //IMEI
        //headers.put("m-imei", ClientInfo.getInstance().imei);
        //API名称
        headers.put("m-api", getAPI());
        return headers;
    }
    
    private String getSign(long time) {
        StringBuilder builder = new StringBuilder();
        builder.append(getAPI());
        builder.append("&" + BaseApplication.VERSION_CODE);
        /*if (!TextUtils.isEmpty(ClientInfo.getInstance().imei)) {
            builder.append("&" + ClientInfo.getInstance().imei);
        }*/
        if (!TextUtils.isEmpty(getSID())) {
            builder.append("&" + getSID());
        }
        builder.append("&" + time);
        builder.append("&" + getV());
        builder.append("&" + getJsonData());
        String value = builder.toString();
        //LogUtil.e(TAG,value);
        return SecurityUtils.sign(value, SecurityUtils.KEY);
    }
    private String getV() {
        return "1.0";
    }
    
    private String getSID() {
        return BaseApplication.getSid();
    }
    
    private long getRealTime() {
        return BaseApplication.getRealTime();
    }
    
    public abstract String getAPI();
}
