/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: RongUserResp.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年8月1日 下午8:09:18 
 */
package com.android.audiorecorder.ui.data.resp;

import com.android.library.net.resp.DataResp;
import com.android.library.utils.TextUtils;

public class FriendCircleFriendSummaryResp extends DataResp implements Comparable<FriendCircleFriendSummaryResp> {

    public int _id;
    public int userCode;
    public String headIcon;
    public String realName;
    public String nickName;
    public int sex;
    public int userType;
    public int userStatus;
    public int permission;
    
    /**
     * 拼音
     */
    public String pinyin;
    
    /**
     * 拼音首字母  大写
     */
    public String index;
    
    /**
     * 在选择好友界面保存是否被选中的标签
     */
    public boolean isChecked;

    
    public String getPinyin() {
        if (pinyin == null) {
            if (!TextUtils.isEmpty(nickName)) {
                //pinyin = PinyinHelper.getInstance(AppContext.curContext).getPinyins(nickName, "");
            } else {
                //pinyin = PinyinHelper.getInstance(AppContext.curContext).getPinyins(String.valueOf(userCode), "");
            }

            if (TextUtils.isEmpty(pinyin)) {
                pinyin = "#";
                index = "#";
            } else {
                if (pinyin.substring(0,1).matches("[a-zA-Z]+")) {
                    pinyin = pinyin.toUpperCase();
                    index = pinyin.charAt(0) + "";
                } else {
                    pinyin = "#";
                    index = "#";
                }
            }
        }
        return pinyin;
    }


    public String getIndex() {
        if (index == null) {
            if (pinyin == null)
                pinyin = getPinyin();
            if (TextUtils.isEmpty(pinyin)) {
                index = "#";
            } else {
                index = pinyin.charAt(0) + "";
            }
        }
        return index;
    }
    
    @Override
    public int compareTo(FriendCircleFriendSummaryResp another) {
        return getPinyin().compareToIgnoreCase(another.getPinyin());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendCircleFriendSummaryResp that = (FriendCircleFriendSummaryResp) o;

        return userCode == that.userCode;

    }

    @Override
    public int hashCode() {
        return (int) (userCode ^ (userCode >>> 32));
    }
}
