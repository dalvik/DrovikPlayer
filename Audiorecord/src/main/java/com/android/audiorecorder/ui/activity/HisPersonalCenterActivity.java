package com.android.audiorecorder.ui.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.resp.FriendCircleFriendDetailResp;
import com.android.audiorecorder.ui.data.resp.HisPersonalCenterResp;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.HisPersonalCenterManager;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.audiorecorder.utils.DateFormat;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.DataResource;
import com.android.library.ui.view.RoundImageView;
import com.android.library.ui.window.ShareWindow;
import com.android.library.utils.TextUtils;

import java.util.Date;

public class HisPersonalCenterActivity extends BaseCompatActivity implements OnClickListener {
    
    public static final int PRISE_SHOW_FLG = 1;//点赞
    public final static int OPERATION_FLG = 3;//更多
    private static final int REQUEST_CODE_GIFT = 0x10;

    private final int findDataHide = 1;//隐藏急速约布局
    private final int findDataShow = 2;//显示急速约布局
    private HisPersonalCenterManager mHisPersonalCenterManager;
    private RoundImageView ivHeaderIcon;
    private ImageView ivDiamond;
    private int whatGetUserDetail;
    private Button btnAct;
    private TextView tvActCount;
    private TextView tvRp;
    private TextView tvCrownCount;
    private TextView tvCharmCount;
    private TextView tvNick;
    private ImageView ivSex;
    private TextView tvAge;
    private TextView tvStar;
    private TextView tvOfflineRate;
    private TextView tvSignature;
    private LinearLayout layoutAppraise;
    private View tvNoappraise;
    private int getShowsList;
    private RelativeLayout layoutSpeedData;
    private TextView tvSpeedDataType;
    private TextView tvSpeedDataTreatOffline;
    private TextView tvSpeedDataTreatOnline;
    private boolean isFriend = false;//是否是好友关系
    private boolean isAttention = false;//是否关注
    private int userId;
    private TextView rightView;
    private ShareWindow shareWindow;//分享窗口
    //private HisPersonalCenterMoreWindow moreWindow;
    private int deleteFrind;
    private int whatPriseShow;
    private BaseData<HisPersonalCenterResp> userResp;
    private Button rightSecondView;
    private int whatAddAttention;
    private int whatTreatExits;
    private ListView listView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /*case PRISE_SHOW_FLG:
                    showResp = (ShowsResp) msg.obj;
                    ShowPriseManager showPriseManager = new ShowPriseManager(uiCallBack);
                    whatPriseShow = showPriseManager.prise(showResp.showId, showResp.userId);
                    break;
                case OPERATION_FLG:
                    showResp = (ShowsResp) msg.obj;
                    //初始化window
                    showListOperationWindow = new ShowListOperationWindow(activity);
                    showListOperationWindow.getWindow().showAtLocation(layoutSpeedData,
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;*/
            }
        }
    };
    private RelativeLayout chatRl;
    private TextView chatTv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_his_personal_center);
        setTitle(R.string.his_personal_center_title);
        initUI();
        mHisPersonalCenterManager = new HisPersonalCenterManager(this);
        getUserDetail();
    }

    @Override
    protected boolean initIntent() {
        userId = getIntent().getIntExtra(ActivityUtil.USER_ID, 0);
        if (userId == 0) {
            return false;
        }
        return true;
    }
    
    private void getUserDetail() {
        whatGetUserDetail = mHisPersonalCenterManager.getUserDetailByUserId(userId);
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        if (what == whatGetUserDetail) {
            switch (result) {
                case HisPersonalCenterManager.RESULT_SUCCESS:
                    userResp = (BaseData<HisPersonalCenterResp>) obj;
                    HisPersonalCenterResp pcr = userResp.data;
                    updateUI(pcr);
                    UserResp user = pcr.dbUsers;
                    //修改好友信息
                    if (DataResource.getInstance().isContactById(user.userCode)) {
                        FriendCircleFriendDetailResp contact = new FriendCircleFriendDetailResp();
                        contact.nickName = user.nickName;
                        contact.headIcon = user.headIcon;
                        contact.userCode = user.userCode;
                        //contact.pigeon = user.pigeon;
                        //contact.crown = user.crown;
                        //contact.treamNum = user.treamNum;
                        contact.sex = user.sex;
                        //ContactDAO.getInstance().update(contact);
                    }
                    // 修改融云用户
                    //DBUser dbuser = UserDAO.getInstance().getUser(user.userId + "");
                    //if (dbuser != null) {
                    //    dbuser.portrait = user.headIcon;
                    //    dbuser.username = user.nickName;
                    //    UserDAO.getInstance().update(dbuser);
                    //}
                    // update rongyun user info
                    mHisPersonalCenterManager.refreshUser(userId, user.nickName, user.headIcon);
                    break;
                default:
                    return false;


            }
        } else if (what == getShowsList) {
            switch (result) {
            /*    case HisPersonalCenterManager.RESULT_SUCCESS:
                    BasePagesResp<ShowsResp> resp = ((BaseData<BasePagesResp<ShowsResp>>) obj).data;
                    if (isFirstPage()) {
                        adapter.setItems(resp.rows);
                    } else {
                        adapter.addItems(resp.rows);
                    }
                    setPage(resp.total, resp.length);
                    break;
                default:
                    return false;*/

            }

        } else if (what == whatPriseShow) {
            //点赞
            /*switch (result) {
                case ShowPriseManager.RESULT_SUCCESS:
                    for (int i = 0; i < adapter.getItems().size(); i++) {
                        if (adapter.getItems().get(i).equals(showResp)) {
                            adapter.getItems().get(i).priseFlag = true;
                            adapter.getItems().get(i).priseNum += 1;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case ShowPriseManager.RESULT_FAILED:
                    for (int i = 0; i < adapter.getItems().size(); i++) {
                        if (adapter.getItems().get(i).equals(showResp)) {
                            adapter.getItems().get(i).priseFlag = false;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    return false;
            }*/


        } else if (what == deleteFrind) {
            /*switch (result) {
                case ContactManager.RESULT_SUCCESS:
                    //好友删除成功
                    //删除本地数据库
                    ContactDAO.getInstance().delete(userId);
                    //删除好友列表
                    DataResource.getInstance().deleteContacts(userId);
                    ContactsActivity.isNeedRefreshContacts = true;
                    finish();
                    break;
                default:
                    return false;
            }*/


        } else if (what == whatAddAttention) {
            /*switch (result) {
                case AttentionManager.RESULT_SUCCESS:
                    //关注成功, 改变背景, 改变标记值
                    isAttention = true;
                    setOptionLeftView(rightSecondView);
                    break;
                default:
                    return false;
            }*/

        } else if (what == whatTreatExits) {
            //线下
            /*switch (result) {
                case TreatExitsManager.RESULT_SUCCESS:
                    String string = ((BaseObjectData<String>) obj).data;
                    if (StringUtils.isEmpty(string)) {
                        ActivityUtil.gotoPublishOfflineTreat(this, userResp.data.dbUsers.userId, userResp.data.dbUsers.nickName);
                    } else {
                        showDialog(DialogUtil.showTreatExitsDialog(activity, string));
                    }
                    break;
                default:
                    return false;
            }*/
        }
        return true;
    }

    /**
     * 更新UI
     *
     * @param data
     */
    private void updateUI(HisPersonalCenterResp data) {
       // HeadUtil.loadHeadIcon(data.dbUsers.headIcon, ivHeaderIcon, data.dbUsers.sex);
        // 修改消息头像
        //RongCloudContext.getInstance().updateUserIcon("" + data.dbUsers.userId, data.dbUsers.headIcon);
        //ivDiamond 土豪
        //活动数量
        tvActCount.setText(activity.getString(R.string.his_personal_center_actcount, data.dbUsers.activityCount));
        //皇冠数量
        /*if (data.dbUsers.crown == RespConstants.Crown.CROWN_NORMAL) {
            tvCrownCount.setBackgroundResource(R.drawable.nearby_item_crown);
            tvCrownCount.setText("1");
        } else {
            tvCrownCount.setBackgroundResource(R.drawable.nearby_item_pigeon);
            tvCrownCount.setText(Math.abs(data.dbUsers.pigeon) + "");
        }*/
        //魅力值
        //tvCharmCount.setBackgroundResource(R.drawable.his_personal_center_charm);
        tvCharmCount.setText("" + data.dbUsers.getAllureValue());
        //人品
        tvRp.setText(activity.getString(R.string.his_personal_center_rp, data.pariseRate));
        //昵称
        tvNick.setText(data.dbUsers.nickName);
        //性别
        /*if (data.dbUsers.sex == RespConstants.Sex.SEX_FEMALE) {
            ivSex.setBackgroundResource(R.drawable.nearby_female);
        } else {
            ivSex.setBackgroundResource(R.drawable.nearby_male);
        }*/
        Date birthday = new Date(data.dbUsers.birthday);
        //年龄
        tvAge.setText(DateFormat.getAge(birthday) + "岁");
        //星座
        tvStar.setText(DateFormat.getStarSign(birthday));
        //线下好评率
        tvOfflineRate.setText(activity.getString(R.string.his_personal_center_offline_rate, data.userPariseRate));
        //个性签名
        if (TextUtils.isNotEmpty(data.dbUsers.signature))
            tvSignature.setText(data.dbUsers.signature);
        //评价列表
        //updatePraiseList(data.praiseList);


        //判断是否是好友,右上角按钮
        isFriend = data.existFriends;

        setOptionView(rightView);
        if (data.followerId == 0) {
            isAttention = false;
        } else {
            isAttention = true;
        }

        setOptionLeftView(rightSecondView);

        if (!isFriend) {
            //急速约布局 当有急速约时
            /*chatRl.setVisibility(View.GONE);
            layoutSpeedData.setVisibility(View.VISIBLE);
            if (data.userSpeedDating == null) {
                tvSpeedDataType.setVisibility(View.GONE);
            } else {
                tvSpeedDataType.setText(data.getSpeedDating());
            }*/
            //设置listView滚动监听
            //setTouchEvent();

        } else {
            chatRl.setVisibility(View.VISIBLE);
            layoutSpeedData.setVisibility(View.GONE);
        }
    }

    /*private void updatePraiseList(ArrayList<HisPersonalCenterResp.AppraiseResp> praiseList) {
        // 评价数据
        RateInfo rateInfo = DataResource.getInstance().getRate();
        layoutAppraise.removeAllViews();
        if (praiseList == null) {
            layoutAppraise.setVisibility(View.GONE);
            tvNoappraise.setVisibility(View.VISIBLE);
            return;
        }
        if (praiseList.size() != 0) {
            RateInfo.RateBean rate = null;
            for (HisPersonalCenterResp.AppraiseResp appraiseResp : praiseList) {
                rate = rateInfo.getRateBean(appraiseResp.appraiseType);
                if (rate == null) {
                    continue;
                }
                View view = LayoutInflater.from(activity).inflate(R.layout.his_personal_center_appraise_item, null);
                TextView his_personal_center_appraise_item_name = (TextView) view.findViewById(R.id.his_personal_center_appraise_item_name);
                TextView his_personal_center_appraise_item_count = (TextView) view.findViewById(R.id.his_personal_center_appraise_item_count);


                if (rate.isGood) {
                    //好的评价
                    his_personal_center_appraise_item_name.setText(rate.name);
                } else {
                    //不好的
                    his_personal_center_appraise_item_name.setBackgroundResource(R.drawable.his_personal_center_appraise_item_tvbg_gray);
                    his_personal_center_appraise_item_name.setTextColor(getResources().getColor(R.color.white));
                    his_personal_center_appraise_item_name.setText(rate.name);
                }
                his_personal_center_appraise_item_count.setText("" + appraiseResp.count);
                layoutAppraise.addView(view);
            }

        } else {
            layoutAppraise.setVisibility(View.GONE);
            tvNoappraise.setVisibility(View.VISIBLE);

        }


    }*/

    private void initUI() {
        listView = (ListView) findViewById(R.id.listView);
        //急速约布局
        layoutSpeedData = (RelativeLayout) findViewById(R.id.his_personal_center_layout_speeddata);
        tvSpeedDataType = (TextView) findViewById(R.id.his_personal_center_tv_speeddata_type);
        tvSpeedDataTreatOffline = (TextView) findViewById(R.id.his_personal_center_tv_speeddata_treatoffline);
        tvSpeedDataTreatOffline.setOnClickListener(this);
        tvSpeedDataTreatOnline = (TextView) findViewById(R.id.his_personal_center_tv_speeddata_treatonline);
        tvSpeedDataTreatOnline.setOnClickListener(this);
        //好友关系时显示聊天
        chatRl = (RelativeLayout) findViewById(R.id.his_personal_center_layout_chat);
        chatTv = (TextView) findViewById(R.id.his_personal_center_more_chat);
        chatTv.setOnClickListener(this);

        //ListView头部布局
        /*View headerView = LayoutInflater.from(activity).inflate(R.layout.his_personal_center_header, null);
        initHeaderView(headerView);
        listView.addHeaderView(headerView);
        adapter = new HisShowsListAdapter(activity, handler);
        setListView(listView, adapter);*/
    }

    private void setTouchEvent() {
        listView.setOnTouchListener(new View.OnTouchListener() {
            public boolean isHiding;//正在隐藏
            public boolean isHided;//已经隐藏
            public boolean isShowing;//正在显示
            public boolean isShowed;//已经显示
            float downX = 0, downY = 0;
            long upTime;
            Runnable runnable;
            /**
             * 隐藏动画
             */
            ObjectAnimator animatorHide = ObjectAnimator.ofFloat(layoutSpeedData, "alpha", 1f, 0f).setDuration(200);
            /**
             * 显示动画
             */
            ObjectAnimator animatorShow = ObjectAnimator.ofFloat(layoutSpeedData, "alpha", 0f, 1f).setDuration(200);

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        //如果手指抬起时间不足1s,则取消显示急速约布局
                        if (System.currentTimeMillis() - upTime < 500) {
                            handler.removeCallbacks(runnable);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //如果手指抬起时间不足1s,则取消显示急速约布局
                        if (System.currentTimeMillis() - upTime < 1000) {
                            handler.removeCallbacks(runnable);
                        }
                        //滚动时判断滚动方向,垂直滑动时隐藏急速约布局
                        if (Math.abs(event.getY() - downY) > 20) {
                            changeFindDataView(findDataHide);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        //手指离开屏幕时 延迟1000ms执行动画
                        handler.removeCallbacks(runnable);
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                changeFindDataView(findDataShow);
                            }
                        };
                        handler.postDelayed(runnable, 1000);
                        upTime = System.currentTimeMillis();
                        break;
                }
                return false;
            }

            private void changeFindDataView(int type) {
                switch (type) {
                    case findDataHide:
                        if (isHiding || isHided)
                            return;
                        if (isShowing)
                            animatorShow.cancel();
                        animatorHide.start();
                        animatorHide.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                isHiding = true;
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isHiding = false;
                                isHided = true;
                                isShowed = false;

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        break;
                    case findDataShow:
                        if (isShowing || isShowed)
                            return;
                        if (isHiding)
                            animatorHide.cancel();
                        animatorShow.start();
                        animatorShow.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                isShowing = true;

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                isShowing = false;
                                isHided = false;
                                isShowed = true;

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });

                        break;
                }


            }
        });
    }

    private void initHeaderView(View headerView) {
        //头像
        /*ivHeaderIcon = (RoundImageView) headerView.findViewById(R.id.his_personal_center_header_iv_headerIcon);
        //钻石
        ivDiamond = (ImageView) headerView.findViewById(R.id.his_personal_center_header_iv_diamond);
        //活动
        btnAct = (Button) headerView.findViewById(R.id.his_personal_center_header_btn_act);
        btnAct.setOnClickListener(this);
        //活动数量
        tvActCount = (TextView) headerView.findViewById(R.id.his_personal_center_header_tv_act_count);
        //人品
        tvRp = (TextView) headerView.findViewById(R.id.his_personal_center_header_tv_rp);
        //皇冠数量
        tvCrownCount = (TextView) headerView.findViewById(R.id.his_personal_center_header_tv_crown_count);
        //魅力值
        tvCharmCount = (TextView) headerView.findViewById(R.id.his_personal_center_header_tv_charm_count);
        //昵称
        tvNick = (TextView) headerView.findViewById(R.id.his_personal_center_header_tv_nick);
        //性别
        ivSex = (ImageView) headerView.findViewById(R.id.his_personal_center_header_iv_sex);
        //年龄
        tvAge = (TextView) headerView.findViewById(R.id.his_personal_center_header_tv_age);
        //年龄
        tvStar = (TextView) headerView.findViewById(R.id.his_personal_center_header_tv_star);
        //线下好评率
        tvOfflineRate = (TextView) headerView.findViewById(R.id.his_personal_center_header_tv_offline_rate);
        //个性签名
        tvSignature = (TextView) headerView.findViewById(R.id.his_personal_center_header_tv_signature);
        //评价列表
        layoutAppraise = (LinearLayout) headerView.findViewById(R.id.his_personal_center_header_layout_appraise);
        //无评论时显示
        tvNoappraise = headerView.findViewById(R.id.his_personal_center_header_tv_noappraise);
*/
    }

    @Override
    protected void setOptionView(TextView option) {
        rightView = option;
        /*if (!isFriend) {
            option.setBackgroundResource(R.drawable.treat_detail_share);
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showShareWindow(1);

                }
            });
        } else {
            //是好友关系时隐藏关注按钮
            option.setBackgroundResource(R.drawable.his_personal_center_point);
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 实例化window
                    moreWindow = new HisPersonalCenterMoreWindow((HisPersonalCenterActivity) activity);
                    //显示窗口
                    moreWindow.getWindow().showAtLocation(layoutSpeedData,
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                }
            });
        }*/


    }


    /**
     * @param type 1: 用户  2 秀场
     */
    public void showShareWindow(int type) {
        if (shareWindow != null) {
            shareWindow.getWindow().dismiss();
        }
        /*ShareBean shareBean = new ShareBean();
        if (type == 1) {
            shareBean.imageType = ShareBean.ImageType.URL;
            shareBean.image = ShareBean.getWebUri(OSSUtil.get144(userResp.data.dbUsers.headIcon));
            String nickName = userResp.data.dbUsers.nickName;
            shareBean.title = activity.getString(R.string.share_user_title, nickName);
            shareBean.content = activity.getString(R.string.share_user_content, nickName);
            shareBean.targetUrl = DataResource.getInstance().getServerResource().getUserInfoUrl(userResp.data.dbUsers.userId);
        } else {
            shareBean.imageType = ShareBean.ImageType.URL;
            shareBean.image = ShareBean.getWebUri(OSSUtil.get144(showResp.headIcon));
            shareBean.title = activity.getString(R.string.share_show_title, showResp.nickName);
            shareBean.content = showResp.signature;
            if (TextUtils.isEmpty(shareBean.content)) {
                shareBean.content = getString(R.string.share_show_content);
            }
            shareBean.targetUrl = DataResource.getInstance().getServerResource().getShowInfoUrl(showResp.userId, showResp.showId);
        }
        // 实例化window
        shareWindow = new ShareWindow(activity, shareBean);
        //显示窗口
        shareWindow.initWindow().showAtLocation(layoutSpeedData,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);*/ //设置layout在PopupWindow中显示的位置
    }

    /**
     * 右边第二个按钮
     *
     * @param optionLeft
     */
    protected void setOptionLeftView(Button optionLeft) {
        rightSecondView = optionLeft;
        if (isFriend) {
            optionLeft.setVisibility(View.GONE);
            return;
        }
        rightSecondView.setVisibility(View.VISIBLE);
        if (!isAttention) {
            /*optionLeft.setBackgroundResource(R.drawable.his_personal_center_attention_normal);
            optionLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关注
                    showWaitingDialog();
                    whatAddAttention = new AttentionManager(uiCallBack).addAttention(userResp.data.dbUsers.userId);
                }
            });*/
        } else {
            //optionLeft.setBackgroundResource(R.drawable.his_personal_center_attention_focus);
            /*optionLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消关注
                }
            });*/
        }
    }

   /* @Override
    public void onClickItemLong(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClickItem(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void getMoreListItem() {
        //getList();
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.his_personal_center_tv_speeddata_treatoffline:
                //线下请
                //ActivityUtil.gotoPublishOfflineTreat(activity);
                whatTreatExits = treatExitsManager.treatExits(userResp.data.dbUsers.userId);
                break;
            case R.id.his_personal_center_tv_speeddata_treatonline:
                //线上请
                ActivityUtil.gotoMessageGift(activity, userId, userResp.data.dbUsers.headIcon, REQUEST_CODE_GIFT);
                break;
            case R.id.his_personal_center_header_btn_act:
                //活动
                ActivityUtil.gotoHisTreatListActivity(activity, userId);
                break;
            case R.id.his_personal_center_more_chat:
                gotoChatActivity();
                break;*/
        }

    }


    public void deleteFriend() {
        /*showConfirmDialog(R.string.his_personal_center_deletefriend_content, R.string.his_personal_center_deletefriend_title, R.string.his_personal_center_deletefriend_confirm, R.string.his_personal_center_deletefriend_cancel, new NormalDialog.OnResultListener() {
            @Override
            public void onConfirm() {
                ContactManager contactManager = new ContactManager(uiCallBack);
                deleteFrind = contactManager.deleteFriend(userId);
            }

            @Override
            public void onCancel() {

            }
        });*/

    }

    public void gotoChatActivity() {
        //ActivityUtil.gotoChatDetailActivity(activity, userId, userResp.data.nickName);

    }

    /**
     * 线下请
     */
    public void treatExitsWindow() {
        //whatTreatExits = treatExitsManager.treatExits(userResp.data.dbUsers.userId);
    }

    public void sendGift() {
        //ActivityUtil.gotoMessageGift(activity, userId, userResp.data.dbUsers.headIcon, REQUEST_CODE_GIFT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GIFT://送礼
                /*if (RongIM.getInstance().getRongIMClient() != null && resultCode == Activity.RESULT_OK) {

                    final GiftMessage message = data.getParcelableExtra(ActivityUtil.INTENT_BEAN);
                    RongIM.getInstance().getRongIMClient().sendMessage(Conversation.ConversationType.PRIVATE,
                            userId + "", message, "", "", new RongIMClient.SendMessageCallback() {
                                @Override
                                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                                    LogUtil.e("GiftProvider", "onError--" + errorCode);
                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    LogUtil.e("GiftProvider", "onSuccess--" + integer);
                                }
                            }, new RongIMClient.ResultCallback<io.rong.imlib.model.Message>() {
                                @Override
                                public void onSuccess(io.rong.imlib.model.Message message) {
                                    LogUtil.e("ResultCallback", "onSuccess--" + message);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    LogUtil.e("ResultCallback", "onError--" + message);
                                }
                            });
                }*/
                break;
        }

    }
}
