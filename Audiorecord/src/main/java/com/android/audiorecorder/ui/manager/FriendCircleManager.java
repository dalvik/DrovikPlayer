package com.android.audiorecorder.ui.manager;

import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.req.FriendCircleActiveCommentReq;
import com.android.audiorecorder.ui.data.req.FriendCircleActivePublishReq;
import com.android.audiorecorder.ui.data.req.FriendCircleActiveRemarkReq;
import com.android.audiorecorder.ui.data.req.FriendCircleActiveReq;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveCommentResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActivePublishResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveRemarkResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendDetailResp;
import com.android.audiorecorder.utils.URLS;
import com.android.library.net.base.IDataCallback;
import com.android.library.net.manager.JSONHttpDataManager;
import com.android.library.net.req.DataReq;
import com.android.library.net.utils.JSONType;

import java.util.ArrayList;

public class FriendCircleManager extends JSONHttpDataManager<BaseData<FriendCircleFriendDetailResp>, DataReq> {

    private static final String URL = URLS.DOMAIN + "action/api/br/view/friendcircle.php";
    private static final String NAME_SPACE = "http://webservice.dhsoft.com";
    private static final String METHOD_ADD_FRIEND_CIRCLE_ACTIVE = "addFriendCircleActive";
    private static final String METHOD_DELETE_FRIEND_CIRCLE_ACTIVE = "deleteFriendCircleActive";
    private static final String METHOD_GET_FRIEND_CIRCLE_ACTIVE_LIST = "getFriendCircleActiveList";
    private static final String METHOD_ADD_FRIEND_CIRCLE_ACTIVE_REMARK = "addFriendCircleActiveRemark";
    private static final String METHOD_CANCEL_FRIEND_CIRCLE_ACTIVE_REMARK = "cancelFriendCircleActiveRemark";
    private static final String METHOD_GET_FRIEND_CIRCLE_REMARK_LIST = "getFriendCircleActiveRemarkList";
    private static final String METHOD_GET_FRIEND_CIRCLE_ACTIVE_COMMENT = "addFriendCircleActiveComment";
    private static final String METHOD_DELETE_FRIEND_CIRCLE_ACTIVE_COMMENT = "deleteFriendCircleActiveComment";
    private static final String METHOD_GET_FRIEND_CIRCLE_ACTIVE_COMMENT_LIST = "getFriendCircleActiveCommentList";
    
    public FriendCircleManager(IDataCallback callback) {
        super(callback);
    }

    @Override
    protected JSONType<BaseData<FriendCircleFriendDetailResp>> initRespType() {
            return new JSONType<BaseData<FriendCircleFriendDetailResp>>() {
        };
    }
    
    /**
     * publish active
     * @param usercode
     * @param activeType
     * @param activeText
     * @param activeImage
     * @return
     */
    public int addFriendCircleActive(int usercode, int activeType, String activeText, String activeImage){
    	FriendCircleActivePublishReq req = new FriendCircleActivePublishReq();
    	req.userCode = usercode;
    	req.activeType = activeType;
    	req.activeText = activeText;
    	req.activeImage = activeImage;
    	return doPostRequest(URL, METHOD_ADD_FRIEND_CIRCLE_ACTIVE, req, new JSONType<BaseData<FriendCircleActivePublishResp>>(){});
    }
    
    /**
     * delete active by id
     * @param activeId
     * @return
     */
    public int deleteFriendCircleActive(int activeId){
    	FriendCircleActivePublishReq req = new FriendCircleActivePublishReq();
    	req._id = activeId;
    	return doPostRequest(URL, METHOD_DELETE_FRIEND_CIRCLE_ACTIVE, req, new JSONType<BaseData<FriendCircleActivePublishResp>>(){});
    }
    
