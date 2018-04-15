package com.drovik.player.audio.mediaplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.drovik.player.AppApplication;
import com.drovik.player.audio.MusicBean;
import com.android.audiorecorder.utils.LogUtil;


public class PlayerService extends Service {

    public static final String ACTION_PLAY = "play";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PREV = "prev";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_COMPLETE_NEXT = "complete_next";
    public static final String ACTION_BIND_LISTENER = "bind_listener";
    /**
     * 当前播放的歌曲进行了切换action
     */
    public static final String ACTION_CURRENT_PLAYING_MUSIC_CHANGED = "action_current_playing_music_changed";
    public static final String ACTION_CURRENT_PLAYING_MUSIC_PLAYING = "action_current_playing_music_playing";
    public static final String ACTION_CURRENT_PLAYING_MUSIC_PAUSE = "action_current_playing_music_pause";
    public static final String ACTION_CURRENT_PLAYING_MUSIC_PREPARE_COMPLETE = "action_current_playing_music_prepare_complete";
    public static final String ACTION_CURRENT_PLAYING_MUSIC_ERROR = "action_current_playing_music_error";
    public static final String MUSIC_BEAN = "Music_bean";
    private PlayerEngineListener remoteEngineListener;
    private static final String TAG = "PlayerService";

    public PlayerService() {
    }

    private PlayerEngine playerEngine;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private PlayerEngineListener localEngineListener = new PlayerEngineListener() {
        @Override
        public boolean onTrackStart() {
            LogUtil.d(TAG, "onTrackStart remoteEngineListener:" + remoteEngineListener);
            if (remoteEngineListener != null) {
                LogUtil.d(TAG, "onTrackStart onTrackStart:" + remoteEngineListener.onTrackStart());
                if (!remoteEngineListener.onTrackStart()) {
                    return false;
                }
            }
            Intent intent = new Intent(ACTION_CURRENT_PLAYING_MUSIC_PLAYING);
            PlayerService.this.sendBroadcast(intent);
            return true;
        }

        @Override
        public void onTrackChanged(MusicBean musicBean) {
            Intent intent = new Intent(ACTION_CURRENT_PLAYING_MUSIC_CHANGED);
            intent.putExtra(MUSIC_BEAN, musicBean);
            PlayerService.this.sendBroadcast(intent);
            if (remoteEngineListener != null) {
                remoteEngineListener.onTrackChanged(musicBean);
            }
        }

        @Override
        public void onTrackProgress(int seconds) {
            if (remoteEngineListener != null) {
                remoteEngineListener.onTrackProgress(seconds);
            }
        }

        @Override
        public void onTrackTotalTime(int seconds) {
            if (remoteEngineListener != null) {
                remoteEngineListener.onTrackTotalTime(seconds);
            }
        }

        @Override
        public void onTrackBuffering(int percent) {
            if (remoteEngineListener != null) {
                remoteEngineListener.onTrackBuffering(percent);
            }
        }

        @Override
        public void onTrackStop() {
            if (remoteEngineListener != null) {
                remoteEngineListener.onTrackStop();
            }
        }

        @Override
        public void onTrackPause() {
            if (remoteEngineListener != null) {
                remoteEngineListener.onTrackPause();
            }
            Intent intent = new Intent(ACTION_CURRENT_PLAYING_MUSIC_PAUSE);
            PlayerService.this.sendBroadcast(intent);
        }

        @Override
        public void onTrackStreamError() {
            if (remoteEngineListener != null) {
                remoteEngineListener.onTrackStreamError();
            }
            Intent intent = new Intent(ACTION_CURRENT_PLAYING_MUSIC_ERROR);
            PlayerService.this.sendBroadcast(intent);
        }

        @Override
        public void onTrackComplete() {
            if (remoteEngineListener != null) {
                remoteEngineListener.onTrackComplete();
            }
        }

        @Override
        public void onPrepare() {
            Log.d(TAG, "onPrepare");
            Intent intent = new Intent(ACTION_CURRENT_PLAYING_MUSIC_PREPARE_COMPLETE);
            PlayerService.this.sendBroadcast(intent);
            if (remoteEngineListener != null) {
                remoteEngineListener.onPrepare();
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        playerEngine = new PlayerEngineImpl(this);
        playerEngine.setListener(localEngineListener);
        AppApplication.getInstance().setConcretePlayerEngine(playerEngine);
        remoteEngineListener = AppApplication.getInstance().fetchPlayerEngineListener();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        String action = intent.getAction();

        if (action.equals(ACTION_STOP)) {
            stopSelfResult(startId);
            return super.onStartCommand(intent, flags, startId);
        }

        if (action.equals(ACTION_BIND_LISTENER)) {
            remoteEngineListener = AppApplication.getInstance().fetchPlayerEngineListener();
            return super.onStartCommand(intent, flags, startId);
        }

        updatePlaylist();

        if (action.equals(ACTION_PLAY)) {

            playerEngine.play();

            return super.onStartCommand(intent, flags, startId);
        }


        if (action.equals(ACTION_NEXT)) {

            playerEngine.next();

            return super.onStartCommand(intent, flags, startId);
        }
        if (action.equals(ACTION_COMPLETE_NEXT)) {

            playerEngine.completeNext();

            return super.onStartCommand(intent, flags, startId);
        }

        if (action.equals(ACTION_PREV)) {
            playerEngine.prev();
            return super.onStartCommand(intent, flags, startId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private synchronized void updatePlaylist() {
        /*if (playerEngine.getPlaylist() != NasApplication.getInstance().fetchPlaylist()) {
            playerEngine.openPlaylist(NasApplication.getInstance().fetchPlaylist());
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //NasApplication.getInstance().setConcretePlayerEngine(null);
        playerEngine.stop();
        playerEngine = null;
    }
}
