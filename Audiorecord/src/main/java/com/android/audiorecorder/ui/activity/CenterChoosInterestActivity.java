package com.android.audiorecorder.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.view.ChoosInterest;

public class CenterChoosInterestActivity extends BaseCompatActivity implements View.OnClickListener {

    public static final String RESULT_INTEREST = "interest";

    private String[] constellationArr = {"水瓶", "双鱼", "牡羊",
            "金牛", "双子", "巨蟹", "狮子", "处女", "天秤",
            "天蝎", "射手", "魔羯"};

    private ChoosInterest choosInterestLifeView;
    private ChoosInterest choosInterestSportView;
    private ChoosInterest choosInterestArtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_choos_interest);
        setTitle(R.string.center_choos_interest);
        initUI();
    }

    private void initUI() {
        //加载自定义流式布局，动态填充数据
        choosInterestLifeView = (ChoosInterest) findViewById(R.id.choosInterestLifeView);
        choosInterestSportView = (ChoosInterest) findViewById(R.id.choosInterestSportView);
        choosInterestArtView = (ChoosInterest) findViewById(R.id.choosInterestArtView);
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        for (int i = 0; i < constellationArr.length; i++) {
            TextView textView = (TextView) layoutInflater.inflate(R.layout.center_choos_item, choosInterestLifeView, false);
            textView.setText(constellationArr[i]);
            choosInterestLifeView.addView(textView);
        }
        for (int i = 0; i < constellationArr.length; i++) {
            TextView textView = (TextView) layoutInflater.inflate(R.layout.center_choos_item, choosInterestSportView, false);
            textView.setText(constellationArr[i]);
            choosInterestSportView.addView(textView);
        }
        for (int i = 0; i < constellationArr.length; i++) {
            TextView textView = (TextView) layoutInflater.inflate(R.layout.center_choos_item, choosInterestArtView, false);
            textView.setText(constellationArr[i]);
            choosInterestArtView.addView(textView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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
    }
}
