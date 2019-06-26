package com.drovik.player.video.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crixmod.sailorcast.model.SCVideo;
import com.crixmod.sailorcast.utils.ImageTools;
import com.drovik.player.R;
import com.drovik.player.video.Const;
import com.drovik.player.video.parser.Episode;
import com.drovik.player.video.ui.GSYVideoPlayActivity;
import com.drovik.player.video.ui.VideoPlayActivity;

import java.util.List;

public class EpisodeListAdapter extends BaseAdapter {
	private Context context;
	private List<Episode> listItems;
	private LayoutInflater listContainer;
	private int itemViewResource;
	private int mNumColumns = 0;
	private int mItemHeight = 0;
	private RelativeLayout.LayoutParams mImageViewLayoutParams;

	public EpisodeListAdapter(Context context, List<Episode> data,
                              int resource) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context);
		listItems = data;
		itemViewResource = resource;
		mImageViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Episode getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView itemView = null;
        if(convertView == null) {
            convertView = listContainer.inflate(itemViewResource, null);
            itemView = new ListItemView();
            itemView.cover = convertView.findViewById(R.id.episode_cover);
            itemView.cover.setLayoutParams(mImageViewLayoutParams);
            itemView.order = (TextView)convertView.findViewById(R.id.episode_order);
            itemView.duration = (TextView)convertView.findViewById(R.id.episode_duration);
            itemView.subTitle = (TextView)convertView.findViewById(R.id.episode_subtitle);
            convertView.setTag(itemView);
        }else {
            itemView = (ListItemView)convertView.getTag();
        }
        final Episode episode = getItem(position);
        itemView.duration.setText(episode.getDuration());
        itemView.order.setText(context.getString(R.string.video_episode_index, episode.getOrder()));
        itemView.subTitle.setText(episode.getSubTitle());
        ImageTools.displayImage(itemView.cover, episode.getImageUrl());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SCVideo video = new SCVideo();
                video.setVideoTitle(episode.getShortTitle());
                Intent mpdIntent = new Intent(context, GSYVideoPlayActivity.class)
                        .putExtra(Const.SC_VIDEO, video)
                        .putExtra(Const.SC_TVID, episode.getTvId())//tvid
                        .putExtra(Const.SC_VID, episode.getVid());//vid
                context.startActivity(mpdIntent);
            }
        });
        return convertView;
    }

    public void setData(List<Episode> data) {
        listItems.clear();
        listItems.addAll(data);
        notifyDataSetChanged();
    }

    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        mImageViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
        notifyDataSetChanged();
    }
    
    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
    }

    public int getNumColumns() {
        return mNumColumns;
    }
    
    static class ListItemView {
        public ImageView cover;
        public TextView order;
        public TextView duration;
        public TextView subTitle;
    }

}
