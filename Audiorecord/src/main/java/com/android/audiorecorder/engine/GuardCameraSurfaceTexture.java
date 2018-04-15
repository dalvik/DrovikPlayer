package com.android.audiorecorder.engine;

import android.graphics.SurfaceTexture;
import android.view.TextureView.SurfaceTextureListener;

public class GuardCameraSurfaceTexture extends SurfaceTexture implements
        SurfaceTextureListener {

    private ISurfaceReady mReady;
    public GuardCameraSurfaceTexture(int textTureId, ISurfaceReady ready) {
        super(textTureId);
        mReady = ready;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                          int height) {
        if(mReady != null){
            mReady.onReady();
        }
        System.out.println("onSurfaceTextureAvailable");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public interface ISurfaceReady{
        public void onReady();
    }
}
