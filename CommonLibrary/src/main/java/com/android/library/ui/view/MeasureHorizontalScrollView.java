package com.android.library.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.android.library.ui.adapter.MyHorizontalScrollViewAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * 单行横向滑动滚动的listView
 */
public class MeasureHorizontalScrollView extends HorizontalScrollView implements
        OnClickListener {

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    private OnItemClickListener mOnClickListener;

    private static final String TAG = "MyHorizontalScrollView";

    private LinearLayout mContainer;

    private int mChildWidth;
    private int mChildHeight;
    private int mCurrentIndex;
    private int mFristIndex;
    private MyHorizontalScrollViewAdapter mAdapter;
    private int mCountOneScreen;
    private int mScreenWitdh;

    private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

    public MeasureHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWitdh = outMetrics.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mContainer = (LinearLayout) getChildAt(0);
    }

    protected void loadNextImg() {
        // 数组边界值计算
        if (mCurrentIndex == mAdapter.getCount() - 1) {
            return;
        }
        // 移除第一张图片，且将水平滚动位置置0
        scrollTo(0, 0);
        mViewPos.remove(mContainer.getChildAt(0));
        mContainer.removeViewAt(0);

        // 获取下一张图片，并且设置onclick事件，且加入容器中
        View view = mAdapter.getView(++mCurrentIndex, null, mContainer);
        view.setOnClickListener(this);
        mContainer.addView(view);
        mViewPos.put(view, mCurrentIndex);

        // 当前第一张图片小标
        mFristIndex++;
        // 如果设置了滚动监听则触发
    }

    /**
     * 加载前一张图片
     */
    protected void loadPreImg() {
        // 如果当前已经是第一张，则返回
        if (mFristIndex == 0)
            return;
        // 获得当前应该显示为第一张图片的下标
        int index = mCurrentIndex - mCountOneScreen;
        if (index >= 0) {
            // mContainer = (LinearLayout) getChildAt(0);
            // 移除最后一张
            int oldViewPos = mContainer.getChildCount() - 1;
            mViewPos.remove(mContainer.getChildAt(oldViewPos));
            mContainer.removeViewAt(oldViewPos);

            // 将此View放入第一个位置
            View view = mAdapter.getView(index, null, mContainer);
            mViewPos.put(view, index);
            mContainer.addView(view, 0);
            view.setOnClickListener(this);
            // 水平滚动位置向左移动view的宽度个像素
            scrollTo(mChildWidth, 0);
            // 当前位置--，当前第一个显示的下标--
            mCurrentIndex--;
            mFristIndex--;
        }
    }

    public void initAdapter(MyHorizontalScrollViewAdapter adapter) {
        this.mAdapter = adapter;
        mContainer = (LinearLayout) getChildAt(0);
        final View view = mAdapter.getView(0, null, mContainer);
        mContainer.addView(view);
        if (mChildWidth == 0 && mChildHeight == 0) {
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            mChildHeight = view.getMeasuredHeight();
            mChildWidth = view.getMeasuredWidth();
            Log.e(TAG, view.getMeasuredWidth() + "," + view.getMeasuredHeight());
            mChildHeight = view.getMeasuredHeight();
            // 计算每次加载多少个View
            mCountOneScreen = (mScreenWitdh / mChildWidth == 0) ? mScreenWitdh
                    / mChildWidth + 1 : mScreenWitdh / mChildWidth + 2;
            Log.e(TAG, "mCountOneScreen = " + mCountOneScreen
                    + " ,mChildWidth = " + mChildWidth);
        }
        // 初始化第一屏幕的元素
        initFirstScreenChildren(mAdapter.getCount());
    }

    public void initFirstScreenChildren(int mCountOneScreen) {
        mContainer = (LinearLayout) getChildAt(0);
        mContainer.removeAllViews();
        mViewPos.clear();

        for (int i = 0; i < mCountOneScreen; i++) {
            View view = mAdapter.getView(i, null, mContainer);
            view.setOnClickListener(this);
            mContainer.addView(view);
            mViewPos.put(view, i);
            mCurrentIndex = i;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int scrollX = getScrollX();
                if (scrollX >= mChildWidth) {
                    loadNextImg();
                }
                if (scrollX == 0) {
                    loadPreImg();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        if (mOnClickListener != null) {
            for (int i = 0; i < mContainer.getChildCount(); i++) {
                mContainer.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
            mOnClickListener.onClick(v, mViewPos.get(v));
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

}