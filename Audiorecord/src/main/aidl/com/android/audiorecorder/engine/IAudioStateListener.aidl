package com.android.audiorecorder.engine;

interface IAudioStateListener {

	void onStateChanged(int state); // 0 stoped   1 started 2 paused
	
}