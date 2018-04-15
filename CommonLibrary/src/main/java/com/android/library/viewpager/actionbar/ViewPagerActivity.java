package com.android.library.viewpager.actionbar;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;

import com.android.library.R;

import java.util.ArrayList;

public class ViewPagerActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private ActionBar mTabBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_layout_viewpager_actionbar);

        mViewPager = (ViewPager) findViewById(R.id.lib_id_viewpager_viewpager);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this, mViewPager);
        mTabBar = getActionBar();

        mAdapter.addTab(mTabBar.newTab().setIcon(R.drawable.lib_drawable_viewpager_actionbar_tab_icon_qworld),
                FirstFragment.class, null);
        mAdapter.addTab(mTabBar.newTab().setIcon(R.drawable.lib_drawable_viewpager_actionbar_tab_icon_qworld),
                SecondFragment.class, null);
        /*mAdapter.addTab(mTabBar.newTab().setIcon(R.drawable.tab_icon_group),
                SecondFragment.class, null);
        mAdapter.addTab(mTabBar.newTab().setIcon(R.drawable.tab_icon_friends),
                ThreeFragment.class, null);
        mAdapter.addTab(mTabBar.newTab().setIcon(R.drawable.tab_icon_recent),
                FourFragment.class, null);*/

    }

    private View getCustomView() {
        return getLayoutInflater().inflate(R.layout.lib_layout_viewpager_actionbar_title_panel, null);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter
            implements TabListener, OnPageChangeListener {

        private Context mContext;
        private ActionBar mActionBar = null;
        private ViewPager mViewPager = null;
        private ArrayList<TabInfo> mTabList = new ArrayList<TabInfo>();

        public ViewPagerAdapter(FragmentManager fg, Activity activity, ViewPager viewPager) {
            // TODO Auto-generated constructor stub
            super(fg);
            mContext = activity;
            mActionBar = activity.getActionBar();
            /*
             * mActionBar.setCustomView(getCustomView(), new LayoutParams(
             * LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
             */
            mActionBar.setDisplayOptions(mActionBar.getDisplayOptions()
                    ^ ActionBar.DISPLAY_SHOW_HOME
                    ^ ActionBar.DISPLAY_SHOW_TITLE, ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE);
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            mViewPager = viewPager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        /** Override OnPageChangeListener start **/
        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            mActionBar.setSelectedNavigationItem(position);
        }

        /** Override OnPageChangeListener end **/

        /** Override TabListener start **/
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub
            Object tag = tab.getTag();
            for (int i = 0; i < mTabList.size(); i++) {
                if (mTabList.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub

        }

        /** Override TabListener end **/

        /** Override FragmentPagerAdapter start **/
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // TODO Auto-generated method stub
            TabInfo tab = mTabList.get(position);
            if (tab.fragment == null) {
                tab.fragment = Fragment.instantiate(mContext,
                        tab.clazz.getName(), tab.bundle);
            }
            return null;//tab.fragment;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mTabList.size();
        }

        /** Override FragmentPagerAdapter end **/

        private View getCustomView() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.lib_layout_viewpager_actionbar_title_panel, null);
        }

        public void addTab(Tab tab, Class<?> clazz, Bundle bundle) {
            TabInfo tabInfo = new TabInfo(clazz, bundle);
            tab.setTag(tabInfo);
            tab.setTabListener(this);
            mTabList.add(tabInfo);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        private static final class TabInfo {
            private final Class<?> clazz;
            private final Bundle bundle;
            Fragment fragment;

            TabInfo(Class<?> clazz, Bundle bundle) {
                this.clazz = clazz;
                this.bundle = bundle;
            }
        }

    }

}
