package com.drovik.player.audio.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.drovik.player.AppApplication;
import com.drovik.player.R;
import com.drovik.player.audio.MusicBean;
import com.drovik.player.audio.mediaplayer.IMusicPlayView;
import com.drovik.player.audio.mediaplayer.MusicPlayController;
import com.drovik.player.audio.mediaplayer.Playlist;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.dialog.CustomDialog;
import com.android.library.utils.NumberUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MusicPlayActivity extends BaseCommonActivity implements IMusicPlayView {
    private static final String TAG = "MusicPlayActivity";
    private TextView mMusicName;
    private TextView mSingerAndAlbum;
    private ImageView mPlayOrPause;
    private ImageView mPlayPre;
    private ImageView mPlayNext;
    private MusicPlayController mMusicPlayController;
    private ImageView mMusicPic;
//    private ImageView mPlayVoice;
    private ImageView mPlayMode;
    private TextView mPlayTime;
    private TextView mTotalTime;
    private SeekBar mPalySeekBar;
    private ImageView mDiscBg;
    private RelativeLayout mainBg;

    private CustomDialog m4GPlayConfirm;
    ConnectivityManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatService.start(this);
        setContentView(R.layout.activity_music_play);
        mMusicPlayController = new MusicPlayController(MusicPlayActivity.this, MusicPlayActivity.this);
        initView();
        initData();
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAvatarAnimation(0f);
        mMusicPlayController.showPlayMusic();
//        LogUtil.d(MusicPlayActivity.class, "mMusicPlayController.getRemotePlayerEngine().getCurrentMediaplayer().duration " + mMusicPlayController.getRemotePlayerEngine().getDuration() );
        showTotalTime((int) (mMusicPlayController.getRemotePlayerEngine().getDuration() /1000));
        boolean isPlaying = mMusicPlayController.getRemotePlayerEngine().getCurrentMediaplayer()== null?false:mMusicPlayController.getRemotePlayerEngine().getCurrentMediaplayer().isPlaying();
        showPlayStatus(isPlaying);
        IntentFilter netChangedFilter = new IntentFilter();
        netChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetChanged, netChangedFilter);
    }


    private void initData() {
    }


    private void initView() {
        mainBg = (RelativeLayout) findViewById(R.id.activity_music_play_main);
        findViewById(R.id.activity_music_play_back).setOnClickListener(mMusicPlayController);
        mMusicName = (TextView) findViewById(R.id.activity_music_paly_name);
        mSingerAndAlbum = (TextView) findViewById(R.id.activity_music_play_singer_album);
        mPlayOrPause = (ImageView) findViewById(R.id.activity_music_play);
        mPlayPre = (ImageView) findViewById(R.id.activity_music_play_pre);
        mPlayNext = (ImageView) findViewById(R.id.activity_music_play_next);

        mPlayOrPause.setOnClickListener(mMusicPlayController);
        mPlayPre.setOnClickListener(mMusicPlayController);
        mPlayNext.setOnClickListener(mMusicPlayController);

        mPlayMode = (ImageView) findViewById(R.id.activity_music_play_playmode);
        mPlayMode.setOnClickListener(mMusicPlayController);
        //TODO
        //setPlaybackMode(Playlist.PlaylistPlaybackMode.getPlaybackModeByOrdinal(PrefUtils.getPlaybackMode()));
//        mPlayVoice = (ImageView) findViewById(R.id.activity_music_play_playvoice);
//        mPlayVoice.setOnClickListener(mMusicPlayController);

        mMusicPic = (ImageView) findViewById(R.id.music_body_disc_origin);

        mPlayTime = (TextView) findViewById(R.id.activity_music_play_playtime);
        mTotalTime = (TextView) findViewById(R.id.activity_music_play_totoaltime);
        mPalySeekBar = (SeekBar) findViewById(R.id.activity_music_play_progress);

        ProgressChange progressChange = new ProgressChange();
        progressChange.setListener(new SeekbarStopTrackingListener() {
            @Override
            public void SeekbarStopTracking(SeekBar seekBar) {
                if (mMusicPlayController.getRemotePlayerEngine() != null) {
                    mMusicPlayController.getRemotePlayerEngine().seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void SeekbarProgressChanged(int progress) {
                if (mMusicPlayController.getRemotePlayerEngine() != null) {
                    //mPlayTime.setText(UIUtility.timeIntMToMM(progress / 1000));
//                    mMusicPlayController.getRemotePlayerEngine().seekTo(progress);
                }
            }
        });

        mPalySeekBar.setOnSeekBarChangeListener(progressChange);
        mDiscBg = (ImageView) findViewById(R.id.music_body_disc_disc);
    }

    /**
     * Seekbar 手动控制
     */

    private class ProgressChange implements SeekBar.OnSeekBarChangeListener {
        private SeekbarStopTrackingListener listener;
        private boolean fromUser;

        public ProgressChange() {

        }

        public void setListener(SeekbarStopTrackingListener listener) {
            this.listener = listener;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.fromUser = fromUser;
            if (this.fromUser && listener != null) {
                listener.SeekbarProgressChanged(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (this.fromUser && listener != null) {
                listener.SeekbarStopTracking(seekBar);
            }
        }
    }


    @Override
    public void showMusicInfo(MusicBean musicBean) {
        if (!AppApplication.getInstance().getPlayerEngineInterface().isPlaying() && !AppApplication.getInstance().getPlayerEngineInterface().isPrepared()) {
            if (!isFinishing()) {
               // showWaitingDialog();
            }
        }
        if (musicBean != null) {
            LogUtil.d(TAG, "showMusicInfo is enter musicBean:" + musicBean.origpath + " isPlaying : " + AppApplication.getInstance().getPlayerEngineInterface().isPlaying());
            //Glide.with(getApplicationContext()).load("http://" + LoginManager.getInstance().baseIp+ ":" + LoginManager.getInstance().getP2PPort()  + "/" + musicBean.thumbpath).asBitmap().centerCrop().placeholder(R.drawable.music_body_initialize_n).diskCacheStrategy(DiskCacheStrategy.ALL).into(mMusicPic);
            if (musicBean.singer.isEmpty()) {
                musicBean.singer = getApplicationContext().getResources().getString(R.string.unknow);
            }
            if (musicBean.title.isEmpty()) {
                musicBean.title = getApplicationContext().getResources().getString(R.string.unknow);
            }
            if (musicBean.album.isEmpty()) {
                musicBean.album = getApplicationContext().getResources().getString(R.string.unknow);
            }
            mSingerAndAlbum.setText(getApplicationContext().getString(R.string.singer_album, musicBean.singer, musicBean.album));
            mMusicName.setText(musicBean.getTitle());
            if (mMusicPlayController.getRemotePlayerEngine().getCurrentMediaplayer() != null && mMusicPlayController.getRemotePlayerEngine().isPrepared()) {
                mTotalTime.setText(NumberUtil.timeIntMToMM(mMusicPlayController.getRemotePlayerEngine().getCurrentMediaplayer().getDuration() / 1000));
                mPalySeekBar.setMax(mMusicPlayController.getRemotePlayerEngine().getCurrentMediaplayer().getDuration());
            }
            if (mMusicPlayController.getRemotePlayerEngine().isPlaying()) {
                mPlayOrPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_pause_n));
                playAnimation();
            } else {
                mPlayOrPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_play_n));
                pauseAnimation();
            }

            SimpleTarget target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (Build.VERSION.SDK_INT >= 16) {
                        mainBg.setBackground(new BitmapDrawable(bitmap));
                    } else {
                        mainBg.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    }

                }
            };
            if (musicBean.thumbpath == null || musicBean.thumbpath.trim().isEmpty()) {
                Glide.with(getApplicationContext()).load(R.drawable.music_body_initialize_n).asBitmap().transform(new BlurTransformation(MusicPlayActivity.this, 20, 5)).into(target);
            } else {
                //TODO
                //Glide.with(getApplicationContext()).load("http://" + LoginManager.getInstance().baseIp + ":" + LoginManager.getInstance().getP2PPort() + "/" + musicBean.thumbpath).asBitmap().transform(new BlurTransformation(MusicPlayActivity.this, 20, 5)).into(target);
            }
        }
    }

    @Override
    public void showPlayStatus(boolean palying) {
        pauseAnimation();
        if (palying) {
            mPlayOrPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_pause_n));
            playAnimation();
        } else {
            mPlayOrPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_play_n));
            pauseAnimation();
        }
    }

    @Override
    public void playOrPause() {
        if (!AppApplication.getInstance().getPlayerEngineInterface().isPlaying()) {
            showPlayConfirm(new Runnable() {
                @Override
                public void run() {
                    if (AppApplication.getInstance().getPlayerEngineInterface().isPlaying()) {
                        AppApplication.getInstance().getPlayerEngineInterface().pause();
                        showPlayStatus(false);
                    } else {
                        AppApplication.getInstance().getPlayerEngineInterface().play();
                        showPlayStatus(true);
                    }
                    if (isIn4G()) {
                        AppApplication.isAllowIn4G = true;
                    }
                }
            });
        } else {
            if (AppApplication.getInstance().getPlayerEngineInterface().isPlaying()) {
                AppApplication.getInstance().getPlayerEngineInterface().pause();
                showPlayStatus(false);
            } else {
                AppApplication.getInstance().getPlayerEngineInterface().play();
                showPlayStatus(true);
            }
        }

    }

    @Override
    public void playPre() {
        showTotalTime((int) (mMusicPlayController.getRemotePlayerEngine().getDuration() /1000));
        showPlayConfirm(new Runnable() {
            @Override
            public void run() {
                AppApplication.getInstance().getPlayerEngineInterface().prev();
                pauseAnimation();
                showPlayStatus(true);
                if (isIn4G()) {
                    AppApplication.isAllowIn4G = true;
                }
            }
        });

    }

    @Override
    public void playNext() {
        //showTotalTime((int) (mMusicPlayController.getRemotePlayerEngine().getDuration() /1000));
        showPlayConfirm(new Runnable() {
            @Override
            public void run() {
                AppApplication.getInstance().getPlayerEngineInterface().next();
                pauseAnimation();
                showPlayStatus(true);
                if (isIn4G()) {
                    AppApplication.isAllowIn4G = true;
                }
            }
        });

    }

    @Override
    public void setPlaybackMode(Playlist.PlaylistPlaybackMode mode) {
        switch (mode) {
            case NORMAL:
                mPlayMode.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_cycleplay_n));
                break;
            case REPEAT:
                mPlayMode.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_singleplay_n));
                break;
            case SHUFFLE:
                mPlayMode.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_randomplay_n));
                break;
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void showVoiceStatus() {

    }

    @Override
    public void showPlayTime(int seconds) {
//        Log.d(TAG, "showPlayTime seconds: " + seconds);
        if (AppApplication.getInstance().getPlayerEngineInterface().isPrepared()) {
            hideWaitingDialog();
        }
        if (AppApplication.getInstance().getPlayerEngineInterface().isPlaying()) {

            mPlayOrPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_pause_n));
            playAnimation();
        } else {
            mPlayOrPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_play_n));
            pauseAnimation();
        }
        LogUtil.d(TAG, "showPlayTime seconds: " + seconds);
        mPlayTime.setText(NumberUtil.timeIntMToMM(seconds));
        mPalySeekBar.setProgress(seconds * 1000);
    }

    @Override
    public void showTotalTime(int seconds) {
        LogUtil.d(TAG, "showTotalTime seconds: " + seconds);
        mTotalTime.setText(NumberUtil.timeIntMToMM(seconds));
        mPalySeekBar.setMax(seconds * 1000);
    }

    @Override
    public void prepareComplete() {
        Log.d(TAG, "prepareComplete " );
        hideWaitingDialog();
    }

    @Override
    public void playError() {
        mPlayOrPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_play_n));
        pauseAnimation();
        hideWaitingDialog();
        Toast.makeText(MusicPlayActivity.this, R.string.palay_error_tip, Toast.LENGTH_SHORT).show();
    }

    public interface SeekbarStopTrackingListener {
        void SeekbarStopTracking(SeekBar seekBar);

        void SeekbarProgressChanged(int progress);
    }

    //磁盘和封面旋转动画
    ObjectAnimator mAniAvatar;
    ObjectAnimator mAniAvatarBg;
    float mValueAvatar = 0f;
    float mValueAvatarBg = 0f;
    //旋转一周所用时间
    private static final int ROTATE_TIME = 12 * 1000;
    //动画旋转重复执行的次数，这里代表无数次，似乎没有无限执行的属性，所以用了一个大数字代表
    private static final int ROTATE_COUNT = Integer.MAX_VALUE;
    private AnimatorSet animSet;

    /**
     * 初始化旋转封面动画对象
     *
     * @param start
     */
    private void initAvatarAnimation(float start) {
        mAniAvatar = ObjectAnimator.ofFloat(mMusicPic, "rotation", start, 359f + start);
        mAniAvatar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mValueAvatar = (Float) animation.getAnimatedValue("rotation");
            }
        });
        mAniAvatar.setDuration(ROTATE_TIME);
        mAniAvatar.setInterpolator(new LinearInterpolator());
