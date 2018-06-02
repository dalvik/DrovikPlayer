package com.drovik.player.video.ui.pager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.ui.pager.FragmentInfo;
import com.android.audiorecorder.ui.pager.MainBluetoothRecordPager;
import com.android.library.ui.pager.BasePager;
import com.android.library.viewpager.scrolltab.SyncHorizontalScrollView;
import com.drovik.player.R;

public class TestPager extends BasePager {

    public static final String ARGUMENTS_NAME = "args";
    private RelativeLayout mScrollLayout;
    private SyncHorizontalScrollView mHsv;
    private RadioGroup mRadioContent;
    private ImageView mScrollTabIndicator;
    private ImageView mScrollTabLeft;
    private ImageView mScrollTabRight;
    private ViewPager mScrollTabViewContent;
    private int indicatorWidth;
    public static int[] mTabTitle = {R.string.channel_show, R.string.channel_movie,R.string.channel_comic, R.string.channel_documentary,R.string.channel_variety };
    public static FragmentInfo[] mPagers = null;
    private LayoutInflater mInflater;
    private TabFragmentPagerAdapter mAdapter;
    private int currentIndicatorLeft = 0;
    private int mSelectIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_activity_viewpager_scrolltab, null);
        findViewById(view);
        initView();
        setListener();
        return view;
    }
    @Override
    public void onResume() {
    	super.onResume();
    	RadioButton br = (RadioButton)mRadioContent.getChildAt(0);
    	br.setChecked(true);
    }

    private void findViewById(View view) {
        mScrollLayout = (RelativeLayout) view.findViewById(R.id.lib_id_viewpager_scroll_tab_item_layout);
        mHsv = (SyncHorizontalScrollView) view.findViewById(R.id.lib_id_viewpager_scroll_tab_view);
        mRadioContent = (RadioGroup) view.findViewById(R.id.lib_id_viewpager_scroll_tab_item_radio);
        mScrollTabIndicator = (ImageView) view.findViewById(R.id.lib_id_viewpager_scroll_tab_item_indicator);
        mScrollTabLeft = (ImageView) view.findViewById(R.id.lib_id_viewpager_scroll_tab_left);
        mScrollTabRight = (ImageView) view.findViewById(R.id.lib_id_viewpager_scroll_tab_right);
        mScrollTabViewContent = (ViewPager) view.findViewById(R.id.lib_id_viewpager_scroll_tab_content);
    }

    private void setListener() {
        mScrollTabViewContent.setOnPageChangeListener(new OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int position) {

                        if (mRadioContent != null && mRadioContent.getChildCount() > position) {
                            ((RadioButton) mRadioContent.getChildAt(position)).performClick();
                            mSelectIndex = position;
                        }
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {

                    }
                });

        mRadioContent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (mRadioContent.getChildAt(checkedId) != null) {

                    TranslateAnimation animation = new TranslateAnimation(
                            currentIndicatorLeft, ((RadioButton) mRadioContent
                                    .getChildAt(checkedId)).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);

                    mScrollTabIndicator.startAnimation(animation);
                    mScrollTabViewContent.setCurrentItem(checkedId); 
                    currentIndicatorLeft = ((RadioButton) mRadioContent.getChildAt(checkedId)).getLeft();

                    mHsv.smoothScrollTo((checkedId > 1 ? ((RadioButton) mRadioContent.getChildAt(checkedId)).getLeft() : 0)
                            - ((RadioButton) mRadioContent.getChildAt(1)).getLeft(), 0);
                }
            }
        });
    }

    private void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        indicatorWidth = dm.widthPixels / 4;
        LayoutParams cursor_Params = mScrollTabIndicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;//
        mScrollTabIndicator.setLayoutParams(cursor_Params);
        mHsv.setSomeParam(mScrollLayout, mScrollTabLeft, mScrollTabRight, getActivity(), dm.widthPixels);
        mInflater = LayoutInflater.from(getActivity());
        initNavigationHSV();
        mAdapter = new TabFragmentPagerAdapter(getActivity(), getChildFragmentManager());
        mScrollTabViewContent.setAdapter(mAdapter);
        RadioButton defaultRadio = (RadioButton)mRadioContent.getChildAt(0);
        defaultRadio.setChecked(true);
    }

    private void initNavigationHSV() {
        mRadioContent.removeAllViews();
        for (int i = 0; i < mTabTitle.length; i++) {
            RadioButton rb = (RadioButton) mInflater.inflate(R.layout.lib_layout_viewpager_radiogroup_item, null);
            rb.setId(i);
            rb.setText(getString(mTabTitle[i]));
            rb.setLayoutParams(new LayoutParams(indicatorWidth, LayoutParams.MATCH_PARENT));
            mRadioContent.addView(rb);
        }
    }
    
    private void initFragment(){
        mPagers = new FragmentInfo[mTabTitle.length];
        Bundle bundle1 = new Bundle();
        bundle1.putInt("mode", MultiMediaService.LUNCH_MODE_MANLY);
        FragmentInfo info1 = new FragmentInfo(MainBluetoothRecordPager.class, bundle1);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("mode", MultiMediaService.LUNCH_MODE_CALL);
        FragmentInfo info2 = new FragmentInfo(MainBluetoothRecordPager.class, bundle2);
        mPagers[0] = info1;
        mPagers[1] = info2;

        mPagers[2] = info2;
        mPagers[3] = info2;
        mPagers[4] = info2;
        mSelectIndex = 0;
    }

    public static class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        
        public TabFragmentPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.mContext = context;
        }

        @Override
        public Fragment getItem(int index) {
            if(mPagers==null || index>=mPagers.length){
                throw new IllegalStateException("Not Set Framgent.");
            }
            FragmentInfo info = mPagers[index];
            info.fragment = Fragment.instantiate(mContext, info.clazz.getName(), info.bundle);
            info.fragment.setArguments(info.bundle);
            return info.fragment;
        }

        @Override
        public int getCount() {
            return mTabTitle.length;
        }
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void reload() {
    }

}
