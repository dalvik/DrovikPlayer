package com.android.audiorecorder.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

public class RotateLayout extends ViewGroup implements Rotatable {

    private static final String TAG = "RotateLayout";
    private int mOrientation;
    private View mChild;


    private static final int ANIMATION_SPEED = 270; // 270 deg/sec

    private int mCurrentDegree = 0; // [0, 359]
    private int mStartDegree = 0;
    private int mTargetDegree = 0;

    private boolean mClockwise = false, mEnableAnimation = true;

    private long mAnimationStartTime = 0;
    private long mAnimationEndTime = 0;

    public RotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // The transparent background here is a workaround of the render issue
        // happened when the view is rotated as the device's orientation
        // changed. The view looks fine in landscape. After rotation, the view
        // is invisible.
        setBackgroundResource(android.R.color.transparent);
    }

    @Override
    protected void onFinishInflate() {
        mChild = getChildAt(0);
        mChild.setPivotX(0);
        mChild.setPivotY(0);
    }

    @Override
    protected void onLayout(
            boolean change, int left, int top, int right, int bottom) {
           //   System.out.println("[philichen] onLayout width " + right + "   " +  left + "   "  + TAG);
    //  System.out.println("[philichen] onLayout width " + bottom + "   " +  top + "   "  + TAG);
        int width = right - left;
        int height = bottom - top;

        switch (mOrientation) {
            case 0:
            case 180:
                mChild.layout(0, 0, width, height);
                break;
            case 90:
            case 270:
                mChild.layout(0, 0, height, width);
                break;
        }
    }

    @Override
    protected synchronized void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int w = 0, h = 0;
        switch(mOrientation) {
            case 0:
            case 180:
                measureChild(mChild, widthSpec, heightSpec);
                w = mChild.getMeasuredWidth();
                h = mChild.getMeasuredHeight();
                break;
            case 90:
            case 270:
                measureChild(mChild, heightSpec, widthSpec);
                w = mChild.getMeasuredHeight();
                h = mChild.getMeasuredWidth();
                break;
        }
        setMeasuredDimension(w, h);

        switch (mOrientation) {
            case 0:
                mChild.setTranslationX(0);
                mChild.setTranslationY(0);
                break;
            case 90:
                mChild.setTranslationX(0);
                mChild.setTranslationY(h);
                break;
            case 180:
                mChild.setTranslationX(w);
                mChild.setTranslationY(h);
                break;
            case 270:
                mChild.setTranslationX(w);
                mChild.setTranslationY(0);
                break;
        }
        Log.d(TAG, "===> mOrientation = " + mOrientation);
        mChild.setRotation(-mOrientation);
    }

    // Rotate the view counter-clockwise
    public void setOrientation(int orientation, boolean animation) {
        orientation = orientation % 360;
        if (mOrientation == orientation) return;
        mOrientation = orientation;
        requestLayout();
    }

    public void setRotate(int degree, boolean animation){
        mEnableAnimation = animation;
        degree = degree >= 0 ? degree % 360 : degree % 360 + 360;
        if (degree == mTargetDegree) return;

        mTargetDegree = degree;
        if (mEnableAnimation) {
            mStartDegree = mCurrentDegree;
            mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();

            int diff = mTargetDegree - mCurrentDegree;
            diff = diff >= 0 ? diff : 360 + diff; // make it in range [0, 359]

            // Make it in range [-179, 180]. That's the shorted distance between the
            // two angles
            diff = diff > 180 ? diff - 360 : diff;

            mClockwise = diff >= 0;
            mAnimationEndTime = mAnimationStartTime
                    + Math.abs(diff) * 1000 / ANIMATION_SPEED;
        } else {
            mCurrentDegree = mTargetDegree;
        }
        Log.d(TAG, "mStartDegree=" + mStartDegree + "  mTargetDegree = "+ mTargetDegree);
        /*LayoutTransition layoutTransition = new LayoutTransition();
        ObjectAnimator animator = ObjectAnimator.ofFloat(null, "rotationX", mStartDegree, mTargetDegree).setDuration(layoutTransition.getDuration(LayoutTransition.CHANGE_APPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, animator);
        setLayoutTransition(layoutTransition);*/
       /* RotateAnimation rotateAnimation = new RotateAnimation(0, 90, getX()/2, getY()/2);
        rotateAnimation.setDuration(300);
        LayoutAnimationController controller = new LayoutAnimationController(rotateAnimation);
        setLayoutAnimation(controller);
        controller.start();
        startLayoutAnimation();*/
        /*requestLayout();
        invalidate();*/
    }
}
