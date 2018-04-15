package com.android.audiorecorder.ui.pager;

import android.net.Uri;
import android.util.Log;

import com.android.audiorecorder.AppContext;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.StringUtils;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class MainRongMessagePager extends ConversationListFragment{

	private String default_token = "SMe7A5D+t+Lyn7vQ5y1HOQuEIRygUVvyaA8L1xPxceLmeU+wR9AEUgbOhhA94msPtidhee8TQRC9DwYF99TLKg==";
	
	private String davmb = "pZt0HVKhGehDhyjSjH+miuUwBx0Q+Y7jt0TjSvRh5vI1kyE15/Fr4AMvh09m056E9mZGMxRaPqpZe1tzu1MeVw==";
	
	private String TAG = "MainMicRecordPager";
	
    public MainRongMessagePager() {
    	Log.d(TAG, "init MainMicRecordPager");
    	isReconnect();
    	System.out.println("222222222 create.");
    }

    private void initSessionList(){
    	Uri uri = Uri.parse("rong://" + AppContext.getInstance().getApplicationInfo().packageName).buildUpon()
        .appendPath("conversationlist")
        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //����˽�ĻỰ�Ƿ�ۺ���ʾ
        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//Ⱥ��
//      .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")//������
//      .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//Ӧ�ù��ڷ���
//      .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//���������
        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//ϵͳ
        .build();
        setUri(uri);
    }

    /**
     * �ж���Ϣ�Ƿ��� push ��Ϣ
     *
     */
    private void isReconnect() {
    	String token = StringUtils.getString(AppContext.getInstance(), StringUtils.KEY_RONG_TOKEN, default_token);
    	if(!StringUtils.isEmpty(token)){
    		reconnect(token);
    	} else {
    		LogUtil.w(TAG, "==> token is empty.");
    	}
        /*Intent intent = getActivity().getIntent();
        String token = null;
        if (AppContext.getInstance() != null) {
        	token = PreferenceManager.getDefaultSharedPreferences(AppContext.getInstance()).getString("DEMO_TOKEN", "default");
            //token = AppContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "default");
        }
        //push��֪ͨ������Ϣ����
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            //ͨ��intent.getData().getQueryParameter("push") Ϊtrue���ж��Ƿ���push��Ϣ
            if (intent.getData().getQueryParameter("push") != null
                    && intent.getData().getQueryParameter("push").equals("true")) {

                reconnect(token);
            } else {
                //�����е���̨���յ���Ϣ��������,��ִ������
                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {
                    reconnect(token);
                } else {
                	initSessionList();
                }
            }
        }*/
    }

    /**
     * ����
     *
     * @param token
     */
    private void reconnect(String token) {
        if (AppContext.getInstance().getApplicationInfo().packageName.equals(AppContext.getCurProcessName(AppContext.getInstance()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                	LogUtil.d(TAG, "--onTokenIncorrect");
                }

                @Override
                public void onSuccess(String s) {
                	LogUtil.d(TAG, "--onSuccess" + s);
                	initSessionList();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                	LogUtil.d(TAG, "--onError" + errorCode);
                }
            });
        }
    }

}
