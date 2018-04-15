package com.android.audiorecorder.engine;

import com.android.audiorecorder.engine.IAudioStateListener;
	
interface IAudioService{

	void startRecord();
	
	void stopRecord();
	
	void regStateListener(IAudioStateListener listener);
	
	void unregStateListener(IAudioStateListener listener);
	
	long getRecorderDuration();
	
	int getMaxAmplitude();
	
	int getAudioRecordState();
	
	void setMode(int mode);
	
	int getMode();
	
	void adjustStreamVolume(int streamType, int direct, int flag);
	
	long checkDiskCapacity();
	
	void startAutoMode();
	
	void stopAudioMode();

	void release();
	
}