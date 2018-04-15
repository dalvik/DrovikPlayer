package com.android.audiorecorder.engine;

import android.os.Parcel;
import android.os.Parcelable;

public class PreviewSize implements Parcelable {

    private int previewWidth;
    
    private int previewHeight;
  
    public PreviewSize(Parcel source){
        if(source != null){
            this.previewWidth = source.readInt();
            this.previewHeight = source.readInt();
        }
    }
    
    public PreviewSize(int width, int height){
        this.previewWidth = width;
        this.previewHeight = height;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(previewWidth);
        dest.writeInt(previewHeight);
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
    }

    public static final Creator<PreviewSize> CREATOR = new Creator<PreviewSize>() {
        
        @Override
        public PreviewSize[] newArray(int size) {
            return new PreviewSize[size];
        }
        
        @Override
        public PreviewSize createFromParcel(Parcel source) {
            return new PreviewSize(source);
        }
    };
    
}
