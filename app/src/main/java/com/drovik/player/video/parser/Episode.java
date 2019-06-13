package com.drovik.player.video.parser;

import android.os.Parcel;
import android.os.Parcelable;

public class Episode implements Parcelable {

    private String description;//描述
    private String duration;//42:10
    private String name;//xxxx第1集
    private String playUrl;//http://www.iqiyi.com/v_19rsjr50nw.html
    private String shortTitle;//题目
    private String subTitle;//摘要
    private String tvId;//2256822000
    private String vid;//b97d2d10d8b4d8d36b3b93288cf7c6c9

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
