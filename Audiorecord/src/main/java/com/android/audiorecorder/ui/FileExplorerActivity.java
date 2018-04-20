package com.android.audiorecorder.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.provider.FileProviderService;
import com.android.audiorecorder.ui.pager.FileAudioRecordPager;
import com.android.audiorecorder.ui.pager.FileImagePager;
import com.android.audiorecorder.ui.pager.FileRecordPager;
import com.android.audiorecorder.ui.pager.FileTelRecordPager;
import com.android.audiorecorder.ui.pager.FileVideoPager;
import com.android.library.ui.activity.BaseCommonActivity;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.SelectableViewAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class FileExplorerActivity extends BaseCommonActivity {

    private static final int SUCESS = 1;
    private static final int FAIL = 2;

    private static final int POSITION_MAIN = 0;

    private static Fragment[] pagers = new Fragment[1];

    private ViewPager viewPager;
    private MainAdapter adapter;
    private int mTitle[] = {R.string.main_tab_main};
    private TextView titleTv;// 标题
    private TextView leftTv;
    private CheckedTextView mSeachTextView;
    private CheckedTextView rightTv;
    private TextView mCancelSearchButton;
    private View mSearchView;
    private EditText mSearchEditText;

    private OnFileSearchListener mFileImageListener;
    private OnFileSearchListener mFileVideoListener;
    private OnFileSearchListener mFileAudioListener;
    private OnFileSearchListener mFileTelRecordListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, MultiMediaService.class));
        startService(new Intent(this, FileProviderService.class));
        setContentView(R.layout.activity_file_explorer_viewpager);
        viewPager = (ViewPager) findViewById(R.id.lib_id_viewpager_viewpager);
        // 标题
        titleTv = (TextView) findViewById(R.id.title_tv);
        leftTv = (TextView) findViewById(R.id.leftTv);
        mSeachTextView = (CheckedTextView) findViewById(R.id.searchTv);
        rightTv = (CheckedTextView) findViewById(R.id.rightChooseTv);
        mSearchView = findViewById(R.id.activity_file_search_rl);
        mCancelSearchButton = (TextView) findViewById(R.id.activity_file_search_cancel);
        mSearchEditText = (EditText) findViewById(R.id.dialog_file_search_edittext);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
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
        adapter = new MainAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        titleTv.setText(mTitle[0]);
        setMainButton(0);
    }

    private void setMainButton(int position){
        leftTv.setVisibility(View.GONE);
        mSeachTextView.setOnClickListener(mOnClick);
        rightTv.setOnClickListener(mOnClick);
    }

    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.searchTv){
                checkTopMenu(false);
            } else if(view.getId() == R.id.rightChooseTv) {
                checkTopMenu(true);
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /*super.handleMessage(msg);
            switch (msg.what) {
                case SUCESS:
                    Log.d(TAG, "==> search list length: " + searchElementInfos.size());
                    mRv.setVisibility(View.VISIBLE);
                    mNoFiles.setVisibility(View.GONE);
                    mRv.getRecycledViewPool().clear();
                    mLoadMoreWrapper.setLoadMoreView(0);
                    mLoadMoreWrapper.notifyDataSetChanged();
                    break;
                case FAIL:
                    Log.d(TAG, "==> search list length: " + searchElementInfos.size());
                    mRv.setVisibility(View.INVISIBLE);
                    mNoFiles.setVisibility(View.VISIBLE);
                    break;
                case CHECK_DOWNLOAD:
                    FileBean path = (FileBean) msg.obj;
                    DownloadInfo info = checkDownLoadStatus(path);
                    Log.d(TAG, "download status; " + info + " " + path);
                    if(info != null && info.getStatus() == DownloadInfo.status_downloaded){
                        mIsDownload = false;
                        path.setOrigpath(info.getPath());
                        choiceApp(path);
                        hideWaitingDialog();
                    } else {
                        mHandler.removeMessages(CHECK_DOWNLOAD);
                        Message message = mHandler.obtainMessage(CHECK_DOWNLOAD);
                        message.obj = path;
                        sendMessageDelayed(message, 800);
                    }
                    break;
                case IGNORE_TIP:
                    mIsIgonreTip = true;
                    break;
                default:
                    break;
            }*/
        }
    };
    private void checkTopMenu(boolean hideSearch) {
        if(hideSearch) {
            mSearchView.setVisibility(View.GONE);
            if(mSearchView.isShown()){
                mSearchEditText.setText("");
            }
        } else {
            mSearchView.setVisibility(View.VISIBLE);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
    }

    private void search() {
        String key = mSearchEditText.getText().toString().trim();
        Log.d(TAG, "search key:" + key);
        FileRecordPager recordPager = (FileRecordPager) adapter.getItem(0);
        if(recordPager != null) {
            int index = recordPager.getmSelectIndex();
            if(index == 0) {
                if(mFileImageListener != null) {
                    mFileImageListener.onSearch(key.toLowerCase());
                }
            } else if(index == 1) {
                if(mFileVideoListener != null) {
                    mFileVideoListener.onSearch(key.toLowerCase());
                }
            } else if(index == 2) {
                if(mFileAudioListener != null) {
                    mFileAudioListener.onSearch(key.toLowerCase());
                }
            } else if(index == 3) {
                if(mFileTelRecordListener != null) {
                    mFileTelRecordListener.onSearch(key.toLowerCase());
                }
            }
        }
    }

    public void setOnFileSearchListener(OnFileSearchListener listener) {
        if(listener instanceof FileImagePager) {
            this.mFileImageListener = listener;
        } else  if(listener instanceof FileVideoPager) {
            this.mFileVideoListener = listener;
        } else if(listener instanceof FileAudioRecordPager) {
            this.mFileAudioListener = listener;
        } else if(listener instanceof FileTelRecordPager) {
            this.mFileTelRecordListener = listener;
        }
    }
    public interface OnFileSearchListener {
        /**
         * 根据输入内容动态显示列表
         * @param content
         */
        public void onSearch(String content);
        /**
         * 设置是否是选择模式
         * @param mode
         */
        public void onMode(int mode);
    }

    private static class MainAdapter extends FragmentPagerAdapter implements SelectableViewAdapter, IconPagerAdapter {

        private int[] ICONS = new int[]{
                R.drawable.tab_main_selector,
        };

        public MainAdapter(FragmentManager fm) {
            super(fm);
            pagers[POSITION_MAIN] = new FileRecordPager();
        }

        @Override
        public int getIconResId(int index) {
            return ICONS[index];
        }

        @Override
        public int getCount() {
            return pagers.length;
        }

        @Override
        public Fragment getItem(int position) {
            return pagers[position];
        }

        @Override
        public TabPageIndicator.SelectableView getView(int position) {
            return null;
        }
    }


}
