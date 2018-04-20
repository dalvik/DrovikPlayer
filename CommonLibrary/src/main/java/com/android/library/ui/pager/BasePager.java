package com.android.library.ui.pager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.library.BaseApplication;
import com.android.library.R;
import com.android.library.net.base.AbstractData;
import com.android.library.net.base.IDataCallback;
import com.android.library.net.manager.AbstractDataManager;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.utils.ToastUtils;


public abstract class BasePager extends Fragment implements IDataCallback {

    public static final int SUCESS = 1;
    public static final int FAIL = 2;

    public static final String TAG = "BasePager";
    protected BaseCommonActivity activity;
    private LinearLayout rootView = null;
    private View waitView = null;
    private View contentView = null;
    protected boolean isNeedAddWaitingView = false;
    private View reloadView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = new LinearLayout(activity);
            if (!initIntent()) {
                activity.finish();
                return rootView;
            }
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rootView.setLayoutParams(params);
            rootView.setOrientation(LinearLayout.VERTICAL);
            contentView = createView(inflater, savedInstanceState);
            rootView.addView(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            if (isNeedAddWaitingView) {
                waitView = addWaitingView(rootView);
                showInnerWaiting();
            }
        }
        return rootView;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract View createView(LayoutInflater inflater, Bundle savedInstanceState);

    protected View findViewById(int id) {
        return contentView.findViewById(id);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        activity = (BaseCommonActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化Intent 参数
     *
     * @return
     */
    protected boolean initIntent() {
        return true;
    }

    /**
     * 是否需要添加等待框
     *
     * @param isAddWaitingView
     */
    public void addWaitingView(boolean isAddWaitingView) {
        this.isNeedAddWaitingView = isAddWaitingView;
    }

    /**
     * 添加等待框
     *
     * @param root
     */
    private View addWaitingView(ViewGroup root) {
        View waitView = LayoutInflater.from(activity).inflate(R.layout.inner_waiting, null);
        root.addView(waitView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return waitView;
    }

    /**
     * 显示等待框
     */
    public void showInnerWaiting() {
        if (waitView != null) {
            waitView.setVisibility(View.VISIBLE);
        }
        contentView.setVisibility(View.GONE);
    }

    /**
     * 隐藏等待框
     */
    public void hideInnerWaiting() {
        if (waitView != null) {
            waitView.setVisibility(View.GONE);
        }
        contentView.setVisibility(View.VISIBLE);
    }

    /**
     * 重新加载数据
     *
     * @author Sean.xie
     */
    public abstract void reload();


    /**
     * 加载失败
     */
    private void onNetError() {
        if (waitView != null) {
            waitView.setVisibility(View.GONE);
        }
        contentView.setVisibility(View.GONE);
        if (null == reloadView) {
            reloadView = LayoutInflater.from(activity).inflate(R.layout.inner_reload, null);
            ImageView reloadBtn = (ImageView) reloadView.findViewById(R.id.reload_btn);
            reloadBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    reloadView.setVisibility(View.GONE);
                    if (isNeedAddWaitingView) {
                        showInnerWaiting();
                    } else {
                        contentView.setVisibility(View.VISIBLE);
                    }
                    reload();
                }
            });
            rootView.addView(reloadView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        } else {
            reloadView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 系统异常
     */
    private void onSystemError(){
        ToastUtils.showToast(R.string.system_err);
    }

    /**
     * 处理业务逻辑
     * @param what
     * @param result
     * @param arg2
     * @param obj
     * @return 如果返回ture 表示,已经处理相关数据, false父类继续处理(打印未知错误信息)
     */
    protected boolean onHandleBiz(int what, int result, int arg2, Object obj){
        return true;
    }

    /**
     * 回话超时
     */
    protected void onSessionTimeout(){
    }

    @Override
    public final void onCallback(int what, int result, int arg2, Object obj) {
        hideWaitingDialog();
        hideInnerWaiting();
        switch (result) {
            case AbstractDataManager.RESULT_ERROR:
                onNetError();
                break;
            default:
                if (result < 400) {//服务器定200 - 400 服务器内部错误
                    if (result == AbstractDataManager.RESULT_SID_TIMEOUT) {
                        onSessionTimeout();
                    }else {
                        if(BaseApplication.DEBUG) {
                            Log.w(TAG, "==>result : " + result  + " obj : " +  (AbstractData) obj);
                            //ToastUtils.showToast(((AbstractData) obj).desc);
                        }else {
                            onSystemError();
                        }
                    }
                } else {// 大于400 逻辑错误
                    if(!onHandleBiz(what, result, arg2, obj)){
                        Log.w(TAG, "==>result : " + result  + " obj : " + (AbstractData) obj);
                        //ToastUtils.showToast(((AbstractData) obj).desc);
                    }
                }
                break;
        }
    }

    public void hideWaitingDialog() {
        ((BaseCommonActivity) activity).hideWaitingDialog();
    }


    public void showWaitingDialog() {
        ((BaseCommonActivity) activity).showWaitingDialog();
    }

    public void showWaitingDialog(int contentId) {
        ((BaseCommonActivity) activity).showWaitingDialog(contentId);
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        handleActivityResult(requestCode, resultCode, data);
    }

    protected void handleActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public static interface OnSubTabSelected {
        void onSelected(int position);
    }
}
