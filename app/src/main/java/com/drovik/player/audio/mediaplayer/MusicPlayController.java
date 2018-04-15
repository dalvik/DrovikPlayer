package com.drovik.player.audio.mediaplayer;

import android.content.Context;
import android.view.View;

import com.drovik.player.AppApplication;
import com.drovik.player.R;
import com.drovik.player.audio.MusicBean;
import com.android.audiorecorder.utils.LogUtil;

public class MusicPlayController implements View.OnClickListener, PlayerEngineListener {
    private Context mContext;
    private IMusicPlayView mMusicPlayView;
    private MusicBean mCurrentMusicBean;

    private String TAG = "MusicPlayController";

    public MusicPlayController(Context context, IMusicPlayView view) {
        this.mContext = context;
        this.mMusicPlayView = view;
        AppApplication.getInstance().setPlayerEngineListener(this);
        mCurrentMusicBean = getRemotePlayerEngine().getCurrentSelectedMusic();
    }

    public void showPlayMusic() {
        LogUtil.d(TAG, "showPlayMusic : " +( getRemotePlayerEngine().getCurrentSelectedMusic() == null) + " mMusicPlayView: " + (mMusicPlayView == null));
        if (mMusicPlayView != null) {
            mMusicPlayView.showMusicInfo(getRemotePlayerEngine().getCurrentSelectedMusic());
        }
    }

    @Override
    public void onClick(View v) {
        LogUtil.d(TAG, "onclick is enter");
        switch (v.getId()) {
            case R.id.activity_music_play:
                LogUtil.d(TAG, "activity_music_play is onclick " + getRemotePlayerEngine().isPlaying());
                mMusicPlayView.playOrPause();
                break;
            case R.id.activity_music_play_pre:
                LogUtil.d(TAG, "activity_music_play_pre is onclick " + getRemotePlayerEngine().isPlaying());

                mMusicPlayView.playPre();
                break;
            case R.id.activity_music_play_next:

                mMusicPlayView.playNext();
                break;

            case R.id.activity_music_play_playmode:
                changeMode();
                break;
//            case R.id.activity_music_play_playvoice:
//                if (mMusicPlayView != null) {
//                    mMusicPlayView.showVoiceStatus();
//                }
//                break;
            case R.id.activity_music_play_back:
                mMusicPlayView.finishActivity();
                break;
        }

    }

    public void changeMode() {
        LogUtil.d(TAG, "changeMode is onclick " + getRemotePlayerEngine().isPlaying());
        int nextMode = (getRemotePlayerEngine().getPlaybackMode().ordinal() + 1) % Playlist.PlaylistPlaybackMode.values().length;
        LogUtil.d(TAG, "selectNext : " + Playlist.PlaylistPlaybackMode.getPlaybackModeByOrdinal(nextMode));
        getRemotePlayerEngine().getPlaylist().setPlaylistPlaybackMode(Playlist.PlaylistPlaybackMode.getPlaybackModeByOrdinal(nextMode));
        showNextModeAndSave(nextMode);
    }

    public void showNextModeAndSave(int nextMode) {
        setPlaybackMode(nextMode);
        //PrefUtils.setPlaybackMode(nextMode);
    }

    private void setPlaybackMode(int mode) {
        Playlist.PlaylistPlaybackMode playbackMode = Playlist.PlaylistPlaybackMode.getPlaybackModeByOrdinal(mode);
        mMusicPlayView.setPlaybackMode(playbackMode);
    }

    @Override
    public boolean onTrackStart() {
        //Todo
//        mMusicPlayView.showPlayStatus(true);
        return true;
    }

    @Override
    public void onTrackChanged(MusicBean musicBean) {
        mMusicPlayView.showMusicInfo(musicBean);
    }

    @Override
    public void onTrackProgress(int seconds) {
        mMusicPlayView.showPlayTime(seconds);
    }

    @Override
    public void onTrackTotalTime(int seconds) {
        mMusicPlayView.showTotalTime(seconds/1000);
    }

    @Override
    public void onTrackBuffering(int percent) {
    }

    @Override
    public void onTrackStop() {
    }

    @Override
    public void onTrackPause() {
//        mMusicPlayView.showPlayStatus(false);
    }

    @Override
    public void onTrackStreamError() {
        mMusicPlayView.playError();
    }

    @Override
    public void onTrackComplete() {
    }

    @Override
    public void onPrepare() {
        mMusicPlayView.prepareComplete();
    }

    public PlayerEngine getRemotePlayerEngine() {
        return AppApplication.getInstance().getPlayerEngineInterface();
    }


}
