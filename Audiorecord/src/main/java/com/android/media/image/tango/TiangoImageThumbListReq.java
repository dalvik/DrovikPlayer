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
public class TiangoImageThumbListReq extends DataReq {

    /**
        1  http://www.tngou.net/tnfs/api/list  返回所有类型的图片
        2  http://www.tngou.net/tnfs/api/list?page=1&rows=10 根据页数返回数据
        3  http://www.tngou.net/tnfs/api/list?id=1 返回分类ID返回所有数据
        4  http://www.tngou.net/tnfs/api/list?id=2&page=2 根据分类ID和页数返回数据
        5  http://www.tngou.net/tnfs/api/list?rows=10 根据页数返回数据
     */
    public int id;
    public int page;
    public int rows;
}
