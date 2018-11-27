package com.drovik.player.weather;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.drovik.player.R;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;


public class CityHolder extends BaseViewHolder<CityInfoData> {

    @BindView(R.id.tv_item_city_letter)
    TextView mTvItemCityLetter;
    @BindView(R.id.tv_item_city_name)
    TextView mTvItemCityName;

    private String mCityId;
    private String mCityName;

    public CityHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(CityInfoData data, int position) {
        mCityId = data.getCityId();
        mCityName = data.getCityName();
        mTvItemCityName.setText(data.getCityName());
        if (data.getInitial() != null) {
            mTvItemCityLetter.setVisibility(View.VISIBLE);
            mTvItemCityLetter.setText(data.getInitial());
        } else {
            mTvItemCityLetter.setVisibility(View.GONE);
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_city_city;
    }

    @OnClick(R.id.tv_item_city_name)
    public void onClick() {
        if (getContext() instanceof Activity) {
            LocationEvent event = new LocationEvent(mCityName);
            EventBus.getDefault().post(event);
            ((Activity) getContext()).finish();
        }
    }
}
