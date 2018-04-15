package com.android.library.ui.manager;

public abstract class ImageThreadExecutor {
    /**
     * 执行
     *
     * @param task
     */
    public abstract void execute(Runnable task);
}
