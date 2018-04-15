/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: AbstractWebServiceManager.java
 * @description: UI层数据请求处理接口
 * @author: 23536   
 * @date: 2015年12月23日 下午3:11:00 
 */
package com.android.library.net.manager;

import android.os.Handler;
import android.os.Message;

import com.android.library.net.base.AbstractData;
import com.android.library.net.base.AbstractDataRequestListener;
import com.android.library.net.base.IDataCallback;

import java.lang.ref.SoftReference;

/**
 * @description: UI层数据请求处理接口
 * @author: 23536
 * @date: 2015年12月23日 下午3:11:00
 */
public abstract class AbstractDataManager<T extends AbstractData> extends Handler {
    
    /**
     * 成功
     */
    public static final int RESULT_SUCCESS = AbstractDataRequestListener.RESULT_SUCCESS;
    /**
     * 失败
     */
    public static final int RESULT_FAILED = AbstractDataRequestListener.RESULT_FAILED;
    /**
     * 失败 网络问题
     */
    public static final int RESULT_ERROR = AbstractDataRequestListener.RESULT_ERROR;
    
    /**
     * 失败 网络问题
     */
    public static final int RESULT_SID_TIMEOUT = AbstractDataRequestListener.RESULT_ERROR;
    
    
    private SoftReference<IDataCallback> mCallback = null;

    public AbstractDataManager() {
    }

    public AbstractDataManager(IDataCallback callback) {
        this.mCallback = new SoftReference<IDataCallback>(callback);
    }

    @Override
    public void handleMessage(Message msg) {
        IDataCallback callback = getCallback();
        handleMessageOnUIThread(msg.what, msg.arg1, msg.arg2, msg.obj);
        if (callback != null) {
            callback.onCallback(msg.what, msg.arg1, msg.arg2, msg.obj);
        }
    }

    protected void handleMessageOnUIThread(int what, int arg1, int arg2,
            Object obj) {

    }

    private IDataCallback getCallback() {
        if (mCallback == null) {
            return null;
        } else {
            return mCallback.get();
        }
    }
    
    /**
     * 数据管理用的Listener<br/>
     * 可在此处理数据
     */
    protected class DataManagerListener implements AbstractDataRequestListener<T> {
        public DataManagerListener() {
        }

        @Override
        public final void sendMessage(int what, int code, T data) {
            Message msg = null;
            switch (code) {
                case RESULT_SUCCESS:
                    msg = Message.obtain(AbstractDataManager.this, what, RESULT_SUCCESS, 0, data);
                    break;
                case RESULT_FAILED:
                    msg = Message.obtain(AbstractDataManager.this, what, RESULT_FAILED, 0, data);
                    break;
                case RESULT_ERROR:
                    msg = Message.obtain(AbstractDataManager.this, what, RESULT_ERROR, 0, data);
                    break;
                default:
                    msg = Message.obtain(AbstractDataManager.this, what, code, 0, data);
            }
            // 防止请求响应太快
            AbstractDataManager.this.sendMessageDelayed(msg, 100);
        }

    }
}
