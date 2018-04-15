package com.android.audiorecorder.ui.data;

import android.os.Parcel;
import android.os.Parcelable;

public class FriendCircleImage implements Parcelable {
	public static final String DATA="data";
	public static final String POSITION="position";
	public String imageUrl;// 大图地址
	public String imageThumbnail;// 小图地址

	public FriendCircleImage() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(imageUrl);
		out.writeString(imageThumbnail);
	}

	public static final Parcelable.Creator<FriendCircleImage> CREATOR = new Creator<FriendCircleImage>() {
		@Override
		public FriendCircleImage[] newArray(int size) {
			return new FriendCircleImage[size];
		}
		@Override
		public FriendCircleImage createFromParcel(Parcel in) {
			return new FriendCircleImage(in);
		}
	};

	public FriendCircleImage(Parcel in) {
		imageUrl = in.readString();
		imageThumbnail = in.readString();
	}
}
