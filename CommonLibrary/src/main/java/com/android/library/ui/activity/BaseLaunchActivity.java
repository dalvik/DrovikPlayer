package com.android.library.ui.activity;

import android.os.Bundle;

import com.android.library.BaseApplication;
import com.android.library.R;

public abstract class BaseLaunchActivity extends BaseCommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载启动页面
        setContentView(R.layout.start_activity);

        /*if (ClientInfo.getInstance().networkType == ClientInfo.NONET) {
            ToastUtils.showToast(R.string.no_net);
        }*/
        BaseApplication.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoNextActivity();
            }
        }, 2000);
    }

    /**
     * 跳转到...
     */
    public abstract void gotoNextActivity();

}
