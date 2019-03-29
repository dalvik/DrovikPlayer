package com.drovik.player.video.ui.pager;


import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCAlbums;
import com.crixmod.sailorcast.model.SCChannel;
import com.crixmod.sailorcast.model.SCChannelFilter;
import com.crixmod.sailorcast.model.SCFailLog;
import com.crixmod.sailorcast.model.SCSite;
import com.crixmod.sailorcast.siteapi.OnGetAlbumsListener;
import com.crixmod.sailorcast.siteapi.OnGetChannelFilterListener;
import com.crixmod.sailorcast.siteapi.SiteApi;
import com.crixmod.sailorcast.uiutils.pagingridview.PagingGridView;
import com.crixmod.sailorcast.view.AlbumFilterDialog;
import com.drovik.player.R;
import com.drovik.player.video.ViewPagerAdapter;
import com.drovik.player.video.ui.adapter.MovieListAdapter;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.conn.NativeDataRef;
import com.iflytek.voiceads.listener.IFLYNativeListener;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumListFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        OnGetAlbumsListener, OnGetChannelFilterListener, ViewPager.OnPageChangeListener, IFLYNativeListener {
    private static final String ARG_CHANNEL_ID = "channelID";
    private static final String ARG_SITE_ID = "siteID";

    private int mChannelID;
    private int mSiteID;
    private int mIndex;
    private PagingGridView mGridView;
    private TextView mEmptyView;
    private int mPageNo = 0;
    private int mPageSize = 30;
    private MovieListAdapter mAdapter;
    private int mColumns = 3;
    private SCChannelFilter mFilter;
    private boolean inFilterMode;
    private SwipeRefreshLayout mSwipeContainer;

    private final TotalHandler mHandler = new TotalHandler();
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private LinearLayout mLayoutDots;
    private List<String> mImageRes;
    private ImageView[][] mImageViews;
    private ImageView[] mDots;
    private final long delay = 10 * 1000;
    private final int AUTO = 0;
    private int mWidth;
    private int mNewWidth;
    private int mPadding;

    private IFLYNativeAd nativeAd;
    private NativeDataRef adItem;
    private View mAdContainer;

    private String TAG = "AlbumListFragment";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AlbumListFragment.
     */
    public static AlbumListFragment newInstance(int mSiteID, int mChannelID) {
        AlbumListFragment fragment = new AlbumListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHANNEL_ID, mChannelID);
        args.putInt(ARG_SITE_ID, mSiteID);
        fragment.setArguments(args);
        return fragment;
    }

    public AlbumListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPageNo = 0;
            mSiteID = getArguments().getInt(ARG_SITE_ID);
            mChannelID = getArguments().getInt(ARG_CHANNEL_ID);
            mIndex = getArguments().getInt(BaseMoviePager.EXTRA_CHANNEL_ID);
            loadMoreAlbums();
            mAdapter = new MovieListAdapter(getActivity(), new SCChannel(mChannelID));
            if(mSiteID == SCSite.LETV) {
                mColumns = 2;
                mAdapter.setColumns(mColumns);
            }

            if(mChannelID == SCChannel.MUSIC) {
                mColumns = 2;
                mAdapter.setColumns(mColumns);
            }
            SiteApi.doGetChannelFilter(mSiteID,mChannelID,this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_album_list, container, false);
        mSwipeContainer = (SwipeRefreshLayout) view;
        mSwipeContainer.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        initView(view);
        initDots();
        initViewPager();
        mGridView = (PagingGridView) view.findViewById(R.id.result_grid);
        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        mEmptyView.setText(getResources().getString(R.string.loading));

        mGridView.setEmptyView(mEmptyView);
        mGridView.setNumColumns(mColumns);
        mGridView.setAdapter(mAdapter);
        mGridView.setHasMoreItems(true);
        mGridView.setScrollableListener(new PagingGridView.Scrollable() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (mGridView != null && mGridView.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = mGridView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = mGridView.getChildAt(0).getTop() >= 0;
                    // enabling or disabling the refresh layout

                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                mSwipeContainer.setEnabled(enable);
            }
        });
        mGridView.setPagingableListener(new PagingGridView.Pagingable() {

            @Override
            public void onLoadMoreItems() {
                loadMoreAlbums();
            }
        });
        loadAD();
        return view;
    }


    public void loadMoreAlbums() {
        mPageNo ++ ;
        if(inFilterMode)
            SiteApi.doGetChannelAlbumsByFilter(mSiteID, mChannelID, mPageNo, mPageSize, mFilter, this);
        else
            SiteApi.doGetChannelAlbums(mSiteID, mChannelID, mPageNo, mIndex, "", "", this);
    }


    @Override
    public synchronized void onGetAlbumsSuccess(SCAlbums albums) {

        if (mColumns == 3) {
            if (albums.size() > 0) {
                if (albums.get(0).getVerImageUrl() == null) {
                    mColumns = 2;

                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mGridView.setNumColumns(mColumns);
                                mAdapter.setColumns(mColumns);
                            }
                        });
                    }
                }
            }
        }

        for(SCAlbum a : albums) {
            if(mAdapter != null)
                mAdapter.addAlbum(a);
        }

        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    if (mGridView != null)
                        mGridView.setIsLoading(false);
                    if (mSwipeContainer != null)
                        mSwipeContainer.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onGetAlbumsFailed(SCFailLog err) {

        try {
            if (getActivity() != null) {
                if(err.getType() == SCFailLog.TYPE_FATAL_ERR)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGridView != null) {
                            mGridView.setIsLoading(false);
                            mGridView.setHasMoreItems(false);
                            mEmptyView.setText(getResources().getString(R.string.album_no_videos));
                        }

                        if (mSwipeContainer != null)
                            mSwipeContainer.setRefreshing(false);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetChannelFilterSuccess(SCChannelFilter filter) {
        if(filter != null)
            mFilter = filter;
    }

    public void setChannelFilter(SCChannelFilter filter) {
        if(filter != null) {
            mFilter = filter;
            inFilterMode = true;
            onRefresh();
        }
    }


    @Override
    public void onGetChannelFilterFailed(SCFailLog err) {

        if(getActivity() != null) {
        }
    }

    public void showAlbumFilterDialog(FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        if(mFilter != null) {
            AlbumFilterDialog dialog = AlbumFilterDialog.newInstance(mSiteID, mChannelID, mFilter);
            dialog.show(fm, "");
        }
    }

    @Override
    public void onRefresh() {
        mAdapter = new MovieListAdapter(getActivity(), new SCChannel(mChannelID));
        mPageNo = 0;
        mGridView.setHasMoreItems(true);
        if(mSiteID == SCSite.LETV) {
            mColumns = 2;
            mAdapter.setColumns(mColumns);
        }

        if(mChannelID == SCChannel.MUSIC) {
            mColumns = 2;
            mAdapter.setColumns(mColumns);
        }
        mGridView.setAdapter(mAdapter);
        mEmptyView.setText(getResources().getString(R.string.loading));
        loadMoreAlbums();
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
            boolean isExposured = adItem.onExposure(mViewPager.getChildAt(position));
            LogUtil.d(TAG, "onPageSelected->" + isExposured);
        }
    }

    @Override
    public void onAdLoaded(NativeDataRef dataRef) {
        adItem = dataRef;
        Toast.makeText(getActivity(), "onAdLoaded", Toast.LENGTH_SHORT).show();
        if(adItem != null) {
            showList();
            LogUtil.d(TAG, "onAdLoaded: " + adItem.getImgList());
        }
    }

    @Override
    public void onAdFailed(AdError error) {
        showList();
        Toast.makeText(getActivity(), "onAdFailed", Toast.LENGTH_SHORT).show();
        LogUtil.d(TAG, "onAdFailed.");
    }

    @Override
    public void onConfirm() {
    }

    @Override
    public void onCancel() {
    }
    private void initView(View view) {
        mAdContainer = view.findViewById(R.id.container);
        mWidth = getResources().getDisplayMetrics().widthPixels;
        mNewWidth = (int) (divideWidth(mWidth, 1080, 6) * 17);
        mPadding = (int) (divideWidth(mWidth, 1080, 6) * 9);
        mImageRes = new ArrayList<>();
        mImageRes.add("http://www.51mtw.com/UploadFiles/2011-11/admin/2011111815413979496.jpg");
        mImageRes.add("http://www.51mtw.com/UploadFiles/2011-11/admin/2011111815413979496.jpg");
        mImageRes.add("http://www.51mtw.com/UploadFiles/2011-11/admin/2011111815413979496.jpg");
        mImageViews = new ImageView[2][];
        mDots = new ImageView[mImageRes.size()];
        mImageViews[0] = new ImageView[mImageRes.size()];
        mImageViews[1] = new ImageView[mImageRes.size()];
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setOnPageChangeListener(this);
        mLayoutDots = (LinearLayout) view.findViewById(R.id.dotLayout);
    }

    private void initViewPager() {
        mImageViews = new ImageView[2][];
        mImageViews[0] = new ImageView[mImageRes.size()];
        mImageViews[1] = new ImageView[mImageRes.size()];
        for (int i = 0; i < mImageViews.length; i++) {
            for (int j = 0; j < mImageViews[i].length; j++) {
                ImageView iv = new ImageView(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                iv.setLayoutParams(lp);
                Picasso.with(getActivity()).load(mImageRes.get(j)).into(iv);
                mImageViews[i][j] = iv;
            }
        }
        mViewPagerAdapter = new ViewPagerAdapter(mImageViews, mImageRes);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(mImageRes.size() * 50);
        mHandler.sendEmptyMessageDelayed(AUTO, delay);
    }

    private void initDots() {
        for (int i = 0; i < mImageRes.size(); i++) {
            ImageView iv = new ImageView(getActivity());
            LayoutParams lp = new LayoutParams(mNewWidth, mNewWidth);
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
        String adUnitId = "5A2FC1A9477DD372965F2B9D3F122005";
        nativeAd = new IFLYNativeAd(getActivity(), adUnitId, this);
        nativeAd.setParameter(AdKeys.APP_VER, StringUtils.getVersionName(getActivity()));
        nativeAd.loadAd();
    }

    private void showList() {
        if (adItem != null && adItem.getImgList() != null && adItem.getImgList().size()>0) {
        } else {
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
            }
            super.handleMessage(msg);
        }
    }
}
