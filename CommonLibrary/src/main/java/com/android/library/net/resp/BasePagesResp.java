package com.android.library.net.resp;

import java.util.ArrayList;

public class BasePagesResp<T> extends DataResp {

    private static final long serialVersionUID = 1L;

    //开始位置
    public int offset;
    //当页个数
    public int length;
    public int total;
    public ArrayList<T> rows;

    /**
     * 是否是后一条
     */
    public boolean lastPage;

    public String nextKeyward;
}
