/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: BaseData.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年4月8日 下午4:53:19 
 */
package com.android.media.data;

import com.android.library.net.base.AbstractData;
import com.android.library.net.resp.DataResp;

import java.util.ArrayList;

/** 
 * @description: 基类数据
 * @author: 23536
 * @date: 2016年4月8日 下午4:53:19  
 */
public class TiangoThumbBaseData<T extends DataResp> extends AbstractData {

    /*public int id;//图库ID编码
    public String title;//标题，信息标题
    public String img;//图片简介
    public int galleryclass;//分类
    public int count;//访问数
    public int rcount;//评论数
    public int fcount;//收藏数
    public long time;//发布时间*/
    
    public boolean status;
    public int total;
    
    public ArrayList<T> tngou;
    
    /*public int count;
    public int fcount;
    public int galleryClass;
    public int id;
    public String url;
    public String img;//缩略图路径
    public int rcount;
    public int size;
    public long time;
    public String title;*/
    
}
