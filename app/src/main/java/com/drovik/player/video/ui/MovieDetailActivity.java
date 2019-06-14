package com.drovik.player.video.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.activity.BaseCompatActivity;
import com.crixmod.sailorcast.SailorCast;
import com.crixmod.sailorcast.database.BookmarkDbHelper;
import com.crixmod.sailorcast.database.HistoryDbHelper;
import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCAlbums;
import com.crixmod.sailorcast.model.SCFailLog;
import com.crixmod.sailorcast.model.SCVideo;
import com.crixmod.sailorcast.model.upnp.CallableRendererFilter;
import com.crixmod.sailorcast.model.upnp.IRendererCommand;
import com.crixmod.sailorcast.model.upnp.IUpnpDevice;
import com.crixmod.sailorcast.siteapi.OnGetAlbumDescListener;
import com.crixmod.sailorcast.siteapi.OnGetVideoPlayUrlListener;
import com.crixmod.sailorcast.siteapi.SiteApi;
import com.crixmod.sailorcast.uiutils.BaseToolbarActivity;
import com.crixmod.sailorcast.utils.ImageTools;
import com.crixmod.sailorcast.view.BaiduPlayerActivity;
import com.crixmod.sailorcast.view.RendererDialog;
import com.crixmod.sailorcast.view.fragments.AlbumPlayGridFragment;
import com.drovik.player.R;
import com.drovik.player.news.adpater.VideoArticleAdapter;
import com.drovik.player.video.adapter.EpisodeListAdapter;
import com.drovik.player.video.parser.EpisodeList;
import com.drovik.player.video.parser.IqiyiParser;
import com.drovik.player.video.ui.adapter.MovieListAdapter;

