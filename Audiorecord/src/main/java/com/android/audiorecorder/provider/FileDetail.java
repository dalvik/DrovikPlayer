package com.android.audiorecorder.provider;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDetail {

    private int id;
    
    private boolean isExists;
    
    private String filePath;
    
    private boolean isFile;
    
    private long lastModifyTime;
    
    private long length;
    
    private String mimeType;
    
    /**jpeg:0 video:1 audio:2 text:3 apk:4 zip:5 other:6**/
    private int fileType;
    
    private long duration;
    
    private int launchMode;
    
    private int showNotification; //1 true
    
    private int upload;//1 true up, 0 false down
    
    private int upDownLoadStatus;
    
    private long downLoadTime;
    
    private long uploadTime;
    
    private int fileResolutionX;
    
    private int fileResolutionY;
    
    private String thumbnailPath;
    
    private String mFileName = "";

    //use for time line
    private int mSection;
    private String mTime;

    //use for tel
    private String mPhoneNumber;
    private String mDisplayName;
    private int mCallPhoneType;// 1:in 2:out
    private int mCount;//record count
    private Bitmap mHeader;

    //use for ui
    private boolean mIsChecked;

    public FileDetail(){
    }
    
    public FileDetail(String path){
        File file = new File(path);
        this.filePath = path;
        if(isExists = file.exists()){
            mimeType = getMIMEType(path);
            isFile = file.isFile();
            if(isFile){
                fillInfo(file);
                if(fileType == FileProvider.FILE_TYPE_JEPG){
                    getImageResolution(path);
                } else if(fileType == FileProvider.FILE_TYPE_VIDEO){
                    getVideoResolution(path);
                } else if(fileType == FileProvider.FILE_TYPE_AUDIO){
                	
                }
            }
        }
    }
    
    private void fillInfo(File file){
        lastModifyTime = file.lastModified();
        length = file.length();
        mFileName = file.getName();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isExists(){
        return isExists;
    }
    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFileResolutionX() {
        return fileResolutionX;
    }

    public void setFileResolutionX(int fileResolutionX) {
        this.fileResolutionX = fileResolutionX;
    }

    public int getFileResolutionY() {
        return fileResolutionY;
    }

    public void setFileResolutionY(int fileResolutionY) {
        this.fileResolutionY = fileResolutionY;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
    
    public int getLaunchMode() {
        return launchMode;
    }

    public void setLaunchMode(int launchMode) {
        this.launchMode = launchMode;
    }

    public int getShowNotification() {
        return showNotification;
    }

    public void setShowNotification(int showNotification) {
        this.showNotification = showNotification;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }

    public int getUpDownLoadStatus() {
        return upDownLoadStatus;
    }

    public void setUpDownLoadStatus(int upDownLoadStatus) {
        this.upDownLoadStatus = upDownLoadStatus;
    }

    public long getDownLoadTime() {
        return downLoadTime;
    }

    public void setDownLoadTime(long downLoadTime) {
        this.downLoadTime = downLoadTime;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public void setFileName(String name) {
        this.mFileName = name;
    }

    public String getFileName(){
    	return mFileName;
    }

    @Override
    public String toString() {
        return "FileDetail [isFile=" + isFile + ", lastModifyTime="
                + lastModifyTime + ", length=" + length + ", mimeType="
                + mimeType + ", fileType=" + fileType + ", duration="
                + duration + ", fileResolutionX=" + fileResolutionX
                + ", fileResolutionY=" + fileResolutionY + ", thumbnailPath="
                + thumbnailPath + "]";
    }

    /** 获取MIME类型 **/
    private String getMIMEType(String name) {
        String type = "";
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("apk")) {
            fileType = FileProvider.FILE_TYPE_APK;
            return "application/vnd.android.package-archive";
        } else if (end.equals("mp4") || end.equals("avi")
                || end.equals("rmvb")) {
            fileType = FileProvider.FILE_TYPE_VIDEO;
            type = "video";
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf")
                || end.equals("ogg") || end.equals("wav") || end.equals("3gpp") || end.equals("3gp")) {
            fileType = FileProvider.FILE_TYPE_AUDIO;
            type = "audio";
        } else if (end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            fileType = FileProvider.FILE_TYPE_JEPG;
            type = "image";
        } else if (end.equals("txt") || end.equals("log")) {
            fileType = FileProvider.FILE_TYPE_TEXT;
            type = "text";
        } else {
            fileType = FileProvider.FILE_TYPE_OTHER;
            type = "*";
        }
        type += "/*";
        return type;
    }

    /** 获取视频的分辨率 **/
    private void getVideoResolution(String path){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try{
            mediaMetadataRetriever.setDataSource(path);
            Bitmap thumbnail = mediaMetadataRetriever.getFrameAtTime(-1);
            if(thumbnail != null){
                fileResolutionX = thumbnail.getWidth();
                fileResolutionY = thumbnail.getHeight();
                File file = new File(path);
                String target = file.getParent() + "/thumbnail/";
                thumbnailPath = saveBitmapInFile(thumbnail, target, file.getName().substring(0,file.getName().lastIndexOf(".")+1)+"jpg");
                thumbnail.recycle();
                thumbnail = null;
            }
        } catch(Exception e){
            System.out.println("path="+ path);
            e.printStackTrace();
        } finally {
            mediaMetadataRetriever.release();
        }
    }
    
    private void getImageResolution(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        fileResolutionX = options.outWidth;
        fileResolutionY = options.outHeight;
    }
    
    private String saveBitmapInFile(Bitmap bitmap, String targetPath, String name){
        try {
            File jpg = new File(targetPath, name);
            if(!jpg.exists()) {
                jpg.mkdirs();
            }
            if(jpg.exists()){
                jpg.delete();
            }
            FileOutputStream fos = new FileOutputStream(jpg);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            targetPath = null;
        } catch (IOException e) {
            e.printStackTrace();
            targetPath = null;
        }
        return targetPath + name;
    }
    
    private int getMediaDuration(){
    	
    	return 0;
    }

    public void setSection(int section){
        this.mSection = section;
    }

    public int getSection() {
        return mSection;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public String getTime() {
        return mTime;
    }

    public void setPhoneNumber(String number){
        this.mPhoneNumber = number;
    }

    public String getPhoneNumber() {
        return this.mPhoneNumber;
    }

    public void setDisPlayName(String name) {
        this.mDisplayName = name;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }
    public void setCallPhoneType(int type) {
        this.mCallPhoneType = type;
    }

    public int getCallPhoneType() {
        return this.mCallPhoneType;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public int getCount(){
        return this.mCount;
    }

    public void setHeader(Bitmap header) {
        this.mHeader = header;
    }

    public Bitmap getHeader() {
        return this.mHeader;
    }

    public void setChecked(boolean checked) {
        this.mIsChecked = checked;
    }

    public boolean isChecked() {
        return mIsChecked;
    }
}
