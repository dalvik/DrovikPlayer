package com.android.library.ui.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.BaseApplication;
import com.android.library.R;


public class ToastUtils {

    private static Toast toast = null;

    /**
     * 自定义Toast显示
     *
     * @param msg 提示的内容
     */
    public static void showToast(String msg, boolean isLong) {
        Toast toast = null;
        Context context = BaseApplication.curContext;
        if (isLong) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        TextView toastView = (TextView) LayoutInflater.from(context).inflate(R.layout.toast_view, null);
        toastView.setText(msg);
        toast.setView(toastView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * Ui线程/非UI线程中显示 Toast
     */
    public static void showToast(int strID) {
        showToast(strID, Gravity.CENTER);
    }

    /**
     * UI线程/非UI线程均可调用 显示 Toast
     */
    public static void showToast(String str) {
        showToast(str, Gravity.CENTER);
    }

    /**
     * UI线程/非UI线程均可调用 显示 Toast
     */
    public static void showToast(final int strID, final int gravity) {
        showToast(BaseApplication.curContext.getString(strID), gravity);
    }

    /**
     * UI线程/非UI线程均可调用 显示 Toast
     */
    public static void showToast(final String str, final int gravity) {
        if (toast == null) {
            try {
                Context context = BaseApplication.curContext;
                toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
                TextView toastView = (TextView) LayoutInflater.from(context).inflate(R.layout.toast_view, null);
                toastView.setText(str);
                toast.setView(toastView);
                if (gravity == Gravity.BOTTOM) {
                    toast.setGravity(gravity, 0, 100);
                } else {
                    toast.setGravity(gravity, 0, 0);
                }
                toast.show();
            } catch (Exception e) {
                BaseApplication.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast(str, gravity);
                    }
                });
            }
        } else {
            toast.cancel();
            toast = null;
            showToast(str, gravity);
        }
    }

    /**
     * 错误
     *
     * @param msgId
     */
    public static void showErrorToast(int msgId) {
        showToast(msgId);
    }

}
