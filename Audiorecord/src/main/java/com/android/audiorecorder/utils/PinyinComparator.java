package com.android.audiorecorder.utils;

import com.android.audiorecorder.ui.data.resp.FriendCircleFriendSummaryResp;

import java.util.Comparator;

public class PinyinComparator implements Comparator<FriendCircleFriendSummaryResp> {

	public int compare(FriendCircleFriendSummaryResp o1, FriendCircleFriendSummaryResp o2) {
		if (o1.getPinyin().equals("@")
				|| o2.getPinyin().equals("#")) {
			return -1;
		} else if (o1.getPinyin().equals("#")
				|| o2.getPinyin().equals("@")) {
			return 1;
		} else {
			return o1.getPinyin().compareTo(o2.getPinyin());
		}
	}

}
