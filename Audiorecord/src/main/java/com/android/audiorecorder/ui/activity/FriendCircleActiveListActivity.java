package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.adapter.FriendCircleFriendActiveAdapter;
import com.android.audiorecorder.ui.adapter.FriendCircleFriendActiveAdapter.FlushListView;
import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.FriendCircleActive;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveCommentResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActivePublishResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveRemarkResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveResp;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.FriendCircleManager;
import com.android.audiorecorder.ui.manager.FriendManager;
import com.android.audiorecorder.ui.manager.UserDao;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.audiorecorder.utils.StringUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.audiorecorder.utils.URLS;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.dialog.ListDialog;
import com.android.library.ui.dialog.ListDialog.ListDialogItemOnclick;
import com.android.library.ui.view.HFRefreshView;
import com.android.library.ui.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * 朋友圈列表
 *
 */
public class FriendCircleActiveListActivity extends BaseCompatActivity implements OnClickListener {

    private final static int PER_PAGE_NUMBER = 20;
    private final static int PUBLISH_REQUEST_CODE = 1000;
	private PopupWindow mActiveFeedPopupWindow;// 添加评论弹出窗口
	private PopupWindow mFriendActiveReplyWindow;//点击评论，在底部弹出的评论内容输入窗口

	private ListDialog dialog;
	private ImageButton back;// 返回按钮
	private ImageButton more;// 弹出更多

	private TextView discuss;// 评论
	private TextView favuor;// 赞
	private TextView favuorCancle;// 取消赞

	// ==============回复==============
	private EditText mActiveReplyEditText;// 回复框
	// ====================回复完结================

	private FriendCircleFriendActiveAdapter mFriendCircleAdpter;

	private Button sendBtn;// 发送按钮


	private InputMethodManager mInputMethodManager;

	private int lastPosition;// listView item最后所在的位置
	private int lastY;// listView item最后所在的y坐标
	
	private ImageView mTopImageView;
	private RoundImageView mHeadIconImageView;
	private TextView mNickNameTextView;
	private TextView mSignatureTextView;
    private HFRefreshView mFriendCircleListView;
    private List<FriendCircleActiveResp> mFriendCircleList;// 动态数据

