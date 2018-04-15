package com.android.audiorecorder.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.android.library.R;
import com.android.library.ui.activity.BaseCompatActivity;

public class CenterChoosSkillActivity extends BaseCompatActivity implements View.OnClickListener {
    public static final String RESULT_SKILL = "skill";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_choos_skill);
        setTitle(R.string.center_choos_skill);
    }

    @Override
    public void onClick(View v) {

    }
}
