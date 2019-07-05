package com.drovik.player.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.library.ui.activity.BaseCompatActivity;
import com.blankj.utilcode.util.Utils;
import com.drovik.player.R;
import com.drovik.player.news.bean.MultiNewsArticleDataBean;
import com.drovik.player.news.fragment.NewsContentFragment;

/**
 * Created by Meiji on 2017/2/28.
 */

public class NewsTextContentActivity extends BaseCompatActivity {

    private static final String TAG = "NewsTextContentActivity";
    private static final String IMG = "img";

    public static void launch(MultiNewsArticleDataBean bean) {
        Utils.getContext().startActivity(new Intent(Utils.getContext(), NewsTextContentActivity.class).putExtra(TAG, bean).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void launch(MultiNewsArticleDataBean bean, String imgUrl) {
        Utils.getContext().startActivity(new Intent(Utils.getContext(), NewsTextContentActivity.class).putExtra(TAG, bean).putExtra(IMG, imgUrl).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_container);
        setTitle(R.string.home_news);
        fullScreen(R.color.base_actionbar_background);
        com.android.library.utils.Utils.setStatusTextColor(true, this);
        setActionBarBackgroundColor(R.color.base_actionbar_background, R.color.base_actionbar_background);
        Intent intent = getIntent();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,  NewsContentFragment.newInstance(intent.getParcelableExtra(TAG), intent.getStringExtra(IMG))).commit();
    }
}
