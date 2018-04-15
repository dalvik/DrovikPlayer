/**
 * Copyright © 2016 浙江大华. All rights reserved.
 *
 * @title: IPlatform.java
 * @description: TODO
 * @author: 23536
 */
package com.android.media;

/**
 * @description: 网络获取文件列表信息接口
 * @author: 23536
 */
public interface IPlatForm {

    /**
     *
     * @title: getTypeList
     * @description: 获取类型列表
     * @return String
     */
    public int getTypeList();

    /**
     *
     * @title: getThumbList
     * @description: 获取缩略图相册列表
     * @param json
     * @return String
     */
    public int getThumbList(String json);

    /**
     *
     * @title: getFileList
     * @description: 获取文件列表
     * @param json
     * @return String
     */
    public int getImageList(String json);

}
