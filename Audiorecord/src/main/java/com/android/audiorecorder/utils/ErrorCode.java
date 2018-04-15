package com.android.audiorecorder.utils;

public final class ErrorCode {

    public final static int SUCCESS = 200;
    public final static int ERROR = -1006;//内部错误
    
    public final static int USER_LOCKED = -1001;//用户被锁定
    public final static int USER_NOT_EXISTS = -1002;//用户不存在
    public final static int USER_DESTORY = -1003;//用户已注销
    public final static int USER_NOT_ACTIVE = -1004;//未激活
    public final static int USER_ACTIVE = 1000;//已激活
    public final static int USER_ACTIVED = 1005;//已注册，请登录
    public final static int USER_ACTIVE_ERROR = -1006;//激活失败
    public final static int USER_INVITE_OUTOFF_TIME = -1007;//邀请码超期
    public final static int USER_INVITE_NOT_EXIST = -1008;//邀请码错误
    public final static int USER_INVITE_ERROR = -1014;//邀请失败
    public final static int USER_LOGIN_ERROR = -1010;//用户名或密码错误
    public final static int USER_UPDATE_ERROR = -1011;//用户信息更新失败
    public final static int USER_QUERY_ERROR = -1012;//用户信息查询失败
    public final static int USER_ISREGISTER_ERROR = -1013;//检查用户是否注册失败
}
