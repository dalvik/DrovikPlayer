package com.android.audiorecorder.ui.manager;

import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.req.FriendCircleFriendSummaryReq;
import com.android.audiorecorder.ui.data.req.RegisterReq;
import com.android.audiorecorder.ui.data.req.UserReq;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendDetailResp;
import com.android.audiorecorder.ui.data.resp.RegisterResp;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.utils.DateUtil;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.URLS;
import com.android.library.net.base.IDataCallback;
import com.android.library.net.manager.JSONHttpDataManager;
import com.android.library.net.req.DataReq;
import com.android.library.net.utils.JSONType;
import com.android.library.utils.MD5;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserManager extends JSONHttpDataManager<BaseData<RegisterResp>, DataReq> {

    public static final String DOMAIN = "";
    public static final int FAIL_USERNAME_OR_PWD_ERROR = 10003;
    public static final int FAIL_THIRD_ON_REGISTER = 10501;
    public static final int FAIL_VCODE = 413;
    public static final int FAIL_REGEISTERED = 10005;
    public static final int FAIL_USER_PWD_READY_SET = 10013;
    private static final String METHOD_USER_ISREGISTERED = "isRegisted";
    private static final String METHOD_USER_REGISTERE = "userRegister";
    private static final String URL = URLS.URL;
	private static final String METHOD_USER_LOGIN = "userLogin";
    private static final String METHOD_UPDATE_USER = "userUpdate";
    private static final String METHOD_USER_FORGET_PASSWORD = "userForgetPassword";
    private static final String METHOD_SEARCH_USER = "searchUser";
    private static final String METHOD_ADD_FRIEND = "addFriend";
    public static final int SEX_MALE = 0;
    public static final int SEX_FEMALE = 1;
    
    
    private String TAG = "UserManager";
    
    public UserManager(IDataCallback callback) {
        super(callback);
    }

    @Override
    protected JSONType<BaseData<RegisterResp>> initRespType() {
        return new JSONType<BaseData<RegisterResp>>() {
        };
    }
    
    /**
     * 
     * @title: isRegisted 
     * @param account:mobile or email
     * @return int
     */
    public int isRegisted(String account){
        RegisterReq req = new RegisterReq();
        req.mobile = account;
        req.checkType = "official";
        return doPostRequest(URL, METHOD_USER_ISREGISTERED, req, new JSONType<BaseData<RegisterResp>>(){});
    }
    
    public int register(String account, String vcode, int type, String password){
        RegisterReq req = new RegisterReq();
        req.mobile = account;
        req.captcha = vcode;
        req.type = type;
        req.password = password;
        return doPostRequest(URL, METHOD_USER_REGISTERE, req, new JSONType<BaseData<RegisterResp>>(){});
    }

    public int login(String acount, String password){
        RegisterReq req = new RegisterReq();
        req.mobile = acount;
        req.password = password;
        return doPostRequest(URL, METHOD_USER_LOGIN, req, new JSONType<BaseData<UserResp>>(){});
    }
    
    public int sendVCode(String mobile){
        return 0;
    }
    
    public int findPwd(String mobile, String captcha, String pwd) {
        RegisterReq req = new RegisterReq();
        req.mobile = mobile;
        req.captcha = captcha;
        req.password = MD5.toLowMD5String(pwd);
        return doPostRequest(URL, METHOD_USER_FORGET_PASSWORD, req);
    }
    
    public int getUser(long userId){
        
        return 0;
    }
    
    public int searchNewUser(String keyWords){
        RegisterReq req = new RegisterReq();
        req.mobile = keyWords;
        return doPostRequest(URL, METHOD_SEARCH_USER, req, new JSONType<BaseData<ArrayList<FriendCircleFriendDetailResp>>>(){});
    }
    
    public int addFriend(int userCode, int userType, int friendCode){
        FriendCircleFriendSummaryReq req = new FriendCircleFriendSummaryReq();
        req.userCode = userCode;
        req.userType = userType;
        req.friendCode = friendCode;
        return doPostRequest(URL, METHOD_ADD_FRIEND, req);
    }
    
    public int updateUser(int userId, String json) {
        UserReq req = new UserReq();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            req.userCode = userId;
            if(jsonObject.has("mobile")){
                String mobile = jsonObject.optString("mobile");
                req.telephone = mobile;
            }
            if(jsonObject.has("sex")){
                int sex = jsonObject.optInt("sex");
                req.sex = sex;
            }
            if(jsonObject.has("birthday")){
                String birthday = jsonObject.optString("birthday");
                req.birthday = DateUtil.getDateByYMD(birthday).getTime();
            }
            if(jsonObject.has("password")){
                String pwd = jsonObject.optString("password");
                //req.password = MD5.toLowMD5String(pwd);
            }
            if(jsonObject.has("signature")){
                req.signature = jsonObject.optString("signature");
            }
            /*if(jsonObject.has("")){
                
            }*/
            if(jsonObject.has("headIcon")){
                String headIcon = jsonObject.optString("headIcon");
                req.headIcon = headIcon;
                LogUtil.d(TAG, "updateUser: headIcon = " + headIcon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return doPostRequest(URL, METHOD_UPDATE_USER, req, new JSONType<BaseData<UserResp>>(){});
    }
    
    public int updateUser(int userId, UserResp resp){
        UserReq req = new UserReq();
        req.userCode = userId;
        req.nickName = resp.nickName;
        req.birthday = resp.birthday;
        req.height = resp.height;
        req.weight = resp.weight;
        req.cityCode = resp.cityCode;
        req.company = resp.company;
        req.vocation = resp.vocation;
        req.school = resp.school;
        req.signature = resp.signature;
        req.technique = resp.technique;
        req.interest = resp.interest;
        req.rich = resp.rich;
        return doPostRequest(URL, "userUpdate", req, new JSONType<BaseData<UserResp>>(){});
    }
}
