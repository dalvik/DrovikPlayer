package com.android.library.net.http;

import com.android.library.net.base.AbstractDataRequestListener;
import com.android.library.net.base.DataStruct;
import com.android.library.net.base.OnReslutListener;
import com.android.library.net.base.RequestThreadPool;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractHttpTaskRequest<T extends DataStruct, V extends IHttpRequest> implements OnReslutListener {

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
    protected abstract T parseResp(String content);

    public int getWhat() {
        return what;
    }

    @Override
    public final void onSucess(String result) {
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
    public final void onFailed() {
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
        HttpRequestTask<V> task = new HttpRequestTask<V>(this, req);
        mMessageManager.execute(task);
    }
}
