package com.drovik.player.video.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crixmod.sailorcast.SailorCast;
import com.crixmod.sailorcast.model.SCAlbum;
import com.crixmod.sailorcast.model.SCAlbums;
import com.crixmod.sailorcast.model.SCChannel;
import com.crixmod.sailorcast.utils.ImageTools;
import com.drovik.player.R;
import com.drovik.player.video.Const;
import com.drovik.player.video.ui.MovieDetailActivity;

public class MovieListAdapter extends BaseAdapter {
    private Context mContext;
    private int mColumns = 3;
    private SCChannel mChannel;
    private int mIndex;

    private SCAlbums mAlbums = new SCAlbums();

    private class ViewHolder {
            LinearLayout resultContainer;
            ImageView videoImage;
            TextView videoTitle;
            TextView videoTip;
    }

    public MovieListAdapter(Context mContext, SCChannel channel, int index) {
        this.mContext = mContext;
        this.mChannel = channel;
        this.mIndex = index;
    }

    public void clear() {
        mAlbums.clear();
    }

    public void addAlbum(SCAlbum album) {
        mAlbums.add(album);
    }

    @Override
    public int getCount() {
        return mAlbums.size();
    }

    @Override
    public SCAlbum getItem(int i) {
        return mAlbums.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SCAlbum album = getItem(i);
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = getOneColumnVideoRowView(viewGroup, viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        setupViewHolder(view,i,viewHolder,album);

        return view;
    }

    private void setupViewHolder(View view, int i, final ViewHolder viewHolder, final SCAlbum album) {
        viewHolder.videoTitle.setText(album.getTitle());
        int x = 0,y = 0;
        String imageUrl = null;
        if(mColumns == 3) {
            Point point = ImageTools.getGridVerPosterSize(mContext, mColumns);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(point.x,point.y);
            viewHolder.videoImage.setLayoutParams(params);
            x = point.x;
            y = point.y;

            if(album.getVerImageUrl() != null)
                imageUrl = album.getVerImageUrl();
        }

        if(mColumns == 2) {
            Point point = ImageTools.getGridHorPosterSize(mContext,mColumns);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(point.x,point.y);
            viewHolder.videoImage.setLayoutParams(params);
            x = point.x;
            y = point.y;
            if(album.getHorImageUrl() != null)
                imageUrl = album.getHorImageUrl();
        }


        if(imageUrl != null) {
            ImageTools.displayImage(viewHolder.videoImage,imageUrl,x,y);
        } else {
            if(mColumns == 2)
                viewHolder.videoImage.setImageDrawable(SailorCast.getResource().getDrawable(R.drawable.loading_hor));
            if(mColumns == 3)
                viewHolder.videoImage.setImageDrawable(SailorCast.getResource().getDrawable(R.drawable.loading));
        }

        viewHolder.videoTip.setText("");
        viewHolder.videoTip.setVisibility(View.GONE);
        if(album.getTip() != null && !album.getTip().isEmpty()) {
            viewHolder.videoTip.setText(album.getTip());
            viewHolder.videoTip.setVisibility(View.VISIBLE);
        }

        viewHolder.resultContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mpdIntent = new Intent(mContext, MovieDetailActivity.class)
                        .putExtra(Const.ALUMB_DETAIL, album)
                        .putExtra(Const.CHANNEL_ID, mChannel.getChannelID())
                        .putExtra(Const.INDEX_ID, mIndex);
                mContext.startActivity(mpdIntent);
            }
        });
    }

    private View getOneColumnVideoRowView(ViewGroup viewGroup, ViewHolder viewHolder) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View itemView;
        if(mColumns == 2)
            itemView = inflater.inflate(R.layout.item_gridview_albumlist_2,viewGroup,false);
        else
            itemView = inflater.inflate(R.layout.item_gridview_albumlist_3,viewGroup,false);

        viewHolder.videoImage = (ImageView) itemView.findViewById(R.id.video_image);
        viewHolder.videoTitle = (TextView) itemView.findViewById(R.id.video_title);
        viewHolder.videoTip = (TextView) itemView.findViewById(R.id.video_tip);
        viewHolder.resultContainer = (LinearLayout)itemView.findViewById(R.id.search_result);

        itemView.setTag(viewHolder);
        return itemView;
    }

    public void setColumns(int mColumns) {
        this.mColumns = mColumns;
    }

}
