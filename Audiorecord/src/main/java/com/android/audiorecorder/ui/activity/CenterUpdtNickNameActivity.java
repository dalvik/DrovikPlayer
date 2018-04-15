package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.UserDao;
import com.android.library.ui.activity.BaseCompatActivity;

/**
 * 
 * @description: 修改用户昵称界面
 * @author: 23536
 * @date: 2016年5月26日 上午10:55:48
 */
public class CenterUpdtNickNameActivity extends BaseCompatActivity implements View.OnClickListener {

    private ImageView delBtn;
    private EditText nicknameEt;

    public final static String RESULT_NICKNAME = "nickname";
    private UserDao mUserDao;
    private UserResp userResp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_updata_nickname);
        setTitle(R.string.center_updata_nickname);
        mUserDao = new UserDao();
        initUI();
    }

    private void initUI() {
        userResp = mUserDao.getUser(this);
        delBtn = (ImageView) findViewById(R.id.delBtn);
        delBtn.setOnClickListener(this);

        //昵称
        nicknameEt = (EditText) findViewById(R.id.nicknameTv);
        nicknameEt.setText(userResp.nickName);
        nicknameEt.setSelection(nicknameEt.length());
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.delBtn) {
            nicknameEt.setText("");
        }
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
        super.setOptionView(option);
        option.setVisibility(View.VISIBLE);
        option.setText(R.string.save);

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(RESULT_NICKNAME, nicknameEt.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}
