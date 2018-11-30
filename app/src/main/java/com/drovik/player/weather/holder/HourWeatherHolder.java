package com.drovik.player.weather.holder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SharedMemory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.ui.SettingsActivity;
import com.drovik.player.R;
import com.drovik.player.weather.BaseRecyclerAdapter;
import com.drovik.player.weather.IWeatherResponse;
import com.drovik.player.weather.ResourceProvider;
import com.drovik.player.weather.WeatherActivity;
import com.drovik.player.weather.WeatherCallBack;
import com.drovik.player.weather.data.HoursForecastData;
import com.drovik.player.weather.holder.BaseViewHolder;
import com.silencedut.router.Router;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

public class HourWeatherHolder extends BaseViewHolder<HoursForecastData> {
    @BindView(R.id.hour)
    TextView hour;
    @BindView(R.id.hour_icon)
    ImageView hourIcon;
    @BindView(R.id.hour_temp)
    TextView hourTemp;

    public HourWeatherHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(HoursForecastData data, int position) {
        IWeatherResponse.Data.Hourly hourly = data.hourly;
        if (hourly == null) {
            return;
        }
        hour.setText(hourly.getTime().substring(11, 16));
        hourIcon.setImageResource(ResourceProvider.getResource(getContext(), "p" + hourly.getCond_code()));
        hourTemp.setText(hourly.getTmp() + "°");
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_hour_forecast;
    }

    @OnClick(R.id.hour_forecast)
    public void onClick() {
        Intent weatherActivity = new Intent(getContext(), WeatherActivity.class);
        weatherActivity.putExtra(ResourceProvider.TYPE, 1);
        getContext().startActivity(weatherActivity);
    }
}
