package com.android.audiorecorder.ui.data;

import java.io.Serializable;

public class FriendCircleReply implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public String sendName;// 发送的名字
	public String replyName;// 回复的名字
	public String content;// 回复的内容
	public int friendId;// 对应的朋友内容id
	public String userId;// 发表人的id
	public String replyUserId;// 回复某人的userId

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public String getReplyName() {
		return replyName;
	}

	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	@Override
	public String toString() {
		return "Reply [id=" + id + ", sendName=" + sendName + ", replyName=" + replyName + ", content=" + content
				+ ", friendId=" + friendId + ", userId=" + userId + "]";
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReplyUserId() {
		return replyUserId;
	}

	public void setReplyUserId(String replyUserId) {
		this.replyUserId = replyUserId;
	}

}
