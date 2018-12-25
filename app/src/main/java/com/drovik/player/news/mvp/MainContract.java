package com.drovik.player.news.mvp;


import android.app.Activity;

import com.drovik.player.news.NewsFrameActivity;
import com.flyco.tablayout.listener.CustomTabEntity;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/3/18
 * 描    述：Main主页面
 * 修订历史：
 * ================================================
 */
public interface MainContract {

    //View(activity/fragment)继承，需要实现的方法
    interface View extends BaseView {
        Activity getActivity();
    }

    //Presenter控制器
    interface Presenter extends BasePresenter {
        ArrayList<CustomTabEntity> getTabEntity();
        void bindView(NewsFrameActivity activity);
    }


}
