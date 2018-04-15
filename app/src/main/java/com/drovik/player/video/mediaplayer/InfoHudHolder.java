package com.drovik.player.video.mediaplayer;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaPlayerProxy;

public class InfoHudHolder {
    private IMediaPlayer mMediaPlayer;
    private long mLoadCost = 0;
    private long mSeekCost = 0;

    private String TAG = "InfoHudHolder";

    public InfoHudHolder(Context context) {
    }


    public void setMediaPlayer(IMediaPlayer mp) {
        mMediaPlayer = mp;
        if (mMediaPlayer != null) {
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 500);
        } else {
            mHandler.removeMessages(MSG_UPDATE_HUD);
        }
    }

    private static String formatedDurationMilli(long duration) {
        if (duration >=  1000) {
            return String.format(Locale.US, "%.2f sec", ((float)duration) / 1000);
        } else {
            return String.format(Locale.US, "%d msec", duration);
        }
    }

    private static String formatedSpeed(long bytes, long elapsed_milli) {
        if (elapsed_milli <= 0) {
            return "0 B/s";
        }

        if (bytes <= 0) {
            return "0 B/s";
        }

        float bytes_per_sec = ((float)bytes) * 1000.f /  elapsed_milli;
        if (bytes_per_sec >= 1000 * 1000) {
            return String.format(Locale.US, "%.2f MB/s", ((float)bytes_per_sec) / 1000 / 1000);
        } else if (bytes_per_sec >= 1000) {
            return String.format(Locale.US, "%.1f KB/s", ((float)bytes_per_sec) / 1000);
        } else {
            return String.format(Locale.US, "%d B/s", (long)bytes_per_sec);
        }
    }

    public void updateLoadCost(long time)  {
        mLoadCost = time;
    }

    public void updateSeekCost(long time)  {
        mSeekCost = time;
    }

    private static String formatedSize(long bytes) {
        if (bytes >= 100 * 1000) {
            return String.format(Locale.US, "%.2f MB", ((float)bytes) / 1000 / 1000);
        } else if (bytes >= 100) {
            return String.format(Locale.US, "%.1f KB", ((float)bytes) / 1000);
        } else {
            return String.format(Locale.US, "%d B", bytes);
        }
    }

    private static final int MSG_UPDATE_HUD = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_HUD: {
                    IjkMediaPlayer mp = null;
                    if (mMediaPlayer == null)
                        break;
                    if (mMediaPlayer instanceof IjkMediaPlayer) {
                        mp = (IjkMediaPlayer) mMediaPlayer;
                    } else if (mMediaPlayer instanceof MediaPlayerProxy) {
                        MediaPlayerProxy proxy = (MediaPlayerProxy) mMediaPlayer;
                        IMediaPlayer internal = proxy.getInternalMediaPlayer();
                        if (internal != null && internal instanceof IjkMediaPlayer)
                            mp = (IjkMediaPlayer) internal;
                    }
                    if (mp == null)
                        break;

                    int vdec = mp.getVideoDecoder();
                    String vDesStr;
                    switch (vdec) {
                        case IjkMediaPlayer.FFP_PROPV_DECODER_AVCODEC:
                            vDesStr = "avcodec";
                            break;
                        case IjkMediaPlayer.FFP_PROPV_DECODER_MEDIACODEC:
                            vDesStr = "MediaCodec";
                            break;
                        default:
                            vDesStr = "";
                            break;
                    }

                    float fpsOutput = mp.getVideoOutputFramesPerSecond();
                    float fpsDecode = mp.getVideoDecodeFramesPerSecond();
                    String fps = String.format(Locale.US, "%.2f/%.2f", fpsDecode, fpsOutput);
                    long videoCachedDuration = mp.getVideoCachedDuration();
                    long audioCachedDuration = mp.getAudioCachedDuration();
                    long videoCachedBytes    = mp.getVideoCachedBytes();
                    long audioCachedBytes    = mp.getAudioCachedBytes();
                    long tcpSpeed            = mp.getTcpSpeed();
                    long bitRate             = mp.getBitRate();
                    long seekLoadDuration    = mp.getSeekLoadDuration();
                    String v_cache = String.format(Locale.US, "%s, %s", formatedDurationMilli(videoCachedDuration), formatedSize(videoCachedBytes));
                    String a_cache = String.format(Locale.US, "%s, %s", formatedDurationMilli(audioCachedDuration), formatedSize(audioCachedBytes));
                    String load_cost = String.format(Locale.US, "%d ms", mLoadCost);
                    String seek_cost =  String.format(Locale.US, "%d ms", mSeekCost);
                    String seek_load_cost = String.format(Locale.US, "%d ms", seekLoadDuration);
                    String tcp_speed = String.format(Locale.US, "%s", formatedSpeed(tcpSpeed, 1000));
                    String bit_rate = String.format(Locale.US, "%.2f kbs", bitRate/1000f);
                    Log.d(TAG, "vDesStr: " + vDesStr + ", fps: " + fps + ", v_cache: " + v_cache + ", a_cache: " + a_cache + ", bit_rate: " + bit_rate);
                    Log.d(TAG, "load_cost: " + load_cost + ", seek_cost: " + seek_cost + ", seek_load_cost: " + seek_load_cost + ", tcp_speed: " + tcp_speed +"\n");
                    mHandler.removeMessages(MSG_UPDATE_HUD);
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_HUD, 1000);
                }
            }
        }
    };
}
