package com.drovik.player.video.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.ui.activity.BaseCompatActivity;
import com.crixmod.sailorcast.SailorCast;
import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCAlbums;
import com.crixmod.sailorcast.model.SCChannel;
import com.crixmod.sailorcast.model.SCSite;
import com.crixmod.sailorcast.model.SCVideo;
import com.crixmod.sailorcast.siteapi.OnGetAlbumsListener;
import com.crixmod.sailorcast.siteapi.SiteApi;
import com.crixmod.sailorcast.uiutils.pagingridview.PagingGridView;
import com.crixmod.sailorcast.utils.ImageTools;
import com.crixmod.sailorcast.view.fragments.AlbumPlayGridFragment;
import com.drovik.player.R;
import com.drovik.player.video.Const;
import com.drovik.player.video.adapter.EpisodeListAdapter;
import com.drovik.player.video.parser.Episode;
import com.drovik.player.video.parser.EpisodeList;
import com.drovik.player.video.parser.IqiyiParser;
import com.drovik.player.video.ui.adapter.MovieListAdapter;
import com.drovik.utils.ToastUtils;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MovieDetailActivity extends BaseCompatActivity implements OnGetAlbumsListener.OnGetEpisodeListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int PAGE_SIZE = 60;
    private SCAlbum mAlbum;
    private int mChannelId;
    private int mIndex;
    private SCVideo mCurrentVideo;
    private int mVideoInAlbum; /* start from 0, item position */


    private int mInitialVideoNoInAlbum = 0;
    private boolean mIsShowTitle = false;
    private boolean mIsBackward = false;
    private boolean mIsFav;
    private AlbumPlayGridFragment mFragment;

    /****  resource  ***/
    private ImageView mAlbumImageView;//缩略图
    private TextView mTitle;//名称
    private TextView mDescribe;//描述
    private TextView mScore;//评分
    private TextView mDoctor;//演员表 [{"image_url":"http://pic2.iqiyipic.com/image/20190312/2c/88/p_5037611_m_601_m6.jpg","name":"沈腾","id":213640105},...]
    private TextView mSecondInfo;//主演
    private IqiyiParser mIqiyiParser;

    private SwipeRefreshLayout mSwipeContainer;
    private PagingGridView mEpisodeGridView;
    private EpisodeListAdapter mEpisodeListAdapter;

    private EpisodeList mEpisodeList;

    private int mPageNo;
    private BroadcastReceiver mRecv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        mPageNo = 0;
        if(intent != null){
            mAlbum = intent.getParcelableExtra(Const.ALUMB_DETAIL);
            mChannelId = intent.getIntExtra(Const.CHANNEL_ID, SCChannel.MOVIE);
            mIndex = intent.getIntExtra(Const.INDEX_ID, 0);
            mIqiyiParser = new IqiyiParser();
            if(mAlbum != null) {
                findViews();
                init();
                setTitle(mAlbum.getTitle());
                initRecv();
                if(mIndex == 1) {
                    findViewById(R.id.album_play_back).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.album_play_back).setVisibility(View.GONE);
                    loadMoreAlbums();
                }
            } else {
                MovieDetailActivity.this.finish();
            }
        } else {
            MovieDetailActivity.this.finish();
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            EpisodeList episodeList = (EpisodeList)msg.obj;
            if(mPageNo ==1 ) {
                mEpisodeListAdapter.setData(episodeList);
            } else {
                mEpisodeListAdapter.addData(episodeList);
            }
            mEpisodeGridView.setIsLoading(false);
            mSwipeContainer.setRefreshing(false);
            if(episodeList != null && episodeList.size()>0) {
                if(episodeList.size() == PAGE_SIZE) {
                    mEpisodeGridView.setHasMoreItems(true);
                } else {
                    mEpisodeGridView.setHasMoreItems(false);
                }
            } else {
                mDescribe.setMaxLines(10);
            }
            if(mEpisodeListAdapter.getCount()>0){
                mSwipeContainer.setVisibility(View.VISIBLE);
                findViewById(R.id.album_play_back).setVisibility(View.GONE);
            } else {
                mSwipeContainer.setVisibility(View.GONE);
                findViewById(R.id.album_play_back).setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRecv != null) {
            unregisterReceiver(mRecv);
            mRecv = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.album_play_back:
                onPlayButtonClick();
                break;
        }
    }

    @Override
    public void onRefresh() {
        mPageNo = 0;
        mEpisodeGridView.setHasMoreItems(true);
        loadMoreAlbums();
    }

    private void findViews() {
        mAlbumImageView = findViewById(R.id.album_image);
        mTitle = findViewById(R.id.album_title);
        mSecondInfo = findViewById(R.id.album_main_actor);
        mScore = findViewById(R.id.album_score);
        mDescribe = findViewById(R.id.album_desc);
        mSwipeContainer = findViewById(R.id.swipe_refresh_layout);
        mSwipeContainer.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) this);
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mEpisodeGridView = findViewById(R.id.frame_list_view_episode);
        mEpisodeGridView.setNumColumns(3);
        mEpisodeGridView.setHasMoreItems(true);
        mEpisodeGridView.setScrollableListener(new PagingGridView.Scrollable() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                boolean enable = false;
                if (mEpisodeGridView != null && mEpisodeGridView.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = mEpisodeGridView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = mEpisodeGridView.getChildAt(0).getTop() >= 0;
                    // enabling or disabling the refresh layout

                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                mSwipeContainer.setEnabled(enable);
            }
        });
        mEpisodeGridView.setPagingableListener(new PagingGridView.Pagingable() {

            @Override
            public void onLoadMoreItems() {
                //loadMoreAlbums();
            }
        });
        mEpisodeList = new EpisodeList();
        mEpisodeListAdapter = new EpisodeListAdapter(this, mEpisodeList, R.layout.item_video_episode_list);
        int width = getResources().getDisplayMetrics().widthPixels/3;
        mEpisodeListAdapter.setItemHeight(width);
        mEpisodeGridView.setAdapter(mEpisodeListAdapter);
        findViewById(R.id.album_play_back).setOnClickListener(this);
    }

    public void closeAlbumDesc(View view) {
        RelativeLayout albumDescContainer = (RelativeLayout) findViewById(R.id.album_desc_container);
        albumDescContainer.setVisibility(View.GONE);
    }

    @Override
    public void onGetEpisodeSuccess(EpisodeList episodes) {
        if(episodes != null) {
            episodes.debugLog();
            Message msg = mHandler.obtainMessage(0, episodes);
            mHandler.sendMessage(msg);
        } else {
            EpisodeList episodes2 = new EpisodeList();
            Message msg = mHandler.obtainMessage(0, episodes2);
            mHandler.sendMessage(msg);
            Log.w(TAG, "==> onGetEpisodeSuccess empty.");
        }
    }

    @Override
    public void onGetEpisodeFailed(String failReason) {
        EpisodeList episodes = new EpisodeList();
        Message msg = mHandler.obtainMessage(0, episodes);
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SailorCast.upnpServiceController.resume(this);
    }

	@Override
	public void onPause() {
		super.onPause();
        SailorCast.upnpServiceController.pause();
        SailorCast.upnpServiceController.getServiceListener().getServiceConnexion().onServiceDisconnected(null);
	}

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 该函数触发播放动作
     */
    public void onPlayButtonClick() {
        boolean isWifi = SailorCast.isNetworkWifi();
        if(isWifi) {
            openVideoPlayer();
        } else {
            final SweetAlertDialog pDialog =  new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            pDialog.setTitleText("是否播放");
            pDialog.setContentText("继续播放可能会耗费您的流量");
            pDialog.setConfirmText("继续播放");
            pDialog.setCancelable(true);
            pDialog.setCancelText("取消");
            pDialog.show();
            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.hide();
                    pDialog.hide();
                    openVideoPlayer();
                }
            });
        }
    }

	private void openVideoPlayer(){
        if(!TextUtils.isEmpty(mAlbum.getTVid()) && !TextUtils.isEmpty(mAlbum.getTVid()) || !TextUtils.isEmpty(mAlbum.getPlayUrl())){
            SCVideo video = new SCVideo();
            video.setVideoTitle(mAlbum.getTitle());
            Intent mpdIntent = new Intent(this, GSYVideoPlayActivity.class)
                    .putExtra(Const.SC_VIDEO, video)
                    .putExtra(Const.SC_VID, mAlbum.getVid())//vid
                    .putExtra(Const.SC_TVID, mAlbum.getTVid())//tvid
                    .putExtra(Const.SC_PLAY_URL, mAlbum.getPlayUrl());//play url
            startActivity(mpdIntent);
        } else {
            ToastUtils.showToast(this, R.string.movie_play_failed);
        }
    }

    private void init() {
        ImageTools.displayImage(mAlbumImageView, mAlbum.getVerImageUrl());
        if(!TextUtils.isEmpty(mAlbum.getTitle())){
            mTitle.setText(mAlbum.getTitle());
        } else {
            mTitle.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(mAlbum.getMainActor())){
            mSecondInfo.setText(mAlbum.getMainActor());
        } else {
            mSecondInfo.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(mAlbum.getScore())){
            mScore.setText(mAlbum.getScore());
        } else {
            mScore.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(mAlbum.getDesc())){
            mDescribe.setText(mAlbum.getDesc());
        } else {
            mDescribe.setVisibility(View.GONE);
        }
    }

    private void initRecv() {
        if(mRecv == null) {
            mRecv = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(Const.ACTION_REFRESH.equalsIgnoreCase(intent.getAction())){
                        ArrayList<SCAlbum> list = intent.getParcelableArrayListExtra(Const.EXTRA_REFRESH);
                        updateAlumbDetail(list);
                    }
                }
            };
            IntentFilter filter = new IntentFilter(Const.ACTION_REFRESH);
            registerReceiver(mRecv, filter);
        }
    }

    private void updateAlumbDetail(ArrayList<SCAlbum> list) {
        if(list != null && list.size()>0) {
            for(Episode episode: mEpisodeList) {
                for(SCAlbum album:list) {
                    if(!TextUtils.isEmpty(episode.getPlayUrl()) && episode.getPlayUrl().equals(album.getPlayUrl())){
                        episode.setTvId(album.getTVid());
                        episode.setVid(album.getAlbumId());
                        episode.setDescription(album.getDesc());
                    }
                }
            }
        }
    }

    public void loadMoreAlbums() {
        mPageNo ++ ;
        SiteApi.doGetEpisodes(SCSite.IQIYI,mChannelId, 1, PAGE_SIZE, mAlbum.getPlayUrl(), mAlbum.getAlbumId(), this);
    }
}
