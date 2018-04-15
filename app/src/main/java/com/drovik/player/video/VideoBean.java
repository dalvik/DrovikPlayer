package com.drovik.player.video;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 34544 on 2016/9/19.
 */
public class VideoBean implements Parcelable {

    public String duration;
    public boolean fav;
    public String highpath;
    public String lowpath;
    public String origpath;
    public String thumbpath;//缩略图
    public String title;
    public boolean isDownload;
    public boolean isSelected; //是否选择
    public long size;
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String date;      //视频下载时间

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getOrigpath() {
        return origpath;
    }

    public void setOrigpath(String origpath) {
        this.origpath = origpath;
    }

    public String getThumbpath() {
        return thumbpath;
    }

    public void setThumbpath(String thumbpath) {
        this.thumbpath = thumbpath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public String getDate() {
        return date;
    }

    public String getLowpath() {
        return lowpath;
    }

    public void setLowpath(String lowpath) {
        this.lowpath = lowpath;
    }

    public String getHighpath() {
        return highpath;
    }

    public void setHighpath(String highpath) {
        this.highpath = highpath;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }


    protected VideoBean(Parcel in) {
        duration = in.readString();
        name = in.readString();
        fav = in.readByte() != 0;
        highpath = in.readString();
        lowpath = in.readString();
        origpath = in.readString();
        thumbpath = in.readString();
        title = in.readString();
        isDownload = in.readByte() != 0;
        date = in.readString();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(duration);
        dest.writeString(name);
        dest.writeByte((byte) (fav ? 1 : 0));
        dest.writeString(highpath);
        dest.writeString(lowpath);
        dest.writeString(origpath);
        dest.writeString(thumbpath);
        dest.writeString(title);
        dest.writeByte((byte) (isDownload ? 1 : 0));
        dest.writeString(date);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }

        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };

    public boolean isSelected() {
        return isSelected;

    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public VideoBean(String path, String thumbPath, boolean isDownload, long size) {
        isSelected = false;
        this.origpath = path;
        this.isDownload = isDownload;
        this.thumbpath = thumbPath;
        this.size = size;
    }
    public VideoBean(String path, String thumbPath, boolean isDownload, long size, String name) {
        isSelected = false;
        this.origpath = path;
        this.isDownload = isDownload;
        this.thumbpath = thumbPath;
        this.size = size;
        this.name=name;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
