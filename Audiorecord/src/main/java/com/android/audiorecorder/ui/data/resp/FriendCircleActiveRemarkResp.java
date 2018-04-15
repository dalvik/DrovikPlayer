package com.android.audiorecorder.ui.data.resp;

import com.android.library.net.resp.DataResp;

public class FriendCircleActiveRemarkResp extends DataResp {

    public int _id;// remark id
    public int userCode;// user code
    public String nickname;// user name
    public String headIcon;//head icon
    public int activePraise;//exists praised
    public long activePraiseTime;// remark time
    
}
