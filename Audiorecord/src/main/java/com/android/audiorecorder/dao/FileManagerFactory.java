package com.android.audiorecorder.dao;

import android.content.Context;

public class FileManagerFactory {


    private static IFileManager mFileManager;
    
    public static IFileManager getFileManagerInstance(Context context){
        if(mFileManager == null) {
            mFileManager = new FileManagerImp(context);
        }
        return mFileManager;
    }
    
}
