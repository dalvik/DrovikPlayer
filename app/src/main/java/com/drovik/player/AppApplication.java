package com.drovik.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.multidex.MultiDex;

import com.android.audiorecorder.AppContext;
import com.android.audiorecorder.engine.MultiMediaService;
import com.drovik.player.audio.MusicBean;
import com.drovik.player.audio.mediaplayer.PlayerEngine;
import com.drovik.player.audio.mediaplayer.PlayerEngineListener;
import com.drovik.player.audio.mediaplayer.PlayerService;
import com.drovik.player.audio.mediaplayer.Playlist;
import com.android.library.BaseApplication;
import com.android.audiorecorder.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;

public class AppApplication extends BaseApplication {

    public static final String ACTION_INTENT_DISCONNECT = "disconnect";                        //断线
    public static final String ACTION_INTENT_LOGINID = "loginid";                        //登陆句柄
    public static final String ACTION_INTENT_DEVIP = "ip";                                //设备ip
    public static final String ACTION_INTENT_DEVPORT = "port";                            //设备port
    public static int DEVICE_PORT = 37777;
    public boolean firstInHome=true;
    public boolean isFirstInHome() {
        return firstInHome;
    }

    public void setFirstInHome(boolean firstInHome) {
        this.firstInHome = firstInHome;
    }

    public String baseIp;
    //    public static LoginHandle loginHandle;
    public static int deviceType;
    private static AppApplication mMyApplication;
    private static Activity mMainActivity = null;        //缓存MainActivity，防止内存不足把此Activity干掉
    private Map<String, Boolean> mCallOrIgnore = new HashMap<String, Boolean>();
    private Map<String, Boolean> mRealAnsweredVTO = new HashMap<String, Boolean>();

    public static AppApplication getInstance() {
        return mMyApplication;
    }

    public void setMainActivity(Activity activity) {
        mMainActivity = activity;
    }

    public Activity getMainActivity() {
        return mMainActivity;
    }

    public void setBaseIP(String ip) {
        this.baseIp = ip;
    }

    public String getBaseIp() {
        return this.baseIp;
    }

    public Map<String, Boolean> getCallOrIgnore() {
        return mCallOrIgnore;
    }

    public Map<String, Boolean> getRealAnsweredVTO() {
        return mRealAnsweredVTO;
    }

    public static boolean isAllowIn4G = false;

    private PlayerEngine servicePlayerEngine;

    private Playlist playlist;

    private PlayerEngineListener playerEngineListener;

    private PlayerEngine intentPlayerEngine;

    private String TAG = "AppApplication";

    public void setConcretePlayerEngine(PlayerEngine playerEngine) {
        this.servicePlayerEngine = playerEngine;
    }

    public Playlist fetchPlaylist() {
        return playlist;
    }

    public PlayerEngineListener fetchPlayerEngineListener() {
        return playerEngineListener;
    }

    public PlayerEngine getPlayerEngineInterface() {
        // request service bind
        if (intentPlayerEngine == null) {
            intentPlayerEngine = new IntentPlayerEngine();
        }
        return intentPlayerEngine;
    }

    public void setPlayerEngineListener(PlayerEngineListener l) {
        getPlayerEngineInterface().setListener(l);
    }


