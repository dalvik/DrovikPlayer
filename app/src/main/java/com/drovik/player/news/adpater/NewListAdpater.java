package com.drovik.player.news.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drovik.player.R;
import com.drovik.player.video.VideoBean;

import java.util.List;

/**
 * Created by 23536 on 2017-11-17.
 */
public class NewListAdpater extends RecyclerView.Adapter {

    private Context mContext;
    private List<VideoBean> mFileList;

    public final static int LAYOUT_TYPE_LIST = 1;
    public final static int LAYOUT_TYPE_GRID = 2;
    private int mLayoutType;

    protected OnItemClickListener mOnItemClickListener;
    private boolean mIsChooseMode;
    private String TAG = "VideoListAdpater";

    public NewListAdpater(Context context, List<VideoBean> fileList){
        this.mContext = context;
        this.mFileList = fileList;
        mLayoutType = LAYOUT_TYPE_GRID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder=> viewType: " + viewType);
        if(viewType == LAYOUT_TYPE_GRID) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_video_grid, parent, false);
            VideoAdpaterHolder holder = new VideoAdpaterHolder(mContext, itemView);
            setListener(parent, holder, viewType);
            return holder;
        } else if(viewType == LAYOUT_TYPE_LIST){
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_video_list, parent, false);
            VideoListAdpaterHolder holder = new VideoListAdpaterHolder(mContext, itemView);
            setListener(parent, holder, viewType);
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder=> position: " + position + " holder： " + holder);
        VideoBean bean = getItem(position);
        if(holder instanceof  VideoAdpaterHolder) {
            VideoAdpaterHolder directoryAdpaterHolder = ((VideoAdpaterHolder) holder);
            if (bean.isDownload) {
                directoryAdpaterHolder.mDownload.setVisibility(View.VISIBLE);
            } else {
                directoryAdpaterHolder.mDownload.setVisibility(View.INVISIBLE);
            }
            if (bean.isSelected) {
                directoryAdpaterHolder.mSelected.setVisibility(View.VISIBLE);
            } else {
                directoryAdpaterHolder.mSelected.setVisibility(View.INVISIBLE);
            }
            directoryAdpaterHolder.mName.setText(bean.title);
        } else {
            VideoListAdpaterHolder videiListAdpaterHolder = ((VideoListAdpaterHolder) holder);
            videiListAdpaterHolder.mName.setText(bean.title);
            videiListAdpaterHolder.mDuration.setText(bean.duration);
            //videiListAdpaterHolder.mSize.setText(Utils.formetFileSize(bean.size));
        }
    }

    @Override
    public int getItemCount() {
        return mFileList.size();
    }

    public VideoBean getItem(int position) {
        if(position<0 && position>mFileList.size()){
            return null;
        }
        return mFileList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mLayoutType;
    }

    public void setMode(boolean flag) {
        this.mIsChooseMode = flag;
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }

    public void setLayoutType(int layoutType) {
        this.mLayoutType = layoutType;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    protected void setListener(final ViewGroup parent, final com.android.library.ui.adapter.ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) {
            return;
        }
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder , position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ViewHolder holder, int position);
        boolean onItemLongClick(View view, ViewHolder holder, int position);
    }

    private static class VideoAdpaterHolder extends com.android.library.ui.adapter.ViewHolder{

        public ImageView mThumb;
        public ImageView mDownload; //标志是否已下载
        public TextView mName;
        private ImageView mSelected;

        public VideoAdpaterHolder(Context context, View itemView) {
            super(context, itemView);
            init(itemView);
        }

        private void init(View view) {
            mThumb = (ImageView) view.findViewById(R.id.item_video_covery);
            mDownload = (ImageView) view.findViewById(R.id.item_video_download);
            mName = (TextView) view.findViewById(R.id.item_video_name);
            mSelected = (ImageView) view.findViewById(R.id.item_video_selected);
        }
    }

    private static class VideoListAdpaterHolder extends com.android.library.ui.adapter.ViewHolder{

        public TextView mName;
        public TextView mDuration;
        public TextView mSize;

        public VideoListAdpaterHolder(Context context, View itemView) {
            super(context, itemView);
            init(itemView);
        }

        private void init(View view) {
            mName = (TextView) view.findViewById(R.id.item_video_name);
            mDuration = (TextView) view.findViewById(R.id.item_video_duration);
            mSize = (TextView) view.findViewById(R.id.item_video_size);
        }
    }
}
