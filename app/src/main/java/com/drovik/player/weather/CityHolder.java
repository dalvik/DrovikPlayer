package com.drovik.player.weather;

import android.view.View;
import android.widget.TextView;

import com.drovik.player.R;

import butterknife.BindView;
import butterknife.OnClick;


public class CityHolder extends BaseViewHolder<CityInfoData> {

    @BindView(R.id.tv_item_city_letter)
    TextView mTvItemCityLetter;
    @BindView(R.id.tv_item_city_name)
    TextView mTvItemCityName;

    private String mCityId;

    public CityHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(CityInfoData data, int position) {
        mCityId = data.getCityId();

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

    }
}
