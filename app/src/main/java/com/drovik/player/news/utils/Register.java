package com.drovik.player.news.utils;

import android.support.annotation.NonNull;

import com.drovik.player.news.bean.LoadingBean;
import com.drovik.player.news.bean.LoadingEndBean;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.presenter.LoadingEndViewBinder;
import com.drovik.player.news.presenter.LoadingViewBinder;
import com.drovik.player.news.presenter.NewsArticleImgViewBinder;
import com.drovik.player.news.presenter.NewsArticleTextViewBinder;
import com.drovik.player.news.presenter.NewsArticleVideoViewBinder;

import me.drakeet.multitype.ClassLinker;
import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by yc on 2018/2/26.
 */

public class Register {

    public static void registerVideoArticleItem(@NonNull MultiTypeAdapter adapter) {
        adapter.register(MultiNewsArticleDataBean.class, new NewsArticleVideoViewBinder());
        adapter.register(LoadingBean.class, new LoadingViewBinder());
        adapter.register(LoadingEndBean.class, new LoadingEndViewBinder());
    }

    public static void registerNewsArticleItem(MultiTypeAdapter adapter) {
        adapter.register(MultiNewsArticleDataBean.class)
                .to(new NewsArticleImgViewBinder(),
                        new NewsArticleVideoViewBinder(),
                        new NewsArticleTextViewBinder())
                .withClassLinker(new ClassLinker<MultiNewsArticleDataBean>() {
                    @NonNull
                    @Override
                    public Class<? extends ItemViewBinder<MultiNewsArticleDataBean, ?>> index(int position, @NonNull MultiNewsArticleDataBean item) {
                        if (item.isHas_video()) {
                            return NewsArticleVideoViewBinder.class;
                        }
                        if (null != item.getImage_list() && item.getImage_list().size() > 0) {
                            return NewsArticleImgViewBinder.class;
                        }
                        return NewsArticleTextViewBinder.class;
                    }
                });
        adapter.register(LoadingBean.class, new LoadingViewBinder());
        adapter.register(LoadingEndBean.class, new LoadingEndViewBinder());
    }
}
