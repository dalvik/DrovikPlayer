package com.drovik.player.weather;

import android.content.Context;
import android.os.Handler;

import com.drovik.player.ui.fragment.HomeFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CityProvider {

    private Context mContext;
    private Handler mCityHandler;
    private ArrayList<City> allCitys;

    public CityProvider(Context context, Handler handler) {
        this.mContext = context;
        this.mCityHandler = handler;
        allCitys = new ArrayList<City>();
    }

    public void loadCitys() {
        mCityHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    String citys = assetFile2String("china_citys.txt", mContext);
                    JSONArray jsonArray = new JSONArray(citys);

                    for (int index = 0; index < jsonArray.length(); index++) {
                        JSONObject cityObject = jsonArray.getJSONObject(index);
                        CityEntry cityEntry = fromJson(cityObject.toString(), CityEntry.class);

                        for (CityEntry.CityBean cityBean : cityEntry.getCity()) {
                            for (CityEntry.CityBean.CountyBean county : cityBean.getCounty()) {
                                City city = new City();
                                city.province = cityEntry.getName();
                                city.provinceEn = cityEntry.getName_en();
                                city.cityName = cityBean.getName();
                                city.cityId = county.getCode();
                                city.country = county.getName();
                                city.countryEn = county.getName_en();

                                allCitys.add(city);
                            }
                        }
                    }
                    Collections.sort(allCitys, new CityComparator());
                    mCityHandler.sendEmptyMessage(HomeFragment.LOAD_CITYS_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ArrayList<City> getCitys() {
        return allCitys;
    }

    private <T> T fromJson(String string, Class<T> clazz) {
        Gson GSON = new Gson();
        return GSON.fromJson(string, clazz);
    }


    private String assetFile2String(String fileName, Context context) {
        StringBuilder result = new StringBuilder();
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            String line;

            while ((line = bufReader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        closeIO(inputReader, bufReader);

        return result.toString();
    }

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    public static void closeIO(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * a-z排序
     */
    private class CityComparator implements Comparator<City> {

        @Override
        public int compare(City cityLeft, City cityRight) {

            char a = cityLeft.countryEn.charAt(0);
            char b = cityRight.countryEn.charAt(0);

            return a - b;
        }
    }
}
