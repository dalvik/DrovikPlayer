package com.android.library.ui.checklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.library.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

public class LibCheckListCheckedImgAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<LibCheckListCheckedImg> data;

	//xutils
    private ImageOptions mImageOptions;
    
	public ArrayList<LibCheckListCheckedImg> getData() {
		return data;
	}

	public LibCheckListCheckedImgAdapter(Context ct, ArrayList<LibCheckListCheckedImg> data) {
		this.mContext = ct;
		this.data = data;
		initXutil();
	}

	public void addImg(String name, String image, String id) {
		LibCheckListCheckedImg map = new LibCheckListCheckedImg();
		map.setName(name);
		map.setImage(image);
		map.setId(id);
		data.add(map);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LibCheckListCheckedImg map = data.get(position);
		final String image = map.getImage();
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.lib_layout_checkedlist_item, null);
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView) convertView.findViewById(R.id.lib_id_checkedlist_item_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		x.image().bind(viewHolder.image, image, mImageOptions);
		viewHolder.id = map.getId();
		return convertView;
	}

	private void initXutil(){
        mImageOptions = new ImageOptions.Builder()
        //.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
        //.setRadius(DensityUtil.dip2px(5))
        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
        .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
        // 加载中或错误图片的ScaleType
        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
        .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        .setLoadingDrawableId(R.drawable.lib_drawble_checkedlist_none)
        .setFailureDrawableId(R.drawable.lib_drawble_checkedlist_none)
        .build();
    }
	static class ViewHolder {
		String id;
		ImageView image;
	}
}