package com.android.audiorecorder.ui.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.library.ui.pager.BasePager;

public class RegisterEmailPager extends BasePager implements OnClickListener {

    private TextView tipTV;
    private Button submitBtn;
    private String mAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_register_email, null);
        initUI(view);
        return view;
    }
    
    @Override
    public void reload() {
    }

    public void setAccount(String email){
        this.mAccount = email;
    }
    
    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onClick(View v) {
        activity.finish();
    }
    
    @Override
    protected boolean onHandleBiz(int what, int result, int arg2, Object obj) {
        return super.onHandleBiz(what, result, arg2, obj);
    }
    
    private void initUI(View view) {
        // 提示
        tipTV = (TextView) view.findViewById(R.id.tipUpdateTv);
        tipTV.setText(activity.getString(R.string.register_email_name_tip, mAccount));

        // 提交
        submitBtn = (Button) view.findViewById(R.id.submitBtn);
        submitBtn.setText(activity.getString(R.string.button_ok));
        submitBtn.setOnClickListener(this);
    }
    
}
