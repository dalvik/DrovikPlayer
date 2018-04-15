package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.acadiatech.json.JSONObject;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.UserDao;
import com.android.audiorecorder.ui.manager.UserManager;
import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ToastUtils;

/**
 * 
 * @description: 修改用户签名
 * @author: 23536
 * @date: 2016年5月26日 上午10:56:07
 */
public class CenterUpdtSignatureActivity extends BaseCompatActivity implements View.OnClickListener {
    private EditText signatureEt;
    private ImageView delBtn;

    public static final String UPDATE_SIGNATURE = "signature";
    //private CenterUpdataManager centerUpdataManager;
    private int updatesignature;
    private UserResp userResp;
    private UserDao mUserDao;
    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_updata_signature);
        setTitle(R.string.center_update_signature);
        mUserManager = new UserManager(this);
        mUserDao = new UserDao();
        initUI();
    }

    private void initUI() {
        userResp = mUserDao.getUser(this);
        signatureEt = (EditText) findViewById(R.id.signatureEt);
        if (userResp.signature == null) {
            signatureEt.setText(R.string.center_signature);
        } else {
            signatureEt.setText(userResp.signature);
        }
        signatureEt.setSelection(signatureEt.length());

        delBtn = (ImageView) findViewById(R.id.delBtn);
        delBtn.setOnClickListener(this);
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        if (what == updatesignature) {
            switch (result) {
                case UserManager.RESULT_SUCCESS:
                    String signature = signatureEt.getText().toString();
                    Intent intent = new Intent();
                    intent.putExtra(UPDATE_SIGNATURE, signature);
                    setResult(RESULT_OK, intent);
                    userResp.signature = signature;
                    mUserDao.insertOrUpdateUser(this, userResp);
                    ToastUtils.showToast(R.string.updata_success);
                    finish();
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.delBtn:
                signatureEt.setText("");
                break;
        }*/
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

    @Override
    protected void setOptionView(TextView option) {
        option.setVisibility(View.VISIBLE);
        option.setText(R.string.save);

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signature = signatureEt.getText().toString();
                showWaitingDialog();
                JSONObject json = new JSONObject();
                json.put("signature", signature);
                //updatesignature = mUserManager.modifyUser(userResp.userCode, json.toJSONString());
            }
        });
    }
}
