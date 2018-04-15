package com.android.audiorecorder.ui.data.resp;

import com.android.library.net.resp.DataResp;

public class HisPersonalCenterResp extends DataResp {

public long id;
    
    public long userCode;
    
    public String realName;
    
    public String email;
    
    public String nickName;
    
    public int sex =2;
    
    public String headIcon;

    public String signature;
    
    public float balance;
    
    public String rongYunToken;
    
    public int userStatus;

    public int type;
    
    public long groupId;
    
    public int status;
    
    /**
     * 用户信息
     */
    public UserResp dbUsers;
    
    /**
     * 评论列表
     */
    //public ArrayList<AppraiseResp> praiseList;

    /**
     * 急速约
     */
    //public SpeedDating userSpeedDating;

    /**
     * 好评率
     */

    public String userPariseRate;


    /**
     * 人品
     */
    public String pariseRate;

    /**
     * 是否是好友:true是 false不是
     */
    public boolean existFriends;
    /**
     * 关注ID
     */

    public long followerId;
    
}
