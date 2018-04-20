package com.android.audiorecorder.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.gallery.bitmapfun.ImageFetcher;
import com.android.audiorecorder.provider.FileDetail;
import com.android.audiorecorder.provider.FileProvider;
import com.android.audiorecorder.ui.stickyheadergridview.MyImageView;
import com.android.audiorecorder.utils.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

public class FileTimeLineGridAdapter extends BaseAdapter implements
		StickyGridHeadersSimpleAdapter {

	private List<FileDetail> list;
	private LayoutInflater mInflater;
	private GridView mGridView;
	private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
	private int mDefaultResource;
	private ImageFetcher mImageFetcher;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private String TAG = "FileTimeLineGridAdapter";

	public FileTimeLineGridAdapter(Context context, List<FileDetail> list,
                                   GridView mGridView, ImageFetcher imageFetcher) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
		this.mGridView = mGridView;
		mDefaultResource = R.drawable.pictures_default;
		this.mImageFetcher = imageFetcher;
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(mDefaultResource)
				.showImageForEmptyUri(mDefaultResource)
				.showImageOnFail(mDefaultResource)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
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
			convertView = mInflater.inflate(R.layout.grid_item, parent, false);
			mViewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.grid_item);
			mViewHolder.mExtraTextView = (TextView) convertView.findViewById(R.id.grid_item_extra);
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
			if(detail.getFileType() == FileProvider.FILE_TYPE_JEPG) {
				path = detail.getFilePath();
				ImageLoader.getInstance().displayImage("file://" + path,  mViewHolder.mImageView, options);
			} else if(detail.getFileType() == FileProvider.FILE_TYPE_VIDEO) {
				path = detail.getThumbnailPath();
				if(TextUtils.isEmpty(path) && mImageFetcher != null) {
					mImageFetcher.loadVideoThumb(detail.getFilePath(), mViewHolder.mImageView);
				} else {
					ImageLoader.getInstance().displayImage("file://" + path,  mViewHolder.mImageView, options);
				}
			} else if(detail.getFileType() == FileProvider.FILE_TYPE_AUDIO || detail.getFileType() == FileProvider.FILE_TYPE_TEL) {
				mViewHolder.mImageView.setImageResource(R.drawable.ic_default_sound_record_thumb);
			}
			if(detail.getLaunchMode() == MultiMediaService.LUNCH_MODE_CALL) {
				String extraString = DateUtil.formatyyMMDDHHmm2(detail.getDownLoadTime()) + " " + String.valueOf(detail.getCount());
				if(!TextUtils.isEmpty(detail.getDisplayName())){
					extraString +=  "\n" + detail.getDisplayName();
				}
				mViewHolder.mExtraTextView.setText(extraString);
				mViewHolder.mExtraTextView.setVisibility(View.VISIBLE);
				mViewHolder.mImageView.setImageResource(R.drawable.ic_default_sound_record_thumb);
				//bitmap = detail.getHeader();
			} else if(detail.getLaunchMode() == MultiMediaService.LUNCH_MODE_MANLY) {
				String extraString = DateUtil.formatyyMMDDHHmm2(detail.getDownLoadTime()) + "\n";
				extraString = extraString + com.android.library.utils.DateUtil.timeParse(detail.getDuration());
				mViewHolder.mExtraTextView.setText(extraString);
				mViewHolder.mExtraTextView.setVisibility(View.VISIBLE);
				mViewHolder.mImageView.setImageResource(R.drawable.ic_default_sound_record_thumb);
			}
			/*mViewHolder.mImageView.setTag(path);
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
			}*/
		}
		/*if (bitmap != null) {
			mViewHolder.mImageView.setImageBitmap(bitmap);
		} else {
			mViewHolder.mImageView.setImageResource(mDefaultResource);
		}*/
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
		if(position < list.size()) {
			mHeaderHolder.mTextView.setText(list.get(position).getTime());
		}
		return convertView;
	}

	public static class ViewHolder {
		public MyImageView mImageView;
		public TextView mExtraTextView;
	}

	public static class HeaderViewHolder {
		public TextView mTextView;
	}

	@Override
	public long getHeaderId(int position) {
		return list.get(position).getSection();
	}

}
