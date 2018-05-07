package com.android.audiorecorder.ui.pager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.android.audiorecorder.AppContext;
import com.android.audiorecorder.R;
import com.android.audiorecorder.dao.FileManagerFactory;
import com.android.audiorecorder.dao.IFileManager;
import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.provider.FileDetail;
import com.android.audiorecorder.provider.FileProvider;
import com.android.audiorecorder.ui.FileExplorerActivity;
import com.android.audiorecorder.ui.ImageViewActvity;
import com.android.audiorecorder.ui.adapter.FileTimeLineGridAdapter;
import com.android.audiorecorder.utils.FileUtils;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.UIHelper;
import com.android.library.ui.pager.BasePager;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileImagePager extends BasePager implements FileExplorerActivity.OnFileSearchListener {

    public final static String EXTRA_THUMB_NAME = "thumb_name";
    
    private GridView mLocalImageGridView;
    private FileTimeLineGridAdapter mStickyGridAdapter;
    private RelativeLayout mNoContent;
    private RefreshLayout mRefreshLayout;
    private ClassicsHeader refreshLayoutHeader;
    private boolean onRefresh;
    private boolean isLoadMore = false;
    public static List<FileDetail> mLocalListViewData;
    private List<FileDetail> searchElementInfos;
    private IFileManager mFileManager;
    private int mOffset;
    private int mPageIndex;
    private int mMode = MultiMediaService.LUNCH_MODE_MANLY;

    private String TAG = "FileImagePager";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileExplorerActivity context = (FileExplorerActivity) getActivity();
        context.setOnFileSearchListener(FileImagePager.this);
    }

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_file_image_timeline_gridview, null);
        Bundle bundle = getArguments();
        if(bundle != null){
        	mMode = bundle.getInt("mode", mMode);
        }
        LogUtil.d(TAG, "==> mode = " + mMode);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayoutHeader = (ClassicsHeader) view.findViewById(R.id.refreshLayout_header);
        mLocalImageGridView = (GridView) view.findViewById(R.id.listView);
        mNoContent = (RelativeLayout) view.findViewById(R.id.activity_nocontent);
        mLocalImageGridView.setEmptyView(mNoContent);
        mNoContent.setVisibility(View.GONE);
        mLocalListViewData = new ArrayList<FileDetail>();
        searchElementInfos = new ArrayList<FileDetail>();
        mFileManager = FileManagerFactory.getFileManagerInstance(getActivity());
        mStickyGridAdapter = new FileTimeLineGridAdapter(getActivity(), searchElementInfos, mLocalImageGridView, null);
        mLocalImageGridView.setAdapter(mStickyGridAdapter);
        mOffset = 0;
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onRefresh = true;
                mOffset = 0;
                mPageIndex = 0;
                loadThumbByCatalog(AppContext.CATALOG_GALLERY_IMAGE, mPageIndex, mHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE);
            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                isLoadMore = true;
                loadThumbByCatalog(AppContext.CATALOG_GALLERY_IMAGE, mPageIndex, mHandler, UIHelper.LISTVIEW_ACTION_SCROLL, UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE);
            }
        });
        mLocalImageGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(mStickyGridAdapter.isChooseMode()) {
                    FileExplorerActivity context = (FileExplorerActivity) getActivity();
                    context.onFileChecked(FileImagePager.this, -1);
                } else {
                    FileDetail detail = mStickyGridAdapter.getItem(position);
                    if(detail != null) {
                        detail.setChecked(true);
                        mStickyGridAdapter.notifyDataSetChanged();
                    }
                    FileExplorerActivity context = (FileExplorerActivity) getActivity();
                    context.onFileChecked(FileImagePager.this, getCheckedCount());
                }
                return true;
            }
        });
        mLocalImageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mStickyGridAdapter.isChooseMode()) {
                    FileDetail detail = mStickyGridAdapter.getItem(position);
                    if(detail != null) {
                        detail.setChecked(!detail.isChecked());
                        mStickyGridAdapter.notifyDataSetChanged();
                    }
                    FileExplorerActivity context = (FileExplorerActivity) getActivity();
                    context.onFileChecked(FileImagePager.this, getCheckedCount());
                } else {
                    Intent intent = new Intent(getActivity(), ImageViewActvity.class);
                    intent.putExtra("TAG_GRID_INDEX", position);
                    intent.putExtra("TAG_PATH_LIST_SIZE", searchElementInfos.size());
                    startActivity(intent);
                }
            }
        });
        loadThumbByCatalog(AppContext.CATALOG_GALLERY_IMAGE, 0, mHandler, UIHelper.LISTVIEW_ACTION_INIT, UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE);
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "==> onActivityCreated.");
    }

    @Override
    public void reload() {
        LogUtil.d(TAG, "==> reload.");
        loadThumbByCatalog(AppContext.CATALOG_GALLERY_IMAGE, 0, mHandler, UIHelper.LISTVIEW_ACTION_INIT, UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE);
    }

    public static String getFilePath(int position) {
        if(mLocalListViewData != null && mLocalListViewData.size()>position){
            return mLocalListViewData.get(position).getFilePath();
        }
        return "";
    }

    @Override
    public void onSearch(final String content) {
        Log.d(TAG, "search key:" + content);
        mRefreshLayout.setEnableRefresh(TextUtils.isEmpty(content));
        mRefreshLayout.setEnableLoadmore(TextUtils.isEmpty(content));
        if (!TextUtils.isEmpty(content)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searchElementInfos.clear();
                    for (int i = 0; i < mLocalListViewData.size(); i++) {
                            FileDetail fileDetail = mLocalListViewData.get(i);
                            if(fileDetail != null) {
                                String name = fileDetail.getFileName();
                                String phoneNumber = fileDetail.getPhoneNumber();
                                String displayName = fileDetail.getDisplayName();
                                Log.d(TAG, "==>: " + name + " " + phoneNumber + " " + displayName);
                                if(name != null && name.toLowerCase().contains(content)) {
                                    searchElementInfos.add(mLocalListViewData.get(i));
                                } else if(phoneNumber != null && phoneNumber.toLowerCase().contains(content)) {
                                    searchElementInfos.add(mLocalListViewData.get(i));
                                } else if(displayName != null && displayName.toLowerCase().contains(content)) {
                                    searchElementInfos.add(mLocalListViewData.get(i));
                                }
                            }
                    }
                    if (searchElementInfos.size() > 0) {
                        Message msg = mSearchHandler.obtainMessage();
                        msg.what = SUCESS;
                        msg.sendToTarget();
                    } else {
                        Message msg = mSearchHandler.obtainMessage();
                        msg.what = FAIL;
                        msg.sendToTarget();
                    }
                }
            }).start();
        } else {
            searchElementInfos.clear();
            searchElementInfos.addAll(mLocalListViewData);
            Message msg = mSearchHandler.obtainMessage();
            msg.what = SUCESS;
            msg.sendToTarget();
        }
    }

    @Override
    public void setMode(boolean mode) {
        if(!mode){//clear checked files
            for(FileDetail detail:searchElementInfos) {
                if(detail != null) {
                    detail.setChecked(false);
                }
            }
        }
        mStickyGridAdapter.setMode(mode);
        mStickyGridAdapter.notifyDataSetInvalidated();
    }

    @Override
    public int deleteFile(List<FileDetail> fileList) {
        if(fileList != null && fileList.size()>0) {
            for(FileDetail detail: fileList) {
                mFileManager.removeFile(UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE, detail.getFilePath());
                int length = searchElementInfos.size();
                for(int i=length-1; i>=0; i--) {
                    FileDetail temp = searchElementInfos.get(i);
                    if(temp.getFilePath().equals(detail.getFilePath())) {
                        searchElementInfos.remove(i);
                    }
                }
            }
            mLocalListViewData.clear();
            mLocalListViewData.addAll(searchElementInfos);
            mSearchHandler.sendEmptyMessage(SUCESS);
        }
        return 1;
    }

    @Override
    public int renameFile(String oldPath, String newName) {
        if(oldPath != null) {
            int length = searchElementInfos.size();
            for(int i=length-1; i>=0; i--) {
                FileDetail temp = searchElementInfos.get(i);
                if(temp.getFilePath().equals(oldPath)) {
                    temp.setFilePath(newName);
                    int indexSeparator = newName.lastIndexOf(File.separator);
                    if(indexSeparator>0){
                        String fileName = newName.substring(indexSeparator+1);
                        temp.setFileName(fileName);
                    }
                    break;
                }
            }
            mLocalListViewData.clear();
            mLocalListViewData.addAll(searchElementInfos);
            mSearchHandler.sendEmptyMessage(SUCESS);
            return mFileManager.renameFile(UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE, oldPath, newName);
        } else {
            Log.w(TAG, "==> oldPath null." );
            return -1;
        }
    }

    @Override
    public List<FileDetail> getCheckFileDetail() {
        return getCheckedFileList();
    }


    private Handler mHandler = new Handler(){
      public void handleMessage(Message msg) {
          super.handleMessage(msg);
          if(msg.what>=0) {
              handleImageListData(msg.what, msg.obj, msg.arg1, msg.arg2);
          }
          if(mLocalListViewData.size() == 0){
              mNoContent.setVisibility(View.VISIBLE);
          } else {
              mNoContent.setVisibility(View.GONE);
          }
          mOffset = mLocalListViewData.size() % IFileManager.PERPAGE_NUMBER;
          mPageIndex = mLocalListViewData.size() / IFileManager.PERPAGE_NUMBER;
          onRefresh = false;
      };
    };

    private Handler mSearchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCESS:
                    Log.d(TAG, "==> search list length: " + searchElementInfos.size());
                    mLocalImageGridView.setVisibility(View.VISIBLE);
                    mNoContent.setVisibility(View.GONE);
                    mOffset = mLocalListViewData.size() % IFileManager.PERPAGE_NUMBER;
                    mPageIndex = mLocalListViewData.size() / IFileManager.PERPAGE_NUMBER;
                    mRefreshLayout.finishRefresh();
                    mStickyGridAdapter.notifyDataSetChanged();
                    onRefresh = false;
                    break;
                case FAIL:
                    Log.d(TAG, "==> search list length: " + searchElementInfos.size());
                    mLocalImageGridView.setVisibility(View.GONE);
                    mNoContent.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    private void loadThumbByCatalog(final int catalog, final int pageIndex, final Handler handler, final int action, final int objType){
        new Thread(){
            public void run() {        
                Message msg = new Message();
                try {
                    LogUtil.d(TAG, "==> loadThumbByCatalog catalog: " + catalog + " " + pageIndex + mOffset + " " + FileUtils.getLaunchModeSet(mMode));
                    List<FileDetail> localAlumbListTmp = mFileManager.loadFileList(true, catalog, "", pageIndex, mOffset, FileUtils.getLaunchModeSet(mMode));
                    LogUtil.d(TAG, "==> catalog: " + localAlumbListTmp.size());
                    msg.what = localAlumbListTmp.size();
                    msg.obj = localAlumbListTmp;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                msg.arg1 = action;
                msg.arg2 = objType;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void handleImageListData(int what, Object obj, int action, int objtype) {
        switch (action) {
        case UIHelper.LISTVIEW_ACTION_INIT:
        case UIHelper.LISTVIEW_ACTION_REFRESH:
        case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
            switch (objtype) {
                case UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE:
                    List<FileDetail> localAudioList = (List<FileDetail>)obj;
                    mLocalListViewData.clear();
                    mLocalListViewData.addAll(localAudioList);
                    searchElementInfos.clear();
                    searchElementInfos.addAll(mLocalListViewData);
                    mRefreshLayout.finishRefresh();
                    mStickyGridAdapter.notifyDataSetChanged();
                    break;
                    default:
                        break;
            }
            break;
        case UIHelper.LISTVIEW_ACTION_SCROLL:
            switch (objtype) {
            case UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE:
                List<FileDetail> localImageFileList = (List<FileDetail>)obj;
                mLocalListViewData.addAll(localImageFileList);
                searchElementInfos.addAll(localImageFileList);
                mStickyGridAdapter.notifyDataSetChanged();
                mRefreshLayout.finishLoadmore();
                break;
                default:
                    break;
            }
            break;
        }
    }

    private int getCheckedCount() {
        int count = 0;
        for(FileDetail detail:searchElementInfos) {
            if(detail.isChecked()) {
                count++;
            }
        }
        return count;
    }

    private List<FileDetail> getCheckedFileList() {
        List<FileDetail> mCheckFileList = new ArrayList<FileDetail>();
        for(FileDetail detail:searchElementInfos) {
            if(detail.isChecked()) {
                mCheckFileList.add(detail);
            }
        }
        return mCheckFileList;
    }
}
