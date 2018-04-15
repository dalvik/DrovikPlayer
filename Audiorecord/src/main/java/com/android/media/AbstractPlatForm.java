/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: AbstractPlatForm.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年1月27日 上午10:13:56 
 */
package com.android.media;

import com.android.library.net.base.IDataCallback;
import com.android.library.net.manager.JSONHttpDataManager;
import com.android.library.net.req.DataReq;
import com.android.library.net.resp.DataResp;
import com.android.media.data.TiangoThumbBaseData;
import com.android.media.image.tango.TiangoImageType;

import java.util.List;

public abstract class AbstractPlatForm extends
        JSONHttpDataManager<TiangoThumbBaseData<DataResp>, DataReq> implements
        IPlatForm {

    private Object mLock = new Object();

    public AbstractPlatForm(IDataCallback callback) {
        super(callback);
    }

    public void sleep() {
        synchronized (mLock) {
            try {
                mLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void wake() {
        synchronized (mLock) {
            mLock.notify();
        }
    }

    public abstract List<TiangoImageType> getImageType();
}
