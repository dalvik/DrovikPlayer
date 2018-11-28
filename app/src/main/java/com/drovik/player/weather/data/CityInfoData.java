package com.drovik.player.weather.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.drovik.player.R;

public class CityInfoData implements BaseAdapterData, Parcelable {

    public String mInitial;
    public String mCityName;
    public String mCityNamePinyin;
    public String mCityId;

    public CityInfoData(String cityName, String cityNamePinyin, String cityId) {
        this.mCityName = cityName;
        this.mCityNamePinyin = cityNamePinyin;
        this.mCityId = cityId;
    }

    protected CityInfoData(Parcel in) {
        mInitial = in.readString();
        mCityName = in.readString();
        mCityNamePinyin = in.readString();
        mCityId = in.readString();
    }

    public static final Creator<CityInfoData> CREATOR = new Creator<CityInfoData>() {
        @Override
        public CityInfoData createFromParcel(Parcel in) {
            return new CityInfoData(in);
        }

        @Override
        public CityInfoData[] newArray(int size) {
            return new CityInfoData[size];
        }
    };

    public String getInitial() {
        return mInitial;
    }

    public void setInitial(String initial) {
        this.mInitial = initial;
    }


    public String getCityName() {
        return mCityName;
    }


    public String getCityNamePinyin() {
        return mCityNamePinyin;
    }

    public String getCityId() {
        return mCityId;
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_city_city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mInitial);
        dest.writeString(mCityName);
        dest.writeString(mCityNamePinyin);
        dest.writeString(mCityId);
    }
}
