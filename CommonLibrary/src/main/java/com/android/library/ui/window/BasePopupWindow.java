package com.android.library.ui.window;

import android.view.WindowManager;
import android.widget.PopupWindow;

import com.android.library.ui.activity.BaseCommonActivity;


public abstract class BasePopupWindow {

    protected final BaseCommonActivity activity;

    protected PopupWindow window;

    protected boolean gotoSharWindow =false;

    public BasePopupWindow(BaseCommonActivity activity) {
        this.activity = activity;
    }

    protected abstract PopupWindow initWindow();

    public PopupWindow getWindow() {
        if(window == null){
            window = initWindow();
        }
        return window;
    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }
}
