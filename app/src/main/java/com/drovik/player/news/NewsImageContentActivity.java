package com.drovik.player.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.audiorecorder.utils.StringUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.blankj.utilcode.util.Utils;
import com.drovik.player.R;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.utils.ImageUtil;
import com.iflytek.voiceads.IFLYBannerAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.listener.IFLYAdListener;

public class NewsImageContentActivity extends BaseCompatActivity {

    private MultiNewsArticleDataBean dataBean;
    private String mImageUrl;
    private String image;
    private String groupId;
    private String itemId;
    private String videoId;
    private String videoTitle;
    private String shareUrl;

    private TextView mNewsTitle;
    private TextView mNewsSource;
    private TextView mReadCount;
    private ImageView mNewsAvatar;
    private ImageView mNewsImage;
    private TextView mNewsContent;

    private static String EXTRA_URL = "url";

    private final static int MSG_REQUEST_AD = 1000;
    private final static int MSG_HIDE_AD = 1001;
    private final static String adUnitId = "4EB378DDD1ACCC98DB5430437962ACF8";
    private IFLYBannerAd bannerView;
    private LinearLayout bannerAdLayout;


    private static String TAG = "NewsVideoContentActivity";

    public static void launch(MultiNewsArticleDataBean bean) {
        Intent intent = new Intent(Utils.getContext(), NewsImageContentActivity.class);
        Utils.getContext().startActivity(intent
                .putExtra(NewsImageContentActivity.TAG, bean)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void launch(MultiNewsArticleDataBean bean, String url) {
        Intent intent = new Intent(Utils.getContext(), NewsImageContentActivity.class);
        Utils.getContext().startActivity(intent
                .putExtra(NewsImageContentActivity.TAG, bean)
                .putExtra(EXTRA_URL, url)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_news_detail);
        setTitle(R.string.home_news);
        mNewsTitle = (TextView) findViewById(R.id.news_title);
        mNewsAvatar = (ImageView) findViewById(R.id.news_avatar);
        mReadCount =  (TextView) findViewById(R.id.news_read_cound);
        mNewsSource = (TextView) findViewById(R.id.news_source);
        mNewsImage = (ImageView) findViewById(R.id.news_image);
        mNewsContent = (TextView) findViewById(R.id.news_content);
        initData();
        bannerAdLayout = (LinearLayout)findViewById(R.id.ad_container);
        sendHandlerMessage(MSG_REQUEST_AD, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        Intent intent = getIntent();
        try {
            dataBean = intent.getParcelableExtra(TAG);
            if(intent.hasExtra(EXTRA_URL)){
                mImageUrl = intent.getStringExtra(EXTRA_URL);
            }
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
            setTitle(R.string.news_title);
            mNewsTitle.setText(this.videoTitle);
            if(dataBean.getUser_info() != null) {
                if(!TextUtils.isEmpty(dataBean.getUser_info().getAvatar_url())) {
                    ImageUtil.loadImgByPicasso(this, dataBean.getUser_info().getAvatar_url(), R.drawable.image_default, mNewsAvatar);
                } else {
                    mNewsAvatar.setVisibility(View.INVISIBLE);
                }

                if(!TextUtils.isEmpty(dataBean.getUser_info().getName())) {
                    mNewsSource.setText(dataBean.getUser_info().getName());
                }
            }
            mReadCount.setText(getString(R.string.news_read_count, dataBean.getComment_count() + "评论 " + StringUtil.formatNum(String.valueOf(dataBean.getRead_count()), false)));
            if(!TextUtils.isEmpty(mImageUrl)) {
                mNewsImage.setVisibility(View.VISIBLE);
                ImageUtil.loadImgByPicasso(this, mImageUrl, R.drawable.image_default, mNewsImage);
            }
            mNewsContent.setText(dataBean.getAbstractX());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
