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
import android.widget.Toast;

import com.android.audiorecorder.R;
import com.android.audiorecorder.engine.MultiMediaService;
import com.android.audiorecorder.provider.FileDetail;
import com.android.audiorecorder.provider.FileProviderService;
import com.android.audiorecorder.ui.pager.FileAudioRecordPager;
import com.android.audiorecorder.ui.pager.FileImagePager;
import com.android.audiorecorder.ui.pager.FileRecordPager;
import com.android.audiorecorder.ui.pager.FileTelRecordPager;
import com.android.audiorecorder.ui.pager.FileVideoPager;
import com.android.library.ui.activity.BaseCommonActivity;
import com.android.library.ui.dialog.CustomDialog;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.SelectableViewAdapter;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

public class FileExplorerActivity extends BaseCommonActivity {


    public static final int INDEX_IMAGE = 0;
    public static final int INDEX_VIDEO = 1;
    public static final int INDEX_AUDIO = 2;
    public static final int INDEX_TELRECORD = 3;

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
    private boolean mChooseMode;

    private OnFileSearchListener mFileImageListener;
    private OnFileSearchListener mFileVideoListener;
    private OnFileSearchListener mFileAudioListener;
    private OnFileSearchListener mFileTelRecordListener;

    private View mFileBottomMenuRelativeLayout;
    private CustomDialog deleteFileDialog;

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
        mCancelSearchButton.setOnClickListener(mOnClick);
        mSearchEditText = (EditText) findViewById(R.id.dialog_file_search_edittext);
        mFileBottomMenuRelativeLayout = findViewById(R.id.activity_file_bottom_rl);
        mFileBottomMenuRelativeLayout.findViewById(R.id.activity_file_copy_iv).setOnClickListener(mOnClick);
        mFileBottomMenuRelativeLayout.findViewById(R.id.activity_file_detail_iv).setOnClickListener(mOnClick);
        mFileBottomMenuRelativeLayout.findViewById(R.id.activity_file_delete_iv).setOnClickListener(mOnClick);
        mFileBottomMenuRelativeLayout.findViewById(R.id.activity_file_rename_iv).setOnClickListener(mOnClick);
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
        FileRecordPager recordPager = (FileRecordPager) adapter.getItem(0);
        recordPager.setOnPageListener(mPageListener);
        titleTv.setText(mTitle[0]);
        setMainButton(0);
        mChooseMode = false;
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

    /**
     * 选择的文件个数变化
     * @param count
     */
    public void onFileChecked(OnFileSearchListener listener, int count) {
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
        public void onMode(boolean mode);

        /**
         * 移除选中的文件
         */
        public void deleteFile(List<FileDetail> fileList);

        /**
         * 文件重命名
         * @param detail
         * @param newName
         */
        public void renameFile(FileDetail detail, String newName);

        /**
         * 获取选中的文件
         * @return
         */
        public List<FileDetail> getCheckFileDetail();
    }

    public interface OnPageListener {
        void onPageChange(int oldPage, int newPage);
    }

    private void showDeleteFileDialog() {
        View createFile = View.inflate(this, R.layout.dialog_file_delete, null);
        TextView confirm_createFile = (TextView) createFile.findViewById(R.id.dialog_create_file_confirm);
        TextView cancel_createFile = (TextView) createFile.findViewById(R.id.dialog_create_file_cancel);
        //deleteFileDialog = new CustomDialog(this, 0, 0, createFile, R.style.settings_style);
        deleteFileDialog.setCanceledOnTouchOutside(true);
        confirm_createFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFileDialog.dismiss();
                deleteFiles();
            }
        });
        cancel_createFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteFileDialog != null) {
                    deleteFileDialog.dismiss();
                }
            }
        });
        deleteFileDialog.show();
    }

    private void deleteFiles() {
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
                rightTv.setVisibility(View.INVISIBLE);
                hideSearchMode(false);
                if(mChooseMode) {
                    setChoolseMode(!mChooseMode);
                }
            } else if(view.getId() == R.id.activity_file_search_cancel) {
                rightTv.setVisibility(View.VISIBLE);
                hideSearchMode(true);
            } else if(view.getId() == R.id.rightChooseTv) {
                hideSearchMode(true);
                setChoolseMode(!mChooseMode);
            } else if(view.getId() == R.id.activity_file_copy_iv) {
            } else if(view.getId() == R.id.activity_file_detail_iv) {

            } else if(view.getId() == R.id.activity_file_delete_iv) {

            } else if(view.getId() == R.id.activity_file_rename_iv) {

            }
        }
    };

    private OnPageListener mPageListener = new OnPageListener() {
        @Override
        public void onPageChange(int oldPage, int newPage) {
            if(mSearchView.isShown()) {
                hideSearchMode(true);
            }
            if(mChooseMode) {
                setChoolseMode(!mChooseMode);
            }
        }
    };

    private void hideSearchMode(boolean hideSearch) {
        if(hideSearch) {
            mSearchView.setVisibility(View.GONE);
            mSearchEditText.setText("");
        } else {
            mSearchView.setVisibility(View.VISIBLE);
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
    }

    private void setChoolseMode(boolean mode) {
        this.mChooseMode = mode;
        mFileBottomMenuRelativeLayout.setVisibility(mChooseMode ? View.VISIBLE : View.GONE);
        FileRecordPager recordPager = (FileRecordPager) adapter.getItem(0);
        if(recordPager != null) {
            int index = recordPager.getmSelectIndex();
            if(index == INDEX_IMAGE) {
                if(mFileImageListener != null) {
                    mFileImageListener.onMode(mChooseMode);
                }
            } else if(index == INDEX_VIDEO) {
                if(mFileVideoListener != null) {
                    mFileVideoListener.onMode(mChooseMode);
                }
            } else if(index == INDEX_AUDIO) {
                if(mFileAudioListener != null) {
                    mFileAudioListener.onMode(mChooseMode);
                }
            } else if(index == INDEX_TELRECORD) {
                if(mFileTelRecordListener != null) {
                    mFileTelRecordListener.onMode(mChooseMode);
                }
            }
        }
    }

    private void search() {
        String key = mSearchEditText.getText().toString().trim();
        Log.d(TAG, "search key:" + key);
        FileRecordPager recordPager = (FileRecordPager) adapter.getItem(0);
        if(recordPager != null) {
            int index = recordPager.getmSelectIndex();
            if(index == INDEX_IMAGE) {
                if(mFileImageListener != null) {
                    mFileImageListener.onSearch(key.toLowerCase());
                }
            } else if(index == INDEX_VIDEO) {
                if(mFileVideoListener != null) {
                    mFileVideoListener.onSearch(key.toLowerCase());
                }
            } else if(index == INDEX_AUDIO) {
                if(mFileAudioListener != null) {
                    mFileAudioListener.onSearch(key.toLowerCase());
                }
            } else if(index == INDEX_TELRECORD) {
                if(mFileTelRecordListener != null) {
                    mFileTelRecordListener.onSearch(key.toLowerCase());
                }
            }
        }
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
