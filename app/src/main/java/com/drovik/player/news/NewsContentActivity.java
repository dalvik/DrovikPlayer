package com.drovik.player.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.ui.activity.BaseCommonActivity;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.drovik.player.R;
import com.drovik.player.news.api.VideoModel;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.bean.VideoContentBean;

import org.yczbj.ycvideoplayerlib.constant.ConstantKeys;
import org.yczbj.ycvideoplayerlib.controller.VideoPlayerController;
import org.yczbj.ycvideoplayerlib.inter.listener.OnVideoBackListener;
import org.yczbj.ycvideoplayerlib.player.VideoPlayer;

import java.util.Random;
import java.util.zip.CRC32;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NewsContentActivity extends BaseCommonActivity {

    private MultiNewsArticleDataBean dataBean;
    private String image;
    private String groupId;
    private String itemId;
    private String videoId;
    private String videoTitle;
    private String shareUrl;

    private TextView mNewsSource;
    private TextView mReadCount;
    private ImageView mNewsAvatar;
    private TextView mNewsContent;

    private static String TAG = "VideoContentActivity";

    public static void launch(MultiNewsArticleDataBean bean) {
        Intent intent = new Intent(Utils.getContext(), NewsContentActivity.class);
        Utils.getContext().startActivity(intent
                .putExtra(NewsContentActivity.TAG, bean)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_news_detail);
        mNewsAvatar = (ImageView) findViewById(R.id.news_avatar);
        mReadCount =  (TextView) findViewById(R.id.news_read_cound);
        mNewsSource = (TextView) findViewById(R.id.news_source);
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
            if(dataBean.getUser_info() != null) {
                if(!TextUtils.isEmpty(dataBean.getUser_info().getAvatar_url())) {
                    Glide.with(this).load(dataBean.getUser_info().getAvatar_url()).asBitmap().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(mNewsAvatar);
                } else {
                    mNewsAvatar.setVisibility(View.INVISIBLE);
                }

                if(!TextUtils.isEmpty(dataBean.getUser_info().getName())) {
                    mNewsSource.setText(dataBean.getUser_info().getName());
                }
            }
            mReadCount.setText(String.valueOf(dataBean.getRead_count()));
            mNewsContent.setText(dataBean.getAbstractX());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
