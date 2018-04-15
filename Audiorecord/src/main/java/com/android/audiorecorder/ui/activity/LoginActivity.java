package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.dao.UserDao;
import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.resp.RegisterResp;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.UserManager;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.audiorecorder.utils.ErrorCode;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ToastUtils;
import com.android.library.utils.MD5;
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
    private View registerPwdRl;

    private UserManager mUserManager;
    private UserDao mUserDao;
    private int whatLogin;
    private int whatRegister;
    private int whatThirdLogin;
    private int whatVCode;

    private String mobile;

    //private ThirdLoginReq req = new ThirdLoginReq();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);
        initUI();
        mUserManager = new UserManager(this);
        mUserDao = new UserDao();
    }

    @Override
    protected boolean initIntent() {
        mobile = getIntent().getStringExtra(ActivityUtil.INTENT_MOBILE);
        if (TextUtils.isEmpty(mobile)) {
            try {
            	mobile = StringUtils.getString(activity, StringUtils.KEY_USER_NAME, "");
                //mobile = AppApplication.getUser().mobile;
                if (TextUtils.isEmpty(mobile)) {
                    mobile = "";
                }
                mobile = "sky@abc.com";
            } catch (Exception e) {
                mobile = "";
            }
        }
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
        registerPwdRl = findViewById(R.id.registerPwdRl);
        mobileEt.setText(mobile);
        mobileEt.setSelection(mobile.length());
        checkMobile(mobileEt.getText().toString());
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        LogUtil.d(TAG, "==> onHandleBiz what : " + what + " result : " + result);
        if (whatLogin == what) {
            switch (result) {
                case UserManager.RESULT_SUCCESS:
                    BaseData<UserResp> data = (BaseData<UserResp>) obj;
                    if(data.code==ErrorCode.SUCCESS){
                        ToastUtils.showToast(R.string.login_success);
                        mUserDao.insertOrUpdateUser(activity, data.data);
                        String account = null;
                        if(!TextUtils.isEmpty(data.data.email)){
                        	account = data.data.email;
                        } else if(!TextUtils.isEmpty(data.data.telephone)) {
                        	account = data.data.telephone;
                        } else {
                        	account = String.valueOf(data.data.userCode);
                        }
                        StringUtils.putValue(activity, StringUtils.KEY_USER_NAME, account);
                        StringUtils.putValue(activity, StringUtils.KEY_USER_ID, "");
                        StringUtils.putValue(activity, StringUtils.KEY_USER_NAME, "");
                        StringUtils.putValue(activity, StringUtils.KEY_USER_PASSWORD, "");
                        StringUtils.putValue(activity, StringUtils.KEY_USER_LOGIN_STATUS, "1");
                        sendBroadcast(new Intent(StringUtils.ACTION_USER_LOGIN));
                        ActivityUtil.gotoMainActivity(activity, true);
                        finish();
                    } else if(data.code == ErrorCode.USER_NOT_ACTIVE){
                        //ToastUtils.showToast(getString(R.string.login_not_active, data.data.email));
                    } else {
                        showToast(data.code);
                    }
                    break;
                case UserManager.FAIL_USERNAME_OR_PWD_ERROR:
                default:
                    return false;
            }
        } else if (whatThirdLogin == what) {
            switch (result) {
                case UserManager.RESULT_SUCCESS:
                    //BaseData<UserResp> data = (BaseData<UserResp>) obj;
                    //AppApplication.setSid(data.sid);
                    //AppApplication.setUser(data.data);
                    ActivityUtil.gotoMainActivity(activity, true);
                    finish();
                    break;
                case UserManager.FAIL_THIRD_ON_REGISTER:
                    //ActivityUtil.gotoThirdRegisterActivity(activity, req);
                    finish();
                    break;
                default:
                    return false;
            }
        } else if (whatRegister == what) {
            switch (result) {
                case UserManager.RESULT_SUCCESS:
                    registerPwdRl.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);
                    loginBtn.setText(R.string.login);
                    registerBtn.setVisibility(View.VISIBLE);
                    registerBtn.setText(R.string.register);
                    BaseData<RegisterResp> baseData = (BaseData<RegisterResp>) obj;
                    showToast(baseData.data.status);
                    if(baseData.data.status == ErrorCode.USER_NOT_ACTIVE){
                        ActivityUtil.gotoRegisterActivity(this, mobileEt.getText().toString(), RegisterActivity.PAGER_EMAIL);
                    }
                    break;
                default:
                    return false;
            }
        }else if (whatVCode == what) {
            switch (result) {
                case UserManager.RESULT_SUCCESS:
                    ActivityUtil.gotoForgotPwdActivity(this, mobileEt.getText().toString());
                    finish();
                    break;
                default:
                    return false;
            }
        }
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
        setEnableBtn(true);
    }

    private void setEnableBtn(boolean enable) {
        loginBtn.setEnabled(enable);
        registerBtn.setEnabled(enable);
    }
    
    /*private void showToast(int status){
        if(status == ErrorCode.USER_ACTIVED){
            ToastUtils.showToast(R.string.login_registered);
        } else if(status == ErrorCode.USER_NOT_ACTIVE){
            //ToastUtils.showToast(R.string.login_registered_unactive);
        } else if(status == ErrorCode.USER_LOCKED){
            ToastUtils.showToast(R.string.login_locked);
        } else if(status == ErrorCode.USER_DESTORY){
            ToastUtils.showToast(R.string.login_destory);
        } else if(status == ErrorCode.USER_NOT_EXISTS || status == ErrorCode.USER_LOGIN_ERROR){
            ToastUtils.showToast(R.string.login_user_not_exist);
        } else {
            ToastUtils.showToast(R.string.unknow_error);
        }
    }*/

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.registerBtn) {
            String account = mobileEt.getText().toString();
            if (getString(R.string.submit).equals(registerBtn.getText())) {
                if(!TextUtils.mactchMoile(account) && !TextUtils.mactchEmail(account) && !(TextUtils.mactchNumberic(account) && account.length()==9)){
                    ToastUtils.showToast(R.string.login_mobile_error);
                    setEnableBtn(false);
                    return;
                }
                EditText registerPwdEt = null;//(EditText) registerPwdRl.findViewById(R.id.pwdEt2);
                EditText registerRePwdEt = (EditText) registerPwdRl.findViewById(R.id.repwdEt);
                String pwd = registerPwdEt.getText().toString();
                String repwd = registerRePwdEt.getText().toString();
                if(pwd.length()<=0 || repwd.length()<=0){
                    ToastUtils.showToast(R.string.login_pwd_empty);
                    return;
                }
                if(!pwd.equalsIgnoreCase(repwd)){
                    ToastUtils.showToast(R.string.login_pwd_not_equal);
                    return;
                }
                doRegister(account,"vCode", 1, pwd);
                /*if(TextUtils.mactchMoile(account) || TextUtils.mactchEmail(account) || (TextUtils.mactchNumberic(account) && account.length()==9)){
                } else {
                }*/
            } else {
                registerPwdRl.setVisibility(View.VISIBLE);
                EditText registerPwdEt = (EditText) findViewById(R.id.pwdEt);
                registerPwdEt.requestFocus();
                loginBtn.setVisibility(View.GONE);
                registerBtn.setText(R.string.submit);
            }
        } else if (id == R.id.loginBtn) {
            if (getString(R.string.submit).equals(loginBtn.getText())) {
                doLogin();
            } else {
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
        whatLogin = mUserManager.login(mobileEt.getText().toString(), MD5.toMD5String(pwd));
    }

    private void doRegister(String account, String code, int type, String password){
        showWaitingDialog();
        whatRegister = mUserManager.register(account, code, type, MD5.toMD5String(password));
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
                return;
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
