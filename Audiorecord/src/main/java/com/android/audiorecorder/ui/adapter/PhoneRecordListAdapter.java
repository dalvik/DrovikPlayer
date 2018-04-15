package com.android.audiorecorder.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.engine.AudioRecordSystem;
import com.android.audiorecorder.provider.FileDetail;
import com.android.audiorecorder.ui.stickyheadergridview.MyImageView;
import com.android.audiorecorder.ui.stickyheadergridview.NativeImageLoader;
import com.android.audiorecorder.utils.DateUtil;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

public class PhoneRecordListAdapter extends BaseAdapter implements
		StickyGridHeadersSimpleAdapter {

	private List<FileDetail> list;
	private LayoutInflater mInflater;
	private GridView mGridView;
	private Resources mResource;
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	private int mDefaultResource;

	public PhoneRecordListAdapter(Context context, List<FileDetail> list,
                                  GridView mGridView) {
		mResource = context.getResources();
		this.list = list;
		mInflater = LayoutInflater.from(context);
		this.mGridView = mGridView;
		mDefaultResource = R.drawable.ic_default_sound_record_thumb;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public FileDetail getItem(int position) {
		if(position>=list.size()){
			return null;
		}
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder;
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_phone_record_list, parent, false);
			mViewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.grid_item);
			mViewHolder.mCountTextView = (TextView) convertView.findViewById(R.id.grid_item_extra);
			convertView.setTag(mViewHolder);
			
			 //用来监听ImageView的宽和高  
			mViewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {
                  
                @Override  
                public void onMeasureSize(int width, int height) {  
                    mPoint.set(width, height);  
                }  
            }); 
			
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		String path = "";
		FileDetail detail = getItem(position);
		Bitmap bitmap = null;
		if(detail != null) {
			String callDuration = com.android.library.utils.DateUtil.timeParse(detail.getDuration());
			String callName = detail.getDisplayName();
			String callType = "";
			if(TextUtils.isEmpty(callName)){
				if(!AudioRecordSystem.PHONE_NUMBER_UNKNOWN.equalsIgnoreCase(detail.getPhoneNumber())) {
					callName = detail.getPhoneNumber();
				} else {
					callName = "";
				}
			}
			if(detail.getCallPhoneType() == AudioRecordSystem.PHONE_CALL_IN) {
				callType = mResource.getString(R.string.phone_record_in_call);
			} else if(detail.getCallPhoneType() == AudioRecordSystem.PHONE_CALL_OUT){
				callType = mResource.getString(R.string.phone_record_out_call);
			}
			String extraString = DateUtil.formatyyMMDDHHmm2(detail.getDownLoadTime()) + " " + callType + "\n";
			extraString += callDuration + " " + callName;
			mViewHolder.mCountTextView.setText(extraString);
			mViewHolder.mCountTextView.setVisibility(View.VISIBLE);
			bitmap = detail.getHeader();
			mViewHolder.mImageView.setTag(path);
			if(!TextUtils.isEmpty(path)) {
				bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint,
						new NativeImageLoader.NativeImageCallBack() {

							@Override
							public void onImageLoader(Bitmap bitmap, String path) {
								ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
								if (bitmap != null && mImageView != null) {
									mImageView.setImageBitmap(bitmap);
								}
							}
						});
			}
		}
		if (bitmap != null) {
			mViewHolder.mImageView.setImageBitmap(bitmap);
		} else {
			mViewHolder.mImageView.setImageResource(mDefaultResource);
		}
		return convertView;
	}
	

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder mHeaderHolder;
		if (convertView == null) {
			mHeaderHolder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.header, parent, false);
			mHeaderHolder.mTextView = (TextView) convertView.findViewById(R.id.header);
			convertView.setTag(mHeaderHolder);
		} else {
			mHeaderHolder = (HeaderViewHolder) convertView.getTag();
		}
		mHeaderHolder.mTextView.setText(list.get(position).getTime());
		return convertView;
	}

	public static class ViewHolder {
		public MyImageView mImageView;
		public TextView mCountTextView;
	}

	public static class HeaderViewHolder {
		public TextView mTextView;
	}

	@Override
	public long getHeaderId(int position) {
		return list.get(position).getSection();
	}

}
