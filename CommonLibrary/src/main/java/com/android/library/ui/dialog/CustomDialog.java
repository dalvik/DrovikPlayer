package com.android.library.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class CustomDialog extends Dialog {

    private static int default_width = 300;
    private static int default_height = 300;


    public CustomDialog(Context context, View layout, int style) {
        this(context, default_width, default_height, layout, style);
    }

    public CustomDialog(Context context, int width, int height, View layout, int style) {
        super(context, style);
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        init(layout, metric);

    }
    private void init(View layout, DisplayMetrics metrics) {
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        p.gravity = Gravity.CENTER;
        p.width = metrics.widthPixels * 6/7;
        window.setAttributes(p);
    }

    /**
     * 设置dialog 宽度
     * @Argument width 宽度
     */
    public void setWidth(int width) {
        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = width;
        window.setAttributes(p);
    }

    public void setHeight(int height) {

        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        p.height = height;
        window.setAttributes(p);
    }

}
