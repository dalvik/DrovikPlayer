/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: TianGoImageThumbDataResp.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年4月8日 下午4:59:32 
 */
package com.android.media.image.tango;

import com.android.library.net.resp.DataResp;

/** 
 * @description: TODO
 * @author: 23536
 * @date: 2016年4月8日 下午4:59:32  
 */
public class TiangoImageThumbListResp extends DataResp {
    
    /**
             返回字段
             字段  类型  说明
            id  long    图库ID编码，
            title   string  标题，信息标题
            img string  图片简介
            gallertclass    int 分类
            count   int 访问数
            rcount  int 评论数
            fcount  int 收藏数
            time    long    发布时间
            
            {
            "status":true,
            "total":5,
            "tngou":[
            {"count":6,
            "fcount":0,
            "galleryclass":1,
            "id":18,
            "img":"/ext/150714/e76407c9a23da57a0f30690aa7917f3e.jpg",
            "rcount":0,
            "size":6,
            "time":1436878500000,
            "title":"MiStar"},
            {……}
            ]}
     */
    public int count;
    public int fcount;
    public int galleryClass;
    public int id;
    public String img;//缩略图路径
    public int rcount;
    public int size;
    public long time;
    public String title;

}
