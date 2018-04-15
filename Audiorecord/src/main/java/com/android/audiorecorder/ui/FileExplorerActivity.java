package com.android.audiorecorder.ui;

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
import com.android.audiorecorder.ui.pager.FileRecordPager;
import com.android.library.ui.activity.BaseCommonActivity;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.SelectableViewAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class FileExplorerActivity extends BaseCommonActivity {

    private static final int POSITION_MAIN = 0;

    private static Fragment[] pagers = new Fragment[1];

    private ViewPager viewPager;
    private MainAdapter adapter;
    private int mTitle[] = {R.string.main_tab_main};
    private TextView titleTv;// 标题
    private TextView leftTv;
    private CheckedTextView rightTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, MultiMediaService.class));
        startService(new Intent(this, FileProviderService.class));
        setContentView(R.layout.activity_file_explorer_viewpager);
        viewPager = (ViewPager) findViewById(R.id.lib_id_viewpager_viewpager);
        // 标题
        titleTv = (TextView) findViewById(R.id.title_tv);

        leftTv = (TextView) findViewById(R.id.leftTv);
        rightTv = (CheckedTextView) findViewById(R.id.rightTv);

        adapter = new MainAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        titleTv.setText(mTitle[0]);
        setMainButton(0);
    }

    private void setMainButton(int position){
        rightTv.setVisibility(View.GONE);
        rightTv.setOnClickListener(null);

        leftTv.setVisibility(View.GONE);
        leftTv.setOnClickListener(null);

        titleTv.setOnClickListener(null);
    }

    private static class MainAdapter extends FragmentPagerAdapter implements SelectableViewAdapter, IconPagerAdapter {

        private int[] ICONS = new int[]{
                R.drawable.tab_main_selector,
        };

        public MainAdapter(FragmentManager fm) {
            super(fm);
            pagers[POSITION_MAIN] = new FileRecordPager();
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
