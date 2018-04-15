package com.android.library.ui.camera.imageselector;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.library.ui.camera.MultiImageSelectorActivity;
import com.android.library.ui.utils.ActivityUtils;

/**
 * Created by Sean.xie on 2015/12/18.
 */
public class ImageSelectorUtil {

    /**
     * 单选图片
     *
     * @param activity
     * @param requestCode
     */
    public static void showSingleImageSelector(FragmentActivity activity, int requestCode) {
        showSelector(activity, MultiImageSelectorActivity.MODE_SINGLE, requestCode);
    }

    /**
     * 在Fragment中启动单选
     *
     * @param activity
     * @param fragment
     * @param requestCode
     */
    public static void showSingleImageSelectorFromFragment(FragmentActivity activity, Fragment fragment, int requestCode) {
        showSelectorFromFragment(activity, fragment, MultiImageSelectorActivity.MODE_SINGLE, requestCode);
    }

    /**
     * 多选图片
     *
     * @param activity
     * @param requestCode
     */
    public static void showMuiltImageSelector(FragmentActivity activity, int requestCode) {
        showSelector(activity, MultiImageSelectorActivity.MODE_MULTI, requestCode);
    }

    /**
     * 在Fragment中启动多选
     *
     * @param activity
     * @param fragment
     * @param requestCode
     */
    public static void showMuiltImageSelectorFromFragment(FragmentActivity activity, Fragment fragment, int requestCode) {
        showSelectorFromFragment(activity, fragment, MultiImageSelectorActivity.MODE_MULTI, requestCode);
    }

    /**
     * 显示图片选择器
     *
     * @param activity
     * @param type
     * @param requestCode
     */
    public static void showSelector(FragmentActivity activity, int type, int requestCode) {
        activity.startActivityForResult(getIntent(activity, type), requestCode);
    }

    /**
     * 在Fragment中启动
     *
     * @param activity
     * @param fragment
     * @param type
     * @param requestCode
     */
    private static void showSelectorFromFragment(FragmentActivity activity, Fragment fragment, int type, int requestCode) {
        ActivityUtils.startActivityFromFragment(activity, fragment, getIntent(activity, type), requestCode);
    }

    /**
     * 选择图片Intent
     *
     * @param activity
     * @param type
     * @return
     */
    private static Intent getIntent(FragmentActivity activity, int type) {
        Intent intent = new Intent(activity, MultiImageSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        // 最大图片选择数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, type);
        return intent;
    }
}
