package com.demo.pager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.pager.BasePager;
import com.android.library.ui.view.InputMethod;
import com.android.library.utils.TextUtils;

import java.util.ArrayList;

public abstract class VCodePager extends BasePager implements InputMethod.Callback, View.OnClickListener {

    private final int WHAT_RESEND = 10;

    private final short TIME_COUNT = 60;
    protected String mobile;
    protected StringBuilder letters = new StringBuilder();
    protected short lastTime = TIME_COUNT;
    private InputMethod im;
    private TextView tipTV;
    private Button submitBtn;
    private LinearLayout vCodeLL;
    private ArrayList<TextView> vCodeViews = new ArrayList<TextView>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_RESEND:
                    if (--lastTime <= 0) {
                        submitBtn.setEnabled(true);
                        submitBtn.setText(activity.getString(R.string.register_resend, ""));
                    } else {
                        if (!submitBtn.isEnabled()) {
                            submitBtn.setText(activity.getString(R.string.register_resend, "" + lastTime));
                        }
                        handler.sendEmptyMessageDelayed(WHAT_RESEND, 1000);
                    }
                    break;
            }
        }
    };

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_vcode, null);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        // 提示
        tipTV = (TextView) view.findViewById(R.id.tipUpdateTv);
        tipTV.setText(activity.getString(R.string.register_vcode_tip, mobile));


        //键盘
        im = (InputMethod) view.findViewById(R.id.im);
        im.setCallback(this);
        // 提交
        submitBtn = (Button) view.findViewById(R.id.submitBtn);
        submitBtn.setText(activity.getString(R.string.register_resend, "" + lastTime));
        submitBtn.setOnClickListener(this);
        handler.sendEmptyMessage(WHAT_RESEND);
        //验证码
        vCodeLL = (LinearLayout) view.findViewById(R.id.VCodeLL);
        int count = vCodeLL.getChildCount();
        for (int i = 0; i < count; i++) {
            vCodeViews.add((TextView) vCodeLL.getChildAt(i));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void reload() {
    }

    @Override
    public void onClick(InputMethod.Option opt) {
        switch (opt) {
            case DEL:
                delNumber();
                break;
            case DONE:
                resendOrSubmit();
                break;
            default:
                addNumber(opt);
                break;

        }
    }

    /**
     * 删除数字
     */
    private void delNumber() {
        int count = vCodeViews.size();
        TextView tv;
        for (int i = count - 1; i >= 0; i--) {
            tv = vCodeViews.get(i);
            if (!TextUtils.isEmpty(tv.getText())) {
                tv.setText("");
                letters.deleteCharAt(letters.length() - 1);
                break;
            }
        }
        if (lastTime > 0) {
            submitBtn.setEnabled(false);
            submitBtn.setText(activity.getString(R.string.register_resend, "" + lastTime));
        } else {
            submitBtn.setEnabled(true);
            submitBtn.setText(activity.getString(R.string.register_resend, ""));
        }
    }

    /**
     * 输入数字
     *
     * @param opt
     */
    private void addNumber(InputMethod.Option opt) {
        for (TextView tv : vCodeViews) {
            if (TextUtils.isEmpty(tv.getText())) {
                tv.setText("" + opt.ordinal());
                letters.append("" + opt.ordinal());
                break;
            }
        }
        if (letters.length() == vCodeViews.size()) {
            submitBtn.setText(R.string.submit);
            submitBtn.setEnabled(true);
        } else {
            if (lastTime > 0) {
                submitBtn.setEnabled(false);
                submitBtn.setText(activity.getString(R.string.register_resend, "" + lastTime));
            } else {
                submitBtn.setEnabled(true);
                submitBtn.setText(activity.getString(R.string.register_resend, ""));
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.submitBtn) {
            resendOrSubmit();
        }
    }

    protected abstract void doSubmit();

    protected abstract void sendVCode();

    private void resendOrSubmit() {
        if (letters.length() == vCodeViews.size()) {
            // submit
            doSubmit();
        } else {
            // resend
            sendVCode();
            lastTime = TIME_COUNT;
            submitBtn.setEnabled(false);
            handler.sendEmptyMessage(WHAT_RESEND);
        }
    }
}
