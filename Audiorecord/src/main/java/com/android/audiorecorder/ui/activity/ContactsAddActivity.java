package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendDetailResp;
import com.android.audiorecorder.ui.manager.UserManager;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.audiorecorder.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ToastUtils;
import com.android.library.utils.TextUtils;

import java.util.ArrayList;


public class ContactsAddActivity extends BaseCompatActivity implements View.OnClickListener {
    private EditText searchEt;
    private ImageView searchIv;
    private View userRl;
    private View wayLl;
    private ImageView iconIv;
    private TextView nicknameTv;
    private Button addBtn;
    private TextView tipTv;
    private UserManager mUserManager;
    private int mWhatSearchNewFriend;
    private int mWhatAddFriend;
    private int mUserCode;
    private int friendId;
    private String allow_visit_localcontacts = "allow_visit_localcontacts";
    private String allow_visit_localcontacts_true = "true";
    private String allow_visit_localcontacts_false = "false";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_add);
        setTitle(R.string.contacts_add_title);
        initUI();
        mUserManager = new UserManager(this);
    }
    
    @Override
    protected boolean initIntent() {
        try {
            mUserCode = getIntent().getIntExtra(ActivityUtil.USER_ID, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void initUI() {
        searchEt = (EditText) findViewById(R.id.searchEt);
        searchIv = (ImageView) findViewById(R.id.searchIv);
        searchIv.setOnClickListener(this);
        //
        userRl = findViewById(R.id.userRl);
        wayLl = findViewById(R.id.wayLl);
        //
        nicknameTv = (TextView) findViewById(R.id.nicknameTv);
        addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(this);
        iconIv = (ImageView) findViewById(R.id.iconIv);
        tipTv = (TextView) findViewById(R.id.tipUpdateTv);
        findViewById(R.id.contactRl).setOnClickListener(this);
        findViewById(R.id.qqRl_addContacts).setOnClickListener(this);
        findViewById(R.id.wxRl_addContacts).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.searchIv) {
            String keyword = searchEt.getText().toString();
            if (!TextUtils.isEmpty(keyword)) {
                showWaitingDialog();
                mWhatSearchNewFriend = mUserManager.searchNewUser(keyword);
                LogUtil.d(TAG, "==>whatSearch = " + mWhatSearchNewFriend + " keyword = " + keyword);
            }

        } else if (i == R.id.contactRl) {/*if (DataStoreUtils.readLocalInfo(allow_visit_localcontacts).equals(allow_visit_localcontacts_true)) {
                    ActivityUtil.gotoContactsLocalActivity(activity);
                } else {
                    showConfirmDialog(R.string.local_contacts_confirmdialog_title,R.string.local_contacts_confirmdialog_content,  R.string.local_contacts_confirmdialog_yes, R.string.local_contacts_confirmdialog_no, new NormalDialog.OnResultListener() {
                        @Override
                        public void onConfirm() {
                            DataStoreUtils.saveLocalInfo(allow_visit_localcontacts, allow_visit_localcontacts_true);
                            hideConfirmDialog();
                            ActivityUtil.gotoContactsLocalActivity(activity);
                        }

                        @Override
                        public void onCancel() {
                            DataStoreUtils.saveLocalInfo(allow_visit_localcontacts, allow_visit_localcontacts_false);
                            hideConfirmDialog();
                        }
                    });
                }*/

        } else if (i == R.id.addBtn) {
            showWaitingDialog();
            mWhatAddFriend = mUserManager.addFriend(mUserCode, 1, friendId);

        } else if (i == R.id.qqRl_addContacts) {//UmengShare.share(activity, getShareBean(), SHARE_MEDIA.QQ);

        } else if (i == R.id.wxRl_addContacts) {//UmengShare.share(activity, getShareBean(), SHARE_MEDIA.WEIXIN);

        }
    }

    /*private ShareBean getShareBean() {
        ShareBean shareBean = new ShareBean();
        shareBean.imageType = ShareBean.ImageType.URL;
        shareBean.image = ShareBean.getWebUri(OSSUtil.get144(AppApplication.getUser().headIcon));
        String nickName = AppApplication.getUser().nickName;
        shareBean.title = activity.getString(R.string.share_user_content, nickName);
        shareBean.content = activity.getString(R.string.share_user_title, nickName);
        shareBean.targetUrl = DataResource.getInstance().getServerResource().getUserInfoUrl(AppApplication.getUser().userId);
        return shareBean;
    }*/

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        LogUtil.d(TAG, "==>what = " + what + " obj = " + obj);
        if (mWhatSearchNewFriend == what) {
            switch (result) {
                case UserManager.RESULT_SUCCESS:
                    BaseData<ArrayList<FriendCircleFriendDetailResp>> data = (BaseData<ArrayList<FriendCircleFriendDetailResp>>) obj;
                    ArrayList<FriendCircleFriendDetailResp> resp = data.data;
                    setUsers(resp);
                    break;
                default:
                    return false;
            }
        } else if (mWhatAddFriend == what) {
            switch (result) {
                case UserManager.RESULT_SUCCESS:
                    BaseData data = (BaseData) obj;
                    if(data.code == 200){
                        addBtn.setText(R.string.contacts_add_waiting);
                        addBtn.setOnClickListener(null);
                        ToastUtils.showToast(R.string.contacts_add_send);
                    } else {
                        ToastUtils.showToast(R.string.contacts_add_fail);
                    }
                    //UserDAO.getInstance().updateStatus(userId, BaseApplication.getRealTime());
                    break;
                //case UserManager.FAIL_IS_FRIEND:
                    //DataResource.getInstance().getContactList();
                    //ToastUtils.showToast(R.string.contacts_add_success);
                    //break;
                default:
                    return false;
            }
        }
        return true;
    }

    private void setUsers(ArrayList<FriendCircleFriendDetailResp> list) {
        if (list == null || list.isEmpty()) {
            wayLl.setVisibility(View.GONE);
            userRl.setVisibility(View.GONE);
            ToastUtils.showToast(R.string.contacts_search_empty);
        } else {
            wayLl.setVisibility(View.GONE);
            userRl.setVisibility(View.VISIBLE);
            FriendCircleFriendDetailResp friend = list.get(0);
            friendId = friend.userCode;
            nicknameTv.setText(friend.nickName);
            //HeadUtil.loadHeadIcon(friend.headIcon, iconIv, friend.sex);
            /*if (DataResource.getInstance().isContactById(friend.userId)) {
                addBtn.setVisibility(View.GONE);
                tipTv.setVisibility(View.VISIBLE);
            } else {
                DBUser user = UserDAO.getInstance().getUser(userId + "");
                addBtn.setVisibility(View.VISIBLE);
                if (user != null && user.status + DateUtil.HOUR_IN_MILLIS > BaseApplication.getRealTime()) {
                    addBtn.setText(R.string.contacts_add_waiting);
                    addBtn.setOnClickListener(null);
                } else {
                    addBtn.setText(R.string.contacts_local_add);
                    addBtn.setOnClickListener(this);
                }
                tipTv.setVisibility(View.GONE);
            }*/
            //TODO
            addBtn.setText(R.string.contacts_local_add);
            tipTv.setVisibility(View.GONE);
            addBtn.setOnClickListener(this);
            
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
