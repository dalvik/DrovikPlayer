package com.drovik.player.news.api;

import com.drovik.player.news.bean.MultiNewsArticleBean;
import com.drovik.player.news.bean.VideoContentBean;
import com.drovik.player.news.manager.RetrofitWrapper;

import io.reactivex.Observable;

/**
 * Created by PC on 2017/8/21.
 * 作者：PC
 */
public class VideoModel {

    private static VideoModel model;
    private VideoApi mApiService;
    private static final String HOST = "http://toutiao.com/";

    private VideoModel() {
        mApiService = RetrofitWrapper
                .getInstance(HOST)
                .create(VideoApi.class);
    }

    public static VideoModel getInstance(){
        if(model == null) {
            model = new VideoModel();
        }
        return model;
    }

    public Observable<MultiNewsArticleBean> getVideoArticle(String category, String maxBehotTime) {
        return mApiService.getVideoArticle(category,maxBehotTime);
    }

    public Observable<VideoContentBean> getVideoContent(String url) {
        return mApiService.getVideoContent(url);
    }

}
