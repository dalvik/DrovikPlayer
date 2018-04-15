/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: PriorityThreadFactory.java
 * @description: 线程工厂
 * @author: 23536   
 * @date: 2015年12月23日 下午3:52:51 
 */
package com.android.library.net.base;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/** 
 * @description: 线程工厂
 * @author: 23536
 * @date: 2015年12月23日 下午3:52:51  
 */
public class PriorityThreadFactory implements ThreadFactory {

    private final int mPriority;
    private final AtomicInteger mNumber = new AtomicInteger();
    private final String mName;

    public PriorityThreadFactory(String name, int priority) {
        mName = name;
        mPriority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, mName + '-' + mNumber.getAndIncrement()) {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(mPriority);
                super.run();
            }
        };
    }
}