    /**
     * get friend circle active list
     * @param userCode
     * @param userType
     * @param pageNumber
     * @param offset
     * @return
     */
    public int getFriendCircleActiveList(int userCode, int userType, int _id, int pageNumber, int offset){
        FriendCircleActiveReq req = new FriendCircleActiveReq();
        req.userCode = userCode;
        req.userType = userType;
        req.length = pageNumber;
        req.offset = offset;
        req._id = _id;
        return doPostRequest(URL, METHOD_GET_FRIEND_CIRCLE_ACTIVE_LIST, req, new JSONType<BaseData<ArrayList<FriendCircleActiveResp>>>(){});
    }

    /**
     * add new active remark
     * @param userCode
     * @param activePraise
     * @return
     */
    public int addFriendCircleActiveRemark(int userCode, int activeId, int activePraise){
    	FriendCircleActiveRemarkReq req = new FriendCircleActiveRemarkReq();
    	req.userCode = userCode;
    	req.activeId = activeId;
    	req.activePraise = activePraise;
    	return doPostRequest(URL, METHOD_ADD_FRIEND_CIRCLE_ACTIVE_REMARK, req, new JSONType<BaseData<FriendCircleActivePublishResp>>(){});
    }
    
    /**
     * remove active remark
     * @param remarkId
     * @param activePraise
     * @return
     */
    public int cancelFriendCircleActiveRemark(int remarkId, int activePraise){
    	FriendCircleActiveRemarkReq req = new FriendCircleActiveRemarkReq();
    	req.activeId = remarkId;
    	req.activePraise = activePraise;
    	return doPostRequest(URL, METHOD_CANCEL_FRIEND_CIRCLE_ACTIVE_REMARK, req, new JSONType<BaseData<FriendCircleActiveRemarkResp>>(){});
    }
    
    /**
     * get active remark list
     * @param activeId
     * @param perPageNumber
     * @param offset
     * @return
     */
    public int getFriendCircleActiveRemarkList(int activeId, int perPageNumber, int offset){
    	FriendCircleActiveRemarkReq req = new FriendCircleActiveRemarkReq();
    	req.activeId = activeId;
    	req.length = perPageNumber;
    	req.offset = offset;
    	return doPostRequest(URL,METHOD_GET_FRIEND_CIRCLE_REMARK_LIST, req, new JSONType<BaseData<ArrayList<FriendCircleActiveRemarkResp>>>(){});
    }
    
    /**
     * add new active comment
     * @param activeId
     * @param userCode
     * @param activeComment
     * @param recvUserCode
     * @return
     */
    public int addFriendCircleActiveComment(int activeId, int userCode, String activeComment, int recvUserCode){
    	FriendCircleActiveCommentReq req = new FriendCircleActiveCommentReq();
    	req.activeId = activeId;
    	req.userCode = userCode;
    	req.activeComment = activeComment;
    	req.recvUserCode = recvUserCode;
    	return doPostRequest(URL, METHOD_GET_FRIEND_CIRCLE_ACTIVE_COMMENT, req, new JSONType<BaseData<FriendCircleActiveCommentResp>>(){});
    }
    
    /**
     * delete active comment by id
     * @param commentId
     * @return
     */
    public int deleteFriendCircleActiveComment(int commentId){
    	FriendCircleActiveCommentReq req = new FriendCircleActiveCommentReq();
    	req.commentId = commentId;
    	return doPostRequest(URL, METHOD_DELETE_FRIEND_CIRCLE_ACTIVE_COMMENT, req, new JSONType<BaseData<FriendCircleActiveCommentResp>>(){});
    }
    
    /**
     * get friend circle active comment list
     * @param activeId
     * @param perPageNumber
     * @param offset
     * @return
     */
    public int getFriendCircleActiveCommentList(int activeId, int perPageNumber, int offset){
    	FriendCircleActiveCommentReq req = new FriendCircleActiveCommentReq();
    	req.activeId = activeId;
    	return doPostRequest(URL, METHOD_GET_FRIEND_CIRCLE_ACTIVE_COMMENT_LIST, req, new JSONType<BaseData<ArrayList<FriendCircleActiveCommentResp>>>(){});
    }
    
}
