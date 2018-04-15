package com.android.audiorecorder.engine;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuardCameraManager {

    private Context mContext;
    
    private Camera mCamera;
    
    private SurfaceTexture mTexture;
    private PictureCallback mPictureCallback;
    private ShutterCallback mShutterCallback;
    private boolean mIsSupportAutoFocus;
    
    private static final int JPEGQUALITY = 70;
    
    private SurfaceTexture mSurfaceTexture;
    
    private String mFocusMode;
    
    private List<String> mSupportFocusMode;
    private int previewWidth = 640;
    private int previewHeight = 480;
    private int imageWidth = 1280;
    private int imageHeight = 720;
    private List<PreviewSize> supportPreviewSizeList = new ArrayList<PreviewSize>();
    private List<PreviewSize> supportImageSizeList = new ArrayList<PreviewSize>();
    
    
    public GuardCameraManager(Context context){
        this.mContext = context;
    }
    
    public boolean checkCameraHardware(String front){
        return mContext.getPackageManager().hasSystemFeature(front);
    }
    
    public boolean startPreview(int textTureId, AutoFocusCallback autoFocusCallback){
        if(mTexture == null){
            mTexture = new SurfaceTexture(textTureId);
        }
        if(mCamera != null){
            try {
                mCamera.setPreviewTexture(mTexture);
                mCamera.startPreview();
                if(mIsSupportAutoFocus){
                    mCamera.autoFocus(autoFocusCallback);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public void getCameraInfo(){
        Camera.Parameters params = mCamera.getParameters();
        List<String> modes = params.getSupportedFocusModes();
        if(modes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mIsSupportAutoFocus = true;
        }
        Resources res = mContext.getResources();
        System.out.println(res.getConfiguration().orientation+"---------->");
        if(res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            params.set("rotation", 270);
        }else{
            params.set("rotation", -90);
        }
        if(isSupport(params, ImageFormat.JPEG)){
            params.setPictureFormat(ImageFormat.JPEG);
            params.setJpegQuality(JPEGQUALITY);
        }
        Size size = getMaxPictureSize(params);
        if(size != null){
            params.setPictureSize(size.width, size.height);
        }
        try{
            mCamera.setParameters(params);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("setParameters failed." + e.getLocalizedMessage());
        }
    }
    
    public boolean isSupportSilgent(){
        return false;
    }
    
    
    public void muteAll(){
        
    }
    
    private boolean isSupport(Camera.Parameters params, int format){
        List<Integer> pictureformats = params.getSupportedPictureFormats();
        for(Integer t:pictureformats){
            if(t == format){
                return true;
            }
        }
        return false;
    }
    
    public int getCameraNumber(){
        return 0;
    }
    
    public Camera getCameraInstance(int front){
        mCamera = Camera.open(front);
        return mCamera;
    }
    
    public void startPreview(ErrorCallback cb) throws IOException {
        if(mCamera == null){
            throw new IOException("Camer Not Init Exception.");
        }
        if(mSurfaceTexture == null){
            throw new IOException("SurfaceTexture Not Init Exception.");
        }
        try {
            Camera.Parameters params = mCamera.getParameters();
            /*Resources res = mContext.getResources();
            System.out.println(res.getConfiguration().orientation+"---------->");
            if(res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                params.set("rotation", 90);
            }else{
                params.set("rotation", -90);
            }*/
            params.setPreviewSize(previewWidth, previewHeight);
            mSupportFocusMode = params.getSupportedFocusModes();
            getMaxPictureSize(params);
            //mCamera.setDisplayOrientation(90);
            mCamera.setParameters(params);
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.setErrorCallback(cb);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void getCameraInfo2(){
        Camera.Parameters params = mCamera.getParameters();
        List<String> modes = params.getSupportedFocusModes();
        if(modes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mIsSupportAutoFocus = true;
        }
        Resources res = mContext.getResources();
        System.out.println(res.getConfiguration().orientation+"---------->");
        if(res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            params.set("rotation", 270);
        }else{
            params.set("rotation", -90);
        }
        mFocusMode = params.getFocusMode();
        if(isSupport(params, ImageFormat.JPEG)){
            params.setPictureFormat(ImageFormat.JPEG);
            params.setJpegQuality(JPEGQUALITY);
        }
        Size size = getMaxPictureSize(params);
        if(size != null){
            params.setPictureSize(size.width, size.height);
        }
        try{
            mCamera.setParameters(params);
        } catch(Exception e){
            e.printStackTrace();
            Log.e("","setParameters failed." + e.getLocalizedMessage());
        }
    }
    
    public boolean isSupportAutoFocus(){
        return mIsSupportAutoFocus;
    }
    
    /**
     * set preview surface
     * @param texture
     */
    public void setSurfaceTexture(SurfaceTexture texture){
        mSurfaceTexture = texture;
    }
    
    /**
     * register callback, set null unregister
     * @param callback
     */
    public void setFrameAvlaivleListener(OnFrameAvailableListener listener){
        if(mSurfaceTexture != null){
            mSurfaceTexture.setOnFrameAvailableListener(listener);
        }
    }
    
    /**
     * register callback, set null unregister
     * @param callback
     */
    public void setAutoFocusCallback(AutoFocusCallback callback){
        if(mCamera != null && mIsSupportAutoFocus){
            mCamera.autoFocus(callback);
        }
    }
    
    /**
     * register callback, set null unregister
     * @param callback
     */
    public void setPictureCallback(PictureCallback callback){
        mPictureCallback = callback;
    }
    
    /**
     * register callback, set null unregister
     * @param callback
     */
    public void setShutterCallback(ShutterCallback callback){
        mShutterCallback = callback;
    }
    
    public void takePicture(ShutterCallback shutter, PictureCallback raw, PictureCallback jpegCallback){
        if(mCamera != null){
            mCamera.takePicture(shutter, raw, jpegCallback);
        }
    }
    
    public void releaseCamera(){
        if(mCamera != null){
            if(mIsSupportAutoFocus){
                mCamera.cancelAutoFocus();
            }
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        destorySurfaceTexture();
    }
    
    private Size getMaxPictureSize(Camera.Parameters params) {
        List<Size> previewSizeList = params.getSupportedPreviewSizes();
        for(Size s:previewSizeList){
            supportPreviewSizeList.add(new PreviewSize(s.width, s.height));
        }
        List<Size> imageSizeList = params.getSupportedPictureSizes();
        int size = imageSizeList.size();
        int defaultWidth = 0;
        Size pictureSize = null;
        for(int i=0;i<size; i++){
            Size temp = imageSizeList.get(i);
            if(temp.width>defaultWidth){
                defaultWidth = temp.width;
                pictureSize = temp;
            }
            supportImageSizeList.add(new PreviewSize(temp.width, temp.height));
        }
        
        return pictureSize;
    }
    
    public PreviewSize getPreviewSize(){
        return new PreviewSize(previewWidth, previewHeight);
    }
    
    public void setPreviewSize(PreviewSize size){
        this.previewWidth = size.getPreviewWidth();
        this.previewHeight = size.getPreviewHeight();
    }
    
    public PreviewSize getImageSize(){
        return supportImageSizeList.get(0);
        //return new PreviewSize(imageWidth, imageHeight);
    }
    
    public List<PreviewSize> getSupportPreviewSizeList(){
        return supportPreviewSizeList;
    }
    
    public List<PreviewSize> getSupportImageSizeList(){
        return supportImageSizeList;
    }
    
    public String getFocusMode(){
        return mFocusMode;
    }
    
    public List<String> getSupportFocusMode(){
        return mSupportFocusMode;
    }
    
    public void refresh(){
        if(mSurfaceTexture != null){
            mSurfaceTexture.updateTexImage();
        }
    }
    
    private void destorySurfaceTexture(){
        if(mSurfaceTexture != null){
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
    }
    
    interface ICameraState{
        void onCameraMessage(int code);
    }
    
}
