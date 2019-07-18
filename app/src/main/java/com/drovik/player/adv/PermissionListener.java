package com.drovik.player.adv;

import java.util.List;

public interface PermissionListener {

    /**
     * 授权成功
     */
    void onGranted();

    /**
     * 授权部分
     * @param grantedPermission
     */
    void onGranted(List<String> grantedPermission);

    /**
     * 拒绝授权
     * @param deniedPermission
     */
    void onDenied(List<String> deniedPermission);
}
