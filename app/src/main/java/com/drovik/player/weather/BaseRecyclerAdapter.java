package com.drovik.player.weather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private SparseArray<Class<? extends BaseViewHolder>> typeHolders = new SparseArray();
    private List<BaseAdapterData> mData = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    protected HolderClickListener mHolderClickListener;

    public BaseRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void registerHolder(Class<? extends BaseViewHolder> viewHolder, int itemViewType) {
        typeHolders.put(itemViewType, viewHolder);
    }

    public <T extends BaseAdapterData> void registerHolder(Class<? extends BaseViewHolder> viewHolder, T data) {
        if (data == null) {
            return;
        }
        typeHolders.put(data.getContentViewId(), viewHolder);
        addData(data);
    }

    public void registerHolder(Class<? extends BaseViewHolder> viewHolder, List<? extends BaseAdapterData> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        typeHolders.put(data.get(0).getContentViewId(), viewHolder);
        addData(data);
    }

    public void setData(List<? extends BaseAdapterData> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        mData.clear();
        addData(data);
    }

    public void clear() {
        mData.clear();
    }

    public void addData(List<? extends BaseAdapterData> data) {
        if (data == null) {
            return;
        }
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public <T extends BaseAdapterData> void addData(T data) {
        if (data == null) {
            return;
        }
        mData.add(data);

        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(viewType, parent, false);
        BaseViewHolder viewHolder = new NoDataViewHolder(itemView, this);
        try {
            Class<?> cls = typeHolders.get(viewType);
            Constructor holderConstructor = cls.getDeclaredConstructor(View.class, BaseRecyclerAdapter.class);
            holderConstructor.setAccessible(true);
            viewHolder = (BaseViewHolder) holderConstructor.newInstance(itemView, this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewHolder;
    }

    public List<BaseAdapterData> getData() {
        return mData;
    }


    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (mData == null || mData.isEmpty() ||  mData.get(position) ==null) {
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

    public  interface HolderClickListener <T extends BaseAdapterData>{
        void onHolderClicked(int position, T data);
    }

    public <T extends BaseAdapterData> void onHolderClicked(int position,T data) {
        if(mHolderClickListener!=null) {
            mHolderClickListener.onHolderClicked(position,data);
        }
    }
}
