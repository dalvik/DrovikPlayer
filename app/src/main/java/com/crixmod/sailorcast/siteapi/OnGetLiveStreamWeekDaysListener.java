package com.crixmod.sailorcast.siteapi;

import com.crixmod.sailorcast.model.SCFailLog;
import com.crixmod.sailorcast.model.SCLiveStream;

/**
 * Created by fire3 on 15-3-9.
 */
public interface OnGetLiveStreamWeekDaysListener {
    public void onGetLiveStreamWeekDaysSuccess(SCLiveStream stream);
    public void onGetLiveStreamWeekDaysFailed(SCFailLog failReason);
}
