package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.adapter.PersonalDetailMessageAdapter;
import com.android.audiorecorder.ui.data.resp.ContactResp;
import com.android.audiorecorder.ui.manager.HisPersonalCenterManager;
import com.android.audiorecorder.ui.manager.UserManager;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ActivityUtils;
import com.android.library.ui.view.HorizontalListView;
import com.android.library.ui.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class StrangerPersonalDetailActivity extends BaseCompatActivity implements View.OnClickListener {
    private PersonalDetailMessageAdapter mMessageAdapter;

    private HisPersonalCenterManager mPersonalNewsManager;
    private RoundImageView mHeader;
    private RoundImageView mSex;
    private TextView mNickName;
    private TextView mUserCode;
    private TextView mAddress;
    private TextView mSingature;
    private HorizontalListView mHorizontalListView;
    private Button mSendMessage;
    private ContactResp mContactResp;

    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_stranger_personal_detail);
        setTitle(R.string.firend_detail_title);
        setActionBarBackground(R.drawable.lib_drawable_common_actionbar_background);
        mPersonalNewsManager = new HisPersonalCenterManager(this);
        initUI();
        initView();
    }

    @Override
    protected boolean initIntent() {
    	mContactResp = (ContactResp) getIntent().getSerializableExtra(ActivityUtils.INTENT_BEAN);
        if (mContactResp == null) {
            return false;
        }
        return true;
    }

    private void initUI() {
        mHeader = (RoundImageView) findViewById(R.id.personal_detail_header_iv_headerIcon);
        mNickName = (TextView) findViewById(R.id.his_personal_center_header_tv_act_count);
        mNickName.setText(getString(R.string.personal_detail_nickname, ""));
        mSex = (RoundImageView) findViewById(R.id.personal_detail_header_iv_sex);
        mUserCode = (TextView) findViewById(R.id.personal_detail_user_code_tv);
        mUserCode.setText(getString(R.string.personal_detail_user_code, ""));
        mAddress = (TextView) findViewById(R.id.personal_detail_address);
        mAddress.setText(getString(R.string.personal_detail_address));
        mHorizontalListView = (HorizontalListView) findViewById(R.id.personalDetailMessageListView);
        findViewById(R.id.persoinal_news_thunb_listview_id).setOnClickListener(new OnClickListener(	) {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(activity, HisPersonalCenterActivity.class);
	        	Bundle bundle = new Bundle();
	        	bundle.putSerializable(ActivityUtils.INTENT_BEAN, mContactResp);
	        	intent.putExtras(bundle);
	        	ActivityUtils.startActivity(activity, intent);
			}
		});
        mSingature = (TextView) findViewById(R.id.personal_detail_signature);
        mSingature.setText(getString(R.string.personal_detail_signature));
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
        System.out.println("=====" + mHeader);
        ImageLoader.getInstance().displayImage("", mHeader, options);
    }

    private void initView() {
    	mMessageAdapter = new PersonalDetailMessageAdapter(activity, false);
        mHorizontalListView.setAdapter(mMessageAdapter);
        updateUi(mContactResp);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.personal_detail_send_message_id) {
            finish();

        }
    }

    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
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
    
    private void updateUi(ContactResp contactResp){
    	ImageLoader.getInstance().displayImage(contactResp.headIcon, mHeader, options);
    	if(contactResp.sex == UserManager.SEX_FEMALE) {
    		mSex.setImageResource(R.drawable.treat_list_item_sex_female);
    	} else {
    		mSex.setImageResource(R.drawable.treat_list_item_sex_male);
    	}
    	if(contactResp.nickName != null){
    		mNickName.setText(getString(R.string.personal_detail_nickname, contactResp.nickName));
    	} else {
    		mNickName.setText(getString(R.string.personal_detail_nickname, ""));
    	}
    	mUserCode.setText(getString(R.string.personal_detail_user_code));
    	if(contactResp.cityCode != null){
    		mAddress.setText(getString(R.string.personal_detail_address));
    	} else {
    		mAddress.setText(getString(R.string.personal_detail_address));
    	}
    	if(contactResp.signature != null){
    		mSingature.setText(getString(R.string.personal_detail_signature));
    	} else {
    		mSingature.setText(getString(R.string.personal_detail_signature));
    	}
    	
    }
}
