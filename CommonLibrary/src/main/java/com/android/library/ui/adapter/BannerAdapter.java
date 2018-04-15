package com.android.library.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;

import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.manager.BitmapMgr;

/**
 * 广告-无限循环适配器
 */
public class BannerAdapter extends BaseListAdapter<Object> {

    public BannerAdapter(BaseCommonActivity activity) {
        super(activity);
    }

    @Override
    public int getCount() {
        if (super.getCount() < 2)// 如果只有一张图时不滚动
            return super.getCount();
        return Integer.MAX_VALUE;
    }

    /**
     * 取真实个数
     *
     * @return
     */
    public int getRealCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        position = position % super.getCount();
        if (convertView == null) {
            ImageView imageview = new ImageView(activity); // 实例化ImageView的对象
            imageview.setScaleType(ImageView.ScaleType.FIT_XY); // 设置缩放方式
            imageview.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT,
                    Gallery.LayoutParams.MATCH_PARENT));
            convertView = imageview;
        }
        BitmapMgr.loadBigBitmap(activity, (ImageView) convertView, (String)getItem(position));

        return convertView; // 返回ImageView
    }

}