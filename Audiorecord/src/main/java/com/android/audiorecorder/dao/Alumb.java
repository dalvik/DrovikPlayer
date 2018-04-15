package com.android.audiorecorder.dao;

public class Alumb {
    
    private int id;
    
    private String alumbName;
    
    private String coverPath;
    
    private int imageNumber;
    
    private String describe;
    
    private boolean isHide;

    
    public Alumb() {
        super();
    }


    public Alumb(int id, String alumbName, String coverPath, int imageNumber,
                 String describe, boolean isHide) {
        super();
        this.id = id;
        this.alumbName = alumbName;
        this.coverPath = coverPath;
        this.imageNumber = imageNumber;
        this.describe = describe;
        this.isHide = isHide;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getAlumbName() {
        return alumbName;
    }


    public void setAlumbName(String alumbName) {
        this.alumbName = alumbName;
    }


    public String getCoverPath() {
        return coverPath;
    }


    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }


    public int getImageNumber() {
        return imageNumber;
    }


    public void setImageNumber(int imageNumber) {
        this.imageNumber = imageNumber;
    }


    public String getDescribe() {
        return describe;
    }


    public void setDescribe(String describe) {
        this.describe = describe;
    }


    public boolean isHide() {
        return isHide;
    }


    public void setHide(boolean isHide) {
        this.isHide = isHide;
    }


    @Override
    public String toString() {
        return "Alumb [id=" + id + ", alumbName=" + alumbName + ", coverPath="
                + coverPath + ", imageNumber=" + imageNumber + ", describe="
                + describe + ", isHide=" + isHide + "]";
    }
    

}
