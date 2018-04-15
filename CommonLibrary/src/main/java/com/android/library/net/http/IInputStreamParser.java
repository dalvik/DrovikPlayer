package com.android.library.net.http;

import java.io.InputStream;

public interface IInputStreamParser<T> {

    /**
     * 解析流
     *
     * @param inputStream 实现中不需关系inputStream的关闭
     * @return
     */
    T parser(InputStream inputStream);

}
