package com.android.audiorecorder.ui.data.resp;

import com.android.library.net.resp.DataResp;

import java.util.ArrayList;

public class FriendCircleActiveResp extends DataResp {

	public int _id;//active id
    public int userCode;
    public String nickname;
    public int activeType;
    public String activeText;
    public String activeTextSummary;
    public String linkUrl;// 连接url
    public String activeImage;
    public long activeTime;
    public ArrayList<FriendCircleActiveRemarkResp> activeRemarkList;
    public ArrayList<FriendCircleActiveCommentResp> activeCommentList;

    public int recvUserCode;
}
