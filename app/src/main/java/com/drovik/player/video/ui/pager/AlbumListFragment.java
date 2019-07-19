package com.drovik.player.video.ui.pager;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

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
import com.drovik.player.adv.AdvConst;
import com.drovik.player.video.ui.adapter.MovieListAdapter;
import com.kuaiyou.loader.AdViewNativeManager;
import com.kuaiyou.loader.loaderInterface.AdViewNativeListener;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumListFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        OnGetAlbumsListener, OnGetChannelFilterListener, AdViewNativeListener {
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


    private String TAG = AlbumListFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AlbumListFragment.
     */
    public static AlbumListFragment newInstance(int mSiteID, int mChannelID) {
        AlbumListFragment fragment = new AlbumListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHANNEL_ID, mChannelID);//TitleInfo index
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
            mChannelID = getArguments().getInt(ARG_CHANNEL_ID);//index
            mIndex = getArguments().getInt(BaseMoviePager.EXTRA_CHANNEL_ID);
            loadMoreAlbums();
            mAdapter = new MovieListAdapter(getActivity(), new SCChannel(mChannelID), mIndex);
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
        return view;
    }


    public void loadMoreAlbums() {
        mPageNo ++ ;
        SiteApi.doGetChannelAlbums(mSiteID, mChannelID, mPageNo, mIndex, "", "", this);
    }


    @Override
    public synchronized void onGetAlbumsSuccess(SCAlbums albums) {

        if(albums != null) {
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
        mAdapter = new MovieListAdapter(getActivity(), new SCChannel(mChannelID), mIndex);
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

    /***********************************************/
    private AdViewNativeManager adViewNative;
    private HashMap<String, Object> nativeAd;
    public static String HTML = "<meta charset='utf-8'><style type='text/css'>* { padding: 0px; margin: 0px;}a:link { text-decoration: none;}</style><div  style='width: 100%; height: 100%;'><img src=\"image_path\" width=\"100%\" height=\"100%\" ></div>";


    private void initAdView() {
        adViewNative = new AdViewNativeManager(getActivity(), AdvConst.ADVIEW_APPID, AdvConst.ADVIEW_NATIVE_VIDEO_ID, this);
        adViewNative.requestAd();
    }

    /**
     * 请求失败时调用该方法
     * @param arg0
     *            失败信息
     */
    @Override
    public void onNativeAdReceiveFailed(String arg0) {
        Log.i("AdViewBID", "onNativeAdReceiveFailed " + arg0);
    }

    /**
     * 当请求成功时调用该方法
     *
     * @param nativeAdList
     *            类型为List<HashMap<String,Object>>，返回得字段内容如下：
     *            title--> 广告标题
     *            adImage-->大图url
     *            adIcon--> Icon图片链接
     *            adFlagLogo--> 广告logo
     *            desc--> 广告描述文字
     *            sec_description--> 广告描述文字2
     * 请着重关注 title,adImage,adIcon,desc 这四个字段
     */
    @Override
    public void onNativeAdReceived(List nativeAdList) {
        Log.i("AdViewBID", "onNativeAdReceived");
        View view = null;
        if (null != nativeAdList && !nativeAdList.isEmpty()) {
            //LayoutInflater inflater = LayoutInflater.from(this);
            nativeAd = (HashMap) nativeAdList.get(0);
            if (null!=nativeAd.get("videoUrl")) {
                Log.i("原生视频信息：","videoUrl="+nativeAd.get("videoUrl")+"\ntitle="+nativeAd.get("title")+"\niconUrl="+nativeAd.get("adIcon")+"\ndesc="+nativeAd.get("description")+"\nendImgUrl="+nativeAd.get("endImgUrl"));
                /*videoUrl2 = (String) nativeAd.get("videoUrl");
                Uri uri = Uri.parse(videoUrl2);
                Toast.makeText(this, "视频广告获取成功", Toast.LENGTH_SHORT).show();
                contentView = inflater.inflate(R.layout.native_video, null);
                natVideoTtl = contentView.findViewById(R.id.natVideoTtl);
                iconWV = contentView.findViewById(R.id.natVideoIcon);
                descTV = contentView.findViewById(R.id.natVideoDesc);
                natVideoTtl.setText((CharSequence) nativeAd.get("title"));
                descTV.setText((CharSequence) nativeAd.get("description"));
                videoView = contentView.findViewById(R.id.videoView);
                //设置视频控制器
                videoView.setMediaController(new MediaController(this));
                videoView.setZOrderOnTop(true);
                //播放完成回调
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText( AdNativeActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
                    }
                });
                if (!TextUtils
                        .isEmpty((CharSequence) nativeAd.get("adIcon"))
                        && null != iconWV) {
                    iconWV.loadData((new String(HTML)).replace("image_path", (CharSequence) nativeAd.get("adIcon")),
                            "text/html; charset=UTF-8", null);
                } else {
                    iconWV.loadData((new String(HTML)).replace("image_path",
                            (CharSequence) "http://www.adview.com/ssp/image/loff6ty1_main_20181214144226632006_1280_720.jpg"),
                            "text/html; charset=UTF-8", null);
                }
                natAd.addView(contentView);
                //设置视频路径
                videoView.setVideoURI(uri);
                //开始播放视频
                videoView.start();
                Toast.makeText(this,"视频播放了",Toast.LENGTH_SHORT).show();*/
            } else if((view = (View) nativeAd.get("nativeView")) != null){
                /*Toast.makeText(this, "模板广告获取成功", Toast.LENGTH_SHORT).show();
                contentView = inflater.inflate(R.layout.native_mod, null);
                natAdMod=contentView.findViewById(R.id.natAdMod);
                natAdMod.addView(view);
                natAd.addView(contentView);
*/
            }else {
                Log.i("原生物料信息：","title="+nativeAd.get("title")+"\niconUrl="+nativeAd.get("adIcon")+"\ndescription="+nativeAd.get("description")+"\nimageUrl="+nativeAd.get("adImage"));
                //Toast.makeText(this, "物料广告获取成功", Toast.LENGTH_SHORT).show();
                //contentView = inflater.inflate(R.layout.item4, null);
                //WebView icon = contentView.findViewById(R.id.icon);
               // WebView image = contentView.findViewById(R.id.image);
                //TextView title = contentView.findViewById(R.id.title);
               // TextView desc = contentView.findViewById(R.id.desc);
                ///TextView desc2 = contentView.findViewById(R.id.desc2);
                //natAd.addView(contentView);
                /*if (null != nativeAd) {
                    String title = (String) nativeAd.get("title");
                    String iconUrl = (String) nativeAd.get("adIcon");
                    String description = (String) nativeAd.get("description");
                    String imageUrl = (String) nativeAd.get("adImage");


                    desc.setText((CharSequence) nativeAd.get("description"));
                    desc2.setText((CharSequence) nativeAd.get("sec_description"));
                    title.setText((CharSequence) nativeAd.get("title"));
                    if (!TextUtils.isEmpty((CharSequence) nativeAd.get("adImage")) && null != image) {
                        image.loadData((new String(HTML)).replace("image_path", (CharSequence) nativeAd.get("adImage")),
                                "text/html; charset=UTF-8", null);
                    }
//                    else {
//                        image.loadData((new String(HTML)).replace("image_path",
//                                (CharSequence) "http://www.adview.com/ssp/image/loff6ty1_main_20181214144226632006_1280_720.jpg"),
//                                "text/html; charset=UTF-8", null);
//                    }
                    if (!TextUtils.isEmpty((CharSequence) nativeAd.get("adIcon")) && null != icon)
                        //icon.loadData((new String(HTML)).replace("image_path", (CharSequence) nativeAd.get("adIcon")), "text/html; charset=UTF-8", null);
                }*/
            }
            // 汇报展示
            //adViewNative.reportImpression((String) nativeAd.get("adId"));
           // adViewNative.reportVideoStatus(this,(String) nativeAd.get("adId"), NATIVEVIDEO_START);//视频开始播放时上报
            //adViewNative.reportVideoStatus(this,(String) nativeAd.get("adId"), NATIVEVIDEO_MEDIUM);//视频播放1/2 时上报
            //adViewNative.reportVideoStatus(this,(String) nativeAd.get("adId"), NATIVEVIDEO_END);//视频播放完成时上报
            /***************************************************************/
            // 触发点击汇报
            /*if (null != natAd)
                natAd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != videoView) {
                            videoView.pause();
                        }
//						natAd.performClick();
                        adViewNative.reportClick((String) nativeAd.get("adId"));
                    }
                });*/
        }
    }

    @Override
    public void onDownloadStatusChange(int arg0) {

    }

    @Override
    public void onNativeAdClosed(View view) {

    }

}
