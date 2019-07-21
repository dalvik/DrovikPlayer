package com.drovik.player.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.drovik.player.R;
import com.drovik.player.adv.AdvConst;
import com.drovik.player.news.api.VideoModel;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.bean.VideoContentBean;
import com.iflytek.voiceads.IFLYBannerAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.listener.IFLYAdListener;
import com.kuaiyou.loader.AdViewBannerManager;
import com.kuaiyou.loader.loaderInterface.AdViewBannerListener;


import org.yczbj.ycvideoplayerlib.constant.ConstantKeys;
import org.yczbj.ycvideoplayerlib.controller.VideoPlayerController;
import org.yczbj.ycvideoplayerlib.inter.listener.OnCompletedListener;
import org.yczbj.ycvideoplayerlib.inter.listener.OnVideoBackListener;
import org.yczbj.ycvideoplayerlib.player.VideoPlayer;

import java.util.Random;
import java.util.zip.CRC32;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NewsVideoContentActivity extends BaseCompatActivity implements AdViewBannerListener {

    private final static int MSG_REQUEST_AD = 1000;
    private final static int MSG_HIDE_AD = 1001;

    private MultiNewsArticleDataBean dataBean;
    private String image;
    private String groupId;
    private String itemId;
    private String videoId;
    private String videoTitle;
    private String shareUrl;

    private VideoPlayer videoPlayer;

    private VideoPlayerController controller;

    private TextView mNewsSource;
    private TextView mNewsContent;

    private static String TAG = "NewsVideoContentActivity";

    public static void launch(MultiNewsArticleDataBean bean) {
        Intent intent = new Intent(Utils.getContext(), NewsVideoContentActivity.class);
        Utils.getContext().startActivity(intent
                .putExtra(NewsVideoContentActivity.TAG, bean)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_video_detail);
        setTitle(R.string.home_news);
        fullScreen(R.color.base_actionbar_background);
        com.android.library.utils.Utils.setStatusTextColor(true, this);
        setActionBarBackgroundColor(R.color.base_actionbar_background, R.color.base_actionbar_background);
        videoPlayer = (VideoPlayer) findViewById(R.id.detail_player);
        mNewsSource = (TextView) findViewById(R.id.news_source);
        mNewsContent = (TextView) findViewById(R.id.news_content);
        bannerAdLayout = (LinearLayout) findViewById(R.id.ad_container);
        initData();
        initAdView();
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
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.releasePlayer();
    }

    private void initData() {
        Intent intent = getIntent();
        try {
            dataBean = intent.getParcelableExtra(TAG);
            if (null != dataBean.getVideo_detail_info()) {
                if (null != dataBean.getVideo_detail_info().getDetail_video_large_image()) {
                    image = dataBean.getVideo_detail_info().getDetail_video_large_image().getUrl();
                }
            }
            this.groupId = dataBean.getGroup_id() + "";
            this.itemId = dataBean.getItem_id() + "";
            this.videoId = dataBean.getVideo_id();
            this.videoTitle = dataBean.getTitle();
            this.shareUrl = dataBean.getDisplay_url();
            mNewsSource.setText(dataBean.getSource());
            mNewsContent.setText(dataBean.getAbstractX());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String url = getVideoContentApi(videoId);
        Log.d(TAG, "==> url: " + url + " videoId " + videoId);
        VideoModel model = VideoModel.getInstance();
        getVideoData(model,url);
    }

    private static String getVideoContentApi(String videoid) {
        String VIDEO_HOST = "http://ib.365yg.com";
        String VIDEO_URL = "/video/urls/v/1/toutiao/mp4/%s?r=%s";
        String r = getRandom();
        String s = String.format(VIDEO_URL, videoid, r);
        // 将/video/urls/v/1/toutiao/mp4/{videoid}?r={Math.random()} 进行crc32加密
        CRC32 crc32 = new CRC32();
        crc32.update(s.getBytes());
        String crcString = crc32.getValue() + "";
        String url = VIDEO_HOST + s + "&s=" + crcString;
        return url;
    }

    private static String getRandom() {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    private void setVideoPlayer(String urls) {
        if(videoPlayer==null || urls==null){
            return;
        }
        LogUtils.e("视频链接"+urls);
        //设置播放类型
        videoPlayer.setPlayerType(ConstantKeys.IjkPlayerType.TYPE_IJK);
        //设置视频地址和请求头部
        videoPlayer.setUp(urls, null);
        //创建视频控制器
        controller = new VideoPlayerController(this);
        controller.setTitle(dataBean.getTitle());
        controller.setLoadingType(ConstantKeys.Loading.LOADING_QQ);
        controller.imageView().setBackgroundResource(R.color.blackText);
        controller.setOnVideoBackListener(new OnVideoBackListener() {
            @Override
            public void onBackClick() {
                onBackPressed();
            }
        });
        controller.setOnCompletedListener(new OnCompletedListener() {
            @Override
            public void onCompleted() {

            }
        });
        //设置视频控制器
        videoPlayer.setController(controller);
        //是否从上一次的位置继续播放
        videoPlayer.continueFromLastPosition(true);
        //设置播放速度
        videoPlayer.setSpeed(1.0f);
        videoPlayer.start();
    }

    private void getVideoData(VideoModel model, String url) {
        model.getVideoContent(url)
                .subscribeOn(Schedulers.io())
                .map(new Function<VideoContentBean, String>() {
                    @Override
                    public String apply(@NonNull VideoContentBean videoContentBean) throws Exception {
                        VideoContentBean.DataBean.VideoListBean videoList = videoContentBean.getData().getVideo_list();
                        if (videoList.getVideo_3() != null) {
                            String base64 = videoList.getVideo_3().getMain_url();
                            String url = (new String(Base64.decode(base64.getBytes(), Base64.DEFAULT)));
                            Log.d(TAG, "getVideoUrls: " + url);
                            return url;
                        }

                        if (videoList.getVideo_2() != null) {
                            String base64 = videoList.getVideo_2().getMain_url();
                            String url = (new String(Base64.decode(base64.getBytes(), Base64.DEFAULT)));
                            Log.d(TAG, "getVideoUrls: " + url);
                            return url;
                        }

                        if (videoList.getVideo_1() != null) {
                            String base64 = videoList.getVideo_1().getMain_url();
                            String url = (new String(Base64.decode(base64.getBytes(), Base64.DEFAULT)));
                            Log.d(TAG, "getVideoUrls: " + url);
                            return url;
                        }
                        return null;
                    }
                })
                //.compose(view.<String>bindToLife())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        setVideoPlayer(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });
    }


    /************************************/
    private LinearLayout bannerAdLayout;
    private AdViewBannerManager adViewBIDView = null;

    private void initAdView() {
        adViewBIDView = new AdViewBannerManager(this, AdvConst.ADVIEW_APPID, AdViewBannerManager.BANNER_AUTO_FILL, false);
        adViewBIDView.setShowCloseBtn(true);
        adViewBIDView.setRefreshTime(15);
        adViewBIDView.setOpenAnim(true);
        adViewBIDView.setOnAdViewListener(this);
        if (null != bannerAdLayout) {
            bannerAdLayout.addView(adViewBIDView.getAdViewLayout());
        }
    }

    @Override
    public void onAdClicked() {
        Log.i("AdViewBID", "onAdClicked");
    }

    @Override
    public void onAdClosed() {
        Log.i("AdViewBID", "onAdClosedAd");
        if (null != adViewBIDView) {
            ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
            for (int i = 0; i < rootView.getChildCount(); i++) {
                if (rootView.getChildAt(i) == adViewBIDView.getAdViewLayout()) {
                    rootView.removeView(adViewBIDView.getAdViewLayout());
                }
            }
        }
        if (null != bannerAdLayout) {
            bannerAdLayout.removeAllViews();
        }
    }

    @Override
    public void onAdDisplayed() {
        Log.i("AdViewBID", "onAdDisplayed");
    }

    @Override
    public void onAdFailedReceived(String arg1) {
        Log.i("AdViewBID", "onAdRecieveFailed");
    }

    @Override
    public void onAdReceived() {
        Log.i("AdViewBID", "onAdRecieved");
    }

}
