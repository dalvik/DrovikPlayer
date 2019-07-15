package com.android.audiorecorder.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.audiorecorder.AppContext;
import com.android.audiorecorder.R;
import com.android.audiorecorder.dao.FileManagerFactory;
import com.android.audiorecorder.dao.FileThumb;
import com.android.audiorecorder.dao.IFileManager;
import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.provider.FileColumn;
import com.android.audiorecorder.provider.FileProvider;
import com.android.audiorecorder.provider.FileProviderService;
import com.android.audiorecorder.ui.adapter.ListViewFileThumbAdapter;
import com.android.audiorecorder.ui.view.ScrollLayout;
import com.android.audiorecorder.utils.FileUtils;
import com.android.audiorecorder.utils.UIHelper;
import com.android.media.PlatFormManager;
import com.android.media.PlatFormManager.IResultListener;
import com.android.media.image.tango.TiangoImageListResp;
import com.android.media.image.tango.TiangoImageThumbListResp;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

public class MainThumbList extends Activity {

	private String TAG = "Main";
	public final static String EXTRA_THUMB_NAME = "thumb_name";
	public final static String EXTRA_FILE_TYPE = "file_type";
	private static final String IMAGE_CACHE_DIR = "thumbs";

	private boolean DEBUG = true;

	private ScrollLayout mScrollLayout;
	private RadioButton[] mButtons;
	private String[] mHeadTitles;
	private int mViewCount;
	private int mCurSel;

	private ImageView mHeadLogo;
	private TextView mHeadTitle;
	private ProgressBar mHeadProgress;

	//footer
	private RadioButton fbImage;
	private RadioButton fbVideo;
	private ImageView fbSetting;

	//local image content view
	private PullToRefreshGridView localImageListView;
	private ListViewFileThumbAdapter localImageListViewAdapter;
	private List<FileThumb> localImageListViewData = new ArrayList<FileThumb>();
	//private View localImageListViewFooter;
	//private TextView localImageListViewFootMore;
	//private ProgressBar localImageListViewFootProgress;

	//local video content view
	private PullToRefreshGridView localVideoListView;
	private ListViewFileThumbAdapter localVideoListViewAdapter;
	private List<FileThumb> localVideoListViewData = new ArrayList<FileThumb>();
	//private View localVideoListViewFooter;
	//private TextView localVideoListViewFootMore;
	//private ProgressBar localVideoListViewFootProgress;

	//local audio content view
	private PullToRefreshGridView localAudioListView;
	private ListViewFileThumbAdapter localAudioListViewAdapter;
	private List<FileThumb> localAudioListViewData = new ArrayList<FileThumb>();
	//private View localAudioListViewFooter;
	//private TextView localAudioListViewFootMore;
	//private ProgressBar localAudioListViewFootProgress;

	//local other content view
	private PullToRefreshGridView localOtherListView;
	private ListViewFileThumbAdapter localOtherListViewAdapter;
	private List<FileThumb> localOtherListViewData = new ArrayList<FileThumb>();
	//private View localOtherListViewFooter;
	//private TextView localOtherListViewFootMore;
	//private ProgressBar localOtherListViewFootProgress;

	//remote image content view
	private PullToRefreshGridView remoteImageListView;
	private ListViewFileThumbAdapter remoteImageListViewAdapter;
	private List<FileThumb> remoteImageListViewData = new ArrayList<FileThumb>();
	//private View remoteImageListViewFooter;
	//private TextView remoteImageListViewFootMore;
	//private ProgressBar remoteImageListViewFootProgress;

	//remote video content view
	private PullToRefreshGridView remoteVideoListView;
	private ListViewFileThumbAdapter remoteVideoListViewAdapter;
	private List<FileThumb> remoteVideoListViewData = new ArrayList<FileThumb>();
	//private View remoteVideoListViewFooter;
	//private TextView remoteVideoListViewFootMore;
	//private ProgressBar remoteVideoListViewFootProgress;

	//remote audio content view
	private PullToRefreshGridView remoteAudioListView;
	private ListViewFileThumbAdapter remoteAudioListViewAdapter;
	private List<FileThumb> remoteAudioListViewData = new ArrayList<FileThumb>();
	//private View remoteAudioListViewFooter;
	//private TextView remoteAudioListViewFootMore;
	//private ProgressBar remoteAudioListViewFootProgress;

	//remote other content view
	private PullToRefreshGridView remoteOtherListView;
	private ListViewFileThumbAdapter remoteOtherListViewAdapter;
	private List<FileThumb> remoteOtherListViewData = new ArrayList<FileThumb>();
	//private View remoteOtherListViewFooter;
	//private TextView remoteOtherListViewFootMore;
	//private ProgressBar remoteOtherListViewFootProgress;

	//handler
	private Handler localImageListViewHandler;
	private Handler localVideoListViewHandler;
	private Handler localAudioListViewHandler;
	private Handler localOtherListViewHandler;
	private Handler remoteImageListViewHandler;
	private Handler remoteVideoListViewHandler;
	private Handler remoteAudioListViewHandler;
	private Handler remoteOtherListViewHandler;

	//top buttons
	private Button frameLocalImageButton;
	private Button frameLocalVideoButton;
	private Button frameLocalAudioButton;
	private Button frameLocalOtherButton;

	private Button frameRemoteImageButton;
	private Button frameRemoteVideoButton;
	private Button frameRemoteAudioButton;
	private Button frameRemoteOtherButton;

	private int localImageListSumData;
	private int localVideoListSumData;
	private int localAudioListSumData;
	private int localOtherListSumData;

	private int remoteImageListSumData;
	private int remoteVideoListSumData;
	private int remoteAudioListSumData;
	private int remoteOtherListSumData;

	private int mCurThumbCatalog = AppContext.CATALOG_LOCAL_IMAGE;

	//private AppContext appContext;//全局Context


	private int mImageThumbWidth;
	private int mImageThumbHeight;
	private int mImageThumbSpacing;

	//private AlumbEngine mAlumbEngine;
	private IFileManager mFileManager;

	private int column_count = 4;// 显示列数

	private int page_count = column_count * 4;// 每次加载15张图片

	private int current_page = 0;

	public static int itemWidth;

	private int mCurFileType;

