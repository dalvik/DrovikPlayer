package com.android.audiorecorder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.utils.StringUtil;

public class AboutActivity extends Activity {
    
    private String TAG = "AboutActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);
        findViewById(R.id.sms_settings_back).setOnClickListener(onClickListener);
        TextView aboutTextView = (TextView) findViewById(R.id.sms_about_contact_content);
        aboutTextView.setText(StringUtil.loadHtmlText(this, R.string.sms_setting_about_contact_content));
        TextView cooperationTextView = (TextView) findViewById(R.id.setting_about_cooperation_id);
        cooperationTextView.setText(StringUtil.loadHtmlText(this, R.string.sms_setting_about_cooperation_content));
    }
   
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    
    
    private OnClickListener onClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
        if(v.getId() == R.id.sms_settings_back) {
            AboutActivity.this.finish();
        }
        }
    };
        
}
