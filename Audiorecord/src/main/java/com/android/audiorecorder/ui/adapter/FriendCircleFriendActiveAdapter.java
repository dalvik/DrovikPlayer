package com.android.audiorecorder.ui.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.FriendCircleActive;
import com.android.audiorecorder.ui.data.FriendCircleImage;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveCommentResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveRemarkResp;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveResp;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.audiorecorder.utils.StringUtil;
import com.android.library.net.utils.LogUtil;
import com.android.library.ui.dialog.CommonDialog;
import com.android.library.ui.view.FriendCircleGridView;
import com.android.library.ui.view.FriendCircleListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendCircleFriendActiveAdapter extends BaseAdapter implements OnClickListener {
	
	private Context context;

	private FriendCircleActiveCommentResp mActiveReply;// 临时引用

	private PopupWindow mActiveFeedWindow;

	 private FriendCircleListView replyList;

	private FirendCircleReplyAdapter mActiveReplyAdapter;

	private List<FriendCircleActiveResp> mFriendActiveList;
	
	private FriendCircleActiveResp mFriendCircleActiveItem;

	//private PopupWindow editWindow;

	//private EditText activeReplyContentEditText;
	
	//private Button mActiveReplyCommitButton;

	//private RelativeLayout topLayout;
	
	/**
	 * 绑定数据
	 * @param holder
	 */

	private List<FriendCircleActiveCommentResp> replys;// 临时引用

	private FlushListView mFlush;

	private CommonDialog mDelDialog;// 删除提示框
	
	private AlertDialog.Builder builder;

	private int mUserCode;
	private String mActivityId;
	private String TAG = "FriendCircleFriendActiveAdapter";
	
	public FriendCircleFriendActiveAdapter(Context context, PopupWindow window,
                                           PopupWindow bottomReplyWindow, FlushListView flush) {
		this.context = context;
		this.mActiveFeedWindow = window;
		this.mFlush=flush;
		//this.editWindow = bottomReplyWindow;
		//activeReplyContentEditText = (EditText) bottomReplyWindow.getContentView().findViewById(R.id.friend_circle_reply_content_id);
		//mActiveReplyCommitButton = (Button) bottomReplyWindow.getContentView().findViewById(R.id.friend_circle_reply_send_button_id);
		//topLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_friend_group, null);
		iniDelDialog();
	}

	/**
	 * 初始化删除dialog
	 */
	private void iniDelDialog() {
		mDelDialog = new CommonDialog(context);
		TextView titile = mDelDialog.getTitle();
		TextView content = mDelDialog.getContent();
		mDelDialog.setLeftOnclick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDelDialog.dismiss();
				
			}
		});
		mDelDialog.setRightOnclick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDelDialog.dismiss();
				if(mFlush != null){
				    mFlush.delTrendById(mActivityId);
				}
			}
		});
		titile.setText(R.string.friend_circle_active_delete_title);
		content.setText(R.string.friend_circle_active_delete_content);
		mDelDialog.setLeftBtnText(content.getResources().getString(R.string.friend_circle_active_delete_cancel));
		mDelDialog.setRightBtnText(content.getResources().getString(R.string.friend_circle_active_delete_ok));
	}

	public void setData(List<FriendCircleActiveResp> list) {
		this.mFriendActiveList = list;
	}

	@Override
	public int getCount() {
		return mFriendActiveList == null ? 0 : mFriendActiveList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFriendActiveList == null ? null : mFriendActiveList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_friend_circle_friend_active_items, null);
			holder = getHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (mFriendActiveList != null) {
			mFriendCircleActiveItem = mFriendActiveList.get(position);
			//friend.position = position;
			convertView.setId(mFriendCircleActiveItem._id);
			bindData(holder);
		}
		return convertView;
	}

	public void setUserCode(int userCode){
	    this.mUserCode = userCode;
	}
	
	private void bindData(ViewHolder holder) {
		if (mFriendCircleActiveItem == null)
			return;
		holder.nickname.setText(mFriendCircleActiveItem.nickname);
		// 判断是否有回复
		if (TextUtils.isEmpty(mFriendCircleActiveItem.activeTextSummary)) {
			holder.contentText.setVisibility(View.GONE);
		} else {
			holder.contentText.setVisibility(View.VISIBLE);
			holder.contentText.setText(mFriendCircleActiveItem.activeTextSummary);
		}

		// 判断是否有分享链接
		if (TextUtils.isEmpty(mFriendCircleActiveItem.linkUrl)) {
			holder.linkContent.setVisibility(View.GONE);
		} else {
			holder.linkContent.setVisibility(View.GONE);
			//holder.linkIcon.setImageResource(R.drawable.head_icon);
			//holder.linkDescription.setText("我只是做个测试");
		}
		holder.sendDate.setText(StringUtil.handTime(mFriendCircleActiveItem.activeTime, context.getResources()));
		//TODO
		// 判断是否有点赞
		if(listIsEmpty(mFriendCircleActiveItem.activeRemarkList)) {
		    holder.favourLayout.setVisibility(View.GONE);
		} else {
		    holder.favourLayout.setVisibility(View.VISIBLE);
		    holder.remarkGridView.setAdapter(new FriendCircleRemarkAdapter(context, mFriendCircleActiveItem.activeRemarkList, mUserCode==mFriendCircleActiveItem.userCode));
		    if (listIsEmpty(mFriendCircleActiveItem.activeCommentList)) {// 只有点赞 则不显示分隔线
                holder.praiseLine.setVisibility(View.GONE);
            } else {
                holder.praiseLine.setVisibility(View.VISIBLE);
                holder.remarkGridView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                        if(mFriendCircleActiveItem.activeRemarkList.size()>index){
                            FriendCircleActiveRemarkResp resp = mFriendCircleActiveItem.activeRemarkList.get(index);
                            //TODO goto persion center by id 
                            ActivityUtil.gotoHisPersonalCeneterActivity(context, resp.userCode);
                        }
                    }
                });
            }
		}
		
		// 判断点赞 与回复是否都没有内容
		if (listIsEmpty(mFriendCircleActiveItem.activeRemarkList) && listIsEmpty(mFriendCircleActiveItem.activeCommentList)) {
			holder.replyContent.setVisibility(View.GONE);
		} else {
			holder.replyContent.setVisibility(View.VISIBLE);
		}
		
		// 判断是否有回复内容
		if (listIsEmpty(mFriendCircleActiveItem.activeCommentList)) {
			holder.replyList.setVisibility(View.GONE);
		} else {
			holder.replyList.setVisibility(View.VISIBLE);
		}
		
		// 分享类型
		holder.shareType.setVisibility(View.GONE);
		// 判断是否有发表图片
		if (TextUtils.isEmpty(mFriendCircleActiveItem.activeImage)) {
			holder.imageGridView.setVisibility(View.GONE);
		} else {
			holder.imageGridView.setVisibility(View.VISIBLE);
			final ArrayList<FriendCircleImage> imageList = getPhotos(mFriendCircleActiveItem.activeImage);
			if (imageList != null) {
				holder.imageGridView.setAdapter(new FriendCircleImageAdapter(context, imageList, false));
				holder.imageGridView.setOnItemClickListener(new OnItemClickListener() {// 设置监听器
					
					@Override
					public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
						//TODO ，点击进入大图
					}
				});
			 }
		}
		mActiveReplyAdapter = new FirendCircleReplyAdapter(context);
		replys = mFriendCircleActiveItem.activeCommentList;
		// =========判断是否有更多回复=========
		checkMoreReply(holder);
		// ==============================

		// ==================评论==================
		mActiveReplyAdapter.setData(replys);
		holder.replyList.setAdapter(mActiveReplyAdapter);
		holder.replyList.setTag(replys);
		holder.replyList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				replys = (List<FriendCircleActiveCommentResp>) parent.getTag();
				mActiveReply = replys.get(position);
				LogUtil.d(TAG, "==> active' userCode : " + mActiveReply.userCode + " self: " + mUserCode);
				if(mActiveReply.userCode == mUserCode) {//not allowed reply to self
				    mFlush.handReply(mActiveReply);// 处理评论
				} else {
				    view.setTag(mActiveReply);
				    mFlush.showDiscussDialog(view);
				}
			}
		});
		// ==================评论end==================
		if(mUserCode == mFriendCircleActiveItem.userCode){
		    holder.delActive.setTag(mFriendCircleActiveItem._id);
		    holder.delActive.setOnClickListener(this);
		} else {
		    holder.delActive.setVisibility(View.INVISIBLE);
		}
		holder.feedIcon.setTag(mFriendCircleActiveItem);
		holder.feedIcon.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.friend_circle_message_delete_id) {
			mActivityId = String.valueOf(v.getTag());
			mDelDialog.show();

		} else if (i == R.id.friend_circle_message_feed_icon_id) {
			showDialog(v);

		} else if (i == R.id.discuss) {
			mFlush.showDiscussDialog(v);

		} else if (i == R.id.favuor_cancle) {
			mFriendCircleActiveItem = (FriendCircleActiveResp) v.getTag();
			mFlush.delParise(String.valueOf(mFriendCircleActiveItem._id));
			mActiveFeedWindow.dismiss();

		} else if (i == R.id.favuor) {
			mFriendCircleActiveItem = (FriendCircleActiveResp) v.getTag();
			mFlush.addTrendParise(String.valueOf(mFriendCircleActiveItem._id));
			mActiveFeedWindow.dismiss();

		/*case R.id.reply_more:// 点赞 说说
			mFlush.getReplyByTrendId(v.getTag());
			break;*/
		} else {
		}
	}
	
    public void addRemarkItem(int activeId, FriendCircleActiveRemarkResp item){
        for(FriendCircleActiveResp active:mFriendActiveList) {
            if(active._id == activeId){
                if(active.activeRemarkList == null){
                    active.activeRemarkList = new ArrayList<FriendCircleActiveRemarkResp>();
                }
                active.activeRemarkList.add(item);
                notifyDataSetChanged();
                break;
            }
        }
    }
    
    public void addCommentItem(int activeId, FriendCircleActiveCommentResp item){
        for(FriendCircleActiveResp active:mFriendActiveList) {
            if(active._id == activeId){
                if(active.activeCommentList == null){
                    active.activeCommentList = new ArrayList<FriendCircleActiveCommentResp>();
                }
                active.activeCommentList.add(item);
                notifyDataSetChanged();
                break;
            }
        }
    }
    
	/**
	 * 处理图片数据
	 * 
	 * @param photo
	 * @return
	 */
	private ArrayList<FriendCircleImage> getPhotos(String photo) {
		if (!photo.contains("[")) {
			return null;
		}
		ArrayList<FriendCircleImage> phs = new ArrayList<FriendCircleImage>();
		try {
			JSONArray array = new JSONArray(photo);
			int size = array.length();
			JSONObject obj;
			FriendCircleImage ph;
			for (int i = 0; i < size; i++) {
				obj = array.getJSONObject(i);
				ph = new FriendCircleImage();
				ph.imageThumbnail = obj.getString("urlMin");
				ph.imageUrl = obj.getString("url");
				phs.add(ph);
			}
			return phs;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void checkMoreReply(ViewHolder holder) {
		/*int replyCount = friend.replyCount;
		if (replyCount > 10 && replyCount != friend.activeReplyList.size()) {// 最多显示10条
			holder.replyMore.setVisibility(View.VISIBLE);
			holder.replyMore.setTag(friend.friendId);
			holder.replyMore.setOnClickListener(this);
		} else {
			holder.replyMore.setVisibility(View.GONE);
		}*/
	}

	/**
	 * 初始化ViewHolder
	 * 
	 * @param convertView
	 * @return
	 */
	private ViewHolder getHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.headIcon = (ImageView) convertView.findViewById(R.id.friend_circle_message_create_headicon_id);
		holder.nickname = (TextView) convertView.findViewById(R.id.friend_circle_message_create_owner_id);
		holder.shareType = (TextView) convertView.findViewById(R.id.friend_circle_message_share_type_id);
		holder.contentText = (TextView) convertView.findViewById(R.id.friend_circle_message_text_content_id);
		holder.linkIcon = (ImageView) convertView.findViewById(R.id.friend_circle_message_link_icon_id);
		holder.linkDescription = (TextView) convertView.findViewById(R.id.friend_circle_message_link_description_id);
		holder.sendDate = (TextView) convertView.findViewById(R.id.friend_circle_message_create_time_id);
		holder.delActive = (TextView) convertView.findViewById(R.id.friend_circle_message_delete_id);
		holder.feedIcon = (ImageButton) convertView.findViewById(R.id.friend_circle_message_feed_icon_id);
		holder.replyList = (FriendCircleListView) convertView.findViewById(R.id.friend_circle_reply_content_listview_id);
		holder.linkContent = (LinearLayout) convertView.findViewById(R.id.friend_circle_message_link_content_linearlayout_id);
		holder.replyContent = (LinearLayout) convertView.findViewById(R.id.friend_circle_message_reply_content_linearlayout_id);
		holder.favourLayout = (LinearLayout) convertView.findViewById(R.id.friend_circle_message_favout_linearlayout_id);
		holder.remarkGridView = (FriendCircleGridView) convertView.findViewById(R.id.friend_circle_message_favout_gridview_id);
		holder.replyMore = (TextView) convertView.findViewById(R.id.friend_circle_reply_more_id);
		holder.imageGridView = (FriendCircleGridView) convertView.findViewById(R.id.friend_circle_message_image_content_id);
		holder.praiseLine = convertView.findViewById(R.id.praise_line);
		return holder;
	}

	private class ViewHolder {
		public TextView shareType;// 分享类型
		public ImageView headIcon;// 头像
		public TextView nickname;// 名字
		public TextView contentText;// 文字内容
		public ImageView linkIcon;// 链接图标
		public TextView linkDescription;// 链接描述
		public TextView sendDate;// 发表时间
		public TextView delActive;// 删除动态
		public ImageButton feedIcon;// 回复icon
		public FriendCircleListView replyList;// 回复的listView
		public LinearLayout linkContent;// 链接内容
		public LinearLayout replyContent;// 回复布局
		public LinearLayout favourLayout;// 点赞布局
		public FriendCircleGridView remarkGridView;// 点赞的gridView
		public TextView replyMore;// 更多回复
		public FriendCircleGridView imageGridView;// 发表的图片 最多8张
		public View praiseLine;// 点赞下面的线
	}

	/**
	 * 显示评论弹窗
	 * @param view
	 */
	private void showDialog(View view) {
		int width = view.getWidth();
		mFriendCircleActiveItem = (FriendCircleActiveResp) view.getTag();
		//mFlush.showCancle(friend);// 显示或者隐藏赞
		int[] location = new int[2];
		view.getLocationInWindow(location);
		int x = location[0] - dip2px(context, width) - width - 80;
		int y = location[1] - 20;
		View v = mActiveFeedWindow.getContentView();
		TextView discuss = (TextView) v.findViewById(R.id.discuss);
		TextView favuor = (TextView) v.findViewById(R.id.favuor);
		TextView favuorCancle = (TextView) v.findViewById(R.id.favuor_cancle);
		favuorCancle.setOnClickListener(this);
		discuss.setOnClickListener(this);
		favuor.setOnClickListener(this);
		if (listIsEmpty(mFriendCircleActiveItem.activeRemarkList)) {
		    favuorCancle.setVisibility(View.GONE);
		    favuor.setVisibility(View.VISIBLE);
		} else {
		    boolean isFavuor = false;
		    for(FriendCircleActiveRemarkResp item:mFriendCircleActiveItem.activeRemarkList ) {
		        if(item.userCode == this.mUserCode){
		            isFavuor = true;
		            break;
		        }
		    }
		    favuorCancle.setVisibility(isFavuor?View.VISIBLE:View.GONE);
		    favuor.setVisibility(isFavuor?View.GONE:View.VISIBLE);
		}
		discuss.setTag(view.getTag());
		favuor.setTag(view.getTag());
		favuorCancle.setTag(view.getTag());
		mActiveFeedWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
	}

	/**
	 * 弹出删除提示框
	 */
	private Dialog dialog;

	private void showDeletedialog(final String trendId) {

		builder.setNegativeButton("否", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("是", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mFlush.delTrendById(trendId);
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.show();
		// delDialog.setLeftOnclick(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog.dismiss();
		// }
		// });
		// delDialog.setRightOnclick(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// flush.delTrendById(trendId);
		// delDialog.dismiss();
		// }
		// });
		// delDialog.show();
	}

	/**
	 * 显示回复窗口
	 * 
	 * @param type
	 */

	/**
	 * 显示评论窗
	 * 
	 * @param type
	 * @param v
	 */
	//@Deprecated
	/*private void showDiscuss(View v) {
		friend = (FriendCircleFriend) v.getTag();
		final int id = friend.id;
		// getViewPosition(friend.position);// 获取view的位置
		activeReplyContentEditText.setFocusable(true);
		activeReplyContentEditText.requestFocus();
		// 设置焦点，不然无法弹出输入法
		editWindow.setFocusable(true);
		// 以下两句不能颠倒
		editWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
		editWindow
				.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		editWindow.showAtLocation(topLayout, Gravity.BOTTOM, 0, 0);
		// 显示键盘
		final InputMethodManager manager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		editWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				manager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
			}
		});
		mActiveReplyCommitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mActiveReply = new FriendCircleReply();
				// reply.sendName =userInfo.getUsername();
				mActiveReply.content = activeReplyContentEditText.getEditableText().toString();
				mActiveReply.friendId = id;
				// db.save(reply);
				flush.saveReply(mActiveReply);
				// flush.flush();
				editWindow.dismiss();
				activeReplyContentEditText.setText("");
			}
		});

		window.dismiss();
	}*/

	/**
	 * 获取view的位置
	 * 
	 * @param position
	 */
	private void getViewPosition(int position) {
		int[] location = new int[2];
		View view = null;// = views.get(position);
		view.getLocationInWindow(location);
		int x = location[0];
		int y = location[1];
		String str = "x=" + x + "                   y=" + y;
		toast(str);
	}

	/**
	 * 回调接口 实现数据刷新
	 * 
	 * @author jiangyue
	 * 
	 */
	public interface FlushListView {
		public void flush();// 刷新数据

		public void showDiscussDialog(View v);// 显示评论对话框

		public void getReplyByTrendId(Object tag);// 根据动态id获取评论回复

		public void getViewPosition(int position);

		public void delParise(String valueOf);// 删除点赞

		public void showCancle(FriendCircleActive friend);// 显示或者隐藏赞

		public void saveReply(FriendCircleActiveCommentResp reply);// 保存回复信息

		public void addTrendParise(String trendId);// 添加点赞

		public void delTrendById(String trendId);// 根据id删除动态

		public void showDel(TextView view, String userId);// 显示删除按钮

		public void handReply(FriendCircleActiveCommentResp reply);// 处理评论

	}

	/**
	 * 判断集合是否是需要的格式 （不为null size>0）
	 * 
	 * @param list
	 * @return
	 */
	private boolean listIsEmpty(List list) {
		if (list != null && list.size() > 0) {
		    return false;
		}
		return true;

	}

	private void toast(String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	private class FriendRunnable implements Runnable {
		private ViewHolder holder;

		public FriendRunnable(ViewHolder holder) {
			this.holder = holder;
		}
		@Override
		public void run() {
			if (holder != null)
				bindData(holder);
		}
	}
	
}
