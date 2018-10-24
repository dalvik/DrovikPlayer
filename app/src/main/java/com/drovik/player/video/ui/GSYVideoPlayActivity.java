package com.drovik.player.video.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.library.utils.PreferenceUtils;
import com.baidu.mobstat.StatService;
import com.crixmod.sailorcast.model.SCLiveStream;
import com.crixmod.sailorcast.model.SCVideo;
import com.drovik.player.R;
import com.drovik.player.video.VideoBean;
import com.drovik.player.video.parser.IqiyiParser;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

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

    private StandardGSYVideoPlayer videoPlayer;

    private OrientationUtils orientationUtils;

    private IqiyiParser mIqiyiParser;
    private String TAG = "VideoPlayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatService.start(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sample_play);
        videoPlayer =  (StandardGSYVideoPlayer)findViewById(R.id.video_player);
        Log.d(TAG, "==> video play onCreate");
        PreferenceUtils.init(this);
        if(!initData()) {
            finish();
            return;
        }
        if(mVideo != null) {
            mIqiyiParser = new IqiyiParser();
            new ParseVideoSourceAysncTask().execute();
        }
        hideBottomUIMenu();
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

    private void init(String url) {
        String source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        source1 = url;
        videoPlayer.setUp(source1, true, mVideoName);

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.ic_launcher);
        videoPlayer.setThumbImageView(imageView);
        //增加title
        videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        videoPlayer.startPlayLogic();
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
        if(mRecorderWakeLock != null && mRecorderWakeLock.isHeld()){
            mRecorderWakeLock.release();
        }
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

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
                    Log.d(TAG,"==> play url2: " + url);
                    init(url.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                GSYVideoPlayActivity.this.finish();
            }
        }
    }
}
