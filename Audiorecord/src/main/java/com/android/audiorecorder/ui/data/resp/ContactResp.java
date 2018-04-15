package com.android.audiorecorder.ui.data.resp;

import com.android.library.net.resp.DataResp;
import com.android.library.utils.TextUtils;

public class ContactResp extends DataResp implements Comparable<ContactResp> {

    public long id;
    
    public int userCode;
    
    public String realName;
    
    public String email;
    
    public String nickName;
    
    public int sex =2;
    
    public String headIcon;

    public String signature;
    
    public String cityCode;
    
    public float balance;
    
    public String rongYunToken;
    
    public int userStatus;
    
    public int type;
    
    public long groupId;
    /**
     * 拼音
     */
    public String pinyin;
    /**
     * 电话号码
     */
    public String mobile;

    /**
     * 拼音首字母  大写
     */
    public String index;

    /**
     * 在选择好友界面保存是否被选中的标签
     */
    public boolean isChecked;

    /**
     * 本地通讯录中好友状态:
     *1:已注册本平台 2.已经是好友了
     */
    public int status;
    
    public int friendId;
    
    public int userType;
    
    public int friendStatus;

    /**
     * 请客号
     */
    public String treamNum;

    public String getPinyin() {
        if (pinyin == null) {
            /*if (!TextUtils.isEmpty(remarkName)) {
                pinyin = PinyinHelper.getInstance().getPinyins(remarkName, "");
            } else {
                pinyin = PinyinHelper.getInstance().getPinyins(nickName, "");
            }*/

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
    public int compareTo(ContactResp another) {
        return getPinyin().compareToIgnoreCase(another.getPinyin());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactResp that = (ContactResp) o;

        return userCode == that.userCode&&mobile==that.mobile;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