import java.util.Collection;
import java.util.concurrent.Callable;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MovieDetailActivity extends BaseCompatActivity implements OnGetAlbumDescListener, AlbumPlayGridFragment.OnAlbumPlayGridListener, OnGetVideoPlayUrlListener, View.OnClickListener {


    private SCAlbum mAlbum;
    private SCVideo mCurrentVideo;
    private int mVideoInAlbum; /* start from 0, item position */


    private int mInitialVideoNoInAlbum = 0;
    private boolean mIsShowTitle = false;
    private boolean mIsBackward = false;
    private BookmarkDbHelper mBookmarkDb;
    private HistoryDbHelper mHistoryDb;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mAlbum = getIntent().getParcelableExtra(MovieListAdapter.SC_ALBUM);
        mIqiyiParser = new IqiyiParser();
        if(mAlbum != null) {
            findViews();
            init();
            setTitle(mAlbum.getTitle());
            mBookmarkDb = new BookmarkDbHelper(this);
            mHistoryDb = new HistoryDbHelper(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    EpisodeList episodeList = mIqiyiParser.parseEpisodeList(mAlbum.getPlayUrl());
                    episodeList.debugLog();
                    Message msg = mHandler.obtainMessage(0, episodeList);
                    mHandler.sendMessage(msg);
                }
            }).start();
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
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void fillAlbumDescView(SCAlbum album) {
        ImageView albumImage = (ImageView) findViewById(R.id.album_image);
        TextView albumTitle = (TextView) findViewById(R.id.album_title);
        TextView albumDirector = (TextView) findViewById(R.id.album_director);
        TextView albumActor = (TextView) findViewById(R.id.album_main_actor);
        TextView albumDesc = (TextView) findViewById(R.id.album_desc);
        LinearLayout albumTopInfo = (LinearLayout) findViewById(R.id.album_topinfo_container);

        albumTitle.setText(album.getTitle());
        if(album.getDirector() != null && !album.getDirector().isEmpty()) {
            albumDirector.setText(getResources().getString(R.string.director) + album.getDirector());
            albumDirector.setVisibility(View.VISIBLE);
        }
        else
            albumDirector.setVisibility(View.GONE);

        if(album.getMainActor() != null && !album.getMainActor().isEmpty()) {
            albumActor.setText(getResources().getString(R.string.actor) + album.getMainActor());
            albumActor.setVisibility(View.VISIBLE);
        }
        else
            albumActor.setVisibility(View.GONE);

        if(album.getDesc() != null && !album.getDesc().isEmpty())
            albumDesc.setText(album.getDesc());

        albumImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAlbumDesc();
            }
        });

        if(album.getVerImageUrl() != null) {
            ImageTools.displayImage(albumImage, album.getVerImageUrl());
        } else if(album.getHorImageUrl() != null) {
            ImageTools.displayImage(albumImage,album.getHorImageUrl());
        }
    }

    public void closeAlbumDesc(View view) {
        RelativeLayout albumDescContainer = (RelativeLayout) findViewById(R.id.album_desc_container);
        albumDescContainer.setVisibility(View.GONE);
    }

    public void openAlbumDesc() {
        RelativeLayout albumDescContainer = (RelativeLayout) findViewById(R.id.album_desc_container);
        albumDescContainer.setVisibility(View.VISIBLE);
    }

    private void openErrorMsg(String msg) {
        TextView textView = (TextView) findViewById(R.id.error_message);
        textView.setText(msg);
        textView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onGetAlbumDescSuccess(final SCAlbum album) {
        mAlbum = album;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                fillAlbumDescView(album);
                invalidateOptionsMenu();
                try {
                    mFragment = AlbumPlayGridFragment.newInstance(mAlbum, mIsShowTitle, mIsBackward, mInitialVideoNoInAlbum);
                    mFragment.setShowTitle(mIsShowTitle);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, mFragment);
                    ft.commit();
                    getFragmentManager().executePendingTransactions();
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }

                if(mAlbum.getVideosTotal() == 1) {
                    openAlbumDesc();
                }

                if(mAlbum.getVideosTotal() == 0) {
                    openAlbumDesc();
                    openErrorMsg(getResources().getString(R.string.album_no_videos));
                }
            }
        });

    }

    @Override
    public void onGetAlbumDescFailed(SCFailLog err) {
    }

    @Override
    public void onAlbumPlayVideoSelected(SCVideo v, int positionInGrid) {
        mCurrentVideo = v;
        mVideoInAlbum = positionInGrid;
        if(mIsBackward == false)
            v.setSeqInAlbum(positionInGrid + 1);
        else
            v.setSeqInAlbum(mAlbum.getVideosTotal() - positionInGrid);
        Log.i("fire3","onAlbumPlayVideoSelected" + v.toJson());
        SiteApi.doGetVideoPlayUrl(v, this);

    }

    @Override
    public void onGetVideoPlayUrlNormal(final SCVideo v, final String urlNormal) {
    }

    @Override
    public void onGetVideoPlayUrlHigh(final SCVideo v, final String urlHigh) {
    }

    @Override
    public void onGetVideoPlayUrlSuper(final SCVideo v, final String urlSuper) {
    }

    @Override
    public void onGetVideoPlayUrlFailed(SCFailLog err) {
    }


    @Override
    protected void onResume() {
        super.onResume();
        SailorCast.upnpServiceController.resume(this);
    }

	@Override
	public void onPause()
	{
		super.onPause();
        SailorCast.upnpServiceController.pause();
        SailorCast.upnpServiceController.getServiceListener().getServiceConnexion().onServiceDisconnected(null);
	}

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void launchDrovikPlayer(SCVideo video, String url) {
        video = new SCVideo();
        video.setIqiyiVideoURL("https://gaqoqingv.jongta.com/2018/07/12/MeJX0X1kkEDXGYVk/playlist.m3u8");
        Intent mpdIntent = new Intent(MovieDetailActivity.this, VideoPlayActivity.class)
                .putExtra(VideoPlayActivity.SCVIDEO, video)
                .putExtra(VideoPlayActivity.SCMEDIA, "https://gaqoqingv.jongta.com/2018/07/12/MeJX0X1kkEDXGYVk/playlist.m3u8");
        startActivity(mpdIntent);
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

    /**
     * 该函数触发播放动作
     */
    public void onDlnaButtonClick(View button) {
        final String url = (String) button.getTag(R.id.key_video_url);
        final SCVideo v = (SCVideo) button.getTag(R.id.key_video);

		final Collection<IUpnpDevice> upnpDevices = SailorCast.upnpServiceController.getServiceListener()
				.getFilteredDeviceList(new CallableRendererFilter());
        if(upnpDevices.size() > 0) {

            FragmentManager fm = getSupportFragmentManager();
            RendererDialog dialog = new RendererDialog();
            dialog.setCallback(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    launchRenderer(v, url);
                    return null;
                }
            });
            dialog.show(fm, "Render");
        } else {
            Toast.makeText(this,getResources().getString(R.string.noRenderer),Toast.LENGTH_SHORT).show();
        }
    }

	private void launchRenderer(SCVideo video, String url) {
        mHistoryDb.addHistory(mAlbum,video,0);
		IRendererCommand rendererCommand = SailorCast.factory.createRendererCommand(SailorCast.factory.createRendererState());
		rendererCommand.launchSCVideo(video, url);
	}

	private void openVideoPlayer(){
        SCVideo video = new SCVideo();
        video.setVideoTitle(mAlbum.getTitle());
        Intent mpdIntent = new Intent(this, GSYVideoPlayActivity.class)
                .putExtra(VideoPlayActivity.SCVIDEO, video)
                .putExtra(VideoPlayActivity.SCSTREAM, mAlbum.getTVid())//tvid
                .putExtra(VideoPlayActivity.SCMEDIA, mAlbum.getAlbumId());//vid
        startActivity(mpdIntent);
    }

    private void init() {
        ImageTools.displayImage(mAlbumImageView, mAlbum.getVerImageUrl());
        mTitle.setText(mAlbum.getTitle());
        mDescribe.setText(mAlbum.getDesc());
        mSecondInfo.setText(mAlbum.getMainActor());
        mScore.setText(mAlbum.getScore());
    }
}
