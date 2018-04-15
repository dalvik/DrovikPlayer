package com.drovik.player.audio.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.drovik.AppApplication;
import com.android.drovik.R;
import com.android.drovik.audio.MusicBean;
import com.android.drovik.audio.mediaplayer.MusicController;
import com.android.drovik.audio.mediaplayer.PlayerEngine;
import com.android.drovik.audio.mediaplayer.PlayerService;
import com.android.drovik.audio.mediaplayer.Playlist;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.dialog.CustomDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

//import android.support.transition.AutoTransition;
//import android.support.transition.TransitionManager;
//import android.support.transition.TransitionSet;

public class MusicActivity extends BaseCompatActivity implements IMusicView {

    private ListView mListView;
    private MusicController mMusciController;
    private MusicAdapter mMusicAdapter;
    private ArrayList<MusicBean> mMusiclist;
    private ArrayList<MusicBean> mSelectMusics = new ArrayList<MusicBean>();

    private ImageView mPlayerOrPause;
    private TextView mMusicCount;
    private TextView mSong;
    private TextView mSingerAndAlbum;
    private ImageView mBottomPic;
    private Playlist allSongsPlaylist;
    private boolean editMode = false;
    private RelativeLayout mBottomWidget;
    private LinearLayout mEditBottom;
    private ImageView mPlayAll;
    private boolean selectAll = false;
    private ImageView mDownload;
    private ImageView mDelete;
    private RefreshLayout refreshLayout;
    private RelativeLayout mSearchContainer;
    private ImageView mSearchView;
    //    private TransitionSet mSet;
    private EditText mSearchEditText;
    private boolean onRefresh;
    private boolean isLoadMore = false;     // 用于判断当前加载是否是加载更多
    private RelativeLayout mNoContent;
    private View mSpliteLine;
    private ClassicsHeader refreshLayoutHeader;
    private ImageView mClear;
    private boolean mSearchMode = false;

    private CustomDialog m4GPlayConfirm;
    ConnectivityManager manager;

