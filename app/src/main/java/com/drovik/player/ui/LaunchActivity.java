package com.drovik.player.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ToastUtils;
import com.android.library.utils.PermissionHelper;
import com.androidquery.AQuery;
import com.crixmod.sailorcast.utils.ImageTools;
import com.drovik.player.R;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.conn.NativeDataRef;
import com.iflytek.voiceads.listener.IFLYNativeListener;

import java.util.ArrayList;
import java.util.List;

import cdc.sed.yff.AdManager;
import cdc.sed.yff.nm.cm.ErrorCode;
import cdc.sed.yff.nm.sp.SplashViewSettings;
import cdc.sed.yff.nm.sp.SpotListener;
import cdc.sed.yff.nm.sp.SpotManager;
import cdc.sed.yff.nm.sp.SpotRequestListener;

public class LaunchActivity extends Activity implements IFLYNativeListener {

    public static final int BASE_REQ_CODE = 1;
    public static final int EXTERNAL_STORAGE_REQ_CODE = BASE_REQ_CODE + 1 ;
    public static final int AUDIO_RECORD_REQ_CODE = EXTERNAL_STORAGE_REQ_CODE + 1;
    public static final int PHONE_CALL_REQ_CODE = AUDIO_RECORD_REQ_CODE + 1;
    public final int PERMISSION_RESULT_CODE = 100;
    public final int PERMISSION_STORAGE_CODE = 101;
    public static final String APP_ID = "da88c11617dad28f";
    public static final String APP_SECRET = "d8cdfdb2eb696a0b";

    private IFLYNativeAd nativeAd;
    private NativeDataRef adItem;
    private AQuery aQuery;

    private PermissionHelper mPermissionHelper;

    //android 6.0以上，需动态申请的权限
    public static String permissionArray[] = {
            /*"android.permission.READ_PHONE_STATE",
            "android.permission.WRITE_EXTERNAL_STORAGE",*/
            "android.permission.ACCESS_FINE_LOCATION",
    };
    private String TAG = "LaunchActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载启动页面
        setContentView(R.layout.start_activity);
        hideBottomUIMenu();
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                Log.i(TAG, "All of requested permissions has been granted, so run app logic.");
                runApp();
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            Log.d(TAG, "The api level of system is lower than 23, so run app logic directly.");
            runApp();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                Log.d(TAG, "All of requested permissions has been granted, so run app logic directly.");
                runApp();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Log.i(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();
            }
        }
        //申请权限
        requestPermission();
        handler.sendEmptyMessageDelayed(1, 4000);
        //loadAD();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpotManager.getInstance(this).onDestroy();
    }

    public void loadAD() {
        nativeAd = new IFLYNativeAd(this, "1B2F5A2298CC2F806AD4614B437070E9", this);
        aQuery = new AQuery(this);
        nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, true);
        nativeAd.setParameter(AdKeys.DEBUG_MODE, StringUtils.getVersionName(this));
        nativeAd.loadAd();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(com.android.library.R.string.permission_not_granted_write_storage);
                }
                break;
            default:
                break;
        }
    }

    public void showAD() {
        final ImageView adImageView = (ImageView) findViewById(R.id.fullscreen_img);
        if (adItem.getImgUrl() != null) {
            ImageTools.displayImage(adImageView, adItem.getImgUrl());
        }
        adImageView.setVisibility(View.VISIBLE);
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

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionList = new ArrayList<>();
            for (String permission : permissionArray) {
                com.android.library.net.utils.LogUtil.d("==> And--M", permission);
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            if (permissionList.size() > 0) {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), PERMISSION_RESULT_CODE);
            }
        }
    }

    /**
     * 跑应用的逻辑
     */
    private void runApp() {
        //初始化SDK
        AdManager.getInstance(this).init(APP_ID, APP_SECRET, true);
        preloadAd();
        setupSplashAd(); // 如果需要首次展示开屏，请注释掉本句代码
    }

    /**
     * 预加载广告
     */
    private void preloadAd() {
        // 注意：不必每次展示插播广告前都请求，只需在应用启动时请求一次
        SpotManager.getInstance(this).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                logInfo("请求插屏广告成功");
                //				// 应用安装后首次展示开屏会因为本地没有数据而跳过
                //              // 如果开发者需要在首次也能展示开屏，可以在请求广告成功之前展示应用的logo，请求成功后再加载开屏
                //				setupSplashAd();
            }

            @Override
            public void onRequestFailed(int errorCode) {
                logError("请求插屏广告失败，errorCode: %s", errorCode);
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        //showShortToast("网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        //showShortToast("暂无插屏广告");
                        break;
                    default:
                        //showShortToast("请稍后再试");
                        break;
                }
            }
        });
    }


    /**
     * 设置开屏广告
     */
    private void setupSplashAd() {
        // 创建开屏容器
        final RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        RelativeLayout.LayoutParams params =  new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE, R.id.view_divider);

        // 对开屏进行设置
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        // 设置是否展示失败自动跳转，默认自动跳转
        splashViewSettings.setAutoJumpToTargetWhenShowFailed(false);
        // 设置跳转的窗口类
        splashViewSettings.setTargetClass(HomeActivity.class);
        // 设置开屏的容器
        splashViewSettings.setSplashViewContainer(splashLayout);

        // 展示开屏广告
        SpotManager.getInstance(this).showSplash(this, splashViewSettings, new SpotListener() {

                    @Override
                    public void onShowSuccess() {
                        logInfo("开屏展示成功");
                    }

                    @Override
                    public void onShowFailed(int errorCode) {
                        logError("开屏展示失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                                logError("网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                logError("暂无开屏广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                                logError("开屏资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                                logError("开屏展示间隔限制");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                logError("开屏控件处在不可见状态");
                                break;
                            default:
                                logError("errorCode: %d", errorCode);
                                break;
                        }
                    }

                    @Override
                    public void onSpotClosed() {
                        logDebug("开屏被关闭");
                    }

                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        logDebug("开屏被点击");
                        logInfo("是否是网页广告？%s", isWebPage ? "是" : "不是");
                    }
                });
    }

    protected void logInfo(String format, Object... args) {
        logMessage(Log.INFO, format, args);
    }

    protected void logError(String format, Object... args) {
        logMessage(Log.ERROR, format, args);
    }

    protected void logDebug(String format, Object... args) {
        logMessage(Log.DEBUG, format, args);
    }

    protected void showShortToast(String format, Object... args) {
        showToast(Toast.LENGTH_SHORT, format, args);
    }

    private void showToast(int duration, String format, Object... args) {
        Toast.makeText(this, String.format(format, args), duration).show();
    }

    private void logMessage(int level, String format, Object... args) {
        String formattedString = String.format(format, args);
        switch (level) {
            case Log.DEBUG:
                Log.d(TAG, formattedString);
                break;
            case Log.INFO:
                Log.i(TAG, formattedString);
                break;
            case Log.ERROR:
                Log.e(TAG, formattedString);
                break;
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
