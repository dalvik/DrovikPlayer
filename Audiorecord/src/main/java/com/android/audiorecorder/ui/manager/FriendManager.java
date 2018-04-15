package com.android.audiorecorder.ui.manager;

import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.req.FriendCircleFriendDetailReq;
import com.android.audiorecorder.ui.data.req.FriendCircleFriendSummaryReq;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendDetailResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendSummaryResp;
import com.android.audiorecorder.utils.URLS;
import com.android.library.net.base.IDataCallback;
import com.android.library.net.manager.JSONHttpDataManager;
import com.android.library.net.req.DataReq;
import com.android.library.net.utils.JSONType;

import java.util.ArrayList;

public class FriendManager extends JSONHttpDataManager<BaseData<FriendCircleFriendDetailResp>, DataReq> {

	private static final String URL = URLS.DOMAIN + "action/api/br/view/friend.php";
	private static final String NAME_SPACE = "http://webservice.dhsoft.com";

    private static final String METHOD_GET_FRIEND_CIRCLE_FRIEND_LIST = "getFriendCircleFriendList";
    private static final String METHOD_GET_FRIEND_CIRCLE_FRIEND_DETAIL = "getFriendCircleFriendDetail";
    private static final String METHOD_DELETE_FRIEND_CIRCLE_FRIEND = "deleteFriendCircleFriend";
    
    public FriendManager(IDataCallback callback) {
        super(callback);
    }

    @Override
    protected JSONType<BaseData<FriendCircleFriendDetailResp>> initRespType() {
            return new JSONType<BaseData<FriendCircleFriendDetailResp>>() {
        };
    }
    
    /**
     * get friend circle friend list
     * @param userCode
     * @param userType
     * @param page
     * @param offset
     * @return
     */
    public int getFriendList(int userCode, int userType, int page, int offset){
        FriendCircleFriendSummaryReq req = new FriendCircleFriendSummaryReq();
        req.userCode = userCode;
        req.userType = userType;
        req.length = page;
        req.offset = offset;
        return doPostRequest(URL, METHOD_GET_FRIEND_CIRCLE_FRIEND_LIST, req, new JSONType<BaseData<ArrayList<FriendCircleFriendSummaryResp>>>(){});
    }
    
    /**
     * get friend circle friend detail
     * @param userId
     * @return
     */
    public int getFriendDetail(int userId){
        FriendCircleFriendDetailReq req = new FriendCircleFriendDetailReq();
        req.userId = userId;
        return doPostRequest(URL, METHOD_GET_FRIEND_CIRCLE_FRIEND_DETAIL, req, new JSONType<BaseData<ArrayList<FriendCircleFriendDetailResp>>>(){});
    }
    
    
    /**
     * fetch friend info
     * @param userCode
     * @return
     */
    public int getFriendCircleFriend(int userCode){
        FriendCircleFriendSummaryReq req = new FriendCircleFriendSummaryReq();
        req.userCode = userCode;
        return doPostRequest(URL, METHOD_GET_FRIEND_CIRCLE_FRIEND_LIST, req);
    }
    
    /**
     * delete friend from friend list
     * @param userCode
     * @param friendCode
     * @return
     */
    public int deleteFriendCircleFriend(int userCode, int friendCode){
        FriendCircleFriendSummaryReq req = new FriendCircleFriendSummaryReq();
        req.userCode = userCode;
        req.friendCode = friendCode;
        return doPostRequest(URL, METHOD_DELETE_FRIEND_CIRCLE_FRIEND, req);
    }
}
