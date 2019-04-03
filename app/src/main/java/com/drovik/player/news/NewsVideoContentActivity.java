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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.drovik.player.R;
import com.drovik.player.news.api.VideoModel;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.bean.VideoContentBean;
import com.iflytek.voiceads.IFLYBannerAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.listener.IFLYAdListener;


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

public class NewsVideoContentActivity extends BaseCompatActivity {

    private final static int MSG_REQUEST_AD = 1000;
    private final static int MSG_HIDE_AD = 1001;
    private final static String adUnitId = "4EB378DDD1ACCC98DB5430437962ACF8";
    private IFLYBannerAd bannerView;
    private LinearLayout bannerAdLayout;

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
        videoPlayer = (VideoPlayer) findViewById(R.id.detail_player);
        mNewsSource = (TextView) findViewById(R.id.news_source);
        mNewsContent = (TextView) findViewById(R.id.news_content);
        bannerAdLayout = (LinearLayout) findViewById(R.id.ad_container);
        initData();
        sendHandlerMessage(MSG_REQUEST_AD, 3000);
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

    private void initAd() {
        bannerView = IFLYBannerAd.createBannerAd(getApplicationContext(), adUnitId);
        bannerView.setParameter(AdKeys.APP_VER, StringUtils.getVersionName(this));
        //广告容器添加bannerView
        bannerAdLayout.removeAllViews();
        bannerAdLayout.addView(bannerView);
        //请求广告，添加监听器
        bannerView.loadAd(mAdListener);
    }

    private void hideAd() {
        if (bannerView != null) {
            bannerView.destroy();
        }
        bannerAdLayout.setVisibility(View.GONE);
    }

    private void sendHandlerMessage(int what, long delayed) {
        mHandler.removeMessages(what);
        mHandler.sendEmptyMessageDelayed(what, delayed);
    }

    //3秒钟后请求，显示1分钟自动隐藏，15分钟重新请求
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_REQUEST_AD:
                    initAd();
                    break;
                case MSG_HIDE_AD:
                    hideAd();
                    sendHandlerMessage(MSG_REQUEST_AD, 10*60*1000);//十分钟后重新请求
                    break;
                default:
                    break;
            }
        }
    };


    private IFLYAdListener mAdListener = new IFLYAdListener() {

        /**
         * 广告请求成功
         */
        @Override
        public void onAdReceive() {
            //展示广告
            bannerAdLayout.setVisibility(View.VISIBLE);
            bannerView.showAd();
            sendHandlerMessage(MSG_HIDE_AD, 60*1000);//1分钟后自动隐藏
            LogUtil.d(TAG, "==> onAdReceive");
            //Toast.makeText(getActivity(), "onAdReceive", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onAdFailed(AdError error) {
            //获取广告失败
            //Toast.makeText(getActivity(), "onAdFailed", Toast.LENGTH_SHORT).show();
            LogUtil.d(TAG, "==> onAdFailed: " + error.getErrorDescription() + " " + error.getErrorCode());
            sendHandlerMessage(MSG_REQUEST_AD, 10*60*1000);//十分钟后重新请求
        }

        /**
         * 广告被点击
         */
        @Override
        public void onAdClick() {
            LogUtil.d(TAG, "==> onAdClick");
        }

        /**
         * 广告被关闭
         */
        @Override
        public void onAdClose() {
            LogUtil.d(TAG, "==> onAdClose");
        }

        /**
         * 广告曝光
         */
        @Override
        public void onAdExposure() {
            LogUtil.d(TAG, "==> onAdExposure");
        }

        /**
         * 下载确认
         */
        @Override
        public void onConfirm() {
            LogUtil.d(TAG, "==> onConfirm");
        }

        /**
         * 下载取消
         */
        @Override
        public void onCancel() {
            LogUtil.d(TAG, "==> onCancel");
        }
    };
}
