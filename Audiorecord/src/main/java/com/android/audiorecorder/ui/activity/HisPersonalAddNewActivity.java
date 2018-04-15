package com.android.audiorecorder.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.dao.PersonalNewsDao;
import com.android.audiorecorder.ui.data.req.PersonalAddNewsReq;
import com.android.audiorecorder.ui.manager.HisPersonalCenterManager;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.utils.ToastUtils;
import com.android.library.ui.view.RoundImageView;

public class HisPersonalAddNewActivity extends BaseCompatActivity {
    
    private HisPersonalCenterManager mHisPersonalCenterManager;
    private RoundImageView ivHeaderIcon;
    private EditText mMessageEditText;
    private int whatAddNews;
    private int mUserId;
    private TextView mOptionTextView;
    private int mInputLength = 0;
    private String TAG = "HisPersonalCenterActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_his_personal_center_add_new);
        setActionBarBackground(R.drawable.lib_drawable_common_actionbar_background);
        setTitle(R.string.personal_center_add_new_message_title);
        initUI();
        mHisPersonalCenterManager = new HisPersonalCenterManager(this);
    }

    @Override
    protected boolean initIntent() {
    	mUserId = getIntent().getIntExtra(ActivityUtil.USER_ID, 0);
        if (mUserId <=0) {
            return false;
        }
        return true;
    }
    
    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        if (what == whatAddNews) {
            switch (result) {
                case HisPersonalCenterManager.RESULT_SUCCESS:
                	PersonalNewsDao newsDao = new PersonalNewsDao();
                	PersonalAddNewsReq req = new PersonalAddNewsReq();
                	req.userId = mUserId;
                	req.newsType = 0;
                	req.newsContent = mMessageEditText.getText().toString();
                	newsDao.insertNewPersonalNews(activity, req);
                	HisPersonalAddNewActivity.this.finish();
                    break;
                default:
                    return false;
            }
        }
        return true;
    }


    private void initUI() {
    	mMessageEditText = (EditText) findViewById(R.id.his_personal_center_add_new_message);
    	mMessageEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				mInputLength = arg0.length();
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				if(mInputLength <=0) {
					if(mOptionTextView != null){
						mOptionTextView.setEnabled(false);
					}
				} else {
					if(mOptionTextView != null){
						mOptionTextView.setEnabled(true);
					}
				}
			}
		});
    }


    @Override
    protected void setOptionView(TextView option) {
    	option.setText(R.string.personal_center_add_new_message_submit);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	String content = mMessageEditText.getText().toString();
            	if(content.length() ==0){
            		ToastUtils.showToast(R.string.personal_center_add_none_message);
            		return ;
            	}
            	showWaitingDlg();
            	whatAddNews = mHisPersonalCenterManager.addPersonalNews(mUserId, 0, content, "");
            }
        });
        mOptionTextView = option;
        if(mInputLength==0){
        	option.setEnabled(false);
        }
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	hideWaitingDialog();
    }

}
