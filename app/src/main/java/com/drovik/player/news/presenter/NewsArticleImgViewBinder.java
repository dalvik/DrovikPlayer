package com.drovik.player.news.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.drovik.player.R;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.utils.ImageUtil;
import com.drovik.player.news.utils.SettingUtil;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 带图片的 item
 */

public class NewsArticleImgViewBinder extends ItemViewBinder<MultiNewsArticleDataBean, NewsArticleImgViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_news_article_img, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull final MultiNewsArticleDataBean item) {

        final Context context = holder.itemView.getContext();

        try {
            String imgUrl = "http://p3.pstatp.com/";
            List<MultiNewsArticleDataBean.ImageListBean> image_list = item.getImage_list();
            if (image_list != null && image_list.size() != 0) {
                String url = image_list.get(0).getUrl();
                ImageUtil.loadImgByPicasso(context,url,R.drawable.image_default,holder.ivImage);
                if (!TextUtils.isEmpty(image_list.get(0).getUri())) {
                    imgUrl += image_list.get(0).getUri().replace("list", "large");
                }
            }

            if (null != item.getUser_info()) {
                String avatar_url = item.getUser_info().getAvatar_url();
                if (!TextUtils.isEmpty(avatar_url)) {
                    ImageUtil.loadImgByPicasso(context,avatar_url,R.drawable.image_default,holder.ivMedia);
                }
            }

            String tv_title = item.getTitle();
            String tv_abstract = item.getAbstractX();
            String tv_source = item.getSource();
            String tv_comment_count = item.getComment_count() + "评论";
            String tv_datetime = TimeUtils.millis2String(item.getPublish_time()* 1000l);
            holder.tvTitle.setText(tv_title);
            holder.tvTitle.setTextSize(SettingUtil.getInstance().getTextSize());
            holder.tvAbstract.setText(tv_abstract);
            holder.tvExtra.setText(tv_source + " - " + tv_comment_count + " - " + tv_datetime);
            holder.ivDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            final String finalImgUrl = imgUrl;
            RxView.clicks(holder.itemView)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                            //NewsContentActivity.launch(item, finalImgUrl);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_media)
        ImageView ivMedia;
        @BindView(R.id.tv_extra)
        TextView tvExtra;
        @BindView(R.id.iv_dots)
        ImageView ivDots;
        @BindView(R.id.header)
        LinearLayout header;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_abstract)
        TextView tvAbstract;
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.ll_content)
        LinearLayout llContent;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
