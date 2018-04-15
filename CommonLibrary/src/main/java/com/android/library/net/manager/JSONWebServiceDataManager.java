/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: JSONDataManager.java
 * @description: 基于JSON的数据请求
 * @author: 23536   
 * @date: 2015年12月23日 下午3:23:30 
 */
package com.android.library.net.manager;

import com.android.library.net.base.AbstractData;
import com.android.library.net.base.IDataCallback;
import com.android.library.net.req.DataReq;
import com.android.library.net.utils.JSONType;
import com.android.library.net.webservice.JSONWebService;

/** 
 * @description: 基于JSON的数据请求
 * @author: 23536
 * @date: 2015年12月23日 下午3:23:30  
 */
public abstract class JSONWebServiceDataManager<T extends AbstractData, K extends DataReq> extends AbstractDataManager<T> {

    protected DataManagerListener listener = new DataManagerListener();
    
    private JSONType<T> respType;
    
    public JSONWebServiceDataManager(IDataCallback callback) {
        super(callback);
        respType = initRespType();
    }
    
    /**
     * 返回Json2Obj类型
     *
     * @return
     */
    protected JSONType<T> initRespType(){
        return null;
    }
    
    /**
     * 发送请求
     *
     * @param req
     * @return
     */
    protected int doRequest(String api, String url, String namespace, K req) {
        if(respType == null){
            throw new RuntimeException("method initRespType() must be overide");
        }
        JSONWebService<T, K> source = new JSONWebService<T, K>();
        source.setReqAndResp(api, url, namespace, req, respType);
        source.setListener(listener);
        source.doRequest();
        return source.getWhat();
    }
    
    /**
     * 发送请求
     *
     * @param req
     * @return
     */
    protected int doRequest(String api, String url, String namespace, K req, JSONType type) {
        JSONWebService<T, K> source = new JSONWebService<T, K>();
        source.setReqAndResp(api, url, namespace, req, type);
        source.setListener(listener);
        source.doRequest();
        return source.getWhat();
    }
}
