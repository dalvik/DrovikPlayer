package com.android.library.net.http;

import java.io.OutputStream;

public interface IOutputStreamParse<T> {
    /**
     * 上传数据,结构处理
     *
     * @param outputStream 服务器流
     * @param data         上传的内容
     */
    void parser(OutputStream outputStream, T data) throws Exception;
}
