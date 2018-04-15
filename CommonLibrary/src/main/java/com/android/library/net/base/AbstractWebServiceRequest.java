/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: AbstractDataSource.java
 * @description: 数据请求和回调接口
 * @author: 23536   
 * @date: 2015年12月23日 下午3:55:16 
 */
package com.android.library.net.base;

import com.android.library.net.req.WebServiceReqest;
import com.android.library.net.webservice.WebServiceRequestTask;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 数据请求和回调接口，负责数据的请求和返回结果的回调
 * @author: 23536
 * @date: 2015年12月23日 下午3:55:16
 */
public abstract class AbstractWebServiceRequest<T extends DataStruct, V extends WebServiceReqest> implements OnReslutListener {

    
    private static RequestThreadPool mMessageManager;

    private static AtomicInteger atomic;

    static {
        if (atomic == null) {
            atomic = new AtomicInteger(10);
        }
    }

    private int what = atomic.getAndIncrement();
    
    private AbstractDataRequestListener<T> listener = null;

    /**
     * 获取请求参数
     */
    protected abstract V getRequest();

    /**
     * 转化Resp
     */
    protected abstract T parseResp(String obj);

    public int getWhat() {
        return what;
    }

    @Override
    public void onSucess(String result) {
        try {
            T t = parseResp(result);
            if (null != listener) {
                if (null == t) {
                    onFailed();
                    return;
                }
                listener.sendMessage(what, AbstractDataRequestListener.RESULT_SUCCESS, t);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onFailed();
        }
    }
    
    @Override
    public void onFailed() {
        if (null != listener) {
            listener.sendMessage(what, AbstractDataRequestListener.RESULT_ERROR, null);
        }
    }

    public void setListener(AbstractDataRequestListener<T> listener) {
        this.listener = listener;
    }

    public final void doRequest() {
        mMessageManager = RequestThreadPool.instance;
        V req = getRequest();
        WebServiceRequestTask<V> task = new WebServiceRequestTask<V>(this, req);
        mMessageManager.execute(task);
    }
}
