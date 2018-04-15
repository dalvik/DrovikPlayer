package com.android.library.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * 终端信息
 */
public class ClientInfo {
    public final static String ANDOROID_ID = "ANDOROID_ID";

    public final static int NONET = 0;
    public final static int MOBILE_3G = 1;
    public final static int MOBILE_2G = 2;
    public final static int WIFI = 3;
    // 未知内容
    public static final String UNKNOWN = "unknown";

    private static ClientInfo instance;

    public String android_id = null;

    // 包名
    public String packageName = null;

    // 安卓系统版本号
    public String androidVer = null;
    // 厂商
    public String manufacturer = null;
    // 机型
    public String productModel = null;
    // imei
    public String imei = null;
    // imsi
    public String imsi = null;
    // 网络状态
    public int networkType;
    // 渠道号
    public String channelID = "www";
    // 屏幕大小
    public int width;
    public int height;
    public String screenSize = null;

    public ClientInfo(Context context) {

        packageName = context.getPackageName();
        androidVer = Build.VERSION.RELEASE;

        manufacturer = Build.MANUFACTURER;// 手机厂商
        productModel = Build.MODEL;// 手机型号

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        imsi = telephonyManager.getSubscriberId();

        if (android.text.TextUtils.isEmpty(imei)) {
            imei = UNKNOWN;
        }
        if (android.text.TextUtils.isEmpty(imsi)) {
            imsi = UNKNOWN;
        }
        resetNetWorkType(context);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
        if (width > height) {
            screenSize = height + "x" + width;
        } else {
            screenSize = width + "x" + height;
        }

        setChannelID(context);
        android_id = FileUtil.getFixedInfo(context,ANDOROID_ID);
        if (TextUtils.isEmpty(android_id)) {
            android_id = imei + System.currentTimeMillis() + String.valueOf(Math.random());
            android_id = MD5.toLowMD5String(android_id);
            FileUtil.saveFixedInfo(context, ANDOROID_ID, android_id);
        }
    }

    public static int getIntSdkVersion() {
        int sdk = 8;
        try {
            sdk = Integer.parseInt(android.os.Build.VERSION.SDK);
        } catch (Exception e) {
            e.printStackTrace();
            sdk = android.os.Build.VERSION.SDK_INT;
        }

        return sdk;
    }

    private static int check2GOr3GNet(Context context) {
        int mobileNetType = NONET;
        if (null == context) {
            return mobileNetType;
        }
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        int netWorkType = telMgr.getNetworkType();
        switch (netWorkType) {
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                // case TelephonyManager.NETWORK_TYPE_EVDO_B:
                mobileNetType = MOBILE_3G;
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
                mobileNetType = MOBILE_2G;
                break;
            default:
                mobileNetType = MOBILE_3G;
                break;
        }

        return mobileNetType;

    }

    public static ClientInfo getInstance(Context context) {
        if (null == instance) {
            instance = new ClientInfo(context);
        }
        return instance;
    }

    /**
     * 初始化
     */
    public static void initClientInfo(Context context) {
        getInstance(context);
    }

    private static String intToIp(int i) {
        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
    }

    private void setChannelID(Context context) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open("Channel")));
            channelID = br.readLine().trim();
        } catch (Exception e) {
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (Exception e) {
            }
        }
    }

    // 获取当前网络状态
    public void resetNetWorkType(Context context) {
        networkType = NONET;
        if (null == context) {
            return;
        }
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || (networkInfo.getState() != State.CONNECTED)) {
            return;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            networkType = check2GOr3GNet(context);
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            networkType = WIFI;
        } else {
            boolean b = ConnectivityManager.isNetworkTypeValid(nType);
            if (b) {
                networkType = MOBILE_3G; // 联通3G就跑这里
            }
        }
    }

}
