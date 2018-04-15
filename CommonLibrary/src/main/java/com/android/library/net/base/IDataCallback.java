/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: IDataCallback.java
 * @description: 在UI层注册该回调，直接更新界面，但是不能在此函数中做耗时操作
 * @author: 23536   
 * @date: 2015年12月23日 下午2:52:02 
 */
package com.android.library.net.base;

/** 
 * @description: 数据回调接口
 * @author: 23536
 * @date: 2015年12月23日 下午2:52:02  
 */
public interface IDataCallback {

    public void onCallback(int what, int arg1, int arg2, Object obj);
    
}
