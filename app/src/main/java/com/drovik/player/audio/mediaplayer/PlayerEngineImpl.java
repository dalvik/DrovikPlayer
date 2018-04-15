package com.drovik.player.audio.mediaplayer;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.drovik.audio.MusicBean;
import com.android.library.net.utils.LogUtil;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerEngineImpl implements PlayerEngine {

    private static final long FAIL_TIME_FRAME = 1000;
    private static final int ACCEPTABLE_FAIL_NUMBER = 5;
    private static final String TAG = "PlayerEngineImpl";

    private Handler playerEngineHandler;
    private Playlist playlist = null;
    private PlayerEngineListener playerEngineListener;
    private long lastFailTime;
    private long timesFailed;
    private boolean buildNoPlay = false;// 为了获取刚进入时候的时长，这个时候不播放，只prepare
    private boolean isPlayingWhenFocusLoss = false;
    private boolean isSingleMusicPlayed = false;
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "OnAudioFocusChangeListener focusChange: " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                if (currentMediaPlayer != null) {
                    isPlayingWhenFocusLoss = currentMediaPlayer.isPlaying();
                }
                if (playerEngineListener != null) {
                    playerEngineListener.onTrackPause();
                }
                pause();
            }
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Log.d(TAG, "OnAudioFocusChangeListener focusChange AudioManager.AUDIOFOCUS_GAIN: " + isPlayingWhenFocusLoss);
                if (isPlayingWhenFocusLoss) {
                    if (playerEngineListener != null) {
                        playerEngineListener.onTrackStart();
                    }
                    if (isSingleMusicPlayed) {
                        play(0);
                    } else {
                        play();
                    }
                }
            }
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if (currentMediaPlayer != null) {
                    isPlayingWhenFocusLoss = currentMediaPlayer.isPlaying();
                }
                if (playerEngineListener != null) {
                    playerEngineListener.onTrackPause();
                }
                pause();
            }
        }
    };

    private boolean requestAudioFocus() {
        int result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        Log.d(TAG, "audioFocusChangeListener result: " + result);
        return result == AudioManager.AUDIOFOCUS_GAIN;
    }


    public PlayerEngineImpl(Context context) {
        lastFailTime = 0;
        timesFailed = 0;
        playerEngineHandler = new Handler();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private InternalMediaPlayer currentMediaPlayer;

    private class InternalMediaPlayer extends MediaPlayer {
        public MusicBean musicBean;
        public boolean preparing = false;
        public boolean playAfterPrepare = false;
        public long duration = 0;
    }

    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (playerEngineListener != null) {
                if (currentMediaPlayer != null)
                    playerEngineListener.onTrackProgress(currentMediaPlayer.getCurrentPosition() / 1000);
                playerEngineHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void openPlaylist(Playlist playlist) {
        if (!playlist.isEmpty())
            this.playlist = playlist;
        else
            this.playlist = null;
    }

    @Override
    public void reset() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.reset();
            currentMediaPlayer.release();
            currentMediaPlayer = null;
            getPlaylist().selectMusic = null;
            getPlaylist().getMusicBeanList().clear();
        }
    }

    @Override
    public Playlist getPlaylist() {
        return this.playlist;
    }

    @Override
    public void build() {
        buildNoPlay = true;
        isSingleMusicPlayed = false;
        LogUtil.d(TAG, "InternalMediaPlayer build build");
        currentMediaPlayer = build(this.playlist.getSelectedMusicBean());
    }

    @Override
    public long getDuration() {
        if (currentMediaPlayer == null) return 0;
        LogUtil.d(TAG, "mMusicPlayController getDuration: "+currentMediaPlayer.duration );
        return currentMediaPlayer.duration;
    }

    @Override
    public void buildByPath(String path, int type) {
        cleanUp();
        isSingleMusicPlayed = true;
        currentMediaPlayer = build(path, type);
    }

    @Override
    public boolean isSingleMusicPlayed() {
        return isSingleMusicPlayed;
    }

    @Override
    public void play(int type) {
        if (currentMediaPlayer == null) {
            return;
        }
        if (!currentMediaPlayer.preparing) {
            if (!currentMediaPlayer.isPlaying()) {
                playerEngineHandler.removeCallbacks(updateTimeTask);
                playerEngineHandler.postDelayed(updateTimeTask, 200);
                if (requestAudioFocus()) {
                    currentMediaPlayer.start();
                }
            }
        } else {
            currentMediaPlayer.playAfterPrepare = true;
        }
    }

    @Override
    public void play() {
        buildNoPlay = false;
        LogUtil.d(TAG, "InternalMediaPlayer build play");
        if (playerEngineListener == null || playerEngineListener.onTrackStart() == false) {
            LogUtil.d(TAG, "InternalMediaPlayer playerEngineListener == null || playerEngineListener.onTrackStart() == false");
            return;
        }

        if (playlist != null) {
//            if(currentMediaPlayer != null && getPlaybackMode() == Playlist.PlaylistPlaybackMode.REPEAT){
//                currentMediaPlayer.seekTo(0);
//            }

            if (playlist.getSelectedMusicBean() == null) {
                LogUtil.d(TAG, "InternalMediaPlayer playlist.getSelectedMusicBean() == null");
                if (playerEngineListener != null)
                    playerEngineListener.onTrackStreamError();
                return;
            }


            if (currentMediaPlayer != null && currentMediaPlayer.musicBean != playlist.getSelectedMusicBean()) {
                LogUtil.d(TAG, "InternalMediaPlayer currentMediaPlayer");
                cleanUp();
                //buildNoPlay = false;
                currentMediaPlayer = build(this.playlist.getSelectedMusicBean());
            }

            if (currentMediaPlayer == null) {
                LogUtil.d(TAG, "InternalMediaPlayer currentMediaPlayer == null");
                //buildNoPlay = false;
                currentMediaPlayer = build(this.playlist.getSelectedMusicBean());
            }

            if (currentMediaPlayer == null) {
                return;
            }

            if (!currentMediaPlayer.preparing) {
                if (!currentMediaPlayer.isPlaying()) {
                    playerEngineHandler.removeCallbacks(updateTimeTask);
                    playerEngineHandler.postDelayed(updateTimeTask, 200);
                    if (requestAudioFocus()) {
                        currentMediaPlayer.start();
                    }
                }
            } else {
                currentMediaPlayer.playAfterPrepare = true;
            }
        }
    }

    @Override
    public void updatePlayedList(ArrayList<MusicBean> removeMusicBeans) {
        playlist.updatePlayedList(removeMusicBeans);
    }

    @Override
    public MusicBean getCurrentSelectedMusic() {
        LogUtil.d(TAG, "getCurrentSelectedMusic : " + (playlist == null));
        if (playlist != null) {
            return playlist.getSelectedMusicBean();
        }
        return null;
    }


    private InternalMediaPlayer build(@NonNull MusicBean selectedMusicBean) {
        isSingleMusicPlayed = false;
        final InternalMediaPlayer mediaPlayer = new InternalMediaPlayer();

        String path = selectedMusicBean.origpath;
        if (null == path || path.isEmpty()) {
            if (playerEngineListener != null) {
                playerEngineListener.onTrackStreamError();
                playerEngineListener.onTrackChanged(selectedMusicBean);
            }
            stop();
            return null;
        }
        //path = "http://" + LoginManager.getInstance().baseIp+ ":" + LoginManager.getInstance().getP2PPort()  + "/" + path;
        LogUtil.d(TAG, "InternalMediaPlayer build path:" + path);
        try {
            //mediaPlayer.setDataSource(Uri.encode(path, "-![.:/,%?&=]"));
            mediaPlayer.setDataSource(path);
            mediaPlayer.musicBean = selectedMusicBean;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    LogUtil.d(TAG, "InternalMediaPlayer build setOnCompletionListener");
                    completeNext();
                }

            });

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.d(TAG, "mMusicPlayController setOnPreparedListener duration: " + mediaPlayer.getDuration());
                    mediaPlayer.duration = mediaPlayer.getDuration();
                    if (playerEngineListener != null) {
                        playerEngineListener.onPrepare();
                        playerEngineListener.onTrackTotalTime((int) mediaPlayer.duration);
                    }
                    mediaPlayer.preparing = false;
                    if (playlist.getSelectedMusicBean() == mediaPlayer.musicBean
                            && mediaPlayer.playAfterPrepare) {
                        mediaPlayer.playAfterPrepare = false;
                        if (!buildNoPlay) {
                            play();
                        }
                    }
                }
            });

            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    if (playerEngineListener != null) {
                        playerEngineListener.onTrackBuffering(percent);
                    }
                }

            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mediaPlayer.preparing = false;
                    LogUtil.d(TAG, "InternalMediaPlayer build setOnErrorListener what: " + what + " extra: " + extra);
                    if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                        // we probably lack network
                        if (playerEngineListener != null) {
                            playerEngineListener.onTrackStreamError();
                        }
                        stop();
                        return true;
                    }

                    // not sure what error code -1 exactly stands for but it causes player to start to jump songs
                    // if there are more than 5 jumps without playback during 1 second then we abort
                    // further playback
                    if (what == -1) {
                        long failTime = System.currentTimeMillis();
                        if (failTime - lastFailTime > FAIL_TIME_FRAME) {
                            // outside time frame
                            timesFailed = 1;
                            lastFailTime = failTime;
                        } else {
                            // inside time frame
                            timesFailed++;
                            if (timesFailed > ACCEPTABLE_FAIL_NUMBER) {

                                if (playerEngineListener != null) {
                                    playerEngineListener.onTrackStreamError();
                                }
                                stop();
                                return true;
                            }
                        }
                    }
                    if (buildNoPlay) {
                        return true;
                    }
                    return false;
                }
            });
            mediaPlayer.preparing = true;
            mediaPlayer.prepareAsync();

            // this is a new track, so notify the listener
            if (playerEngineListener != null) {
                playerEngineListener.onTrackChanged(playlist.getSelectedMusicBean());
            }
            return mediaPlayer;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private InternalMediaPlayer build(String path, int type) {
        final InternalMediaPlayer mediaPlayer = new InternalMediaPlayer();

        if (null == path || path.isEmpty()) {
            if (playerEngineListener != null) {
                playerEngineListener.onTrackStreamError();
            }
            stop();
            return null;
        }
        //path = "http://" + NasApplication.getInstance().baseIp+ ":" + NasApplication.getInstance().getP2PPort()  + "/" + path;
        LogUtil.d(TAG, "InternalMediaPlayer build path:" + path);
        try {
            /*if (type == MusicPlayDownloadActivity.TYPE_OFFLINE) {
                mediaPlayer.setDataSource(path);
            } else {
                path = "http://" + LoginManager.getInstance().baseIp+ ":" + LoginManager.getInstance().getP2PPort()  + "/" + path;
                mediaPlayer.setDataSource(Uri.encode(path, "-![.:/,%?&=]"));
            }*/
            //mediaPlayer.musicBean = selectedMusicBean;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    LogUtil.d(TAG, "InternalMediaPlayer build setOnCompletionListener");
//                    completeNext();
                    if (playerEngineListener != null) {
                        playerEngineListener.onTrackComplete();
                    }
                    playerEngineHandler.removeCallbacks(updateTimeTask);
                }

            });

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.d(TAG, "mMusicPlayController setOnPreparedListener duration: " + mediaPlayer.getDuration());
                    mediaPlayer.duration = mediaPlayer.getDuration();
                    if (playerEngineListener != null) {
                        playerEngineListener.onPrepare();
                        playerEngineListener.onTrackTotalTime((int) mediaPlayer.duration);
                    }
                    mediaPlayer.preparing = false;
                    if (currentMediaPlayer == null) {
                        return;
                    }

                    if (!currentMediaPlayer.preparing) {
                        if (!currentMediaPlayer.isPlaying()) {
                            playerEngineHandler.removeCallbacks(updateTimeTask);
                            playerEngineHandler.postDelayed(updateTimeTask, 200);
                            if (requestAudioFocus()) {
                                currentMediaPlayer.start();
                            }
                        }
                    } else {
                        currentMediaPlayer.playAfterPrepare = true;
                    }
                }
            });

            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    if (playerEngineListener != null) {
                        playerEngineListener.onTrackBuffering(percent);
                    }
                }

            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    LogUtil.d(TAG, "InternalMediaPlayer build setOnErrorListener what: " + what + " extra: " + extra);
                    if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                        // we probably lack network
                        if (playerEngineListener != null) {
                            playerEngineListener.onTrackStreamError();
                        }
                        stop();
                        return true;
                    }

                    // not sure what error code -1 exactly stands for but it causes player to start to jump songs
                    // if there are more than 5 jumps without playback during 1 second then we abort
                    // further playback
                    if (what == -1) {
                        long failTime = System.currentTimeMillis();
                        if (failTime - lastFailTime > FAIL_TIME_FRAME) {
                            // outside time frame
                            timesFailed = 1;
                            lastFailTime = failTime;
                        } else {
                            // inside time frame
                            timesFailed++;
                            if (timesFailed > ACCEPTABLE_FAIL_NUMBER) {

                                if (playerEngineListener != null) {
                                    playerEngineListener.onTrackStreamError();
                                }
                                stop();
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
            mediaPlayer.preparing = true;
            mediaPlayer.prepareAsync();

            // this is a new track, so notify the listener
            return mediaPlayer;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }/* catch (IOException e) {
            e.printStackTrace();
        }*/

        return null;
    }

    @Override
    public void next() {
        LogUtil.d(TAG, "InternalMediaPlayer build next");
        if (null != this.playlist) {
            playlist.selectNext();
            resetMediaPlayer();
            play();
        }
    }

    @Override
    public void completeNext() {
        LogUtil.d(TAG, "InternalMediaPlayer build completeNext");
        if (null != this.playlist) {
            playlist.selectCompleteNext();
            resetMediaPlayer();
            play();
        }
    }

    /**
     * 上一曲和下一曲时播放器置0
     */
    private void resetMediaPlayer() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.seekTo(0);
        }
    }

    @Override
    public boolean isPlaying() {
        if (currentMediaPlayer == null) {
            return false;
        }
        if (currentMediaPlayer.preparing) return false;
        return currentMediaPlayer.isPlaying();
    }

    @Override
    public boolean isPrepared() {
        if (currentMediaPlayer == null) {
            return false;
        }
        return !currentMediaPlayer.preparing;
    }

    @Override
    public void stop() {
        cleanUp();
        if (playerEngineListener != null) {
            playerEngineListener.onTrackStop();
        }
    }

    private void cleanUp() {
        if (currentMediaPlayer != null) {
            try {
                currentMediaPlayer.stop();
            } catch (Exception e) {
            } finally {
                currentMediaPlayer.release();
                currentMediaPlayer = null;
            }
        }
    }

    @Override
    public void prev() {
        LogUtil.d(TAG, "InternalMediaPlayer build prev");
        if (playlist != null) {
            playlist.selectPrev();
            resetMediaPlayer();
            play();
        }
    }


    @Override
    public void pause() {
        if (null != currentMediaPlayer) {

            if (currentMediaPlayer.preparing) {
                currentMediaPlayer.playAfterPrepare = false;
                return;
            }
            if (currentMediaPlayer.isPlaying()) {
                currentMediaPlayer.pause();
                if (playerEngineListener != null) {
                    playerEngineListener.onTrackPause();
                }
            }
        }
    }

    @Override
    public void skipTo(int index) {
        LogUtil.d(TAG, "InternalMediaPlayer build skipTo");
        playlist.select(index);
        play();
    }

    @Override
    public void setListener(PlayerEngineListener listener) {
        this.playerEngineListener = listener;
    }

    @Override
    public void setPlaybackMode(Playlist.PlaylistPlaybackMode aMode) {
        if (playlist != null) {
            playlist.setPlaylistPlaybackMode(aMode);
        } else {
            try {
                throw new Exception("playlist is null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public Playlist.PlaylistPlaybackMode getPlaybackMode() {
        return playlist.getPlaylistPlaybackMode();
    }

    public void forward(int time) {
        if (currentMediaPlayer.getDuration() >= currentMediaPlayer.getCurrentPosition() + time) {
            currentMediaPlayer.seekTo(currentMediaPlayer.getCurrentPosition() + time);
        } else {
            currentMediaPlayer.seekTo(currentMediaPlayer.getDuration());
        }

    }

    @Override
    public void rewind(int time) {
        if (currentMediaPlayer.getCurrentPosition() - time > 0) {
            currentMediaPlayer.seekTo(currentMediaPlayer.getCurrentPosition() - time);
        } else {
            currentMediaPlayer.seekTo(0);
        }

    }

    @Override
    public void seekTo(int progress) {
        currentMediaPlayer.seekTo(progress);
    }

    @Override
    public boolean addOrCancelFavMusic(MusicBean musicBean, boolean isAdd) {
        if (playlist != null) {
            return playlist.addOrCancelFav(musicBean, isAdd);
        }
        return false;
    }

    @Override
    public void delCurrentSelectedMusicAndPlayNext() {
        LogUtil.d(TAG, "InternalMediaPlayer build delCurrentSelectedMusicAndPlayNext");
        if (playlist != null) {
            playlist.delCurrentSelectedMusicAndSetSelectedMusic();
            play();
        }

    }

    @Override
    public MediaPlayer getCurrentMediaplayer() {
        if (currentMediaPlayer != null) {
            return currentMediaPlayer;
        }
        return null;
    }
}
