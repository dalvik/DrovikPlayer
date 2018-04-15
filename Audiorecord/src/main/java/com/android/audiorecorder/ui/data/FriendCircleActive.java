package com.android.audiorecorder.ui.data;

import java.io.Serializable;
import java.util.List;

public class FriendCircleActive implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public int userCode;// 用户id
	public String headIcon;// 头像icon
	public int shareType;// 0-是自己发送的 1-分享连接 2-分享歌曲 3-分享视频
	public String nickname;// 名字
	public String linkUrl;// 连接url
	// public Bitmap linkIcon;// 链接图标
	// public String linkDescription;// 链接描述
	public String contentText;// 发表的文字内容
	public String sendDate;// 发表时间
	public String favourName;// 点赞的名字
	public List<FriendCircleReply> activeReplyList;// 回复的listView
	public int friendId;// 动态id
	public int position;// 记录item位置
	public String images;// 发表的图片url
	public int replyCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhoto() {
		return headIcon;
	}

	public void setPhoto(String headIcon) {
		this.headIcon = headIcon;
	}

	public int getShareType() {
		return shareType;
	}

	public void setShareType(int shareType) {
		this.shareType = shareType;
	}

	public String getName() {
		return nickname;
	}

	public void setName(String name) {
		this.nickname = name;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getContentText() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public String getFavourName() {
		return favourName;
	}

	public void setFavourName(String favourName) {
		this.favourName = favourName;
	}

	public List<FriendCircleReply> getReplyList() {
		return activeReplyList;
	}

	public void setReplyList(List<FriendCircleReply> replyList) {
		this.activeReplyList = replyList;
	}

	public int getUserId() {
		return userCode;
	}

	public void setUserId(int userId) {
		this.userCode = userId;
	}

	public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}
}
