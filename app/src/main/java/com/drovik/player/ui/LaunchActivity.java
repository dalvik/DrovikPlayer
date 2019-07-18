package com.drovik.player.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.androidquery.AQuery;
import com.drovik.player.R;
import com.drovik.player.adv.AdvConst;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.config.AdError;
import com.iflytek.voiceads.config.AdKeys;
import com.iflytek.voiceads.conn.NativeDataRef;
import com.iflytek.voiceads.listener.IFLYNativeListener;
import com.kuaiyou.loader.AdViewSpreadManager;
import com.kuaiyou.loader.InitSDKManager;
import com.kuaiyou.loader.loaderInterface.AdViewSpreadListener;

import java.util.ArrayList;
import java.util.List;

public class LaunchActivity extends Activity implements IFLYNativeListener, AdViewSpreadListener {

    public static final int MSG_FINISH = 1000;
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

    public String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE};
    ;
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
        initAdView();
        hideBottomUIMenu();
        if (Build.VERSION.SDK_INT >= 23) {
            requestRunTimePermission(permissions);
        }else{
            requestSpreadAd();
        }
        handler.sendEmptyMessageDelayed(MSG_FINISH, 4000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(MSG_FINISH);
        LogUtil.d(TAG, "==> onDestroy.");
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
    public void onAdLoaded(NativeDataRef dataRef) {
        adItem = dataRef;
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
                case MSG_FINISH:
                    gotoTarget();
                    break;
                default:
                    break;
            }
        }
    };

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
    /***************************************************/

    private AdViewSpreadManager adSpreadBIDView = null;
    private int count = 1;

    private void initAdView() {
        InitSDKManager.getInstance().init(this, AdvConst.ADVIEW_APPID, null);
        InitSDKManager.setDownloadNotificationEnable(false);
    }

    private void requestSpreadAd(){
        adSpreadBIDView = new AdViewSpreadManager(this, AdvConst.ADVIEW_APPID, (RelativeLayout) findViewById(R.id.spreadlayout));
        //adSpreadBIDView.setLogo(R.drawable.ic_laucher_h);
        //adSpreadBIDView.setBackgroundColor(Color.WHITE);
        adSpreadBIDView.setSpreadNotifyType(AdViewSpreadManager.NOTIFY_COUNTER_TEXT);
        adSpreadBIDView.setOnAdViewListener(this);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onAdNotifyCustomCallback( final int ruleTime,final  int delayTime) {
        Log.i("AdViewBID", "onAdNotifyCustomCallback");
        final TextView tv1 = new TextView(this);
        final Button btn1 = new Button(this);
        final LayoutParams btnLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        final LayoutParams tvLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        btn1.setId(123123);
        btn1.setText("Skip");
        tv1.setText(ruleTime + delayTime + "");

        btnLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tvLp.addRule(RelativeLayout.LEFT_OF, btn1.getId());

        adSpreadBIDView.getParentLayout().postDelayed(new Runnable() {

            @Override
            public void run() {
                btn1.setVisibility(View.VISIBLE);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adSpreadBIDView.cancelAd();
                    }
                });
            }
        }, ruleTime * 1000);
        adSpreadBIDView.getParentLayout().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (ruleTime + delayTime - count >= 1) {
                    tv1.setText(ruleTime + delayTime - count + "");
                    count++;
                    adSpreadBIDView.getParentLayout().postDelayed(this, 1000);
                }
            }
        }, 1000);
        adSpreadBIDView.getParentLayout().addView(btn1, btnLp);
        adSpreadBIDView.getParentLayout().addView(tv1, tvLp);
        btn1.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onAdClicked() {
        Log.i("AdViewBID", "onAdClicked");
    }

    @Override
    public void onAdClosed() {
        Log.i("AdViewBID", "onAdClosedAd");
        handler.removeMessages(MSG_FINISH);
        handler.sendEmptyMessageDelayed(MSG_FINISH, 500);
    }

    @Override
    public void onAdClosedByUser() {
        Log.i("AdViewBID", "onAdClosedByUser");
        handler.removeMessages(MSG_FINISH);
        handler.sendEmptyMessageDelayed(MSG_FINISH, 500);
    }

    @Override
    public void onAdDisplayed() {
        Log.i("AdViewBID", "onAdDisplayed");
    }

    @Override
    public void onAdFailedReceived(String arg1) {
        Log.i("AdViewBID", "onAdRecieveFailed");
    }

    @Override
    public void onAdReceived() {
        Log.i("AdViewBID", "onAdRecieved");
    }

    @Override
    public void onAdSpreadPrepareClosed() {
        handler.removeMessages(MSG_FINISH);
        handler.sendEmptyMessageDelayed(MSG_FINISH, 500);
        Log.i("AdViewBID", "onAdSpreadPrepareClosed");
    }

    protected void requestRunTimePermission(String[] permissions) {
        // 用于存放为授权的权限
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            // 判断是否已经授权，未授权，则加入待授权的权限集合中
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        // 判断集合
        if (!permissionList.isEmpty()) { // 如果集合不为空，则需要去授权
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            requestSpreadAd();
        }
    }

    /**
     * 权限申请结果
     * @param requestCode
     *            请求码
     * @param permissions
     *            所有的权限集合
     * @param grantResults
     *            授权结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    // 被用户拒绝的权限集合
                    List<String> deniedPermissions = new ArrayList<>();
                    // 用户通过的权限集合
                    List<String> grantedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        // 获取授权结果，这是一个int类型的值
                        int grantResult = grantResults[i];

                        if (grantResult != PackageManager.PERMISSION_GRANTED) { // 用户拒绝授权的权限
                            String permission = permissions[i];
                            deniedPermissions.add(permission);
                        } else { // 用户同意的权限
                            String permission = permissions[i];
                            grantedPermissions.add(permission);
                        }
                    }

                    if (deniedPermissions.isEmpty()) { // 用户拒绝权限为空
                        requestSpreadAd();
                    } else { // 不为空
                        Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
                        // 回调授权成功的接口
                        // 回调授权失败的接口
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
    }

}
