package com.drovik.player.video.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.provider.FileProviderService;
import com.android.audiorecorder.ui.pager.MainCenterPager;
import com.android.audiorecorder.ui.pager.MainFindPager;
import com.android.audiorecorder.ui.pager.MainMicRecordPager;
import com.android.audiorecorder.ui.pager.MainRecordPager;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.library.ui.activity.BaseCommonActivity;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.SelectableViewAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class MovieHomeActivity extends BaseCommonActivity {

    private static final int POSITION_MAIN = 0;
    private static final int POSITION_MSG = 1;
    private static final int POSITION_FIND = 2;
    public static final int POSITION_CENTER = 3;

    private static Fragment[] pagers = new Fragment[4];

    private ViewPager viewPager;
    private TabPageIndicator indicator;
    private MainAdapter adapter;
    //private HttpClient httpClient = new DefaultHttpClient();
    private int mTitle[] = {};//R.string.site_youku, R.string.site_letv, R.string.site_sohu, R.string.site_iqiyi
    private TextView titleTv;// 标题
    private TextView leftTv;
    private CheckedTextView rightTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_viewpager);
        viewPager = (ViewPager) findViewById(R.id.lib_id_viewpager_viewpager);
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        // 标题
        titleTv = (TextView) findViewById(R.id.title_tv);

        leftTv = (TextView) findViewById(R.id.leftTv);
        rightTv = (CheckedTextView) findViewById(R.id.rightTv);

        adapter = new MainAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        indicator.setSmoothScrollingEnabled(false);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setSelectedTab(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        titleTv.setText(mTitle[0]);
        setMainButton(0);
    }

    private void setSelectedTab(int position) {
        switch (position) {
            case POSITION_MAIN:
                titleTv.setText(mTitle[POSITION_MAIN]);
                break;
            case POSITION_MSG:
                titleTv.setText(mTitle[POSITION_MSG]);
                break;
            case POSITION_FIND:
                titleTv.setText(mTitle[POSITION_FIND]);
                break;
            case POSITION_CENTER:
                titleTv.setText(mTitle[POSITION_CENTER]);
                break;
        }
        indicator.setCurrentItem(position);
        setMainButton(position);
    }

    private void setMainButton(int position){
        if (position == POSITION_MSG) {
            leftTv.setVisibility(View.VISIBLE);
            leftTv.setBackgroundResource(R.drawable.msg_friends);
            leftTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtil.gotoContactsActivity(activity);
                }
            });
            rightTv.setVisibility(View.GONE);
            /*rightTv.setBackgroundResource(R.drawable.msg_notify);
            rightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtil.gotoNotifyActivity(activity);
                }
            });*/
        } else {
            rightTv.setVisibility(View.GONE);
            rightTv.setOnClickListener(null);

            leftTv.setVisibility(View.GONE);
            leftTv.setOnClickListener(null);

            titleTv.setOnClickListener(null);

        }
    }
    private static class MainAdapter extends FragmentPagerAdapter implements SelectableViewAdapter, IconPagerAdapter {

        private int[] ICONS = new int[]{
                R.drawable.tab_main_selector,
                R.drawable.tab_msg_selector,
                R.drawable.tab_find_selector,
                R.drawable.tab_center_selector
        };

        public MainAdapter(FragmentManager fm) {
            super(fm);
            pagers[POSITION_MAIN] = new MainRecordPager();
            pagers[POSITION_MSG] = new MainMicRecordPager();
            pagers[POSITION_FIND] = new MainFindPager();
            pagers[POSITION_CENTER] = new MainCenterPager();
        }

        @Override
        public int getIconResId(int index) {
            return ICONS[index];
        }

        @Override
        public int getCount() {
            return pagers.length;
        }

        @Override
        public Fragment getItem(int position) {
            return pagers[position];
        }

        @Override
        public TabPageIndicator.SelectableView getView(int position) {
            return null;
        }
    }


}
