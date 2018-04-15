package com.android.audiorecorder.ui.pager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.util.ArrayList;
import java.util.List;

public class FileImagePager extends BasePager {

    public final static String EXTRA_THUMB_NAME = "thumb_name";
    
    private GridView mLocalImageGridView;
    private FileTimeLineGridAdapter mFileTimeLineGridAdapter;
    private RelativeLayout mNoContent;
    private RefreshLayout mRefreshLayout;
    private ClassicsHeader refreshLayoutHeader;
    private boolean onRefresh;
    private boolean isLoadMore = false;
    public static List<FileDetail> mLocalImageListViewData;
    private IFileManager mFileManager;
    private int mOffset;
    private int mPageIndex;
    private int mMode = MultiMediaService.LUNCH_MODE_MANLY;

    private String TAG = "FileImagePager";
    
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
        mLocalImageListViewData = new ArrayList<FileDetail>();
        mFileManager = FileManagerFactory.getFileManagerInstance(getActivity());
        mFileTimeLineGridAdapter = new FileTimeLineGridAdapter(getActivity(), mLocalImageListViewData, mLocalImageGridView, null);
        mLocalImageGridView.setAdapter(mFileTimeLineGridAdapter);
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
        mLocalImageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ImageViewActvity.class);
                intent.putExtra("TAG_GRID_INDEX", position);
                intent.putExtra("TAG_PATH_LIST_SIZE", mLocalImageListViewData.size());
                startActivity(intent);
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
        if(mLocalImageListViewData != null && mLocalImageListViewData.size()>position){
            return mLocalImageListViewData.get(position).getFilePath();
        }
        return "";
    }

    private Handler mHandler = new Handler(){
      public void handleMessage(Message msg) {
          super.handleMessage(msg);
          if(msg.what>=0) {
              handleImageListData(msg.what, msg.obj, msg.arg1, msg.arg2);
          }
          if(mLocalImageListViewData.size() == 0){
              mNoContent.setVisibility(View.VISIBLE);
          } else {
              mNoContent.setVisibility(View.GONE);
          }
          mOffset = mLocalImageListViewData.size() % IFileManager.PERPAGE_NUMBER;
          mPageIndex = mLocalImageListViewData.size() / IFileManager.PERPAGE_NUMBER;
          onRefresh = false;
      };
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
                    mLocalImageListViewData.clear();
                    mLocalImageListViewData.addAll(localAudioList);
                    mRefreshLayout.finishRefresh();
                    mFileTimeLineGridAdapter.notifyDataSetChanged();
                    break;
                    default:
                        break;
            }
            break;
        case UIHelper.LISTVIEW_ACTION_SCROLL:
            switch (objtype) {
            case UIHelper.LISTVIEW_DATATYPE_GALLERY_IMAGE:
                List<FileDetail> localImageFileList = (List<FileDetail>)obj;
                mLocalImageListViewData.addAll(localImageFileList);
                mFileTimeLineGridAdapter.notifyDataSetChanged();
                mRefreshLayout.finishLoadmore();
                break;
                default:
                    break;
            }
            break;
        }
    }
}
