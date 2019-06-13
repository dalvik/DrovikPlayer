package com.drovik.player.video.ui;

import java.io.File;
import java.io.FileOutputStream;

public class SaveFile {

    private String TAG = "SaveFile";

    private static SaveFile mSaveFile;

    private SaveFile() {
    }

    public static SaveFile getInstance(){
        if(mSaveFile == null) {
            mSaveFile = new SaveFile();
        }
        return mSaveFile;
    }

    public void writeString(String content) {
        try {
            if(content != null) {
                String filePath = "/mnt/sdcard/log.txt";
                File file = new File(filePath);
                if(file.exists()){
                    file.delete();
                }
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
