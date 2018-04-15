package com.demo;

import android.os.Bundle;

import com.android.library.R;
import com.demo.pager.ThirdRegisterVCodePager;

import java.util.Map;

public class ThirdRegisterActivity extends RegisterActivity {//implements OauthCallback {

    //private ThirdLoginReq req = null;
    //private ThirdRegisterMobilePager mobilePager;

    private Map<String, Object> infos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.thirdRegister);
    }

    /**
     * 初始化Intent 参数
     *
     * @return
     */
    @Override
    protected boolean initIntent() {
        /*try {
            req = (ThirdLoginReq) getIntent().getSerializableExtra(ActivityUtil.INTENT_THIRD_REQ);

            SHARE_MEDIA platform = SHARE_MEDIA.QQ;
            switch (req.type) {
                case ReqConstants.ThirdType.TYPE_QQ:
                    platform = SHARE_MEDIA.QQ;
                    break;
                case ReqConstants.ThirdType.TYPE_SINA:
                    platform = SHARE_MEDIA.SINA;
                    break;
                case ReqConstants.ThirdType.TYPE_WECHAT:
                    platform = SHARE_MEDIA.WEIXIN;
                    break;
            }
            UmengSocial.getPlatformInfo(this, platform, this);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }*/
        return true;
    }

    @Override
    protected void initUI() {
        //mobilePager = new ThirdRegisterMobilePager();
        //vCodePager = new ThirdRegisterVCodePager();
        //modifyPager = new ThirdRegisterModifyPager();
        //replaceFragment(R.id.container, mobilePager);
    }


    /*public ThirdLoginReq getReq() {
        return req;
    }*/

    public Map<String, Object> getInfos() {
        return infos;
    }

    public void gotoVCode(String mobile) {
        //req.mobile = mobile;
        ((ThirdRegisterVCodePager) vCodePager).setMobile(mobile);
        replaceFragment(R.id.container, vCodePager);
    }

    /*@Override
    public void onSuccess(SHARE_MEDIA platform, Bundle bundle) {
    }

    @Override
    public void onSuccess(SHARE_MEDIA platform, Map<String, Object> infos) {
        this.infos = infos;
    }

    @Override
    public void onFailed(SHARE_MEDIA platform, int status) {
    }

    @Override
    public void onError(SocializeException e, SHARE_MEDIA platform) {
    }

    @Override
    public void onCancel(SHARE_MEDIA platform) {
    }*/
}
