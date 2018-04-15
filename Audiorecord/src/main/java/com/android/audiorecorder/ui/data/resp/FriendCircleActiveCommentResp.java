package com.android.audiorecorder.ui.data.resp;

import com.android.library.net.resp.DataResp;

public class FriendCircleActiveCommentResp extends DataResp {

	public int _id;//comment id
	public int userCode;//user code
	public String nickname;// user name
	public String headIcon;
    public int activeId;// active id
    public int recvUserCode;//recv user code
    public String recvNickname;// recv user name
    public String activeComment;// comment content
    public long activeCommentTime;//comment time
    
}
