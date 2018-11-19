package com.drovik.player.weather;

import android.support.annotation.NonNull;

/**
 * Created by SilenceDut on 2018/1/4 .
 */

public class City {
    /**
     * cityId : CN101310230
     * province : 广东
     * city : 广州
     * country： 番禺
     * longitude : 114.20
     * latitude : 11.26
     */

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
