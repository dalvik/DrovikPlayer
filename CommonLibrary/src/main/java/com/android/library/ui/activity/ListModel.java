package com.android.library.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.library.R;

public class ListModel {

    protected ListView listView;
    protected BaseAdapter adapter;
    private BaseCommonActivity activity;
    // listener
    private ListModelListener listener;
    // 无更多内容加载
    private View loading = null;
    //页脚
    private boolean hasFootView;
    //最后可见位置
    private int lastVisiblePosition;
    // 加载更多
    private boolean isLoadMore;
    // 无更多
    private boolean isFootViewNoMore = true;
    //总个数
    private int total;
    //目前已加载的数目
    private int offset;

    public ListModel(BaseCommonActivity activity) {
        this.activity = activity;
    }

    // 设置监听
    public void setListener(ListModelListener listener) {
        this.listener = listener;
    }

    /**
     * 设置ListView
     */
    public void setListView(final ListView list, BaseAdapter adapter) {
        setListView(list, null, adapter);
    }

    /**
     * 设置ListView
     */
    public void setListView(final ListView list, View headerView, final BaseAdapter adapter) {
        listView = list;
        isLoadMore = true;
        LayoutInflater inflater = LayoutInflater.from(activity);
        loading = inflater.inflate(R.layout.footer_loading_small, null);
        boolean removeHeader = false;
        if (headerView == null) {
            // 没有HeaderView , footView 就会有问题
            headerView = new TextView(activity);
            removeHeader = true;
        }
        this.adapter = adapter;
        listView.addHeaderView(headerView);
        listView.setAdapter(adapter);
        if (removeHeader) {
            listView.removeHeaderView(headerView);
        }

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position <= listView.getCount()) {
                    if (listener != null) {
                        int curPosition;
                        int itemCount = adapter.getCount();
                        int childViewCount = listView.getChildCount();
                        int headerCount = listView.getHeaderViewsCount();
                        if (itemCount + headerCount == childViewCount) {
                            // 规范厂商
                            curPosition = position - headerCount;
                        } else {
                            //二逼厂商
                            curPosition = position;
                        }

                        listener.onClickItem(parent, view, position - listView.getHeaderViewsCount(), id);
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onClickItemLong(parent, view, position - listView.getHeaderViewsCount(), id);
                }
                return false;
            }
        });

        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // +1 为了还没到最后一个元素就开始加载更多
                if (lastVisiblePosition +1 >= listView.getCount() - listView.getHeaderViewsCount() && isLoadMore) {
                    isLoadMore = false;
                    if (total == 0 && offset == total) {
                        return;
                    }
                    if (hasNextPage()) {
                        addFootView();
                        if (listener != null) {
                            listener.getMoreListItem();
                        }
                    } else {
                        addFootViewNoMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastVisiblePosition = listView.getLastVisiblePosition();
            }
        });
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 加载出错
     */
    public void loadError() {
        removeFootView();
    }

    /**
     * 设置分页信息
     *
     * @param total 总数
     * @param size  当前页个数
     */
    public void setPage(int total, int size) {
        removeFootView();
        if (total > size && size > 0) {
            isLoadMore = true;
        } else {
            isLoadMore = false;
        }
        this.total = total;
        this.offset += size;
    }

    public void reSetPage(){
        total = 0;
        offset = 0;
    }
    /**
     * 是否有下一页
     *
     * @return
     */
    public boolean hasNextPage() {
        return total > offset;
    }

    /**
     * 当前已加载数目
     * @return
     */
    public int getOffset(){
        return offset;
    }
    /**
     * 添加加载更多
     */
    public void addFootView() {
        listView.addFooterView(loading);
        hasFootView = true;
    }

    /**
     * 添加无更多加载布局
     */
    private void addFootViewNoMore() {
        if (isFootViewNoMore) {
            removeFootView();
            isFootViewNoMore = false;
        }
    }

    /**
     * 移除加载更多
     */
    private void removeFootView() {
        if (hasFootView) {
            listView.removeFooterView(loading);
            hasFootView = false;
        }
    }

    public static interface ListModelListener {

        /**
         * 长按
         *
         * @param parent
         * @param view
         * @param position
         * @param id
         */
        void onClickItemLong(AdapterView<?> parent, View view, int position, long id);

        /**
         * 点击
         *
         * @param parent
         * @param view
         * @param position
         * @param id
         */
        void onClickItem(AdapterView<?> parent, View view, int position, long id);

        /**
         * 获取更多
         */
        void getMoreListItem();

    }
}
