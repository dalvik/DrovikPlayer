package com.android.audiorecorder.ui.data.resp;

import android.content.Context;

import com.android.audiorecorder.R;
import com.android.library.net.resp.DataResp;

public class UserResp extends DataResp {

    public int id;
    /**
     * 业务唯一数字ID
     */
    public int userCode;
    
    public String email;
    
    /**
     * 手机号
     */
    public String telephone;
    
    /**
     * 昵称
     */
    public String nickName;
    
    /**
     * 性别
     */
    public int sex;
    /**
     * 生日
     */
    public long birthday;
    /**
     * 皇冠@1:正常|2: 丢失
     */
    public int crown;
    /**
     * 鸽子
     */
    public int pigeon;
    /**
     * 身高(cm)
     */
    public int height;
    /**
     * 体重(kg)
     */
    public int weight;
    /**
     * 头像
     */
    public String headIcon;

    /**
     * 行业@
     */
    public long vocation;
    /**
     * 公司名称
     */
    public String company;
    /**
     * 学校
     */
    public String school;
    /**
     * 个性签名
     */
    public String signature;
    /**
     * 兴趣 @
     */
    public long interest;
    /**
     * 城市编码
     */
    public String cityCode;
    /**
     * 省份编码
     */
    public String provinceCode;
    /**
     * 魅力值
     */
    public int allureValue;
    /**
     * 技能值
     */
    public int techniqueValue;
    /**
     * 余额
     */
    public float balance;
    /**
     * 技能
     */
    public long technique;

    /**
     * 是否首资登录
     */
    public boolean firstLogin;

    /**
     * 融云token
     */
    public String rongYunToken;
    /**
     * 用户地理位置
     */
    //public DeviceInfo deviceInfo;

    /**
     * 活动数量
     */
    public int activityCount;
    /**
     * 土豪
     */
    public int rich;

    public int isFristLogin;
    
    public int userStatus;
    
    public int type;
    
    public static String getVocation(long vocation) {
        return "计算机/软件";
    }

    public static String getTechnique(long technique) {
        return "摄影";
    }

    /**
     * @param sex
     * @return
     */
    public static String getSexText(Context context, int sex) {
        if (sex == 1) {
            return context.getString(R.string.female);
        } else {
            return context.getString(R.string.male);
        }
    }

    /**
     * 获取兴趣名称
     * @return
     */
    public String getInterest() {
        return "";//DataResource.getInstance().getInterest().getInterest(interest);
    }

    //魅力值
    public int getAllureValue() {
        if (allureValue <= 1000) {
            return 1;
        } else if (allureValue <= 10000) {
            return 2;
        } else if (allureValue <= 10 * 10000) {
            return 3;
        } else if (allureValue <= 100 * 10000) {
            return 4;
        } else {
            return 5;
        }
    }

    //土豪
    public int getRich() {
        if (rich <= 50) {
            return 0;
        } else if (rich <= 200) {
            return 1;
        } else if (rich <= 1000) {
            return 2;
        } else if (rich <= 5000) {
            return 3;
        } else {
            return 4;
        }
    }
}
