package com.drovik.player.news.fragment;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.Utils;
import com.drovik.player.R;
import com.drovik.player.news.NewsFrameActivity;
import com.drovik.player.news.mvp.BaseLazyFragment;
import com.drovik.player.news.mvp.BaseList1Fragment;
import com.drovik.player.news.mvp.BasePagerAdapter;
import com.drovik.player.news.utils.SettingUtil;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yc on 2018/2/28.
 *
 */

public class NewsFragment extends BaseLazyFragment {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private NewsFrameActivity activity;

    private List<Fragment> fragmentList = new ArrayList<>();
    private BasePagerAdapter adapter;
    private String[] categoryId;
    private String[] categoryName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (NewsFrameActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        tabLayout.setBackgroundColor(SettingUtil.getInstance().getColor());
    }


    @Override
    public int getContentView() {
        return R.layout.base_tab_view;
    }

    @Override
    public void initView() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setBackgroundColor(SettingUtil.getInstance().getColor());
        viewPager.setOffscreenPageLimit(10);
        setup(tabLayout);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        categoryId = Utils.getContext().getResources().getStringArray(R.array.mobile_news_id);
        categoryName = Utils.getContext().getResources().getStringArray(R.array.mobile_news_name);
    }

    @Override
    public void onLazyLoad() {
        ArrayList<String> title = new ArrayList<>();
        if(categoryId.length<=15){
            return;
        }
        for (int i = 0; i < 15; i++) {
            Fragment fragment = NewsArticleFragment.newInstance(categoryId[i]);
            fragmentList.add(fragment);
            title.add(categoryName[i]);
        }
        adapter = new BasePagerAdapter(getChildFragmentManager(), fragmentList,title);
        viewPager.setAdapter(adapter);
    }

    public void onDoubleClick() {
        if (fragmentList != null && fragmentList.size() > 0) {
            int item = viewPager.getCurrentItem();
            ((BaseList1Fragment) fragmentList.get(item)).onRefresh();
        }
    }

    public void setup(View view) {
        int compatPadingTop = 0;
        // android 4.4以上将Toolbar添加状态栏高度的上边距，沉浸到状态栏下方
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            compatPadingTop = getStatusBarHeight();
        }
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + compatPadingTop, view.getPaddingRight(), view.getPaddingBottom());
    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.d("CompatToolbar", "状态栏高度：" + px2dp(statusBarHeight) + "dp");
        return statusBarHeight;
    }

    public float px2dp(float pxVal) {
        final float scale = getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }
}
