package com.android.audiorecorder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.audiorecorder.R;

import java.util.List;

/**
 * * 实现对listView的循环滚动 
 *
 */
public class DonateListAdapter extends BaseAdapter {
	
	private List<String> list;
	private LayoutInflater mInflater;
	
	public DonateListAdapter(Context context, List<String> list){
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0 % list.size());
	}

	@Override
	public long getItemId(int arg0) {
		return arg0 % list.size();
	}
	@Override
	public View getView(int postition, View convertView, ViewGroup arg2) {
		ViewHoler viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHoler();
			convertView = mInflater.inflate(R.layout.layout_donate_list_item, null);
			viewHolder.donateAccountTv = (TextView) convertView.findViewById(R.id.br_donate_account_tv);
			viewHolder.donateNameTv = (TextView) convertView.findViewById(R.id.br_donate_name_tv);
			viewHolder.donateAmountTv = (TextView) convertView.findViewById(R.id.br_donate_amount_tv);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHoler) convertView.getTag();
		}
		String item = list.get(postition % list.size());
		String[] donateArray = item.split(";");
		if(donateArray != null && donateArray.length>2){
		    viewHolder.donateAccountTv.setText(donateArray[0]);//account
		    viewHolder.donateNameTv.setText(donateArray[1]);//name
		    viewHolder.donateAmountTv.setText(donateArray[2]);//amount
		}
		return convertView;
	}
	
	static class ViewHoler{
		TextView donateAccountTv;
		TextView donateNameTv;
		TextView donateAmountTv;
	}

}
