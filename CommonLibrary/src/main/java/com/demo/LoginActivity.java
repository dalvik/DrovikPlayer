package com.demo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ToastUtils;
import com.android.library.utils.TextUtils;

public class LoginActivity extends BaseCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener{//, OauthCallback {


    private static final int PWD_LIMIT = 6;
    private EditText mobileEt;
    private Button loginBtn;
    private Button registerBtn;
    private Button delBtn;
    private TextView forgotTv;
    private EditText pwdEt;
    private View pwdRl;

    //private LoginManager loginManager;
    //private RegisterManager registerManager;
    private int whatLogin;
    private int whatCheck;
    private int whatThirdLogin;
    private int whatVCode;

    private String mobile;

    //private ThirdLoginReq req = new ThirdLoginReq();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout_login);
        setTitle(R.string.login);
        initUI();
        //loginManager = new LoginManager(this);
        //registerManager = new RegisterManager(this);
    }

    @Override
    protected boolean initIntent() {
        mobile = "13826232558";
       /* mobile = getIntent().getStringExtra(ActivityUtil.INTENT_MOBILE);
        if (TextUtils.isEmpty(mobile)) {
            try {
                mobile = AppApplication.getUser().mobile;
                if (TextUtils.isEmpty(mobile)) {
                    mobile = "";
                }
            } catch (Exception e) {
                mobile = "";
            }
        }*/
        return true;
    }

    private void initUI() {
        mobileEt = (EditText) findViewById(R.id.mobileEt);
        mobileEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    delBtn.setVisibility(View.VISIBLE);
                } else {
                    delBtn.setVisibility(View.GONE);
                }
                checkMobile(s.toString());
            }
        });

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);

        delBtn = (Button) findViewById(R.id.delBtn);
        delBtn.setOnClickListener(this);

        forgotTv = (TextView) findViewById(R.id.forgotTv);
        forgotTv.setOnClickListener(this);

        pwdEt = (EditText) findViewById(R.id.pwdEt);
        pwdEt.setOnEditorActionListener(this);
        pwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPwd(s.toString());
            }
        });
        pwdRl = findViewById(R.id.pwdRl);

        mobileEt.setText(mobile);
        mobileEt.setSelection(mobile.length());
        checkMobile(mobileEt.getText().toString());
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        /*if (whatLogin == what) {
            switch (result) {
                case LoginManager.RESULT_SUCCESS:
                    BaseData<UserResp> data = (BaseData<UserResp>) obj;
                    AppApplication.setSid(data.sid);
                    AppApplication.setUser(data.data);
                    ActivityUtil.gotoMainActivity(activity, data.data.firstLogin);
                    finish();
                    break;
                case LoginManager.FAIL_USERNAME_OR_PWD_ERROR:
                default:
                    return false;
            }
        } else if (whatThirdLogin == what) {
            switch (result) {
                case LoginManager.RESULT_SUCCESS:
                    BaseData<UserResp> data = (BaseData<UserResp>) obj;
                    AppApplication.setSid(data.sid);
                    AppApplication.setUser(data.data);
                    ActivityUtil.gotoMainActivity(activity, data.data.firstLogin);
                    finish();
                    break;
                case LoginManager.FAIL_THIRD_ON_REGISTER:
                    ActivityUtil.gotoThirdRegisterActivity(activity, req);
                    finish();
                    break;
                default:
                    return false;
            }
        } else if (whatCheck == what) {
            switch (result) {
                case RegisterManager.RESULT_SUCCESS:
                    ActivityUtil.gotoRegisterActivity(this, mobileEt.getText().toString());
                    break;
                default:
                    return false;
            }
        } else if (whatVCode == what) {
            switch (result) {
                case RegisterManager.RESULT_SUCCESS:
                    ActivityUtil.gotoForgotPwdActivity(this, mobileEt.getText().toString());
                    finish();
                    break;
                default:
                    return false;
            }
        }*/
        return true;
    }

    private void checkPwd(String pwd) {
        if (pwd.length() < PWD_LIMIT) {
            setEnableBtn(false);
        } else {
            setEnableBtn(true);
        }
    }

    private void checkMobile(String mobile) {
        if (TextUtils.isMobile(mobile)) {
            setEnableBtn(true);
        } else {
            if (mobile.length() == 11) {
                ToastUtils.showToast(R.string.login_mobile_error);
            }
            setEnableBtn(false);
        }
    }

    private void setEnableBtn(boolean enable) {
        loginBtn.setEnabled(enable);
        registerBtn.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.registerBtn) {
            showWaitingDialog();
        } else if (id == R.id.loginBtn) {
            if (getString(R.string.submit).equals(loginBtn.getText())) {//提交
                doLogin();
            } else {// 登录
                pwdEt.requestFocus();
                pwdRl.setVisibility(View.VISIBLE);
                registerBtn.setVisibility(View.GONE);
                loginBtn.setText(R.string.submit);
                checkPwd(pwdEt.getText().toString());
            }
        } else if (id == R.id.delBtn) {
            registerBtn.setVisibility(View.VISIBLE);
            loginBtn.setText(R.string.login);
            mobileEt.setText("");
            pwdEt.setText("");
            pwdRl.setVisibility(View.GONE);
        } else if (id == R.id.forgotTv) {
            String mobile = mobileEt.getText().toString();
            if (!TextUtils.isMobile(mobile)) {
                ToastUtils.showToast(R.string.login_mobile_error);
                return;
            }
            showWaitingDialog();
        }
    }

    private void doLogin() {
        showWaitingDialog();
        String pwd = pwdEt.getText().toString();
        //whatLogin = loginManager.login(mobileEt.getText().toString(), pwd);
    }

    public void onSina(View v) {
        //UmengSocial.loginBySina(this, this);
    }

    public void onWeChat(View v) {
        //UmengSocial.loginByWeChat(this, this);
    }

    public void onQQ(View v) {
        //UmengSocial.loginByQQ(this, this);
    }

    /*@Override
    public void onSuccess(SHARE_MEDIA platform, Bundle bundle) {
        req.accessToken = bundle.getString("access_token");
        req.expiresIn = bundle.getString("expires_in");
        req.openId = bundle.getString("openid");
        switch (platform) {
            case QQ:
                req.type = ReqConstants.ThirdType.TYPE_QQ;
                break;
            case WEIXIN:
                req.type = ReqConstants.ThirdType.TYPE_WECHAT;
                break;
            case SINA:
                req.type = ReqConstants.ThirdType.TYPE_SINA;
                req.openId = bundle.getString("uid");
                break;
            default:
                return;// 未定义平台登录 什么都不做
        }
        showWaitingDialog();
        whatThirdLogin = loginManager.thirdLogin(req);
    }

    @Override
    public void onSuccess(SHARE_MEDIA platform, Map<String, Object> info) {
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


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_NULL:
                break;
            case EditorInfo.IME_ACTION_SEND:
                break;
            case EditorInfo.IME_ACTION_DONE:
                if (pwdEt.getText().length() < PWD_LIMIT) {
                    ToastUtils.showToast(R.string.login_pwd_empty);
                } else {
                    doLogin();
                }
                break;
        }
        return true;
    }
}
