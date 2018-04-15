package com.android.library.ui.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.library.R;

public class HFRecyclerView extends RecyclerView {

	/*------------------ 常量 begin ------------------*/
    //类型
    public static final int TYPE_GRID = 0;
    public static final int TYPE_STAGGER = 1;
    //方向
    public static final int ORIENTATION_VERTICAL = 0;
    public static final int ORIENTATION_HORIZONTAL = 1;
    /*------------------ 常量 end ------------------*/

    //类型、方向、列数
    private int type = TYPE_GRID;
    private int orientation = ORIENTATION_VERTICAL;
    private int column = 1;

    //分割线
    private int dividerSize = 0;
    private int dividerColor = Color.BLACK;
    private Drawable dividerDrawable = null;

    //动画
    private boolean isDefaultAnimatorOpen = false;

    private boolean move = false;
    private int mIndex = 0;

    private Context mContext;
    private LayoutManager mLayoutManager;
    private HFItemDecoration mItemDecoration;

    public HFRecyclerView(Context context) {
        this(context, null);
    }

    public HFRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HFRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
    	super(context, attrs, defStyleAttr);
    	mContext = context;
    	/*================== 获取自定义属性 begin ==================*/
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HFRecyclerView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.HFRecyclerView_hfr_type) {
                type = typedArray.getInt(attr, 0);
            } else if (attr == R.styleable.HFRecyclerView_hfr_orientation) {
                orientation = typedArray.getInt(attr, 0);
            } else if (attr == R.styleable.HFRecyclerView_hfr_column) {
                column = typedArray.getInt(attr, 1);
            } else if (attr == R.styleable.HFRecyclerView_hfr_divider_height) {
                dividerSize = (int) typedArray.getDimension(attr, 0f);
            } else if (attr == R.styleable.HFRecyclerView_hfr_divider_drawable) {
                dividerDrawable = typedArray.getDrawable(attr);
            } else if (attr == R.styleable.HFRecyclerView_hfr_divider_color) {
                dividerColor = typedArray.getColor(attr, Color.BLACK);
            } else if (attr == R.styleable.HFRecyclerView_hfr_default_animator_open) {
                isDefaultAnimatorOpen = typedArray.getBoolean(attr, false);
            }
        }
        typedArray.recycle();
        /*================== 获取自定义属性 end ==================*/
        init();
    }
    
    private void init() {
        //1、设置RecyclerView的类型和方向
        switch (type) {
            case TYPE_GRID:
                switch (orientation) {
                    case ORIENTATION_VERTICAL:
                        mLayoutManager = new GridLayoutManager(mContext, column);
                        break;
                    case ORIENTATION_HORIZONTAL:
                        mLayoutManager = new GridLayoutManager(mContext, column, GridLayoutManager.HORIZONTAL, false);
                        break;
                }
                break;
            case TYPE_STAGGER:
                switch (orientation) {
                    case ORIENTATION_VERTICAL:
                        mLayoutManager = new StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.VERTICAL);
                        break;
                    case ORIENTATION_HORIZONTAL:
                        mLayoutManager = new StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.HORIZONTAL);
                        break;
                }
                break;
        }
        this.setLayoutManager(mLayoutManager);

        //2、设置RecyclerView的分割线样式
        this.removeItemDecoration(mItemDecoration);
        mItemDecoration = new HFItemDecoration(mContext, orientation, dividerSize, dividerColor, dividerDrawable);
        this.addItemDecoration(mItemDecoration);

        //3、设置默认动画是否开启
        if (!isDefaultAnimatorOpen) {
            //关闭默认动画
            closeItemAnimator();
        } else {
            //打开默认动画
            openItemAnimator();
        }

        //4、设置滚动监听（用于平滑滚动）
        addOnScrollListener(new RecyclerViewListener());

    }

    public void notifyViewChanged() {
        init();
        //重新设置布局管理器后需要设置适配器
        Adapter adapter = this.getAdapter();
        if (adapter != null)
            this.setAdapter(adapter);
    }


    public void smoothMoveToPosition(int position) {
        if (type != TYPE_GRID) {
            return;
        }

        if (position < 0 || position >= getAdapter().getItemCount()) {
            Log.e("CSDN_LQR", "超出范围了");
            return;
        }
        mIndex = position;
        stopScroll();

        GridLayoutManager glm = (GridLayoutManager) mLayoutManager;
        int firstItem = glm.findFirstVisibleItemPosition();
        int lastItem = glm.findLastVisibleItemPosition();
        if (position <= firstItem) {
            this.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            int top = this.getChildAt(position - firstItem).getTop();
            this.smoothScrollBy(0, top);
        } else {
            this.smoothScrollToPosition(position);
            move = true;
        }

    }

    public void moveToPosition(int position) {
        if (type != TYPE_GRID) {
            return;
        }

        if (position < 0 || position >= getAdapter().getItemCount()) {
            Log.e("CSDN_LQR", "超出范围了");
            return;
        }
        mIndex = position;
        stopScroll();

        GridLayoutManager glm = (GridLayoutManager) mLayoutManager;
        int firstItem = glm.findFirstVisibleItemPosition();
        int lastItem = glm.findLastVisibleItemPosition();
        if (position <= firstItem) {
            this.scrollToPosition(position);
        } else if (position <= lastItem) {
            int top = this.getChildAt(position - firstItem).getTop();
            this.scrollBy(0, top);
        } else {
            this.scrollToPosition(position);
            move = true;
        }

    }

    class RecyclerViewListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (mOnScrollListenerExtension != null) {
                mOnScrollListenerExtension.onScrollStateChanged(recyclerView, newState);
            }

            if (type != TYPE_GRID) {
                return;
            }
            GridLayoutManager glm = (GridLayoutManager) mLayoutManager;

            if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                move = false;
                int n = mIndex - glm.findFirstVisibleItemPosition();
                if (0 <= n && n < HFRecyclerView.this.getChildCount()) {
                    int top = HFRecyclerView.this.getChildAt(n).getTop();
                    HFRecyclerView.this.smoothScrollBy(0, top);
                }

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (mOnScrollListenerExtension != null) {
                mOnScrollListenerExtension.onScrolled(recyclerView, dx, dy);
            }

            if (type != TYPE_GRID) {
                return;
            }
            GridLayoutManager glm = (GridLayoutManager) mLayoutManager;
            if (move) {
                move = false;
                int n = mIndex - glm.findFirstVisibleItemPosition();
                if (0 <= n && n < HFRecyclerView.this.getChildCount()) {
                    int top = HFRecyclerView.this.getChildAt(n).getTop();
                    HFRecyclerView.this.scrollBy(0, top);
                }
            }
        }
    }

    private OnScrollListenerExtension mOnScrollListenerExtension;

    public OnScrollListenerExtension getOnScrollListenerExtension() {
        return mOnScrollListenerExtension;
    }

    public void setOnScrollListenerExtension(OnScrollListenerExtension onScrollListenerExtension) {
        mOnScrollListenerExtension = onScrollListenerExtension;
    }

    public interface OnScrollListenerExtension {
        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }

    class HFItemDecoration extends ItemDecoration {
        private Context mContext;
        private int mOrientation;
        private int mDividerSize = 0;
        private int mDividerColor = Color.BLACK;
        private Drawable mDividerDrawable;
        private Paint mPaint;

        public HFItemDecoration(Context context, int orientation, int dividerSize, int dividerColor, Drawable dividerDrawable) {
            mContext = context;
            mOrientation = orientation;
            mDividerSize = dividerSize;
            mDividerColor = dividerColor;
            mDividerDrawable = dividerDrawable;

            //绘制纯色分割线
            if (dividerDrawable == null) {
                //初始化画笔(抗锯齿)并设置画笔颜色和画笔样式为填充
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setColor(mDividerColor);
                mPaint.setStyle(Paint.Style.FILL);
                //绘制图片分割线
            } else {
                //如果没有指定分割线的size，则默认是图片的厚度
                if (mDividerSize == 0) {
                    if (mOrientation == ORIENTATION_VERTICAL) {
                        mDividerSize = dividerDrawable.getIntrinsicHeight();
                    } else {
                        mDividerSize = dividerDrawable.getIntrinsicWidth();
                    }
                }
            }

        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, State state) {
            //纵向列表画横线，横向列表画竖线
            if (mOrientation == ORIENTATION_VERTICAL) {
                drawHorientationDivider(c, parent, state);
            } else {
                drawVerticalDivider(c, parent, state);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            if (mOrientation == ORIENTATION_VERTICAL) {
                outRect.set(0, 0, 0, mDividerSize);
            } else {
                outRect.set(0, 0, mDividerSize, 0);
            }
        }

        private void drawHorientationDivider(Canvas c, RecyclerView parent, State state) {
            //得到分割线的四个点：左、上、右、下
            //画横线时左右可以根据parent得到
            int left = parent.getPaddingLeft();
            int right = parent.getMeasuredWidth() - parent.getPaddingRight();

            //上下需要根据每个孩子控件计算
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + dividerSize;
                //得到四个点后开始画
                if (mDividerDrawable == null) {
                    c.drawRect(left, top, right, bottom, mPaint);
                } else {
                    mDividerDrawable.setBounds(left, top, right, bottom);
                    mDividerDrawable.draw(c);
                }
            }
        }

        private void drawVerticalDivider(Canvas c, RecyclerView parent, State state) {
            //画竖线时上下可以根据parent得到
            int top = parent.getPaddingTop();
            int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();

            //左右需要根据孩子控件计算
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                int left = child.getRight() + params.rightMargin;
                int right = left + dividerSize;
                //得到四个点后开始画
                if (mDividerDrawable == null) {
                    c.drawRect(left, top, right, bottom, mPaint);
                } else {
                    mDividerDrawable.setBounds(left, top, right, bottom);
                    mDividerDrawable.draw(c);
                }
            }
        }
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
    }

    public Drawable getDividerDrawable() {
        return dividerDrawable;
    }

    public void setDividerDrawable(Drawable dividerDrawable) {
        this.dividerDrawable = dividerDrawable;
    }

    public boolean isDefaultAnimatorOpen() {
        return isDefaultAnimatorOpen;
    }

    public void openItemAnimator() {
        isDefaultAnimatorOpen = true;
        this.getItemAnimator().setAddDuration(120);
        this.getItemAnimator().setChangeDuration(250);
        this.getItemAnimator().setMoveDuration(250);
        this.getItemAnimator().setRemoveDuration(120);
        ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(true);
    }

    public void closeItemAnimator() {
        isDefaultAnimatorOpen = false;
        this.getItemAnimator().setAddDuration(0);
        this.getItemAnimator().setChangeDuration(0);
        this.getItemAnimator().setMoveDuration(0);
        this.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public LayoutManager getLayoutManager() {
        return mLayoutManager;
    }
}
