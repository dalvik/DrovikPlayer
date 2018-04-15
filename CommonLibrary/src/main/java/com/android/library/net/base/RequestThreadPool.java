/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: DataSourceThreadPool.java
 * @description: 用户执行提交过来的数据请求，执行耗时操作
 * @author: 23536   
 * @date: 2015年12月23日 下午3:51:40 
 */
package com.android.library.net.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 
 * @description: 数据请求任务线程池
 * @author: 23536
 * @date: 2015年12月23日 下午3:51:40  
 */
public enum RequestThreadPool {
    instance;//单实例模式
    private ExecutorService executor;

    private RequestThreadPool() {
        PriorityThreadFactory threadFactory = new PriorityThreadFactory("request_thread_pool",
                android.os.Process.THREAD_PRIORITY_DEFAULT);
        executor = Executors.newCachedThreadPool(threadFactory);
    }

    public void execute(Runnable task) {
        if (executor != null && !executor.isShutdown()) {
            executor.execute(task);
        }
    }

    public void stop() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }
}
