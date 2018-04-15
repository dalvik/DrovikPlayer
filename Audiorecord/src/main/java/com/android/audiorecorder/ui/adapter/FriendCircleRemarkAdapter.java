package com.android.audiorecorder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveRemarkResp;

import net.tsz.afinal.FinalBitmap;

import java.util.List;

public class FriendCircleRemarkAdapter extends BaseAdapter {
	private Context context;
	private List<FriendCircleActiveRemarkResp> mRemarks;
	private boolean mSelf;// true是自己的动态  false 他人的动态
	private FinalBitmap finalBit;

	public FriendCircleRemarkAdapter(Context context, List<FriendCircleActiveRemarkResp> remarks, boolean self) {
		this.context = context;
		this.mRemarks = remarks;
		this.mSelf = self;
		finalBit = FinalBitmap.create(context);
		finalBit.configLoadfailImage(R.drawable.user_logo);
	}

	public void setData(List<FriendCircleActiveRemarkResp> remarks) {
		this.mRemarks = remarks;
	}

	@Override
	public int getCount() {
		return mRemarks == null ? 0 : mRemarks.size();
	}

	@Override
	public Object getItem(int position) {
		return mRemarks == null ? null : mRemarks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = new Holder();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_friend_active_remark_item, null);
			holder.nickName = (TextView) convertView.findViewById(R.id.friend_circle_message_favout_name_id);
			holder.imageView = (ImageView) convertView.findViewById(R.id.friend_circle_message_favout_icon_id);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		FriendCircleActiveRemarkResp remark = (FriendCircleActiveRemarkResp) mRemarks.get(position);
		if(mSelf){
		    holder.imageView.setVisibility(View.VISIBLE);
		    //finalBit.display(holder.imageView,"http://g.hiphotos.baidu.com/image/pic/item/960a304e251f95caff8f2fa5cb177f3e670952ae.jpg");
		    finalBit.display(holder.imageView, remark.headIcon);
		} else {
		    holder.nickName.setVisibility(View.VISIBLE);
		    holder.nickName.setText(remark.nickname);
		    
		}
		return convertView;
	}

	private class Holder {
	    public TextView nickName;
		public ImageView imageView;
	}
}
