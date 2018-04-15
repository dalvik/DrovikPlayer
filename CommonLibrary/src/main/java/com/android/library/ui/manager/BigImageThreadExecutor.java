package com.android.library.ui.manager;

import com.android.library.net.base.PriorityThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BigImageThreadExecutor extends ImageThreadExecutor {

    private static BigImageThreadExecutor instance = null;

    static {
        instance = new BigImageThreadExecutor();
        // 现在查看大图一次只能显示一张,为了不让用户等待太久一次只下载一张,如果做成显示多张,就换成和2+N
        instance.executor = instance.createExcutor();
    }

    private ThreadPoolExecutor executor = null;

    private BigImageThreadExecutor() {
    }

    public static BigImageThreadExecutor getInstance() {
        return instance;
    }

    private ThreadPoolExecutor createExcutor() {
        PriorityThreadFactory threadFactory = new PriorityThreadFactory("Big-Image-thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        executor = new ThreadPoolExecutor(0, 2, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(20),
                threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
        return executor;
    }

    /**
     * 提交任务
     *
     * @param task
     */
    @Override
    public void execute(Runnable task) {
        if (executor == null) {
            executor = createExcutor();
        }
        executor.execute(task);
    }
}
