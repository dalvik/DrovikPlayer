package com.android.audiorecorder.ui.adapter;


import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.resp.FriendCircleActiveCommentResp;
import com.android.audiorecorder.ui.view.HandyTextView;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.library.utils.SpannableUtil;

import java.util.List;

public class FirendCircleReplyAdapter extends BaseAdapter implements OnClickListener {

	private Context context;

	private List<FriendCircleActiveCommentResp> list;

	private FriendCircleActiveCommentResp reply;

	public FirendCircleReplyAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<FriendCircleActiveCommentResp> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return list == null ? null : list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_friend_active_reply_item, null);
			holder = getViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (list != null) {
			reply = list.get(position);
			bindData(holder);
		}
		return convertView;
	}

	/**
	 * 绑定数据
	 * 
	 * @param holder
	 */
	private void bindData(ViewHolder holder) {
		if (reply == null)
			return;
		String replyName = null;
		if(isEmpty(reply.nickname)) {//top line
		    replyName = String.valueOf(reply.userCode);
		} else {
		    replyName = reply.nickname;
		}
		String replyKeyName = context.getResources().getString(R.string.friend_circle_active_comment_reply);
		String replyDel = context.getResources().getString(R.string.friend_circle_message_reply_del);
		SpannableStringBuilder contentSpannableStringBuilder = new SpannableStringBuilder();
		SpannableString spannableReplyName = SpannableUtil.setClickableSpan(replyName, 0, replyName.length(), new ClickableSpan() {
            
            @Override
            public void onClick(View widget) {
                ActivityUtil.gotoHisPersonalCeneterActivity(context, reply.userCode);
            }
            
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
		});
		spannableReplyName.setSpan(new ForegroundColorSpan(R.color.reply_gray), 0, replyName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		contentSpannableStringBuilder.append(spannableReplyName);
		String recvName = null;
        if(reply.recvUserCode > 0) {
            if(isEmpty(reply.recvNickname)) {
                recvName = String.valueOf(reply.recvUserCode);
            } else {
                recvName = reply.recvNickname;
            }
        }
        if(recvName != null) {
            contentSpannableStringBuilder.append(replyKeyName);
            SpannableString spannableRecvName = SpannableUtil.setClickableSpan(recvName, 0, recvName.length(), new ClickableSpan() {
                
                @Override
                public void onClick(View widget) {
                    ActivityUtil.gotoHisPersonalCeneterActivity(context, reply.recvUserCode);
                }
                
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            });
            spannableRecvName.setSpan(new ForegroundColorSpan(R.color.reply_gray), 0, recvName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentSpannableStringBuilder.append(spannableRecvName);
        }
		contentSpannableStringBuilder.append(replyDel + reply.activeComment);
        holder.content.setText(contentSpannableStringBuilder);
        holder.content.setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * 初始化viewHolder
	 * 
	 * @param convertView
	 * @return
	 */
	private ViewHolder getViewHolder(View convertView) {
		ViewHolder holder = new ViewHolder();
		//holder.sendName = (TextView) convertView.findViewById(R.id.friend_circle_active_comment_nickname_id);
		holder.content = (HandyTextView) convertView.findViewById(R.id.friend_circle_active_comment_content_id);
		return holder;
	}

	private class ViewHolder {
		//public TextView recvName;// 回复的名字
		//public TextView sendName;// 发送的名字
		public HandyTextView content;// 回复的内容
		//public TextView replyTemp;// 回复文字
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/*case R.id.friend_circle_active_comment_nickname_id:
			Toast.makeText(context, "去到他的首页", Toast.LENGTH_SHORT).show();
			break;*/
		/*case R.id.friend_circle_active_comment_recv_nickname_id:
			Toast.makeText(context, "去到他的首页", Toast.LENGTH_SHORT).show();
			break;*/
		default:
			break;
		}
	}
	/**
	 * 判断指定的字符串是否是 正确的（不为“”、null 、“null”）
	 * 
	 * @param str
	 * @return
	 */
	private boolean isEmpty(String str) {
		if (str != null && !"".equals(str) && !"null".equals(str))
			return false;
		return true;
	}
}
