package com.drovik.player.audio.mediaplayer;


import com.drovik.player.audio.MusicBean;

public interface PlayerEngineListener {

    boolean onTrackStart();

    void onTrackChanged(MusicBean musicBean);

    void onTrackProgress(int seconds);

    void onTrackTotalTime(int seconds);

    void onTrackBuffering(int percent);

    void onTrackStop();

    void onTrackPause();

    void onTrackStreamError();

    void onTrackComplete();

    void onPrepare();

}
