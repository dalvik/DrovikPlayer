package com.android.audiorecorder.ui.data;

import com.android.library.net.req.DataReq;

public class BasePageReq extends DataReq {

	/**** perpage number size ****/
	public Integer length = 22;
	
	/***** page offset ****/
	public Integer offset;
	
	public String nextKeywords;
}
