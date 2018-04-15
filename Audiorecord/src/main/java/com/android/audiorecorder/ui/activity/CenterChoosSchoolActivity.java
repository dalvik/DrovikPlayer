package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.UserDao;
import com.android.library.ui.activity.BaseCompatActivity;

public class CenterChoosSchoolActivity extends BaseCompatActivity implements View.OnClickListener {
    public static final String RESULT_SCHOOL = "school";
    private ImageView delBtn;
    private AutoCompleteTextView schoolACTV;
    private ArrayAdapter<String> arrayAdapter;

    private UserResp userResp;
    private UserDao mUserDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_choos_school);
        setTitle(R.string.center_choos_school);
        mUserDao = new UserDao();
        initUI();
        initView();
    }

    private void initUI() {
        userResp = mUserDao.getUser(this);
        delBtn = (ImageView) findViewById(R.id.delBtn);
        delBtn.setOnClickListener(this);

        schoolACTV = (AutoCompleteTextView) findViewById(R.id.schoolACTV);
        schoolACTV.setText(userResp.school);
        schoolACTV.setSelection(schoolACTV.length());
    }

    private void initView() {
        String[] school = {"浙江大学", "浙江大学A", "浙江大学B", "浙江大学C", "浙江大学D", "浙江大学E", "浙江大学F"};
        arrayAdapter = new ArrayAdapter<String>(activity, R.layout.center_autocomplete_item, school);
        schoolACTV.setAdapter(arrayAdapter);
        //设置输入多少字符后提示，默认值为2，在此设为１
        schoolACTV.setThreshold(1);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.delBtn) {
            schoolACTV.setText("");
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
                String school = schoolACTV.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(RESULT_SCHOOL, school);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
