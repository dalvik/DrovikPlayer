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
public class TiangoImageListResp extends DataResp {
    /**
             返回字段
             字段  类型  说明
            id  long    图片ID编码，
            title   string  标题，信息标题
            url string  web浏览地址 URL分享地址
            img string  图片简介
            galleryclass    int 分类
            count   int 访问数
            rcount  int 评论数
            fcount  int 收藏数
            time    long    发布时间
            List    Picture 图片 主要有 scr 字段
            
        {
        "status":true,
        "count":21,
        "fcount":0,
        "galleryclass":3,
        "id":1,
        "url":"http://www.tngou.net/tnfs/show/1",
        "img":"/ext/150714/aeb85cdb34f325ccfb3ae0928f846d2d.jpg",
        "rcount":0,
        "size":18,
        "time":1436874237000,
        "title":"絕對吸引眼球"，
        "list":"[
        {\"gallery\":1,\"id\":1,
        \"src\":\"/ext/150714/aeb85cdb34f325ccfb3ae0928f846d2d.jpg\"},
        {……}]"
        }
     */
    
    public String gallery;
    public int id;
    public String src;

}
