package com.android.library.ui.view.wheelview;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.library.R;

import java.util.ArrayList;


/**
 * 自定义滚动轮适配器
 */
public class WheelTextAdapter<T> extends BaseAdapter {
    public int selectPos = -1;
    ArrayList<WheelBean<T>> items = null;
    int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    int mHeight = 50;
    Context mContext = null;

    public WheelTextAdapter(Context context, int mHeight) {
        mContext = context;
        this.mHeight = mHeight;
    }

    public void setItems(ArrayList<WheelBean<T>> data) {
        items = data;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return (null != items) ? items.size() : 0;
    }

    @Override
    public WheelBean<T> getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;
        if (null == convertView) {
            convertView = new TextView(mContext);
            convertView.setLayoutParams(new TosGallery.LayoutParams(mWidth, mHeight));
            textView = (TextView) convertView;
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.5f);
            textView.setTextColor(mContext.getResources().getColor(R.color.wheel_text_normal_color));
            textView.setMaxLines(2);
            textView.setMinHeight(mContext.getResources().getDimensionPixelSize(R.dimen.wheel_item_height));
            textView.setEllipsize(TruncateAt.END);
        }

        if (null == textView) {
            textView = (TextView) convertView;
        }

        WheelBean info = items.get(position);
        textView.setText(info.mText);
        // textView.setTextColor(info.mColor);
        if (selectPos == position) {
            textView.setTextColor(mContext.getResources().getColor(R.color.wheel_text_focus_color));
        } else {
            textView.setTextColor(mContext.getResources().getColor(R.color.wheel_text_normal_color));
        }

        return convertView;
    }
}
