package com.android.library.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;

import com.android.library.ui.adapter.BannerAdapter;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 无限滚动广告栏组件
 */
@SuppressWarnings("deprecation")
public class BannerGallery extends Gallery implements AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener, OnTouchListener {
    /**
     * 显示的Activity
     */
    private Context mContext;
    /**
     * 条目单击事件接口
     */
    private OnItemClickListener onItemClickListener;
    /**
     * 图片切换时间
     */
    private int mSwitchTime;
    /**
     * 自动滚动的定时器
     */
    private Timer mTimer;
    /**
     * 圆点容器
     */
    private LinearLayout mOvalLayout;
    /**
     * 当前选中的数组索引
     */
    private int curIndex = 0;
    /**
     * 上次选中的数组索引
     */
    private int oldIndex = 0;
    /**
     * 圆点选中时的背景ID
     */
    private int mFocusedId;
    /**
     * 圆点正常时的背景ID
     */
    private int mNormalId;
    /**
     * 处理定时滚动任务
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 不包含spacing会导致onKeyDown()失效!!!
            // 失效onKeyDown()前先调用onScroll(null,1,0)可处理
            onScroll(null, null, 1, 0);
            onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
        }
    };

    public BannerGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BannerGallery(Context context) {
        super(context);
    }

    public BannerGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void start(Context context, int switchTime) {
        start(context, switchTime, null, 0, 0);
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        if (!(adapter instanceof BannerAdapter)) {
            throw new RuntimeException(" must AdAdapter");
        }
        super.setAdapter(adapter);
    }

    /**
     * @param context    显示的Activity ,不能为null
     * @param switchTime 图片切换时间 写0 为不自动切换
     * @param ovalLayout 圆点容器 ,可为空
     * @param focusedId  圆点选中时的背景ID,圆点容器可为空写0
     * @param normalId   圆点正常时的背景ID,圆点容器为空写0
     */
    public void start(Context context, int switchTime, LinearLayout ovalLayout, int focusedId, int normalId) {
        this.mContext = context;
        this.mSwitchTime = switchTime;
        this.mOvalLayout = ovalLayout;
        this.mFocusedId = focusedId;
        this.mNormalId = normalId;

        setOnItemClickListener(this);
        setOnTouchListener(this);
        setOnItemSelectedListener(this);
        setSoundEffectsEnabled(false);
        setAnimationDuration(700); // 动画时间
        setUnselectedAlpha(1); // 未选中项目的透明度
        // 不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
        setSpacing(0);
        // 取靠近中间 图片数组的整倍数
        setSelection(getCount() / 2); // 默认选中中间位置为起始位置
        setFocusableInTouchMode(true);
        startTimer();// 开始自动滚动任务
    }

    public void setGalleryAdapter(BannerAdapter adAdapter) {
        setAdapter(adAdapter);
        initOvalLayout();// 初始化圆点
    }

    /**
     * 初始化圆点
     */
    private void initOvalLayout() {
        if (mOvalLayout != null) {
            mOvalLayout.removeAllViews();
        }

        if (mOvalLayout != null && getRealCount() < 2) {// 如果只有一第图时不显示圆点容器
            mOvalLayout.getLayoutParams().height = 0;
        } else if (mOvalLayout != null) {
            mOvalLayout.getLayoutParams().height = 20;
            // 圆点的大小是 圆点窗口的 70%;
            int Ovalheight = (int) (mOvalLayout.getLayoutParams().height * 0.7);
            // 圆点的左右外边距是 圆点窗口的 20%;
            int Ovalmargin = (int) (mOvalLayout.getLayoutParams().height * 0.2);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Ovalheight, Ovalheight);
            layoutParams.setMargins(Ovalmargin, 0, Ovalmargin, 0);
            for (int i = 0; i < getRealCount(); i++) {
                View v = new View(mContext); // 员点
                v.setLayoutParams(layoutParams);
                v.setBackgroundResource(mNormalId);
                mOvalLayout.addView(v);
            }
            // 选中第一个
            mOvalLayout.getChildAt(0).setBackgroundResource(mFocusedId);
        }
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int kEvent;
        if (isScrollingLeft(e1, e2)) { // 检查是否往左滑动
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        } else { // 检查是否往右滑动
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;

    }

    /**
     * 检查是否往左滑动
     */
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > (e1.getX() + 50);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        try {
            return super.onScroll(e1, e2, distanceX, distanceY);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()) {
            startTimer();// 开始自动滚动任务
        } else {
            stopTimer();// 停止自动滚动任务
        }
        return false;
    }

    /**
     * 图片切换事件
     */
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
        try {
            curIndex = position % getRealCount();
            if (mOvalLayout != null && getRealCount() > 1) { // 切换圆点
                mOvalLayout.getChildAt(oldIndex).setBackgroundResource(mNormalId); // 圆点取消
                mOvalLayout.getChildAt(curIndex).setBackgroundResource(mFocusedId);// 圆点选中
                oldIndex = curIndex;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getRealCount() {
        return ((BannerAdapter) getAdapter()).getRealCount();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    /**
     * 项目点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(curIndex);
        }
    }

    /**
     * 设置项目点击事件监听器
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * 停止自动滚动任务
     */
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 开始自动滚动任务 图片大于1张才滚动
     */
    public void startTimer() {
        if (mTimer == null && getRealCount() > 1 && mSwitchTime > 0) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                public void run() {
                    handler.sendMessage(handler.obtainMessage(1));
                }
            }, mSwitchTime, mSwitchTime);
        }
    }

    /**
     * 项目点击事件监听器接口
     */
    public static interface OnItemClickListener {
        /**
         * @param curIndex 当前条目在数组中的下标
         */
        void onItemClick(int curIndex);
    }
}
