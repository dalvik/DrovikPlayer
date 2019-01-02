package com.drovik.player.news.contract;

import com.drovik.player.news.base.IBasePresenter;
import com.drovik.player.news.base.IBaseView;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;

public interface INewsContent {

    interface View extends IBaseView<Presenter> {

        /**
         * 加载网页
         */
        void onSetWebView(String url, boolean flag);
    }

    interface Presenter extends IBasePresenter {

        /**
         * 请求数据
         */
        void doLoadData(MultiNewsArticleDataBean dataBean);
    }
}
