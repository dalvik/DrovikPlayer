package com.android.audiorecorder.ui.pager;

import android.os.Bundle;

import com.demo.pager.VCodePager;

public class RegisterVCodePager extends VCodePager {

    //protected RegisterManager manager = null;
    int whatVCode;
    int whatRegister;

    /**
     * 初始化Intent 参数
     *
     * @return
     */
    @Override
    protected boolean initIntent() {
        /*mobile = activity.getIntent().getStringExtra(ActivityUtil.INTENT_MOBILE);
        if (TextUtils.isEmpty(mobile)) {
            return false;
        }*/
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //manager = new RegisterManager(this);
        sendVCode();
    }

    @Override
    public void reload() {
    }

    protected void doSubmit() {
        //showWaitingDialog();
        //whatRegister = manager.register(mobile, letters.toString());
    }

    protected void sendVCode() {
        hideWaitingDialog();
        //showWaitingDialog();
        //whatVCode = manager.sendVCode(mobile);
    }

    @Override
    protected boolean onHandleBiz(int what, int result, int arg2, Object obj){
        if (what == whatVCode) {
            /*switch (result) {
                case RegisterManager.RESULT_SUCCESS:
                    break;
                case RegisterManager.FAIL_VCODE:
                    lastTime = 1;
                default:
                    return false;
            }*/
        } else if (what == whatRegister) {
            /*switch (result) {
                case RegisterManager.RESULT_SUCCESS:
                    UserResp user = new UserResp();
                    user.mobile = mobile;
                    user.userId = ((BaseObjectData<Long>) obj).data;
                    AppApplication.setUser(user);
                    ((RegisterActivity) activity).gotoModify(mobile);
                    break;
                case RegisterManager.FAIL_REGEISTERED:
                    activity.finish();
                default:
                    return false;
            }*/
        }
        return true;
    }
}
