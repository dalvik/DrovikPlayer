package com.android.audiorecorder.dao;

public class FileThumb {

    private int id;
    
    private String name;
    
    private String coverPath;
    
    private int fileNumber;
    
    private String fileDescribe;//summary
    
    private long createTime;
    
    private long modifyTime;

    private int fileType;
    
    private boolean isLocal;
    
    private boolean isProvite;
    
    public FileThumb() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public int getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getFileDescribe() {
        return fileDescribe;
    }

    public void setFileDescribe(String fileDescribe) {
        this.fileDescribe = fileDescribe;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    public boolean isProvite() {
        return isProvite;
    }

    public void setProvite(boolean isProvite) {
        this.isProvite = isProvite;
    }

    @Override
    public String toString() {
        return "FileThumb [id=" + id + ", name=" + name + ", coverPath="
                + coverPath + ", fileNumber=" + fileNumber + ", fileDescribe="
                + fileDescribe + ", createTime=" + createTime + ", modifyTime="
                + modifyTime + ", fileType=" + fileType + ", isLocal="
                + isLocal + ", isProvite=" + isProvite + "]";
    }
    
}
