package com.drovik.player.video.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;
import com.android.library.utils.PreferenceUtils;
import com.crixmod.sailorcast.model.SCLiveStream;
import com.crixmod.sailorcast.model.SCVideo;
import com.crixmod.sailorcast.utils.ImageTools;
import com.drovik.player.R;
import com.drovik.player.video.VideoBean;
import com.drovik.player.video.parser.IqiyiParser;
import com.iflytek.voiceads.IFLYVideoAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.conn.VideoDataRef;
import com.iflytek.voiceads.listener.IFLYVideoListener;

import org.yczbj.ycvideoplayerlib.constant.ConstantKeys;
import org.yczbj.ycvideoplayerlib.controller.VideoPlayerController;
import org.yczbj.ycvideoplayerlib.inter.listener.OnCompletedListener;
import org.yczbj.ycvideoplayerlib.inter.listener.OnVideoBackListener;
import org.yczbj.ycvideoplayerlib.manager.VideoPlayerManager;
import org.yczbj.ycvideoplayerlib.player.VideoPlayer;

import java.net.MalformedURLException;
import java.net.URL;

public class GSYVideoPlayActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SCMEDIA = "sc_media";
    public static final String SCSTREAM = "sc_stream";
    public static final String SCVIDEO = "sc_video";
    public final static String VIDEO = "video";
    private String mVideoPath;
    private String mVid;
    private String mTvid;
    private String mVideoName = "";
    private Uri mVideoUri;
    private VideoBean data;
    private View mVideoGuideLL;

    private SCVideo mVideo;
    private SCLiveStream mStream;
    private PowerManager.WakeLock mRecorderWakeLock;

    private VideoPlayer videoPlayer;

    private IqiyiParser mIqiyiParser;

    private VideoPlayerController controller;

    private boolean mHasPlayComplete;

    //add IFLY video ad
    private IFLYVideoAd videoAd;
    private RelativeLayout adContainer;
    private VideoDataRef videoADDataRef;
    private ViewGroup adView;
    private ImageView mAdCoverImageView;
    private boolean hasCached;
    private String TAG = "GSYVideoPlayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sample_play);
        mAdCoverImageView = (ImageView) findViewById(R.id.rewarded_video_ad_cover_view);
        adContainer = (RelativeLayout) findViewById(R.id.rewarded_video_ad_view);
        videoPlayer =  (VideoPlayer)findViewById(R.id.video_player);
        Log.d(TAG, "==> video play onCreate");
        PreferenceUtils.init(this);
        if(!initData()) {
            finish();
            return;
        }
        mHasPlayComplete = false;
        hideBottomUIMenu();
        requestNativeVideoAd();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mRecorderWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DrovikVideoPlay_"+ powerManager.toString());
        if(!mRecorderWakeLock.isHeld()){
            mRecorderWakeLock.acquire();
        }
    }

    /**
     * 初始化相关的信息
     */
    private boolean initData() {
        boolean result = false;
        Intent intent = getIntent();
        if(intent != null) {
            data = intent.getParcelableExtra(VIDEO);
            if(data != null) {
                mVideoPath =data.origpath;;
                String temp = mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1);
                mVideoName = temp.substring(0,temp.lastIndexOf("."));
                Log.d(TAG, "initPlayer path:" + mVideoPath + " mVideoName: " + mVideoName);
                result = true;
            } else {
                mVideo = intent.getParcelableExtra(SCVIDEO);
                if(mVideo != null)  {
                    mVid = intent.getStringExtra(SCMEDIA);//vid
                    mTvid = intent.getStringExtra(SCSTREAM);//tvid
                    mVideoName = mVideo.getVideoTitle();
                    /*String mStreamString = intent.getStringExtra(SCSTREAM);
                    if(mStreamString != null && !mStreamString.isEmpty()) {
                        mStream = SCLiveStream.fromJson(mStreamString);
                    }
                    if(mStream != null) {
                        mVideoName = mStream.getChannelName();
                    }*/
                    return true;
                } else {
                    String intentAction = intent.getAction();
                    if (!TextUtils.isEmpty(intentAction)) {
                        if (intentAction.equals(Intent.ACTION_VIEW)) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                mVideoUri = intent.getData();
                                Log.d(TAG, "==> mVideoUri: " + mVideoUri);
                                if(mVideoUri != null) {
                                    String tempPath = mVideoUri.getPath();
                                    String temp = tempPath.substring(tempPath.lastIndexOf("/") + 1);
                                    mVideoName = temp.substring(0,temp.lastIndexOf("."));
                                    return true;
                                } else{
                                    return false;
                                }
                            } else {
                                mVideoPath = intent.getDataString();
                                String temp = mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1);
                                mVideoName = temp.substring(0,temp.lastIndexOf("."));
                                result = true;
                            }
                        } else if (intentAction.equals(Intent.ACTION_SEND)) {
                            mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                            Log.d(TAG, "initPlayer mVideoUri:" + mVideoUri);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                String scheme = mVideoUri.getScheme();
                                if (!TextUtils.isEmpty(scheme)) {
                                    if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                                        mVideoPath = mVideoUri.getPath();
                                        String temp = mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1);
                                        mVideoName = temp.substring(0,temp.lastIndexOf("."));
                                        result = true;
                                    } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                                        Log.e(TAG, "Can not resolve content below Android-ICS\n");
                                        result = false;
                                    } else {
                                        Log.e(TAG, "Unknown scheme " + scheme + "\n");
                                        result = false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private void loadVideoSource() {
        adContainer.setVisibility(View.GONE);
        mAdCoverImageView.setVisibility(View.GONE);
        videoPlayer.setVisibility(View.VISIBLE);
        if(mVideo != null) {
            mIqiyiParser = new IqiyiParser();
            new ParseVideoSourceAysncTask().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(videoPlayer != null) {
            videoPlayer.pause();
        }
        if (videoAd != null) {
            videoAd.onResume();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoAd != null) {
            videoAd.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayerManager.instance().releaseVideoPlayer();
        if(mRecorderWakeLock != null && mRecorderWakeLock.isHeld()){
            mRecorderWakeLock.release();
        }
        if (videoAd != null) {
            videoAd.release();
            adView = null;
            videoAd = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK ){
            if(controller!=null && controller.getLock()){
                //如果锁屏，那就屏蔽返回键
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        /*if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }*/
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

    }

    private void setVideoPlayer(String urls) {
        if(videoPlayer==null || urls==null){
            return;
        }
        //LogUtils.e("视频链接"+urls);
        //设置播放类型
        videoPlayer.setPlayerType(ConstantKeys.IjkPlayerType.TYPE_IJK);
        //设置视频地址和请求头部
        videoPlayer.setUp(urls, null);
        //创建视频控制器
        controller = new VideoPlayerController(this);
        controller.setTitle(mVideoName);
        controller.setOnCompletedListener(new OnCompletedListener() {
            @Override
            public void onCompleted() {
                mHasPlayComplete = true;
                if(hasCached){
                    playAdVideo();
                } else {
                    GSYVideoPlayActivity.this.finish();
                }
            }
        });
        controller.setLoadingType(ConstantKeys.Loading.LOADING_QQ);
        controller.imageView().setBackgroundResource(R.color.blackText);
        controller.setOnVideoBackListener(new OnVideoBackListener() {
            @Override
            public void onBackClick() {
                GSYVideoPlayActivity.this.finish();
            }
        });
        //设置视频控制器
        videoPlayer.setController(controller);
        videoPlayer.enterFullScreen();
        videoPlayer.start();
    }
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void requestNativeVideoAd() {
        String adUnitId = "BF0BA46C856F38EC59C81A97F7B76F72";
        String coverBg = PreferenceUtils.getSharedPreferences().getString("cover_bg", "");
        if(TextUtils.isEmpty(coverBg)) {
            mAdCoverImageView.setImageResource(R.drawable.ad_video_cover);
        } else {
            ImageTools.displayImage(mAdCoverImageView, coverBg);
        }
        videoAd = new IFLYVideoAd(this, adUnitId, IFLYVideoAd.FULLSCREEN_VIDEO_AD, mVideoAdListener);
        videoAd.setParameter(AdKeys.APP_VER, StringUtils.getVersionName(this));
        videoAd.loadAd();
        handler.sendEmptyMessageDelayed(1, 4000);
    }

    private IFLYVideoListener mVideoAdListener = new IFLYVideoListener() {

        @Override
        public void onAdLoaded(VideoDataRef dateRef) {
            videoADDataRef = dateRef;
            StringBuilder stringBuilder = new StringBuilder();
            if (videoADDataRef.getDuration() > 0) {
                stringBuilder.append("duration:" + videoADDataRef.getDuration() + "\n");
            }
            if (videoADDataRef.getWidth() > 0) {
                stringBuilder.append("width:" + videoADDataRef.getWidth() + "\n");
            }
            if (videoADDataRef.getHeight() > 0) {
                stringBuilder.append("height:" + videoADDataRef.getHeight() + "\n");
            }
            if (!TextUtils.isEmpty(videoADDataRef.getAdSourceMark())) {
                stringBuilder.append("mark:" + videoADDataRef.getAdSourceMark() + "\n");
            }
            if (!TextUtils.isEmpty(videoADDataRef.getImgUrl())) {
                PreferenceUtils.getSharedPreferences().edit().putString("cover_bg", videoADDataRef.getImgUrl()).apply();
                ImageTools.displayImage(mAdCoverImageView, videoADDataRef.getImgUrl());
                stringBuilder.append("img:" + videoADDataRef.getImgUrl() + "\n");
                videoADDataRef.onExposure(mAdCoverImageView);
            }
            if (!TextUtils.isEmpty(videoADDataRef.getIconUrl())) {
                stringBuilder.append("icon:" + videoADDataRef.getIconUrl() + "\n");
            }
            if (!TextUtils.isEmpty(videoADDataRef.getTitle())) {
                stringBuilder.append("title:" + videoADDataRef.getTitle() + "\n");
            }
            if (!TextUtils.isEmpty(videoADDataRef.getDesc())) {
                stringBuilder.append("desc:" + videoADDataRef.getDesc() + "\n");
            }
            if (!TextUtils.isEmpty(videoADDataRef.getContent())) {
                stringBuilder.append("content:" + videoADDataRef.getContent() + "\n");
            }
            if (!TextUtils.isEmpty(videoADDataRef.getBrand())) {
                stringBuilder.append("brand:" + videoADDataRef.getBrand() + "\n");
            }
            if (!TextUtils.isEmpty(videoADDataRef.getCtatext())) {
                stringBuilder.append("ctatext:" + videoADDataRef.getCtatext() + "\n");
            }
            LogUtil.d(TAG, "==> onAdLoaded: " + stringBuilder.toString());
            Toast.makeText(GSYVideoPlayActivity.this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            if(videoAd != null) {
                hasCached = false;
                videoAd.cacheVideo();
            }
        }


        @Override
        public void onVideoCached() {
            LogUtil.d(TAG, "==> onVideoCached");
            Toast.makeText(GSYVideoPlayActivity.this, "onVideoCached", Toast.LENGTH_SHORT).show();
            if (videoAd != null) {
                hasCached = true;
                playAdVideo();
            }
        }

        @Override
        public void onAdFailed(AdError error) {
            LogUtil.d(TAG, "==> onAdFailed: " + error.getErrorCode() + " " + error.getErrorDescription());
            Toast.makeText(GSYVideoPlayActivity.this, "onAdFailed: " + error.getErrorCode(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdClick() {
            LogUtil.d(TAG, "==> onAdClick");
            videoADDataRef.onClick(adView);
        }

        @Override
        public void onVideoStart() {
            //缓冲完成 开始播放
            LogUtil.d(TAG, "==> onAdStartPlay, 倒计时没结束");
            //如果倒计时没结束，隐藏广告上的覆盖页
            videoADDataRef.onExposure(adView);
        }

        @Override
        public void onAdPlayError() {
            LogUtil.d(TAG, "==> onAdPlayError");
            //播放出错
            loadVideoSource();
        }

        @Override
        public void onVideoComplete() {
            LogUtil.d(TAG, "==> onAdPlayComplete");
            if(mHasPlayComplete) {
                GSYVideoPlayActivity.this.finish();
            } else {
                //结束播放
                loadVideoSource();
            }
        }

        @Override
        public void onVideoReplay() {
            LogUtil.d(TAG, "==> onVideoReplay");
        }

        @Override
        public void onConfirm() {
            LogUtil.d(TAG, "==> onConfirm");
        }

        @Override
        public void onCancel() {
            LogUtil.d(TAG, "==> onCancel");
        }

    };

    private class ParseVideoSourceAysncTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void[] objects) {
            String m3u8Path = mIqiyiParser.parseVideoSource(mVid, mTvid);
            return m3u8Path;
        }

        @Override
        protected void onPostExecute(String  path){
            super.onPostExecute(path);
            if(!TextUtils.isEmpty(path)) {
                mVideoPath = path;
                try {
                    URL url = new URL(Uri.encode(mVideoPath, "-![.:/,%?&=]"));
                    //Log.d(TAG,"==> play url2: " + url);
                    setVideoPlayer(url.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                GSYVideoPlayActivity.this.finish();
            }
        }
    }

    private void playAdVideo() {
        if(hasCached && videoAd != null) {
            handler.removeMessages(1);
            videoPlayer.setVisibility(View.GONE);
            mAdCoverImageView.setVisibility(View.GONE);
            adContainer.setVisibility(View.VISIBLE);
            adContainer.removeAllViews();
            adView = videoAd.getVideoView();
            adView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            adContainer.addView(adView);
            videoAd.showAd(IFLYVideoAd.LANDSCAPE);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    loadVideoSource();
                    break;
                default:
                    break;
            }
        }
    };
}
