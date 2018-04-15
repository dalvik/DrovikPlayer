package com.android.library.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.library.R;
import com.android.library.ui.sidebar.SlideLinearLayout;


/**
 * 带有actionbar的基类：
 * 1、actionbar
 * 2、bodyview
 * 3、reloadview
 * 4、waitview
 */
public abstract class BaseActionBarActivity extends BaseCommonActivity {
    protected boolean mIsNeedAddWaitingView = false;
    /**
     * head view
     */
    protected View mActionBarView;
    private SlideLinearLayout mRootSliedLayout;
    private View mWaitView = null;
    /**
     * body view
     */
    protected View mBodyContentView = null;
    /**
     * reload view
     */
    private View mReloadView = null;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(createRootView(LayoutInflater.from(this).inflate(layoutResID, null)));
        initActionBar((RelativeLayout) findViewById(R.id.action_bar));
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(createRootView(view), params);
        initActionBar((RelativeLayout) findViewById(R.id.action_bar));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(createRootView(view));
        initActionBar((RelativeLayout) findViewById(R.id.action_bar));
    }

    /**
     * 是否需要添加等待框
     * @param isNeedAddWaitingView
     */
    public void addWaitingView(boolean isNeedAddWaitingView) {
        this.mIsNeedAddWaitingView = isNeedAddWaitingView;
    }

    /**
     * 隐藏等待框
     */
    public void hideInnerWaiting() {
        if (mWaitView != null) {
            mWaitView.setVisibility(View.GONE);
        }
        mBodyContentView.setVisibility(View.VISIBLE);
    }

    /**
     * 重新加载数据
     */
    public abstract void reload();

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.fixedly, R.anim.fixedly);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        //overridePendingTransition(R.anim.right_in, R.anim.fixedly);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        //overridePendingTransition(R.anim.right_in, R.anim.fixedly);
    }

    @Override
    protected void onSessionTimeout() {
        super.onSessionTimeout();
        hideWaitingDialog();
        hideInnerWaiting();
    }

    public void showInputMethod() {
        InputMethodManager im = (InputMethodManager) this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!im.isActive() || this.getCurrentFocus() != null) {
            im.showSoftInput(this.getCurrentFocus(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void hideSoftKeyboard() {
        InputMethodManager im = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im.isActive() || this.getCurrentFocus() != null) {
            im.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(), 0);
        }
    }

    protected void onSystemError(){
        super.onSystemError();
        hideWaitingDialog();
        hideInnerWaiting();
    }

    /**
     * 加载失败
     */
    @Override
    protected void onNetError() {
        hideWaitingDialog();
        hideInnerWaiting();
        if (null == mReloadView) {
            mReloadView = LayoutInflater.from(this).inflate(R.layout.inner_reload, null);
            ImageView reloadBtn = (ImageView) mReloadView.findViewById(R.id.reload_btn);
            reloadBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mReloadView.setVisibility(View.GONE);
                    mBodyContentView.setVisibility(View.VISIBLE);
                    if (mIsNeedAddWaitingView) {
                        showInnerWaiting();
                    }
                    reload();
                }
            });
            mRootSliedLayout.addView(mReloadView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        } else {
            mReloadView.setVisibility(View.VISIBLE);
        }
        mBodyContentView.setVisibility(View.GONE);
    }

    /**
     * 初始化Action Bar
     * @param layout
     */
    protected abstract void initActionBar(RelativeLayout layout);

    protected void enableSlideLayout(boolean enabled) {
        mRootSliedLayout.enableSlide(enabled);
    }

    @Override
    protected final boolean onHandleBiz(int what, int result, int arg2, Object obj) {
        hideWaitingDialog();
        hideInnerWaiting();
        return onHandleBiz(what,result,obj);
    }

    protected boolean onHandleBiz(int what,int result,Object obj){
        return true;
    }

    /**
     * 创建根View
     * @param view
     * @return
     */
    private SlideLinearLayout createRootView(View view) {
        mBodyContentView = view;
        mRootSliedLayout = new SlideLinearLayout(this);
        mRootSliedLayout.setBackgroundResource(R.color.base_content_background);
        mRootSliedLayout.setOrientation(LinearLayout.VERTICAL);
        mActionBarView = LayoutInflater.from(this).inflate(R.layout.base_action_bar, null);
        mRootSliedLayout.addView(mActionBarView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mRootSliedLayout.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        if (mIsNeedAddWaitingView) {
            mWaitView = addWaitingView(mRootSliedLayout);
            showInnerWaiting();
        }
        return mRootSliedLayout;
    }

    protected void setActionBarVisiable(int visiable) {
        mActionBarView.setVisibility(visiable);
    }

    /**
     * 添加等待框
     * @param root
     */
    private View addWaitingView(ViewGroup root) {
        View waitView = LayoutInflater.from(this).inflate(R.layout.inner_waiting, null);
        root.addView(waitView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return waitView;
    }

    /**
     * 显示等待框
     */
    private void showInnerWaiting() {
        if (mWaitView != null) {
            mWaitView.setVisibility(View.VISIBLE);
        }
        mBodyContentView.setVisibility(View.GONE);
    }
}
