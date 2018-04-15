package com.demo;

import android.os.Bundle;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.utils.TextUtils;
import com.demo.pager.ForgotPwdPager;
import com.demo.pager.ForgotVCodePager;

public class ForgotPwdActivity extends BaseCompatActivity {

    ForgotPwdPager pwdPager;
    ForgotVCodePager vCodePager;
    String mobile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_layout_ui_register);
        setTitle(R.string.gorget_pwd);
        initUI();
    }


    /**
     * 初始化Intent 参数
     *
     * @return
     */
    @Override
    protected boolean initIntent() {
        //mobile = getIntent().getStringExtra(ActivityUtil.INTENT_MOBILE);
        if (TextUtils.isEmpty(mobile)) {
            return false;
        }
        return true;
    }

    protected void initUI() {
        pwdPager = new ForgotPwdPager();
        vCodePager = new ForgotVCodePager();
        replaceFragment(R.id.container, pwdPager);
    }

    public void gotoVCode(String pwd) {
        vCodePager.setMobile(mobile);
        vCodePager.setPwd(pwd);
        replaceFragment(R.id.container, vCodePager);
    }

}
