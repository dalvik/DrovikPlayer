package com.android.library.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
    protected View mFooterBarView;

    protected int mColorPrimaryNavigation = R.color.base_actionbar_background;
    /**
     * reload view
     */
    private View mReloadView = null;


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(createRootView(LayoutInflater.from(this).inflate(layoutResID, null)));
        initActionBar((RelativeLayout) findViewById(R.id.header_view));
        initFooterBar((RelativeLayout) findViewById(R.id.footer_view));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(createRootView(view), params);
        initActionBar((RelativeLayout) findViewById(R.id.action_bar));
        initFooterBar((RelativeLayout) findViewById(R.id.footer_view));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(createRootView(view));
        initActionBar((RelativeLayout) findViewById(R.id.action_bar));
        initFooterBar((RelativeLayout) findViewById(R.id.footer_view));
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

    /**
     * 初始化FooterBar
     * @param layout
     */
    protected abstract void initFooterBar(RelativeLayout layout);

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

    protected void setColorPrimary(int colorPrimary) {
        this.mColorPrimaryNavigation = colorPrimary;
    }

    protected void fullScreen(int colorId) {//navigation bg
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(colorId));
                //导航栏颜色也可以正常设置
                window.setNavigationBarColor(getResources().getColor(colorId));
            } else {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    protected void initStatusBar(int color) {
        if (color != -1) {
            //设置了状态栏颜色
            addStatusViewWithColor(color);
        }
        if (getActionBar() != null) {
            //要增加内容视图的 paddingTop,否则内容被 ActionBar 遮盖
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ViewGroup rootView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
                rootView.setPadding(0, getStatusBarHeight() + getActionBarHeight(this), 0, 0);
            }
        }
    }

    protected void initStatusBarDrawable(int statusBarDrawable, int defaultStartBarColor) {
        if (statusBarDrawable != -1 && defaultStartBarColor != -1) {
            //设置了状态栏颜色
            addStatusViewWithDrawble(statusBarDrawable, defaultStartBarColor);
        }
        if (getActionBar() != null) {
            //要增加内容视图的 paddingTop,否则内容被 ActionBar 遮盖
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ViewGroup rootView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
                rootView.setPadding(0, getStatusBarHeight() + getActionBarHeight(this), 0, 0);
            }
        }
    }

    protected void addStatusViewWithColor(int statusbarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (false) {
                //要在内容布局增加状态栏，否则会盖在侧滑菜单上
                ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
                //DrawerLayout 则需要在第一个子视图即内容试图中添加padding
                View parentView = rootView.getChildAt(0);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                View statusBarView = new View(this);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
                statusBarView.setBackgroundColor(statusbarColor);
                //添加占位状态栏到线性布局中
                linearLayout.addView(statusBarView, lp);
                //侧滑菜单
                DrawerLayout drawer = (DrawerLayout) parentView;
                //内容视图
                View content = findViewById(0);
                //将内容视图从 DrawerLayout 中移除
                drawer.removeView(content);
                //添加内容视图
                linearLayout.addView(content, content.getLayoutParams());
                //将带有占位状态栏的新的内容视图设置给 DrawerLayout
                drawer.addView(linearLayout, 0);
            } else {
                //设置 paddingTop
                ViewGroup rootView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
                rootView.setPadding(0, getStatusBarHeight(), 0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //直接设置状态栏颜色
                    getWindow().setStatusBarColor(statusbarColor);
                } else {
                    //增加占位状态栏
                    ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
                    View statusBarView = new View(this);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
                    statusBarView.setBackgroundColor(statusbarColor);
                    decorView.addView(statusBarView, lp);
                }
            }
        }
    }


    /**
     * only for home
     * @param resId
     */
    protected void addStatusViewWithDrawble(int resId, int statusbarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (false) {
                //要在内容布局增加状态栏，否则会盖在侧滑菜单上
                ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);
                //DrawerLayout 则需要在第一个子视图即内容试图中添加padding
                View parentView = rootView.getChildAt(0);
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                View statusBarView = new View(this);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());

                statusBarView.setBackgroundResource(resId);
                //添加占位状态栏到线性布局中
                linearLayout.addView(statusBarView, lp);
                //侧滑菜单
                DrawerLayout drawer = (DrawerLayout) parentView;
                //内容视图
                View content = findViewById(0);
                //将内容视图从 DrawerLayout 中移除
                drawer.removeView(content);
                //添加内容视图
                linearLayout.addView(content, content.getLayoutParams());
                //将带有占位状态栏的新的内容视图设置给 DrawerLayout
                drawer.addView(linearLayout, 0);
            } else {
                //设置 paddingTop
                ViewGroup rootView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
                rootView.setPadding(0, getStatusBarHeight(), 0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //直接设置状态栏颜色
                    getWindow().setStatusBarColor(statusbarColor);
                } else {
                    //增加占位状态栏
                    ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
                    View statusBarView = new View(this);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
                    statusBarView.setBackgroundResource(resId);
                    decorView.addView(statusBarView, lp);
                }
            }
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    protected int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        Log.d("CompatToolbar", "statusBar: " + px2dp(statusBarHeight) + "dp");
        return statusBarHeight;
    }

    protected static int getActionBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
            result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return result;
    }

    protected float px2dp(float pxVal) {
        final float scale = getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * 创建根View
     * @param view
     * @return
     */
    private SlideLinearLayout createRootView(View view) {
        mBodyContentView = view;
        mRootSliedLayout = new SlideLinearLayout(this);
        mRootSliedLayout.setContentDescription("root_view");
        mRootSliedLayout.setBackgroundResource(R.color.base_content_background);
        mActionBarView = LayoutInflater.from(this).inflate(R.layout.action_bar, null);
        mFooterBarView =  LayoutInflater.from(this).inflate(R.layout.footer_bar, null);

        RelativeLayout.LayoutParams footerParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        footerParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRootSliedLayout.addView(mFooterBarView, footerParam);

        RelativeLayout.LayoutParams titleBarParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleBarParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mRootSliedLayout.addView(mActionBarView, titleBarParam);

        RelativeLayout.LayoutParams bodyParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bodyParam.addRule(RelativeLayout.BELOW, mActionBarView.getId());
        mRootSliedLayout.addView(view, bodyParam);

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
