package com.android.audiorecorder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.RecorderFile;
import com.android.audiorecorder.audio.MusicUtils;
import com.android.audiorecorder.ui.AudioRecordList;
import com.android.audiorecorder.utils.FileUtils;
import com.android.audiorecorder.utils.StringUtil;

import java.util.List;

public class RecordListAdapter extends BaseAdapter {

    private Context mContext;
    
    private LayoutInflater mInflater;
    
    private List<RecorderFile> mFileList;
    
    private int mPlayId;
    
    private int mState;
    
    private ITaskClickListener mTaskClickListener;

    public RecordListAdapter(Context context, List<RecorderFile> fileList){
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mFileList = fileList;
        mState = AudioRecordList.IDLE;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public RecorderFile getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        RecorderFile file = getItem(position);
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.recordlist_items, null);
            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            viewHolder.duration = (TextView)convertView.findViewById(R.id.duration);
            viewHolder.size = (TextView)convertView.findViewById(R.id.size);
            viewHolder.play = (ImageButton)convertView.findViewById(R.id.play);
            viewHolder.play.setTag(position);
            viewHolder.play.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    if(mTaskClickListener != null){
                        mTaskClickListener.onTaskClick(StringUtil.toInt(viewHolder.play.getTag()), AudioRecordList.ITEM_OPERATION_PLAY);
                    }
                }
            });
            viewHolder.play_indicator = (ImageView)convertView.findViewById(R.id.play_indicator);
            viewHolder.ibListItemMenu = (ImageButton)convertView.findViewById(R.id.list_item_menu);
            viewHolder.ibListItemMenu.setTag(position);
            viewHolder.ibListItemMenu.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    
                    PopupMenu localPopupMenu = new PopupMenu(mContext, viewHolder.ibListItemMenu);
                    Menu localMenu = localPopupMenu.getMenu();
                    if(StringUtil.toInt(viewHolder.ibListItemMenu.getTag()) == mPlayId){
                        if(mState == AudioRecordList.PLAY){
                            localMenu.add(1, AudioRecordList.ITEM_OPERATION_PLAY, 1, R.string.pause);
                        } else {
                            localMenu.add(1, AudioRecordList.ITEM_OPERATION_PLAY, 1, R.string.play);
                        }
                    } else {
                        localMenu.add(1, AudioRecordList.ITEM_OPERATION_PLAY, 1, R.string.play);
                    }
                    localMenu.add(1, AudioRecordList.ITEM_OPERATION_DETAILS, 1, R.string.information);
                    localMenu.add(1, AudioRecordList.ITEM_OPERATION_DELETE, 1, R.string.delete_item);
                    localPopupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                        
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(mTaskClickListener != null){
                                mTaskClickListener.onTaskClick(StringUtil.toInt(viewHolder.play.getTag()), item.getItemId());
                            }
                            return true;
                        }
                    });
                    localPopupMenu.show();
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder =  (ViewHolder) convertView.getTag();
        }
        if(mPlayId == position){
            if(mState == AudioRecordList.PLAY){
                viewHolder.play.setBackgroundResource(R.drawable.playpause_button_selector);
            }else{
                viewHolder.play.setBackgroundResource(R.drawable.play_button_selector);
            }
            viewHolder.play_indicator.setVisibility(View.VISIBLE);
        } else {
            viewHolder.play_indicator.setVisibility(View.INVISIBLE);
            viewHolder.play.setBackgroundResource(R.drawable.play_button_selector);
        }
        viewHolder.title.setText(file.getName());
        viewHolder.duration.setText(MusicUtils.makeTimeString(mContext, file.getDuration()));
        viewHolder.size.setText(FileUtils.formetFileSize(file.getSize()));
        return convertView;
    }
    
    public void setPlayId(int id, int state){
        this.mPlayId = id;
        this.mState = state;
    }
    
    public int getPlayState(){
        return this.mState;
    }
    
    public int getPlayId(){
        return this.mPlayId;
    }
    
    public void setTaskClickListener(ITaskClickListener listener){
        this.mTaskClickListener = listener;
    }
    
    public interface ITaskClickListener{
        
        void onTaskClick(int index, int itemAction);
    }
    
    class ViewHolder {
      TextView duration;
      ImageButton ibListItemMenu;
      String path;
      ImageButton play;
      ImageView play_indicator;
      int position;
      TextView size;
      TextView title;
    }
}
