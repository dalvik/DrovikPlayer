/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: IBaseReq.java
 * @description: TODO
 * @author: 23536   
 * @date: 2015年12月23日 下午3:28:45 
 */
package com.android.library.net.req;

import org.ksoap2.serialization.SoapObject;

/** 
 * @description: webservice请求基类
 * @author: 23536
 * @date: 2015年12月23日 下午3:28:45  
 */
public interface WebServiceReqest {

    /**
     *  
     * @title: getMethod 
     * @description: 调用的方法名
     * @return
     * @return: String
     */
    public String getMethod();

    /**
     * 
     * @title: getNamespace 
     * @description: 获取请求的webservice namespace
     * @return
     * @return: String
     */
    public String getNamespace();
    /**
     * 
     * @title: getUrl 
     * @description: 请求的URL
     * @return
     * @return: String
     */
    public String getUrl();
    
    /**
     * 
     * @title: getSoapObject 
     * @description: 获取参数封装的对象
     * @return
     * @return: SoapObject
     */
    public SoapObject getSoapObject();
    
}
