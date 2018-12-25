package com.drovik.player.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.drovik.player.R;

public class LeftFragment extends Fragment implements View.OnClickListener {
    private static LeftFragment leftFragment;
    private LinearLayout mManagerDevices;
    private LinearLayout mManagerTransport;
    private LinearLayout mManagerDownload;
    private LinearLayout mManagerSetting;
    private OnFolderChangeListener mListener;

    public static LeftFragment newInstance() {
        leftFragment = new LeftFragment();
        Bundle args = new Bundle();
//        args.putString(USERNAME, userName);
//        args.putString(IP, ip);
//        args.putInt(DEVICE_ID, device_id);
//        args.putString(DEVICE_NAME, device_name);
        leftFragment.setArguments(args);
        return leftFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFolderChangeListener) {
            mListener = (OnFolderChangeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFolderChangeListener");
        }
    }

    private void initView(View view) {
        mManagerDevices = (LinearLayout) view.findViewById(R.id.left_manager_devices);
        mManagerTransport = (LinearLayout) view.findViewById(R.id.left_manager_transport);
        mManagerDownload = (LinearLayout) view.findViewById(R.id.left_manager_download);
        mManagerSetting = (LinearLayout) view.findViewById(R.id.left_manager_setting);
        mManagerDevices.setOnClickListener(this);
        mManagerTransport.setOnClickListener(this);
        mManagerDownload.setOnClickListener(this);
        mManagerSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        /*if (UIUtility.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.left_manager_devices:
                if (mListener != null) {
                    mListener.onFolderChange(NewsFrameActivity.MENU_DEVICE, true);
                }
                break;
            case R.id.left_manager_transport:
                if (mListener != null) {
                    mListener.onFolderChange(NewsFrameActivity.MENU_TRANSPORT, true);
                }
                break;
            case R.id.left_manager_download:
                if (mListener != null) {
                    mListener.onFolderChange(NewsFrameActivity.MENU_DOWNLOAD, true);
                }
                break;
            case R.id.left_manager_setting:
                if (mListener != null) {
                    mListener.onFolderChange(NewsFrameActivity.MENU_SETTING, true);
                }
                break;
        }*/
    }

    public interface OnFolderChangeListener {
        void onFolderChange(int path, boolean open);
    }
}
