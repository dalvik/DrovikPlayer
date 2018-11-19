package com.drovik.player.weather;

import android.support.annotation.NonNull;
public class City {

    @NonNull
    public String cityId = "";

    public String country;

    public String countryEn;

    public String cityName;

    public String province;

    public String provinceEn;

    public String longitude;

    public String latitude;

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
