package com.drovik.player.video.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;
import com.android.library.utils.PreferenceUtils;
import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCAlbums;
import com.crixmod.sailorcast.model.SCLiveStream;
import com.crixmod.sailorcast.utils.ImageTools;
import com.drovik.player.R;
import com.drovik.player.adv.AdvConst;
import com.drovik.player.video.Const;
import com.drovik.player.video.parser.IqiyiParser;
import com.iflytek.voiceads.IFLYVideoAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.conn.VideoDataRef;
import com.iflytek.voiceads.listener.IFLYVideoListener;
import com.kuaiyou.loader.AdViewInstlManager;
import com.kuaiyou.loader.AdViewVideoManager;
import com.kuaiyou.loader.loaderInterface.AdViewInstlListener;
import com.kuaiyou.loader.loaderInterface.AdViewVideoListener;

import org.yczbj.ycvideoplayerlib.constant.ConstantKeys;
import org.yczbj.ycvideoplayerlib.controller.VideoPlayerController;
import org.yczbj.ycvideoplayerlib.inter.listener.OnCompletedListener;
import org.yczbj.ycvideoplayerlib.inter.listener.OnPlayOrPauseListener;
import org.yczbj.ycvideoplayerlib.inter.listener.OnVideoBackListener;
import org.yczbj.ycvideoplayerlib.manager.VideoPlayerManager;
import org.yczbj.ycvideoplayerlib.player.VideoPlayer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class GSYVideoPlayActivity extends AppCompatActivity implements AdViewInstlListener , AdViewVideoListener {
    private String mVideoPath;
    private String mVid;
    private String mTvid;
    private String mPlayUrl;
    private String mVideoName = "";
    private Uri mVideoUri;

    private boolean mIsSCVideo;

    private PowerManager.WakeLock mRecorderWakeLock;

    private VideoPlayer videoPlayer;

    private IqiyiParser mIqiyiParser;

    private VideoPlayerController controller;

    private boolean mHasPlayComplete;

    private Context mContext;
    private String TAG = "GSYVideoPlayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sample_play);
        videoPlayer =  findViewById(R.id.video_player);
        mIqiyiParser = new IqiyiParser();
        Log.d(TAG, "==> video play onCreate");
        PreferenceUtils.init(this);
        if(!initData()) {
            finish();
            return;
        } else {
            mHasPlayComplete = false;
            initAdView();
            loadVideoSource();
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mRecorderWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DrovikVideoPlay_"+ powerManager.toString());
            if(!mRecorderWakeLock.isHeld()){
                mRecorderWakeLock.acquire();
            }
        }
    }

    /**
     * 初始化相关的信息
     */
    private boolean initData() {
        boolean result = false;
        Intent intent = getIntent();
        if(intent != null) {
            if(intent.hasExtra(Const.SC_TVID) && intent.hasExtra(Const.SC_VID)) {
                mVid = intent.getStringExtra(Const.SC_VID);//vid
                mTvid = intent.getStringExtra(Const.SC_TVID);//tvid
                mVideoName = intent.getStringExtra(Const.SC_TITLE);
                mPlayUrl = intent.getStringExtra(Const.SC_PLAY_URL);
                mIsSCVideo = true;
                result = true;
            } else {
                String intentAction = intent.getAction();
                if (!TextUtils.isEmpty(intentAction)) {
                    if (intentAction.equals(Intent.ACTION_VIEW)) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mVideoUri = intent.getData();
                            mVideoPath = mVideoUri.toString();
                            Log.i(TAG, "==> mVideoUri: " + mVideoPath);
                            if(mVideoUri != null) {
                                String tempPath = mVideoUri.getPath();
                                String temp = tempPath.substring(tempPath.lastIndexOf("/") + 1);
                                //mVideoName = temp.substring(0,temp.lastIndexOf("."));
                                mVideoName = intent.getStringExtra(Const.SC_TITLE);
                                result = true;
                            }
                        } else {
                            mVideoPath = intent.getDataString();
                            String temp = mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1);
                            //mVideoName = temp.substring(0,temp.lastIndexOf("."));
                            mVideoName = intent.getStringExtra(Const.SC_TITLE);
                            Log.i(TAG, "==> mVideoUri: " + mVideoPath);
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
        return result;
    }

    private void loadVideoSource() {
        if(mIsSCVideo){
            if(TextUtils.isEmpty(mTvid) || TextUtils.isEmpty(mVid)){
                new ParseScciptHeaderAysncTask().execute();
            } else {
                new ParseVideoSourceAysncTask().execute();
            }
        } else {
            setVideoPlayer(mVideoPath);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(videoPlayer != null) {
            videoPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayerManager.instance().releaseVideoPlayer();
        if(mRecorderWakeLock != null && mRecorderWakeLock.isHeld()){
            mRecorderWakeLock.release();
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
        GSYVideoPlayActivity.this.finish();
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
        controller.setHideTime(10000);
        controller.setOnCompletedListener(new OnCompletedListener() {
            @Override
            public void onCompleted() {
                mHasPlayComplete = true;
                GSYVideoPlayActivity.this.finish();
            }
        });
        controller.setLoadingType(ConstantKeys.Loading.LOADING_QQ);
        controller.imageView().setBackgroundResource(R.color.blackText);
        controller.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(View.VISIBLE != visibility) {
                    hideBottomUIMenu();
                }
            }
        });
        controller.setOnVideoBackListener(new OnVideoBackListener() {
            @Override
            public void onBackClick() {
                GSYVideoPlayActivity.this.finish();
            }
        });
        controller.setOnPlayOrPauseListener(new OnPlayOrPauseListener() {
            @Override
            public void onPlayOrPauseClick(boolean isPlaying) {
                if(!isPlaying){
                    adInstlBIDView.closeInstl();
                } else {
                    if(mIdAdViewReady){
                        adInstlBIDView.showInstl(GSYVideoPlayActivity.this);
                    }
                }
            }
        });
        //设置视频控制器
        videoPlayer.setController(controller);
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

    private class ParseVideoSourceAysncTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void[] objects) {
            String m3u8Path = null;
            if(!TextUtils.isEmpty(mVid) && !TextUtils.isEmpty(mTvid)) {
                m3u8Path = mIqiyiParser.parseVideoSource(mVid, mTvid);
            }
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

    private class ParseScciptHeaderAysncTask extends AsyncTask<Void, Void, SCAlbums>{

        @Override
        protected SCAlbums doInBackground(Void[] objects) {
            SCAlbums alumbs = mIqiyiParser.getSCAlbum(mIqiyiParser.loadHtml(mPlayUrl));
            for(SCAlbum album:alumbs){
                int startIndexPlayUrl = mPlayUrl.indexOf("//");
                int startIndexAlumbUrl = album.getPlayUrl().indexOf("//");
                String playUrl = mPlayUrl.substring(startIndexPlayUrl + 2);
                String alumbUrl = album.getPlayUrl().substring(startIndexAlumbUrl + 2);
                System.out.println(playUrl + "=" + alumbUrl);
                if(playUrl.equals(alumbUrl)){
                    mVid = album.getAlbumId();
                    mTvid = album.getTVid();
                    break;
                }
            }
            return alumbs;
        }

        @Override
        protected void onPostExecute(SCAlbums  list){
            super.onPostExecute(list);
            Intent intent = new Intent(Const.ACTION_REFRESH);
            intent.putParcelableArrayListExtra(Const.EXTRA_REFRESH, list);
            sendBroadcast(intent);
            /*if(!TextUtils.isEmpty(mTvid) && !TextUtils.isEmpty(mVid)){
                new ParseVideoSourceAysncTask().execute();
            }*/
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


    protected void logInfo(String format, Object... args) {
        logMessage(Log.INFO, format, args);
    }

    protected void logError(String format, Object... args) {
        logMessage(Log.ERROR, format, args);
    }

    protected void logDebug(String format, Object... args) {
        logMessage(Log.DEBUG, format, args);
    }

    protected void showShortToast(String format, Object... args) {
        showToast(Toast.LENGTH_SHORT, format, args);
    }

    private void showToast(int duration, String format, Object... args) {
        Toast.makeText(this, String.format(format, args), duration).show();
    }

    private void logMessage(int level, String format, Object... args) {
        String formattedString = String.format(format, args);
        switch (level) {
            case Log.DEBUG:
                Log.d(TAG, formattedString);
                break;
            case Log.INFO:
                Log.i(TAG, formattedString);
                break;
            case Log.ERROR:
                Log.e(TAG, formattedString);
                break;
        }
    }

    /***************************************************/

    private AdViewInstlManager adInstlBIDView = null;
    private boolean mIdAdViewReady;
    private AdViewVideoManager videoManager = null;

    private void initAdView() {
        adInstlBIDView = new AdViewInstlManager(this, AdvConst.ADVIEW_APPID, true);//有关闭按钮：true，无关闭按钮：false
//		adInstlBIDView.setDisplayMode(AdViewInstlManager.DISPLAYMODE_DIALOG);
        adInstlBIDView.setOnAdViewListener(this);

        videoManager = new AdViewVideoManager(this, AdvConst.ADVIEW_APPID, AdvConst.ADVIEW_VIDEO_ID, this, false);
        // 设置屏幕方向，取值可参照ActivityInfo.SCREEN_XXXXXX 定义的常量
        videoManager.setVideoOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onAdClicked() {
        Log.i("AdViewBID", "onAdClicked");
    }

    @Override
    public void onAdClosed() {
        Log.i("AdViewBID", "onAdClosedAd");
    }

    @Override
    public void onAdReady() {
        Log.i("AdViewBID", "onAdReady");
        mIdAdViewReady = true;
        /**
         * 1.普通展示方式
         */
        //adInstlBIDView.showInstl(this);
//		/**
//		 * 2.自定义展示方式
//		 */
//		// 如果使用自定义插屏，如退屏广告 可使用 下面的方法， 获取广告view，自定义展示
//		if (adInstlBIDView.getDialogView() != null) {
//			new AlertDialog.Builder(AdInstlActivity.this).setView(adInstlBIDView.getDialogView()).setPositiveButton("去看看", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					adInstlBIDView.reportClick();
//					adInstlBIDView.closeInstl();
//				}
//			}).setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					adInstlBIDView.closeInstl();
//				}
//			}).show();
//			adInstlBIDView.reportImpression();
//		}

    }

    @Override
    public void onAdDisplayed() {
        Log.i("AdViewBID", "onDisplayed");
    }

    @Override
    public void onAdFailedReceived( String arg1) {
        Log.i("AdViewBID", "onAdRecieveFailed："+arg1);
    }

    @Override
    public void onAdReceived() {
        Log.i("AdViewBID", "onAdRecieved");
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /*************      adview video   callback  ***********/

    @Override
    public void onFailedReceivedVideo(String error) {
        Log.i(TAG, "onFailedRecievedVideo:"+error);
//		nextVideo.setEnabled(true);
    }

    @Override
    public void onVideoStartPlayed() {
        Log.i(TAG, "onVideoStartPlayed");
    }

    @Override
    public void onVideoFinished() {
        Log.i(TAG, "onVideoFinished");
//		nextVideo.setEnabled(true);
    }

    @Override
    public void onVideoClosed() {
        Log.i(TAG, "onVideoClosed");
//		nextVideo.setEnabled(true);
    }

    @Override
    public void onPlayedError(String arg0) {
        Log.i(TAG, "onPlayedError:"+arg0);
//		nextVideo.setEnabled(true);
    }

    @Override
    public void onReceivedVideo(String arg0) {
        Log.i(TAG, "onRecievedVideo:"+arg0);
    }

    @Override
    public void onVideoReady() {
        Log.i(TAG, "onVideoReady");
        videoManager.playVideo(this);
    }
}
