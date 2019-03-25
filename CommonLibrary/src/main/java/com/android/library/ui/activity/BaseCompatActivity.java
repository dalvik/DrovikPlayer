package com.android.library.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.R;

/**
 * 基类沉浸式ActionBarActivity
 * 1、左端返回，可以是文本，可以是图片
 * 2、中间是标题：主标题和副标题
 * 3、右一菜单：图片或者文字，可显示可隐藏
 * 4、右二菜单：图片或者文字，可显示可隐藏
 * 5、整改actionbar可以隐藏
 */
public abstract class BaseCompatActivity extends BaseActionBarActivity {

    private PopupWindow popupMenu = null;

    /**
     * 返回菜单
     */
    private View mBackView;
    private ImageView mBackImageView;
    private TextView mBackTextView;

    /**
     * 标题
     */
    private View mTitleView;
    private TextView mMainTitle;
    private TextView mSubTitle;

    /**
     * 右边左侧按钮
     */
    private View mLeftOptionView;
    private ImageView mLeftOptionImageView;
    private TextView mLeftOptionTextView;

    /**
     * 右边右侧按钮
     */
    private View mRightOptionView;
    private ImageView mRightOptionImageView;
    private TextView mRightOptionTextView;

    /**
     * Actionbar背景色
     * @param resourceId
     */
    protected void setActionBarBackground(int resourceId) {
        mActionBarView.setBackgroundResource(resourceId);
    }
    
    protected void setActionBarBackgroundColor(int colorId, int fontColor){
        mActionBarView.setBackgroundColor(getResources().getColor(colorId));
        initStatusBar(getResources().getColor(fontColor));
    }

    /**
     * 内容背景色
     * @param resourceId
     */
    protected void setContentBackground(int resourceId) {
        mBodyContentView.setBackgroundResource(resourceId);
    }

    protected void setBackgroundColor(int colorId){
        mBodyContentView.setBackgroundColor(colorId);
    }

    /**
     * 返回键按钮操作
     * @param back
     */
    protected void setBackView(TextView back) {
        mBackView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void setBackImageResource(int resourceId) {
        mBackImageView.setImageResource(resourceId);
    }

    protected void setBackBackgroundResource(int resourceId) {
        mBackImageView.setBackgroundResource(resourceId);
    }

    protected void setBackText(int resourceId) {
        mBackTextView.setText(resourceId);
    }

    protected void setOptionView(TextView option) {
        option.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rightOptionBtn();
            }
        });
    }

    /**
     * 主标题字体颜色
     * @param colorId
     */
    public void setTitleColor(int colorId){
        this.mMainTitle.setTextColor(colorId);
    }

    /**
     * 副标题字体颜色
     * @param colorId
     */
    public void setSubTitleColor(int colorId){
        this.mSubTitle.setTextColor(colorId);
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(int title) {
        this.mMainTitle.setText(title);
    }

    public void setTitle(String title) {
        this.mMainTitle.setText(title);
    }

    public void setSubTitle(int title) {
        this.mSubTitle.setText(title);
    }

    public void setSubTitle(String title) {
        this.mSubTitle.setText(title);
    }
    /**
     * 获取标题内容
     * @return
     */
    public String getTitleText() {
        return this.mMainTitle.getText().toString();
    }

    public String getSubTitleText() {
        return this.mSubTitle.getText().toString();
    }

    /**
     * 设置标题栏点击事件
     *
     * @param onClickListener
     */
    public void setTitleClickListener(OnClickListener onClickListener) {
        this.mTitleView.setOnClickListener(onClickListener);
    }

    //设置左侧菜单
    protected void setLeftOptionView(Button optionLeft) {
        mLeftOptionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                leftOptionBtn();
            }
        });
    }

    //设置左侧按钮文本
    protected void setLeftOptionText(int resourceId) {
        mLeftOptionTextView.setText(resourceId);
    }

    //设置左侧按钮显示状态
    protected void setLeftOptionViewVisiable(int visiable) {
        mLeftOptionView.setVisibility(visiable);
    }

    //设置右侧按钮资源
    protected void setRightOptionImageResource(int resourceId) {
        mRightOptionImageView.setBackgroundResource(resourceId);
    }

    //设置右侧按钮文本
    protected void setRightOptionText(int resourceId) {
        mRightOptionTextView.setText(resourceId);
    }

    //设置左侧按钮显示状态
    protected void setRightOptionViewVisiable(int visiable) {
        mRightOptionView.setVisibility(visiable);
    }

    protected void setRightOptionView(Button optionLeft) {
        mRightOptionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                leftOptionBtn();
            }
        });
    }

    /**
     * 右按钮操作键
     */
    public void leftOptionBtn() {
    }

    public void setLeftOptionVisiable(int visiable) {

    }

    /**
     * 右按钮操作键
     */
    public void rightOptionBtn() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void replaceFragment(int id, Fragment fragment) {
        replaceFragment(id, fragment, null);
    }

    protected void replaceFragment(int id, Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(id, fragment, tag);
        // transaction.addToBackStack();
        // 事务提交
        transaction.commitAllowingStateLoss();
    }

    /**
     * 取Fragment
     * @param tag
     * @return
     */
    protected Fragment getFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        return fm.findFragmentByTag(tag);
    }

    @Override
    public void reload() {
    }

     /**
     * 初始化Action Bar
     *
     * @param layout
     */
    @Override
    protected void initActionBar(RelativeLayout layout) {
        enableSlideLayout(false);
        createActionBar(layout);
    }

    @Override
    protected void initFooterBar(RelativeLayout layout) {
        mFooterBarView = addFooterBar(layout);
    }

    private View createActionBar(RelativeLayout layout) {
        // 初始化ActionBar样式
        View view = LayoutInflater.from(this).inflate(R.layout.base_compat_action_bar, layout);
        //back
        mBackView = view.findViewById(R.id.back);
        mBackImageView = (ImageView) view.findViewById(R.id.back_imageview);
        mBackTextView = (TextView) view.findViewById(R.id.back_textview);

        //title
        mTitleView = view.findViewById(R.id.title_view);
        mMainTitle = (TextView) view.findViewById(R.id.title);
        mSubTitle = (TextView) view.findViewById(R.id.sub_title);

        //left option
        mLeftOptionView = view.findViewById(R.id.left_option);
        mLeftOptionImageView = (ImageView) view.findViewById(R.id.left_option_imageview);
        mLeftOptionTextView = (TextView) view.findViewById(R.id.left_option_textview);

        //right option
        mRightOptionView = view.findViewById(R.id.right_option);
        mRightOptionImageView = (ImageView) view.findViewById(R.id.right_option_imageview);
        mRightOptionTextView = (TextView) view.findViewById(R.id.right_option_textview);

        setLeftOptionView(null);
        setRightOptionView(null);
        setBackView(null);
        fullScreen();
        setActionBarBackgroundColor(R.color.colorPrimary, R.color.textColorAccent);
        return view;
    }

    private View addFooterBar(RelativeLayout layout) {
        View view = LayoutInflater.from(this).inflate(R.layout.footer_bar, layout);
        return view;
    }
}
