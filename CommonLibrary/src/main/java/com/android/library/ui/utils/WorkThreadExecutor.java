package com.android.library.ui.utils;


import com.android.library.net.utils.LogUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkThreadExecutor {
    private static final String TAG = "WorkThreadExecutor";
    /**
     * 实例
     */
    private static WorkThreadExecutor instance = null;

    /**
     * 工作线程
     */
    private ExecutorService executor = null;

    private WorkThreadExecutor() {
    }

    public static WorkThreadExecutor getInstance() {
        if (instance == null) {
            instance = new WorkThreadExecutor();
            instance.executor = new ThreadPoolExecutor(3, 50, 30l, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(50));
        }
        return instance;
    }

    /**
     * 提交任务
     *
     * @param task
     */
    public void execute(Runnable task) {
        LogUtil.i(TAG, "WorkThreadExecutor Task Id : " + task);
        instance.executor.execute(task);
    }
}
