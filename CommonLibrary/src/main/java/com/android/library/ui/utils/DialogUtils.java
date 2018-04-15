package com.android.library.ui.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;

import com.android.library.R;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.dialog.ConfirmDialog;
import com.android.library.ui.dialog.DateTimePickerDialog;
import com.android.library.ui.dialog.SelectorListDialog;
import com.android.library.ui.dialog.TextDialog;
import com.android.library.ui.dialog.UpdateDialog;
import com.android.library.ui.dialog.WaitingDialog;


public class DialogUtils {

    private static final String DLG_SPEED = "dlg_speed";
    
    /**
     * 日期选择框
     *
     * @param activity
     * @param year        年
     * @param monthOfYear 月(1 -- 12)
     * @param dayOfMonth  日
     * @param listener
     * @return
     */
    public static Dialog newDatePickerDialog(BaseCommonActivity activity, int year, int monthOfYear, int dayOfMonth, DatePickerDialog.OnDateSetListener listener) {
        return new DatePickerDialog(activity, R.style.UI_Packer_Date, listener, year, monthOfYear, dayOfMonth - 1);
    }

    /**
     * 日期时间选择框
     *
     * @param listener
     * @return
     */
    public static DateTimePickerDialog newDateTimePickerDialog(DateTimePickerDialog.OnSelectedListener listener) {
        return DateTimePickerDialog.newInstance(listener);
    }

    /**
     * 列表选择对话框
     *
     * @param resId
     * @param listener
     * @return
     */

    public static SelectorListDialog newSelectDialog(int resId, SelectorListDialog.OnSelectedListener listener) {
        return SelectorListDialog.newInstance(false, resId, listener);
    }

    /**
     * 显示提示框
     *
     * @param content
     * @return
     */
    public static TextDialog newTipDialog(int content) {
        return TextDialog.newInstance(R.string.tip, content, 0, false);
    }

    /**
     * 显示等待对话框
     *
     * @param contentId
     */
    public static WaitingDialog newWaitingDialog(int contentId) {
        return WaitingDialog.newInstance(contentId);
    }


    /**
     * 显示确认对话框
     *
     * @param contentId
     * @param titleId
     * @param confirmId
     * @param cancelId
     * @param listener
     * @return
     */
    public static ConfirmDialog newConfirmDialog(int contentId, int titleId, int confirmId, int cancelId, ConfirmDialog.OnResultListener listener) {
        return ConfirmDialog.newInstance(titleId, contentId, confirmId, cancelId, false, listener);
    }
    
    /**
     * 发布极速约
     * @param activity
     * @return
     */
    /*public static SpeedDialog showSpeedDialog(BaseCommonActivity activity) {
        SpeedDialog dialog = SpeedDialog.newInstance();
        dialog.show(activity.getSupportFragmentManager(), DLG_SPEED);
        return dialog;
    }*/

    /**
     * 更新提示框
     *
     * @param content
     * @return
     */
    public static UpdateDialog newUpdateDialog(String content, int confirm, int cancelId, ConfirmDialog.OnResultListener listener) {
        return UpdateDialog.newInstance(R.string.update, content, confirm, cancelId, false, listener);
    }
}
