package com.android.audiorecorder.engine;

import com.android.audiorecorder.engine.IRongMessageListener;

interface IRongMessage{

	void connect();
	
    void refreshToken(int userCode, String name, String headerIcon);
    
	String getToken();
	
	void registerRongMessageListener(IRongMessageListener listener);
	
	void unRegisterRongMessageListener(IRongMessageListener listener);
}