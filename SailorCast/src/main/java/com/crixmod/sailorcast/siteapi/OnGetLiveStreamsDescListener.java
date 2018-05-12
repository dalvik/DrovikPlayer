package com.crixmod.sailorcast.siteapi;

import com.crixmod.sailorcast.model.SCFailLog;
import com.crixmod.sailorcast.model.SCLiveStream;
import com.crixmod.sailorcast.model.SCLiveStreams;

/**
 * Created by fire3 on 15-3-9.
 */
public interface OnGetLiveStreamsDescListener {
    public void onGetLiveStreamsDescSuccess(SCLiveStreams streams);
    public void onGetLiveStreamsDescFailed(SCFailLog failReason);
}
