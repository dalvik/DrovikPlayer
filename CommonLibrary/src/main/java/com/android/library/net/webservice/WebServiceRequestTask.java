/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: DataSourceTask.java
 * @description: 执行webservice网络请任务
 * @author: 23536   
 * @date: 2015年12月23日 下午3:35:28 
 */
package com.android.library.net.webservice;

import android.util.Log;

import com.android.library.net.base.OnReslutListener;
import com.android.library.net.req.WebServiceReqest;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/** 
 * @description: 执行webservice请求任务
 * @author: 23536
 * @date: 2015年12月23日 下午3:35:28  
 */
public class WebServiceRequestTask<T extends WebServiceReqest> implements Runnable {

    private final static int COUNT_TIMES = 1;
    private OnReslutListener listener = null;
    private T req = null;
    
    private String TAG = "DataSourceTask";

    public WebServiceRequestTask(OnReslutListener listener, T req) {
        this.listener = listener;
        this.req = req;
    }

    private String postMethod() {
        HttpTransportSE httpTransportSE = new HttpTransportSE(req.getUrl(), 10000);
        String xmlTagString ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        httpTransportSE.setXmlVersionTag(xmlTagString);
        httpTransportSE.debug = true;
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
        envelope.bodyOut = req.getSoapObject();
        //String res = "{'data':{'FTPServer':'172.7.50.170','FTPPort':21,'userName':'dss','userPwd':'dss'},'result':{'code':'success'},'locale':'zh_CN'}";
        //res = "{'result':{'code':'success'},'locale':'zh_CN'}";
        SoapObject obj = req.getSoapObject();
        int count = obj.getPropertyCount();
        for(int i=0;i<count; i++){
            Log.d(TAG, "arg" + i + "=" + obj.getProperty(i).toString());
        }
        try {
            httpTransportSE.call(null, envelope);//req.getNamespace() + req.getMethod()
            if(envelope.getResponse() != null){
                SoapObject result = (SoapObject) envelope.bodyIn;
                String resp = result.getProperty("execute").toString();
                Log.d(TAG, resp);
                return resp ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        int times = 0;
        while ((times++) < COUNT_TIMES) {
            String jsonContent = postMethod();
            if (null == jsonContent) {
                if (times == COUNT_TIMES) {
                    listener.onFailed();
                } else {
                    continue;
                }
            } else {
                listener.onSucess(jsonContent);
                break;
            }
        }
    }

}
