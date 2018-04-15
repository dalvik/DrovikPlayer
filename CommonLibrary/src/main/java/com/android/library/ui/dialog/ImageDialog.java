package com.android.library.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.library.R;
import com.android.library.ui.camera.MultiImageSelectorActivity;
import com.android.library.ui.camera.cropper.scrop.Crop;
import com.android.library.ui.camera.cropper.scrop.DataActivity;
import com.android.library.ui.camera.imageselector.ImageSelectorUtil;
import com.android.library.ui.utils.ActivityUtils;
import com.android.library.utils.FileUtil;
import com.android.library.utils.MD5;

import java.io.File;
import java.util.ArrayList;

public class ImageDialog extends BaseDialog {
    public static final int REQUEST_IMAGE = 0xFF0;
    public static final int REQUEST_CAMERA = 0xFF1;
    // 秀场发布  相机拍摄
    public final static int PHOTO_TAKE = 1;
    // 秀场发布  大图片删除
    public final static int PHOTO_DELETE = 2;

    private Button topBtn;
    private Button bottomBtn;
    private Button cancelBtn;

    private View rootView;

    // 类型
    private int type;
    // 是否多选
    private boolean isMutil;
    // 事件监听
    private OnClickListener listener;
    private File cameraFile;

    public static ImageDialog newInstance(int type, boolean isMutil, OnClickListener listener) {
        ImageDialog dialogFragment = new ImageDialog();
        dialogFragment.type = type;
        dialogFragment.listener = listener;
        dialogFragment.isMutil = isMutil;
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_POSITION, POSITION_BOTTOM);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    /**
     * 选择图片
     *
     * @return
     */
    public static ImageDialog getTakeSinglePhotoWindow() {
        return newInstance(PHOTO_TAKE, false, null);
    }

    /**
     * 选择图片
     *
     * @return
     */
    public static ImageDialog getTakeMutilPhotoWindow() {
        return newInstance(PHOTO_TAKE, true, null);
    }

    /**
     * 删除图片
     *
     * @param listener
     * @return
     */
    public static ImageDialog getDeletePhotoWindow(OnClickListener listener) {
        return newInstance(PHOTO_DELETE, false, listener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 处理图片返回相关
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return true 已经处理过  false 未处理
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (isMutil) {
                    Intent intent = new Intent();
                    ArrayList<String> photoList = new ArrayList<String>();
                    photoList.add(cameraFile.getAbsolutePath());
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_RESULT, photoList);
                    DataActivity.passData(activity, intent);
                } else {
                    // 修剪图片
                    doCropPhoto(cameraFile);
                }
                break;
            case REQUEST_IMAGE:
                // 获取返回的图片列表
                ArrayList<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path == null || path.isEmpty()) {
                    return;
                }
                if (isMutil) {
                    DataActivity.passData(activity, data);
                } else {
                    // 修剪图片
                    doCropPhoto(new File(path.get(0)));
                }
                break;
        }
        dismissAllowingStateLoss();
    }

    private void doCropPhoto(File f) {
        Uri destination = Uri.fromFile(new File(FileUtil.getImageCachePath(activity), "cropped" + System.currentTimeMillis()));
        Crop.of(Uri.fromFile(f), destination).asSquare().start(activity);
    }


    /**
     * 照相机Intent
     *
     * @return
     */
    private Intent getTakePickIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getPhotoFileName()));
        return intent;
    }

    /**
     * 拍照图片名称
     *
     * @return
     */
    private File getPhotoFileName() {
        cameraFile =  new File(FileUtil.getImageCachePath(activity), MD5.toMD5String(System.currentTimeMillis()+""));
        return cameraFile;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.image_window, null);
        topBtn = (Button) rootView.findViewById(R.id.topBtn);
        bottomBtn = (Button) rootView.findViewById(R.id.bottomBtn);
        cancelBtn = (Button) rootView.findViewById(R.id.cancelBtn);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        rootView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = rootView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismissAllowingStateLoss();
                    }
                }
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (PHOTO_TAKE == type) {
            topBtn.setText(activity.getResources().getString(R.string.take_photo));
            bottomBtn.setText(activity.getResources().getString(R.string.choose_photo));
            topBtn.setTextColor(activity.getResources().getColor(R.color.findshows_color_87_black));
            bottomBtn.setTextColor(activity.getResources().getColor(R.color.findshows_color_87_black));
            //设置按钮监听
            topBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getTakePickIntent();
                    ActivityUtils.startActivityFromFragmentForResult(activity, ImageDialog.this, intent, REQUEST_CAMERA);
                }
            });
            bottomBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMutil) {
                        ImageSelectorUtil.showMuiltImageSelector(activity, REQUEST_IMAGE);
                    } else {
                        ImageSelectorUtil.showSingleImageSelectorFromFragment(activity, ImageDialog.this,REQUEST_IMAGE);
                    }
                }
            });
        } else if (PHOTO_DELETE == type) {
            topBtn.setText(activity.getResources().getString(R.string.delete_photo));
            bottomBtn.setText(activity.getResources().getString(R.string.his_personal_center_more_delete));
            topBtn.setTextColor(activity.getResources().getColor(R.color.findshows_color_87_black));
            bottomBtn.setTextColor(activity.getResources().getColor(R.color.findshows_color_red));
            //设置按钮监听
            topBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClickTopBtn();
                    }
                    dismissAllowingStateLoss();
                }
            });
            bottomBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClickBottomBtn();
                    }
                    dismissAllowingStateLoss();
                }
            });
        }
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
    }

    public interface OnClickListener {
        /**
         * 点击上面的按钮
         */
        void onClickTopBtn();

        /**
         * 点击下面的按钮
         */
        void onClickBottomBtn();
    }
}



