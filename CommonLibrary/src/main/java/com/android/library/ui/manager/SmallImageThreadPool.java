package com.android.library.ui.manager;

import com.android.library.net.base.PriorityThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 图片下载专用
 *
 * @author Xielei
 */
public class SmallImageThreadPool extends ImageThreadExecutor {
    /**
     * 实例
     */
    private static SmallImageThreadPool instance = null;

    /**
     * 图片下载专用
     */
    private ThreadPoolExecutor executor = null;

    private SmallImageThreadPool() {
    }

    /**
     * 去实例
     *
     * @return
     */
    public static SmallImageThreadPool getInstance() {
        synchronized (SmallImageThreadPool.class) {
            if (instance == null) {
                PriorityThreadFactory threadFactory = new PriorityThreadFactory("image-thread",
                        android.os.Process.THREAD_PRIORITY_BACKGROUND);
                instance = new SmallImageThreadPool();
                instance.executor = new ThreadPoolExecutor(0, 2, 60L, TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(20), threadFactory,
                        new ThreadPoolExecutor.DiscardOldestPolicy());
            }
            return instance;
        }
    }

    /**
     * 执行
     *
     * @param task
     */
    @Override
    public void execute(Runnable task) {
        instance.executor.execute(task);
    }
}
