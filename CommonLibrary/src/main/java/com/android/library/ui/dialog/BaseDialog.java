package com.android.library.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.library.net.base.DataManagerUICallBack;
import com.android.library.net.base.DataManagerUICallBack.UICallBackListener;
import com.android.library.ui.activity.BaseCommonActivity;

/**
 * 
 * @description: 弹出框
 * @author: 23536
 * @date: 2016年1月10日 上午10:25:35
 */
public class BaseDialog extends DialogFragment implements UICallBackListener {

    protected static final String ARGS_STYLE = "STYLE";
    protected static final String ARGS_THEME = "THEME";
    protected static final String ARGS_CANCELABLE = "CANCELABLE";

    protected static final String ARGS_TITLE = "TITLE";
    protected static final String ARGS_CONTENT = "CONTENT";
    protected static final String ARGS_CONFIRM = "CONFIRM";
    protected static final String ARGS_CANCEL = "CANCEL";
    protected static final String ARGS_POSITION = "POSITION";

    protected static final int POSITION_CENTER = 0;
    protected static final int POSITION_TOP = 1;
    protected static final int POSITION_BOTTOM = 2;

    protected BaseCommonActivity activity;

    protected int content;
    protected int title;
    protected int confirm;
    private BaseDialog nextDialog;
    private boolean showNext = true;
    protected DataManagerUICallBack uiCallBack;

    public BaseDialog() {
        super();
        uiCallBack = new DataManagerUICallBack(this);
    }

    /**
     * @param style
     * @param theme
     * @param cancelable
     * @return
     */
    private static BaseDialog newInstance(int style, int theme, boolean cancelable) {
        BaseDialog dialogFragment = new BaseDialog();

        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_STYLE, style);
        bundle.putInt(ARGS_THEME, style);
        bundle.putBoolean(ARGS_CANCELABLE, cancelable);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            int style = args.getInt(ARGS_STYLE, 1);
            int theme = args.getInt(ARGS_THEME, 0);
            switch (style) {
                case STYLE_NORMAL:
                case STYLE_NO_TITLE:
                case STYLE_NO_FRAME:
                case STYLE_NO_INPUT:
                    setStyle(style, theme);//设置样式
                    break;
                default:
                    setStyle(STYLE_NO_TITLE, theme);//设置样式
            }

            boolean cancelable = args.getBoolean(ARGS_CANCELABLE, true);
            setCancelable(cancelable);

            title = args.getInt(ARGS_TITLE, 0);
            content = args.getInt(ARGS_CONTENT, 0);
            confirm = args.getInt(ARGS_CONFIRM, 0);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDialogWidth();
        activity = (BaseCommonActivity) getActivity();
    }

    protected void setText(TextView tv, int resID) {
        if (resID > 0) {
            tv.setText(resID);
        }
    }


    /**
     * 设置对话框宽度
     */
    protected void setDialogWidth() {
        // 设置宽度
        Window window = getDialog().getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = dm.widthPixels * 6 / 7; // 屏幕的6/7
        getDialog().getWindow().setAttributes(params);
    }

    protected void handleResponseData(int what, int result, int arg2, Object obj) {
    }
    
    public void dismissAllowingStateLoss(){
        this.dismiss();
    }
    
    protected void setTitle(int titleId){
    }
    protected void setTitle(String title){
    }
    
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        showNextDialog();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        showNextDialog();
    }
    
    /**
     * 弹出下一个对话框
     */
    private void showNextDialog() {
        if (nextDialog != null && showNext) {
            activity.showDialog(nextDialog);
        }
        showNext = true;
    }

    /**
     * 设置下一个对话框
     *
     * @param nextDialog
     */
    public void setNextDilaog(BaseDialog nextDialog) {
        this.nextDialog = nextDialog;
    }

    /**
     * 不弹框关闭
     */
    public void dismissWithNoNext() {
        showNext = false;
        dismiss();
    }
    
    /**
     * 显示等待框
     */
    protected void showWaitingDialog() {
        activity.showWaitingDialog();
    }

    /**
     * 隐藏等待框
     */
    protected void hideWaitingDialog() {
        activity.hideWaitingDialog();
    }
    
    @Override
    public boolean onHandleBiz(int what, int result, int arg2, Object obj) {
        return false;
    }

    @Override
    public void onHandleUI() {
        hideWaitingDialog();
    }

    /* (non Javadoc) 
     * @title: onNetError
     * @description: TODO
     * @param what 
     * @see com.android.library.net.base.DataManagerUICallBack.UICallBackListener#onNetError(int) 
     */
    @Override
    public void onNetError(int what) {
    }

    /* (non Javadoc) 
     * @title: onSessionTimeOut
     * @description: TODO 
     * @see com.android.library.net.base.DataManagerUICallBack.UICallBackListener#onSessionTimeOut() 
     */
    @Override
    public void onSessionTimeOut() {
    }
}