    private FriendManager mFriendManager;
    private FriendCircleManager mFriendCircleManager;
    private UserDao mDao;
    private UserResp mUserResp;
    private int mUserCode;
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
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private boolean mHasMoreData;
    private String TAG = "FriendCircleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_friend_circle_active_list);
        setBackgroundColor(getResources().getColor(R.color.base_bg));
        setTitle(R.string.find_friend);
        initViews();
        initData();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    protected boolean initIntent() {
    	mUserCode = getIntent().getIntExtra(ActivityUtil.USER_ID, -1);
        if (mUserCode < 0 ) {
            return false;
        } else {
        	return true;
        }
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
                    }
                } else {
                	mHasMoreData = false;
                }
                mFriendCircleListView.stopLoadMore();
                mFriendCircleAdpter.notifyDataSetChanged();
                break;
            default:
            	mHasMoreData = false;
                mFriendCircleListView.stopLoadMore();
                mFriendCircleAdpter.notifyDataSetChanged();
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
    	Drawable drawable= getResources().getDrawable(R.drawable.friend_circle_active_new);
    	drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    	option.setCompoundDrawables(null, null, drawable, null);
    	option.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(StringUtils.getString(activity, StringUtils.KEY_USER_LOGIN_STATUS, "0").equals("1")){
					Intent intent = new Intent(FriendCircleActiveListActivity.this, FriendCircleActivePublishActivity.class);
					intent.putExtra(ActivityUtil.INTENT_OBJECT, mUserResp.userCode);
					//startActivityForResult(intent, PUBLISH_REQUEST_CODE);
					ActivityUtil.startActivityForResult(activity, intent, PUBLISH_REQUEST_CODE);
				} else {
					ActivityUtil.gotoLoginActivity(activity);
				}
			}
		});
    }
    
    
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.friend_circle_reply_send_button_id) {
            String content = mActiveReplyEditText.getText().toString();
            System.out.println("==> " + content);
            mActiveReplyEditText.setText("");
            mFriendActiveReplyWindow.dismiss();
            mWhatAddActiveComment = mFriendCircleManager.addFriendCircleActiveComment(mCommentActiveId, mUserResp.userCode, content, mReplyUserCode);

        } else if (i == R.id.friend_circle_head_icon) {
            ActivityUtil.gotoPersonalDetailActivity(this, mUserCode);

        } else {
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PUBLISH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(ActivityUtil.INTENT_OBJECT)) {
                    mHandler.removeCallbacks(mLoadActiveTask);
                    mHandler.postDelayed(mLoadActiveTask, 20);
                    /*FriendCircleActiveResp active = (FriendCircleActiveResp) data.getSerializableExtra(ActivityUtil.INTENT_OBJECT);
                    if(active != null){
                        active.headIcon = mUserResp.headIcon;
                        mFriendCircleList.add(0, active);
                        mFriendCircleAdpter.notifyDataSetInvalidated();
                    }*/
                }
            }

        } else {
        }
    }
    
    private void initData() {
        mHasMoreData = true;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(activity));
        options = new DisplayImageOptions.Builder()
        //.showImageOnLoading(defaultHeaderRes)
        //.showImageForEmptyUri(defaultHeaderRes)
        .showImageOnFail(R.drawable.user_logo)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .bitmapConfig(Config.RGB_565)
        .build();
        mDao = new UserDao();
        mUserResp = mDao.getUser(this);
        mFriendManager = new FriendManager(this);
        mFriendCircleManager = new FriendCircleManager(this);
        mWhatActiveList = getData(PER_PAGE_NUMBER, 0);
        mFriendCircleAdpter.setUserCode(mUserResp.userCode);
        mNickNameTextView.setText(mUserResp.nickName);
        mSignatureTextView.setText(mUserResp.signature);
        LogUtil.d(TAG, "==>headIcon:" + mUserResp.headIcon + " signature:" + mUserResp.signature);
        ImageLoader.getInstance().displayImage(URLS.DOMAIN + mUserResp.headIcon, mHeadIconImageView, options);
    }
    
    private int getData(int pageNumber, int offset) {
        return mFriendCircleManager.getFriendCircleActiveList(mUserResp.userCode, 0, getNewActiveId(), pageNumber, offset);
	}
	
	private void initViews() {
		dialog = new ListDialog(this);
		initPopWindow();// 初始化弹出窗
		mFriendCircleListView = (HFRefreshView) findViewById(R.id.lib_friend_circle_id);
		mTopImageView = (ImageView) findViewById(R.id.friend_circle_active_top_bg_id);
		mNickNameTextView = (TextView)findViewById(R.id.friend_circle_nickname);
		mHeadIconImageView = (RoundImageView)findViewById(R.id.friend_circle_head_icon);
		mSignatureTextView = (TextView)findViewById(R.id.friend_circle_signature);
		mHeadIconImageView.setOnClickListener(this);
		mFriendCircleListView.setOnRefreshListener(new HFRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.removeCallbacks(mLoadActiveTask);
                mHandler.postDelayed(mLoadActiveTask, 200);
            }
            
            @Override
            public void onLoadMore() {
                if(mHasMoreData) {
                    mHandler.removeCallbacks(mLoadMoreActiveTask);
                    mHandler.postDelayed(mLoadMoreActiveTask, 500);
                }
            }
        });
		mFriendCircleList = new ArrayList<FriendCircleActiveResp>();
		mFriendCircleAdpter = new FriendCircleFriendActiveAdapter(this, mActiveFeedPopupWindow, null, new FriendCircleActiveFlush());// 初始化适配器
		mFriendCircleAdpter.setData(mFriendCircleList);
		mFriendCircleListView.setAdapter(mFriendCircleAdpter);
		mFriendCircleListView.setFocusableInTouchMode(false);
	}
	
	private void initPopWindow() {
		View view = getLayoutInflater().inflate(R.layout.layout_friend_circle_popup_reply, null);
		mActiveFeedPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, true);
		mActiveFeedPopupWindow.setAnimationStyle(R.style.reply_window_anim);
		discuss = (TextView) view.findViewById(R.id.discuss);
		favuor = (TextView) view.findViewById(R.id.favuor);
		favuorCancle = (TextView) view.findViewById(R.id.favuor_cancle);
		mActiveFeedPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.black));
		mActiveFeedPopupWindow.setOutsideTouchable(true);
		discuss.setOnClickListener(this);
		favuor.setOnClickListener(this);
		favuorCancle.setOnClickListener(this);

		View activeReplyInputView = getLayoutInflater().inflate(R.layout.layout_friend_circle_friend_replay_input, null);
		mFriendActiveReplyWindow = new PopupWindow(activeReplyInputView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		mFriendActiveReplyWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
		mFriendActiveReplyWindow.setOutsideTouchable(true);
		mActiveReplyEditText = (EditText) activeReplyInputView.findViewById(R.id.friend_circle_reply_content_id);
		sendBtn = (Button) activeReplyInputView.findViewById(R.id.friend_circle_reply_send_button_id);
		sendBtn.setOnClickListener(this);
	}
	
	/**
	 * 显示回复评论内容输入框
	 * @param reply
	 */
	private void showDiscuss() {
		mActiveReplyEditText.setFocusable(true);
		mActiveReplyEditText.requestFocus();

		// 设置焦点，不然无法弹出输入法
		mFriendActiveReplyWindow.setFocusable(true);

		// 以下两句不能颠倒
		mFriendActiveReplyWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		mFriendActiveReplyWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mFriendActiveReplyWindow.showAtLocation(mFriendCircleListView, Gravity.BOTTOM, 0, 0);

		// 显示键盘
		mInputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		mFriendActiveReplyWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				mInputMethodManager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
			}
		});
		mActiveFeedPopupWindow.dismiss();
	}
    
	private class FriendCircleActiveFlush implements FlushListView{

		@Override
		public void flush() {
			
		}

		@Override
		public void showDiscussDialog(View v) {
		    Object obj = v.getTag();
		    if(obj instanceof FriendCircleActiveResp) {
		        FriendCircleActiveResp friend = (FriendCircleActiveResp) obj;
		        mCommentActiveId = friend._id;
		        mReplyUserCode = friend.recvUserCode;
		    } else if(obj instanceof FriendCircleActiveCommentResp) {
		        FriendCircleActiveCommentResp comment = (FriendCircleActiveCommentResp) obj;
                mCommentActiveId = comment.activeId;
                mReplyUserCode = comment.userCode;
		    }
		    LogUtil.d(TAG, "==> discus : active id: " + mCommentActiveId + " to : " + mReplyUserCode);
			showDiscuss();
		}

		@Override
		public void getReplyByTrendId(Object tag) {
			Toast.makeText(FriendCircleActiveListActivity.this, "getReplyByTrendId ", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void getViewPosition(int position) {
			Toast.makeText(FriendCircleActiveListActivity.this, "getViewPosition " + position, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void showCancle(FriendCircleActive friend) {
		}

		@Override
		public void saveReply(FriendCircleActiveCommentResp reply) {
			Toast.makeText(FriendCircleActiveListActivity.this, "saveReply　", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void addTrendParise(String activeId) {
		    mWhatAddActiveRemark = mFriendCircleManager.addFriendCircleActiveRemark(mUserResp.userCode, StringUtil.toInt(activeId), 1);
		}

		@Override
        public void delParise(String activeId) {
		    mDeleteActiveId = StringUtil.toInt(activeId);
		    List<FriendCircleActiveRemarkResp> list = getRemarkList(mDeleteActiveId);
		    if(list != null){
		        for(FriendCircleActiveRemarkResp item:list) {
		            if(item.userCode == mUserResp.userCode){//get the active's remark id
		                mWhatDeleteActiveRemark = mFriendCircleManager.cancelFriendCircleActiveRemark(item._id, 0);
		                break;
		            }
		        }
		    }
        }

		@Override
		public void delTrendById(String activeId) {//delete friend circle active
		    mWhatDeleteActive = mFriendCircleManager.deleteFriendCircleActive(StringUtil.toInt(activeId));
		}

		@Override
		public void showDel(TextView view, String userId) {
			Toast.makeText(FriendCircleActiveListActivity.this, "showDel " + userId, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void handReply(final FriendCircleActiveCommentResp reply) {
			String[] items= new String[] { "删除", "复制" };//,"回复"
			dialog.init(items, new ListDialogItemOnclick() {
				@Override
				public void onClick(View view) {
					TextView v = (TextView) view;
					if ("删除".equals(v.getText())) {// 删除
					    mWhatDeleteActiveComment = mFriendCircleManager.deleteFriendCircleActiveComment(reply._id);
					} else if ("复制".equals(v.getText())) {// 复制
						ClipboardManager c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						//toast("已复制到剪切板");
					} else if ("回复".equals(v.getText())) {// 回复
						showDiscuss();
					}
					dialog.dismiss();
				}
			});
			dialog.show();
		
		}
	}
	
	private Runnable mLoadActiveTask = new Runnable() {
        
        @Override
        public void run() {
            mWhatActiveList = getData(PER_PAGE_NUMBER, 0);
            LogUtil.d(TAG, "==>mWhatNewActiveList: " + mWhatActiveList);
        }
    };
    
    private Runnable mLoadMoreActiveTask = new Runnable() {
        
        @Override
        public void run() {
            mWhatMoreActiveList = getData(PER_PAGE_NUMBER, 0);
            LogUtil.d(TAG, "==>mWhatMoreActiveList: " + mWhatMoreActiveList);
        }
    };
    
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
