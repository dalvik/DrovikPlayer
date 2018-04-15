package com.android.audiorecorder.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.UserDao;
import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;

/**
 * Created by Pengbin on 2015/11/19.
 */
public class CenterChoosCompanyActivity extends BaseCompatActivity implements View.OnClickListener {
    public static final String RESULT_COMPANY = "company";
    private ImageView delBtn;
    private AutoCompleteTextView companyAcTv;
    private ArrayAdapter<String> arrayAdapter;

    private UserResp userResp;
    private UserDao mUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_choos_company);
        setTitle(R.string.center_choos_company);
        initUI();
        initView();
    }

    private void initUI() {
        mUserDao = new UserDao();
        userResp = mUserDao.getUser(this);
        delBtn = (ImageView) findViewById(R.id.delBtn);
        delBtn.setOnClickListener(this);

        companyAcTv = (AutoCompleteTextView) findViewById(R.id.companyAcTv);
        companyAcTv.setText(userResp.company);
        companyAcTv.setSelection(companyAcTv.length());
    }

    private void initView() {
        String[] company = {"杭州", "杭州A", "杭州B", "杭州C", "杭州D", "杭州E", "杭州F"};
        arrayAdapter = new ArrayAdapter<String>(activity, R.layout.center_autocomplete_item, company);
        companyAcTv.setAdapter(arrayAdapter);
        //设置输入多少字符后提示，默认值为2，在此设为１
        companyAcTv.setThreshold(1);
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.delBtn:
                companyAcTv.setText("");
                break;
        }*/
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
        option.setVisibility(View.VISIBLE);
        option.setText(R.string.save);

        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String company = companyAcTv.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(RESULT_COMPANY, company);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
