package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.adapter.FriendCircleActiveCenterAdapter;
import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveCommentResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActivePublishResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveRemarkResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveResp;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.FriendCircleManager;
import com.android.audiorecorder.ui.manager.FriendManager;
import com.android.audiorecorder.ui.manager.UserDao;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.dialog.ListDialog;
import com.android.library.ui.view.HFRefreshView;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.List;

public class FriendCircleActiveCenterActivity extends BaseCompatActivity implements OnClickListener {

    private final static int PER_PAGE_NUMBER = 20;
    private final static int PUBLISH_REQUEST_CODE = 1000;
	private PopupWindow mActiveFeedPopupWindow;// 添加评论弹出窗口
	private PopupWindow mFriendActiveReplyWindow;//点击评论，在底部弹出的评论内容输入窗口

	private ListDialog dialog;
	private ImageButton back;// 返回按钮
	private ImageButton more;// 弹出更多

	private TextView discuss;// 评论
	private TextView favuor;// 赞

	private FriendCircleActiveCenterAdapter mFriendCircleAdpter;

	private Button sendBtn;// 发送按钮


	private int lastPosition;// listView item最后所在的位置
	private int lastY;// listView item最后所在的y坐标
	
	private ImageView mHeadIconImageView;
	private TextView mNickNameTextView;
    private HFRefreshView mFriendCircleListView;
    private List<FriendCircleActiveResp> mFriendCircleList;// 动态数据

    private FriendManager mFriendManager;
    private FriendCircleManager mFriendCircleManager;
    private UserDao mDao;
    private UserResp mUserResp;
    
