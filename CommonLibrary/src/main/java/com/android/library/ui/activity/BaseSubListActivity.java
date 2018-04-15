package com.android.library.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class BaseSubListActivity extends BaseCompatActivity implements ListModel.ListModelListener {

    protected ListModel listModel = null;

    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsNeedAddWaitingView = true;
        super.onCreate(savedInstanceState);
        listModel = new ListModel(this);
        listModel.setListener(this);

    }

    /**
     * 设置ListView
     *
     * @param list
     * @param adapter
     */
    protected void setListView(final ListView list, BaseAdapter adapter) {
        setListView(list, null, adapter);
    }

    /**
     * 设置ListView
     */
    protected void setListView(final ListView list, View headerView, BaseAdapter adapter) {
        listModel.setListView(list, headerView, adapter);
    }

    protected void addFoodView() {
        listModel.addFootView();
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


    public void reSetPage(){
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

}
