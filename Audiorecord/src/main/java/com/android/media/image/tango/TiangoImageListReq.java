/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: TianGoImageThumbDataResp.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年4月8日 下午4:59:32 
 */
package com.android.media.image.tango;

import com.android.library.net.req.DataReq;

/** 
 * @description: TODO
 * @author: 23536
 * @date: 2016年4月8日 下午4:59:32  
 */
public class TiangoImageListReq extends DataReq {

    /**
        1  http://www.tngou.net/tnfs/api/show?id=10
        2  http://www.tngou.net/tnfs/api/show?id=100
     */
    public int id;
    public int page;
    public int rows;
}
