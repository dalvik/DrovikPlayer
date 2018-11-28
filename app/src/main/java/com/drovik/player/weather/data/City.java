package com.drovik.player.weather.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class City implements Parcelable {
    @NonNull
    public String cityId = "";

    public String country;

    public String countryEn;

    public String cityName;

    public String province;

    public String provinceEn;

    public String longitude;

    public String latitude;

    public City() {

    }

    protected City(Parcel in) {
        cityId = in.readString();
        country = in.readString();
        countryEn = in.readString();
        cityName = in.readString();
        province = in.readString();
        provinceEn = in.readString();
        longitude = in.readString();
        latitude = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityId);
        dest.writeString(country);
        dest.writeString(countryEn);
        dest.writeString(cityName);
        dest.writeString(province);
        dest.writeString(provinceEn);
        dest.writeString(longitude);
        dest.writeString(latitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public String toString() {
        return String.format("cityId=%s", this.cityName)
                + String.format("province=%s", this.province)
                + String.format("provinceEn=%s", this.provinceEn)
                + String.format("city=%s", this.cityName)
                + String.format("country=%s", this.country)
                + String.format("countryEn=%s", this.countryEn)
                + String.format("longitude=%s", this.longitude)
                + String.format("latitude=%s", this.latitude);
    }
}
