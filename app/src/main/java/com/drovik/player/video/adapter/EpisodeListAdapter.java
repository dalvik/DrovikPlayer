package com.drovik.player.video.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.crixmod.sailorcast.model.SCVideo;
import com.crixmod.sailorcast.utils.ImageTools;
import com.drovik.player.R;
import com.drovik.player.video.Const;
import com.drovik.player.video.parser.Episode;
import com.drovik.player.video.ui.GSYVideoPlayActivity;

public class EpisodeListAdapter extends BaseQuickAdapter<Episode, BaseViewHolder> {
	private Context context;
	private RelativeLayout.LayoutParams mImageViewLayoutParams;

	public EpisodeListAdapter(Context context) {
	    super(R.layout.item_video_episode_list, null);
		this.context = context;
		mImageViewLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

    @Override
    protected void convert(BaseViewHolder itemView, final Episode episode) {
        itemView.setText(R.id.episode_duration, episode.getDuration());
        itemView.setText(R.id.episode_order, context.getString(R.string.video_episode_index, episode.getOrder()));
        itemView.setText(R.id.episode_subtitle, episode.getSubTitle());
        ImageView cover = itemView.getView(R.id.episode_cover);
        ImageTools.displayImage(cover, episode.getImageUrl());
        itemView.getView(R.id.item_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SCVideo video = new SCVideo();
                video.setVideoTitle(episode.getShortTitle());
                Intent mpdIntent = new Intent(context, GSYVideoPlayActivity.class)
                        .putExtra(Const.SC_VIDEO, video)
                        .putExtra(Const.SC_TVID, episode.getTvId())//tvid
                        .putExtra(Const.SC_VID, episode.getVid())
                        .putExtra(Const.SC_TITLE, episode.getShortTitle())
                        .putExtra(Const.SC_PLAY_URL, episode.getPlayUrl());//vid
                context.startActivity(mpdIntent);
            }
        });
    }

}