    private int mWhatActiveList;
    private int mWhatMoreActiveList;
    private int mWhatAddActiveRemark;
    private int mWhatDeleteActiveRemark;
    private int mWhatDeleteActive;
    private int mWhatAddActiveComment;
    private int mWhatDeleteActiveComment;
    private int mDeleteActiveId;
    private int mCommentActiveId;
    private int mReplyUserCode;
    private Handler mHandler = new Handler();
    private FinalBitmap mFinalBit;
    private boolean mHasMoreData;
    private String TAG = "FriendCircleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_friend_circle_person_center);
        initViews();
        initData();
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        if (what == mWhatActiveList) {
            switch (result) {
                case FriendCircleManager.RESULT_SUCCESS:
                    BaseData<ArrayList<FriendCircleActiveResp>> data = (BaseData<ArrayList<FriendCircleActiveResp>>) obj;
                    if(data.code == 200 && data.data != null){
                        ArrayList<FriendCircleActiveResp> activeList = (ArrayList<FriendCircleActiveResp>)data.data;
                        if(activeList != null && activeList.size()>0){
                            mFriendCircleList.clear();
                            mFriendCircleList.addAll(activeList);
                            mFriendCircleAdpter.notifyDataSetChanged();
                        }
                    }
                    mFriendCircleListView.stopRefresh();
                    break;
                default:
                    mFriendCircleListView.stopRefresh();
                    return false;
            }
        } else if(what == mWhatMoreActiveList) {
            switch (result) {
            case FriendCircleManager.RESULT_SUCCESS:
                BaseData<ArrayList<FriendCircleActiveResp>> data = (BaseData<ArrayList<FriendCircleActiveResp>>) obj;
                if(data.code == 200 && data.data != null){
                    ArrayList<FriendCircleActiveResp> activeList = (ArrayList<FriendCircleActiveResp>)data.data;
                    if(activeList != null){
                        int size = activeList.size();
                        if(size < PER_PAGE_NUMBER){
                            LogUtil.d(TAG, "==> no more data.");
                            mHasMoreData = false;
                        }
                        mFriendCircleList.addAll(activeList);
                        mFriendCircleAdpter.notifyDataSetChanged();
                    }
                }
                mFriendCircleListView.stopLoadMore();
                break;
            default:
                mFriendCircleListView.stopLoadMore();
                return false;
            }
        } else if(what == mWhatAddActiveRemark){
            switch (result) {
            case FriendCircleManager.RESULT_SUCCESS:
                BaseData<FriendCircleActivePublishResp> data = (BaseData<FriendCircleActivePublishResp>) obj;
                if(data.code == 200 && data.data != null){
                    FriendCircleActivePublishResp resp = (FriendCircleActivePublishResp) data.data;
                    if(resp != null) {
                        LogUtil.d(TAG, "==> add remark : " + resp.activeId);
                        FriendCircleActiveRemarkResp remarkResp = new FriendCircleActiveRemarkResp();
                        remarkResp.userCode = mUserResp.userCode;
                        remarkResp.nickname = mUserResp.nickName;
                        remarkResp.headIcon = mUserResp.headIcon;
                        mFriendCircleAdpter.addRemarkItem(resp.activeId, remarkResp);
                    }
                }
                break;
            default:
                return false;
            }
        } else if(what == mWhatDeleteActiveRemark) {
            switch (result) {
            case FriendCircleManager.RESULT_SUCCESS:
                BaseData<FriendCircleActiveRemarkResp> data = (BaseData<FriendCircleActiveRemarkResp>) obj;
                if(data.code == 200 && data.data != null){
                    FriendCircleActiveRemarkResp remarkResp = (FriendCircleActiveRemarkResp) data.data;
                    List<FriendCircleActiveRemarkResp> remarkList = getRemarkList(mDeleteActiveId);
                    if(remarkList != null){
                        for(FriendCircleActiveRemarkResp resp:remarkList) {
                            if(resp.userCode == mUserResp.userCode && resp._id == remarkResp._id){
                                remarkList.remove(resp);
                                mFriendCircleAdpter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
                break;
            default:
                return false;
            }
        } else if(what == mWhatDeleteActive) {
            switch (result) {
            case FriendCircleManager.RESULT_SUCCESS:
                BaseData<FriendCircleActivePublishResp> data = (BaseData<FriendCircleActivePublishResp>) obj;
                if(data.code == 200 && data.data != null){
                    FriendCircleActivePublishResp resp = (FriendCircleActivePublishResp) data.data;
                    if(resp != null) {
                        LogUtil.d(TAG, "==> delete active id : " + resp.activeId);
                        for(FriendCircleActiveResp active:mFriendCircleList) {
                            if(active._id == resp.activeId){
                                mFriendCircleList.remove(active);
                                mFriendCircleAdpter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
                break;
            default:
                break;
            }
        } else if(what == mWhatAddActiveComment) {
            switch (result) {
            case FriendCircleManager.RESULT_SUCCESS:
                BaseData<FriendCircleActiveCommentResp> data = (BaseData<FriendCircleActiveCommentResp>) obj;
                if(data.code == 200 && data.data != null){
                    FriendCircleActiveCommentResp resp = (FriendCircleActiveCommentResp) data.data;
                    LogUtil.d(TAG, "==> add comment : " + resp.activeId);
                    FriendCircleActiveCommentResp commentResp = new FriendCircleActiveCommentResp();
                    commentResp.userCode = mUserResp.userCode;
                    commentResp.nickname = mUserResp.nickName;
                    commentResp.headIcon = mUserResp.headIcon;
                    commentResp.activeComment = resp.activeComment;
                    commentResp.activeCommentTime = resp.activeCommentTime;
                    mFriendCircleAdpter.addCommentItem(resp.activeId, commentResp);
                }
                break;
            default:
                break;
            }
        } else if(what == mWhatDeleteActiveComment){
            switch (result) {
            case FriendCircleManager.RESULT_SUCCESS:
                BaseData<FriendCircleActiveCommentResp> data = (BaseData<FriendCircleActiveCommentResp>) obj;
                if(data.code == 200 && data.data != null){
                    FriendCircleActiveCommentResp resp = (FriendCircleActiveCommentResp) data.data;
                    LogUtil.d(TAG, "==> remove comment : " + resp.activeId);
                    List<FriendCircleActiveCommentResp> commentList = getCommentList(resp.activeId);
                    if(commentList != null){
                        for(FriendCircleActiveCommentResp comment:commentList) {
                            if(resp.userCode == mUserResp.userCode){
                                commentList.remove(comment);
                                mFriendCircleAdpter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
                break;
            default:
                break;
            }
        }
    	return true;
    }
    
    @Override
    protected void setOptionView(TextView option) {
    	super.setOptionView(option);
    	option.setVisibility(View.VISIBLE);
    	option.setText(R.string.friend_circle_active_publish_commit);
    	option.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActivityUtil.gotoPublishFirendCircleActivity(activity, mUserResp.userCode, PUBLISH_REQUEST_CODE);
			}
		});
    }
    
    
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.friend_circle_reply_send_button_id) {
        } else if (i == R.id.friend_circle_head_icon) {
            if (mUserResp.userCode > 0) {
                ActivityUtil.gotoHisPersonalCeneterActivity(this, mUserResp.userCode);
            }

        } else {
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
        case PUBLISH_REQUEST_CODE:
            if(resultCode == RESULT_OK) {
                if(data != null && data.hasExtra(ActivityUtil.INTENT_OBJECT)){
                }
            }
            break;
            default:
                break;
        }
    }
    
    private void initData() {
        mHasMoreData = true;
        mFinalBit = FinalBitmap.create(this);
        mFinalBit.configLoadfailImage(R.drawable.user_logo);
        mDao = new UserDao();
        mUserResp = mDao.getUser(this);
        mFriendManager = new FriendManager(this);
        mFriendCircleManager = new FriendCircleManager(this);
        mWhatActiveList = getData(PER_PAGE_NUMBER, 0);
        mFriendCircleAdpter.setUserCode(mUserResp.userCode);
        mNickNameTextView.setText(mUserResp.nickName);
        mFinalBit.display(mHeadIconImageView, mUserResp.headIcon);
    }
    
    private int getData(int pageNumber, int offset) {
        return mFriendCircleManager.getFriendCircleActiveList(mUserResp.userCode, 0, getNewActiveId(), pageNumber, offset);
	}
	
	private void initViews() {
		dialog = new ListDialog(this);
		mFriendCircleListView = (HFRefreshView) findViewById(R.id.lib_friend_circle_id);
		mNickNameTextView = (TextView)findViewById(R.id.friend_circle_nickname);
		mHeadIconImageView = (ImageView)findViewById(R.id.friend_circle_head_icon);
		mHeadIconImageView.setOnClickListener(this);
		mFriendCircleListView.setOnRefreshListener(new HFRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
            
            @Override
            public void onLoadMore() {
                if(mHasMoreData) {
                   // mHandler.removeCallbacks(mLoadMoreActiveTask);
                   // mHandler.postDelayed(mLoadMoreActiveTask, 200);
                }
            }
        });
		mFriendCircleList = new ArrayList<FriendCircleActiveResp>();
		mFriendCircleAdpter = new FriendCircleActiveCenterAdapter(this, mActiveFeedPopupWindow, null, null);// 初始化适配器
		mFriendCircleAdpter.setData(mFriendCircleList);
		mFriendCircleListView.setAdapter(mFriendCircleAdpter);
		mFriendCircleListView.setFocusableInTouchMode(false);
	}
	
	
    private int getNewActiveId(){
        if(mFriendCircleList != null && mFriendCircleList.size()>0){
            return mFriendCircleList.get(0)._id;
        }
        return 0;
    }
    
    private List<FriendCircleActiveRemarkResp> getRemarkList(int activeId){
        for(FriendCircleActiveResp active:mFriendCircleList) {
            if(active._id == activeId){
                return active.activeRemarkList;
            }
        }
        return null;
    }
    
    private List<FriendCircleActiveCommentResp> getCommentList(int activeId){
        for(FriendCircleActiveResp active:mFriendCircleList) {
            if(active._id == activeId){
                return active.activeCommentList;
            }
        }
        return null;
    }
}
