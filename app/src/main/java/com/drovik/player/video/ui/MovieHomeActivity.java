package com.drovik.player.video.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.activity.BaseCompatActivity;
import com.drovik.player.R;
import com.drovik.player.video.ViewPagerAdapter;
import com.drovik.player.video.ui.pager.AlbumListFragment;
import com.drovik.player.video.ui.pager.IqiyiPager;
import com.drovik.player.video.ui.pager.LetvPager;
import com.drovik.player.video.ui.pager.SohuPager;
import com.drovik.player.video.ui.pager.TestPager;
import com.drovik.player.video.ui.pager.YoukuPager;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.conn.NativeDataRef;
import com.iflytek.voiceads.listener.IFLYNativeListener;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.SelectableViewAdapter;
import com.viewpagerindicator.TabPageIndicator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MovieHomeActivity extends BaseCompatActivity implements ViewPager.OnPageChangeListener, IFLYNativeListener {

    private static final int POSITION_MAIN = 0;
    private static final int POSITION_MSG = 1;
    private static final int POSITION_FIND = 2;
    public static final int POSITION_CENTER = 3;

    private static Fragment[] pagers = new Fragment[1];

    private ViewPager viewPager;
    private TabPageIndicator indicator;
    private MainAdapter adapter;
    //private HttpClient httpClient = new DefaultHttpClient();
    private int mTitle[] = {R.string.site_iqiyi};//R.string.site_youku, R.string.site_letv, R.string.site_sohu,
    private TextView titleTv;// 标题
    private TextView leftTv;
    private CheckedTextView rightTv;


    private final TotalHandler mHandler = new TotalHandler();
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private LinearLayout mLayoutDots;
    private List<String> mImageRes;
    private ImageView[][] mImageViews;
    private ImageView[] mDots;
    private final long delay = 10 * 1000;
    private final int AUTO = 0;
    private final int LOAD_AD = 1;
    private final int SHOW_AD = 2;
    private int mWidth;
    private int mNewWidth;
    private int mPadding;

    private IFLYNativeAd nativeAd;
    private NativeDataRef adItem;
    private View mAdContainer;
    private final String adUnitId = "5A2FC1A9477DD372965F2B9D3F122005";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_movie_viewpager);
        setActionBarVisiable(View.GONE);
        fullScreen(R.color.base_actionbar_background);
        findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieHomeActivity.this.finish();
            }
        });
        initView(mBodyContentView);
        viewPager = (ViewPager) findViewById(R.id.lib_id_viewpager_viewpager);
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setVisibility(View.GONE);
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
        mHandler.sendEmptyMessageDelayed(LOAD_AD, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(LOAD_AD);
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
        /*if (position == POSITION_MSG) {
            leftTv.setVisibility(View.VISIBLE);
            leftTv.setBackgroundResource(R.drawable.msg_friends);
            leftTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtil.gotoContactsActivity(activity);
                }
            });
            rightTv.setVisibility(View.GONE);*/
            /*rightTv.setBackgroundResource(R.drawable.msg_notify);
            rightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtil.gotoNotifyActivity(activity);
                }
            });*/
        /*} else {
            rightTv.setVisibility(View.GONE);
            rightTv.setOnClickListener(null);

            leftTv.setVisibility(View.GONE);
            leftTv.setOnClickListener(null);
            titleTv.setOnClickListener(null);
        }*/

        rightTv.setVisibility(View.GONE);
        rightTv.setOnClickListener(null);

        leftTv.setVisibility(View.GONE);
        leftTv.setOnClickListener(null);

        titleTv.setOnClickListener(null);
    }

    private void initTitle() {

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentDot(position % mImageRes.size());
        if (adItem != null && adItem.getImgList() != null && adItem.getImgList().size()>0) {
        }
    }

    @Override
    public void onAdLoaded(NativeDataRef dataRef) {
        adItem = dataRef;
        if(adItem != null) {
            //Toast.makeText(this, "onAdLoaded" + adItem.getImgList(), Toast.LENGTH_SHORT).show();
            mHandler.removeMessages(SHOW_AD);
            mHandler.sendEmptyMessageDelayed(SHOW_AD, 0);
            //LogUtil.d(TAG, "onAdLoaded: " + adItem.getImgList());
        }
    }

    @Override
    public void onAdFailed(AdError error) {
        mHandler.removeMessages(SHOW_AD);
        mHandler.sendEmptyMessageDelayed(SHOW_AD, 0);
        //Toast.makeText(this, "onAdFailed", Toast.LENGTH_SHORT).show();
        //LogUtil.d(TAG, "onAdFailed.");
    }

    @Override
    public void onConfirm() {
    }

    @Override
    public void onCancel() {
    }

    private void initView(View view) {
        mAdContainer = view.findViewById(R.id.ad_gallery);
        mWidth = getResources().getDisplayMetrics().widthPixels;
        mNewWidth = (int) (divideWidth(mWidth, 1080, 6) * 17);
        mPadding = (int) (divideWidth(mWidth, 1080, 6) * 9);
        mImageRes = new ArrayList<>();
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setOnPageChangeListener(this);
        mLayoutDots = (LinearLayout) view.findViewById(R.id.dotLayout);
        mAdContainer.setVisibility(View.GONE);
    }

    private void initViewPager() {
        mImageViews = new ImageView[2][];
        mImageViews[0] = new ImageView[mImageRes.size()];
        mImageViews[1] = new ImageView[mImageRes.size()];
        for (int i = 0; i < mImageViews.length; i++) {
            for (int j = 0; j < mImageViews[i].length; j++) {
                ImageView iv = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setLayoutParams(lp);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adItem.downloadApp();
                    }
                });
                Picasso.with(this).load(mImageRes.get(j)).into(iv);
                mImageViews[i][j] = iv;
            }
        }
        mViewPagerAdapter = new ViewPagerAdapter(mImageViews, mImageRes);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(mImageRes.size() * 50);
        mHandler.sendEmptyMessageDelayed(AUTO, delay);
    }

    private void initDots() {
        mDots = new ImageView[mImageRes.size()];
        for (int i = 0; i < mImageRes.size(); i++) {
            ImageView iv = new ImageView(this);
            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(mNewWidth, mNewWidth);
            lp.leftMargin = mPadding;
            lp.rightMargin = mPadding;
            lp.topMargin = mPadding;
            lp.bottomMargin = mPadding;
            iv.setLayoutParams(lp);
            iv.setImageResource(R.drawable.dot_normal);
            mLayoutDots.addView(iv);
            mDots[i] = iv;
        }
        mDots[0].setImageResource(R.drawable.dot_selected);
    }

    private double divideWidth(int screenWidth, int picWidth, int retainValue) {
        BigDecimal screenBD = new BigDecimal(Double.toString(screenWidth));
        BigDecimal picBD = new BigDecimal(Double.toString(picWidth));
        return screenBD.divide(picBD, retainValue, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void setCurrentDot(int currentPosition) {
        for (int i = 0; i < mDots.length; i++) {
            if (i == currentPosition) {
                mDots[i].setImageResource(R.drawable.dot_selected);
            } else {
                mDots[i].setImageResource(R.drawable.dot_normal);
            }
        }
    }

    private void loadAD() {
        nativeAd = new IFLYNativeAd(this, adUnitId, this);
        nativeAd.setParameter(AdKeys.APP_VER, StringUtils.getVersionName(this));
        nativeAd.loadAd();
    }

    private void showList() {
        if (adItem != null && adItem.getImgList() != null && adItem.getImgList().size()>0) {
            mImageRes.clear();
            mImageRes.addAll(adItem.getImgList());
            initDots();
            initViewPager();
            mAdContainer.setVisibility(View.VISIBLE);
            titleTv.setTextColor(getResources().getColor(R.color.white));
            adItem.onExposure(mAdContainer);
        } else {
            mAdContainer.setVisibility(View.GONE);
        }
    }

    class TotalHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AUTO:
                    int index = mViewPager.getCurrentItem();
                    mViewPager.setCurrentItem(index + 1);
                    mHandler.sendEmptyMessageDelayed(AUTO, delay);
                    break;
                case LOAD_AD:
                    loadAD();
                    break;
                case SHOW_AD:
                    showList();
                    break;
                    default:
                        break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 视频主页底部tab
     */
    private static class MainAdapter extends FragmentPagerAdapter implements SelectableViewAdapter, IconPagerAdapter {

        private int[] ICONS = new int[]{
                R.drawable.tab_main_selector,
                R.drawable.tab_msg_selector,
                R.drawable.tab_find_selector,
                R.drawable.tab_center_selector
        };

        public MainAdapter(FragmentManager fm) {
            super(fm);
            //pagers[POSITION_MAIN] = new YoukuPager();
            //pagers[POSITION_MSG] = new LetvPager();
            //pagers[POSITION_FIND] = new SohuPager();
            pagers[POSITION_MAIN] = new IqiyiPager();
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

    public static class TitleInfo{

        public int title;
        public int index;

        public TitleInfo(int title, int index) {
            this.title = title;
            this.index = index;
        }
    }
}
