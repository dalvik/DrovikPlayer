package com.drovik.player.audio.mediaplayer;

import android.media.MediaPlayer;

import com.drovik.player.audio.MusicBean;

import java.util.ArrayList;

public interface PlayerEngine {

    void openPlaylist(Playlist playlist);

    void reset();

    void build();

    long getDuration();

    void buildByPath(String path, int type);

    boolean isSingleMusicPlayed();

    void play(int type);

    Playlist getPlaylist();

    void play();

    void updatePlayedList(ArrayList<MusicBean> removeMusicBeans);

    MusicBean getCurrentSelectedMusic();

    boolean isPlaying();

    boolean isPrepared();

    void next();

    void completeNext();

    void stop();

    void prev();

    void pause();

    void skipTo(int index);

    void setListener(PlayerEngineListener listener);

    void setPlaybackMode(Playlist.PlaylistPlaybackMode aMode);

    Playlist.PlaylistPlaybackMode getPlaybackMode();

    void forward(int time);

    void rewind(int time);

    void seekTo(int progress);

    boolean addOrCancelFavMusic(MusicBean musicBean, boolean isAdd);

    void delCurrentSelectedMusicAndPlayNext();

    MediaPlayer getCurrentMediaplayer();

}
