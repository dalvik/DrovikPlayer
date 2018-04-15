package com.demo.pager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.library.R;
import com.android.library.ui.pager.BasePager;

public class ThirdRegisterMobilePager extends BasePager implements View.OnClickListener {

    private EditText mobileEt;

    private Button submitBtn;

    //private RegisterManager manager = null;
    private int whatCheck;

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mobile, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //manager = new RegisterManager(this);
        initUI();
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
                if (s.toString().length() == 11) {
                    submitBtn.setEnabled(true);
                } else {
                    submitBtn.setEnabled(false);
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
        /*switch (v.getId()) {
            case R.id.submitBtn:
                showWaitingDialog();
                //whatCheck = manager.isThirdRegisted(mobileEt.getText().toString());
                break;
        }*/
    }

    @Override
    protected boolean onHandleBiz(int what, int result, int arg2, Object obj){
        /*if (what == whatCheck) {
            switch (result) {
                case RegisterManager.RESULT_SUCCESS:
                    ((ThirdRegisterActivity) activity).gotoVCode(mobileEt.getText().toString());
                    break;
                default:
                    return false;
            }
        }*/
        return true;
    }
}
