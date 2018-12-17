package com.drovik.player.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import com.android.library.ui.activity.BaseCommonActivity;
import com.blankj.utilcode.util.Utils;
import com.drovik.player.R;
import com.drovik.player.news.api.VideoModel;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.bean.VideoContentBean;


import java.util.Random;
import java.util.zip.CRC32;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class VideoContentActivity extends BaseCommonActivity {

    private MultiNewsArticleDataBean dataBean;
    private String image;
    private String groupId;
    private String itemId;
    private String videoId;
    private String videoTitle;
    private String shareUrl;
    //private NormalGSYVideoPlayer detailPlayer;

    //private GSYADVideoPlayer adPlayer;

    private String urlAd = "http://video.7k.cn/app_video/20171202/6c8cf3ea/v.m3u8.mp4";

    private String urlAd2 = "http://video.7k.cn/app_video/20171202/6c8cf3ea/v.m3u8.mp4";

    private String url = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";

    private static String TAG = "VideoContentActivity";

    public static void launch(MultiNewsArticleDataBean bean) {
        Intent intent = new Intent(Utils.getContext(), VideoContentActivity.class);
        Utils.getContext().startActivity(intent
                .putExtra(VideoContentActivity.TAG, bean)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ad_player2);
        //detailPlayer = (NormalGSYVideoPlayer) findViewById(R.id.detail_player);
        //adPlayer = (GSYADVideoPlayer) findViewById(R.id.ad_player);

        //initVideoBuilderMode();

       /* detailPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });
        detailPlayer.setStartAfterPrepared(false);
        detailPlayer.setReleaseWhenLossAudio(false);

        detailPlayer.setGSYVideoProgressListener(new GSYVideoProgressListener() {
            private int preSecond = 0;
            @Override
            public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
                //在5秒的时候弹出中间广告
                int currentSecond = currentPosition / 1000;
                if (currentSecond == 5 && currentSecond != preSecond) {
                   // detailPlayer.getCurrentPlayer().onVideoPause();
                   // getGSYADVideoOptionBuilder().setUrl(urlAd2).build(adPlayer);
                    //startAdPlay();
                }
                preSecond = currentSecond;
            }
        });*/

        initData();
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        String url = getVideoContentApi(videoId);
        Log.d(TAG, "==> url: " + url + " videoId " + videoId);
        VideoModel model = VideoModel.getInstance();
        getVideoData(model,url);
    }


    private void onLoadData() {

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
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });
    }




}
