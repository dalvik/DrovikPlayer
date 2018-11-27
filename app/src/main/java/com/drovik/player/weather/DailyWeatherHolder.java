package com.drovik.player.weather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.utils.DateUtil;
import com.drovik.player.R;

import butterknife.BindView;

public class DailyWeatherHolder extends BaseViewHolder<DailyWeatherData> {

    @BindView(R.id.date_week)
    TextView dateWeek;
    @BindView(R.id.weather_status_daily)
    TextView weatherStatusDaily;
    @BindView(R.id.weather_icon_daily)
    ImageView weatherIconDaily;
    @BindView(R.id.temp_daily)
    TextView tempDaily;

    public DailyWeatherHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(DailyWeatherData data, int position) {
        IWeatherResponse.Data.DailyForecast dailyForecastData = data.dailyForecastData;
        if(dailyForecastData == null) {
            return;
        }
        if(dailyForecastData.getDate().equals(DateUtil.getWeek(DateUtil.getMopnthDay()))) {
            dateWeek.setText("今天");
        }else {
            dateWeek.setText(dailyForecastData.getDate());
        }

        weatherStatusDaily.setText(dailyForecastData.getTmp_min());
        tempDaily.setText(dailyForecastData.getTmp_min() + "~" + dailyForecastData.getTmp_max());
        //weatherIconDaily.setImageResource(ResourceProvider.getIconId(dailyForecastData.getWeather()));
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_daily_forecast;
    }

}
