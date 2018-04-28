package com.android.audiorecorder.dao;

import com.android.audiorecorder.RecorderFile;
import com.android.audiorecorder.provider.FileDetail;

import java.util.List;
import java.util.Set;

public interface IFileManager{
    
    public final static int PERPAGE_NUMBER = 30;
    
    public abstract void createDiretory(String directory);
    public abstract boolean createFile(String path);
    public abstract boolean isExists(String path);
    public abstract boolean removeFile(int category, String path);
    public abstract int renameFile(int category, String oldPath, String newPath);
    
    public void insertRecorderFile(RecorderFile file);
    public List<RecorderFile> queryAllFileList(int mimeType, int page, int pageNumber);
    public List<RecorderFile> queryPublicFileList(int mimeType, int page, int pageNumber);
    public List<RecorderFile> queryPrivateFileList(int mimeType, int page, int pageNumber);
    public int getFileCount(int mimeType, int type);//mimeType : image audio video  -1 all;  lucher type manly tel auto -a all 
    
    public long delete(int mimeType, long id);
    public void updateUpLoadProgress(int mimeType, long progress, long id);
    

    public long addTask(long id, boolean download);
    
    public abstract List<FileThumb> loadFileThumbList(boolean isLocal, int mediaType, int pageIndex, int pageNumber, Set<Integer> launchType);
    
    public int getFileThumbCount(int fileType, int type, Set<Integer> launchType);
    
    public abstract List<FileDetail> loadFileList(boolean isLocal, int mediaType, String thumbName, int pageIndex, int pageNumber, Set<Integer> launchType);
    
    public int getFileListCount(int fileType, String thumbName, Set<Integer> launchType);
    
}
