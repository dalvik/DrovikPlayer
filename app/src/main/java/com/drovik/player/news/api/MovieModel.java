package com.drovik.player.news.api;


import com.drovik.player.news.bean.MovieBean;
import com.drovik.player.news.bean.MovieDetailBean;
import com.drovik.player.news.manager.RetrofitWrapper;

import io.reactivex.Observable;

/**
 * Created by PC on 2017/8/21.
 * 作者：PC
 */

public class MovieModel {

    private static MovieModel model;
    private MovieApi mApiService;
    private static final String HOST = "http://api.svipmovie.com/front/";

    private MovieModel() {
        mApiService = RetrofitWrapper
                .getInstance(HOST)
                .create(MovieApi.class);
    }

    public static MovieModel getInstance(){
        if(model == null) {
            model = new MovieModel();
        }
        return model;
    }

    public Observable<MovieBean> getHomePage() {
        return mApiService.getHomePage();
    }

    public Observable<MovieDetailBean> getVideoInfo(String mediaId) {
        return mApiService.getVideoInfo(mediaId);
    }

    public Observable<MovieBean> getVideoList(String catalogId , String pnum) {
        return mApiService.getVideoList(catalogId,pnum);
    }

    public Observable<MovieBean> getVideoListByKeyWord(String keyword,String pnum) {
        return mApiService.getVideoListByKeyWord(keyword,pnum);
    }

    public Observable<MovieBean> getCommentList(String mediaId , String pnum) {
        return mApiService.getCommentList(mediaId,pnum);
    }


}
