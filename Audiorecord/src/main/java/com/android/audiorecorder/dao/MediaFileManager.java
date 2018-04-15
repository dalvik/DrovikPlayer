package com.android.audiorecorder.dao;

import java.io.File;
import java.io.IOException;

public abstract class MediaFileManager  implements IFileManager {

    public void createDiretory(String directory) {
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public boolean createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public boolean removeFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
    
}
