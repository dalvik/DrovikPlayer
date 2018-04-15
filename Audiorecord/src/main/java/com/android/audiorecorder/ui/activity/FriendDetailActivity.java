package com.android.audiorecorder.ui.activity;

import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.adapter.PersonalDetailMessageAdapter;
import com.android.audiorecorder.ui.data.BaseData;
import com.android.audiorecorder.ui.data.PersonalDetailBaseData;
import com.android.audiorecorder.ui.data.resp.PersonalNewsResp;
import com.android.audiorecorder.ui.manager.FriendManager;
import com.android.audiorecorder.ui.manager.HisPersonalCenterManager;
import com.android.audiorecorder.ui.manager.UserManager;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.URLS;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.view.HorizontalListView;
import com.android.library.ui.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import io.rong.imkit.RongIM;

public class FriendDetailActivity extends BaseCompatActivity implements View.OnClickListener {
    private PersonalDetailMessageAdapter mMessageAdapter;

    private HisPersonalCenterManager mPersonalNewsManager;
    private RoundImageView mHeader;
    private ImageView mSex;
    private TextView mNameMemo;
    private TextView mNickName;
    private TextView mUserCode;
    private TextView mAddress;
    private TextView mSingature;
    private HorizontalListView mHorizontalListView;
    private Button mSendMessage;
    private int whatPersionDetail;

    private int mUserId;

    private String mUrl;
    private String mUserName;
    private PersonalDetailBaseData mDetailData =  null;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    private boolean mLaunched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_friend_detail);
        setTitle(R.string.firend_detail_title);
        //setActionBarBackground(R.drawable.lib_drawable_common_actionbar_background);
        mPersonalNewsManager = new HisPersonalCenterManager(this);
        mUserName = "";
        initUI();
        initView();
    }

    @Override
    protected boolean initIntent() {
    	mUserId = getIntent().getIntExtra(ActivityUtil.USER_ID, 0);
        if (mUserId <= 0) {
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	mLaunched = false;
    }

    private void initUI() {
        mHeader = (RoundImageView) findViewById(R.id.personal_detail_header_iv_headerIcon);
        mHeader.setOnClickListener(this);
        mNameMemo = (TextView) findViewById(R.id.his_personal_center_user_name_memo_tv);
        mNickName = (TextView) findViewById(R.id.personal_detail_user_nickname_tv);
        mSex = (ImageView) findViewById(R.id.personal_detail_header_iv_sex);
        mUserCode = (TextView) findViewById(R.id.his_personal_center_user_count_tv);
        mAddress = (TextView) findViewById(R.id.personal_detail_address);
        findViewById(R.id.persoinal_news_thunb_listview_id).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ActivityUtil.gotoFriendCircleListActivity(activity, mDetailData.userCode);
			}
		});
        mHorizontalListView = (HorizontalListView) findViewById(R.id.personalDetailMessageListView);
        mHorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ActivityUtil.gotoFriendCircleListActivity(activity, mDetailData.userCode);
			}

        });
        /*mHorizontalListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(!mLaunched){
					mLaunched = true;
					if(mDetailData != null){
						Intent intent = new Intent(activity, HisPersonalCenterActivity.class);
						ContactResp resp = new ContactResp();
						resp.userCode = mDetailData.userCode;;
						resp.nickName = mDetailData.nickName;
						resp.headIcon = mDetailData.headIcon;
						Bundle bundle = new Bundle();
						bundle.putSerializable(ActivityUtils.INTENT_BEAN, resp);
						intent.putExtras(bundle);
						ActivityUtils.startActivity(activity, intent);
						ActivityUtil.gotoFriendCircleListActivity(activity, mDetailData.userCode);
					}
				}
				return false;
			}
		});*/
        mSingature = (TextView) findViewById(R.id.personal_detail_signature);

        mSendMessage = (Button) findViewById(R.id.personal_detail_send_message_id);
        mSendMessage.setOnClickListener(this);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
        //.showImageOnLoading(defaultHeaderRes)
        //.showImageForEmptyUri(defaultHeaderRes)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .bitmapConfig(Config.RGB_565)
        .build();
    	mNameMemo.setText(getString(R.string.personal_detail_nickname, ""));
    	mUserCode.setText(getString(R.string.personal_detail_user_code, mUserId));
    	mAddress.setText("");
    	mSingature.setText("");
    }

    private void initView() {
    	whatPersionDetail = mPersonalNewsManager.getPersionalThumbnailList(mUserId);
    	mMessageAdapter = new PersonalDetailMessageAdapter(activity, false);
        mHorizontalListView.setAdapter(mMessageAdapter);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.personal_detail_send_message_id) {
            if (mUserId > 0) {
                RongIM.getInstance().startPrivateChat(this, String.valueOf(mUserId), mUserName);
            }
            finish();

        } else if (i == R.id.personal_detail_header_iv_headerIcon) {
            if (!TextUtils.isEmpty(mUrl)) {
                ActivityUtil.gotoBitImageHeaderActicity(activity, mUrl);
            }

        } else {
        }
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
    	if (what == whatPersionDetail) {
            switch (result) {
                case FriendManager.RESULT_SUCCESS:
                	BaseData baseData = (BaseData) obj;
                	if(baseData.data != null){
                		mDetailData = (PersonalDetailBaseData) baseData.data;
                		updateUi(mDetailData);
                		if(mDetailData.data != null){
                			ArrayList<PersonalNewsResp> list = (ArrayList<PersonalNewsResp>)mDetailData.data;
                			mMessageAdapter.addItems(list);
                		}
                		LogUtil.i(TAG, "add = " + mDetailData.address + " code =  "+ mDetailData.cityCode);
                	}
                    return true;
                default:
                    return false;
            }
    	}
    	return true;
    }

    @Override
    protected void setBackView(TextView back) {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void setOptionView(TextView option) {
        option.setVisibility(View.INVISIBLE);
    }
    
    private void updateUi(PersonalDetailBaseData detailData){
    	mUrl = detailData.headIcon;
    	ImageLoader.getInstance().displayImage(URLS.DOMAIN + detailData.headIcon, mHeader, options);
    	if(detailData.sex == UserManager.SEX_FEMALE) {
    		mSex.setImageResource(R.mipmap.ic_friend_female);
    	} else {
    		mSex.setImageResource(R.mipmap.ic_friend_male);
    	}
    	if(TextUtils.isEmpty(detailData.nameMemo)){
    		if(TextUtils.isEmpty(detailData.nickName)){
    			mNameMemo.setText("");
    			mUserName = getString(R.string.personal_detail_nickname_default);
    		} else {
    			mUserName = detailData.nickName;
    			mNameMemo.setText(detailData.nickName);
    		}
    	} else {
    		mUserName = detailData.nameMemo;
    		mNameMemo.setText(detailData.nameMemo);
    		if(!TextUtils.isEmpty(detailData.nickName)){
    			mNickName.setText(detailData.nickName);
    			mNickName.setVisibility(View.VISIBLE);
    		}
    	}
    	mUserCode.setText(getString(R.string.personal_detail_user_code, mUserId));
    	if(!TextUtils.isEmpty(detailData.cityCode)){
    		mAddress.setText(detailData.cityCode);
    	} else {
    		mAddress.setText("");
    	}
    	if(!TextUtils.isEmpty(detailData.signature)){
    		mSingature.setText(detailData.signature);
    	} else {
    		mSingature.setText("");
    	}
    	
    }
}
