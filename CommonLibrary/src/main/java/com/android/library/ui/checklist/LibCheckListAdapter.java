package com.android.library.ui.checklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibCheckListAdapter extends BaseAdapter {
	private Context mContext;
	private List<HashMap<String, Object>> mData;
	private Map<Integer, Boolean> checkedMap;

    //xutils
    private ImageOptions mImageOptions;
    
	public List<HashMap<String, Object>> getData() {
		return mData;
	}

	public LibCheckListAdapter(Context context, List<HashMap<String, Object>> data) {
		this.mContext = context;
		this.mData = data;
		checkedMap = new HashMap<Integer, Boolean>();
		setData();
		initXutil();
	}

	public Map<Integer, Boolean> getCheckedMap() {
		return checkedMap;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HashMap<String, Object> map = mData.get(position);
		final String name = map.get("name") + "";
		final String image = map.get("image") + "";
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.lib_layout_checklist_item, null);

			viewHolder = new ViewHolder();
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.lib_id_checklist_item_select_checkbox);
			viewHolder.tvCatalog = (TextView) convertView.findViewById(R.id.lib_id_checklist_item_catalog);
			viewHolder.name = (TextView) convertView.findViewById(R.id.lib_id_checklist_item_name);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.lib_id_checklist_item_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		boolean isShowTitle = false;

		viewHolder.checkBox.setChecked(checkedMap.get(Integer.parseInt(""+ map.get("id"))));
		if (isShowTitle) {
			viewHolder.tvCatalog.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tvCatalog.setVisibility(View.GONE);
		}
		x.image().bind(viewHolder.image, image, mImageOptions);
		viewHolder.name.setText(name);
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
	
	private void setData() {
        for (HashMap<String, Object> m : mData) {
            checkedMap.put(Integer.parseInt(m.get("id") + ""), false);
        }
    }
	
	static class ViewHolder {
		TextView tvCatalog;
		CheckBox checkBox;
		ImageView image;
		TextView name;
	}

}