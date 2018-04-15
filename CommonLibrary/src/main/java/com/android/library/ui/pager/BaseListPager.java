package com.android.library.ui.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.activity.ListModel;
import com.android.library.ui.activity.ListModel.ListModelListener;


public abstract class BaseListPager extends BasePager implements ListModelListener {

    protected ListModel listModel = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listModel = new ListModel((BaseCommonActivity) getActivity());
        listModel.setListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 设置ListView
     *
     * @param list
     * @param adapter
     */
    protected void setListView(ListView list, BaseAdapter adapter) {
        setListView(list, null, adapter);
    }

    /**
     * 设置ListView
     */
    protected void setListView(ListView list, View headerView, BaseAdapter adapter) {
        listModel.setListView(list, headerView, adapter);
    }

    /**
     * 设置页信息
     *
     * @param total
     * @param size
     */
    protected void setPage(int total, int size) {
        listModel.setPage(total, size);
    }

    public void reSetPage() {
        listModel.reSetPage();
    }

    /**
     * 是否是第一页
     * @return
     */
    public boolean isFirstPage() {
        return listModel.getOffset() == 0 ? true : false;
    }

    /**
     * 是否有下一页
     * @return
     */
    public boolean hasNextPage(){
        return listModel.hasNextPage();
    }

    @Override
    public void onResume() {
        super.onResume();
        listModel.notifyDataSetChanged();
    }

}
