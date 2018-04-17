package com.drovik.player.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.audiorecorder.ui.FileExplorerActivity;
import com.android.audiorecorder.ui.SoundRecorder;
import com.android.audiorecorder.ui.activity.LoginActivity;
import com.drovik.player.AppApplication;
import com.drovik.player.R;
import com.drovik.player.audio.ui.MusicActivity;
import com.drovik.player.ui.HomeActivity;
import com.drovik.player.video.ui.VideoMainActivity;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.pager.BasePager;

public class HomeFragment extends BasePager implements View.OnClickListener, IHomeView {

    private static final int LOGIN_SUCCESS = 1001;
    private LinearLayout mNoDevice;
    private ImageView gifBg;
    private TextView mDeviceSize;
    private TextView mDeviceName;
    private LinearLayout mOperate;
    private FrameLayout mLoginImg;
    private RelativeLayout mContainer;
    private Context mContext;
    private RelativeLayout mHeader;
    private AnimationDrawable frameAnim;
   // private HomeController mHomeController;
    private String free;
    private String device_total;
   // private ArrayList<Device> devices;
   // private Device mSelectDevice;//最后一个登陆的设备
    //private LoginHandle loginHandle;
    private boolean mLoginSucess;

    private String TAG = "HomeFragment";

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    stopLoginAnim();
                    /*if (loginHandle.handle != 0) {
                        NasApplication.getInstance().setLoginHandle(loginHandle);
                        NasApplication.getInstance().setBaseIP(mSelectDevice.getIp());
                        NasApplication.deviceType = mSelectDevice.getDeviceType();
                        if (mHomeController != null) {
                            mHomeController.getDeviceFree();
                        }
                        showDevice(true, true);
                    } else {
                        showDevice(true, false);
                        Toast.makeText(mContext, ErrorHelper.getError(loginHandle.errorCode, mContext), Toast.LENGTH_SHORT).show();
                    }*/
                    break;
            }
        }
    };
    private ImageView mHomeMenu;


    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initData();
        initView(view);
        initHome();
        return view;
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void reload() {

    }

    private void initData() {
        /*devices = (ArrayList<Device>) DeviceManager.instance().getAllDevice(Device.DEV_TYPE_IP);
        devices.addAll(DeviceManager.instance().getAllDevice(Device.DEV_TYPE_P2P_CHINA));
        devices.addAll(DeviceManager.instance().getAllDevice(Device.DEV_TYPE_P2P_OVERSEAS));*/
    }

    private void initView(View view) {
        mContainer = (RelativeLayout) view.findViewById(R.id.home_container);
        mHeader = (RelativeLayout) view.findViewById(R.id.home_header);
        mHomeMenu = (ImageView) view.findViewById(R.id.home_menu);
        mHomeMenu.setOnClickListener(this);
        mLoginImg = (FrameLayout) view.findViewById(R.id.home_login);
        mLoginImg.setOnClickListener(this);
        mDeviceSize = (TextView) view.findViewById(R.id.home_device_size);
        mOperate = (LinearLayout) view.findViewById(R.id.home_operate);
        gifBg = (ImageView) view.findViewById(R.id.home_login_gif_bg);
        frameAnim = new AnimationDrawable();
        // 为AnimationDrawable添加动画帧
        frameAnim.addFrame(getResources().getDrawable(R.drawable.home_body_device_gif_bg_h), 300);
        frameAnim.addFrame(getResources().getDrawable(R.drawable.home_body_device_gif_bg_h), 300);
        // 设置为循环播放
        frameAnim.setOneShot(false);
        gifBg.setBackgroundDrawable(frameAnim);

        mDeviceName = (TextView) view.findViewById(R.id.home_device_name);
        view.findViewById(R.id.home_file).setOnClickListener(this);
        view.findViewById(R.id.home_photo).setOnClickListener(this);
        view.findViewById(R.id.home_video).setOnClickListener(this);
        view.findViewById(R.id.home_music).setOnClickListener(this);
        view.findViewById(R.id.home_recorder).setOnClickListener(this);
        view.findViewById(R.id.home_sur).setOnClickListener(this);

        mNoDevice = (LinearLayout) view.findViewById(R.id.home_no_device);
        mNoDevice.setOnClickListener(this);

    }

    private void initHome() {
        LogUtil.d(TAG, "initHome LoginHandle:" + AppApplication.getInstance());
        /*if (devices != null && devices.size() > 0) {
            LogUtil.d(HomeFragment.class, " devices.size():" + devices.size() + "--" + NasApplication.getInstance().getLoginHandle());
            if (NasApplication.getInstance().getLoginHandle() != null && NasApplication.getInstance().getLoginHandle().handle != 0) {
                showDevice(true, true);
            } else {
                showDevice(true, false);
                if (NasApplication.getInstance().isFirstInHome()) {
                    autoLogin();
                    NasApplication.getInstance().setFirstInHome(false);
                }
            }
        } else {
            showDevice(false, false);
        }*/

    }


    @Override
    public void onResume() {
        super.onResume();
        /*if (mHomeController != null) {
            mHomeController.getDeviceFree();
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void autoLogin() {
        startLoginAnim();
         /*if (devices != null) {
            mSelectDevice = devices.get(devices.size() - 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                   LoginModule.instance().logOutAll();
                    loginHandle = LoginModule.instance().getLoginHandle(mSelectDevice);
                    Message msg = mHandler.obtainMessage();
                    msg.what = LOGIN_SUCCESS;
                    msg.sendToTarget();
                }
            }).start();
        }*/
    }

    private void showDevice(boolean isShow, boolean loginSucess) {
        /*LogUtil.d(HomeFragment.class, "showDevice isShow:" + isShow + "--loginSucess:" + loginSucess);
        if (isShow) {
            mNoDevice.setVisibility(View.GONE);
            mLoginImg.setVisibility(View.VISIBLE);
            mDeviceSize.setVisibility(View.VISIBLE);
            mOperate.setVisibility(View.VISIBLE);
            mContainer.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.home_bg));
            mLoginSucess = loginSucess;
            if (loginSucess) {
                gifBg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.home_body_device_gif_bg_h));
            } else {
                gifBg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.home_body_device_gif_bg_n));
                mDeviceSize.setText(mContext.getResources().getText(R.string.offline_tip));
            }
            mHomeMenu.setBackgroundDrawable(mContext.getApplicationContext().getResources().getDrawable(R.drawable.home_menu_selector));
        } else {
            mNoDevice.setVisibility(View.VISIBLE);
            mLoginImg.setVisibility(View.GONE);
            mDeviceSize.setVisibility(View.GONE);
            mOperate.setVisibility(View.GONE);
            mDeviceName.setTextColor(mContext.getResources().getColor(R.color.common_title));
            mDeviceName.setText(mContext.getResources().getText(R.string.home));
            mContainer.setBackgroundColor(mContext.getResources().getColor(R.color.common_bg));
            mHeader.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            //mContext.getApplicationContext().getResources().getDrawable(R.drawable.common_menu_selector)
            mHomeMenu.setBackgroundDrawable(mContext.getApplicationContext().getResources().getDrawable(R.drawable.common_menu_selector));
        }*/

    }

    @Override
    public void onClick(View v) {
        /*if (UIUtility.isFastDoubleClick()) {
            return;
        }*/
        switch (v.getId()) {
            case R.id.home_menu:
                HomeActivity activity = (HomeActivity) getActivity();
                activity.openLeftMenu();
                break;
            case R.id.home_file:
                Intent intentFile = new Intent(mContext, FileExplorerActivity.class);
                startActivity(intentFile);
                break;
            case R.id.home_photo:
                Intent intentPhoto = new Intent(mContext, SoundRecorder.class);
                startActivity(intentPhoto);
                break;
            case R.id.home_video:
                Intent intentVideo = new Intent(mContext, VideoMainActivity.class);
                startActivity(intentVideo);
                break;
            case R.id.home_music:
                Intent intentMusic = new Intent(mContext, MusicActivity.class);
                startActivity(intentMusic);
                break;
            case R.id.home_recorder:
                Intent intentRecord = new Intent(mContext, SoundRecorder.class);
                startActivity(intentRecord);
                break;
            /*case R.id.home_sur:
                break;
            case R.id.home_no_device:
                LogUtil.d(HomeFragment.class, "home_no_device is enter");
                Intent intentDownload = new Intent(mContext, ScanSerialActivity.class);
                startActivity(intentDownload);
                break;*/
            case R.id.home_login:
                Intent loginActivity = new Intent(mContext, LoginActivity.class);
                startActivity(loginActivity);
                /*if (!mLoginSucess) {
                    startLoginAnim();
                    autoLogin();
                }*/
                break;
        }
    }

    protected void startLoginAnim() {
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.start();
            //mDeviceSize.setText(mContext.getResources().getText(R.string.logining_device));
        }
    }

    protected void stopLoginAnim() {
        if (frameAnim != null && frameAnim.isRunning()) {
            frameAnim.stop();
        }
    }

    @Override
    public void showDeviceFree(int available, int total) {
        if (available > 1024) {
            free = (float) (available / 1024) + "GB";
        } else {
            free = available + "MB";
        }
        if (total > 1024) {
            device_total = (float) (total / 1024) + "GB";
        } else {
            device_total = total + "MB";
        }
        //mDeviceSize.setText(mContext.getApplicationContext().getString(R.string.device_free, free, device_total));
    }
}
