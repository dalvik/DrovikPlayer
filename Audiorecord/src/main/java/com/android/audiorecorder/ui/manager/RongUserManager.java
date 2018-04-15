package com.android.audiorecorder.ui.manager;

import com.android.audiorecorder.ui.data.RongBaseData;
import com.android.audiorecorder.ui.data.req.RongUserReq;
import com.android.audiorecorder.ui.data.resp.RongUserResp;
import com.android.audiorecorder.utils.URLS;
import com.android.library.net.base.IDataCallback;
import com.android.library.net.manager.JSONHttpDataManager;
import com.android.library.net.utils.JSONType;

public class RongUserManager extends JSONHttpDataManager<RongBaseData<RongUserResp>, RongUserReq> {

    private static final String RONG_USER = URLS.DOMAIN + "action/api/br/model/RongcloudManager.php";
    
    public RongUserManager(IDataCallback callback) {
        super(callback);
    }

    @Override
    protected JSONType<RongBaseData<RongUserResp>> initRespType() {
        return new JSONType<RongBaseData<RongUserResp>>() {
        };
    }
    
    public int getUserToken(int userCode, String name, String headerIcon){
        RongUserReq req = new RongUserReq();
        req.userCode = userCode;
        req.name = name;
        req.portraitUri = headerIcon;
        return doPostRequest(RONG_USER, "getToken", req);
    }
    
    public int refreshUser(int userCode, String name, String headerIcon){
        RongUserReq req = new RongUserReq();
        req.userCode = userCode;
        req.name = name;
        req.portraitUri = headerIcon;
        return doPostRequest(RONG_USER + "getToken", "", req);
    }
    
    public int checkUserOnline(){
        
        return 0;
    }
    
    public int blockUser(){
        return 0;
    }
    
    public int unBlockUser(){
        
        return 0;
    }
    
    public int queryBlockUser(){
        
        return 0;
    }
    
    public int addBlackList(){
        
        return 0;
    }
    
    public int removeBlackList(){
        return 0;
    }
    
    public int queryBalckList(){
        
        return 0;
    }
}
