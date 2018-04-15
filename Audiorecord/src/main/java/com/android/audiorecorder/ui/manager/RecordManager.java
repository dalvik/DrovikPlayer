package com.android.audiorecorder.ui.manager;

import com.android.library.net.base.IDataCallback;
import com.android.library.net.manager.JSONWebServiceDataManager;

public class RecordManager extends JSONWebServiceDataManager {

    public RecordManager(IDataCallback callback) {
        super(callback);
    }
    
}
