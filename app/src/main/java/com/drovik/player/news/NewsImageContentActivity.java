package com.drovik.player.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.utils.StringUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.blankj.utilcode.util.Utils;
import com.drovik.player.R;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.utils.ImageUtil;

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
        setContentView(R.layout.activity_text_news_detail);
        setTitle(R.string.home_news);
        mNewsTitle = (TextView) findViewById(R.id.news_title);
        mNewsAvatar = (ImageView) findViewById(R.id.news_avatar);
        mReadCount =  (TextView) findViewById(R.id.news_read_cound);
        mNewsSource = (TextView) findViewById(R.id.news_source);
        mNewsImage = (ImageView) findViewById(R.id.news_image);
        mNewsContent = (TextView) findViewById(R.id.news_content);
        initData();
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

}
