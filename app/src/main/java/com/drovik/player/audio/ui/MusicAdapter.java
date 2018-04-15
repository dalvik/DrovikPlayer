package com.drovik.player.audio.ui;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.drovik.R;
import com.android.drovik.audio.MusicBean;
import com.android.library.net.utils.LogUtil;

import java.util.ArrayList;

public class MusicAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MusicBean> mList;
    private boolean editMode;

    private String TAG = "MusicAdapter";

    public MusicAdapter(Context context, ArrayList<MusicBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void refreshAdapter() {
        this.notifyDataSetChanged();
    }

    public void setData(ArrayList<MusicBean> list) {
        this.mList = list;
        this.notifyDataSetChanged();
    }

    public ArrayList<MusicBean> getData() {
        return this.mList;
    }

    public void setEditMode(boolean mode) {
        this.editMode = mode;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mList != null) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_music, null);
            holder = new ViewHolder();
            holder.isPlaying = (ImageView) convertView.findViewById(R.id.item_music_palying);
            holder.pic = (ImageView) convertView.findViewById(R.id.item_music_icon);
            holder.singer_album = (TextView) convertView.findViewById(R.id.item_music_singer_album);
            holder.song = (TextView) convertView.findViewById(R.id.item_music_song);
            holder.select = (ImageView) convertView.findViewById(R.id.item_music_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MusicBean musicBean = mList.get(position);
       // Glide.with(mContext).load("http://" + LoginManager.getInstance().baseIp+ ":" + LoginManager.getInstance().getP2PPort()  + "/" + musicBean.thumbpath).asBitmap().centerCrop().placeholder(R.drawable.music_body_initialize_n).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.pic);
        if (musicBean.singer == null || (musicBean.singer != null && musicBean.singer.isEmpty())) {
            musicBean.singer = mContext.getResources().getString(R.string.unknow);
        }
        if (musicBean.title == null || (musicBean.title != null && musicBean.title.isEmpty())) {
            musicBean.title = mContext.getResources().getString(R.string.unknow);
        }
        if (musicBean.album == null || (musicBean.album != null && musicBean.album.isEmpty())) {
            musicBean.album = mContext.getResources().getString(R.string.unknow);
        }
        LogUtil.d(TAG, "singer: " + musicBean.singer + " album: " + musicBean.album);
        holder.singer_album.setText(mContext.getString(R.string.singer_album, musicBean.singer, musicBean.album));
        holder.song.setText(musicBean.getTitle());
        //设置当前播放的歌曲信息
        /*final MusicBean currentMusicBean = NasApplication.getInstance().getPlayerEngineInterface().getCurrentSelectedMusic();
        if (currentMusicBean == null) {
            holder.isPlaying.setVisibility(View.GONE);
        } else {
            if (currentMusicBean.origpath.equals(musicBean.origpath) && NasApplication.getInstance().getPlayerEngineInterface().getPlaylist().isSelectedMusicInCurrentMusics()) {
                holder.isPlaying.setVisibility(View.VISIBLE);
            } else {
                holder.isPlaying.setVisibility(View.GONE);
            }
        }*/

        if (editMode) {
            holder.select.setVisibility(View.VISIBLE);
            if (musicBean.getSelect()) {
                holder.select.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.common_list_select_h));
            } else {
                holder.select.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.common_list_select_n));
            }
        } else {
            holder.select.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView isPlaying;
        ImageView pic;
        TextView song;
        TextView singer_album;
        ImageView select;
    }
}
