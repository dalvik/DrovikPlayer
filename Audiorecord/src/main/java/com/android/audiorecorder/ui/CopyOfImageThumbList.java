package com.android.audiorecorder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.audiorecorder.R;
import com.android.audiorecorder.dao.FileManagerFactory;
import com.android.audiorecorder.dao.IFileManager;
import com.android.audiorecorder.provider.FileDetail;
import com.android.audiorecorder.ui.adapter.ListViewFileAdapter;
import com.android.audiorecorder.utils.StringUtil;
import com.android.audiorecorder.utils.UIHelper;
import com.android.library.ui.activity.BaseCompatActivity;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

public class CopyOfImageThumbList extends BaseCompatActivity implements
        View.OnCreateContextMenuListener, OnItemClickListener, OnScrollListener {
    
    public final static int PAGE_NUMBER = 30;
    
    
    public final static int MSG_TOGGLE_UI = 5;
    public final static int MSG_REFRESH_LIST = 10;
    
    private final static int MSG_LOAD_FILE_LIST=  20;
    
    public static final int ITEM_OPERATION_PLAY = 1;
    public static final int ITEM_OPERATION_RENAME = 2;
    public static final int ITEM_OPERATION_DETAILS = 3;
    public static final int ITEM_OPERATION_DELETE = 4;
    
    //private int mCurPlayIndex;
    
    private static final int QUIT = 2;
    private static final int REFRESH = 1;
    private String TAG = "RecordList";
    private ListViewFileAdapter mAdapter;
    private List<FileDetail> mFileList;
    private long mDuration;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private boolean mFromTouch;
    //private final Handler mHandler;
    private long mLastSeekEventTime;
    private long mPosOverride = -1;

    public long mSelectedId;
    public int mSelectedPosition;
    private PullToRefreshGridView mPullToRefreshGridView;

    private int mLastVisibleIndex = 0;
    private boolean mLoadLogFlag;
    
    private int mImageThumbWidth;
    private int mImageThumbSpacing;
    

    private String mThumbName;
    private IFileManager mFileManager;
    
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_REFRESH_LIST:
                    mAdapter.notifyDataSetChanged();
                    break;
                case REFRESH:
                    break;
                case MSG_TOGGLE_UI:
                    break;
                case MSG_LOAD_FILE_LIST:
                	int page = msg.arg1;
                	List<FileDetail> list = mFileManager.loadFileList(true, 0, mThumbName, page, PAGE_NUMBER, null);
                	int len = list.size();
                	Log.i(TAG, "---> load " + page + " page. number = " + len);
                    if(len==PAGE_NUMBER) {
                        mPullToRefreshGridView.setTag(UIHelper.LISTVIEW_DATA_MORE);
                    }else {
                        mPullToRefreshGridView.setTag(UIHelper.LISTVIEW_DATA_FULL);
                    }
                    mLoadLogFlag = false;
                    mFileList.addAll(list);
                    mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
                	break;
                    default:
                        break;
            }
        };
    };

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.layout_image_list);
        mPullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.list_view_image);
        mPullToRefreshGridView.setOnItemClickListener(this);
        mPullToRefreshGridView.setOnScrollListener(this);
        mFileList = new ArrayList<FileDetail>();
        mAdapter = new ListViewFileAdapter(this, mFileList, R.layout.layout_file_grid_item);
        mPullToRefreshGridView.setAdapter(mAdapter);
        mPullToRefreshGridView.setEmptyView(null);
        setTitle(R.string.list);
        registerForContextMenu(mPullToRefreshGridView);
        mImageThumbWidth = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_width);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        mPullToRefreshGridView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            
            @Override
            public void onGlobalLayout() {
                if (mAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(mPullToRefreshGridView.getWidth() / (mImageThumbWidth + mImageThumbSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (mPullToRefreshGridView.getWidth() / numColumns) - mImageThumbSpacing;
                        mAdapter.setNumColumns(numColumns);
                        mAdapter.setItemHeight(columnWidth);
                        Log.d(TAG, "onCreateView - local audio view numColumns set to " + numColumns);
                    }
                }
            }
        });
        mFileManager = FileManagerFactory.getFileManagerInstance(this);
        Intent intent = getIntent();
        if(intent != null) {
            mThumbName = intent.getStringExtra(MainThumbList.EXTRA_THUMB_NAME);
            Log.i(TAG, "--> thumb name = " + mThumbName);
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_item_delete) {
        } else {
        }
        return true;
    }
    public void init() {
        Message msg = mHandler.obtainMessage(MSG_LOAD_FILE_LIST);
        msg.arg1 = 0;
        mHandler.sendMessage(msg);
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
    }
    
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "===> onStop.");
    }
    
    @Override
    public void onDestroy()
    {
        Log.d(TAG, "===> onDestroy.");
        super.onDestroy();
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent(this, ImageViewActvity.class);
        startActivity(intent);
    }
    
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    	//mLastVisibleIndex = mPullToRefreshGridView.getLastVisiblePosition();
    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    	if(scrollState == SCROLL_STATE_IDLE) {
            if(!mLoadLogFlag && mLastVisibleIndex == (mAdapter.getCount() -1) && (StringUtil.toInt(mPullToRefreshGridView.getTag())== UIHelper.LISTVIEW_DATA_MORE)) {
                mLoadLogFlag = true;
                mHandler.removeMessages(MSG_LOAD_FILE_LIST);
                Message msg = mHandler.obtainMessage(MSG_LOAD_FILE_LIST);
                msg.arg1 = mFileList.size() / PAGE_NUMBER;
                mHandler.sendMessage(msg);
            }
        }
    }
    
    
    private void showInfomationDlg(int index) {
        /*LayoutInflater layoutInflater = (LayoutInflater) getSystemService("layout_inflater");
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
        localBuilder.show();*/
    }
    
}