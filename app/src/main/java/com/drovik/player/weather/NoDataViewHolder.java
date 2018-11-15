package com.drovik.player.weather;

import android.view.View;

public class NoDataViewHolder extends HoursForecastViewHolder {
    public NoDataViewHolder(View itemView, HoursForecastRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(HoursForecastAdapterData Data, int position) {

    }

    @Override
    public int getContentViewId() {
        return 0;
    }


}
