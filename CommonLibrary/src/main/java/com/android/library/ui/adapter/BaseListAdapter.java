package com.android.library.ui.adapter;

import android.content.res.Resources;
import android.widget.BaseAdapter;

import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<T> extends BaseAdapter {

    protected BaseCommonActivity activity;

    protected List<T> items;

    public BaseListAdapter(BaseCommonActivity activity) {
        this.activity = activity;
        items = new ArrayList<T>();
    }

    public void addItems(ArrayList<T> items) {
        if (!TextUtils.isEmpty(items)) {
            this.items.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void addItem(T item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<T> items) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        try {
            items.remove(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        if (position > getCount() - 1) {
            position = getCount() - 1;
        }
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * get Resource
     *
     * @return
     */
    protected Resources getResources() {
        return activity.getResources();
    }

    /**
     * get String
     *
     * @param resId
     * @return
     */
    protected String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * get String
     *
     * @param resId
     * @param args
     * @return
     */
    protected String getString(int resId, Object... args) {
        return getResources().getString(resId, args);
    }

    /**
     * get Color
     *
     * @param resId
     * @return
     */
    protected int getColor(int resId) {
        return getResources().getColor(resId);
    }
}
