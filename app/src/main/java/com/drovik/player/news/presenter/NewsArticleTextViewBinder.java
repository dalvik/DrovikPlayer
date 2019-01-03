package com.drovik.player.news.presenter;

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

import com.android.audiorecorder.utils.DateUtil;
import com.drovik.player.R;
import com.drovik.player.news.NewsContentActivity;
import com.drovik.player.news.NewsContentActivity2;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.utils.ImageUtil;
import com.drovik.player.news.utils.SettingUtil;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.ItemViewBinder;

/**
 * 不带图片的 item
 */

public class NewsArticleTextViewBinder extends ItemViewBinder<MultiNewsArticleDataBean, NewsArticleTextViewBinder.ViewHolder> {

    private static final String TAG = "NewsArticleTextViewBind";

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_news_article_text, null);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull final MultiNewsArticleDataBean item) {

        final Context context = holder.itemView.getContext();

        try {
            if (null != item.getUser_info()) {
                String avatar_url = item.getUser_info().getAvatar_url();
                if (!TextUtils.isEmpty(avatar_url)) {
                    holder.ivMedia.setVisibility(View.GONE);
                    ImageUtil.loadImgByPicasso(context,avatar_url,R.drawable.image_default, holder.ivMedia);
                }
            }
            String tv_title = item.getTitle();
            String tv_abstract = item.getAbstractX();
            String tv_source = item.getSource();
            String tv_comment_count = item.getComment_count() + "条评论";
            if(!TextUtils.isEmpty(item.getLabel())) {
                holder.tvLevel.setText(item.getLabel());
            } else {
                holder.tvLevel.setVisibility(View.GONE);
            }
            String tv_datetime = DateUtil.getTimeTips(item.getPublish_time()* 1000l);
            holder.tvTitle.setText(tv_title);
            holder.tvTitle.setTextSize(SettingUtil.getInstance().getTextSize());
            holder.tvAbstract.setText(tv_abstract);
            holder.tvExtra.setText(tv_source + " " + tv_comment_count + " " + tv_datetime);
            holder.ivDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            RxView.clicks(holder.itemView)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Object o) throws Exception {
                            NewsContentActivity.launch(item);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_level)
        TextView tvLevel;
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
        @BindView(R.id.content)
        LinearLayout content;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
