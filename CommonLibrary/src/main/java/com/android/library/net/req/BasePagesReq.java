package com.android.library.net.req;

/**
 * Created by Sean.xie on 2015/11/24.
 */
public class BasePagesReq extends DataReq {

    /**
     * 查询多少条
     */
    public Integer length = 20;
    /**
     * 依稀量。 每几条开始查询
     */
    public Integer offset;
    /**
     * 查询下一页关键字
     */
    public String nextKeyward;
}
