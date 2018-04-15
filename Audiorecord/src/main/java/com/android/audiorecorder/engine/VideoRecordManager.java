/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: MediaRecord.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年4月28日 上午11:06:23 
 */
package com.android.audiorecorder.engine;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Surface;

import com.android.library.net.utils.LogUtil;

public class VideoRecordManager extends AbstractRecord {

    private Context mContext;
    private CameraServiceConnection mConnection;
    private Binder mToken;
    
    private IVideoService mVdeioRecordSystem;
    private OnBindListener mListener;
    
    private String TAG = "VideoRecordManager";
    
    public VideoRecordManager(Context context, OnBindListener listener) {
        mConnection = new CameraServiceConnection();
        mToken = new Binder();
        Intent intent = new Intent();
        intent.setAction(MultiMediaService.Action_Video_Record);
        mContext.bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
    }
    
    public void setSurface(Surface surface, int surfaceViewWidth, int surfaceViewHeight, int previewWidth,
                           int previewHeight) {
        if(mVdeioRecordSystem != null) {
            try {
                mVdeioRecordSystem.setSurface(surface, surfaceViewWidth, surfaceViewHeight, previewWidth, previewHeight);
            } catch (RemoteException e) {
                LogUtil.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void startRecord() {
    }

    @Override
    public void stopRecord() {
    }

    public interface OnBindListener {
        public final static int CODE_BIND = 0x01;
        public final static int CODE_UNBIND = 0x02;
        void onBind(int code);
    }
    
    private class CameraServiceConnection implements ServiceConnection {

        public CameraServiceConnection(){
            
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mVdeioRecordSystem = VideoRecordSystem.Stub.asInterface(service);
            LogUtil.i(TAG, "client binded " + service);
            if(mVdeioRecordSystem != null){
                try {
                    mVdeioRecordSystem.registerStateListener(mToken, mVideoStateListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            /*if(mVideoStateListener != null) {
                mVideoStateListener.onStateChanged(0);
            }*/
            if(mListener != null) {
                mListener.onBind(OnBindListener.CODE_BIND);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mVdeioRecordSystem = null;
            /*if(mVideoStateListener != null){
                mVideoStateListener.onStateChanged(0);
            }*/
            if(mListener != null){
                mListener.onBind(OnBindListener.CODE_UNBIND);
            }
        }
    }
    
    private IVideoStateListener mVideoStateListener = new IVideoStateListener.Stub() {

        @Override
        public void onStateChanged(int state) throws RemoteException {
            
        }
        
    };
}
