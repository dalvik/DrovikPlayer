package com.demo.pager;

import android.os.Bundle;

/**
 * Created by Sean.xie on 2015/11/10.
 */
public class ForgotVCodePager extends VCodePager {

    //RegisterManager registerManager;
    private String pwd;
    private int whatVCode;
    private int whatForgot;

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //registerManager = new RegisterManager(this);
    }

    @Override
    protected void doSubmit() {
        //whatForgot = registerManager.findPwd(mobile, letters.toString(), pwd);
    }

    @Override
    protected void sendVCode() {
        showWaitingDialog();
        //whatVCode = registerManager.sendVCode(mobile);
    }

    @Override
    protected boolean onHandleBiz(int what, int result, int arg2, Object obj){
        /*if (what == whatVCode) {
            switch (result) {
                case RegisterManager.RESULT_SUCCESS:
                    break;
                case RegisterManager.FAIL_VCODE:
                    lastTime = 1;
                default:
                    return false;
            }
        } else if (what == whatForgot) {
            switch (result) {
                case ThirdRegisterManager.RESULT_SUCCESS:
                    ActivityUtil.gotoLogin(activity,mobile);
                    activity.finish();
                    ToastUtils.showToast(R.string.forgot_find_success);
                    break;
                default:
                    return false;
            }
        }*/
        return true;
    }

}
