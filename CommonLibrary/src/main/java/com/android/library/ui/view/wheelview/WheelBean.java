package com.android.library.ui.view.wheelview;

/**
 * 自定义滑动轮实体
 */
public class WheelBean<T> {
    public int mIndex; // 当前列表中的索引号
    public String mText; // 显示的内容
    public T data; //数据

    public WheelBean(int index, String text, T data) {
        mIndex = index;
        mText = text;
        this.data = data;
    }

}