    //此处加载全局用到的so库
    /*static {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("Log");
        System.loadLibrary("StreamParser");
        System.loadLibrary("StreamPackage");
        System.loadLibrary("StreamConvertor");
        System.loadLibrary("ConvertInterface");
        System.loadLibrary("AudioPairSDK");
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        mMyApplication = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    private class IntentPlayerEngine implements PlayerEngine {

        @Override
        public void openPlaylist(Playlist playlist) {
            AppApplication.this.playlist = playlist;
            if (servicePlayerEngine != null) {
                servicePlayerEngine.openPlaylist(playlist);
            }
        }

        @Override
        public void reset() {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.reset();
            } else {
                startAction("");
            }
        }

        @Override
        public void build() {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.build();
            } else {
                startAction("");
            }
        }

        @Override
        public void buildByPath(String path, int type) {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.buildByPath(path, type);
            } else {
                startAction("");
            }
        }

        @Override
        public boolean isSingleMusicPlayed() {
            if (servicePlayerEngine != null) {
                return servicePlayerEngine.isSingleMusicPlayed();
            }
            return false;
        }

        @Override
        public void play(int type) {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.play(type);
            } else {
                startAction("");
            }
        }

        @Override
        public long getDuration() {
            if (servicePlayerEngine != null) {
                return servicePlayerEngine.getDuration();
            }
            return 0;

        }

        @Override
        public Playlist getPlaylist() {
            return playlist;
        }

        @Override
        public void play() {
            if (servicePlayerEngine != null) {
                playlistCheck();
                LogUtil.d(TAG, "InternalMediaPlayer build play");
                servicePlayerEngine.play();
            } else {
                startAction(PlayerService.ACTION_PLAY);
            }
        }

        @Override
        public void updatePlayedList(ArrayList<MusicBean> removeMusicBeans) {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.updatePlayedList(removeMusicBeans);
            } else {
                startAction("");
            }
        }

        @Override
        public MusicBean getCurrentSelectedMusic() {
            if (servicePlayerEngine != null) {
                playlistCheck();
                return servicePlayerEngine.getCurrentSelectedMusic();
            }
            return null;
        }

        @Override
        public boolean isPlaying() {
            if (servicePlayerEngine == null) {
                return false;
            } else {
                return servicePlayerEngine.isPlaying();
            }
        }

        @Override
        public boolean isPrepared() {
            if (servicePlayerEngine == null) {
                return false;
            } else {
                return servicePlayerEngine.isPrepared();
            }
        }

        @Override
        public void next() {
            if (servicePlayerEngine != null) {
                playlistCheck();
                servicePlayerEngine.next();
            } else {
                startAction(PlayerService.ACTION_NEXT);
            }
        }

        @Override
        public void completeNext() {
            if (servicePlayerEngine != null) {
                playlistCheck();
                servicePlayerEngine.completeNext();
            } else {
                startAction(PlayerService.ACTION_COMPLETE_NEXT);
            }
        }

        private void startAction(String action) {
            Intent intent = new Intent(AppApplication.this,  PlayerService.class);
            intent.setAction(action);
            /*if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }*/
            startService(intent);
        }

        private void playlistCheck() {
            if (servicePlayerEngine != null) {
                if (servicePlayerEngine.getPlaylist() == null && playlist != null) {
                    servicePlayerEngine.openPlaylist(playlist);
                }
            }
        }

        @Override
        public void stop() {
            startAction(PlayerService.ACTION_STOP);
        }

        @Override
        public void prev() {
            if (servicePlayerEngine != null) {
                playlistCheck();
                servicePlayerEngine.prev();
            } else {
                startAction(PlayerService.ACTION_PREV);
            }
        }

        @Override
        public void pause() {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.pause();
            }
        }

        @Override
        public void skipTo(int index) {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.skipTo(index);
            }
        }

        @Override
        public void setListener(PlayerEngineListener listener) {
            playerEngineListener = listener;
            if (servicePlayerEngine != null || playerEngineListener != null) {
                startAction(PlayerService.ACTION_BIND_LISTENER);
            }
        }

        @Override
        public void setPlaybackMode(Playlist.PlaylistPlaybackMode aMode) {
            playlist.setPlaylistPlaybackMode(aMode);
        }

        @Override
        public Playlist.PlaylistPlaybackMode getPlaybackMode() {
            return playlist.getPlaylistPlaybackMode();
        }

        @Override
        public void forward(int time) {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.forward(time);
            }
        }

        @Override
        public void rewind(int time) {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.rewind(time);
            }
        }

        @Override
        public void seekTo(int progress) {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.seekTo(progress);
            }
        }

        @Override
        public boolean addOrCancelFavMusic(MusicBean musicBean, boolean isAdd) {
            if (servicePlayerEngine != null) {
                return servicePlayerEngine.addOrCancelFavMusic(musicBean, isAdd);
            } else {
                return playlist.addOrCancelFav(musicBean, isAdd);
            }
        }

        @Override
        public void delCurrentSelectedMusicAndPlayNext() {
            if (servicePlayerEngine != null) {
                servicePlayerEngine.delCurrentSelectedMusicAndPlayNext();
            }
        }

        @Override
        public MediaPlayer getCurrentMediaplayer() {
            if (servicePlayerEngine != null) {
                return servicePlayerEngine.getCurrentMediaplayer();
            }
            return null;
        }
    }
}
