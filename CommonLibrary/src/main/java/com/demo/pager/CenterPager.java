package com.demo.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.pager.BasePager;
import com.android.library.ui.window.ShareWindow;

public class CenterPager extends BasePager implements View.OnClickListener {

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

    @Override
    public void reload() {

    }

    @Override
    public View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lib_layout_ui_center, null);
        return view;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lib_layout_ui_center, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        //UserResp user = AppApplication.getUser();
        //性别
        /*if (user.sex == RespConstants.Sex.SEX_FEMALE) {
            BitmapMgr.loadBigBitmap(headerIconIv, user.headIcon, R.drawable.female_icon1);
        } else {
            BitmapMgr.loadBigBitmap(headerIconIv, user.headIcon, R.drawable.male_icon1);
        }*/
    }

    private void initUI() {
        headerIconIv = (ImageView) findViewById(R.id.headerIconIv);
        /*nicknameTv = (TextView) findViewById(R.id.nicknameTv);
        signatureTv = (TextView) findViewById(R.id.signatureTv);

        crownTv = (TextView) findViewById(R.id.crownTv);
        allureTv = (TextView) findViewById(R.id.allureTv);*/

        //UserResp user = AppApplication.getUser();
        //昵称
        //nicknameTv.setText(user.nickName);
        //签名
        /*if (TextUtils.isEmpty(user.signature)) {
            signatureTv.setText(R.string.center_signature);
        } else {
            signatureTv.setText(user.signature);
        }
        signatureTv.setOnClickListener(this);
        //皇冠
        if (user.crown == CROWN_NORMAL) {
            crownTv.setText("没值传回");
        } else {
            crownTv.setText("丢失");
        }
        //魅力值
        allureTv.setText(String.valueOf(user.allureValue));*/

        //关注
        //findViewById(R.id.collectRl).setOnClickListener(this);
        //个人中心
        //findViewById(R.id.headerIconIv).setOnClickListener(this);
        //活动列表
        //findViewById(R.id.treatRl).setOnClickListener(this);
        //设置
        //findViewById(R.id.settingRL).setOnClickListener(this);
        // 账户
        //findViewById(R.id.centerAcountRl).setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            //个性签名
            case R.id.signatureTv:
                ActivityUtil.gotoCenterUpdtSignatureActivity(activity, this, SIGNATURE_CODE);
                break;
            //个人中心
            case R.id.headerIconIv:
                ActivityUtil.gotoCenterUpdtMaterialActicity(activity);
                break;
            //关注
            case R.id.collectRl:
                ActivityUtil.gotoPersonAttentionActivity(activity);
                //实例化window
//                shareWindow = new ShareWindow(activity, this);
//                //显示窗口
//                shareWindow.showAtLocation(activity.findViewById(R.id.center),
//                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                break;
            //活动列表
            case R.id.treatRl:
                ActivityUtil.gotoMytreatListActivity(activity);
                break;
            //设置
            case R.id.settingRL:
                ActivityUtil.gotoCenterSettingActivity(activity);
                break;
            case R.id.centerAcountRl:
                ActivityUtil.gotoCenterAcountActivity(activity);
                break;
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
        }*/
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
}
