package com.android.audiorecorder.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.audiorecorder.DebugConfig;
import com.android.audiorecorder.R;
import com.android.audiorecorder.RecorderFile;
import com.android.audiorecorder.audio.IMediaPlaybackService;
import com.android.audiorecorder.audio.MediaPlaybackService;
import com.android.audiorecorder.audio.MusicUtils;
import com.android.audiorecorder.audio.MusicUtils.ServiceToken;
import com.android.audiorecorder.dao.FileManagerFactory;
import com.android.audiorecorder.dao.IFileManager;
import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.ui.adapter.RecordListAdapter;
import com.android.audiorecorder.ui.adapter.RecordListAdapter.ITaskClickListener;
import com.android.audiorecorder.utils.FileUtils;
import com.android.audiorecorder.utils.StringUtil;
import com.android.audiorecorder.utils.UIHelper;
import com.android.library.ui.activity.BaseCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoRecordList extends BaseCompatActivity implements
        View.OnCreateContextMenuListener, OnItemClickListener, ITaskClickListener, OnScrollListener {
    
    public final static int PAGE_NUMBER = 18;
    
    public final static int IDLE = 0;
    public final static int PLAY = 1;
    public final static int PAUSE = 2;
    
    private int mCurState = IDLE;
    
    public final static int MSG_TOGGLE_UI = 5;
    public final static int MSG_REFRESH_LIST = 10;
    
    private final static int MSG_LOAD_SMSLIST=  20;
    
    public static final int ITEM_OPERATION_PLAY = 1;
    public static final int ITEM_OPERATION_RENAME = 2;
    public static final int ITEM_OPERATION_DETAILS = 3;
    public static final int ITEM_OPERATION_DELETE = 4;
    
    //private int mCurPlayIndex;
    
    private BroadcastReceiver mPlayCompleteReciBroadcastReceiver;
    
    private static final int QUIT = 2;
    private static final int REFRESH = 1;
    private String TAG = "RecordList";
    private RecordListAdapter mAdapter;
    private List<RecorderFile> mFileList;
    private TextView mCurrentTime;
    private long mDuration;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private boolean mFromTouch;
    //private final Handler mHandler;
    private TextView mIndicator;
    private long mLastSeekEventTime;
    private long mPosOverride = -1;
    private ProgressBar mProgress;
    private LinearLayout mProgressLayout;
    //private TrackQueryHandler mQueryHandler;

    public long mSelectedId;
    public int mSelectedPosition;
    private TextView mTotalTime;
    private ListView mTrackList;
    private PowerManager.WakeLock mWakeLock;

    //start audio play
    private IMediaPlaybackService mService;
    private ServiceToken mToken;
    private boolean paused;
    private String mPlayPath;

    private int mLastVisibleIndex = 0;
    private boolean mLoadLogFlag;
    
    //end audo play
    
    private IFileManager mFileManager;
    
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_REFRESH_LIST:
                    mAdapter.notifyDataSetChanged();
                    break;
                case REFRESH:
                    if(!mFromTouch){
                        long next = refreshNow();
                        queueNextRefresh(next);
                    }
                    break;
                case MSG_TOGGLE_UI:
                    int position = msg.arg1;
                    startPlayback(position);
                    break;
                case MSG_LOAD_SMSLIST:
                	int page = msg.arg1;
                	List<RecorderFile> list = DebugConfig.DEBUG ? mFileManager.queryAllFileList(1, page, PAGE_NUMBER) : mFileManager.queryPublicFileList(1, page, PAGE_NUMBER);
                	int len = list.size();
                	Log.i(TAG, "---> load " + page + " page. number = " + len);
                    if(len==PAGE_NUMBER) {
                        mTrackList.setTag(UIHelper.LISTVIEW_DATA_MORE);
                    }else {
                    	mTrackList.setTag(UIHelper.LISTVIEW_DATA_FULL);
                    }
                    mLoadLogFlag = false;
                    mFileList.addAll(list);
                    updateCounter();
                    mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
                	break;
                    default:
                        break;
            }
        };
    };

    @SuppressLint("WrongViewCast")
	public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setVolumeControlStream(3);
        setContentView(R.layout.recordlist_view);
        this.mTrackList = (ListView) findViewById(android.R.id.list);
        this.mTrackList.setOnItemClickListener(this);
        this.mTrackList.setOnScrollListener(this);
        mFileList = new ArrayList<RecorderFile>();
        mAdapter = new RecordListAdapter(this, mFileList);
        mTrackList.setAdapter(mAdapter);
        setTitle(R.string.list);
        registerForContextMenu(this.mTrackList);
        this.mIndicator = (TextView) findViewById(R.id.indicator);
        this.mProgress = (ProgressBar) findViewById(R.id.progress);
        this.mProgressLayout = (LinearLayout) findViewById(R.id.progresslayout);
        this.mCurrentTime = (TextView) findViewById(R.id.currenttime);
        this.mTotalTime = (TextView) findViewById(R.id.totaltime);
        this.mProgressLayout.setVisibility(View.GONE);
        PowerManager localPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        this.mWakeLock = localPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "RecorderPlayer");
        if ((this.mProgress instanceof SeekBar)) {
            Log.d(this.TAG, "setOnSeekBarChangeListener");
            SeekBar seeker = (SeekBar) this.mProgress;
            seeker.setOnSeekBarChangeListener(mSeekListener);
        }
        this.mProgress.setMax(1000);
        mFileManager = FileManagerFactory.getFileManagerInstance(this);
        if(mPlayCompleteReciBroadcastReceiver == null){
            mPlayCompleteReciBroadcastReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(MediaPlaybackService.PLAY_COMPLETE_ACTION.equals(intent.getAction())){
                        mProgress.setProgress(1000);
                        if(mService != null){
                            try {
                                mService.pause();
                                int playPositoin = checkNewPlayPosition();
                                mAdapter.setPlayId(playPositoin, PAUSE);
                                mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            registerReceiver(mPlayCompleteReciBroadcastReceiver, new IntentFilter(MediaPlaybackService.PLAY_COMPLETE_ACTION));
        }
        init();
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        //getSupportMenuInflater().inflate(R.menu.recordlist_menu, paramMenu);
        return super.onCreateOptionsMenu(paramMenu);
    }
    
    public boolean onPrepareOptionsMenu(Menu paramMenu) {
        /*if(mFileList.size() == 0){
            paramMenu.findItem(R.id.menu_item_delete).setEnabled(false);
        }*/
        return true;
    }
    
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_item_delete:
                *//*if(mCurPlayIndex == -1 && mCurPlayIndex>=mFileList.size()){
                    Toast.makeText(this, getString(R.string.select_delete_file), Toast.LENGTH_SHORT).show();
                    return true;
                }
                deleteItem(mCurPlayIndex);*//*
                break;
                default:
                    break;
        }
        return true;
    }*/
    public void init() {
        mAdapter.setPlayId(-1, mCurState);
        mAdapter.setTaskClickListener(this);
        Message msg = mHandler.obtainMessage(MSG_LOAD_SMSLIST);
        msg.arg1 = 0;
        mHandler.sendMessage(msg);
    }
    
    private void updateCounter() {
      if ((mFileList.size() == 0)) {
          this.mIndicator.setVisibility(View.GONE);
      } else {
          this.mIndicator.setVisibility(View.VISIBLE);
          int count = mFileManager.getFileCount(1, DebugConfig.DEBUG ? -1 : MultiMediaService.LUNCH_MODE_MANLY);
          this.mIndicator.setText(getResources().getQuantityString(R.plurals.NNNtrackscount, count, count));
      }
    }
    
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
    @Override
    public void onStart() {
        super.onStart();
        paused = false;

        mToken = MusicUtils.bindToService(this, osc);
        if (mToken == null) {
            mHandler.sendEmptyMessage(QUIT);
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "===> onStop.");
        paused = true;
        mHandler.removeMessages(REFRESH);
        if(mService != null) {
            try {
                mService.stop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        MusicUtils.unbindFromService(mToken);
        mService = null;
    }
    
    @Override
    public void onDestroy()
    {
        Log.d(TAG, "===> onDestroy.");
        if(mPlayCompleteReciBroadcastReceiver != null){
        	unregisterReceiver(mPlayCompleteReciBroadcastReceiver);
        	mPlayCompleteReciBroadcastReceiver = null;
        }
        super.onDestroy();
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        toggleAudio(position);
    }
    
    @Override
    public void onTaskClick(int index, int action) {
        switch(action){
            case ITEM_OPERATION_PLAY:
                toggleAudio(index);
                break;
            case ITEM_OPERATION_DETAILS:
                showInfomationDlg(index);
                break;
            case ITEM_OPERATION_DELETE:
                deleteItem(index);
                break;
                default:
                    break;
        }
    }
    
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    	mLastVisibleIndex = mTrackList.getLastVisiblePosition();
    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    	if(scrollState == SCROLL_STATE_IDLE) {
            if(!mLoadLogFlag && mLastVisibleIndex == (mAdapter.getCount() -1) && (StringUtil.toInt(mTrackList.getTag())== UIHelper.LISTVIEW_DATA_MORE)) {
                mLoadLogFlag = true;
                mHandler.removeMessages(MSG_LOAD_SMSLIST);
                Message msg = mHandler.obtainMessage(MSG_LOAD_SMSLIST);
                msg.arg1 = mFileList.size() / PAGE_NUMBER;
                mHandler.sendMessage(msg);
            }
        }
    }
    
    private void toggleAudio(int position){
        Message msg = mHandler.obtainMessage(MSG_TOGGLE_UI);
        msg.arg1 = position;
        mHandler.sendMessage(msg);
    }
    
    private void deleteItem(int position){
        if(position == - 1 || position>=mFileList.size()){
            return;
        }
        try {
            int playPositoin = checkNewPlayPosition();
            Log.d(TAG, "---> delete position = " + position + " playPositoin = " + playPositoin);
            if(playPositoin == position){
                if(mService != null){
                    mService.stop();
                }
                mPlayPath = "";
            }
            RecorderFile file = mFileList.get(position);
            File deleteFile = new File(file.getPath());
            deleteFile.delete();
            mFileManager.delete(0, file.getId());
            mFileList.remove(position);
            if(playPositoin == position){
                mAdapter.setPlayId(-1, PAUSE);
            }else{
                int newPositoin = checkNewPlayPosition();
                mAdapter.setPlayId(newPositoin, mAdapter.getPlayState());
            }
            mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
            updateCounter();
            Toast.makeText(this, getResources().getQuantityString(R.plurals.NNNtracksdeleted, 1, 1), Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
    private ServiceConnection osc = new ServiceConnection() {
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = IMediaPlaybackService.Stub.asInterface(obj);
            Log.i(TAG, "---> AudioMediaService bind success.");
        }
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };
    
    private void startPlayback(int position) {
        Log.i(TAG, "---> startPlayback position = " + position);
        if(mService == null || position <0 || mFileList.size()<=position){
            return;
        }
        int curPlayPosition = checkNewPlayPosition();
        if(position == curPlayPosition){
            try {
                if(mService.isPlaying()){
                    mService.pause();
                    mAdapter.setPlayId(position, PAUSE);
                }else {
                    mService.play();
                    mAdapter.setPlayId(position, PLAY);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            mPlayPath = mFileList.get(position).getPath();
            if(DebugConfig.DEBUG) {
                Log.d(TAG, "===> open audio filename = " + mPlayPath);
            }
            if (mPlayPath != null && mPlayPath.length() > 0) {
                try {
                    mService.stop();
                    mService.openFile(mPlayPath);
                    mService.play();
                    mAdapter.setPlayId(position, PLAY);
                } catch (Exception ex) {
                    Log.d("MediaPlaybackActivity", "couldn't start playback: " + ex);
                }
            }
        }
        mProgressLayout.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
        updateTrackInfo();
        long next = refreshNow();
        queueNextRefresh(next);
    }
    
    private void updateTrackInfo() {
        if (mService == null) {
            return;
        }
        try {
            String path = mService.getPath();
            if (path == null) {
                Toast.makeText(this, R.string.service_open_error_msg, Toast.LENGTH_SHORT).show();
                return;
            }
            mDuration = mService.duration();
            long remain =  mDuration % 1000;
            long totalSeconds = (remain == 0) ? mDuration / 1000 : (mDuration / 1000);
            mTotalTime.setText(MusicUtils.makeTimeString(this, totalSeconds));
        } catch (RemoteException ex) {
        }
    }
    
    private long refreshNow() {
        if(mService == null)
            return 500;
        try {
            long pos = mPosOverride < 0 ? mService.position() : mPosOverride;
            if ((pos >= 0) && (mDuration > 0)) {
                mCurrentTime.setText(MusicUtils.makeTimeString(this, pos / 1000));
                int progress = (int) (1000 * pos / mDuration);
                mProgress.setProgress(progress);
                
                if (mService.isPlaying()) {
                    mCurrentTime.setVisibility(View.VISIBLE);
                } else {
                    // blink the counter
                    int vis = mCurrentTime.getVisibility();
                    mCurrentTime.setVisibility(vis == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                    return 500;
                }
            } else {
                mCurrentTime.setText("0:00");
                mProgress.setProgress(1000);
            }
            // calculate the number of milliseconds until the next full second, so
            // the counter can be updated at just the right time
            long remaining = 1000 - (pos % 1000);
            // approximate how often we would need to refresh the slider to
            // move it smoothly
            int width = mProgress.getWidth();
            if (width == 0) width = 320;
            long smoothrefreshtime = mDuration / width;
            if(mService.getPlayState() == MediaPlaybackService.PLAY_STATE_COMPLETE && mService.getSeekState() != MediaPlaybackService._STATE_SEEKING) {
                return 500;
            }
            if (smoothrefreshtime > remaining){
                return remaining;
            }
            if (smoothrefreshtime <= 20) {
                return 20;
            }
            return smoothrefreshtime;
        } catch (RemoteException ex) {
        }
        return 500;
    }
    
    private void queueNextRefresh(long delay) {
        if (!paused) {
            Message msg = mHandler.obtainMessage(REFRESH);
            mHandler.removeMessages(REFRESH);
            mHandler.sendMessageDelayed(msg, delay);
        }
    }
    
    
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            if (mService == null) return;
            mLastSeekEventTime = 0;
            mFromTouch = true;
        }
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser || (mService == null)) return;
            long now = SystemClock.elapsedRealtime();
            if ((now - mLastSeekEventTime) > 250) {
                mLastSeekEventTime = now;
                mPosOverride = mDuration * progress / 1000;
                try {
                    if(mPosOverride<=mDuration){
                        mCurrentTime.setText(MusicUtils.makeTimeString(VideoRecordList.this, mPosOverride / 1000));
                        mService.seek(mPosOverride);
                    }
                } catch (RemoteException ex) {
                }

                if (!mFromTouch) {
                    refreshNow();
                    mPosOverride = -1;
                }
            }
        }
        
        public void onStopTrackingTouch(SeekBar bar) {
            if (mService == null) return;
            long seekTo = mDuration * bar.getProgress() / 1000;
            try {
                if(seekTo<=mDuration){
                    mCurrentTime.setText(MusicUtils.makeTimeString(VideoRecordList.this, mPosOverride / 1000));
                    mService.seek(seekTo);
                }
            } catch (RemoteException ex) {
            }
            mPosOverride = -1;
            mFromTouch = false;
            queueNextRefresh(refreshNow());
        }
    };

    private void showInfomationDlg(int index) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
        localBuilder.setTitle(R.string.information);
        localBuilder.setIcon(android.R.drawable.ic_menu_info_details);
        localBuilder.setPositiveButton(R.string.button_ok, null);
        RecorderFile file = mFileList.get(index);
        View localView2 = layoutInflater.inflate(R.layout.information_dlg, null);
        TextView title = (TextView) localView2.findViewById(R.id.title);
        title.setText(file.getName());
        ImageView thumb = (ImageView) localView2.findViewById(R.id.thumb);
        Drawable localDrawable = getResources().getDrawable(R.drawable.ic_default_thumb);
        thumb.setImageDrawable(localDrawable);
        TextView duration = (TextView) localView2.findViewById(R.id.duration);
        duration.setText(MusicUtils.makeTimeString(this, file.getDuration()));
        TextView size = (TextView) localView2.findViewById(R.id.size);
        size.setText(FileUtils.formetFileSize(file.getSize()));
        TextView type = (TextView) localView2.findViewById(R.id.type);
        type.setText(file.getMimeType());
        TextView pathTextView = (TextView) localView2.findViewById(R.id.path);
        pathTextView.setText(file.getPath());
        localBuilder.setView(localView2);
        localBuilder.show();
    }
    
    private int checkNewPlayPosition(){
        int i = -1;
        for(RecorderFile file:mFileList){
            i++;
            if(file.getPath().equalsIgnoreCase(mPlayPath)){
                return i;
            }
        }
        return -1;
    }
    
    private void loadAudioFileList(){
        
    }
}