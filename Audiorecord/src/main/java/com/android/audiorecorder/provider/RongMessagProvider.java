package com.android.audiorecorder.provider;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

import com.android.audiorecorder.AppContext;
import com.android.audiorecorder.dao.UserDao;
import com.android.audiorecorder.engine.AbstractRongMessage;
import com.android.audiorecorder.engine.IRongMessageListener;
import com.android.audiorecorder.ui.data.RongBaseData;
import com.android.audiorecorder.ui.data.resp.RongUserResp;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.RongUserManager;
import com.android.audiorecorder.utils.LogUtil;
import com.android.library.net.base.IDataCallback;

public class RongMessagProvider extends AbstractRongMessage implements IDataCallback {

	private String mToken;
	private int whatGetToken;
	private RongUserManager mRongUserManager;
	private IRongMessageListener mRongMessageListener;
    private Context mContext;
    private UserResp mUserResp;
    private UserDao mUserDao;
	private String TAG = "RongMessagProvider";
	
	public RongMessagProvider(Context context){
		this.mContext = context;
		mRongUserManager = new RongUserManager(this);
		mUserDao = new UserDao();
	}
	
	@Override
	public void connect() throws RemoteException {
		loadUserInfo();
	}

	@Override
	public void refreshToken(int userId, String name, String headerIcon) throws RemoteException {
		gotoFetchToken(userId, name, headerIcon);
	}
	
	@Override
	public String getToken() throws RemoteException {
		return mToken;
	}

	@Override
	public void registerRongMessageListener(IRongMessageListener listener) throws RemoteException {
		this.mRongMessageListener = listener;
	}

	@Override
	public void unRegisterRongMessageListener(IRongMessageListener listener) throws RemoteException {
		this.mRongMessageListener = null;
	}

	@Override
	public void onCallback(int what, int arg1, int arg2, Object obj) {
		LogUtil.d(TAG, "==> what = " + what + " arg1 = " + arg1 + " arg2 = " + arg2 + " obj = " + obj);
		if(whatGetToken == what){
			switch(arg1){
			 case RongUserManager.RESULT_SUCCESS:
				 RongBaseData baseData = (RongBaseData) obj;
				 RongUserResp data = (RongUserResp) baseData.data;
				 if(data != null){
					 mToken = data.token;
					 LogUtil.i(TAG, "==> token = " + data.token + " " + data.userId + " "+ data.code);
				 }
                 break;
             case RongUserManager.RESULT_FAILED:
            	 LogUtil.e(TAG, "==> get token error");
                 break;
             default:
			}
		}
	}
	
	private void gotoFetchToken(int userCode, String name, String headIcon){
		whatGetToken = mRongUserManager.getUserToken(userCode, name, headIcon);
	}
	
	private void loadUserInfo(){
		int tempUserId = 0;
		String tempName = null;
		String tempHeadIcon = null;
		mUserResp = mUserDao.getUser(mContext);
		if(mUserResp != null){
			tempUserId = mUserResp.userCode;
			tempName = mUserResp.nickName;
			tempHeadIcon = mUserResp.headIcon;
		}
		gotoFetchToken(tempUserId, tempName, tempHeadIcon);
	}
}
