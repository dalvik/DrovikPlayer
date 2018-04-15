package com.android.library.ui.utils;


import com.android.library.utils.TextUtils;

import java.util.ArrayList;

public class China {

    public ArrayList<Province> provinces;

    public String getCityName(String cityCode) {
        if(TextUtils.isEmpty(cityCode)){
            return null;
        }
        for(Province province: provinces){
            for(City city:province.citys){
                if(cityCode.equals(city.code)){
                    return city.city;
                }
            }
        }
        return null;
    }

    public static class Province {
        public String province;
        public String code;
        public ArrayList<City> citys;
    }

    public static class City {
        public String city;
        public String code;
    }
}