    private String TAG = "MusicActivity";

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        setTitle(R.string.home_music);
        mMusciController = new MusicController(MusicActivity.this);
        initView();
        initData();
        showEditMode(editMode);
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayerService.ACTION_CURRENT_PLAYING_MUSIC_CHANGED);
        filter.addAction(PlayerService.ACTION_CURRENT_PLAYING_MUSIC_PLAYING);
        filter.addAction(PlayerService.ACTION_CURRENT_PLAYING_MUSIC_PAUSE);
        filter.addAction(PlayerService.ACTION_CURRENT_PLAYING_MUSIC_PREPARE_COMPLETE);
        filter.addAction(PlayerService.ACTION_CURRENT_PLAYING_MUSIC_ERROR);
        registerReceiver(musicChangedReceiver, filter);
        showMusicInfo(getRemotePlayerEngine().getCurrentSelectedMusic());

        IntentFilter disconnectFilter = new IntentFilter();
        //disconnectFilter.addAction(LoginManager.OFFLINE_DEVICE);
        registerReceiver(mDeviceDisconnect, disconnectFilter);

        IntentFilter netChangedFilter = new IntentFilter();
        netChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetChanged, netChangedFilter);

    }

    private void initView() {
        mNoContent = (RelativeLayout) findViewById(R.id.activity_music_nocontent);
        mPlayAll = (ImageView) findViewById(R.id.activity_music_playall);
        mPlayAll.setOnClickListener(mMusciController);
        mMusicCount = (TextView) findViewById(R.id.activity_music_playall_tv_count);
        mSong = (TextView) findViewById(R.id.activity_music_bottom_singer);
        mSingerAndAlbum = (TextView) findViewById(R.id.activity_music_bottom_album);
        mBottomPic = (ImageView) findViewById(R.id.activity_music_bottom_widget_iv);
        mBottomWidget = (RelativeLayout) findViewById(R.id.activity_music_bottom_widget);
        mEditBottom = (LinearLayout) findViewById(R.id.activity_music_bottom);

        mListView = (ListView) findViewById(R.id.activity_music_listview);
        mListView.setEmptyView(mNoContent);
        mNoContent.setVisibility(View.INVISIBLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                LogUtil.d(TAG, "onItemClick position:" + position);
                ArrayList<MusicBean> mMusiclist = mMusicAdapter.getData();
                if (editMode) {
                    if (mMusiclist.get(position).getSelect()) {
                        mMusiclist.get(position).setSelect(false);
                        mSelectMusics.remove(mMusiclist.get(position));
                    } else {
                        mMusiclist.get(position).setSelect(true);
                        mSelectMusics.add(mMusiclist.get(position));
                    }
                    if (mSelectMusics.size() > 0) {
                        mDelete.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_delete_selector));
                        mDownload.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_download_selector));
                    } else {
                        mDelete.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_delete_d));
                        mDownload.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_download_d));
                    }
                    mMusicAdapter.setData(mMusiclist);
                } else {
                    showPlayConfirm(new Runnable() {
                        @Override
                        public void run() {
                            playMusic(position);
                            if (isIn4G()) {
                                AppApplication.isAllowIn4G = true;
                            }
                        }
                    });
                }

            }
        });
        findViewById(R.id.activity_music_bottom_widget).setOnClickListener(mMusciController);
        mPlayerOrPause = (ImageView) findViewById(R.id.activity_music_bottom_widget_pause);
        mPlayerOrPause.setOnClickListener(mMusciController);
        showPlayStatus(AppApplication.getInstance().getPlayerEngineInterface().isPlaying());
        findViewById(R.id.activity_music_bottom_widget_next).setOnClickListener(mMusciController);
        mDownload = (ImageView) findViewById(R.id.activity_photo_download);
        mDelete = (ImageView) findViewById(R.id.activity_photo_delete);
        mDownload.setOnClickListener(mMusciController);
        mDelete.setOnClickListener(mMusciController);
        refreshLayoutHeader = (ClassicsHeader) findViewById(R.id.music_refreshLayout_header);
        refreshLayout = (RefreshLayout) findViewById(R.id.music_refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onRefresh = true;
                mMusciController.getMusicData();
                refreshlayout.finishRefresh(1000);
            }
        });

        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                isLoadMore = true;
                mMusciController.getMusicMoreData(mMusiclist.size());
                refreshlayout.finishLoadmore(1000);
            }
        });

        mSearchView = (ImageView) findViewById(R.id.activity_music_search);
        mSearchView.setOnClickListener(mMusciController);

        mSearchContainer = (RelativeLayout) findViewById(R.id.activity_music_search_container);
        mSearchEditText = (EditText) findViewById(R.id.activity_music_search_edittext);
        mClear = (ImageView) findViewById(R.id.clear);
        mClear.setOnClickListener(mMusciController);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = mSearchEditText.getText().toString().trim();
                if (password != null && password.length() > 0) {
                    mClear.setVisibility(View.VISIBLE);
                    ArrayList<MusicBean> musicBeens = (ArrayList<MusicBean>) getRemotePlayerEngine().getPlaylist().getMusicBeanList();
                    ArrayList<MusicBean> searchMusicBean = new ArrayList<MusicBean>();
                    for (int i = 0; i < musicBeens.size(); i++) {
                        MusicBean bean = musicBeens.get(i);
                        if (bean.singer.contains(password) || bean.album.contains(password) || bean.getTitle().contains(password)) {
                            searchMusicBean.add(bean);
                        }
                    }
//                    mMusiclist = searchMusicBean;
                    mMusicAdapter.setData(searchMusicBean);
                } else {
                    mClear.setVisibility(View.INVISIBLE);
                    if (mMusicAdapter != null) {
                        ArrayList<MusicBean> musicBeens = mMusiclist;
//                        mMusiclist = musicBeens;
                        mMusicAdapter.setData(musicBeens);
                    }
                }



            }
        });
        mSpliteLine = findViewById(R.id.activity_music_splite);
        findViewById(R.id.activity_music_search_cancel).setOnClickListener(mMusciController);
        reduce();
    }

    private void initData() {
        mMusciController.getMusicData();
    }

    public void showNoContent(boolean isShow) {
        LogUtil.d(TAG, "showNoContent isShow:" + isShow);
        /*if (isShow) {
            mNoContent.setVisibility(View.VISIBLE);
            mPlayAll.setVisibility(View.GONE);
            mSearchContainer.setVisibility(View.INVISIBLE);
            mSpliteLine.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
            mBottomWidget.setVisibility(View.INVISIBLE);
            refreshLayoutHeader.setVisibility(View.INVISIBLE);
            mNoContent.setVisibility(View.VISIBLE);
        } else {
            mNoContent.setVisibility(View.INVISIBLE);
            mPlayAll.setVisibility(View.VISIBLE);
            mSearchContainer.setVisibility(View.VISIBLE);
            mSpliteLine.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);
            mBottomWidget.setVisibility(View.VISIBLE);
            refreshLayoutHeader.setVisibility(View.VISIBLE);
        }*/
    }

    public void showMusicInfo(MusicBean musicBean) {
        if (musicBean != null) {
            LogUtil.d(TAG, "showMusicInfo musicBean:" + musicBean.origpath + "--playing:" + getRemotePlayerEngine().isPlaying());
            //Glide.with(getApplicationContext()).load("http://" + LoginManager.getInstance().baseIp + ":" + LoginManager.getInstance().getP2PPort() + "/" + musicBean.thumbpath).asBitmap().centerCrop().placeholder(R.drawable.music_body_initialize_n).diskCacheStrategy(DiskCacheStrategy.ALL).into(mBottomPic);
            if (musicBean.singer.isEmpty()) {
                musicBean.singer = getApplicationContext().getResources().getString(R.string.unknow);
            }
            if (musicBean.title.isEmpty()) {
                musicBean.title = getApplicationContext().getResources().getString(R.string.unknow);
            }
            if (musicBean.album.isEmpty()) {
                musicBean.album = getApplicationContext().getResources().getString(R.string.unknow);
            }
            mSong.setText(musicBean.getTitle());
            mSingerAndAlbum.setText(getApplicationContext().getString(R.string.singer_album, musicBean.singer, musicBean.album));
//            try {

//                getRemotePlayerEngine().getCurrentMediaplayer().setDataSource(Uri.encode(musicBean.getOrigpath(), "-![.:/,%?&=]"));
//                getRemotePlayerEngine().getCurrentMediaplayer().prepareAsync();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void expand() {
        //设置伸展状态时的布局
        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mSearchContainer.getLayoutParams();
        LayoutParams.width = LayoutParams.MATCH_PARENT;
        LayoutParams.setMargins(0, 0, 0, 0);
        mSearchContainer.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(mSearchContainer);
        mSearchEditText.setFocusable(true);
        mSearchEditText.setFocusableInTouchMode(true);
        mSearchEditText.requestFocus();
        showInputMethod();
    }

    private void reduce() {
        //设置收缩状态时的布局
        RelativeLayout.LayoutParams LayoutParams = (RelativeLayout.LayoutParams) mSearchContainer.getLayoutParams();
        LayoutParams.width = LayoutParams.MATCH_PARENT;
        WindowManager windowManager = activity.getWindowManager();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixels = displayMetrics.widthPixels;
        LayoutParams.setMargins(widthPixels, 0, 0, 0);
        mSearchContainer.setLayoutParams(LayoutParams);
        //开始动画
        beginDelayedTransition(mSearchContainer);
        mSearchEditText.setFocusable(false);
        mSearchEditText.setText(null);
        hideSoftKeyboard();
    }

    void beginDelayedTransition(ViewGroup view) {
//        mSet = new AutoTransition();
//        mSet.setDuration(500);
//        TransitionManager.beginDelayedTransition(view, mSet);
    }

    public void playMusic(int originPosition) {
        ArrayList<MusicBean> mMusiclist = mMusicAdapter.getData();
        Playlist currentPlaylist = getRemotePlayerEngine().getPlaylist();
        allSongsPlaylist = new Playlist();
        allSongsPlaylist.setMusicBeanList(mMusiclist);
        if (currentPlaylist == null) {
            getRemotePlayerEngine().openPlaylist(allSongsPlaylist);
        } else {
            if (currentPlaylist != allSongsPlaylist) {
                getRemotePlayerEngine().openPlaylist(allSongsPlaylist);
            }
        }
        getRemotePlayerEngine().getPlaylist().select(originPosition);
        getRemotePlayerEngine().play();
    }


    private PlayerEngine getRemotePlayerEngine() {
        return AppApplication.getInstance().getPlayerEngineInterface();
    }

    private void showEditMode(boolean mode) {
        if (mode) {
            mBottomWidget.setVisibility(View.GONE);
            mEditBottom.setVisibility(View.VISIBLE);
            mPlayAll.setVisibility(View.GONE);
            mListView.setPadding(0, 0, 0, mEditBottom.getHeight());
            refreshLayout.setEnableRefresh(false);
            refreshLayout.setEnableLoadmore(false);
            refreshLayout.setEnableAutoLoadmore(false);
        } else {
            mBottomWidget.setVisibility(View.VISIBLE);
            mEditBottom.setVisibility(View.GONE);
            mPlayAll.setVisibility(View.VISIBLE);
            refreshLayout.setEnableAutoLoadmore(true);
            refreshLayout.setEnableLoadmore(true);
            refreshLayout.setEnableRefresh(true);
            mListView.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public void startLoad() {
        if (!onRefresh) {
            showWaitingDialog();
        }
    }

    @Override
    public void successLoad(ArrayList<MusicBean> list) {
        LogUtil.d(TAG, "successLoad size: " + list.size());
        mMusiclist = list;
        hideWaitingDialog();

        if (mMusiclist != null && mMusiclist.size() > 0) {
            Playlist currentPlaylist = getRemotePlayerEngine().getPlaylist();
            allSongsPlaylist = new Playlist();
            allSongsPlaylist.setMusicBeanList(mMusiclist);
            for (int i = 0; i < mMusiclist.size(); i++) {
                Log.d(TAG, "indexL: " + i + " origPath: " + mMusiclist.get(i).origpath + " singer: " + mMusiclist.get(i).singer + " album: " + mMusiclist.get(i).album);
                if (getRemotePlayerEngine().getCurrentSelectedMusic() != null) {
                    if (mMusiclist.get(i).origpath.equals(getRemotePlayerEngine().getCurrentSelectedMusic().origpath)) {
                        LogUtil.d(TAG, "select index: " + i);
                        mMusiclist.get(i).isSelect = true;
                        allSongsPlaylist.select(i);
                    } else {
                        mMusiclist.get(i).isSelect = false;
                    }
                }
            }

            MusicBean selectedMusic = getRemotePlayerEngine().getCurrentSelectedMusic();
            allSongsPlaylist.selectMusic = selectedMusic;
            allSongsPlaylist.updateSelectedIndex(mMusiclist);

            if (currentPlaylist == null) {
                getRemotePlayerEngine().openPlaylist(allSongsPlaylist);
            } else {
                if (currentPlaylist != allSongsPlaylist) {
                    getRemotePlayerEngine().openPlaylist(allSongsPlaylist);
                }
            }

            //getRemotePlayerEngine().setPlaybackMode(Playlist.PlaylistPlaybackMode.getPlaybackModeByOrdinal(PrefUtils.getPlaybackMode()));
            if (getRemotePlayerEngine().getCurrentSelectedMusic() == null) {
                getRemotePlayerEngine().getPlaylist().select(0);
            }
        }
        if (list != null) {
            mMusicCount.setText(getApplicationContext().getString(R.string.music_count, list.size()));
            MusicBean musicBean = list.get(0);
            if (getRemotePlayerEngine().getCurrentSelectedMusic() != null) {
                musicBean = getRemotePlayerEngine().getCurrentSelectedMusic();
            }
            showMusicInfo(musicBean);
        }
//        mMusicAdapter = new MusicAdapter(MusicActivity.this, mMusiclist);
//        mListView.setAdapter(mMusicAdapter);

        onRefresh = false;
        if (mMusiclist == null || mMusiclist.size() == 0) {
            showNoContent(true);
        } else if (mMusiclist != null && mMusiclist.size() > 0) {
            showNoContent(false);
        }
        if (getRemotePlayerEngine().getCurrentMediaplayer() == null || !getRemotePlayerEngine().getCurrentMediaplayer().isPlaying()) {
            getRemotePlayerEngine().build();
        }
        mMusicAdapter = new MusicAdapter(MusicActivity.this, mMusiclist);
        mListView.setAdapter(mMusicAdapter);
    }

    @Override
    public void successLoadMore(ArrayList<MusicBean> list) {
        isLoadMore = false;
        if (list != null && list.size() > 0) {
            Log.d(TAG, "successLoadMore list size: " + list.size());
            mMusiclist.addAll(list);
            //更新选中状态
            for (int i = 0; i < mMusiclist.size(); i++) {
                if (getRemotePlayerEngine().getCurrentSelectedMusic() != null) {
                    if (mMusiclist.get(i).origpath.equals(getRemotePlayerEngine().getCurrentSelectedMusic().origpath)) {
                        mMusiclist.get(i).isSelect = true;
                    } else {
                        mMusiclist.get(i).isSelect = false;
                    }
                }
            }
            mMusicAdapter.setData(mMusiclist);
            mMusicCount.setText(getApplicationContext().getString(R.string.music_count, mMusiclist.size()));
            allSongsPlaylist.setMusicBeanList(mMusiclist);
        } else {
            Log.d(TAG, "list == null || list size == 0");
        }
    }

    @Override
    public void failLoad() {
        hideWaitingDialog();
        onRefresh = false;
        if (!isLoadMore) {
            showNoContent(true);
        }
        isLoadMore = false;
    }

    @Override
    public void finshActivity() {
        finish();
    }

    @Override
    public void startMusicPlay() {
        Intent intent = new Intent(MusicActivity.this, MusicPlayActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPlayStatus(boolean palying) {
        LogUtil.d(TAG, "showPlayStatus isPlaying: " + palying);
        if (!getRemotePlayerEngine().isSingleMusicPlayed()) {
            if (palying) {
                mPlayerOrPause.setImageResource(R.drawable.music_softkey_pause_n);
            } else {
                mPlayerOrPause.setImageResource(R.drawable.music_softkey_play_n);
            }
        }
    }

    @Override
    public void clickChoice() {
        editMode = true;
        for (int i = 0; i < mMusiclist.size(); i++) {
            mMusiclist.get(i).isSelect = false;
        }
        selectAll = false;
        mDelete.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_delete_d));
        mDownload.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_download_d));
        mMusicAdapter.setData(mMusiclist);
        mMusicAdapter.setEditMode(true);
        showEditMode(true);
    }

    @Override
    public void clickCancel() {
        LogUtil.d(TAG, "clickCancel");
        editMode = false;
        mMusicAdapter.setEditMode(false);
        showEditMode(false);
        for (int i = 0; i < mMusiclist.size(); i++) {
            if (getRemotePlayerEngine().getCurrentSelectedMusic() != null) {
                if (mMusiclist.get(i).origpath.equals(getRemotePlayerEngine().getCurrentSelectedMusic().origpath)) {
                    mMusiclist.get(i).isSelect = true;
                } else {
                    mMusiclist.get(i).isSelect = false;
                }
            }
        }
        mSelectMusics.clear();

    }

    @Override
    public void clickUpload() {
        /*Intent intent = new Intent(MusicActivity.this, UploadLocalMusicActivity.class);
        intent.putExtra(UploadLocalMusicActivity.UPLOAD_PATH, "/lv/MultiMedia/Music");// /lv/MultiMedia/Movie
        startActivity(intent);*/
    }

    @Override
    public void clickSelectAll() {
        /*if (selectAll) {
            for (int i = 0; i < mMusiclist.size(); i++) {
                mMusiclist.get(i).setSelect(false);
            }
            mSelectMusics.clear();
            mMusicAdapter.setData(mMusiclist);
            mDelete.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_delete_d));
            mDownload.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_download_d));
        } else {
            mSelectMusics.clear();
            for (int i = 0; i < mMusiclist.size(); i++) {
                mMusiclist.get(i).setSelect(true);
                mSelectMusics.add(mMusiclist.get(i));
            }
            mDelete.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_delete_selector));
            mDownload.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_softkey_download_selector));
            mMusicAdapter.setData(mMusiclist);
        }
        selectAll = !selectAll;*/
    }

    @Override
    public void clickDownload() {
        /*if (mSelectMusics == null || mSelectMusics.size() == 0) {
            return;
        }
        int[] integerSize = getResources().getIntArray(R.array.folder_size_integer);
        int index = PreferenceUtils.getFloderSize(PreferenceUtils.INDEX);
        if ((CacheUtils.getDownloadFileSize(getApplicationContext()) / 1024) > integerSize[index]) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.more_than_download), Toast.LENGTH_SHORT).show();
            return;
        } else if (PreferenceUtils.getWifiOpen(PreferenceUtils.WIFI) && !NetWorkUtils.getNetWorkState(getApplicationContext()).equals("WIFI")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.wifi_tips), Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(MusicActivity.this, getString(R.string.add_download_list), Toast.LENGTH_LONG).show();
        DownloadManager downloadManager = DownloadManager.getInstance(getApplicationContext());
        ArrayList<String> photos = new ArrayList<String>();
        for (int i = 0; i < mSelectMusics.size(); i++) {
            photos.add(mSelectMusics.get(i).getOrigpath());
        }
        downloadManager.prepare(photos, ConstantUtils.MUSIC);
        for (int i = 0; i < mMusiclist.size(); i++) {
            mMusiclist.get(i).setSelect(false);
        }
        mSelectMusics.clear();
        showEditMode(false);
        mMusicAdapter.setEditMode(false);
        mMusicAdapter.setData(mMusiclist);*/
    }

    private CustomDialog mDeleteMusicDialog;

    @Override
    public void clickDelete() {
        if (mSelectMusics == null || mSelectMusics.size() == 0) {
            return;
        }
        View view = View.inflate(MusicActivity.this, R.layout.dialog_delete, null);
        TextView message = (TextView) view.findViewById(R.id.dialog_delete_message);
        message.setText(R.string.delete_tip);
        TextView sure = (TextView) view.findViewById(R.id.dialog_delete_confirm);
        TextView cancel = (TextView) view.findViewById(R.id.dialog_delete_cancel);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteMusicDialog.dismiss();
                ArrayList<String> deletePath = new ArrayList<String>();
                for (int i = 0; i < mSelectMusics.size(); i++) {
                    deletePath.add(mSelectMusics.get(i).origpath);
                }
                mMusciController.deleteMuscis(deletePath);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteMusicDialog.dismiss();
            }
        });
        mDeleteMusicDialog = new CustomDialog(MusicActivity.this, 0, 0, view, R.style.settings_style);
        mDeleteMusicDialog.show();
    }

    @Override
    public void playAll() {
        playMusic(0);
    }

    @Override
    public void playCurrent() {
        if (!getRemotePlayerEngine().isPlaying()) {
            showPlayConfirm(new Runnable() {
                @Override
                public void run() {
                    if (getRemotePlayerEngine().isPlaying()) {
                        getRemotePlayerEngine().pause();
                        showPlayStatus(false);
                    } else {
                        showPlayStatus(true);
                        getRemotePlayerEngine().play();
                    }
                    if (isIn4G()) {
                        AppApplication.isAllowIn4G = true;
                    }
                }
            });
        } else {
            if (getRemotePlayerEngine().isPlaying()) {
                getRemotePlayerEngine().pause();
                showPlayStatus(false);
            } else {
                showPlayStatus(true);
                getRemotePlayerEngine().play();
            }
        }
    }

    @Override
    public void clickShowSearch() {
        expand();
        mSearchMode = true;
    }

    @Override
    public void clickSearchCancel() {
        reduce();
        mSearchMode = false;
        for (int i = 0; i < mMusiclist.size(); i++) {
            if (getRemotePlayerEngine().getCurrentSelectedMusic() != null) {
                if (mMusiclist.get(i).origpath.equals(getRemotePlayerEngine().getCurrentSelectedMusic().origpath)) {
                    LogUtil.d(TAG, "select index: " + i);
                    mMusiclist.get(i).isSelect = true;
                    allSongsPlaylist.select(i);
                } else {
                    mMusiclist.get(i).isSelect = false;
                }
            }
        }
        if (mMusicAdapter != null) {
            ArrayList<MusicBean> musicBeens = mMusiclist;
//                        mMusiclist = musicBeens;
            mMusicAdapter.setData(musicBeens);
        }
    }

    @Override
    public void startDelete() {
        showWaitingDialog();
    }

    @Override
    public void deleteSucess() {
        hideWaitingDialog();
        int selectPosition = getRemotePlayerEngine().getPlaylist().getSelectedIndex();
        mMusiclist.removeAll(mSelectMusics);
        LogUtil.d(TAG, "deleteSucess size: " + mMusiclist.size());
        Playlist currentPlaylist = getRemotePlayerEngine().getPlaylist();
        boolean isSelectedDeleted = true;
        allSongsPlaylist = new Playlist();
        allSongsPlaylist.setMusicBeanList(mMusiclist);
        for (int i = 0; i < mMusiclist.size(); i++) {
            if (getRemotePlayerEngine().getCurrentSelectedMusic() != null) {
                if (mMusiclist.get(i).origpath.equals(getRemotePlayerEngine().getCurrentSelectedMusic().origpath)) {
                    LogUtil.d(TAG, "select index: " + i);
                    mMusiclist.get(i).isSelect = true;
                    allSongsPlaylist.select(i);
                    isSelectedDeleted = false;
                } else {
                    mMusiclist.get(i).isSelect = false;
                }
            }
        }
        getRemotePlayerEngine().getPlaylist().updatePlayedList(mSelectMusics);
        if (isSelectedDeleted) {
            if (getRemotePlayerEngine().isPlaying()) {
                getRemotePlayerEngine().pause();
            }
            if (mMusiclist.size() <= selectPosition) {
                selectPosition = 0;
            }
            if (!allSongsPlaylist.isEmpty()) {
                allSongsPlaylist.select(selectPosition);
                playMusic(selectPosition);
            }
        }
        if (mMusiclist.size() == 0) {
            //showProgressDialog(true);
            onRefresh = true;
            showNoContent(true);
            mMusciController.getMusicData();
        }
        if (currentPlaylist == null) {
            getRemotePlayerEngine().openPlaylist(allSongsPlaylist);
        } else {
            if (currentPlaylist != allSongsPlaylist) {
                getRemotePlayerEngine().openPlaylist(allSongsPlaylist);
            }
        }
        mMusicCount.setText(getApplicationContext().getString(R.string.music_count, mMusiclist.size()));
        editMode = false;
        showEditMode(false);
        mMusicAdapter.setEditMode(false);
        mMusicAdapter.setData(mMusiclist);
        mSelectMusics.clear();
    }

    @Override
    public void deleteFail() {
        hideWaitingDialog();
        Toast.makeText(this, R.string.delete_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clickClear() {
        mSearchEditText.setText("");
    }

    @Override
    public void playNext() {

        showPlayConfirm(new Runnable() {
            @Override
            public void run() {
                AppApplication.getInstance().getPlayerEngineInterface().next();
                showWaitingDialog();
                showPlayStatus(true);
                if (isIn4G()) {
                    AppApplication.isAllowIn4G = true;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicChangedReceiver != null) {
            unregisterReceiver(musicChangedReceiver);
        }

        if (mDeviceDisconnect != null) {
            unregisterReceiver(mDeviceDisconnect);
        }

        if (mNetChanged != null) {
            unregisterReceiver(mNetChanged);
        }
    }


    private BroadcastReceiver musicChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.d(TAG, "musicChangedReceiver is enter action:" + action);
            if (action == null) return;
            if (PlayerService.ACTION_CURRENT_PLAYING_MUSIC_CHANGED.equals(intent.getAction())) {
                if (mMusicAdapter != null) mMusicAdapter.refreshAdapter();
                MusicBean musicBean = intent.getParcelableExtra(PlayerService.MUSIC_BEAN);
                showMusicInfo(musicBean);
            } else if (PlayerService.ACTION_CURRENT_PLAYING_MUSIC_PLAYING.equals(intent.getAction())) {
                showPlayStatus(true);
            } else if (PlayerService.ACTION_CURRENT_PLAYING_MUSIC_PAUSE.equals(intent.getAction())) {
                showPlayStatus(false);
            } else if (PlayerService.ACTION_CURRENT_PLAYING_MUSIC_PREPARE_COMPLETE.equals(intent.getAction())) {
                Log.d(TAG, "ACTION_CURRENT_PLAYING_MUSIC_PREPARE_COMPLETE");
                int selectedIndex = 0;//AppApplication.getInstance().getPlayerEngineInterface().getPlaylist().getSelectedIndex();
                int firstVisibleIndex = mListView.getFirstVisiblePosition();
                int lastVisibleIndex = mListView.getLastVisiblePosition();
                if (selectedIndex != Playlist.VALID_INDEX && (selectedIndex < firstVisibleIndex || selectedIndex > lastVisibleIndex)) {
                    mListView.setSelection(selectedIndex);
                }
                hideWaitingDialog();
            } else if (PlayerService.ACTION_CURRENT_PLAYING_MUSIC_ERROR.equals(intent.getAction())) {
                Log.d(TAG, "ACTION_CURRENT_PLAYING_MUSIC_ERROR");
                Toast.makeText(MusicActivity.this, R.string.palay_error_tip, Toast.LENGTH_SHORT).show();
                mPlayerOrPause.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_body_play_n));
                hideWaitingDialog();
            }
        }
    };

    private BroadcastReceiver mDeviceDisconnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*if (LoginManager.OFFLINE_DEVICE.equals(intent.getAction())) {
                if (getRemotePlayerEngine() != null) {
                    getRemotePlayerEngine().pause();
                }
//                Toast.makeText(MusicActivity.this, R.string.device_offline, Toast.LENGTH_SHORT).show();
            }*/
        }
    };

    private BroadcastReceiver mNetChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

                NetworkInfo info  = manager.getActiveNetworkInfo();
                if (info != null) {
                    if (info.isConnected()) {
                        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.d(TAG, "mNetChanged action: " + ConnectivityManager.CONNECTIVITY_ACTION + " TYPE_MOBILE");
                            if (AppApplication.getInstance().getPlayerEngineInterface().isPlaying()) {
                                AppApplication.getInstance().getPlayerEngineInterface().pause();
                                showPlayConfirm(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppApplication.getInstance().getPlayerEngineInterface().play();
                                        if (isIn4G()) {
                                            AppApplication.isAllowIn4G = true;
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    };

    private boolean isIn4G() {
        boolean isIn4G = false;
        NetworkInfo info  = manager.getActiveNetworkInfo();
        if (info != null) {
            if (info.isConnected()) {
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isIn4G = true;
                }
            }
        }
        return isIn4G;
    }

    private void showPlayConfirm(final Runnable sureRun) {
        if (isIn4G()) {
            if (AppApplication.isAllowIn4G) {
                sureRun.run();
            } else {
                View view = View.inflate(MusicActivity.this, R.layout.dialog_delete, null);
                TextView message = (TextView) view.findViewById(R.id.dialog_delete_message);
                message.setText(R.string.only_wifi_play_tip);
                TextView sure = (TextView) view.findViewById(R.id.dialog_delete_confirm);
                TextView cancel = (TextView) view.findViewById(R.id.dialog_delete_cancel);
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m4GPlayConfirm.dismiss();
                        sureRun.run();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m4GPlayConfirm.dismiss();
                    }
                });
                m4GPlayConfirm = new CustomDialog(MusicActivity.this, 0, 0, view, R.style.settings_style);
                m4GPlayConfirm.show();
            }
        } else {
            sureRun.run();
        }
    }
}
