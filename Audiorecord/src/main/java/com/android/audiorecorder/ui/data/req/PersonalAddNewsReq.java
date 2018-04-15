package com.android.audiorecorder.ui.data.req;

import com.android.library.net.req.DataReq;

public class PersonalAddNewsReq extends DataReq {

    public int userId;
    
    public int newsType;
    
    public String newsContent;
    
    public String imgSrc;
    
    public long newsTime;
}
