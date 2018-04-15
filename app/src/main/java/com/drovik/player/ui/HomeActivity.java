package com.drovik.player.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.provider.FileProviderService;
import com.drovik.player.R;
import com.drovik.player.ui.fragment.HomeFragment;
import com.drovik.player.ui.fragment.LeftFragment;
import com.android.library.ui.activity.BaseCompatActivity;
import com.nineoldandroids.view.ViewHelper;

public class HomeActivity extends BaseCompatActivity implements LeftFragment.OnFolderChangeListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(HomeActivity.this, MultiMediaService.class));
        startService(new Intent(HomeActivity.this, FileProviderService.class));
        setContentView(R.layout.activity_home);
        //setTitle(R.string.manager_device);
        setActionBarVisiable(View.GONE);
        initData();
        initView();
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
}