	private FileListChangeObserver observer;
	private PlatFormManager mImagePlatFormManager;

	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			Handler handler = localImageListViewHandler;
			int objType =  UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE;
			//加载数据
			if(msg.arg1 == FileProvider.FILE_TYPE_JEPG){
				handler = localVideoListViewHandler;
				objType =  UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE;
				mCurThumbCatalog =  AppContext.CATALOG_LOCAL_IMAGE;
			} else if(msg.arg1 == FileProvider.FILE_TYPE_VIDEO){
				handler = localVideoListViewHandler;
				objType =  UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO;
				mCurThumbCatalog =  AppContext.CATALOG_LOCAL_VIDEO;
			} else if(msg.arg1 == FileProvider.FILE_TYPE_AUDIO){
				handler = localAudioListViewHandler;
				objType =  UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO;
				mCurThumbCatalog =  AppContext.CATALOG_LOCAL_AUDIO;
			} else if(msg.arg1 == FileProvider.FILE_TYPE_OTHER){
				handler = localOtherListViewHandler;
				objType =  UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER;
				mCurThumbCatalog =  AppContext.CATALOG_LOCAL_OTHER;
			}
			loadThumbByCatalog(mCurThumbCatalog, 0, handler, UIHelper.LISTVIEW_ACTION_INIT, objType);
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		//appContext = (AppContext)getApplication();
		//initData();
		//mAlumbEngine = new AlumbEngine();
		mImageThumbWidth = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_width);
		mImageThumbHeight = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_height);
		mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
		mFileManager = FileManagerFactory.getFileManagerInstance(this);
		Intent intent = getIntent();
		mCurFileType = intent.getIntExtra(EXTRA_FILE_TYPE, UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE);
		this.initHeadView();
		this.initFootBar();
		this.initPageScroll();
		this.initFrameButton();
		this.initFrameListView();
		initObserver();
		mImagePlatFormManager = new PlatFormManager(mResultListener);
		//UpdateManager.getUpdateManager().checkAppUpdate(this, true);
	}

	private void initPageScroll() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_linearlayout_footer);
		mHeadTitles = getResources().getStringArray(R.array.head_titles);
		mViewCount = mScrollLayout.getChildCount();
		mButtons = new RadioButton[mViewCount];
		if(DEBUG) {
			Log.d(TAG, "### mViewCount= " + mViewCount);
		}
		for(int i = 0; i < mViewCount; i++)	{
			mButtons[i] = (RadioButton) linearLayout.getChildAt(i*2);
			mButtons[i].setTag(i);
			mButtons[i].setChecked(false);
			mButtons[i].setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int pos = (Integer)(v.getTag());
					if(mCurSel == pos) {
						switch (pos) {
							case 0://image
								if(localImageListViewData.size() == 0) {
									mCurThumbCatalog = AppContext.CATALOG_LOCAL_IMAGE;
									loadThumbByCatalog(mCurThumbCatalog, 0, localImageListViewHandler, UIHelper.LISTVIEW_ACTION_INIT, UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE);
								}
								break;
							case 1://video
								mCurThumbCatalog = AppContext.CATALOG_REMOTE_IMAGE;
								if(remoteImageListViewData.size() == 0) {
									loadThumbByCatalog(mCurThumbCatalog, 0, remoteImageListViewHandler, UIHelper.LISTVIEW_ACTION_INIT, UIHelper.LISTVIEW_DATATYPE_REMOTE_IMAGE);
								}
								break;
							case 2://settings
								break;
							default:
								break;
						}
					}
					setCurPoint(pos);
					mScrollLayout.snapToScreen(pos);
				}
			});
		}
		//设置第一显示屏
		mCurSel = 0;
		mButtons[mCurSel].setEnabled(true);
		mButtons[mCurSel].setChecked(true);
		mScrollLayout.SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
			public void OnViewChange(int viewIndex) {
				setCurPoint(viewIndex);
			}
		});
	}

	/**
	 * 设置底部栏当前焦点
	 * @param index
	 */
	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index)
			return;
		mButtons[mCurSel].setChecked(false);
		mButtons[index].setChecked(true);
		mHeadTitle.setText(mHeadTitles[index]);
		mCurSel = index;
	}

	private void initHeadView() {
		mHeadLogo = (ImageView) findViewById(R.id.main_head_logo);
		mHeadTitle = (TextView) findViewById(R.id.main_head_title);
		mHeadProgress = (ProgressBar) findViewById(R.id.main_head_progress);
		mHeadLogo.setImageResource(R.drawable.base_actionbar_back_selector);
		mHeadLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainThumbList.this.finish();
			}
		});
	}

	private void initFootBar() {
		fbImage = (RadioButton)findViewById(R.id.main_footbar_image);
		fbVideo = (RadioButton)findViewById(R.id.main_footbar_video);
		fbSetting = (ImageView)findViewById(R.id.main_footbar_setting);
	}

	private void initFrameListView() {
		//初始化listview控件
		this.initLocalImageListView();
		this.initLocalVideoListView();
		this.initLocalAudioListView();
		this.initLocalOtherListView();
		this.initRemoteImageListView();
		this.initRemoteVideoListView();
		this.initRemoteAudioListView();
		this.initRemoteOtherListView();
		//加载listview数据
		this.initFrameListViewData();
	}


	/**
	 * 初始化主页的按钮
	 */
	private void initFrameButton() {
		frameLocalImageButton = (Button)findViewById(R.id.frame_btn_local_image);
		frameLocalVideoButton = (Button)findViewById(R.id.frame_btn_local_video);
		frameLocalAudioButton = (Button)findViewById(R.id.frame_btn_local_audio);
		frameLocalOtherButton = (Button)findViewById(R.id.frame_btn_local_other);
		frameRemoteImageButton = (Button)findViewById(R.id.frame_btn_remote_image);
		frameRemoteVideoButton = (Button)findViewById(R.id.frame_btn_remote_video);
		frameRemoteAudioButton = (Button)findViewById(R.id.frame_btn_remote_audio);
		frameRemoteOtherButton = (Button)findViewById(R.id.frame_btn_remote_other);

		frameLocalImageButton.setOnClickListener(frameFileBtnClick(frameLocalImageButton, AppContext.CATALOG_LOCAL_IMAGE));
		frameLocalVideoButton.setOnClickListener(frameFileBtnClick(frameLocalVideoButton, AppContext.CATALOG_LOCAL_VIDEO));
		frameLocalAudioButton.setOnClickListener(frameFileBtnClick(frameLocalAudioButton, AppContext.CATALOG_LOCAL_AUDIO));
		frameLocalOtherButton.setOnClickListener(frameFileBtnClick(frameLocalOtherButton, AppContext.CATALOG_LOCAL_OTHER));
		frameRemoteImageButton.setOnClickListener(frameFileBtnClick(frameRemoteImageButton, AppContext.CATALOG_REMOTE_IMAGE));
		frameRemoteVideoButton.setOnClickListener(frameFileBtnClick(frameRemoteVideoButton, AppContext.CATALOG_REMOTE_VIDEO));
		frameRemoteAudioButton.setOnClickListener(frameFileBtnClick(frameRemoteAudioButton, AppContext.CATALOG_REMOTE_AUDIO));
		frameRemoteOtherButton.setOnClickListener(frameFileBtnClick(frameRemoteOtherButton, AppContext.CATALOG_REMOTE_OTHER));
	}

	private OnClickListener frameFileBtnClick(final Button btn, final int catalog){
		return new OnClickListener() {
			public void onClick(View v) {
				mCurThumbCatalog = catalog;
				if(catalog<AppContext.CATALOG_REMOTE_IMAGE){
					if(btn == frameLocalImageButton){
						frameLocalImageButton.setEnabled(false);
					}else{
						frameLocalImageButton.setEnabled(true);
					}
					if(btn == frameLocalVideoButton){
						frameLocalVideoButton.setEnabled(false);
					}else{
						frameLocalVideoButton.setEnabled(true);
					}
					if(btn == frameLocalAudioButton){
						frameLocalAudioButton.setEnabled(false);
					}else{
						frameLocalAudioButton.setEnabled(true);
					}
					if(btn == frameLocalOtherButton){
						frameLocalOtherButton.setEnabled(false);
					}else{
						frameLocalOtherButton.setEnabled(true);
					}
					if(btn == frameLocalImageButton) {
						localImageListView.setVisibility(View.VISIBLE);
						localVideoListView.setVisibility(View.GONE);
						localAudioListView.setVisibility(View.GONE);
						localOtherListView.setVisibility(View.GONE);
						mHeadTitle.setText(mHeadTitles[0]);
						loadThumbByCatalog(catalog, 0, localImageListViewHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG, UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE);
					} else if(btn == frameLocalVideoButton) {
						localVideoListView.setVisibility(View.VISIBLE);
						localImageListView.setVisibility(View.GONE);
						localAudioListView.setVisibility(View.GONE);
						localOtherListView.setVisibility(View.GONE);
						mHeadTitle.setText(mHeadTitles[1]);
						loadThumbByCatalog(catalog, 0, localVideoListViewHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG, UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO);
					} else if(btn == frameLocalAudioButton ){
						localAudioListView.setVisibility(View.VISIBLE);
						localImageListView.setVisibility(View.GONE);
						localVideoListView.setVisibility(View.GONE);
						localOtherListView.setVisibility(View.GONE);
						mHeadTitle.setText(mHeadTitles[2]);
						loadThumbByCatalog(catalog, 0, localAudioListViewHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG, UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
					} else if(btn == frameLocalOtherButton ){
						localOtherListView.setVisibility(View.VISIBLE);
						localImageListView.setVisibility(View.GONE);
						localVideoListView.setVisibility(View.GONE);
						localAudioListView.setVisibility(View.GONE);
						mHeadTitle.setText(mHeadTitles[3]);
						loadThumbByCatalog(catalog, 0, localOtherListViewHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG,  UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER);
					}
				} else {
					if(btn == frameRemoteImageButton){
						frameRemoteImageButton.setEnabled(false);
					}else{
						frameRemoteImageButton.setEnabled(true);
					}
					if(btn == frameRemoteVideoButton){
						frameRemoteVideoButton.setEnabled(false);
					}else{
						frameRemoteVideoButton.setEnabled(true);
					}
					if(btn == frameRemoteAudioButton){
						frameRemoteAudioButton.setEnabled(false);
					}else{
						frameRemoteAudioButton.setEnabled(true);
					}
					if(btn == frameRemoteOtherButton){
						frameRemoteOtherButton.setEnabled(false);
					}else{
						frameRemoteOtherButton.setEnabled(true);
					}
					if(btn == frameRemoteImageButton) {//remote image
						remoteImageListView.setVisibility(View.VISIBLE);
						remoteAudioListView.setVisibility(View.GONE);
						remoteVideoListView.setVisibility(View.GONE);
						remoteOtherListView.setVisibility(View.GONE);
						loadThumbByCatalog(catalog, 0, remoteImageListViewHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG, UIHelper.LISTVIEW_DATATYPE_REMOTE_IMAGE);
					} else if(btn == frameRemoteVideoButton) {
						remoteVideoListView.setVisibility(View.VISIBLE);
						remoteImageListView.setVisibility(View.GONE);
						remoteAudioListView.setVisibility(View.GONE);
						remoteOtherListView.setVisibility(View.GONE);
						loadThumbByCatalog(catalog, 0, remoteVideoListViewHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG, UIHelper.LISTVIEW_DATATYPE_REMOTE_VIDEO);
					} else if(btn == frameRemoteAudioButton ){
						remoteAudioListView.setVisibility(View.VISIBLE);
						remoteImageListView.setVisibility(View.GONE);
						remoteImageListView.setVisibility(View.GONE);
						remoteVideoListView.setVisibility(View.GONE);
						loadThumbByCatalog(catalog, 0, remoteAudioListViewHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG, UIHelper.LISTVIEW_DATATYPE_REMOTE_AUDIO);
					} else if(btn == frameRemoteOtherButton ){
						remoteOtherListView.setVisibility(View.VISIBLE);
						remoteImageListView.setVisibility(View.GONE);
						remoteVideoListView.setVisibility(View.GONE);
						remoteAudioListView.setVisibility(View.GONE);
						loadThumbByCatalog(catalog, 0, remoteOtherListViewHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG, UIHelper.LISTVIEW_DATATYPE_REMOTE_OTHER);
					}
				}
			}
		};
	}

	/**
	 * 初始化ListView数据
	 */
	private void initFrameListViewData() {
		//初始化Handler
		localImageListViewHandler = this.getListViewHandler(localImageListView, localImageListViewAdapter, null, null, AppContext.PAGE_SIZE);
		localVideoListViewHandler = this.getListViewHandler(localVideoListView, localVideoListViewAdapter, null, null, AppContext.PAGE_SIZE);
		localAudioListViewHandler = this.getListViewHandler(localAudioListView, localAudioListViewAdapter, null, null, AppContext.PAGE_SIZE);
		localOtherListViewHandler = this.getListViewHandler(localOtherListView, localOtherListViewAdapter, null, null, AppContext.PAGE_SIZE);
		remoteImageListViewHandler = this.getListViewHandler(remoteImageListView, remoteImageListViewAdapter, null, null, AppContext.PAGE_SIZE);
		remoteVideoListViewHandler = this.getListViewHandler(remoteVideoListView, remoteVideoListViewAdapter, null, null, AppContext.PAGE_SIZE);
		remoteAudioListViewHandler = this.getListViewHandler(remoteAudioListView, remoteAudioListViewAdapter, null, null, AppContext.PAGE_SIZE);
		remoteOtherListViewHandler = this.getListViewHandler(remoteOtherListView, remoteOtherListViewAdapter, null, null, AppContext.PAGE_SIZE);
		initLoadData();
	}

	private void initLoadData(){
		Handler handler = localImageListViewHandler;
		int objType =  UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE;
		//加载数据
		if(mCurFileType == UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO){
			handler = localVideoListViewHandler;
			objType =  UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO;
			mCurThumbCatalog =  AppContext.CATALOG_LOCAL_VIDEO;
			frameLocalVideoButton.setFocusable(true);
			frameLocalVideoButton.setEnabled(false);
			mHeadTitle.setText(mHeadTitles[1]);
		} else if(mCurFileType == UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO){
			handler = localAudioListViewHandler;
			objType =  UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO;
			mCurThumbCatalog =  AppContext.CATALOG_LOCAL_AUDIO;
			frameLocalAudioButton.setFocusable(true);
			frameLocalAudioButton.setEnabled(false);
			mHeadTitle.setText(mHeadTitles[2]);
		} else if(mCurFileType == UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER){
			handler = localOtherListViewHandler;
			objType =  UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER;
			mCurThumbCatalog =  AppContext.CATALOG_LOCAL_OTHER;
			frameLocalOtherButton.setFocusable(true);
			frameLocalOtherButton.setEnabled(false);
			mHeadTitle.setText(mHeadTitles[3]);
		} else {
			frameLocalImageButton.setFocusable(true);
			frameLocalImageButton.setEnabled(false);
			mHeadTitle.setText(mHeadTitles[0]);
		}
		localImageListView.setVisibility(mCurFileType == UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE?View.VISIBLE:View.GONE);
		localVideoListView.setVisibility(mCurFileType == UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO?View.VISIBLE:View.GONE);
		localAudioListView.setVisibility(mCurFileType == UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO?View.VISIBLE:View.GONE);
		localOtherListView.setVisibility(mCurFileType == UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER?View.VISIBLE:View.GONE);
		loadThumbByCatalog(mCurThumbCatalog, 0, handler, UIHelper.LISTVIEW_ACTION_INIT, objType);
	}
	/**
	 * 获取listview的初始化Handler
	 * @param adapter
	 * @return
	 */
	private Handler getListViewHandler(final PullToRefreshGridView imageListView, final BaseAdapter adapter, final TextView more, final ProgressBar progress, final int pageSize){
		return new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what>=0) {
					handleImageListData(msg.what, msg.obj, msg.arg2, msg.arg1);
					if(msg.what<pageSize) {
						imageListView.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						imageListView.onRefreshComplete();
						//more.setText(R.string.load_full);
					}else if(msg.what == pageSize) {
						imageListView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						//more.setText(R.string.load_more);
					}
				}else if(msg.what == -1) {
					imageListView.setTag(UIHelper.LISTVIEW_DATA_MORE);
					//more.setText(R.string.load_error);
				}
				if(adapter.getCount() ==0) {
					imageListView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					//more.setText(R.string.load_empty);
				}
				//progress.setVisibility(ProgressBar.GONE);
				mHeadProgress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					//imageListView.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					imageListView.onRefreshComplete();
					//imageListView.setSelection(0);
				}else if(msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					imageListView.onRefreshComplete();
					//imageListView.setSelection(0);
				}
			}
		};
	}

	//init local image listview
	private void initLocalImageListView() {
		localImageListViewAdapter = new ListViewFileThumbAdapter(this, localImageListViewData, R.layout.layout_thumb_grid_item);
		localImageListViewAdapter.setFileType(UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE);
		//localImageListViewFooter = getLayoutInflater().inflate(R.layout.layout_list_view_footer, null);
		//localImageListViewFootMore = (TextView) localImageListViewFooter.findViewById(R.id.list_view_foot_more);
		//localImageListViewFootProgress = (ProgressBar) localImageListViewFooter.findViewById(R.id.list_view_foot_progress);
		localImageListView = (PullToRefreshGridView) findViewById(R.id.frame_list_view_local_image);
		//localImageListView.addFooterView(localImageListViewFooter);
		localImageListView.setAdapter(localImageListViewAdapter);
		localImageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchRecordListActivity(localImageListViewAdapter.getItem(position).getFileType(), localImageListViewAdapter.getItem(position).getName());
			}
		});
		localImageListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
				//localImageListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				/*if(localImageListViewData.size()<=0) {
					localImageListView.onScrollStateChanged(view, SCROLL_STATE_TOUCH_SCROLL);
				}*/
			}
		});
		localImageListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (localImageListViewAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(localImageListView.getWidth() / (mImageThumbWidth + mImageThumbSpacing));
					if (numColumns > 0) {
						final int columnWidth =
								(localImageListView.getWidth() / numColumns) - mImageThumbSpacing;
						localImageListViewAdapter.setNumColumns(numColumns);
						localImageListViewAdapter.setItemHeight(columnWidth);
						Log.d(TAG, "onCreateView - local image view numColumns set to " + numColumns);
					}
				}
			}
		});
		localImageListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				loadThumbByCatalog(mCurThumbCatalog, 0, localImageListViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			}

		});
	}

	//init remote image
	private void initRemoteImageListView() {
		remoteImageListViewAdapter = new ListViewFileThumbAdapter(this, remoteImageListViewData,  R.layout.layout_thumb_grid_item);
		remoteImageListViewAdapter.setFileType(UIHelper.LISTVIEW_DATATYPE_REMOTE_IMAGE);
		//remoteImageListViewFooter = getLayoutInflater().inflate(R.layout.layout_list_view_footer, null);
		//remoteImageListViewFootMore = (TextView)remoteImageListViewFooter.findViewById(R.id.list_view_foot_more);
		//remoteImageListViewFootProgress = (ProgressBar)remoteImageListViewFooter.findViewById(R.id.list_view_foot_progress);
		remoteImageListView = (PullToRefreshGridView)findViewById(R.id.frame_list_view_remote_image);
		//remoteImageListView.addFooterView(remoteImageListViewFooter);
		remoteImageListView.setAdapter(remoteImageListViewAdapter);
		remoteImageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchRecordListActivity(remoteImageListViewAdapter.getItem(position).getFileType(), remoteImageListViewAdapter.getItem(position).getName());
			}
		});
		remoteImageListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				///remoteImageListView.onScrollStateChanged(view, scrollState);
				//数据为空--不用继续下面代码了
				if(remoteImageListViewData.size() == 0) return;
				//判断是否滚动到底部
				if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
				} else {
				}
				boolean scrollEnd = false;
				/*try{
					if(view.getPositionForView(remoteImageListViewFooter) == view.getLastVisiblePosition()) {
						scrollEnd = true;
					}
				} catch (Exception e) {
					scrollEnd = false;
				}
				int imageListDataState = StringUtils.toInt(remoteImageListView.getTag());
				if(scrollEnd && imageListDataState == UIHelper.LISTVIEW_DATA_MORE) {
					remoteImageListViewFootMore.setText(R.string.load_ing);
					remoteImageListViewFootProgress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = remoteImageListSumData/AppContext.PAGE_SIZE;
					loadImageByCatalog(AppContext.CATALOG_REMOTE_IMAGE, pageIndex, remoteImageListViewHandler, UIHelper.LISTVIEW_ACTION_SCROLL, UIHelper.LISTVIEW_DATATYPE_REMOTE_IMAGE);
				}*/
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
				//remoteImageListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				if(remoteImageListViewData.size()<=0) {
					remoteImageListView.onScrollStateChanged(view, SCROLL_STATE_TOUCH_SCROLL);
				}
			}
		});
		remoteImageListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (remoteImageListViewAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(remoteImageListView.getWidth() / (mImageThumbWidth + mImageThumbSpacing));
					if (numColumns > 0) {
						final int columnWidth =
								(remoteImageListView.getWidth() / numColumns) - mImageThumbSpacing;
						remoteImageListViewAdapter.setNumColumns(numColumns);
						remoteImageListViewAdapter.setItemHeight(columnWidth);
						Log.d(TAG, "onCreateView - remote image view numColumns set to " + numColumns);
					}
				}
			}
		});
		remoteImageListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				loadThumbByCatalog(mCurThumbCatalog, 0, remoteImageListViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_REMOTE_IMAGE);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			}

		});
	}

	//init local video
	private void initLocalVideoListView() {
		localVideoListViewAdapter = new ListViewFileThumbAdapter(this, localVideoListViewData, R.layout.layout_thumb_grid_item);
		localVideoListViewAdapter.setFileType(UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO);
		//localVideoListViewFooter = getLayoutInflater().inflate(R.layout.layout_list_view_footer, null);
		//localVideoListViewFootMore = (TextView)localVideoListViewFooter.findViewById(R.id.list_view_foot_more);
		//localVideoListViewFootProgress = (ProgressBar)localVideoListViewFooter.findViewById(R.id.list_view_foot_progress);
		localVideoListView = (PullToRefreshGridView)findViewById(R.id.frame_list_view_local_video);
		//localVideoListView.addFooterView(localVideoListViewFooter);//添加底部视图  必须在setAdapter前
		localVideoListView.setAdapter(localVideoListViewAdapter);
		localVideoListView.setEmptyView(null);
		localVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchRecordListActivity(localVideoListViewAdapter.getItem(position).getFileType(), localVideoListViewAdapter.getItem(position).getName());
			}
		});
		localVideoListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//localVideoListView.onScrollStateChanged(view, scrollState);

				//数据为空--不用继续下面代码了
				//if(localVideoListViewData.size() == 0) return;

				//判断是否滚动到底部
                /*boolean scrollEnd = false;
                try {
                    if(view.getPositionForView(localVideoListViewFooter) == view.getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(localVideoListView.getTag());
                if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
                {
                    localVideoListViewFootMore.setText(R.string.load_ing);
                    localVideoListViewFootProgress.setVisibility(View.VISIBLE);
                    //当前pageIndex
                    int pageIndex = localVideoListSumData/AppContext.PAGE_SIZE;
                    loadImageByCatalog(AppContext.CATALOG_LOCAL_VIDEO, pageIndex, localVideoListViewHandler, UIHelper.LISTVIEW_ACTION_SCROLL, UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO);
                }*/
			}
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//localVideoListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				if(localVideoListViewData.size()<=0) {
					localVideoListView.onScrollStateChanged(view, SCROLL_STATE_TOUCH_SCROLL);
				}
			}
		});
		localVideoListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (localVideoListViewAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(
							localVideoListView.getWidth() / (mImageThumbWidth + mImageThumbSpacing));
					if (numColumns > 0) {
						final int columnWidth = (localVideoListView.getWidth() / numColumns) - mImageThumbSpacing;
						localVideoListViewAdapter.setNumColumns(numColumns);
						localVideoListViewAdapter.setItemHeight(columnWidth);
						Log.d(TAG, "onCreateView - local video view numColumns set to " + numColumns);
					}
				}
			}
		});
		localVideoListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				loadThumbByCatalog(mCurThumbCatalog, 0, localVideoListViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			}

		});
	}

	//init remote video
	private void initRemoteVideoListView() {
		remoteVideoListViewAdapter = new ListViewFileThumbAdapter(this, remoteVideoListViewData, R.layout.layout_thumb_grid_item);
		remoteVideoListViewAdapter.setFileType(UIHelper.LISTVIEW_DATATYPE_REMOTE_VIDEO);
		//remoteVideoListViewFooter = getLayoutInflater().inflate(R.layout.layout_list_view_footer, null);
		//remoteVideoListViewFootMore = (TextView)remoteVideoListViewFooter.findViewById(R.id.list_view_foot_more);
		//remoteVideoListViewFootProgress = (ProgressBar)remoteVideoListViewFooter.findViewById(R.id.list_view_foot_progress);
		remoteVideoListView = (PullToRefreshGridView)findViewById(R.id.frame_list_view_remote_video);
		//remoteVideoListView.addFooterView(remoteVideoListViewFooter);//添加底部视图  必须在setAdapter前
		remoteVideoListView.setAdapter(remoteVideoListViewAdapter);
		remoteVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchRecordListActivity(remoteVideoListViewAdapter.getItem(position).getFileType(), remoteVideoListViewAdapter.getItem(position).getName());
			}
		});
		remoteVideoListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//remoteVideoListView.onScrollStateChanged(view, scrollState);

				//数据为空--不用继续下面代码了
				if(remoteVideoListViewData.size() == 0) return;

				//判断是否滚动到底部
                /*boolean scrollEnd = false;
                try {
                    if(view.getPositionForView(remoteVideoListViewFooter) == view.getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(remoteVideoListView.getTag());
                if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
                {
                    remoteVideoListViewFootMore.setText(R.string.load_ing);
                    remoteVideoListViewFootProgress.setVisibility(View.VISIBLE);
                    //当前pageIndex
                    int pageIndex = remoteVideoListSumData/AppContext.PAGE_SIZE;
                    loadImageByCatalog(AppContext.CATALOG_REMOTE_VIDEO, pageIndex, remoteVideoListViewHandler, UIHelper.LISTVIEW_ACTION_SCROLL, UIHelper.LISTVIEW_DATATYPE_REMOTE_VIDEO);
                }*/
			}
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//remoteVideoListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				if(remoteVideoListViewData.size()<=0) {
					remoteVideoListView.onScrollStateChanged(view, SCROLL_STATE_TOUCH_SCROLL);
				}
			}
		});
		remoteVideoListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (remoteVideoListViewAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(
							remoteVideoListView.getWidth() / (mImageThumbWidth + mImageThumbSpacing));
					if (numColumns > 0) {
						final int columnWidth = (remoteVideoListView.getWidth() / numColumns) - mImageThumbSpacing;
						remoteVideoListViewAdapter.setNumColumns(numColumns);
						remoteVideoListViewAdapter.setItemHeight(columnWidth);
						Log.d(TAG, "onCreateView - remote video view numColumns set to " + numColumns);
					}
				}
			}
		});
		remoteVideoListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				loadThumbByCatalog(mCurThumbCatalog, 0, remoteVideoListViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_REMOTE_VIDEO);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			}

		});
	}

	//init local audio
	private void initLocalAudioListView() {
		localAudioListViewAdapter = new ListViewFileThumbAdapter(this, localAudioListViewData, R.layout.layout_thumb_grid_item);
		localAudioListViewAdapter.setFileType(UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
		//localAudioListViewFooter = getLayoutInflater().inflate(R.layout.layout_list_view_footer, null);
		//localAudioListViewFootMore = (TextView)localAudioListViewFooter.findViewById(R.id.list_view_foot_more);
		//localAudioListViewFootProgress = (ProgressBar)localAudioListViewFooter.findViewById(R.id.list_view_foot_progress);
		localAudioListView = (PullToRefreshGridView) findViewById(R.id.frame_list_view_local_audio);
		//localAudioListView.addFooterView(localAudioListViewFooter);//添加底部视图  必须在setAdapter前
		localAudioListView.setAdapter(localAudioListViewAdapter);
		localAudioListView.setEmptyView(null);
		localAudioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchRecordListActivity(localAudioListViewAdapter.getItem(position).getFileType(), localAudioListViewAdapter.getItem(position).getName());
			}
		});
		localAudioListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//localAudioListView.onScrollStateChanged(view, scrollState);

				//数据为空--不用继续下面代码了
				if(localAudioListViewData.size() == 0) return;

				//判断是否滚动到底部
                /*boolean scrollEnd = false;
                try {
                    if(view.getPositionForView(localAudioListViewFooter) == view.getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(localAudioListView.getTag());
                if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
                {
                    localAudioListViewFootMore.setText(R.string.load_ing);
                    localAudioListViewFootProgress.setVisibility(View.VISIBLE);
                    //当前pageIndex
                    int pageIndex = localAudioListSumData/AppContext.PAGE_SIZE;
                    loadImageByCatalog(AppContext.CATALOG_LOCAL_AUDIO, pageIndex, localAudioListViewHandler, UIHelper.LISTVIEW_ACTION_SCROLL, UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
                }*/
			}
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//localAudioListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				if(localAudioListViewData.size()<=0) {
					localAudioListView.onScrollStateChanged(view, SCROLL_STATE_TOUCH_SCROLL);
				}
			}
		});
		localAudioListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (localAudioListViewAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(
							localAudioListView.getWidth() / (mImageThumbWidth + mImageThumbSpacing));
					if (numColumns > 0) {
						final int columnWidth = (localAudioListView.getWidth() / numColumns) - mImageThumbSpacing;
						localAudioListViewAdapter.setNumColumns(numColumns);
						localAudioListViewAdapter.setItemHeight(columnWidth);
						Log.d(TAG, "onCreateView - local audio view numColumns set to " + numColumns);
					}
				}
			}
		});
		localAudioListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				loadThumbByCatalog(mCurThumbCatalog, 0, localAudioListViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			}

		});
	}

	//init remote audio
	private void initRemoteAudioListView() {
		remoteAudioListViewAdapter = new ListViewFileThumbAdapter(this, remoteAudioListViewData, R.layout.layout_thumb_grid_item);
		remoteAudioListViewAdapter.setFileType(UIHelper.LISTVIEW_DATATYPE_REMOTE_AUDIO);
		//remoteAudioListViewFooter = getLayoutInflater().inflate(R.layout.layout_list_view_footer, null);
		//remoteAudioListViewFootMore = (TextView)remoteAudioListViewFooter.findViewById(R.id.list_view_foot_more);
		//remoteAudioListViewFootProgress = (ProgressBar)remoteAudioListViewFooter.findViewById(R.id.list_view_foot_progress);
		remoteAudioListView = (PullToRefreshGridView)findViewById(R.id.frame_list_view_remote_audio);
		//remoteAudioListView.addFooterView(remoteAudioListViewFooter);//添加底部视图  必须在setAdapter前
		remoteAudioListView.setAdapter(remoteAudioListViewAdapter);
		remoteAudioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchRecordListActivity(remoteAudioListViewAdapter.getItem(position).getFileType(), remoteAudioListViewAdapter.getItem(position).getName());
			}
		});
		remoteAudioListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//remoteAudioListView.onScrollStateChanged(view, scrollState);

				//数据为空--不用继续下面代码了
				if(remoteAudioListViewData.size() == 0) return;

				//判断是否滚动到底部
                /*boolean scrollEnd = false;
                try {
                    if(view.getPositionForView(remoteAudioListViewFooter) == view.getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(remoteAudioListView.getTag());
                if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
                {
                    remoteAudioListViewFootMore.setText(R.string.load_ing);
                    remoteAudioListViewFootProgress.setVisibility(View.VISIBLE);
                    //当前pageIndex
                    int pageIndex = remoteAudioListSumData/AppContext.PAGE_SIZE;
                    loadImageByCatalog(AppContext.CATALOG_REMOTE_AUDIO, pageIndex, remoteAudioListViewHandler, UIHelper.LISTVIEW_ACTION_SCROLL, UIHelper.LISTVIEW_DATATYPE_REMOTE_AUDIO);
                }*/
			}
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//remoteAudioListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				if(remoteAudioListViewData.size()<=0) {
					remoteAudioListView.onScrollStateChanged(view, SCROLL_STATE_TOUCH_SCROLL);
				}
			}
		});
		remoteAudioListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (remoteAudioListViewAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(
							remoteAudioListView.getWidth() / (mImageThumbWidth + mImageThumbSpacing));
					if (numColumns > 0) {
						final int columnWidth = (remoteAudioListView.getWidth() / numColumns) - mImageThumbSpacing;
						remoteAudioListViewAdapter.setNumColumns(numColumns);
						remoteAudioListViewAdapter.setItemHeight(columnWidth);
						Log.d(TAG, "onCreateView - remote audio view numColumns set to " + numColumns);
					}
				}
			}
		});
		remoteAudioListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				loadThumbByCatalog(mCurThumbCatalog, 0, remoteAudioListViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_REMOTE_AUDIO);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			}

		});
	}

	//init local other
	private void initLocalOtherListView() {
		localOtherListViewAdapter = new ListViewFileThumbAdapter(this, localOtherListViewData, R.layout.layout_thumb_grid_item);
		//localOtherListViewFooter = getLayoutInflater().inflate(R.layout.layout_list_view_footer, null);
		//localOtherListViewFootMore = (TextView)localOtherListViewFooter.findViewById(R.id.list_view_foot_more);
		//localOtherListViewFootProgress = (ProgressBar)localOtherListViewFooter.findViewById(R.id.list_view_foot_progress);
		localOtherListView = (PullToRefreshGridView)findViewById(R.id.frame_list_view_local_other);
		//localOtherListView.addFooterView(localOtherListViewFooter);//添加底部视图  必须在setAdapter前
		localOtherListView.setAdapter(localOtherListViewAdapter);
		localOtherListView.setEmptyView(null);
		localOtherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchRecordListActivity(localOtherListViewAdapter.getItem(position).getFileType(), localOtherListViewAdapter.getItem(position).getName());
			}
		});
		localOtherListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//localOtherListView.onScrollStateChanged(view, scrollState);

				//数据为空--不用继续下面代码了
				if(localOtherListViewData.size() == 0) return;

				//判断是否滚动到底部
                /*boolean scrollEnd = false;
                try {
                    if(view.getPositionForView(localOtherListViewFooter) == view.getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(localOtherListView.getTag());
                if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
                {
                    localOtherListViewFootMore.setText(R.string.load_ing);
                    localOtherListViewFootProgress.setVisibility(View.VISIBLE);
                    //当前pageIndex
                    int pageIndex = localOtherListSumData/AppContext.PAGE_SIZE;
                    loadImageByCatalog(AppContext.CATALOG_LOCAL_OTHER, pageIndex, localOtherListViewHandler, UIHelper.LISTVIEW_ACTION_SCROLL, UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER);
                }*/
			}
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//localOtherListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				if(localOtherListViewData.size()<=0) {
					localOtherListView.onScrollStateChanged(view, SCROLL_STATE_TOUCH_SCROLL);
				}
			}
		});
		localOtherListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (localOtherListViewAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(
							localOtherListView.getWidth() / (mImageThumbWidth + mImageThumbSpacing));
					if (numColumns > 0) {
						final int columnWidth = (localOtherListView.getWidth() / numColumns) - mImageThumbSpacing;
						localOtherListViewAdapter.setNumColumns(numColumns);
						localOtherListViewAdapter.setItemHeight(columnWidth);
						Log.d(TAG, "onCreateView - local other view numColumns set to " + numColumns);
					}
				}
			}
		});
		localOtherListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				loadThumbByCatalog(mCurThumbCatalog, 0, localOtherListViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			}

		});
	}

	//init remote other
	private void initRemoteOtherListView() {
		remoteOtherListViewAdapter = new ListViewFileThumbAdapter(this, remoteOtherListViewData, R.layout.layout_thumb_grid_item);
		//remoteOtherListViewFooter = getLayoutInflater().inflate(R.layout.layout_list_view_footer, null);
		//remoteOtherListViewFootMore = (TextView)remoteOtherListViewFooter.findViewById(R.id.list_view_foot_more);
		//remoteOtherListViewFootProgress = (ProgressBar)remoteOtherListViewFooter.findViewById(R.id.list_view_foot_progress);
		remoteOtherListView = (PullToRefreshGridView)findViewById(R.id.frame_list_view_remote_other);
		//remoteOtherListView.addFooterView(remoteOtherListViewFooter);//添加底部视图  必须在setAdapter前
		remoteOtherListView.setAdapter(remoteVideoListViewAdapter);
		remoteOtherListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				launchRecordListActivity(remoteVideoListViewAdapter.getItem(position).getFileType(), remoteVideoListViewAdapter.getItem(position).getName());
			}
		});
		remoteVideoListView.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//remoteOtherListView.onScrollStateChanged(view, scrollState);

				//数据为空--不用继续下面代码了
				if(remoteOtherListViewData.size() == 0) return;

				//判断是否滚动到底部
				/*boolean scrollEnd = false;
				try {
					if(view.getPositionForView(remoteOtherListViewFooter) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(remoteOtherListView.getTag());
				if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
				{
					remoteOtherListViewFootMore.setText(R.string.load_ing);
					remoteOtherListViewFootProgress.setVisibility(View.VISIBLE);
					//当前pageIndex
					int pageIndex = remoteOtherListSumData/AppContext.PAGE_SIZE;
					curImageCatalog = AppContext.CATALOG_REMOTE_OTHER;
					loadImageByCatalog(curImageCatalog, pageIndex, remoteOtherListViewHandler, UIHelper.LISTVIEW_ACTION_SCROLL, UIHelper.LISTVIEW_DATATYPE_REMOTE_OTHER);
				}*/
			}
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//remoteOtherListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				if(remoteOtherListViewData.size()<=0) {
					remoteOtherListView.onScrollStateChanged(view, SCROLL_STATE_TOUCH_SCROLL);
				}
			}
		});
		remoteOtherListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (remoteOtherListViewAdapter.getNumColumns() == 0) {
					final int numColumns = (int) Math.floor(
							remoteOtherListView.getWidth() / (mImageThumbWidth + mImageThumbSpacing));
					if (numColumns > 0) {
						final int columnWidth = (remoteOtherListView.getWidth() / numColumns) - mImageThumbSpacing;
						remoteOtherListViewAdapter.setNumColumns(numColumns);
						remoteOtherListViewAdapter.setItemHeight(columnWidth);
						Log.d(TAG, "onCreateView - remote other view numColumns set to " + numColumns);
					}
				}
			}
		});
		remoteOtherListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				loadThumbByCatalog(mCurThumbCatalog, 0, remoteOtherListViewHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_REMOTE_OTHER);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			}

		});
	}

	public void handleImageListData(int what, Object obj, int objtype, int actiontype) {
		switch (actiontype) {
			case UIHelper.LISTVIEW_ACTION_INIT:
			case UIHelper.LISTVIEW_ACTION_REFRESH:
			case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
				switch (objtype) {
					case UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE:
						List<FileThumb> localImageList = (List<FileThumb>)obj;
						localImageListSumData = what;
						localImageListViewData.clear();//先清除原有数据
						localImageListViewData.addAll(localImageList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO:
						List<FileThumb> localVideoList = (List<FileThumb>)obj;
						localVideoListSumData = what;
						localVideoListViewData.clear();//先清除原有数据
						localVideoListViewData.addAll(localVideoList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO:
						List<FileThumb> localAudioList = (List<FileThumb>)obj;
						localAudioListSumData = what;
						localAudioListViewData.clear();//先清除原有数据
						localAudioListViewData.addAll(localAudioList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER:
						List<FileThumb> localOtherList = (List<FileThumb>)obj;
						localOtherListSumData = what;
						localOtherListViewData.clear();//先清除原有数据
						localOtherListViewData.addAll(localOtherList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_REMOTE_IMAGE:
						List<FileThumb> remoteImageList = (List<FileThumb>)obj;
						remoteImageListSumData = what;
						remoteImageListViewData.clear();//先清除原有数据
						remoteImageListViewData.addAll(remoteImageList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_REMOTE_VIDEO:
						List<FileThumb> remoteVideoList = (List<FileThumb>)obj;
						remoteVideoListSumData = what;
						remoteVideoListViewData.clear();//先清除原有数据
						remoteVideoListViewData.addAll(remoteVideoList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_REMOTE_AUDIO:
						List<FileThumb> remoteAudioList = (List<FileThumb>)obj;
						remoteAudioListSumData = what;
						remoteAudioListViewData.clear();//先清除原有数据
						remoteAudioListViewData.addAll(remoteAudioList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_REMOTE_OTHER:
						List<FileThumb> remoteOtherList = (List<FileThumb>)obj;
						remoteOtherListSumData = what;
						remoteOtherListViewData.clear();//先清除原有数据
						remoteOtherListViewData.addAll(remoteOtherList);
						break;
					default:
						break;
				}
				break;
			case UIHelper.LISTVIEW_ACTION_SCROLL:
				switch (objtype) {
					case UIHelper.LISTVIEW_DATATYPE_LOCAL_IMAGE:
						List<FileThumb> localImageFileList = (List<FileThumb>)obj;
						localImageListSumData += what;
						localImageListViewData.addAll(localImageFileList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_LOCAL_VIDEO:
						List<FileThumb> localVideoFileList = (List<FileThumb>)obj;
						localVideoListSumData += what;
						localVideoListViewData.addAll(localVideoFileList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO:
						List<FileThumb> localAudioFileList = (List<FileThumb>)obj;
						localAudioListSumData += what;
						localAudioListViewData.addAll(localAudioFileList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_LOCAL_OTHER:
						List<FileThumb> localOtherFileList = (List<FileThumb>)obj;
						localOtherListSumData += what;
						localOtherListViewData.addAll(localOtherFileList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_REMOTE_IMAGE:
						List<FileThumb> remoteImageFileList = (List<FileThumb>)obj;
						remoteImageListSumData += what;
						remoteImageListViewData.addAll(remoteImageFileList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_REMOTE_VIDEO:
						List<FileThumb> remoteVideoFileList = (List<FileThumb>)obj;
						remoteVideoListSumData += what;
						remoteVideoListViewData.addAll(remoteVideoFileList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_REMOTE_AUDIO:
						List<FileThumb> remoteAudioFileList = (List<FileThumb>)obj;
						remoteAudioListSumData += what;
						remoteAudioListViewData.addAll(remoteAudioFileList);
						break;
					case UIHelper.LISTVIEW_DATATYPE_REMOTE_OTHER:
						List<FileThumb> remoteOtherFileList = (List<FileThumb>)obj;
						remoteOtherListSumData += what;
						remoteOtherListViewData.addAll(remoteOtherFileList);
						break;
					default:
						break;
				}
		}
	}

	private void loadThumbByCatalog(final int catalog, final int pageIndex, final Handler handler, final int action, final int objType){
		mHeadProgress.setVisibility(ProgressBar.VISIBLE);
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					List<FileThumb> localAlumbListTmp = mFileManager.loadFileThumbList(catalog>AppContext.CATALOG_LOCAL_OTHER?false:true, catalog, pageIndex, getOffset(), FileUtils.getLaunchModeSet());
					msg.what = localAlumbListTmp.size();
					msg.obj = localAlumbListTmp;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = objType;
				Log.d(TAG, "===>curImageCatalog = " + mCurThumbCatalog + " catalog = " + catalog + " objtype = " + objType + " number = " + msg.what);
				if(mCurThumbCatalog == catalog) {
					handler.sendMessage(msg);
				}
			}
		}.start();
	}

	private void launchRecordListActivity(int mediaType, String thumbName){
		Intent intent = new Intent();
		if(mediaType == FileProvider.FILE_TYPE_JEPG){
			intent.setClass(this, ImageList.class);
		} else if(mediaType == FileProvider.FILE_TYPE_VIDEO){
			intent.setClass(this, VideoRecordList.class);
		} else if(mediaType == FileProvider.FILE_TYPE_AUDIO){
			intent.setClass(this, AudioRecordList.class);
		} else if(mediaType == FileProvider.FILE_TYPE_OTHER){
			intent.setClass(this, AudioRecordList.class);
		}
		intent.putExtra(EXTRA_THUMB_NAME, thumbName);
		startActivity(intent);
	}
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(observer != null){
			getContentResolver().unregisterContentObserver(observer);
			observer = null;
		}
	}

	private void initData(){
		ContentValues value = new ContentValues();
		value.put(FileColumn.COLUMN_FILE_TYPE, 0);
		value.put(FileColumn.COLUMN_LOCAL_PATH, "/storage/sdcard0/mpt/2000-01-01/001/jpg/08/56/41[R][0@0][0].jpg");
		for(int i=0;i<13;i++){
			value.put(FileColumn.COLUMN_THUMB_NAME, "2000-01-0"+i);
			value.put(FileColumn.COLUMN_MIME_TYPE, "jpeg/*");
			value.put(FileColumn.COLUMN_DOWN_LOAD_TIME, System.currentTimeMillis());
			value.put(FileColumn.COLUMN_LAUNCH_MODE, 2);
			value.put(FileColumn.COLUMN_FILE_DURATION, 122);
			value.put(FileColumn.COLUMN_DOWN_LOAD_TIME, System.currentTimeMillis());
			value.put(FileColumn.COLUMN_UP_LOAD_TIME, System.currentTimeMillis());
			value.put(FileColumn.COLUMN_FILE_RESOLUTION_X, 0);
			value.put(FileColumn.COLUMN_FILE_RESOLUTION_Y, 0);
			getContentResolver().insert(FileProvider.JPEGS_URI, value);
		}
	}

	private void initObserver(){
		if(observer == null){
			observer = new FileListChangeObserver(mHandler);
			getContentResolver().registerContentObserver(FileProvider.JPEGS_URI, true, observer);
			getContentResolver().registerContentObserver(FileProvider.VIDEOS_URI, true, observer);
			getContentResolver().registerContentObserver(FileProvider.AUDIOS_URI, true, observer);
		}
	}

	private class FileListChangeObserver extends ContentObserver {

		public FileListChangeObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			String name = uri.getPathSegments().get(0);
			if("jpeg".equalsIgnoreCase(name)){
				mHandler.removeMessages(0);
				Message msg = mHandler.obtainMessage(0);
				msg.arg1 = FileProvider.FILE_TYPE_JEPG;
				mHandler.sendMessageDelayed(msg, 100);
			} else if("video".equalsIgnoreCase(name)){
				mHandler.removeMessages(0);
				Message msg = mHandler.obtainMessage(0);
				msg.arg1 = FileProvider.FILE_TYPE_VIDEO;
				mHandler.sendMessageDelayed(msg, 100);
			} else if("audio".equalsIgnoreCase(name)){
				mHandler.removeMessages(0);
				Message msg = mHandler.obtainMessage(0);
				msg.arg1 = FileProvider.FILE_TYPE_AUDIO;
				mHandler.sendMessageDelayed(msg, 100);
			}
			super.onChange(selfChange, uri);
		}
	}

	private IResultListener mResultListener = new IResultListener(){

		@Override
		public void OnTypeList(String json) {
		}

		@Override
		public void OnThumbList(List<TiangoImageThumbListResp> list) {
		}

		@Override
		public void OnImageList(List<TiangoImageListResp> list) {
		}

	};

	private int getOffset(){
		List<FileThumb> temp = null;
		switch (mCurThumbCatalog) {
			case AppContext.CATALOG_LOCAL_IMAGE:
				temp = localImageListViewData;
				break;
			case AppContext.CATALOG_LOCAL_VIDEO:
				temp = localVideoListViewData;
				break;
			case AppContext.CATALOG_LOCAL_AUDIO:
				temp = localAudioListViewData;
				break;
			case AppContext.CATALOG_LOCAL_OTHER:
				temp = localOtherListViewData;
				break;
			case AppContext.CATALOG_REMOTE_IMAGE:
				temp = remoteImageListViewData;
				break;
			case AppContext.CATALOG_REMOTE_VIDEO:
				temp = remoteVideoListViewData;
				break;
			case AppContext.CATALOG_REMOTE_AUDIO:
				temp = remoteAudioListViewData;
				break;
			case AppContext.CATALOG_REMOTE_OTHER:
				temp = remoteOtherListViewData;
				break;
			default:
				temp = localImageListViewData;
				break;
		}
		return temp.size()/IFileManager.PERPAGE_NUMBER;
	}
}
