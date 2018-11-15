package com.drovik.player.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;

public abstract class HoursForecastViewHolder<T extends IWeatherResponse> extends RecyclerView.ViewHolder implements IProvideItemId {

    protected HoursForecastRecyclerAdapter mBaseAdapter;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private void setUIContext(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    protected LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    protected Context getContext() {
        return mContext;
    }


    public HoursForecastViewHolder(View itemView, HoursForecastRecyclerAdapter baseRecyclerAdapter) {
        super(itemView);
        mBaseAdapter = baseRecyclerAdapter;
        ButterKnife.bind(this, itemView);
        setUIContext(itemView.getContext());
    }


    public abstract void updateItem(T data, int position);
}
