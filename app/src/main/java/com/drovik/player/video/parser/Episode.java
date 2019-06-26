package com.drovik.player.video.parser;

import android.os.Parcel;
import android.os.Parcelable;

public class Episode implements Parcelable {

    private String description;//描述
    private String duration;//42:10
    private String name;//xxxx第1集
    private String playUrl;//http://www.iqiyi.com/v_19rsjr50nw.html
    private String shortTitle;//标题
    private String subTitle;//摘要
    private String tvId;//2256822000
    private String vid;//b97d2d10d8b4d8d36b3b93288cf7c6c9
    private String order;// 1 2 3
    private String imageUrl;//

    public Episode(){

    }

    protected Episode(Parcel in) {
        description = in.readString();
        duration = in.readString();
        name = in.readString();
        playUrl = in.readString();
        shortTitle = in.readString();
        subTitle = in.readString();
        tvId = in.readString();
        vid = in.readString();
        order = in.readString();
        imageUrl = in.readString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTvId() {
        return tvId;
    }

    public void setTvId(String tvId) {
        this.tvId = tvId;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(duration);
        dest.writeString(name);
        dest.writeString(playUrl);
        dest.writeString(shortTitle);
        dest.writeString(subTitle);
        dest.writeString(tvId);
        dest.writeString(vid);
        dest.writeString(order);
        dest.writeString(imageUrl);
    }

    @Override
    public String toString() {
        return "Episode{" +
                "description='" + description + '\'' +
                ", duration='" + duration + '\'' +
                ", name='" + name + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", shortTitle='" + shortTitle + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", tvId='" + tvId + '\'' +
                ", vid='" + vid + '\'' +
                '}';
    }
}
