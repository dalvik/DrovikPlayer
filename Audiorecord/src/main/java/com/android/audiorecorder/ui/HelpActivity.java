package com.android.audiorecorder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.utils.StringUtil;

public class HelpActivity extends Activity {
    
    private String TAG = "HelpActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_help);
        TextView aboutTextView = (TextView) findViewById(R.id.setting_help_about_software);
        aboutTextView.setText(StringUtil.loadHtmlText(this, R.string.sms_setting_help_software_content));
        TextView functionContentTextView = (TextView) findViewById(R.id.setting_help_function_content);
        functionContentTextView.setText(StringUtil.loadHtmlText(this, R.string.sms_setting_help_function_content));
        findViewById(R.id.sms_settings_back).setOnClickListener(onClickListener);
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
            int i = v.getId();
            if (i == R.id.sms_settings_back) {
                HelpActivity.this.finish();

            } else {
            }
        }
    };
        
}
