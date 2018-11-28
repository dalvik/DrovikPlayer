package com.drovik.player.weather.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.drovik.player.weather.BaseRecyclerAdapter;
import com.drovik.player.weather.IProvideItemId;
import com.drovik.player.weather.data.BaseAdapterData;

import butterknife.ButterKnife;

public abstract class BaseViewHolder<T extends BaseAdapterData> extends RecyclerView.ViewHolder implements IProvideItemId {

    protected BaseRecyclerAdapter mBaseAdapter;
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


    public BaseViewHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView);
        mBaseAdapter = baseRecyclerAdapter;
        ButterKnife.bind(this, itemView);
        setUIContext(itemView.getContext());
    }


    public abstract void updateItem(T data, int position);
}
