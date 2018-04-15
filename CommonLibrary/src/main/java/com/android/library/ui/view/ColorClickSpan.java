package com.android.library.ui.view;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class ColorClickSpan extends ClickableSpan {
    private boolean mIsPressed;
    private int mNormalTextColor;
    private int mPressedTextColor;

    private OnSpanClickListener listener;

    public ColorClickSpan(int textColor) {
        this(textColor, textColor);
    }

    public ColorClickSpan(int textColor, OnSpanClickListener listener) {
        this(textColor, textColor, listener);
    }

    public ColorClickSpan(int normalTextColor, int pressedTextColor) {
        this(normalTextColor, pressedTextColor, null);
    }

    public ColorClickSpan(int normalTextColor, int pressedTextColor, OnSpanClickListener listener) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        this.listener = listener;
    }

    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        paint.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
        paint.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        if (listener != null) {
            listener.onClick();
        }
    }

    public static interface OnSpanClickListener {
        void onClick();
    }

    public static class LinkTouchMovementMethod extends LinkMovementMethod {
        private ColorClickSpan mPressedSpan;

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
                            spannable.getSpanEnd(mPressedSpan));
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                ColorClickSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            } else {
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return true;
        }

        ColorClickSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ColorClickSpan[] link = spannable.getSpans(off, off, ColorClickSpan.class);
            ColorClickSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        }

    }
}
