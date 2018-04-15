package com.android.audiorecorder.ui.data.req;

import com.android.library.net.req.DataReq;

public class RegisterReq extends DataReq {
    // 用户ID
    public Long userId;
    // 手机号
    public String mobile;
    // 验证码
    public String captcha;

    public Integer sex;

    // YYYY-MM-DD
    public String birthday;

    public String password;

    // 手机号验证类型
    public String checkType;
    // 头像
    public String headIcon;
    
    //注册类型 0 tel, 1 email
    public int type;
}
