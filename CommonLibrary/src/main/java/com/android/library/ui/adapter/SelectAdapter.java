package com.android.library.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCommonActivity;


/**
 * 选择数据列表适配器
 */
public class SelectAdapter extends BaseListAdapter<String> {

    public SelectAdapter(BaseCommonActivity activity) {
        super(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.select_item_view, null);
            holder = new ViewHolder();
            holder.content = (TextView) convertView.findViewById(R.id.select_item_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.content.setText(items.get(position)); // 设置每项显示内容
        return convertView;
    }

    /**
     * 缓存类 *
     */
    class ViewHolder {
        TextView content;
    }
}
