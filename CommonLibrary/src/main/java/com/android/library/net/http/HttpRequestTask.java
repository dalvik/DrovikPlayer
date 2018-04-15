package com.android.library.net.http;


import com.android.library.net.base.OnReslutListener;
import com.android.library.net.utils.HttpUtil;
import com.android.library.net.utils.LogUtil;

import java.io.InputStream;

/**
 * 
 * @description: 基于HTTP的网络请求
 * @author: 23536
 * @date: 2016年1月5日 下午5:12:53 
 * @param <T>
 */
public class HttpRequestTask<T extends IHttpRequest> implements Runnable {
    private final static int COUNT_TIMES = 1;
    private OnReslutListener listener = null;
    private T req = null;

    public HttpRequestTask(OnReslutListener listener, T req) {
        this.listener = listener;
        this.req = req;
    }

    private byte[] postMethod() {
        return HttpUtil.loadData(req instanceof JSONHttpPost ?  false : true , req.getUrl(), req.getHeader(), req.getData(), new IInputStreamParser<byte[]>() {
            @Override
            public byte[] parser(final InputStream inputStream) {
                try {
                    byte[] readBuffer = new byte[512*2];
                    StringBuffer sb = new StringBuffer();
                    int len = 0;
                    while ((len = inputStream.read(readBuffer)) != -1) {
                        sb.append(new String(readBuffer, 0, len));
                    }
                    LogUtil.i("HttpRequestTask", sb.toString());
                    return sb.toString().getBytes();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void run() {
        int times = 0;
        while ((times++) < COUNT_TIMES) {
            byte[] result = postMethod();
            if (null == result) {
                if (times == COUNT_TIMES) {
                    listener.onFailed();
                } else {
                    continue;
                }
            } else {
                listener.onSucess(new String(result));
                break;
            }
        }
    }
}