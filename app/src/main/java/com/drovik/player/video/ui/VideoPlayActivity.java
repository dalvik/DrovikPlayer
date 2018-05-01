package com.drovik.player.video.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.drovik.player.R;
import com.drovik.player.video.VideoBean;
import com.drovik.player.video.mediaplayer.SuperPlayer;
import com.android.library.utils.PreferenceUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class VideoPlayActivity extends AppCompatActivity implements SuperPlayer.OnNetChangeListener, View.OnClickListener {
    public final static String VIDEO = "video";
    private String mVideoPath;
    private String mVideoName = "";
    private Uri mVideoUri;
    private VideoBean data;
    private SuperPlayer player;
    private View mVideoGuideLL;

    private String TAG = "VideoPlayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatService.start(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_video);
        Log.d(TAG, "==> video play onCreate");
        PreferenceUtils.init(this);
        if(!initData()) {
            finish();
            return;
        }
        initPlayer();
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
        return result;
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        player = (SuperPlayer) findViewById(R.id.view_super_player);
        player.setNetChangeListener(true)//设置监听手机网络的变化
                .setOnNetChangeListener(this)//实现网络变化的回调
                .onPrepared(new SuperPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                         */
                    }
                }).onComplete(new Runnable() {
            @Override
            public void run() {
                VideoPlayActivity.this.finish();
                /**
                 * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                 */
            }
        }).onInfo(new SuperPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                /**
                 * 监听视频的相关信息。
                 */

            }
        }).onError(new SuperPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                /**
                 * 监听视频播放失败的回调
                 */

            }
        });
        player.setTitle(mVideoName);//设置视频的titleName
        if(mVideoPath != null){//开始播放视频
            try {
                URL url = new URL(Uri.encode(mVideoPath, "-![.:/,%?&=]"));
                Log.d(TAG,"==> play url2: " + url);
                player.play(url.toString());
                player.setScaleType(SuperPlayer.SCALETYPE_FITXY);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else if(mVideoUri != null){
            player.play(mVideoUri);
            player.setScaleType(SuperPlayer.SCALETYPE_FITXY);
        } else {
            finish();
        }
        mVideoGuideLL = findViewById(R.id.video_guide_ll);
        if(!PreferenceUtils.isInit()){
            mVideoGuideLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVideoGuideLL.setVisibility(View.GONE);
                }
            });
            mVideoGuideLL.setVisibility(View.VISIBLE);
            PreferenceUtils.setInit();
        }
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 网络链接监听类
     */
    @Override
    public void onWifi() {
        //ToDo
    }

    @Override
    public void onMobile() {
        //ToDo
    }

    @Override
    public void onDisConnect() {
        Toast.makeText(VideoPlayActivity.this, R.string.video_play_network_disconnect, Toast.LENGTH_SHORT).show();
        VideoPlayActivity.this.finish();
    }

    @Override
    public void onNoAvailable() {
        Toast.makeText(VideoPlayActivity.this, R.string.video_play_network_unavailable, Toast.LENGTH_SHORT).show();
        VideoPlayActivity.this.finish();
    }


    /**
     * 下面的这几个Activity的生命状态很重要
     */
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

}
