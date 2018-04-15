package com.android.audiorecorder.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;

public class CenterSettingUpdateAccountActivity extends BaseCompatActivity implements View.OnClickListener {
    private TextView treamNumTv;
    private TextView qqTv;
    private TextView wxTv;
    private TextView mobileTv;

    private RelativeLayout treamNumRL;
    private RelativeLayout qqRL;
    private RelativeLayout wxRL;
    private RelativeLayout mobileRL;

    //private UserResp userResp;
    private Bundle bundle;

    /**
     * QQ
     */
    private static final int ACCOUNT_TREAMNUM_QQ_CODE = 2;
    /**
     * 微信
     */
    private static final int ACCOUNT_TREAMNUM_WX_CODE = 3;
    /**
     * 手机号
     */
    private static final int ACCOUNT_TREAMNUM_MOBILE_CODE = 4;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_layout_ui_center_setting_account);
        setTitle(R.string.center_setting_account);
        initUI();
    }

    private void initUI() {
        //userResp = AppApplication.getUser();
        qqTv = (TextView) findViewById(R.id.qqTv);
        wxTv = (TextView) findViewById(R.id.wxTv);
        mobileTv = (TextView) findViewById(R.id.mobileTv);
        /*if(TextUtils.isEmpty(userResp.mobile)){
            mobileTv.setText(R.string.center_setting_account_unbounded);
        }else {
            mobileTv.setText(userResp.mobile);
        }*/

        findViewById(R.id.qqRL).setOnClickListener(this);
        findViewById(R.id.wxRL).setOnClickListener(this);
        findViewById(R.id.mobileRL).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.qqRL:
                break;
            case R.id.wxRL:
                break;
            case R.id.mobileRL:
                ActivityUtil.gotoCenterSettingAccountMobileActivity(activity);
                break;
        }*/
    }
}
