package com.drovik.player.weather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.silencedut.baselib.commonhelper.adapter.BaseRecyclerAdapter;
import com.silencedut.baselib.commonhelper.adapter.BaseViewHolder;
import com.silencedut.baselib.commonhelper.utils.Check;
import com.silencedut.knowweather.R;
import com.silencedut.weather_core.api.weatherprovider.WeatherData;
import com.silencedut.weather_core.corebase.ResourceProvider;

import butterknife.BindView;

/**
 * Created by SilenceDut on 16/10/21.
 */

public class HourWeatherHolder extends HoursForecastViewHolder<IWeatherResponse> {
    @BindView(R.id.hour)
    TextView hour;
    @BindView(R.id.hour_icon)
    ImageView hourIcon;
    @BindView(R.id.hour_temp)
    TextView hourTemp;

    public HourWeatherHolder(View itemView, HoursForecastRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(IWeatherResponse data, int position) {
        WeatherData.HoursForecastEntity hoursForecastData = data.ge;
        if (Check.isNull(hoursForecastData)) {
            return;
        }
        hour.setText(hoursForecastData.getTime().substring(11, 16));
        hourIcon.setImageResource(ResourceProvider.getIconId(hoursForecastData.getWeather()));
        hourTemp.setText(hoursForecastData.getTemp());
    }

    @Override
    public int getContentViewId() {
        return R.layout.weather_item_hour_forecast;
    }

}
