package com.android.audiorecorder.gallery.genuin;

import android.graphics.Bitmap;

public interface ImageGetterCallback {
    public void imageLoaded(int pos, int offset, Bitmap bitmap);
    public int fullImageSizeToUse(int pos, int offset);
    public void completed();
}
