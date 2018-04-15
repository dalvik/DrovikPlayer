package com.demo.pager;

import android.os.Bundle;

public class ThirdRegisterVCodePager extends RegisterVCodePager {

    //ThirdRegisterManager thirdManager;

    @Override
    protected boolean initIntent() {
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //thirdManager = new ThirdRegisterManager(this);
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    protected void doSubmit() {
        /*ThirdLoginReq req = ((ThirdRegisterActivity) activity).getReq();
        req.captcha = letters.toString();
        whatRegister = thirdManager.thirdRegister(req);*/
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
        } else if (what == whatRegister) {
            switch (result) {
                case ThirdRegisterManager.RESULT_SUCCESS:
                    BaseData<UserResp> data = (BaseData<UserResp>) obj;
                    if (TextUtils.isEmpty(data.sid)) {
                        UserResp user = new UserResp();
                        user.mobile = mobile;
                        user.userId = data.data.userId;
                        AppApplication.setUser(user);
                        ((RegisterActivity) activity).gotoModify(mobile);
                    } else {
                        AppApplication.setSid(data.sid);
                        AppApplication.setUser(data.data);
                        ActivityUtil.gotoMainActivity(activity, data.data.firstLogin);
                        activity.finish();
                    }
                    break;
                case ThirdRegisterManager.FAIL_REGEISTERED:
                    activity.finish();
                default:
                    return false;
            }
        }*/
        return true;
    }
}
