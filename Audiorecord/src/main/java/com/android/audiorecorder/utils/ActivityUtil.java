package com.android.audiorecorder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.audiorecorder.ui.CenterSettingsActivity;
import com.android.audiorecorder.ui.MainFrameActivity;
import com.android.audiorecorder.ui.activity.CenterChoosAddressActivity;
import com.android.audiorecorder.ui.activity.CenterChoosCompanyActivity;
import com.android.audiorecorder.ui.activity.CenterChoosIndustryActivity;
import com.android.audiorecorder.ui.activity.CenterChoosInterestActivity;
import com.android.audiorecorder.ui.activity.CenterChoosSchoolActivity;
import com.android.audiorecorder.ui.activity.CenterChoosSkillActivity;
import com.android.audiorecorder.ui.activity.CenterUpdtBirthdayActivity;
import com.android.audiorecorder.ui.activity.CenterUpdtMaterialActicity;
import com.android.audiorecorder.ui.activity.CenterUpdtNickNameActivity;
import com.android.audiorecorder.ui.activity.CenterUpdtSignatureActivity;
import com.android.audiorecorder.ui.activity.ContactsAddActivity;
import com.android.audiorecorder.ui.activity.FriendCircleActiveListActivity;
import com.android.audiorecorder.ui.activity.FriendCircleActivePublishActivity;
import com.android.audiorecorder.ui.activity.FriendCircleContactsActivity;
import com.android.audiorecorder.ui.activity.FriendDetailActivity;
import com.android.audiorecorder.ui.activity.HisPersonalAddNewActivity;
import com.android.audiorecorder.ui.activity.HisPersonalCenterActivity;
import com.android.audiorecorder.ui.activity.LoginActivity;
import com.android.audiorecorder.ui.activity.RegisterActivity;
import com.android.audiorecorder.ui.activity.ShowBigImageActivity;
import com.android.library.ui.utils.ActivityUtils;
import com.android.library.utils.TextUtils;

import io.rong.imkit.RongIM;

//import com.android.audiorecorder.ui.activity.ContactsActivity;

public class ActivityUtil extends ActivityUtils {

    /**
     * 第三方登录参数
     **/
    public static final String INTENT_THIRD_REQ = "third_args";
    /**
     * 页信息
     **/
    public static final String INTENT_PAGER = "pager";
    /**
     * 首次登录
     **/
    public static final String INTENT_FIRSTLOGIN = "firstLogin";
    /**
     * POI信息
     **/
    public static final String INTENT_POI = "poi";
    /**
     * 单一数据
     **/
    public static final String INTENT_OBJECT = "simple";

    public static final String INTENT_BEAN = "bean";
    /**
     * URL
     */
    public static final String INTENT_URL = "url";
    /**
     * 坐标
     */
    public static final String INTENT_LATLNG = "LATLNG";
    public static final String INTENT_TITLE = "title";
    public static final String INTENT_CONTENT = "content";

    /**
     * 感应
     */
    public static final String INTENT_SENSE = "sense";
    /**
     * 处理
     */
    public static final String INTENT_DISPOSE = "dispose";
    public static final String INTENT_ROLE = "role";
    /**
     * 评价
     */
    public static final String INTENT_EVALUATE = "evaluate";

    /**
     * 用户ID
     */
    public static final String USER_ID = "USER_ID";

    /**
     * 图片地址列表
     */
    public static final String IMAGE_LIST = "IMAGE_LIST";
    
    public static final String CHOOSE_CONTACTS = "CHOOSE_CONTACTS";
    public static final String CONTACTS_LIST = "CONTACTS_LIST";
    
