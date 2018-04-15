package com.drovik.player.audio.mediaplayer;

import android.os.AsyncTask;
import android.view.View;
import com.drovik.player.R;
import com.drovik.player.AppApplication;
import com.drovik.player.audio.MusicBean;
import com.drovik.player.audio.ui.IMusicView;
import com.android.audiorecorder.utils.LogUtil;

import java.util.ArrayList;

public class MusicController implements View.OnClickListener {
    private IMusicView mIMusicView;
    private static final int COUNT_LEN = 8;

    public MusicController(IMusicView view) {
        mIMusicView = view;
    }

    public void getMusicData() {
        new GetMusicDataAsyncTask().execute();
    }

    public void getMusicMoreData(int offset) {
        new GetMusicDataMoreAsyncTask().execute(offset);
    }

    private String TAG = "MusicController";

    public void deleteMuscis(ArrayList<String> deletePath) {
        new DeleteMusicAsyncTask().execute(deletePath);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.activity_music_back:
                if (mIMusicView != null) {
                    mIMusicView.finshActivity();
                }
                break;*/
            case R.id.activity_music_bottom_widget:
                if (mIMusicView != null) {
                    mIMusicView.startMusicPlay();
                }
                break;
            case R.id.activity_music_bottom_widget_next:
                if (mIMusicView != null) {
                    mIMusicView.playNext();
                }

                break;
            case R.id.activity_music_bottom_widget_pause:
                //LogUtil.d(TAG, "pause or play:" + AppApplication.getInstance().getPlayerEngineInterface().isPlaying());
                if (mIMusicView != null) {
                    mIMusicView.playCurrent();
                }
                break;
            /*case R.id.activity_music_choice:
                if (mIMusicView != null) {
                    mIMusicView.clickChoice();
                }
                break;
            case R.id.activity_music_cancel:
                if (mIMusicView != null) {
                    mIMusicView.clickCancel();
                }
                break;
            case R.id.activity_music_upload:
                if (mIMusicView != null) {
                    mIMusicView.clickUpload();
                }
                break;
            case R.id.activity_music_select_all:
                if (mIMusicView != null) {
                    mIMusicView.clickSelectAll();
                }
                break;
            case R.id.activity_photo_download:
                if (mIMusicView != null) {
                    mIMusicView.clickDownload();
                }
                break;
            case R.id.activity_photo_delete:
                if (mIMusicView != null) {
                    mIMusicView.clickDelete();
                }
                break;*/
            case R.id.activity_music_playall:
                if (mIMusicView != null) {
                    mIMusicView.playAll();
                }
                break;
            case R.id.activity_music_search:
                if (mIMusicView != null) {
                    mIMusicView.clickShowSearch();
                }
                break;
            case R.id.activity_music_search_cancel:
                if (mIMusicView != null) {
                    mIMusicView.clickSearchCancel();
                }
                break;
            case R.id.clear:
                if (mIMusicView != null) {
                    mIMusicView.clickClear();
                }
                break;
            default:
                break;
        }
    }

    private PlayerEngine getRemotePlayerEngine() {
        return AppApplication.getInstance().getPlayerEngineInterface();
    }


    class DeleteMusicAsyncTask extends AsyncTask<ArrayList<String>, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mIMusicView != null) {
                mIMusicView.startDelete();
            }
        }

        @Override
        protected Boolean doInBackground(ArrayList<String>... params) {
            if (isCancelled()) {
                return false;
            }
            boolean removeFiles = true;// GetDataManager.getInstance().removeFiles(params[0]);
            return removeFiles;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                if (mIMusicView != null) {
                    mIMusicView.deleteSucess();
                }
            } else {
                if (mIMusicView != null) {
                    mIMusicView.deleteFail();
                }
            }
        }
    }

    class GetMusicDataAsyncTask extends AsyncTask<Void, Void, ArrayList<MusicBean>> {
        int onceCount = 12;
        int offset = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mIMusicView != null) {
                mIMusicView.startLoad();
            }
        }

        @Override
        protected ArrayList<MusicBean> doInBackground(Void... params) {
            LogUtil.d(TAG, "GetMusicDataAsyncTask doInBackground is enter");
            ArrayList<MusicBean> list = new ArrayList<MusicBean>();
            for(int i=0; i<20; i++) {
                MusicBean bean = new MusicBean();
                bean.playpath = "/storage/sdcard0/mpt/2010-01-09/001/da/03/03.08.04-03.08.10[R][0@0][0].dav";
                bean.bitrate = 128;
                bean.channels = 1;
                bean.duration = "100000";
                bean.album = "/sdcard/mpt/2010-01-09/001/da/03/03.07.20-03.07.34[R][0@0][0].dav";
                bean.origpath = "/storage/sdcard0/mpt/2010-01-09/001/da/03/03.08.04-03.08.10[R][0@0][0].dav";
                bean.singer = "singer name";
                bean.thumbpath2 = "/sdcard/mpt/2010-01-09/001/da/03/03.07.20-03.07.34[R][0@0][0].dav";
                bean.title = "03.07.20-03.07.34[R][0@0][0].dav";
                bean.fav = false;
                list.add(bean);
            }
            /*GetContentByListResponse contentByList = GetDataManager.getInstance().getContentByList("@Mymusic", "title", "Ascent", offset, onceCount);
            if (contentByList != null && contentByList.result) {
                return contentByList.params.list;
            }*/
            return list;
        }


        @Override
        protected void onPostExecute(ArrayList<MusicBean> musicBeen) {
            super.onPostExecute(musicBeen);
            if (musicBeen != null) {
                if (mIMusicView != null) {
                    mIMusicView.successLoad(musicBeen);
                }
            } else {
                mIMusicView.failLoad();
            }

        }
    }

    class GetMusicDataMoreAsyncTask extends AsyncTask<Integer, Void, ArrayList<MusicBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<MusicBean> doInBackground(Integer... params) {
            int offset = params[0];
            /*GetContentByListResponse contentByList = GetDataManager.getInstance().getContentByList("@Mymusic", "title", "Ascent", offset, COUNT_LEN);

            if (contentByList != null && contentByList.result) {
                return contentByList.params.list;
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MusicBean> musicBeen) {
            super.onPostExecute(musicBeen);
            if (musicBeen != null) {
                if (mIMusicView != null) {
                    mIMusicView.successLoadMore(musicBeen);
                }
            } else {
                mIMusicView.failLoad();
            }
        }
    }
}
