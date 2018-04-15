package com.android.audiorecorder.ui.activity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ToastUtils;


public class CenterSettingUpdatePwdActivity extends BaseCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public static final int NEWPWD_MIN_LENGTH = 6;
    public static final int NEWPWD_MAX_LENGTH = 20;

    private EditText oldPwdEt;
    private CheckBox oldLockIv;
    private EditText newPwdEt;
    private CheckBox newLockIv;

    //private CenterUpdataManager cpManager = null;

    private int updatapwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_layout_ui_center_setting_update_pwd);
        setTitle(R.string.center_setting_updata_pwd);
        //cpManager = new CenterUpdataManager(this);
        initUI();
    }

    private void initUI() {
        oldPwdEt = (EditText) findViewById(R.id.oldPwdEt);
        oldLockIv = (CheckBox) findViewById(R.id.oldLockIv);
        oldLockIv.setOnCheckedChangeListener(this);
        newPwdEt = (EditText) findViewById(R.id.newPwdEt);
        newLockIv = (CheckBox) findViewById(R.id.newLockIv);
        newLockIv.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            //隐藏密码
            oldPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            oldLockIv.setBackgroundResource(R.drawable.certer_setting_pwd_hide);
            newLockIv.setBackgroundResource(R.drawable.certer_setting_pwd_hide);
        }else{
            //显示密码
            oldPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            newPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            oldLockIv.setBackgroundResource(R.drawable.certer_setting_pwd_show);
            newLockIv.setBackgroundResource(R.drawable.certer_setting_pwd_show);
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

    //private RegisterReq registerReq;

    @Override
    protected void setOptionView(TextView option) {
        super.setOptionView(option);
        option.setVisibility(View.VISIBLE);
        option.setText(R.string.finish);

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = oldPwdEt.getText().toString();
                String newPwd = newPwdEt.getText().toString();
                //原始密码或者新密码为空
                if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(newPwd)) {
                    ToastUtils.showToast(R.string.updata_pwd_null);
                } else if (!(newPwd.length() >= NEWPWD_MIN_LENGTH && (newPwd.length() <= NEWPWD_MAX_LENGTH))){
                    //新密码由6-20位
                    ToastUtils.showToast(R.string.updata_pwd_empty);
                } else if (newPwd.equals(oldPwd)) {
                    //新密码与原始密码相同
                    ToastUtils.showToast(R.string.updata_pwd_equal);
                } /*else if (TextUtils.isSerailNumber(newPwd)) {
                    //输入序列数字
                    ToastUtils.showToast(R.string.updata_pwd_serail_number);
                } else if (TextUtils.isSameLetter(newPwd)) {
                    //输入相同字符
                    ToastUtils.showToast(R.string.updata_pwd_same_number);
                } else {
                    updatapwd = cpManager.ChangePwd(oldPwd, newPwd);
                }*/
            }

        });
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        /*if (what == updatapwd) {
            switch (result) {
                case CenterUpdataManager.RESULT_SUCCESS:
                    ToastUtils.showToast(R.string.updata_success);
                    finish();
                    break;
                default:
                    return false;
            }
        }*/
        return true;
    }

}
