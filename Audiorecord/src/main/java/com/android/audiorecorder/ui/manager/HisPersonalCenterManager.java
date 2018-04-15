package com.android.audiorecorder.ui.manager;

import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.PersonalDetailBaseData;
import com.android.audiorecorder.ui.data.req.HisPersonalNewReq;
import com.android.audiorecorder.ui.data.req.PersonalAddNewsReq;
import com.android.audiorecorder.ui.data.req.PersonalNewsReq;
import com.android.audiorecorder.ui.data.resp.HisPersonalNewsResp;
import com.android.audiorecorder.ui.data.resp.PersonalNewsResp;
import com.android.audiorecorder.utils.URLS;
import com.android.library.net.base.IDataCallback;
import com.android.library.net.manager.JSONHttpDataManager;
import com.android.library.net.req.DataReq;
import com.android.library.net.utils.JSONType;

import java.util.ArrayList;

public class HisPersonalCenterManager extends JSONHttpDataManager<BaseData<HisPersonalNewsResp>, DataReq> {

    private static final String PERSONAL_NEWS = URLS.DOMAIN + "action/api/br/view/personal_news.php";
    
    public HisPersonalCenterManager(IDataCallback callback) {
        super(callback);
    }

    @Override
    protected JSONType<BaseData<HisPersonalNewsResp>> initRespType() {
        return new JSONType<BaseData<HisPersonalNewsResp>>() {
        };
    }
    
    public int getUserDetailByUserId(int userId){
    	PersonalNewsReq req = new PersonalNewsReq();
        req.userId = userId;
        return doPostRequest(PERSONAL_NEWS, "", req);
    }
    
    public int refreshUser(int userId, String name, String headerIcon){
    	PersonalNewsReq req = new PersonalNewsReq();
        req.userId = userId;
       // req.name = name;
        //req.portraitUri = headerIcon;
        return doPostRequest(PERSONAL_NEWS, "", req);
    }
    
    public int getPersionalThumbnailList(int userId){
    	PersonalNewsReq req = new PersonalNewsReq();
        req.userId = userId;
        return doPostRequest(PERSONAL_NEWS, "getPersonalNewsThumbnail", req, new JSONType<BaseData<PersonalDetailBaseData<ArrayList<PersonalNewsResp>>>>() {
		});
    }
    
    public int getPersonalUserNewList(int userId, int offset){
    	HisPersonalNewReq req = new HisPersonalNewReq();
        req.userId = userId;
        req.offset = offset;
        return doPostRequest(PERSONAL_NEWS, "getPersonalNewsList", req, new JSONType<BaseData<ArrayList<PersonalNewsResp>>>() {
		});
    }
    
    public int addPersonalNews(int userId, int newsType, String newsContent, String imgSrc){
    	PersonalAddNewsReq req = new PersonalAddNewsReq();
        req.userId = userId;
        req.newsType = newsType;
        req.newsContent = newsContent;
        req.imgSrc = imgSrc;
        return doPostRequest(PERSONAL_NEWS, "addPersonalNews", req);
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
