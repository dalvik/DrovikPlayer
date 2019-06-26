package com.drovik.player.video.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MovieDetailActivity extends BaseCompatActivity implements OnGetAlbumsListener.OnGetEpisodeListener, View.OnClickListener {

    private SCAlbum mAlbum;
    private int mChannelId;
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

    private GridView mEpisodeGridView;
    private EpisodeListAdapter mEpisodeListAdapter;

    private EpisodeList mEpisodeList;

    private BroadcastReceiver mRecv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        if(intent != null){
            mAlbum = intent.getParcelableExtra(Const.ALUMB_DETAIL);
            mChannelId = intent.getIntExtra(Const.CHANNEL_ID, SCChannel.MOVIE);
            mIqiyiParser = new IqiyiParser();
            if(mAlbum != null) {
                findViews();
                init();
                setTitle(mAlbum.getTitle());
                initRecv();
                SiteApi.doGetEpisodes(SCSite.IQIYI,mChannelId, mAlbum.getPlayUrl(),this);
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
            mEpisodeListAdapter.setData(episodeList);
            if(episodeList != null && episodeList.size()>0) {
                findViewById(R.id.album_play_back).setVisibility(View.GONE);
                mEpisodeGridView.setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.album_play_back).setVisibility(View.VISIBLE);
                mDescribe.setMaxLines(10);
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

    private void findViews() {
        mAlbumImageView = findViewById(R.id.album_image);
        mTitle = findViewById(R.id.album_title);
        mSecondInfo = findViewById(R.id.album_main_actor);
        mScore = findViewById(R.id.album_score);
        mDescribe = findViewById(R.id.album_desc);

        mEpisodeGridView = findViewById(R.id.frame_list_view_episode);
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
                    .putExtra(Const.SC_VID, mAlbum.getAlbumId())//vid
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
}
