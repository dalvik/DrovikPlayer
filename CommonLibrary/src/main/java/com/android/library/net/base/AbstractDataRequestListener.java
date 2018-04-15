/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: DataSourceListener.java
 * @description: TODO
 * @author: 23536   
 * @date: 2015年12月23日 下午3:18:58 
 */
package com.android.library.net.base;

/** 
 * @description: 数据分发接口
 * @author: 23536
 * @date: 2015年12月23日 下午3:18:58  
 */
public interface AbstractDataRequestListener<T> {
    /**
     * 成功
     */
    public static final int RESULT_SUCCESS = 5000;
    /**
     * 失败
     */
    public static final int RESULT_FAILED = 5001;
    /**
     * 内部错误
     */
    public static final int RESULT_ERROR = 0;
    
    /**
     * 网络错误
     */
    public static final int RESULT_NET_ERROR = -1;
    
    public static final int RESULT_PARSEERR = -2;
    
    /**
     * 
     * @title: sendMessage 
     * @description: 向上层传递消息
     * @param what 请求码
     * @param code 响应码
     * @param data 结果数据
     * @return: void
     */
    public void sendMessage(int what, int code, T data);
}
