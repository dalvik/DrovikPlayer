package com.drovik.player.audio.mediaplayer;

import com.drovik.player.audio.MusicBean;
import com.android.audiorecorder.utils.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Playlist implements Serializable {


    private final static int SHUFFLE_NEXT = 2000;
    public final static int VALID_INDEX = -1;

    public MusicBean selectMusic;

    private String TAG = "Playlist";

    public void select(int index) {
        if (index >= 0 && index < musicBeanList.size()) {
            selected = index;
            selectMusic = musicBeanList.get(selected);
        } else {
            try {
                throw new Exception("select index out of list");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 同步当前歌曲列表
     */
    public void syncCurrentList() {
        List<MusicBean> tmpList = new ArrayList<>();
        for (MusicBean bean : orignMusicBeanList) {
            int i = 0;
            for (MusicBean myBean : musicBeanList) {
                if (bean.origpath.equals(myBean.origpath)) {
                    i++;
                    break;
                }
            }
            if (i == 0) {
                tmpList.add(bean);
            }
        }
        musicBeanList.addAll(tmpList);
    }

    public void delCurrentSelectedMusicAndSetSelectedMusic() {
        musicBeanList.remove(getSelectedMusicBean());
        if (!isEmpty())
            selected %= musicBeanList.size();
        else
            selected = -1;
    }

    public void delCurrentSelecteMusicBean() {
        musicBeanList.remove(getSelectedMusicBean());
    }

    public enum PlaylistPlaybackMode {
        NORMAL, SHUFFLE, REPEAT;

        public static PlaylistPlaybackMode getPlaybackModeByOrdinal(int ordinal) {
            if (NORMAL.ordinal() == ordinal) {
                return NORMAL;
            } else if (SHUFFLE.ordinal() == ordinal) {
                return SHUFFLE;
            } else if (REPEAT.ordinal() == ordinal) {
                return REPEAT;
            } else {
                return NORMAL;
            }
        }
    }

    protected int selected = -1;

    private PlaylistPlaybackMode playlistPlaybackMode = PlaylistPlaybackMode.NORMAL;


    private List<MusicBean> musicBeanList = new ArrayList<>();


    private List<String> playedMusicList = new ArrayList<>();


    private String fileTitle;

    private List<MusicBean> orignMusicBeanList;

    public List<MusicBean> getOrignMusicBeanList() {
        return orignMusicBeanList;
    }

    public List<MusicBean> getMusicBeanList() {
        return musicBeanList;
    }

    public void setMusicBeanList(List<MusicBean> musicBeanList) {
        this.orignMusicBeanList = musicBeanList;
        this.musicBeanList.clear();
        this.musicBeanList.addAll(musicBeanList);
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }


    /**
     * 设置是否添加或取消到收藏
     *
     * @param isAdd
     */
    public boolean addOrCancelFav(MusicBean musicBean, boolean isAdd) {
        musicBean.fav = isAdd;
        for (MusicBean bean : orignMusicBeanList) {
            if (musicBean.origpath.equals(bean.origpath)) {
                bean.fav = isAdd;
                return true;
            }
        }
        return false;
    }


    public PlaylistPlaybackMode getPlaylistPlaybackMode() {
        return playlistPlaybackMode;
    }

    public void setPlaylistPlaybackMode(PlaylistPlaybackMode playlistPlaybackMode) {
        if (playlistPlaybackMode != PlaylistPlaybackMode.SHUFFLE) {
            playedMusicList.clear();
        }
        this.playlistPlaybackMode = playlistPlaybackMode;
    }

    public boolean isEmpty() {
        return musicBeanList.isEmpty();
    }


    public int getSelectedIndex() {
        for (int i = 0; i < musicBeanList.size(); i++) {
            if (musicBeanList.get(i).origpath.equals(selectMusic.origpath)) {
                selected = i;
                return i;
            }
        }
        return -1;
    }

    public MusicBean getSelectedMusicBean() {
//        LogUtil.d(Playlist.class, "getSelectedMusicBean selected: " + selected + " isEmpty: " + isEmpty());
//        if (selected != -1 && selected < musicBeanList.size()) {
//            selectMusic = musicBeanList.get(selected);
//            return musicBeanList.get(selected);
//        } else {
//            if (!isEmpty() && selected == -1) {
//                selected = 0;
//                selectMusic = musicBeanList.get(selected);
//                return musicBeanList.get(selected);
//            } else {
//                return musicBeanList.get(0);
//            }
//        }
        if (selectMusic == null && musicBeanList.size() > 0) {
            selectMusic = musicBeanList.get(0);
        }
        return selectMusic;
    }

    /**
     * 用于判断 选择的歌曲是否在现有的列表中
     * @return
     */
    public boolean isSelectedMusicInCurrentMusics() {
        for (int i = 0; i < musicBeanList.size(); i++) {
            if (musicBeanList.get(i).origpath.equals(selectMusic.origpath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 更新选择歌曲列表索引
     * @param newMusicList
     */
    public void updateSelectedIndex(ArrayList<MusicBean> newMusicList)  {
        if (selectMusic != null) {
            for (int i = 0; i < newMusicList.size(); i++) {
                if (selectMusic.origpath.equals(newMusicList.get(i).origpath)) {
                    selected = i;
                }
            }
        }
    }

    /**
     * 获取当前播放的歌曲是否已收藏
     *
     * @return
     */
    public boolean isCurrentMusicFav() {
        return getSelectedMusicBean().fav;
    }


    public boolean isLastMusicOnList() {
        return selected == musicBeanList.size() - 1;
    }

    public void selectCompleteNext() {
        if (!isEmpty()) {
            switch (getPlaylistPlaybackMode()) {
                case NORMAL:
                    selected++;
                    selected %= musicBeanList.size();
                    selectMusic = musicBeanList.get(selected);
                    break;

                case SHUFFLE:
//                    if (playedMusicList.size() <= 5) {
//                        selected++;
//                        selected %= musicBeanList.size();
//                        playedMusicList.add(getSelectedMusicBean().origpath);
//                    } else {
                    playedMusicList.add(getSelectedMusicBean().origpath);
                    selected = new Random().nextInt(musicBeanList.size() + 1 + SHUFFLE_NEXT);//lyw
                    LogUtil.d(TAG, "selectNext SHUFFLE selected:" + selected + "--musicBeanList size" + musicBeanList.size());
                    selected %= musicBeanList.size();
                    selectMusic = musicBeanList.get(selected);
//                    }
                    break;

                case REPEAT:
                    selected %= musicBeanList.size();
                    selectMusic = musicBeanList.get(selected);
                    break;
            }
        }
    }

    public void updatePlayedList(ArrayList<MusicBean> removeMusicBeens) {
        Iterator<String> iterator = playedMusicList.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            for (int i = 0; i < removeMusicBeens.size(); i++) {
                if (value.equals(removeMusicBeens.get(i).origpath)) {
                    iterator.remove();
                }
            }
        }
    }


    public void selectNext() {
        LogUtil.d(TAG, "selectNext mode: " + getPlaylistPlaybackMode());
        if (!isEmpty()) {
            switch (getPlaylistPlaybackMode()) {
                case NORMAL:
                    selected++;
                    selected %= musicBeanList.size();
                    selectMusic = musicBeanList.get(selected);
                    break;

                case SHUFFLE:
                    LogUtil.d(TAG, "selectNext mode: " + getPlaylistPlaybackMode() + " size: " + playedMusicList.size());
//                    if (playedMusicList.size() <= 5) {
//                        selected++;
//                        selected %= musicBeanList.size();
//                        playedMusicList.add(getSelectedMusicBean().origpath);
//                    } else {
                    playedMusicList.add(getSelectedMusicBean().origpath);
                    selected = new Random().nextInt(musicBeanList.size() + 1 + SHUFFLE_NEXT);//lyw
                    LogUtil.d(TAG, "selectNext SHUFFLE selected:" + selected + "--musicBeanList size" + musicBeanList.size());
                    selected %= musicBeanList.size();
                    selectMusic = musicBeanList.get(selected);
//                    }
                    break;

                case REPEAT:
                    selected++;
                    selected %= musicBeanList.size();
                    selectMusic = musicBeanList.get(selected);
                    break;
            }
        }
    }


    public void selectPrev() {
        if (!isEmpty()) {

            switch (getPlaylistPlaybackMode()) {
                case NORMAL:
                case REPEAT:
                    selected--;
                    if (selected < 0) {
                        selected = musicBeanList.size() - 1;
                    }
                    selectMusic = musicBeanList.get(selected);
                    break;

                case SHUFFLE:
//                    if (playedMusicList != null && playedMusicList.size() <= 5) {
//                        selected--;
//                        if (selected < 0) {
//                            selected = musicBeanList.size() - 1;
//                        }
//                        playedMusicList.add(getSelectedMusicBean().origpath);
//                    } else {
                    selectPrevWhenShuffleMode();
                    selectMusic = musicBeanList.get(selected);
//                    }
                    break;
            }
        }
    }

    private void selectPrevWhenShuffleMode() {
        if (playedMusicList.isEmpty()) {
            selected = new Random().nextInt(musicBeanList.size() - 1 + SHUFFLE_NEXT);
            selected %= musicBeanList.size();
        } else {
            while (true) {
                if (!playedMusicList.isEmpty()) {
                    String origpath = playedMusicList.get(playedMusicList.size() - 1);
                    for (int i = 0; i < musicBeanList.size(); i++) {
                        if (origpath.equals(musicBeanList.get(i).origpath)) {
                            selected = i;
                            selected %= musicBeanList.size();
                            playedMusicList.remove(playedMusicList.size() - 1);
                            return;
                        }
                    }
                    playedMusicList.remove(playedMusicList.size() - 1);
                } else {
                    selected = new Random().nextInt(musicBeanList.size() + 1 + SHUFFLE_NEXT);//lyw
                    selected %= musicBeanList.size();
                    return;
                }
            }
        }
    }
}
