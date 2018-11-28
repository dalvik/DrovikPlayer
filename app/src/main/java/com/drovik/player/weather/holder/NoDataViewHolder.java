package com.drovik.player.weather.holder;

import android.view.View;

import com.drovik.player.weather.BaseRecyclerAdapter;
import com.drovik.player.weather.data.BaseAdapterData;
import com.drovik.player.weather.holder.BaseViewHolder;

public class NoDataViewHolder extends BaseViewHolder {

    public NoDataViewHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(BaseAdapterData Data, int position) {

    }

    @Override
    public int getContentViewId() {
        return 0;
    }


}
