package com.drovik.player.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.library.net.utils.LogUtil;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class HoursForecastRecyclerAdapter extends RecyclerView.Adapter<HoursForecastViewHolder> {

    private SparseArray<Class<? extends HoursForecastViewHolder>> typeHolders = new SparseArray();
    private List<HoursForecastAdapterData> mData = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    protected HolderClickListener mHolderClickListener;
    private String TAG = "HoursForecastRecyclerAdapter";

    public HoursForecastRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void registerHolder(Class<? extends HoursForecastViewHolder> viewHolder, int itemViewType) {
        typeHolders.put(itemViewType, viewHolder);
    }

    public <T extends HoursForecastAdapterData> void registerHolder(Class<? extends HoursForecastViewHolder> viewHolder, T data) {
        if (data == null) {
            return;
        }
        typeHolders.put(data.getContentViewId(), viewHolder);
        addData(data);
    }

    public void registerHolder(Class<? extends HoursForecastViewHolder> viewHolder, List<? extends HoursForecastAdapterData> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        typeHolders.put(data.get(0).getContentViewId(), viewHolder);
        addData(data);
    }

    public void setData(List<? extends HoursForecastAdapterData> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        mData.clear();
        addData(data);
    }

    public void clear() {
        mData.clear();
    }

    public void addData(List<? extends HoursForecastAdapterData> data) {
        if (data == null) {
            return;
        }
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public <T extends HoursForecastAdapterData> void addData(T data) {
        if (data == null) {
            return;
        }
        mData.add(data);

        notifyDataSetChanged();
    }

    @Override
    public HoursForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(viewType, parent, false);
        HoursForecastViewHolder viewHolder = new NoDataViewHolder(itemView, this);
        try {
            Class<?> cls = typeHolders.get(viewType);
            Constructor holderConstructor = cls.getDeclaredConstructor(View.class, HoursForecastRecyclerAdapter.class);
            holderConstructor.setAccessible(true);
            viewHolder = (HoursForecastViewHolder) holderConstructor.newInstance(itemView, this);
        } catch (NoSuchMethodException e) {
            LogUtil.e(TAG, "Create %s error,is it a inner class? can't create no static inner ViewHolder " + typeHolders.get(viewType));
        } catch (Exception e) {
            LogUtil.e(TAG, e.getCause() + "");
        }
        return viewHolder;
    }

    public List<HoursForecastAdapterData> getData() {
        return mData;
    }


    @Override
    public void onBindViewHolder(HoursForecastViewHolder holder, int position) {
        if (mData == null || mData.isEmpty() || mData.get(position) == null) {
            return;
        }

        if (getItemViewType(position) != holder.getContentViewId()) {
            return;
        }

        holder.updateItem(mData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getContentViewId();
    }

    public void setOnHolderClickListener(HolderClickListener clickListener) {
        this.mHolderClickListener = clickListener;
    }

    public  interface HolderClickListener <T extends HoursForecastAdapterData>{
        void onHolderClicked(int position, T data);
    }

    public <T extends HoursForecastAdapterData> void onHolderClicked(int position,T data) {
        if(mHolderClickListener!=null) {
            mHolderClickListener.onHolderClicked(position,data);
        }
    }
}
