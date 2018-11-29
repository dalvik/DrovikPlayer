package com.drovik.player.weather.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.utils.DateUtil;
import com.drovik.player.R;
import com.drovik.player.weather.BaseRecyclerAdapter;
import com.drovik.player.weather.IWeatherResponse;
import com.drovik.player.weather.ResourceProvider;
import com.drovik.player.weather.data.DailyWeatherData;

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
        if(dailyForecastData.getDate().equals(DateUtil.getMopnthDay())) {
            dateWeek.setText(getContext().getResources().getString(R.string.today));
        }else {
            dateWeek.setText(DateUtil.getWeek(dailyForecastData.getDate()));
        }
        weatherStatusDaily.setText(dailyForecastData.getCond_txt_d());
        tempDaily.setText(dailyForecastData.getTmp_min() + "~" + dailyForecastData.getTmp_max() + "°");
        weatherIconDaily.setImageResource(ResourceProvider.getIconId(dailyForecastData.getCond_txt_d()));
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_daily_forecast;
    }

}
