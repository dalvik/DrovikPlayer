package com.drovik.player.audio;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 34544 on 2017/11/10.
 */
public class MusicBean implements Parcelable {

    public String album;//专辑
    public int bitrate;//比特率
    public int channels;//声道数
    public String duration;//时长，mm:ss
    public boolean fav;//是否收藏
    public String origpath = "";//文件绝对路径
    public String playpath;//播放路径
    public int samplerate;

    public String singer;//歌手名
    public String thumbpath;//封面图片绝对路径
    public String thumbpath2;//模糊封面
    public String title;//音乐标题


    public boolean isDownload;
    public boolean isPlaying;
    public boolean isAdd = false;    //状态用于标记添加歌单页面是否添加到歌单
    public boolean isSelect = false; //状态用于标记在音乐选择是否选择


    public MusicBean() {
    }


    protected MusicBean(Parcel in) {
        album = in.readString();
        bitrate = in.readInt();
        channels = in.readInt();
        duration = in.readString();
        fav = in.readByte() != 0;
        origpath = in.readString();
        playpath = in.readString();
        samplerate = in.readInt();
        singer = in.readString();
        thumbpath = in.readString();
        thumbpath2 = in.readString();
        title = in.readString();
        isDownload = in.readByte() != 0;
        isPlaying = in.readByte() != 0;
        isAdd = in.readByte() != 0;
        isSelect = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(album);
        dest.writeInt(bitrate);
        dest.writeInt(channels);
        dest.writeString(duration);
        dest.writeByte((byte) (fav ? 1 : 0));
        dest.writeString(origpath);
        dest.writeString(playpath);
        dest.writeInt(samplerate);
        dest.writeString(singer);
        dest.writeString(thumbpath);
        dest.writeString(thumbpath2);
        dest.writeString(title);
        dest.writeByte((byte) (isDownload ? 1 : 0));
        dest.writeByte((byte) (isPlaying ? 1 : 0));
        dest.writeByte((byte) (isAdd ? 1 : 0));
        dest.writeByte((byte) (isSelect ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MusicBean> CREATOR = new Creator<MusicBean>() {
        @Override
        public MusicBean createFromParcel(Parcel in) {
            return new MusicBean(in);
        }

        @Override
        public MusicBean[] newArray(int size) {
            return new MusicBean[size];
        }
    };

    public String getThumbpath2() {
        return thumbpath2;
    }

    public void setThumbpath2(String thumbpath2) {
        this.thumbpath2 = thumbpath2;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public int getSamplerate() {
        return samplerate;
    }

    public void setSamplerate(int samplerate) {
        this.samplerate = samplerate;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public boolean getSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public MusicBean(String origpath, String thumbPath, String title, String singer, String album, boolean isDownload) {
        this.origpath = origpath;
        this.thumbpath = thumbPath;
        this.title = title;
        this.album = album;
        this.singer = singer;
        this.isDownload = isDownload;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
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

    public String getPlaypath() {
        return playpath;
    }

    public void setPlaypath(String playpath) {
        this.playpath = playpath;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
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


}
