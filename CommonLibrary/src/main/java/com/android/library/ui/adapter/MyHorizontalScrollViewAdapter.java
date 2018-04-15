package com.android.library.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyHorizontalScrollViewAdapter{

	private LayoutInflater mInflater;
	private List mDataList;
	private Context mContext;

	private int mType;

	public MyHorizontalScrollViewAdapter(Context context, List data, int type) {
	    this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDataList = data;
		this.mType = type;
	}

	public int getCount() {
		return mDataList.size();
	}

	public Object getItem(int position) {
		return mDataList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		/*if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.treat_online_goods_item, parent, false);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.treat_online_goods_icon_iv);
			viewHolder.name = (TextView) convertView.findViewById(R.id.treat_online_goods_name_tv);
			viewHolder.number = (TextView) convertView.findViewById(R.id.treat_online_goods_number_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Goods goods = getItem(position);
		if(mType == TreatOnlinePublishActivity.GOODS_TYPE_HOT || TextUtils.isEmpty(goods.name)){
			viewHolder.name.setVisibility(View.GONE);
			viewHolder.number.setVisibility(View.GONE);
		} else {
			viewHolder.name.setText(goods.name);
			viewHolder.number.setText(mContext.getString(R.string.treat_online_choose_good_price_number, goods.price, goods.sellCount));
		}
		BitmapMgr.loadBitmap(viewHolder.icon, goods.imageUrl, R.drawable.treat_online_choose_add_goods);*/
		return convertView;
	}

	class ViewHolder {
		ImageView icon;
		TextView name;
		TextView number;
	}

}
