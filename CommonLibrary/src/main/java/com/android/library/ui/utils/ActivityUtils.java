package com.android.library.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;

import com.android.library.BaseApplication;
import com.android.library.utils.TextUtils;


/**
 * 实现UI的跳转，定义Activity之间跳转最简单接口 
 * 目前 重构之外的跳转代码分散到各个Activity中，这样重复劳动太多，容易出现错误
 *
 */
public class ActivityUtils {

    /**
     * 单一数据
     **/
    public static final String INTENT_SIMPLE = "simple";
    /**
     * 复杂数据
     */
    public static final String INTENT_BEAN = "bean";
    /**
     * 手机号
     **/
    public static final String INTENT_MOBILE = "mobile";

    /**
     * 处理 多次点击 打开多个页面
     */
    public static void startActivity(Activity activity, Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    /**
     * 处理 多次点击 打开多个页面
     */
    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity activity, Class clazz, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 通用的不带参数的界面跳转 但是activity是从右边进来的
     */
    public static void startActivity(Class<?> cls) {
        Intent intent = new Intent(BaseApplication.curContext, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        BaseApplication.curContext.startActivity(intent);
    }

    /**
     * 不带动画的跳转
     */
    public static void startActivity(Activity activity, Class<?> activityCkass) {
        Intent intent = new Intent(activity, activityCkass);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
//        activity.overridePendingTransition(0, 0);
    }

    /**
     * @param activity
     * @param fragment
     * @param clazz
     */
    public static void startActivityFromFragment(FragmentActivity activity, Fragment fragment, Class<?> clazz) {
        Intent intent = new Intent(activity, clazz);
        startActivityFromFragment(activity, fragment, intent);
    }

    public static void startActivityFromFragment(FragmentActivity activity, Fragment fragment, Class<?> clazz, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        startActivityFromFragment(activity, fragment, intent, requestCode);
    }

    /**
     * @param activity
     * @param fragment
     * @param intent
     */
    public static void startActivityFromFragment(FragmentActivity activity, Fragment fragment, Intent intent) {
        startActivityFromFragment(activity, fragment, intent, -1);
    }

    /**
     * @param activity
     * @param fragment
     * @param intent
     * @param requestCode
     */
    public static void startActivityFromFragment(FragmentActivity activity, Fragment fragment, Intent intent, int requestCode) {
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivityFromFragment(fragment, intent, requestCode);
    }
    
    /**
     * 跳转主界面
     *
     * @param activity
     * @param isFirst
     */
    public static void gotoMainActivity(Activity activity, boolean isFirst) {
        // 连接融云
        //RongCloudContext.connect(AppApplication.getRongCloudToken());
        //Intent intent = new Intent(activity, MainActivity.class);
        //intent.putExtra(INTENT_FIRSTLOGIN, isFirst);
        //startActivity(activity, intent);
    }

    /**
     * 调用系统打电话
     *
     * @param phone
     */
    public static void callPhone(String phone) {
        callPhone("tel:" + phone, "");
    }

    public static void callPhone(String url, String tip) {
        try {
            TelephonyManager phoneMgr = (TelephonyManager) BaseApplication.curContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String simNum = phoneMgr.getSimSerialNumber();
            if (simNum == null || simNum.equals("")) {
                ToastUtils.showToast("当前无Sim卡 , 不能拨打电话!");
                return;
            }
            Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
            BaseApplication.curContext.startActivity(phoneIntent);
            if (!TextUtils.isEmpty(tip)) {
                ToastUtils.showToast(tip);
            }
        } catch (Exception e) {
            ToastUtils.showToast("请开放应用拨打电话权限");
        }
    }

    /**
     * 调用系统更多分享
     *
     * @param activity
     * @param content
     */
    public static void shareInSystem(Activity activity, String content) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, content);
            intent.setType("text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivityForResult(Intent.createChooser(intent, "更多分享"), 7777);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoLogin(Context context) {
        gotoLogin(context, null);
    }

    public static void gotoLogin(Context context, String loginName) {
        gotoLogin(context, loginName, false);
    }
    
    /**
     * @param context   Context
     * @param loginName 登录账号
     * @param tick      是否被踢下线
     */
    public static void gotoLogin(Context context, String loginName, boolean tick) {
        Intent intent = new Intent(context.getPackageName() + ".login");
        intent.putExtra(INTENT_SIMPLE, tick);
        if (!TextUtils.isEmpty(loginName)) {
            intent.putExtra(INTENT_MOBILE, loginName);
        }
        if (context instanceof Activity) {
            startActivity((Activity) context, intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    
    public static void gotoLogin(Activity activity) {
        Intent intent = new Intent(activity.getPackageName() + ".login");
        startActivity(activity, intent);
    }

    public static void gotoLogin(Activity activity, String mobile) {
        Intent intent = new Intent(activity.getPackageName() + ".login");
        intent.putExtra(INTENT_MOBILE, mobile);
        startActivity(activity, intent);
    }

    public static void startActivityFromFragmentForResult(FragmentActivity activity, Fragment fragment, Class<?> clazz, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityFromFragment(activity, fragment, intent, requestCode);
    }
    
    public static void startActivityFromFragmentForResult(FragmentActivity activity, Fragment fragment, Intent intent, int requestCode) {
        startActivityFromFragment(activity, fragment, intent, requestCode);
    }
}
