package com.drovik.player.video.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.drovik.player.R;
import com.drovik.player.video.VideoBean;
import com.android.library.player.video.OnPlayerBackListener;
import com.android.library.player.video.OnShowThumbnailListener;
import com.android.library.player.video.PlayStateParams;
import com.android.library.player.video.PlayerView;
import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * ========================================
 * 描 述：点播全屏竖屏场景
 * ========================================
 */
public class PlayerActivity extends AppCompatActivity {

    public final static String VIDEO = "video";
    private String mVideoPath;
    private String mVideoName = "";
    private Uri mVideoUri;
    private VideoBean data;
    private View mVideoGuideLL;

    private PlayerView player;
    private Context mContext;
    private View rootView;
    private String TAG = "PlayerActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = getLayoutInflater().from(this).inflate(R.layout.simple_player_view_player, null);
        setContentView(rootView);
        if(initData()){
            if(mVideoPath != null) {//开始播放视频
                player = new PlayerView(this, rootView){
                    @Override
                    public PlayerView toggleProcessDurationOrientation() {
                        hideSteam(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? PlayStateParams.PROCESS_PORTRAIT : PlayStateParams.PROCESS_LANDSCAPE);
                    }
                }
                .setTitle(mVideoName)
                .setScaleType(PlayStateParams.fitparent)
                .setShowSpeed(true)
                .forbidTouch(false)
                .hideCenterPlayer(true)
                .setNetWorkTypeTie(true)
                .hideMenu(true)
                .setOnlyFullScreen(true)
                .showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                        Glide.with(mContext)
                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
                                .placeholder(R.drawable.ic_launcher_recorder)
                                .error(R.drawable.ic_launcher_recorder)
                                .into(ivThumbnail);
                    }
                })
                .setPlaySource(mVideoPath)
                .setPlayerBackListener(new OnPlayerBackListener() {
                    @Override
                    public void onPlayerBack() {
                        //这里可以简单播放器点击返回键
                        finish();
                    }
                })
                .startPlay();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean initData() {
        boolean result = false;
        Intent intent = getIntent();
        if(intent != null) {
            data = intent.getParcelableExtra(VIDEO);
            if(data != null) {
                mVideoPath =data.origpath;;
                String temp = mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1);
                mVideoName = temp.substring(0,temp.lastIndexOf("."));
                try {
                    URL url = new URL(Uri.encode(mVideoPath, "-![.:/,%?&=]"));
                    mVideoPath  =url.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "initPlayer path:" + mVideoPath + " mVideoName: " + mVideoName);
                result = true;
            } else {
                String intentAction = intent.getAction();
                if (!TextUtils.isEmpty(intentAction)) {
                    if (intentAction.equals(Intent.ACTION_VIEW)) {
                        mVideoPath = intent.getDataString();
                        String temp = mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1);
                        mVideoName = temp.substring(0,temp.lastIndexOf("."));
                        try {
                            URL url = new URL(Uri.encode(mVideoPath, "-![.:/,%?&=]"));
                            mVideoPath  = url.toString();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "==> mVideoPath: " + mVideoPath);
                        result = true;
                    } else if (intentAction.equals(Intent.ACTION_SEND)) {
                        mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                        Log.d(TAG, "==> initPlayer mVideoUri: " + mVideoUri);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            String scheme = mVideoUri.getScheme();
                            if (!TextUtils.isEmpty(scheme)) {
                                if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                                    mVideoPath = mVideoUri.getPath();
                                    String temp = mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1);
                                    mVideoName = temp.substring(0, temp.lastIndexOf("."));
                                    result = true;
                                } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                                    Log.e(TAG, "==> Can not resolve content below Android-ICS\n");
                                    result = false;
                                } else {
                                    Log.e(TAG, "==> Unknown scheme " + scheme + "\n");
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
}
