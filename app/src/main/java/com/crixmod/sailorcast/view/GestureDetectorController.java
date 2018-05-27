package com.crixmod.sailorcast.view;


import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class GestureDetectorController
        implements OnGestureListener
{
    private ScrollViewType currentType = ScrollViewType.NOTHING;
    private GestureDetector mGestureDetector = null;
    private IGestureListener mListener;
    private int mWidth;

    public GestureDetectorController(Context paramContext, IGestureListener listener)
    {
        this.mWidth = paramContext.getResources().getDisplayMetrics().widthPixels;
        mGestureDetector = new GestureDetector(paramContext, this);
        this.mListener  = listener;
    }

    public boolean onDown(MotionEvent paramMotionEvent)
    {
        this.currentType = ScrollViewType.NOTHING;
        return true;
    }

    public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
        return true;
    }

    public void onLongPress(MotionEvent paramMotionEvent) {}

    public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
    {
        int i;
        if ((this.mListener != null) && (paramMotionEvent1 != null) && (paramMotionEvent2 != null)) {

            if (this.currentType != ScrollViewType.NOTHING) {
                if (this.currentType == ScrollViewType.VERTICAL_RIGHT) {
                    this.mListener.onScrollRight(paramFloat2, paramMotionEvent2.getY() - paramMotionEvent1.getY());
                } else if (this.currentType == ScrollViewType.VERTICAL_LEFT) {
                    this.mListener.onScrollLeft(paramFloat2, paramMotionEvent2.getY() - paramMotionEvent1.getY());
                } else if (this.currentType == ScrollViewType.HORIZONTAL) {
                    this.mListener.onScrollHorizontal(paramFloat1, paramMotionEvent2.getX() - paramMotionEvent1.getX());
                }
                return false;
            }

            if (Math.abs(paramFloat2) <= Math.abs(paramFloat1)) {
                this.currentType = ScrollViewType.HORIZONTAL;
                this.mListener.onScrollBegin(this.currentType);
                return false;
            }

            i = this.mWidth / 3;
            if (paramMotionEvent1.getX() <=i ) {
                currentType = ScrollViewType.VERTICAL_LEFT;
                this.mListener.onScrollBegin(this.currentType);
            }
            else if (paramMotionEvent1.getX() > i * 2)
            {
                currentType = ScrollViewType.VERTICAL_RIGHT;
                this.mListener.onScrollBegin(this.currentType);
            }
            else {
                currentType = ScrollViewType.NOTHING;
            }
        }
        return false;
    }

    public void onShowPress(MotionEvent paramMotionEvent) {}

    public boolean onSingleTapUp(MotionEvent paramMotionEvent)
    {
        return false;
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
        return this.mGestureDetector.onTouchEvent(paramMotionEvent);
    }

    public void setGestureListener(IGestureListener paramIGestureListener)
    {
        this.mListener = paramIGestureListener;
    }

    enum ScrollViewType
    {
        NOTHING,
        VERTICAL_LEFT,
        HORIZONTAL,
        VERTICAL_RIGHT
    }

    static interface IGestureListener
    {
        public void onScrollBegin(ScrollViewType paramScrollViewType);
        public void onScrollHorizontal(float paramFloat1, float paramFloat2);
        public void onScrollLeft(float paramFloat1, float paramFloat2);
        public void onScrollRight(float paramFloat1, float paramFloat2);
    }
}

