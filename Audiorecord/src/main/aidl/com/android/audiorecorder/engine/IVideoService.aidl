package com.android.audiorecorder.engine;

import com.android.audiorecorder.engine.IVideoStateListener;
import com.android.audiorecorder.engine.IVideoStateListener;
import com.android.audiorecorder.engine.IVideoFrameAvailableListener;
import com.android.audiorecorder.engine.IAutoFocus;
import com.android.audiorecorder.engine.PreviewSize;
	
interface IVideoService{
	
   void setSurface(in Surface surface, int surfaceViewWidth, int surfaceViewHeight, int previewWidth, int previewHeight);
    
    /**
    * set camera preview width and height
    */
    void setPreviewSize(in PreviewSize size);
    
    /**
    * get camer pveview width and height
    */
    PreviewSize getPreviewSize();
    
    PreviewSize getImageSize();
    
    void startPreview(int glTexture);
    
    String getFocusMode();
    
    List<String> getSupportFocusMode();

    void stopPreview();
    
    void release();
    
    void tackPiture();
        
    int startVideoRecord();
    
    int stopVideoRecord();
    
    int videoCapture();
    
    int videoSnap();
    
    void registerFrameAvailableListener(IBinder token, IVideoFrameAvailableListener listener);
    
    void unRegisterFrameAvailableListener(IBinder token, IVideoFrameAvailableListener listener);
    
    void registerAutoFocusListener(IBinder token, IAutoFocus listener);
    
    void unRegisterAutoFocusListener(IBinder token, IAutoFocus listener);
    
    void registerStateListener(IBinder token, IVideoStateListener listener);
    
    void unRegisterStateListener(IBinder token, IVideoStateListener listener);
    
    long getRecorderTime();
    
    boolean isRecorderStart();
    
    void setMode(int mode);
    
    int getMode();
    
	void adjustStreamVolume(int streamType, int direct, int flag);
	
	long checkDiskCapacity();
	
}	