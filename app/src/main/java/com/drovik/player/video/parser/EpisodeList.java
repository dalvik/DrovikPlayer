package com.drovik.player.video.parser;

import android.util.Log;

import java.util.ArrayList;

public class EpisodeList extends ArrayList<Episode> {
    private static final String TAG = "EpisodeList";
    public void debugLog() {
        for (Episode a : this) {
            Log.d(TAG, a.toString());
        }
    }
}
