/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: HttpRequest.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年1月5日 下午5:06:29 
 */
package com.android.library.net.http;

import java.util.HashMap;

/** 
 * @description: TODO
 * @author: 23536
 * @date: 2016年1月5日 下午5:06:29  
 */
public interface IHttpRequest {

    public String getUrl();

    public byte[] getData();

    public HashMap<String, String> getHeader();
}
