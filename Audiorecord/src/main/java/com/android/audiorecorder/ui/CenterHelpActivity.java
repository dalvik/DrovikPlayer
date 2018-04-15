package com.android.audiorecorder.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.utils.StringUtil;
import com.android.library.ui.activity.BaseCompatActivity;

public class CenterHelpActivity extends BaseCompatActivity {
    
    private String TAG = "HelpActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_help);
        setTitle(R.string.sms_setting_help);
        setActionBarBackground(R.drawable.lib_drawable_common_actionbar_background);
        TextView aboutTextView = (TextView) findViewById(R.id.setting_help_about_software);
        aboutTextView.setText(StringUtil.loadHtmlText(this, R.string.sms_setting_help_software_content));
        TextView functionContentTextView = (TextView) findViewById(R.id.setting_help_function_content);
        functionContentTextView.setText(StringUtil.loadHtmlText(this, R.string.sms_setting_help_function_content));
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
        
}
