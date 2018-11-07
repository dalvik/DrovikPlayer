package com.drovik.player.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.audiorecorder.utils.LogUtil;
import com.android.library.BaseApplication;
import com.android.library.ui.camera.bean.Image;
import com.baidu.mobstat.StatService;
import com.crixmod.sailorcast.utils.ImageTools;
import com.drovik.player.R;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class LaunchActivity extends Activity implements IFLYNativeListener {

    private IFLYNativeAd nativeAd;
    private NativeADDataRef adItem;

    private String TAG = "LaunchActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载启动页面
        setContentView(R.layout.start_activity);
        loadAD();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent  = new Intent(LaunchActivity.this, HomeActivity.class);
                startActivity(intent);
                LaunchActivity.this.finish();
            }
        }, 3000);
        StatService.start(this);
    }

    public void loadAD() {
        if (nativeAd == null) {
            nativeAd = new IFLYNativeAd(this, "1B2F5A2298CC2F806AD4614B437070E9", this);
        }
        //nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
        //nativeAd.setParameter(AdKeys.DEBUG_MODE, "true");
        int count = 1;
        nativeAd.loadAd(count);
    }

    public void showAD() {
        final ImageView adImageView = (ImageView) findViewById(R.id.fullscreen_img);
        if (adItem.getImgUrls() != null && adItem.getImgUrls().size() > 0) {
            ImageTools.displayImage(adImageView, adItem.getImgUrls().get(0));
        } else {
            ImageTools.displayImage(adImageView, adItem.getImage());
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
            }
        }.start();
        adImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adItem.onClicked(adImageView);
            }
        });
        //原生广告需上传点击位置
        findViewById(R.id.fullscreen_img).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        nativeAd.setParameter(AdKeys.CLICK_POS_DX, event.getX() + "");
                        nativeAd.setParameter(AdKeys.CLICK_POS_DY, event.getY() + "");
                        break;
                    case MotionEvent.ACTION_UP:
                        nativeAd.setParameter(AdKeys.CLICK_POS_UX, event.getX() + "");
                        nativeAd.setParameter(AdKeys.CLICK_POS_UY, event.getY() + "");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        if (adItem.onExposured(this.findViewById(R.id.fullscreen_img))) {
            Log.d("", "曝光成功");
        }
    }

    @Override
    public void onADLoaded(List<NativeADDataRef> list) {
        if (list.size() > 0) {
            adItem = list.get(0);
            showAD();
        }
    }

    @Override
    public void onAdFailed(AdError adError) {
        LogUtil.d(TAG, "==> onAdFailed: " + adError.getErrorDescription());

    }

    @Override
    public void onConfirm() {

    }

    @Override
    public void onCancel() {

    }
}