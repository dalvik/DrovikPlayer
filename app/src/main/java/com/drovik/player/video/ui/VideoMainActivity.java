package com.drovik.player.video.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.drovik.R;
import com.android.drovik.video.VideoBean;
import com.android.drovik.video.ui.adapter.VideoListAdpater;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.view.HFLoadMoreWrapper;
import com.android.library.ui.view.HFRecyclerView;
import com.android.library.ui.view.HFWrapper;
import com.android.library.utils.NetWorkUtils;
import com.android.library.utils.PreferenceUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

/**
 * Created by 23536 on 2017-11-13.
 */
public class VideoMainActivity extends BaseCompatActivity implements View.OnClickListener {

    public static final String MOVIE = "/lv/MultiMedia/Movie";

    public final  static int FILE_OPT_COPY = 1;
    public final  static int FILE_OPT_MOVE = 2;

    public static final String FILE_OPT_MODE = "file_opt_mode";
    public static final String FILE_OPT_DATA = "file_opt_data";

    public static final String UPLOAD_PATH = "upload_path";
    private final  static int MODE_NORMAL = 0;
    private final  static int MODE_CHOICE = 1;

    private static final int SUCESS = 1;
    private static final int FAIL = 2;

    private final String ROOT = "/lv";
    private final String MULTIMEDIA = "/lv/MultiMedia";
    private final String SURVEILLANCE = "Surveillance";
    private final String PHOTO = "Photo";

    private ArrayList<VideoBean> mFileNames;
    private ArrayList<VideoBean> mSelectFileNames = new ArrayList<VideoBean>();
    private ArrayList<VideoBean> searchElementInfos = new ArrayList<VideoBean>();

    private HFRecyclerView mRv;
    private VideoListAdpater mVideoListAdapter;
    private HFWrapper mHFWrapper;
    private HFLoadMoreWrapper mLoadMoreWrapper;
    private TextView mNoFiles;
    private boolean mIsChooseMode; //是否是选择模式  edit select
    private boolean fileNextShow = true;

    private View mFileSearchRelativeLayout;
    private ImageView mStartSearchImageView;
    private TextView mCancelSearchTextView;
    private View mVideoAllFileRelativeLayout;
    private EditText mFileSearchEditText;
    private RefreshLayout mRefreshLayout;
    private boolean mOnRefresh;

    private boolean firstSort = false;