    /**
     * check register
     *
     * @param activity
     */
    public static void gotoRegisterActivity(Activity activity, String mobile, int pageIndex) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        if (!TextUtils.isEmpty(mobile)) {
            intent.putExtra(INTENT_MOBILE, mobile);
        }
        if (pageIndex != 0) {
            intent.putExtra(ActivityUtil.INTENT_PAGER, pageIndex);
        }
        startActivity(activity, intent);
    }
    /**
    * goto login activity
    */
    public static void gotoLoginActivity(Activity activity){
        startActivity(activity, LoginActivity.class);
    }
    
    /**
     * 找回密码
     *
     * @param activity
     * @param mobile
     */
    public static void gotoForgotPwdActivity(Activity activity, String mobile) {
        Intent intent = null;//new Intent(activity, ForgotPwdActivity.class);
        intent.putExtra(INTENT_MOBILE, mobile);
        startActivity(activity, intent);
    }
    /**
     * 跳转主界面
     *
     * @param activity
     * @param isFirst
     */
    public static void gotoMainActivity(Activity activity, boolean isFirst) {
        // 连接融云
        //RongCloudContext.connect(AppApplication.getRongCloudToken());
        Intent intent = new Intent(activity, MainFrameActivity.class);
        intent.putExtra(INTENT_FIRSTLOGIN, isFirst);
        startActivity(activity, intent);
    }
    
    public static void gotoContactsActivity(Activity activity){
        startActivity(activity, FriendCircleContactsActivity.class);
    }
    
    public static void gotoChooseContactsActivity(Activity activity, int requestCode) {
        Intent intent = null;//new Intent(activity, ContactsActivity.class);
        intent.putExtra(CHOOSE_CONTACTS, true);
        startActivityForResult(activity, intent, requestCode);
    }
    
    public static void gotoContactsAddActivity(Activity activity, int userId) {
        Intent intent = new Intent(activity, ContactsAddActivity.class);
        intent.putExtra(USER_ID, userId);
        startActivity(activity, intent);
    }
    
    public static void gotoContactsAddActivity(Activity activity) {
        startActivity(activity, ContactsAddActivity.class);
    }
    
    public static void gotoFriendCircleListActivity(Context activity, int userId) {
        Intent intent = new Intent(activity, FriendCircleActiveListActivity.class);
        intent.putExtra(USER_ID, userId);
        activity.startActivity(intent);
    }
    
    public static void gotoPublishFirendCircleActivity(Activity activity, int userCode, int requestCode){
    	Intent intent = new Intent(activity, FriendCircleActivePublishActivity.class);
		intent.putExtra(ActivityUtil.INTENT_OBJECT, userCode);
		startActivityForResult(activity, intent, requestCode);
    }
    
    public static void gotoHisPersonalCeneterActivity(Context activity, int userId) {
        Intent intent = new Intent(activity, HisPersonalCenterActivity.class);
        intent.putExtra(USER_ID, userId);
        activity.startActivity(intent);
        //startActivity(activity, intent);
    }
    
    public static void gotoHisPersonalCeneterActivity(Activity activity, int userId) {
        Intent intent = new Intent(activity, HisPersonalCenterActivity.class);
        intent.putExtra(USER_ID, userId);
        startActivity(activity, intent);
    }
    
    public static void gotoPersonalDetailActivity(Activity activity, int userId) {
        Intent intent = new Intent(activity, FriendDetailActivity.class);
        intent.putExtra(USER_ID, userId);
        startActivity(activity, intent);
    }
    
    public static void gotoPersonalDetailActivity(Context activity, int userId) {
        Intent intent = new Intent(activity, FriendDetailActivity.class);
        intent.putExtra(USER_ID, userId);
        activity.startActivity(intent);
    }
    
    public static void gotoHisPersonalAddMessageActivity(Activity activity, int userId) {
        Intent intent = new Intent(activity, HisPersonalAddNewActivity.class);
        intent.putExtra(USER_ID, userId);
        startActivity(activity, intent);
    }
    
    /**
     * update signature
     */
    public static void gotoCenterUpdateSignatureActivity(Activity activity) {
        startActivity(activity, CenterUpdtSignatureActivity.class);
    }
    
    /**
     * update material
     */
    public static void gotoCenterUpdateMaterialActicity(Activity activity) {
        startActivity(activity, CenterUpdtMaterialActicity.class);
    }
    
    public static void gotoBitImageHeaderActicity(Activity activity, String url) {
    	Intent intent = new Intent(activity, ShowBigImageActivity.class);
        intent.putExtra(INTENT_URL, url);
        startActivity(activity, intent);
    }
    
    /**
     * update nickname
     */
    public static void gotoCenterUpdtNickNameActivity(Activity activity, int requestCode){
        startActivityForResult(activity, CenterUpdtNickNameActivity.class, requestCode);
    }
    
    /**
     * 修改生日
     *
     * @param activity
     * @param requestCode
     */
    public static void gotoCenterUpdtBirthdayActivity(Activity activity, int requestCode, long birthday) {
        Intent intent = new Intent(activity, CenterUpdtBirthdayActivity.class);
        intent.putExtra(ActivityUtil.INTENT_SIMPLE, birthday);
        startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 修改居住地
     *
     * @param activity
     * @param requestCode
     */
    public static void gotoCenterUpdtAddressActivity(Activity activity, int requestCode) {
        startActivityForResult(activity, CenterChoosAddressActivity.class, requestCode);
    }

    /**
     * 选择兴趣
     *
     * @param activity
     * @param requestCode
     */
    public static void gotoCenterUpdtInterestActivity(Activity activity, long interest, int requestCode) {
        Intent intent = new Intent(activity, CenterChoosInterestActivity.class);
        intent.putExtra(ActivityUtil.INTENT_SIMPLE, interest);
        startActivityForResult(activity, intent, requestCode);
    }

    /**
     * 选择学校
     *
     * @param activity
     * @param requestCode
     */
    public static void gotoCenterUpdtSchoolActivity(Activity activity, int requestCode) {
        startActivityForResult(activity, CenterChoosSchoolActivity.class, requestCode);
    }

    /**
     * 选择公司
     *
     * @param activity
     * @param requestCode
     */
    public static void gotoCenterChoosCompanyActivity(Activity activity, int requestCode) {
        startActivityForResult(activity, CenterChoosCompanyActivity.class, requestCode);
    }

    /**
     * 选择技能
     *
     * @param activity
     * @param requestCode
     */
    public static void gotoCenterChoosSkillActivity(Activity activity, int requestCode) {
        startActivityForResult(activity, CenterChoosSkillActivity.class, requestCode);
    }

    /**
     * 选择行业
     *
     * @param activity
     * @param requestCode
     */
    public static void gotoCenterChoosIndustryActivity(Activity activity, int requestCode) {
        startActivityForResult(activity, CenterChoosIndustryActivity.class, requestCode);
    }

    /**
     * 收藏 关注
     *
     * @param activity
     */
    public static void gotoPersonAttentionActivity(Activity activity) {
        //startActivity(activity, PersonAttentionActivity.class);
    }
    
    /**
     * goto center settings
     */
    public static void gotoCenterSettingActivity(Activity activity) {
        startActivity(activity, CenterSettingsActivity.class);
    }
    
    /**
     * goto center account
     */
    public static void gotoCenterAcountActivity(Activity activity) {
        startActivity(activity, CenterSettingsActivity.class);
    }
    
    /**
     * single chat
     *
     * @param activity
     * @param userId
     */
    public static void gotoChatDetailActivity(Activity activity, Long userId, String nickName) {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(activity, String.valueOf(userId), nickName);
        }
    }

    /**
     * group chat
     *
     * @param activity
     * @param groupId
     * @param title
     */
    public static void gotoGroupDetailActivity(Activity activity, Long groupId, String title) {
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startGroupChat(activity, String.valueOf(groupId), title);
        }
    }
    
    public static void startActivityFromFragmentForResult(FragmentActivity activity, Fragment fragment, Class<?> clazz, int requestCode) {
        Intent intent = new Intent(activity, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityFromFragment(activity, fragment, intent, requestCode);
    }
}
