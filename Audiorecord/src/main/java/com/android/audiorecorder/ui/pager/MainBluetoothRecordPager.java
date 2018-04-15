package com.android.audiorecorder.ui.pager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.audiorecorder.AppContext;
import com.android.audiorecorder.R;
import com.android.audiorecorder.dao.FileManagerFactory;
import com.android.audiorecorder.dao.FileThumb;
import com.android.audiorecorder.dao.IFileManager;
import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.provider.FileColumn;
import com.android.audiorecorder.provider.FileProvider;
import com.android.audiorecorder.ui.AudioRecordList;
import com.android.audiorecorder.ui.ImageList;
import com.android.audiorecorder.ui.VideoRecordList;
import com.android.audiorecorder.ui.adapter.ListViewFileThumbAdapter;
import com.android.audiorecorder.utils.FileUtils;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.UIHelper;
import com.android.library.ui.pager.BasePager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

public class MainBluetoothRecordPager extends BasePager {

    public final static String EXTRA_THUMB_NAME = "thumb_name";
    
    private int mImageThumbSize;
    private int mImageThumbHeight;
    private int mImageThumbSpacing;
    
    private PullToRefreshGridView localAudioListView;
    private ListViewFileThumbAdapter mLocalAudioListViewAdapter;
    private List<FileThumb> mLocalAudioListViewData;
    private IFileManager mFileManager;
    private int mOffset;
    private int mMode = MultiMediaService.LUNCH_MODE_MANLY;
    
    
    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_main_record_list, null);
        Bundle bundle = getArguments();
        if(bundle != null){
        	mMode = bundle.getInt("mode", mMode);
        }
        LogUtil.d(TAG, "==> mode = " + mMode);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_width);
        mImageThumbHeight = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_height);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        localAudioListView = (PullToRefreshGridView) view.findViewById(R.id.listView);
        localAudioListView.setEmptyView(null);
        mLocalAudioListViewData = new ArrayList<FileThumb>();
        mFileManager = FileManagerFactory.getFileManagerInstance(getActivity());
        mLocalAudioListViewAdapter = new ListViewFileThumbAdapter(getActivity(), mLocalAudioListViewData, R.layout.layout_thumb_grid_item);
        mLocalAudioListViewAdapter.setFileType(UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
        localAudioListView.setAdapter(mLocalAudioListViewAdapter);
        mOffset = 0;
        localAudioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchRecordListActivity(mLocalAudioListViewAdapter.getItem(position).getFileType(), mLocalAudioListViewAdapter.getItem(position).getName());
            }
        });
        localAudioListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //localAudioListView.onScrollStateChanged(view, scrollState);
                
                if(mLocalAudioListViewData.size() == 0) return;
            }
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //localAudioListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                if(mLocalAudioListViewData.size()<=0) {
                    localAudioListView.onScrollStateChanged(view, SCROLL_STATE_TOUCH_SCROLL);
                }
            }
        });
        localAudioListView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            
            @Override
            public void onGlobalLayout() {
                if (mLocalAudioListViewAdapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(
                            localAudioListView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (localAudioListView.getWidth() / numColumns) - mImageThumbSpacing;
                        mLocalAudioListViewAdapter.setNumColumns(numColumns);
                        mLocalAudioListViewAdapter.setItemHeight(columnWidth);
                        Log.d(TAG, "onCreateView - local audio view numColumns set to " + numColumns);
                    }
                }
            }
        });
        localAudioListView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
            	mOffset = 0;
            	if(checkInit()){
            		loadThumbByCatalog(AppContext.CATALOG_LOCAL_AUDIO, 0, mHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
            	}
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
            }

        });
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(checkInit()){
        	loadThumbByCatalog(AppContext.CATALOG_LOCAL_AUDIO, 0, mHandler, UIHelper.LISTVIEW_ACTION_INIT, UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
        }
    }

    @Override
    public void reload() {
    }
    
    private Handler mHandler = new Handler(){
      public void handleMessage(Message msg) {
          super.handleMessage(msg);
          if(msg.what>=0) {
              handleImageListData(msg.what, msg.obj, msg.arg2, msg.arg1);
              if(msg.what<IFileManager.PERPAGE_NUMBER) {
                  localAudioListView.setTag(UIHelper.LISTVIEW_DATA_FULL);
                  mLocalAudioListViewAdapter.notifyDataSetChanged();
                  localAudioListView.onRefreshComplete();
                  //more.setText(R.string.load_full);
              }else if(msg.what == IFileManager.PERPAGE_NUMBER) {
                  localAudioListView.setTag(UIHelper.LISTVIEW_DATA_MORE);
                  mLocalAudioListViewAdapter.notifyDataSetChanged();
                  //more.setText(R.string.load_more);
              }
          }else if(msg.what == -1) {
              localAudioListView.setTag(UIHelper.LISTVIEW_DATA_MORE);
              //more.setText(R.string.load_error);
          }
          if(mLocalAudioListViewAdapter.getCount() ==0) {
              localAudioListView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
              //more.setText(R.string.load_empty);
          }
          //progress.setVisibility(ProgressBar.GONE);
          if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
              //imageListView.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
              localAudioListView.onRefreshComplete();
              //imageListView.setSelection(0);
          }else if(msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
              localAudioListView.onRefreshComplete();
              //imageListView.setSelection(0);
          }
      };  
    };
    private void launchRecordListActivity(int mediaType, String thumbName){
        Intent intent = new Intent();
        if(mediaType == FileProvider.FILE_TYPE_JEPG){
            intent.setClass(getActivity(), ImageList.class);
        } else if(mediaType == FileProvider.FILE_TYPE_VIDEO){
            intent.setClass(getActivity(), VideoRecordList.class);
        } else if(mediaType == FileProvider.FILE_TYPE_AUDIO){
            intent.setClass(getActivity(), AudioRecordList.class);
        } else if(mediaType == FileProvider.FILE_TYPE_OTHER){
            intent.setClass(getActivity(), ImageList.class);
        }
        intent.putExtra(EXTRA_THUMB_NAME, thumbName);
        intent.putExtra("mode", mMode);
        LogUtil.d(TAG, "==> mode = " + mMode + " thumbName = " + thumbName);
        startActivity(intent);
    }
    
    private void loadThumbByCatalog(final int catalog, final int pageIndex, final Handler handler, final int action, final int objType){
        //mHeadProgress.setVisibility(ProgressBar.VISIBLE);
        new Thread(){
            public void run() {        
                Message msg = new Message();
                try {
                    List<FileThumb> localAlumbListTmp = mFileManager.loadFileThumbList(catalog>AppContext.CATALOG_LOCAL_OTHER?false:true, catalog, pageIndex, mOffset, FileUtils.getLaunchModeSet(mMode));
                    msg.what = localAlumbListTmp.size();
                    msg.obj = localAlumbListTmp;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = objType;
                //Log.d(TAG, "===>curImageCatalog = " + mCurThumbCatalog + " catalog = " + catalog + " objtype = " + objType + " number = " + msg.what);
                //if(mCurThumbCatalog == catalog) {
                    handler.sendMessage(msg);
                //}
            }
        }.start();
    }
    
    private void handleImageListData(int what, Object obj, int objtype, int actiontype) {
        switch (actiontype) {
        case UIHelper.LISTVIEW_ACTION_INIT:
        case UIHelper.LISTVIEW_ACTION_REFRESH:
        case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
            switch (objtype) {
                case UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO:
                    List<FileThumb> localAudioList = (List<FileThumb>)obj;
                    mLocalAudioListViewData.clear();
                    mLocalAudioListViewData.addAll(localAudioList);
                    break;
                    default:
                        break;
            }
            break;
        case UIHelper.LISTVIEW_ACTION_SCROLL:
            switch (objtype) {
            case UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO:
                List<FileThumb> localAudioFileList = (List<FileThumb>)obj;
                //localAudioListSumData += what;
                mLocalAudioListViewData.addAll(localAudioFileList);
                mOffset = mLocalAudioListViewData.size()%IFileManager.PERPAGE_NUMBER;
                break;
                default:
                    break;
            }
        }
    }
    
    private boolean checkInit(){
    	/*String initValue = StringUtils.getString(getActivity(), FileColumn.COLUMN_FILE_INIT, 0);
    	return StringUtil.toInt(initValue, 0) == 1;*/
    	String[] pro = {FileColumn.COLUMN_SETTING_VALUE};
    	String where = FileColumn.COLUMN_SETTING_KEY + "='" + FileColumn.COLUMN_FILE_INIT + "'";
    	Cursor cursor = getActivity().getContentResolver().query(FileProvider.SETTINGS_URI, pro, where, null, null);
    	int value = 0;
    	if(cursor != null){
    		if(cursor.moveToNext()){
    			value = cursor.getInt(0);
    		}
    		cursor.close();
    	}
    	return value == 1;
    }
}
