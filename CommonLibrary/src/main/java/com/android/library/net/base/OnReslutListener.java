/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: OnReslutListener.java
 * @description: 结果请求监听处理接口
 * @author: 23536   
 * @date: 2016年1月5日 下午2:42:06 
 */
package com.android.library.net.base;

/** 
 * @description: 处理请求结果
 * @author: 23536
 * @date: 2016年1月5日 下午2:42:06  
 */
public interface OnReslutListener {
    /**
     * 成功
     *
     * @param response
     */
    void onSucess(String response);

    /**
     * 失败
     */
    void onFailed();
}
