package com.demo.pager;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.library.R;
import com.android.library.ui.pager.BasePager;
import com.demo.ForgotPwdActivity;
import com.demo.RegisterActivity;

/**
 * Created by Sean.xie on 2015/11/12.
 */
public class ForgotPwdPager extends BasePager implements View.OnClickListener {

    private EditText pwdEt;

    private Button submitBtn;

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pwd, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    private void initUI() {
        pwdEt = (EditText) findViewById(R.id.pwdEt);
        pwdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() < RegisterActivity.PWD_LIMIT) {
                    submitBtn.setEnabled(false);
                } else {
                    submitBtn.setEnabled(true);
                }
            }
        });

        submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);
    }

    /**
     * 重新加载数据
     *
     * @author Sean.xie
     */
    @Override
    public void reload() {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.submitBtn) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            ((ForgotPwdActivity) activity).gotoVCode(pwdEt.getText().toString());
        }
    }

}
