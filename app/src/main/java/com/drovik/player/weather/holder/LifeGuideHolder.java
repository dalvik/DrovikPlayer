package com.drovik.player.weather.holder;

import android.view.View;
import android.widget.TextView;

import com.drovik.player.R;
import com.drovik.player.weather.BaseRecyclerAdapter;
import com.drovik.player.weather.WeatherCallBack;
import com.drovik.player.weather.data.LifeIndexGuideData;
import com.drovik.player.weather.holder.BaseViewHolder;
import com.silencedut.router.Router;

import butterknife.BindView;

public class LifeGuideHolder extends BaseViewHolder<LifeIndexGuideData> implements WeatherCallBack.LifeAdvice {
    @BindView(R.id.guide_title)
    TextView mGuideTitle;

    public LifeGuideHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
        Router.instance().register(this);
    }

    @Override
    public void updateItem(LifeIndexGuideData data, int position) {
        mGuideTitle.setText(data.guide);
    }


    @Override
    public void lifeAdvice(String index, String advice) {
        mGuideTitle.setText(index);
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_index_guide;
    }
}
