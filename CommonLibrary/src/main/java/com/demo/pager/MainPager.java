package com.demo.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.library.R;
import com.android.library.ui.adapter.BannerAdapter;
import com.android.library.ui.adapter.TreatListAdapter;
import com.android.library.ui.pager.BaseInnerLoadListPager;
import com.android.library.ui.view.BannerGallery;
import com.baoyz.widget.PullRefreshLayout;

public class MainPager extends BaseInnerLoadListPager {

    // 广告
    public BannerGallery adGallery; // 图片滚动控件
    //private TreatListManager treatListManager;
    private int whatList;

    private BannerAdapter adAdapter;
    //private OfficialManager bannerManager;
    private int whatBanner;

    private TreatListAdapter treatAdapter;
    private ListView listView;
    private Long currentFrash;
    private Long lastFrash;

    private boolean isRefresh = true;
    private PullRefreshLayout layout;
    // 是否刷新新数据
    private boolean isNew;

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.treat_list, null);
        adGallery = new BannerGallery(activity);
        // 设置广告的高度
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, activity.getResources()
//                .getDimensionPixelSize(R.dimen.ad_height));
        adGallery.setLayoutParams(params);
        adGallery.setVisibility(View.GONE);
        adAdapter = new BannerAdapter(activity);
        adGallery.setAdapter(adAdapter);

        /** 滚动组件 */
        adGallery.setOnItemClickListener(new BannerGallery.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (adAdapter.getCount() > 0) {
                    //ActivityUtil.gotoWebBrowserActivity(activity, adAdapter.getItem(position).send_url);
                }
            }
        });
        layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        listView = (ListView) layout.findViewById(R.id.listView);
        treatAdapter = new TreatListAdapter(activity);
        setListView(listView, adGallery, treatAdapter);
        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                isRefresh = true;
                reSetPage();
                getList();
                //防止网络异常下次不能刷新
                layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.setRefreshing(false);
                    }
                }, 10 * 000);
            }
        });

        // refresh complete
       // layout.setRefreshing(false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //treatListManager = new TreatListManager(this);
        //bannerManager = new OfficialManager(this);
        getList();
        getBanner();
    }

    private void getBanner() {
        //whatBanner = bannerManager.getBannerList();
    }

    private void getList() {
        //TreatListReq bean = ((MainActivity) activity).filterBean;
        //whatList = treatListManager.getList(bean.bizCategory, bean.cityCode, bean.lat, bean.lng, currentFrash, lastFrash, bean.sex, bean.sort, listModel.getOffset(), isNew);
    }

    public void refresh() {
        reSetPage();
        isRefresh = true;
        currentFrash = null;
        lastFrash = null;
        getList();
    }

    /**
     * 刷新列表数据
     *
     * @param resp
     */
    /*private void setTreatList(TreatListResp resp) {
        ArrayList<TreatResp> list = new ArrayList<TreatResp>();
        BasePagesResp<TreatResp> page = resp.nList;
        int total = 0;
        int offset = 0;
        boolean hasNew = false;
        if (page != null) {
            ArrayList<TreatResp> newList = page.rows;
            if (!newList.isEmpty()) {
                hasNew = true;
                total += page.total;
                offset += newList.size();
                newList.get(0).tipType = TreatResp.TIP_TYPE_NEW;
                list.addAll(newList);
            }
        }
        page = resp.oldList;
        if (page != null) {
            ArrayList<TreatResp> oldList = page.rows;
            if (!oldList.isEmpty()) {
                total += page.total;
                offset += oldList.size();
                if (hasNew) {
                    oldList.get(0).tipType = TreatResp.TIP_TYPE_OLD;
                }
                list.addAll(oldList);
            }
        }
        if (isRefresh) {
            lastFrash = currentFrash;
            currentFrash = resp.sysTime;
            isRefresh = false;
            treatAdapter.setItems(list);
        } else {
            treatAdapter.addItems(list);
        }
        setPage(total, offset);
    }*/

    /*private void setCurrentTreat(TreatResp treat) {

    }*/

    @Override
    protected boolean onHandleBiz(int what, int result, int arg2, Object obj) {
        /*layout.setRefreshing(false);
        if (what == whatList) {
            switch (result) {
                case TreatListManager.RESULT_SUCCESS:
                    TreatListResp data = ((BaseData<TreatListResp>) obj).data;
                    setTreatList(data);
                    setCurrentTreat(data.joinActivities);
                    break;
                default:
                    return false;
            }
        } else if (whatBanner == what) {
            switch (result) {
                case OfficialManager.RESULT_SUCCESS:
                    ArrayList<OfficialResp> items = ((BaseObjectData<ArrayList<OfficialResp>>) obj).data;
                    adAdapter.setItems(items);
                    adGallery.start(activity, 4000);
                    adGallery.setVisibility(View.VISIBLE);
                    break;
                default:
                    return false;
            }
        }*/
        return true;
    }

    @Override
    public void reload() {
        getList();
    }

    @Override
    public void onClickItemLong(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onClickItem(AdapterView<?> parent, View view, int position, long id) {
        /*TreatResp treat = treatAdapter.getItem(position);
        switch (treat.bigType) {
            case RespConstants.TreatBigType.BIGTYPE_OFFLINE:
                ActivityUtil.gotoTreatDetail(activity, treat);
                break;
            case RespConstants.TreatBigType.BIGTYPE_ONLINE:
                ActivityUtil.gotoTreatOnlineDetail(activity, treat);
                break;
        }*/
    }

    @Override
    public void getMoreListItem() {
        getList();
    }
}
