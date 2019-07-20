package com.crixmod.sailorcast.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.crixmod.sailorcast.SailorCast;

/**
 * Created by fire3 on 2014/12/26.
 */
public class SCAlbum implements Parcelable {
    private String mAlbumId = null;
    private Integer mVideosTotal = 0;   /* 总共的视频数量 */
    private String mTitle = null;
    private String mSubTitle = null;
    private String mDirector = null;
    private String mMainActor = null;
    private String mVerImageUrl = null;
    private String mHorImageUrl = null;
    private String mDesc = null;
    private SCSite mSite = new SCSite(SCSite.UNKNOWN);
    private String mTip = null;
    private Boolean mIsCompleted = false;  /* 是否完结 */
    private String mLetvStyle = null;  /* Letv需要的字段, 其它站点不需要， 界面不显示 */
    private String mTVid;
    private String mVid;//when adview, presend adid
    private String mScore;
    private String mPlayUrl;//http://www.iqiyi.com/a_19rrhrra2l.html
    private String mAdView;

    public SCAlbum(int siteID) {
        this.mSite = new SCSite(siteID);
    }

    protected SCAlbum(Parcel in) {
        mAlbumId = in.readString();
        if (in.readByte() == 0) {
            mVideosTotal = null;
        } else {
            mVideosTotal = in.readInt();
        }
        mTitle = in.readString();
        mSubTitle = in.readString();
        mDirector = in.readString();
        mMainActor = in.readString();
        mVerImageUrl = in.readString();
        mHorImageUrl = in.readString();
        mDesc = in.readString();
        mTip = in.readString();
        byte tmpMIsCompleted = in.readByte();
        mIsCompleted = tmpMIsCompleted == 0 ? null : tmpMIsCompleted == 1;
        mLetvStyle = in.readString();
        mTVid = in.readString();
        mVid = in.readString();
        mScore = in.readString();
        mPlayUrl = in.readString();
        mAdView = in.readString();
    }

    public static final Creator<SCAlbum> CREATOR = new Creator<SCAlbum>() {
        @Override
        public SCAlbum createFromParcel(Parcel in) {
            return new SCAlbum(in);
        }

        @Override
        public SCAlbum[] newArray(int size) {
            return new SCAlbum[size];
        }
    };

    public String toJson() {
        String ret = SailorCast.getGson().toJson(this);
        return ret;
    }

    public static SCAlbum fromJson(String json) {
        SCAlbum album  = SailorCast.getGson().fromJson(json,SCAlbum.class);
        return album;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAlbumId);
        if (mVideosTotal == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(mVideosTotal);
        }
        dest.writeString(mTitle);
        dest.writeString(mSubTitle);
        dest.writeString(mDirector);
        dest.writeString(mMainActor);
        dest.writeString(mVerImageUrl);
        dest.writeString(mHorImageUrl);
        dest.writeString(mDesc);
        dest.writeString(mTip);
        dest.writeByte((byte) (mIsCompleted == null ? 0 : mIsCompleted ? 1 : 2));
        dest.writeString(mLetvStyle);
        dest.writeString(mTVid);
        dest.writeString(mVid);
        dest.writeString(mScore);
        dest.writeString(mPlayUrl);
        dest.writeString(mAdView);
    }

    public String getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(String mAlbumId) {
        this.mAlbumId = mAlbumId;
    }

    public Integer getVideosTotal() {
        return mVideosTotal;
    }

    public void setVideosTotal(Integer mVideosTotal) {
        this.mVideosTotal = mVideosTotal;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public void setSubTitle(String mSubTitle) {
        this.mSubTitle = mSubTitle;
    }

    public String getDirector() {
        return mDirector;
    }

    public void setDirector(String mDirector) {
        this.mDirector = mDirector;
    }

    public String getMainActor() {
        return mMainActor;
    }

    public void setMainActor(String mMainActor) {
        this.mMainActor = mMainActor;
    }

    public String getVerImageUrl() {
        return mVerImageUrl;
    }

    public void setVerImageUrl(String mVerImageUrl) {
        this.mVerImageUrl = mVerImageUrl;
    }

    public String getHorImageUrl() {
        return mHorImageUrl;
    }

    public void setHorImageUrl(String mHorImageUrl) {
        this.mHorImageUrl = mHorImageUrl;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public SCSite getSite() {
        return mSite;
    }

    public void setSite(SCSite mSite) {
        this.mSite = mSite;
    }

    public String getTip() {
        return mTip;
    }

    public void setTip(String mTip) {
        this.mTip = mTip;
    }

    public Boolean getIsCompleted() {
        return mIsCompleted;
    }

    public void setIsCompleted(Boolean mIsCompleted) {
        this.mIsCompleted = mIsCompleted;
    }

    public String getLetvStyle() {
        return mLetvStyle;
    }

    public void setLetvStyle(String mLetvStyle) {
        this.mLetvStyle = mLetvStyle;
    }

    public String getTVid() {
        return mTVid;
    }

    public void setTVid(String mTVid) {
        this.mTVid = mTVid;
    }

    public String getVid() {
        return mVid;
    }

    public void setVid(String mVid) {
        this.mVid = mVid;
    }

    public String getScore() {
        return mScore;
    }

    public void setScore(String mScore) {
        this.mScore = mScore;
    }

    public String getPlayUrl() {
        return mPlayUrl;
    }

    public void setPlayUrl(String mPlayUrl) {
        this.mPlayUrl = mPlayUrl;
    }

    public String getAdView() {
        return mAdView;
    }

    public void setAdView(String mAdView) {
        this.mAdView = mAdView;
    }
}
