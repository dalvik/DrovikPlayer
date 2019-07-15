package com.drovik.player.video.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.ui.activity.BaseCompatActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.crixmod.sailorcast.SailorCast;
import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCChannel;
import com.crixmod.sailorcast.model.SCSite;
import com.crixmod.sailorcast.model.SCVideo;
import com.crixmod.sailorcast.siteapi.OnGetAlbumsListener;
import com.crixmod.sailorcast.siteapi.SiteApi;
import com.crixmod.sailorcast.utils.ImageTools;
import com.crixmod.sailorcast.view.fragments.AlbumPlayGridFragment;
import com.drovik.player.R;
import com.drovik.player.video.Const;
import com.drovik.player.video.adapter.EpisodeListAdapter;
import com.drovik.player.video.parser.Episode;
import com.drovik.player.video.parser.EpisodeList;
import com.drovik.player.video.parser.IqiyiParser;
import com.drovik.utils.ToastUtils;

import java.util.ArrayList;

//import cn.pedant.SweetAlert.SweetAlertDialog;

public class MovieDetailActivity extends BaseCompatActivity implements OnGetAlbumsListener.OnGetEpisodeListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final static int MSG_LOAD_MSG = 1000;
    private final static int PAGE_SIZE = 30;
    private final static int PAGE_START = 1;
    private int mPageIndex = PAGE_START;

    private SCAlbum mAlbum;
    private int mChannelId;
    private int mIndex;
    private SCVideo mCurrentVideo;
    private int mVideoInAlbum; /* start from 0, item position */


    private AlbumPlayGridFragment mFragment;

    /****  resource  ***/
    private ImageView mAlbumImageView;//缩略图
    private TextView mTitle;//名称
    private TextView mDescribe;//描述
    private TextView mDirctor;//主演
    private TextView mScore;//评分
    private TextView mDoctor;//演员表 [{"image_url":"http://pic2.iqiyipic.com/image/20190312/2c/88/p_5037611_m_601_m6.jpg","name":"沈腾","id":213640105},...]
    private TextView mSecondInfo;//主演
    private IqiyiParser mIqiyiParser;

    private SwipeRefreshLayout mSwipeContainer;
    private RecyclerView mEpisodeGridView;
    private EpisodeListAdapter mEpisodeListAdapter;

    private EpisodeList mEpisodeList;

    private BroadcastReceiver mRecv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        fullScreen(R.color.base_actionbar_background);
        com.android.library.utils.Utils.setStatusTextColor(true, this);
        setActionBarBackgroundColor(R.color.base_actionbar_background, R.color.base_actionbar_background);
        Intent intent = getIntent();
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
            switch (msg.what){
                case MSG_LOAD_MSG:
                    EpisodeList episodeList = (EpisodeList)msg.obj;
                    if(episodeList != null) {
                        if(mPageIndex == PAGE_START){
                            mEpisodeListAdapter.setNewData(episodeList);
                        } else {
                            mEpisodeListAdapter.addData(episodeList);
                        }
                        int dataSize = episodeList.size();
                        if(dataSize == 0) {
                            mSwipeContainer.setVisibility(View.GONE);
                            findViewById(R.id.album_play_back).setVisibility(View.VISIBLE);
                            mEpisodeListAdapter.setEnableLoadMore(false);
                        } else {
                            mSwipeContainer.setVisibility(View.VISIBLE);
                            findViewById(R.id.album_play_back).setVisibility(View.GONE);
                            if(dataSize % PAGE_SIZE == 0){
                                mEpisodeListAdapter.setEnableLoadMore(true);
                            } else {
                                mEpisodeListAdapter.setEnableLoadMore(false);
                            }
                        }
                    } else {
                        mEpisodeListAdapter.setEnableLoadMore(false);
                    }
                    Log.i(TAG, "==> pageIndex: " + mPageIndex);
                    mSwipeContainer.setRefreshing(false);
                    mEpisodeListAdapter.loadMoreComplete();
                    break;
                    default:
                        break;
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
        //这里的作用是防止下拉刷新的时候还可以上拉加载
        mEpisodeListAdapter.setEnableLoadMore(false);
        mPageIndex = PAGE_START;
        loadMoreAlbums();
    }

    private void findViews() {
        mAlbumImageView = findViewById(R.id.album_image);
        mTitle = findViewById(R.id.album_title);
        mDirctor = findViewById(R.id.album_director);
        mSecondInfo = findViewById(R.id.album_main_actor);
        mScore = findViewById(R.id.album_score);
        mDescribe = findViewById(R.id.album_desc);
        mSwipeContainer = findViewById(R.id.swipe_refresh_layout);
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mEpisodeGridView = findViewById(R.id.frame_list_view_episode);
        mEpisodeGridView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        mEpisodeList = new EpisodeList();
        mEpisodeListAdapter = new EpisodeListAdapter(this);
        mEpisodeListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPageIndex = mPageIndex + 1;
                // 请求数据
                loadMoreAlbums();
            }
        }, mEpisodeGridView);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //这里的作用是防止下拉刷新的时候还可以上拉加载
                mEpisodeListAdapter.setEnableLoadMore(false);
                mPageIndex = PAGE_START;
                // 刷新数据
                loadMoreAlbums();
            }
        });
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
            Message msg = mHandler.obtainMessage(MSG_LOAD_MSG, episodes);
            mHandler.sendMessage(msg);
        } else {
            EpisodeList episodes2 = new EpisodeList();
            Message msg = mHandler.obtainMessage(MSG_LOAD_MSG, episodes2);
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
        openVideoPlayer();
        if(isWifi) {
        } else {
            /*final SweetAlertDialog pDialog =  new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
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
            });*/
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
                    .putExtra(Const.SC_TITLE, mAlbum.getTitle())
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
        if(!TextUtils.isEmpty(mAlbum.getDirector())){
            mDirctor.setText(mAlbum.getDirector());
        } else {
            mDirctor.setVisibility(View.GONE);
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
        SiteApi.doGetEpisodes(SCSite.IQIYI,mChannelId, mPageIndex, PAGE_SIZE, mAlbum.getPlayUrl(), mAlbum.getAlbumId(), this);
    }
}
