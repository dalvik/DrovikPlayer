/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: PlatFromManager.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年4月8日 下午5:05:04 
 */
package com.android.media;

import com.android.library.net.base.IDataCallback;
import com.android.library.net.utils.LogUtil;
import com.android.media.data.TiangoImageBaseData;
import com.android.media.data.TiangoThumbBaseData;
import com.android.media.image.tango.TiangoImageListResp;
import com.android.media.image.tango.TiangoImageThumbListResp;
import com.android.media.image.tango.TiangoImageType;
import com.android.media.image.tango.TiangoPlatFormWebService;

import java.util.List;

/** 
 * @description: TODO
 * @author: 23536
 * @date: 2016年4月8日 下午5:05:04  
 */
public class PlatFormManager implements IDataCallback {

    private AbstractPlatForm mPlatForm;
 
    private PlatFormFactory mFactory;
    
    private IResultListener mListener;
    
    private String TAG = "PlatFormManager";
    
    private int whatTypeList;
    private int whatThumbList;
    private int whatImageList;
    
    private List<TiangoImageThumbListResp> imageThumbList;
    private List<TiangoImageListResp> imageList;
    
    public PlatFormManager(IResultListener listener){
        this.mListener = listener;
        mPlatForm = new TiangoPlatFormWebService(this);
    }
    
    public List<TiangoImageType> getTypeList(){
        return mPlatForm.getImageType();
    }
    
    public List<TiangoImageThumbListResp> getThumbList(String json){
        whatThumbList = mPlatForm.getThumbList(json);
        LogUtil.d(TAG, "==> getThumbList = " + whatThumbList);
        return imageThumbList;
    }
    
    public List<TiangoImageListResp> getImageList(String json){
        whatImageList = mPlatForm.getImageList(json);
        return imageList;
    }
    
    private void init(){
        
    }

    @Override
    public void onCallback(int what, int arg1, int arg2, Object obj) {
        LogUtil.d(TAG, "--> onCallback = " + what + "  arg1 = " + arg1 + " arg2 = " + arg2);
        if(what == whatTypeList){
            if(mListener != null){
                mListener.OnTypeList("");
            }
        } else if(what == whatThumbList){
            if(obj != null) {
                TiangoThumbBaseData resp = (TiangoThumbBaseData) obj;
                imageThumbList = resp.tngou;
            }
            if(mListener != null){
                mListener.OnThumbList(imageThumbList);
            }
        } else if(what == whatImageList){
            if(obj != null) {
                TiangoImageBaseData resp = (TiangoImageBaseData) obj;
                imageList = resp.list;
            }
            if(mListener != null){
                mListener.OnImageList(imageList);
            }
        }
    }
    
    public interface IResultListener{
        void OnTypeList(String json);//类型列表
        void OnThumbList(List<TiangoImageThumbListResp> list);//相册缩略图列表
        void OnImageList(List<TiangoImageListResp> list);//图片缩略图列表
    }
}
