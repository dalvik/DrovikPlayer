/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: AbstractData.java
 * @description: 承载返回的数据内容
 * @author: 23536   
 * @date: 2016年1月5日 下午2:23:56 
 */
package com.android.library.net.base;

import com.android.library.net.resp.DataResp;

/** 
 * @description: 仅支持接收返回的数据
 * @author: 23536
 * @date: 2016年1月5日 下午2:23:56  
 */
public class AbstractData<T extends DataResp> extends DataStruct {
    //public String data;
}
