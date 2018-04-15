package com.android.library.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class MetaDataUtil {

    /**
     * 获取Application MetaData
     *
     * @param ctx
     * @param key
     * @return
     */
    public static String getApplicationMetaData(Context ctx, String key) {
        try {
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_META_DATA);
            return packageInfo.applicationInfo.metaData.getString(key);
        } catch (Exception e) {
        }
        return null;
    }
}
