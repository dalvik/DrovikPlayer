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
import com.android.audiorecorder.ui.adapter.FileTimeLineGridAdapter;
import com.android.audiorecorder.utils.FileUtils;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.UIHelper;
import com.android.library.ui.pager.BasePager;
import com.android.library.ui.utils.ToastUtils;
import com.android.library.utils.OpenFiles;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAudioRecordPager extends BasePager {

    public final static String EXTRA_THUMB_NAME = "thumb_name";

    private GridView mLocalImageGridView;
    private FileTimeLineGridAdapter mStickyGridAdapter;
    private RelativeLayout mNoContent;
    private RefreshLayout mRefreshLayout;
    private ClassicsHeader refreshLayoutHeader;
    private boolean onRefresh;
    private boolean isLoadMore = false;
    public List<FileDetail> mLocalListViewData;
    private IFileManager mFileManager;
    private int mOffset;
    private int mPageIndex;
    private int mMode = MultiMediaService.LUNCH_MODE_MANLY;

    private String TAG = "FileAudioRecordPager";

    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_file_audio_record_timeline_gridview, null);
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
        mFileManager = FileManagerFactory.getFileManagerInstance(getActivity());
        mStickyGridAdapter = new FileTimeLineGridAdapter(getActivity(), mLocalListViewData, mLocalImageGridView, null);
        mLocalImageGridView.setAdapter(mStickyGridAdapter);
        mOffset = 0;
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onRefresh = true;
                mOffset = 0;
                mPageIndex = 0;
                loadThumbByCatalog(AppContext.CATALOG_LOCAL_AUDIO, mPageIndex, mHandler, UIHelper.LISTVIEW_ACTION_REFRESH, UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
            }
        });

        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                isLoadMore = true;
                loadThumbByCatalog(AppContext.CATALOG_LOCAL_AUDIO, mPageIndex, mHandler, UIHelper.LISTVIEW_ACTION_SCROLL, UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
            }
        });
        mLocalImageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileDetail info = mLocalListViewData.get(position);
                File file = new File(info.getFilePath());
                if(file.exists()){
                    Intent intent = OpenFiles.getVideoFileIntent(getActivity(), file);
                    startActivity(intent);
                }else {
                    ToastUtils.showToast(R.string.file_not_exist);
                }
            }
        });
        loadThumbByCatalog(AppContext.CATALOG_LOCAL_AUDIO, 0, mHandler, UIHelper.LISTVIEW_ACTION_INIT, UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.d(TAG, "==>onActivityCreated.");
    }

    @Override
    public void reload() {
        LogUtil.d(TAG, "==>reload.");
        loadThumbByCatalog(AppContext.CATALOG_LOCAL_AUDIO, 0, mHandler, UIHelper.LISTVIEW_ACTION_INIT, UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO);
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

    private void loadThumbByCatalog(final int catalog, final int pageIndex, final Handler handler, final int action, final int objType){
        new Thread(){
            public void run() {
                Message msg = new Message();
                try {
                    LogUtil.d(TAG, "==> loadThumbByCatalog catalog: " + catalog + " " + pageIndex + " " + mOffset + " " + FileUtils.getLaunchModeSet(mMode));
                    List<FileDetail> localAlumbListTmp = mFileManager.loadFileList(true, catalog, "", pageIndex, mOffset, FileUtils.getLaunchModeSet(mMode));
                    LogUtil.d(TAG, "==> catalog size: " + localAlumbListTmp.size());
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
                    case UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO:
                        List<FileDetail> localAudioList = (List<FileDetail>)obj;
                        mLocalListViewData.clear();
                        mLocalListViewData.addAll(localAudioList);
                        mRefreshLayout.finishRefresh();
                        mStickyGridAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
                break;
            case UIHelper.LISTVIEW_ACTION_SCROLL:
                switch (objtype) {
                    case UIHelper.LISTVIEW_DATATYPE_LOCAL_AUDIO:
                        List<FileDetail> localImageFileList = (List<FileDetail>)obj;
                        mLocalListViewData.addAll(localImageFileList);
                        mStickyGridAdapter.notifyDataSetChanged();
                        mRefreshLayout.finishLoadmore();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}
