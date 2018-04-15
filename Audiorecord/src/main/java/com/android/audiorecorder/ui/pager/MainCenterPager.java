package com.android.audiorecorder.ui.pager;

import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.dao.UserDao;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.UserManager;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.audiorecorder.utils.StringUtils;
import com.android.audiorecorder.utils.URLS;
import com.android.library.ui.pager.BasePager;
import com.android.library.ui.window.ShareWindow;
import com.android.library.utils.TextUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainCenterPager extends BasePager implements View.OnClickListener {

    private static final int CROWN_NORMAL = 1;//正常
    private static final int CROWN_NULL = 2;//丢失

    private static final int SIGNATURE_CODE = 11;//修改签名
    private ImageView headerIconIv;
    private TextView nicknameTv;
    private TextView signatureTv;
    private TextView crownTv;
    private TextView allureTv;

    private ShareWindow shareWindow;
    private ImageView qqIv;
    private ImageView weixinIv;
    private ImageView fridendIv;
    private UserDao mUserDao;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    
    @Override
    public View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lib_layout_ui_center, null);
        return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserResp user = mUserDao.getUser(getActivity());
        //签名
        if (TextUtils.isEmpty(user.signature)) {
            signatureTv.setText(R.string.center_signature);
        } else {
            signatureTv.setText(user.signature);
        }
        ImageLoader.getInstance().displayImage(URLS.DOMAIN + user.headIcon, headerIconIv, options);
    }

    private void initUI(View view) {
        mUserDao = new UserDao();
        UserResp user = mUserDao.getUser(getActivity());
        int defaultHeaderRes = user.sex == UserManager.SEX_FEMALE ? R.drawable.female_icon1 : R.drawable.male_icon1;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        options = new DisplayImageOptions.Builder()
        //.showImageOnLoading(defaultHeaderRes)
        //.showImageForEmptyUri(defaultHeaderRes)
        .showImageOnFail(defaultHeaderRes)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .bitmapConfig(Config.RGB_565)
        .build();
        
        headerIconIv = (ImageView) view.findViewById(R.id.headerIconIv);
        nicknameTv = (TextView) view.findViewById(R.id.nicknameTv);
        signatureTv = (TextView) view.findViewById(R.id.signatureTv);

        //crownTv = (TextView) findViewById(R.id.crownTv);
        //allureTv = (TextView) findViewById(R.id.allureTv);
        
        ImageLoader.getInstance().displayImage(URLS.DOMAIN + user.headIcon, headerIconIv, options);
        //昵称
        nicknameTv.setText(user.nickName);
        //签名
        if (TextUtils.isEmpty(user.signature)) {
            signatureTv.setText(R.string.center_signature);
        } else {
            signatureTv.setText(user.signature);
        }
        signatureTv.setOnClickListener(this);
        //皇冠
        //if (user.crown == CROWN_NORMAL) {
        //    crownTv.setText("没值传回");
        //} else {
        //    crownTv.setText("丢失");
        //}
        //魅力值
        //allureTv.setText(String.valueOf(user.allureValue));

        //关注
        view.findViewById(R.id.collectRl).setOnClickListener(this);
        //个人中心
        view.findViewById(R.id.headerIconIv).setOnClickListener(this);
        //活动列表
        //view.findViewById(R.id.treatRl).setOnClickListener(this);
        //设置
        view.findViewById(R.id.settingRL).setOnClickListener(this);
        // 账户
        view.findViewById(R.id.centerAcountRl).setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signatureTv) {
            if (StringUtils.getString(activity, StringUtils.KEY_USER_LOGIN_STATUS, "0").equals("1")) {
                ActivityUtil.gotoCenterUpdateSignatureActivity(activity);
            } else {
                ActivityUtil.gotoLoginActivity(activity);
            }

            //个人中心
        } else if (i == R.id.headerIconIv) {
            if (StringUtils.getString(activity, StringUtils.KEY_USER_LOGIN_STATUS, "0").equals("1")) {
                ActivityUtil.gotoCenterUpdateMaterialActicity(activity);
            } else {
                ActivityUtil.gotoLoginActivity(activity);
            }

            //关注
        } else if (i == R.id.collectRl) {//ActivityUtil.gotoPersonAttentionActivity(activity);
            //实例化window
//                shareWindow = new ShareWindow(activity, this);
//                //显示窗口
//                shareWindow.showAtLocation(activity.findViewById(R.id.center),
//                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置

            //活动列表
        } else if (i == R.id.treatRl) {//ActivityUtil.gotoMytreatListActivity(activity);

            //设置
        } else if (i == R.id.settingRL) {
            ActivityUtil.gotoCenterSettingActivity(activity);

        } else if (i == R.id.centerAcountRl) {
            ActivityUtil.gotoCenterAcountActivity(activity);

            //分享测试
//            case R.id.qqIv:
//                UmengShare.qqChat(activity);
//                break;
//            case R.id.weixinIv:
//                UmengShare.weChat(activity);
//                break;
//            case R.id.fridendIv:
//                UmengShare.friendChat(activity);
//                break;
        }
    }

    /*@Override
    protected void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            //个性签名
            case SIGNATURE_CODE:
                String signature = data.getExtras().getString(CenterUpdtSignatureActivity.UPDATE_SIGNATURE);
                signatureTv.setText(signature);
                break;
        }
    }*/
    

    @Override
    public void reload() {

    }
}
