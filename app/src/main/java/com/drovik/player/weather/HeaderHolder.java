package com.drovik.player.weather;

import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.drovik.player.R;
import com.silencedut.router.Router;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HeaderHolder extends BaseViewHolder<HeaderData>{

    @BindView(R.id.tv_located_city)
    TextView mTvLocatedCity;
    @BindView(R.id.city_header_recyclerView)
    RecyclerView mRecyclerView;
    private boolean mLocationSucceeded;

    private BaseRecyclerAdapter mHotCityAdapter;


    public HeaderHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
        Router.instance().register(this);
        initViews();

    }

    @Override
    public void updateItem(HeaderData data, int position) {
        if (data == null || data.getCities().isEmpty()) {
            return;
        }
        mHotCityAdapter.clear();
        List<HotCity> hotCities = new ArrayList<>();
        for (Pair<String, String> city : data.getCities()) {
            hotCities.add(new HotCity(city.first, city.second));
        }
        mHotCityAdapter.registerHolder(HotCityHolder.class, hotCities);
    }

    @Override
    public int getContentViewId() {
        return R.layout.item_city_city_select_header;
    }


    public void initViews() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mHotCityAdapter = new BaseRecyclerAdapter(getContext());
        mRecyclerView.setAdapter(mHotCityAdapter);
    }


    private void showLocation(boolean locationSuccess) {
        mLocationSucceeded = locationSuccess;
        if (locationSuccess) {
            mTvLocatedCity.setText("aaa");
        } else {
            mTvLocatedCity.setText(R.string.city_located_failed);
        }
    }

    @OnClick(R.id.location_layout)
    void locate() {
        if (mLocationSucceeded) {
        } else {
           // CoreManager.getImpl(ILocationApi.class).startLocation();
            mTvLocatedCity.setText(R.string.city_locating);
        }
    }

    static final class HotCity implements BaseAdapterData {
        String mCityName;
        String mCityId;

        HotCity(String cityName, String cityId) {
            mCityName = cityName;
            mCityId = cityId;
        }

        @Override
        public int getContentViewId() {
            return R.layout.item_city_hot_city;
        }
    }

    public static final class HotCityHolder extends BaseViewHolder<HotCity> {

        @BindView(R.id.tv_hot_city_name)
        TextView mTvHotCityName;
        HotCity mHotCity;

        public HotCityHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
            super(itemView, baseRecyclerAdapter);
        }

        @Override
        public void updateItem(HotCity data, int position) {
            mTvHotCityName.setText(data.mCityName);
            mHotCity = data;
        }

        @Override
        public int getContentViewId() {
            return R.layout.item_city_hot_city;
        }

        @OnClick(R.id.tv_hot_city_name)
        void navigationWeather() {
            /*CoreManager.getImpl(IWeatherProvider.class).updateWeather(mHotCity.mCityId);
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }*/
        }
    }
}
