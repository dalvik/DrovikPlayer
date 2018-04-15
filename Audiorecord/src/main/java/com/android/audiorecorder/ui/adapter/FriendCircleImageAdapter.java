package com.android.audiorecorder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.FriendCircleImage;

import net.tsz.afinal.FinalBitmap;

import java.util.List;

public class FriendCircleImageAdapter extends BaseAdapter {
	private Context context;
	private List paths;
	private boolean localtion;// true是本地图片 false 是网络图片
	private FinalBitmap finalBit;

	private AddImage add;

	public FriendCircleImageAdapter(Context context, List paths, boolean localtion) {
		this.context = context;
		this.paths = paths;
		this.localtion = localtion;
		finalBit = FinalBitmap.create(context);
	}

	public void setAddImage(AddImage add) {
		this.add = add;
	}


	public void setData(List<String> paths) {
		this.paths = paths;
	}

	@Override
	public int getCount() {
		return paths == null ? 0 : paths.size();
	}

	@Override
	public Object getItem(int position) {
		return paths == null ? null : paths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = new Holder();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.layout_friend_images, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
//		holder.imageView.setImageResource(R.drawable.aaa);
		FriendCircleImage ph = (FriendCircleImage) paths.get(position);
//		finalBit.display(holder.imageView,"http://g.hiphotos.baidu.com/image/pic/item/960a304e251f95caff8f2fa5cb177f3e670952ae.jpg");
		finalBit.display(holder.imageView, ph.imageUrl);
		return convertView;
	}

	private class Holder {
		public ImageView imageView;
	}

	/**
	 * 加载图片接口
	 * 
	 * @author jiangyue
	 * 
	 */
	public interface AddImage {
		public void addImage(ImageView view, String path);
	}
}
