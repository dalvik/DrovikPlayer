package com.drovik.player.audio.mediaplayer;

import com.drovik.player.audio.MusicBean;

public interface IMusicPlayView {

    void showMusicInfo(MusicBean bean);

    void showPlayStatus(boolean palying);

    void playOrPause();

    void playPre();

    void playNext();

    void setPlaybackMode(Playlist.PlaylistPlaybackMode mode);

    void finishActivity();

    void showVoiceStatus();

    void showPlayTime(int seconds);

    void showTotalTime(int seconds);

    void prepareComplete();

    void playError();

}
