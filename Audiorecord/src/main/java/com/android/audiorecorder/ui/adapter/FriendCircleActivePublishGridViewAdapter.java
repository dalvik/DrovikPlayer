package com.android.audiorecorder.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.audiorecorder.R;

import java.util.ArrayList;
import java.util.List;

public class FriendCircleActivePublishGridViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<Bitmap> list = new ArrayList<Bitmap>();

	public FriendCircleActivePublishGridViewAdapter() {
		super();
	}

	/**
	 * 获取列表数据
	 * 
	 * @param list
	 */
	public void setList(List<Bitmap> list) {
		this.list = list;
		this.notifyDataSetChanged();
		Log.e(" 3333 ", this.list.size() + "");
	}

	public FriendCircleActivePublishGridViewAdapter(Context mContext, List<Bitmap> list) {
		super();
		this.mContext = mContext;
		this.list = list;
		Log.e(" 2222 ", list.size() + "");
	}

	@Override
	public int getCount() {
		Log.e("  ", list.size() + "");
		if (list == null) {
			return 1;
		} else if (list.size() == 9) {
			return 9;
		} else {
			return list.size() + 1;
		}
	}

	@Override
	public Object getItem(int position) {
		if (list != null && list.size() == 9) {
			return list.get(position);
		}

		else if (list == null || position - 1 < 0 || position > list.size()) {
			return null;
		} else {
			return list.get(position - 1);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_friend_circle_active_publish_item,
					null);
			holder = new ViewHolder();
			holder.item_grida_image = (ImageView) convertView.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (isShowAddItem(position)) {
			holder.item_grida_image.setImageResource(R.drawable.friend_circle_active_add_image);
			holder.item_grida_image.setBackgroundResource(R.color.gray);

		} else {
			holder.item_grida_image.setImageBitmap(list.get(position));
			holder.item_grida_image.setBackgroundResource(R.color.bg_gray);
		}
		return convertView;
	}

	/**
	 * 判断当前下标是否是最大值
	 * 
	 * @param position
	 *            当前下标
	 * @return
	 */
	private boolean isShowAddItem(int position) {
		int size = list == null ? 0 : list.size();
		return position == size;
	}

	class ViewHolder {
		ImageView item_grida_image;
	}

}
