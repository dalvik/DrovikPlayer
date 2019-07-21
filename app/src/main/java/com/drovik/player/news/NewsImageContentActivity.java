package com.drovik.player.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.audiorecorder.utils.StringUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.blankj.utilcode.util.Utils;
import com.drovik.player.R;
import com.drovik.player.adv.AdvConst;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.utils.ImageUtil;
import com.iflytek.voiceads.IFLYBannerAd;
import com.kuaiyou.loader.AdViewBannerManager;
import com.kuaiyou.loader.loaderInterface.AdViewBannerListener;

public class NewsImageContentActivity extends BaseCompatActivity implements AdViewBannerListener {

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
        fullScreen(R.color.base_actionbar_background);
        com.android.library.utils.Utils.setStatusTextColor(true, this);
        setActionBarBackgroundColor(R.color.base_actionbar_background, R.color.base_actionbar_background);
        mNewsTitle = (TextView) findViewById(R.id.news_title);
        mNewsAvatar = (ImageView) findViewById(R.id.news_avatar);
        mReadCount =  (TextView) findViewById(R.id.news_read_cound);
        mNewsSource = (TextView) findViewById(R.id.news_source);
        mNewsImage = (ImageView) findViewById(R.id.news_image);
        mNewsContent = (TextView) findViewById(R.id.news_content);
        initData();
        bannerAdLayout = (LinearLayout)findViewById(R.id.ad_container);
        initAdView();
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

    /************************************/
    private LinearLayout bannerAdLayout;
    private AdViewBannerManager adViewBIDView = null;

    private void initAdView() {
        adViewBIDView = new AdViewBannerManager(this, AdvConst.ADVIEW_APPID, AdViewBannerManager.BANNER_AUTO_FILL, true);
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