//        mAniAvatar.setRepeatCount(ROTATE_COUNT);

        mAniAvatarBg = ObjectAnimator.ofFloat(mDiscBg, "rotation", start, 359f + start);
        mAniAvatarBg.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mValueAvatarBg = (Float) animation.getAnimatedValue("rotation");
            }
        });
        mAniAvatarBg.setDuration(ROTATE_TIME);
        mAniAvatarBg.setInterpolator(new LinearInterpolator());
//        mAniAvatarBg.setRepeatCount(ROTATE_COUNT);
        mAniAvatar.cancel();
        mAniAvatar.end();
        mAniAvatarBg.cancel();
        mAniAvatarBg.end();
    }


    /**
     * 播放
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void playAnimation() {
        LogUtil.d(TAG, "playAnimation is enter");
        if (animSet == null) {
            animSet = new AnimatorSet();
        }
        if (!animSet.isRunning()) {
            animSet.play(mAniAvatar);
            animSet.play(mAniAvatarBg);
            animSet.start();
        }
    }

    /**
     * 暂停
     */
    public void pauseAnimation() {
        LogUtil.d(TAG, "pauseAnimation is enter");
//        mAniAvatar.removeAllListeners();
//        mAniAvatarBg.removeAllListeners();
        if (animSet != null ) {
            animSet.cancel();
//            animSet.end();
        }
        if (mAniAvatar != null ) {
            mAniAvatar.cancel();
//            mAniAvatar.end();
        }
        if (mAniAvatarBg != null ) {
            mAniAvatarBg.cancel();
//            mAniAvatarBg.end();
        }

        initAvatarAnimation(mValueAvatar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetChanged != null) {
            unregisterReceiver(mNetChanged);
        }
    }

    private BroadcastReceiver mNetChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "mNetChanged action: " + ConnectivityManager.CONNECTIVITY_ACTION);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

                NetworkInfo info  = manager.getActiveNetworkInfo();
                if (info != null) {
                    if (info.isConnected()) {
                        /*if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                            if (AppApplication.getInstance().getPlayerEngineInterface().isPlaying()) {
                                AppApplication.getInstance().getPlayerEngineInterface().pause();
                                showPlayConfirm(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppApplication.getInstance().getPlayerEngineInterface().play();
                                        if (isIn4G()) {
                                            AppApplication.isAllowIn4G = true;
                                        }
                                    }
                                });
                            }
                        }*/
                    }
                }
            }
        }
    };

    private boolean isIn4G() {
        boolean isIn4G = false;
        NetworkInfo info  = manager.getActiveNetworkInfo();
        if (info != null) {
            if (info.isConnected()) {
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isIn4G = true;
                }
            }
        }
        return isIn4G;
    }

    private void showPlayConfirm(final Runnable sureRun) {

        if (isIn4G()) {
            if (AppApplication.isAllowIn4G) {
                sureRun.run();
            } else {
                View view = View.inflate(this, R.layout.dialog_delete, null);
                TextView message = (TextView) view.findViewById(R.id.dialog_delete_message);
                message.setText(R.string.only_wifi_play_tip);
                TextView sure = (TextView) view.findViewById(R.id.dialog_delete_confirm);
                TextView cancel = (TextView) view.findViewById(R.id.dialog_delete_cancel);
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m4GPlayConfirm.dismiss();
                        sureRun.run();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m4GPlayConfirm.dismiss();
                    }
                });
                m4GPlayConfirm = new CustomDialog(this, 0, 0, view, R.style.settings_style);
                m4GPlayConfirm.show();
            }
        } else {
            sureRun.run();
        }
    }

}
