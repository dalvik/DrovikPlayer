package com.drovik.player.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.audiorecorder.utils.LogUtil;
import com.androidquery.AQuery;
import com.crixmod.sailorcast.utils.ImageTools;
import com.drovik.player.R;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.conn.NativeDataRef;
import com.iflytek.voiceads.listener.IFLYNativeListener;

public class LaunchActivity extends Activity implements IFLYNativeListener {

    private IFLYNativeAd nativeAd;
    private NativeDataRef adItem;
    private AQuery aQuery;

    private String TAG = "LaunchActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        // 加载启动页面
        setContentView(R.layout.start_activity);
        loadAD();
        handler.sendEmptyMessageDelayed(1, 6000);
    }

    public void loadAD() {
        nativeAd = new IFLYNativeAd(this, "1B2F5A2298CC2F806AD4614B437070E9", this);
        aQuery = new AQuery(this);
        nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, true);
        nativeAd.setParameter(AdKeys.DEBUG_MODE, getVersionName(this));
        nativeAd.loadAd();
    }

    public void showAD() {
        final ImageView adImageView = (ImageView) findViewById(R.id.fullscreen_img);
        if (adItem.getImgUrl() != null) {
            ImageTools.displayImage(adImageView, adItem.getImgUrl());
        }
        adImageView.setVisibility(View.VISIBLE);

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                if (millisUntilFinished >= 1000) {
//                    mCountDownText.setText("剩余时间: " + millisUntilFinished / 1000 + "s");
//                }else {
//                    mCountDownText.setText("剩余时间: 0s");
//                }
            }

            @Override
            public void onFinish() {
                //全屏广告展示结束
                handler.removeMessages(1);
                handler.sendEmptyMessage(1);
            }
        }.start();
        adImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adItem.onClick(adImageView);
            }
        });
        if (adItem.onExposure(this.findViewById(R.id.fullscreen_img))) {
            Log.d(TAG, "onExposure");
        }
    }

    @Override
    public void onAdLoaded(NativeDataRef dataRef) {
        adItem = dataRef;
        showAD();
    }

    @Override
    public void onAdFailed(AdError adError) {
        LogUtil.d(TAG, "==> onAdFailed: " + adError.getErrorDescription() + " : " + adError.getErrorCode());
        handler.removeMessages(1);
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onConfirm() {

    }

    @Override
    public void onCancel() {

    }

    private void gotoTarget() {
        Intent intent  = new Intent(LaunchActivity.this, HomeActivity.class);
        startActivity(intent);
        LaunchActivity.this.finish();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    gotoTarget();
                    break;
                default:
                    break;
            }
        }
    };

    public String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
