package com.drovik.player.audio.ui;

import com.drovik.player.audio.MusicBean;

import java.util.ArrayList;

public interface IMusicView {
    public void startLoad();

    public void successLoad(ArrayList<MusicBean> list);
    public void successLoadMore(ArrayList<MusicBean> list);

    public void failLoad();

    public void finshActivity();

    public void startMusicPlay();

    public void showPlayStatus(boolean palying);

    public void clickChoice();

    public void clickCancel();

    public void clickUpload();

    public void clickSelectAll();

    public void clickDownload();

    public void clickDelete();

    public void playAll();

    public void playCurrent();

    public void clickShowSearch();

    public void clickSearchCancel();

    public void startDelete();

    public void deleteSucess();

    public void deleteFail();

    public void clickClear();

    public void playNext();


}