    public static final String TAG = "FileMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_main);
        setTitle(R.string.video_title);
        initView();
        initData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_file_search:
                setSearchMode();
                break;
            case R.id.activity_file_search_cancel:
                cancelSearchMode();
                break;
            case R.id.fragment_file_tv_nothing:
                onRefreshFileList("");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            VideoMainActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        mFileNames = new ArrayList<VideoBean>();
        mRefreshLayout = (RefreshLayout) findViewById(R.id.file_list_rl);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                LogUtil.i(TAG, "onRefresh.");
                initData();
                refreshlayout.finishRefresh(1000);
            }
        });
        mRv = (HFRecyclerView) findViewById(R.id.fragment_file_listview);
        mRv.setHasFixedSize(true);
        mVideoListAdapter = new VideoListAdpater(this, searchElementInfos);
        mRv.setAdapter(mVideoListAdapter);
        updateRecycleViewType(VideoListAdpater.LAYOUT_TYPE_GRID);
        mHFWrapper = new HFWrapper(mVideoListAdapter);
        mLoadMoreWrapper = new HFLoadMoreWrapper(mHFWrapper);
        mLoadMoreWrapper.setLoadMoreView(R.layout.footer);
        mLoadMoreWrapper.setOnLoadMoreListener(new HFLoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
            LogUtil.i(TAG, "onLoadMoreRequested.");
            }
        });
        mRv.setAdapter(mLoadMoreWrapper);
        mVideoListAdapter.setOnItemClickListener(new VideoListAdpater.OnItemClickListener() {
            @Override
            public void onItemClick(View view, HFRecyclerView.ViewHolder holder, int position) {
                if (mIsChooseMode) {
                    if (mFileNames.get(position).isSelected) {
                        mFileNames.get(position).setSelected(false);
                        mSelectFileNames.remove(mFileNames.get(position));
                    } else {
                        mFileNames.get(position).setSelected(true);
                        mSelectFileNames.add(mFileNames.get(position));
                    }
                    mRv.getRecycledViewPool().clear();
                    mLoadMoreWrapper.setLoadMoreView(0);
                    mLoadMoreWrapper.notifyDataSetChanged();
                } else {
                    if (PreferenceUtils.getWifiOpen(PreferenceUtils.WIFI) && !NetWorkUtils.getNetWorkState(VideoMainActivity.this).equals("WIFI")) {
                        //Toast.makeText(VideoMainActivity.this, getResources().getString(R.string.wifi_tips), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(mFileSearchRelativeLayout.getVisibility() == View.VISIBLE) {
                        cancelSearchMode();
                    }
                    Intent intent = new Intent(VideoMainActivity.this, VideoPlayActivity.class);
                    intent.putExtra(VideoPlayActivity.VIDEO, mFileNames.get(position));
                    startActivity(intent);
                    //String path = Utils.getPrePath() + mFileNames.get(position).origpath;
                    //startActivity(OpenFiles.getVideoUrlIntent(path));
                }
            }

            @Override
            public boolean onItemLongClick(View view, HFRecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mNoFiles = (TextView) findViewById(R.id.fragment_file_tv_nothing);
        mNoFiles.setOnClickListener(this);

        mFileSearchRelativeLayout = findViewById(R.id.activity_file_search_rl);
        mVideoAllFileRelativeLayout = findViewById(R.id.activity_all_file_rl);
        mStartSearchImageView = (ImageView) findViewById(R.id.activity_file_search);
        mStartSearchImageView.setOnClickListener(this);
        mFileSearchEditText = (EditText) findViewById(R.id.dialog_file_search_edittext);
        mFileSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCancelSearchTextView = (TextView) findViewById(R.id.activity_file_search_cancel);
        mCancelSearchTextView.setOnClickListener(this);
    }

    private void initData() {
        mOnRefresh = true;
        new LoadDataAsyncTask().execute();
    }

    private void onRefreshFileList(String path) {
        Log.d(TAG, "==> onRefreshFileList: " + path);
        //加载显示数据
        initData();
    }
    private void search() {
        String key = mFileSearchEditText.getText().toString().trim();
        Log.d(TAG, "search key:" + key);
        if (!TextUtils.isEmpty(key)) {
            search(key);
        } else {
            searchElementInfos.clear();
            searchElementInfos.addAll(mFileNames);
            Message msg = mHandler.obtainMessage();
            msg.what = SUCESS;
            msg.sendToTarget();
        }
    }

    /**
     * 根据Key 搜索 歌曲
     *
     * @param key 关键字
     */
    private void search(final String key) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchElementInfos.clear();
                for (int i = 0; i < mFileNames.size(); i++) {
                    if (mFileNames.get(i).getTitle().toLowerCase().contains(key.toLowerCase())) {
                        searchElementInfos.add(mFileNames.get(i));
                    }
                }
                if (searchElementInfos.size() > 0) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = SUCESS;
                    msg.sendToTarget();
                } else {
                    Message msg = mHandler.obtainMessage();
                    msg.what = FAIL;
                    msg.sendToTarget();
                }
            }
        }).start();
    }

    private void changeChooseMode(boolean mode) {
        mIsChooseMode = mode;
        mVideoListAdapter.setMode(mIsChooseMode);
        mStartSearchImageView.setEnabled(!mIsChooseMode);
        if(!mIsChooseMode) {
            checkedAll(mode);
        }
    }

    private void invilateView() {
        mLoadMoreWrapper.setLoadMoreView(0);
        mLoadMoreWrapper.notifyDataSetChanged();
    }

    private void setRecycleViewMode() {
        mIsChooseMode = false;
    }

    private void setSearchMode() {
        mFileSearchRelativeLayout.setVisibility(View.VISIBLE);
        mVideoAllFileRelativeLayout.setVisibility(View.GONE);
        updateRecycleViewType(VideoListAdpater.LAYOUT_TYPE_LIST);
    }

    private void cancelSearchMode() {
        mFileSearchRelativeLayout.setVisibility(View.GONE);
        mFileSearchEditText.setText("");
        mVideoAllFileRelativeLayout.setVisibility(View.VISIBLE);
        updateRecycleViewType(VideoListAdpater.LAYOUT_TYPE_GRID);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFileSearchEditText.getWindowToken(), 0);
    }

    private void updateRecycleViewType(int layoutType) {
        mVideoListAdapter.setLayoutType(layoutType);
        if(layoutType == VideoListAdpater.LAYOUT_TYPE_GRID) {
            mRv.setColumn(2);
            mRv.setOrientation(HFRecyclerView.ORIENTATION_VERTICAL);
            mRv.setType(HFRecyclerView.TYPE_GRID);
        } else {
            mRv.setColumn(1);
            mRv.setOrientation(HFRecyclerView.ORIENTATION_VERTICAL);
            mRv.setType(HFRecyclerView.TYPE_STAGGER);
        }
        mRv.notifyViewChanged();
    }

    private boolean isCheckedAll() {
        int length = mFileNames.size();
        boolean isCheckedAll = true;
        for(int i=0; i<length; i++) {
            VideoBean fileBean = mFileNames.get(i);
            if(!fileBean.isSelected()){
                isCheckedAll = false;
                return isCheckedAll;
            }
        }
        Log.d(TAG, "==> isCheckedAll: " + isCheckedAll);
        return isCheckedAll;
    }

    private void checkedAll(boolean isCheckedAll) {
        int length = mFileNames.size();
        mSelectFileNames.clear();
        for(int i=0; i<length; i++) {
            VideoBean fileBean = mFileNames.get(i);
            fileBean.setSelected(isCheckedAll);
            if(isCheckedAll){
                mSelectFileNames.add(fileBean);
            }
        }
        if(!isCheckedAll){
            mSelectFileNames.clear();
        }
        invilateView();
    }


    private VideoBean getSelectElementInfo() {
        if(mSelectFileNames.size() >0 ){
            return mSelectFileNames.get(0);
        }
        return null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCESS:
                    mRv.setVisibility(View.VISIBLE);
                    mNoFiles.setVisibility(View.GONE);
                    mRv.getRecycledViewPool().clear();
                    mLoadMoreWrapper.setLoadMoreView(0);
                    mLoadMoreWrapper.notifyDataSetChanged();
                    break;
                case FAIL:
                    mRv.setVisibility(View.INVISIBLE);
                    mNoFiles.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {

        ArrayList<VideoBean> requestVideoBean = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRv.setVisibility(View.VISIBLE);
            mNoFiles.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mFileNames.clear();
            if(requestVideoBean != null && requestVideoBean.size()>0) {
                mFileNames.addAll(requestVideoBean);
            }
            if(mOnRefresh) {
                mOnRefresh = false;
                searchElementInfos.clear();
            }
            searchElementInfos.addAll(mFileNames);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideWaitingDialog();
            mRv.getRecycledViewPool().clear();
            mLoadMoreWrapper.setLoadMoreView(0);
            mLoadMoreWrapper.notifyDataSetChanged();
        }
    }
}
