package com.android.library.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.library.R;


public class InputMethod extends LinearLayout implements View.OnClickListener {

    private Callback callback;

    public InputMethod(Context context) {
        super(context);
        init(context);
    }

    public InputMethod(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public InputMethod(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /*@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InputMethod(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }*/

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.input_method, this);
        findViewById(R.id.im_0).setOnClickListener(this);
        findViewById(R.id.im_1).setOnClickListener(this);
        findViewById(R.id.im_2).setOnClickListener(this);
        findViewById(R.id.im_3).setOnClickListener(this);
        findViewById(R.id.im_4).setOnClickListener(this);
        findViewById(R.id.im_5).setOnClickListener(this);
        findViewById(R.id.im_6).setOnClickListener(this);
        findViewById(R.id.im_7).setOnClickListener(this);
        findViewById(R.id.im_8).setOnClickListener(this);
        findViewById(R.id.im_9).setOnClickListener(this);
        findViewById(R.id.im_0).setOnClickListener(this);
        findViewById(R.id.im_done).setOnClickListener(this);
        findViewById(R.id.im_del).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (callback == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.im_1) {
            callback.onClick(Option.ONE);
        } else if (id == R.id.im_2) {
            callback.onClick(Option.TWO);
        } else if (id == R.id.im_3) {
            callback.onClick(Option.THREE);
        } else if (id == R.id.im_4) {
            callback.onClick(Option.FOUR);
        } else if (id == R.id.im_5) {
            callback.onClick(Option.FIVE);
        } else if (id == R.id.im_6) {
            callback.onClick(Option.SIX);
        } else if (id == R.id.im_7) {
            callback.onClick(Option.SEVEN);
        } else if (id == R.id.im_8) {
            callback.onClick(Option.EIGHT);
        } else if (id == R.id.im_9) {
            callback.onClick(Option.NINE);
        } else if (id == R.id.im_0) {
            callback.onClick(Option.ZERO);
        } else if (id == R.id.im_del) {
            callback.onClick(Option.DEL);
        } else if (id == R.id.im_done) {
            callback.onClick(Option.DONE);
        }
    }

    public static enum Option {
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, DEL, DONE;
    }

    public static interface Callback {
        void onClick(Option opt);
    }
}
