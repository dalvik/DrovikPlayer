package com.drovik.player.weather.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.drovik.player.R;
import com.drovik.player.weather.BaseRecyclerAdapter;
import com.drovik.player.weather.data.GuideData;
import com.drovik.player.weather.holder.BaseViewHolder;

import butterknife.BindView;

public class GuideHolder extends BaseViewHolder<GuideData> {
    @BindView(R.id.guide_title)
    TextView mGuideTitle;
    @BindView(R.id.guide_icon)
    ImageView mGuideIcon;

    public GuideHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(GuideData data, int position) {
        mGuideTitle.setText(data.guide);
        if (data.guideIconId != 0) {
            mGuideIcon.setImageResource(data.guideIconId);
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_weather_guide;
    }

}
