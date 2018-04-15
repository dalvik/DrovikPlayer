package com.demo.pager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.dialog.ConfirmDialog;
import com.android.library.ui.pager.BasePager;
import com.android.library.ui.utils.DialogUtils;

import java.util.Calendar;


public class RegisterModifyPager extends BasePager implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    protected TextView birthdayEt;
    protected RadioGroup sexRg;
    protected int sex;
    protected long birthday;
    private EditText pwdEt;
    private CheckBox eyeCk;
    private Button submitBtn;
    private Dialog datePickerDialog;
    //private RegisterManager setPwdManager;
    private int whatSetPwd;
    private String mobile;

    @Override
    protected boolean onHandleBiz(int what, int result, int arg2, Object obj) {
        if (what == whatSetPwd) {
            /*switch (result) {
                case RegisterManager.RESULT_SUCCESS:
                    BaseData<UserResp> data = (BaseData<UserResp>) obj;
                    AppApplication.setSid(data.sid);
                    AppApplication.setUser(data.data);
                    ActivityUtil.gotoMainActivity(activity, true);
                    activity.finish();
                    break;
                case RegisterManager.FAIL_USER_PWD_READY_SET:
                    activity.finish();
                    AppApplication.deleteUser();
                default:
                    return false;
            }*/
        }
        return true;
    }

    @Override
    public void reload() {

    }

    @Override
    public View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_modify, null);
        initUI(view);
        return view;
    }

    protected void initUI(View view) {
        //密码
        pwdEt = (EditText) view.findViewById(R.id.pwdEt);
        // 密码可见
        eyeCk = (CheckBox) view.findViewById(R.id.eyeCk);
        eyeCk.setOnCheckedChangeListener(this);
        //生日
        birthdayEt = (TextView) view.findViewById(R.id.birthdayEt);
        birthdayEt.setOnClickListener(this);
        //性别
        sexRg = (RadioGroup) view.findViewById(R.id.sexRg);
        sexRg.setOnCheckedChangeListener(this);
        //提交
        submitBtn = (Button) view.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);

        datePickerDialog = DialogUtils.newDatePickerDialog(activity, 1990, 1, 1, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthdayEt.setText(activity.getString(R.string.birthday, year, monthOfYear + 1, dayOfMonth));
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                calendar.get(Calendar.MILLISECOND);
                birthday = calendar.getTimeInMillis();
                calendar.getTime().getTime();
                System.currentTimeMillis();
            }
        });


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setPwdManager = new RegisterManager(this);
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.submitBtn:
                String pwd = pwdEt.getText().toString();
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtils.showToast(R.string.register_pwd_empty);
                    return;
                } else {
                    int len = pwd.length();
                    if (len < RegisterActivity.PWD_LIMIT) {
                        ToastUtils.showToast(R.string.register_pwd_empty);
                        return;
                    }
                }
                if (birthday > 0) {
                    int age = (int) ((System.currentTimeMillis() - birthday) / 1000 / 3600 / 24 / 365);
                    if (age < 18) {
                        ToastUtils.showToast(R.string.register_age_young);
                        return;
                    } else if (age > 80) {
                        ToastUtils.showToast(R.string.register_age_old);
                        return;
                    }
                } else {
                    ToastUtils.showToast(R.string.register_birthday_incorrect);
                    return;
                }
                if (sex == 0) {
                    ToastUtils.showToast(R.string.register_sex_empty);
                    return;
                }
                showSexConfirmDlg();
                break;
            case R.id.birthdayEt:
                datePickerDialog.show();
                break;
        }*/
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        /*switch (checkedId) {
            case R.id.male:
                // 男性
                sex = 1;
                break;
            case R.id.female:
                //女性
                sex = 2;
                break;
        }*/
    }

    /**
     * 确认提示框
     */
    private void showSexConfirmDlg() {
        ConfirmDialog dialog = DialogUtils.newConfirmDialog(R.string.register_sex_confirm, R.string.tip, R.string.register_sex_ok, R.string.register_sex_cancel, new ConfirmDialog.OnResultListener() {
            @Override
            public void onConfirm() {
                showWaitingDialog();
                String pwd = pwdEt.getText().toString();
                //whatSetPwd = setPwdManager.modifyUser(AppApplication.getUser().userId, mobile, sex, birthday, pwd, null);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            pwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            pwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        pwdEt.setSelection(pwdEt.getText().length());
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
