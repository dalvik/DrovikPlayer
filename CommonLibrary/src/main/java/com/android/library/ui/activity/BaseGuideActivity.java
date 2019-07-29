package com.android.library.ui.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.library.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 初次启动引导页
 */
public abstract class BaseGuideActivity extends BaseCommonActivity implements OnClickListener, OnPageChangeListener {
    // 引导图片资源
    private int[] pics = null;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private ArrayList<View> views;
    // 底部小点图片
    private View[] dots;

    // 记录当前选中位置
    private int lastSelectIndex;
    private LinearLayout dotsLayout;

    private Button enterBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.guide_activity);

        views = new ArrayList<View>();
        enterBtn = (Button) findViewById(R.id.guide_enter_btn);
        enterBtn.setOnClickListener(this);

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        pics = initPictures();
        if (pics == null || pics.length == 0) {
            throw new RuntimeException("Guide Pics not init");
        }
        // 初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setBackgroundResource(pics[i]);
            views.add(iv);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // 初始化Adapter
        adapter = new ViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
        // 绑定回调
        viewPager.setOnPageChangeListener(this);

        // 初始化底部小点
        initDots();
    }

    public abstract int[] initPictures();

    private void initDots() {
        dotsLayout = (LinearLayout) findViewById(R.id.dots_ll);

        dots = new View[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            dots[i] = dotsLayout.getChildAt(i);
            dots[i].setEnabled(false);// 都设为灰色
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
        }

        lastSelectIndex = 0;
        dots[lastSelectIndex].setEnabled(true);// 设置为白色，即选中状态
    }

    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }

        viewPager.setCurrentItem(position);
    }

    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || lastSelectIndex == positon) {
            return;
        }

        dots[positon].setEnabled(true);
        dots[lastSelectIndex].setEnabled(false);

        lastSelectIndex = positon;
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int position) {
        // 设置底部小点选中状态
        if (position == pics.length - 1) {
            dotsLayout.setVisibility(View.GONE);
            enterBtn.setVisibility(View.VISIBLE);
        } else {
            dotsLayout.setVisibility(View.VISIBLE);
            enterBtn.setVisibility(View.GONE);
            setCurDot(position);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.guide_enter_btn == id) {
            gotoNextActivity();
        } else {
            int position = (Integer) v.getTag();
            setCurView(position);
            setCurDot(position);
        }
    }

    public abstract void gotoNextActivity();

    public class ViewPagerAdapter extends PagerAdapter {

        // 界面列表
        private List<View> views;

        public ViewPagerAdapter(List<View> views) {
            this.views = views;
        }

        // 销毁arg1位置的界面
        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        // 获得当前界面数
        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }
            return 0;
        }

        // 初始化arg1位置的界面
        @Override
        public Object instantiateItem(View view, int position) {
            ((ViewPager) view).addView(views.get(position), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return views.get(position);
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

    }
}
