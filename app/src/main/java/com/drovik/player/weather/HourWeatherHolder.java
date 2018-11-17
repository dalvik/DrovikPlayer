package com.drovik.player.weather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.drovik.player.R;

import butterknife.BindView;

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

}
