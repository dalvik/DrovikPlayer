package com.drovik.player.ui.fragment;

public interface IHomeView {
    /**
     * 显示设备大小和剩余大小
     * @param available
     * @param total
     */
    public void showDeviceFree(int available, int total);
}
