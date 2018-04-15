package com.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.dialog.ConfirmDialog;

public class CenterSettingActivity extends BaseCompatActivity implements View.OnClickListener {
    private RelativeLayout accountRL;
    private RelativeLayout updataPwdRL;
    private RelativeLayout exitLoginRL;
    private RelativeLayout regardsRL;
    private RelativeLayout renewalRL;
    private TextView tipTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_layout_ui_setting);
        setTitle(R.string.center_personage_setting);
        ininUI();
    }

    private void ininUI() {
        //账户
        findViewById(R.id.accountRL).setOnClickListener(this);
        //修改密码
        findViewById(R.id.updataPwdRL).setOnClickListener(this);
        //关于我请客
        findViewById(R.id.aboutRL).setOnClickListener(this);
        //版本更新
        findViewById(R.id.updateRL).setOnClickListener(this);
        //版本有更新的提示
        tipTv = (TextView) findViewById(R.id.tipUpdateTv);
        //退出账号
        findViewById(R.id.exitRL).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.accountRL) {
            //ActivityUtils.gotoCenterSettingUpdateAccountActivity(activity);
        } else if (id == R.id.updataPwdRL) {
            //ActivityUtil.gotoCenterSettingUpdatePwdActivity(activity);
        } else if (id == R.id.aboutRL) {
            //ActivityUtils.gotoRegardsActivity(activity);
        } else if (id == R.id.updateRL) {
            //ActivityUtils.gotoRenewalActivity(activity);
        } else if (id == R.id.exitRL) {
            showConfirmDialog(R.string.center_setting_sure_exit_login, R.string.center_setting_sure_exit_login_title, R.string.confirm, R.string.cancel, new ConfirmDialog.OnResultListener() {
                @Override
                public void onConfirm() {
                    finishAll();//关闭所有activity
                    //AppApplication.logout();//清除数据
                    Intent intent = new Intent();
                    //intent.setClass(activity, LoginActivity.class);//跳转到登录
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }

                @Override
                public void onCancel() {
                    hideConfirmDialog();
                }
            });
        }
    }

    @Override
    protected void setBackView(TextView back) {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

}
