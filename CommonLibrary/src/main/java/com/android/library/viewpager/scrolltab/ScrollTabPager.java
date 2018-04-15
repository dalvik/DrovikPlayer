package com.android.library.viewpager.scrolltab;

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

import com.android.library.R;

public class ScrollTabPager extends Fragment {

    public static final String ARGUMENTS_NAME = "args";
    private RelativeLayout mScrollLayout;
    private SyncHorizontalScrollView mHsv;
    private RadioGroup mRadioContent;
    private ImageView mScrollTabIndicator;
    private ImageView mScrollTabLeft;
    private ImageView mScrollTabRight;
    private ViewPager mScrollTabViewContent;
    private int indicatorWidth;
    public static String[] mTabTitle = { "选项1", "选项2", "选项3", "选项4", "选项5" };
    public static FragmentInfo[] mPagers = null;
    private LayoutInflater mInflater;
    private TabFragmentPagerAdapter mAdapter;
    private int currentIndicatorLeft = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lib_layout_viewpager_scrolltab, null);
        findViewById(view);
        initView();
        setListener();
        return view;
    }

    private void findViewById(View view) {
        mScrollLayout = (RelativeLayout) view
                .findViewById(R.id.lib_id_viewpager_scroll_tab_item_layout);
        mHsv = (SyncHorizontalScrollView) view
                .findViewById(R.id.lib_id_viewpager_scroll_tab_view);
        mRadioContent = (RadioGroup) view
                .findViewById(R.id.lib_id_viewpager_scroll_tab_item_radio);
        mScrollTabIndicator = (ImageView) view
                .findViewById(R.id.lib_id_viewpager_scroll_tab_item_indicator);
        mScrollTabLeft = (ImageView) view
                .findViewById(R.id.lib_id_viewpager_scroll_tab_left);
        mScrollTabRight = (ImageView) view
                .findViewById(R.id.lib_id_viewpager_scroll_tab_right);
        mScrollTabViewContent = (ViewPager) view
                .findViewById(R.id.lib_id_viewpager_scroll_tab_content);
    }

    private void setListener() {
        mScrollTabViewContent
                .setOnPageChangeListener(new OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int position) {

                        if (mRadioContent != null
                                && mRadioContent.getChildCount() > position) {
                            ((RadioButton) mRadioContent.getChildAt(position))
                                    .performClick();
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

                    // 执行位移动画
                    mScrollTabIndicator.startAnimation(animation);
System.out.println("checkedId = " + checkedId);
                    mScrollTabViewContent.setCurrentItem(checkedId); 
                    // ViewPager跟随一起 切换,记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = ((RadioButton) mRadioContent
                            .getChildAt(checkedId)).getLeft();

                    mHsv.smoothScrollTo(
                            (checkedId > 1 ? ((RadioButton) mRadioContent
                                    .getChildAt(checkedId)).getLeft() : 0)
                                    - ((RadioButton) mRadioContent
                                            .getChildAt(2)).getLeft(), 0);
                }
            }
        });
    }

    private void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        indicatorWidth = dm.widthPixels / 4;
        LayoutParams cursor_Params = mScrollTabIndicator.getLayoutParams();
        cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
        mScrollTabIndicator.setLayoutParams(cursor_Params);
        mHsv.setSomeParam(mScrollLayout, mScrollTabLeft, mScrollTabRight,
                getActivity(), dm.widthPixels);
        mInflater = LayoutInflater.from(getActivity());
        initNavigationHSV();
        Fragment f = null;
        mAdapter = new TabFragmentPagerAdapter(getActivity(),
                getChildFragmentManager());
        mScrollTabViewContent.setAdapter(mAdapter);
    }

    private void initNavigationHSV() {
        mRadioContent.removeAllViews();
        for (int i = 0; i < mTabTitle.length; i++) {
            RadioButton rb = (RadioButton) mInflater.inflate(
                    R.layout.lib_layout_viewpager_radiogroup_item, null);
            rb.setId(i);
            rb.setText(mTabTitle[i]);
            rb.setLayoutParams(new LayoutParams(indicatorWidth,
                    LayoutParams.MATCH_PARENT));
            mRadioContent.addView(rb);
        }
    }
    
    private void initFragment(){
        mPagers = new FragmentInfo[mTabTitle.length];
        FragmentInfo info1 = new FragmentInfo(CommonUIFragment.class, null);
        FragmentInfo info2 = new FragmentInfo(LaunchUIFragment.class, null);
        mPagers[0] = info1;
        mPagers[1] = info2;
        mPagers[2] = info2;
        mPagers[3] = info2;
        mPagers[4] = info2;
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
            info.fragment = Fragment.instantiate(mContext,
                    info.clazz.getName(), info.bundle);
            info.fragment.setArguments(info.bundle);
            return info.fragment;
        }

        @Override
        public int getCount() {
            return mTabTitle.length;
        }
    }

}
