package com.drovik.player.news.contract;



import org.yczbj.ycvideoplayer.base.mvp2.IBaseListView;
import org.yczbj.ycvideoplayer.base.mvp2.IBasePresenter;
import org.yczbj.ycvideoplayer.ui.video.model.bean.MultiNewsArticleDataBean;

import java.util.List;

public interface INewsArticle {

    interface View extends IBaseListView<Presenter> {

        /**
         * 请求数据
         */
        void onLoadData();

        /**
         * 刷新
         */
        void onRefresh();
    }

    interface Presenter extends IBasePresenter {

        /**
         * 请求数据
         */
        void doLoadData(String... category);

        /**
         * 再起请求数据
         */
        void doLoadMoreData();

        /**
         * 设置适配器
         */
        void doSetAdapter(List<MultiNewsArticleDataBean> dataBeen);

        /**
         * 加载完毕
         */
        void doShowNoMore();
    }
}
