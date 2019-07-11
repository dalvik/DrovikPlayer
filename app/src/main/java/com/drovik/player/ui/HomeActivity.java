package com.drovik.player.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.engine.UpdateManager;
import com.android.audiorecorder.provider.FileProviderService;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ToastUtils;
import com.android.library.utils.PermissionHelper;
import com.android.library.utils.Utils;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.crixmod.sailorcast.SailorCast;
import com.drovik.player.R;
import com.drovik.player.location.LocationService;
import com.drovik.player.ui.fragment.HomeFragment;
import com.drovik.player.ui.fragment.LeftFragment;
import com.drovik.player.weather.event.LocationEvent;
import com.nineoldandroids.view.ViewHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseCompatActivity implements LeftFragment.OnFolderChangeListener {

    public final int PERMISSION_RESULT_CODE = 100;
    public final int PERMISSION_STORAGE_CODE = 101;
    public final static int MENU_DEVICE = 1001;
    public final static int MENU_DOWNLOAD = 1002;
    public final static int MENU_TRANSPORT = 1003;
    public final static int MENU_SETTING = 1004;
    public final static int MENU_HOME = 1005;
    private long lastBackPressed = 0;
    private Fragment mCurrentFragment;

    public final static String USERNAME = "username";
    public final static String IP = "ip";
    public final static String DEVICE_ID = "device_id";
    public final static String DEVICE_NAME = "device_name";

    private DrawerLayout mDrawerLayout;
    private FrameLayout left;

    private String userName;
    private String ip;
    private int device_id;
    private String device_name;

    private PermissionHelper mPermissionHelper;

    private LocationService locationService;

    //android 6.0以上，需动态申请的权限
    public static String permissionArray[] = {
            "android.permission.READ_PHONE_STATE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.WRITE_EXTERNAL_STORAGE",
    };

    private final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(HomeActivity.this, MultiMediaService.class));
        startService(new Intent(HomeActivity.this, FileProviderService.class));
        setContentView(R.layout.activity_home);
        Utils.setStatusTextColor(false, this);
        fullScreen(R.color.home_color_primary);
        setActionBarBackgroundColor(R.color.home_color_primary, R.color.home_actionbar_background);
        //setActionBarBackgroundDrawable(R.drawable.home_actionbar_bg, R.color.home_actionbar_background);
        setActionBarVisiable(View.GONE);
        initData();
        initView();
        UpdateManager.getUpdateManager().checkAppUpdate(this, false);
        //申请权限
        requestPermission();
        /*if(!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showToast(com.android.audiorecorder.R.string.permission_should_granted);
            } else {
                requestPermission(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, EXTERNAL_STORAGE_REQ_CODE);
            }
        }*/
        initYouMi();
        initLocationSDK();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener);//注销掉监听
        locationService.stop();//停止定位服务
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra(USERNAME);
            ip = intent.getStringExtra(IP);
            device_id = intent.getIntExtra(DEVICE_ID, 0);
            device_name = intent.getStringExtra(DEVICE_NAME);
        }
    }


    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        left = (FrameLayout) findViewById(R.id.left_drawer);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                //改变DrawLayout侧栏透明度，若不需要效果可以不设置
                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth() * (1 - scale));
                ViewHelper.setPivotX(mContent, 0);
                ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        replaceFragment(R.id.main_content, HomeFragment.newInstance());
        replaceFragment(R.id.left_drawer, LeftFragment.newInstance());
    }

    /**
     * 打开左滑菜单
     */
    public void openLeftMenu() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        //mContent.setLeft(Utils.dp2px(HomeActivity.this, slideOffset * 240));
        //mBottomMenu.setLeft(Utils.dp2px(HomeActivity.this, slideOffset * 240));
    }

    @Override
    public void onFolderChange(int path, boolean open) {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT) && open) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
        switch (path) {
            /*case MENU_DEVICE:
                DeviceFragment uploadeFragment = DeviceFragment.newInstance();
                switchContent(uploadeFragment);
                mCurrentFragment = uploadeFragment;
                break;
            case MENU_DOWNLOAD:
                DownloadFolderFragment downloadFragment = DownloadFolderFragment.newInstance();
                switchContent(downloadFragment);
                mCurrentFragment = downloadFragment;
                break;
            case MENU_SETTING:
                SettingFragment settingFragment = SettingFragment.newInstance();
                switchContent(settingFragment);
                mCurrentFragment = settingFragment;
                break;
            case MENU_TRANSPORT:
                TransportFragment transportFragment = TransportFragment.newInstance();
                switchContent(transportFragment);
                mCurrentFragment = transportFragment;
                break;
            case MENU_HOME:
                HomeFragment homeFragment = HomeFragment.newInstance();
                switchContent(homeFragment);
                mCurrentFragment = homeFragment;
                break;*/
        }
    }

    public void switchContent(Fragment fragment) {
        mCurrentFragment = fragment;
        replaceFragment(R.id.main_content, fragment);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                            return true;
                        }
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastBackPressed < 2000) {
                            super.onBackPressed();
                        } else {
                            showToast(R.string.exit_tip);
                        }
                        lastBackPressed = currentTime;
                        return true;
                    }
            }
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

    private void initYouMi() {
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(activity);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                com.android.audiorecorder.utils.LogUtil.i(TAG, "All of requested permissions has been granted, so run app logic.");
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            com.android.audiorecorder.utils.LogUtil.d(TAG, "The api level of system is lower than 23, so run app logic directly.");
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                com.android.audiorecorder.utils.LogUtil.d(TAG, "All of requested permissions has been granted, so run app logic directly.");
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                com.android.audiorecorder.utils.LogUtil.i(TAG, "Some of requested permissions hasn't been granted, so apply permissions first.");
                mPermissionHelper.applyPermissions();
            }
        }

    }

    private void initLocationSDK() {
        locationService = ((SailorCast) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionList = new ArrayList<>();
            for (String permission : permissionArray) {
                LogUtil.d("==> And--M", permission);
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            if (permissionList.size() > 0) {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), PERMISSION_RESULT_CODE);
            }
        }
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                LocationEvent event = new LocationEvent(location.getCity());
                EventBus.getDefault().post(event);
            }
        }

    };

}
