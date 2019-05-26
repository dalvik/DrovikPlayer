package com.android.audiorecorder.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.audiorecorder.R;

public class SettingsActivity extends Activity {
    
    public static final String KEY_RECORDER_START = "key_recorder_start";
    public static final String KEY_RECORDER_END = "key_recorder_end";
    public static final String KEY_UPLOAD_START = "key_upload_start";
    public static final String KEY_UPLOAD_END = "key_upload_end";
    public static final String KEY_VALID = "key_valid";//1:show 0:hide
    
    public static final String KEY_CUR_VERSION_CODE = "cur_version_code";
    public static final String KEY_CUR_VERSION_NAME = "cur_version_name";
    
    public static final String KEY_NEW_VERSION_CODE = "new_version_code";
    public static final String KEY_NEW_VERSION_NAME = "new_version_name";
    
    public static final String KEY_NEW_VERSION_URL = "new_version_url";
    
    public static final String KEY_MAC_ADDRESS = "key_mac";
    
    public static final String KEY_UPLOAD_URL = "key_upload_url";
    public static final String DEFAULT_UPLOAD_URL = "http://alumb.sinaapp.com/file_recv.php";
    
    public static final String KEY_SUGGESTION_PHONE_NUMBER = "key_phone_number";
    
    
    private String TAG = "SettingsActivity";
    
    private TextView mCurrentVersion;
    private TextView mUpdateVersion;
    
    private SharedPreferences mSettings;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings);
        mSettings = getSharedPreferences(SettingsActivity.class.getName(), MODE_PRIVATE);
        findViewById(R.id.sms_settings_back).setOnClickListener(onClickListener);
        findViewById(R.id.version_layout).setOnClickListener(onClickListener);
        mCurrentVersion = (TextView) findViewById(R.id.now_version_name);
        findViewById(R.id.update_version_name).setOnClickListener(onClickListener);
        mUpdateVersion = (TextView) findViewById(R.id.update_version_name);
        mCurrentVersion.setText(mSettings.getString(KEY_CUR_VERSION_NAME, ""));
        if(mSettings.getInt(KEY_CUR_VERSION_CODE, 0) < mSettings.getInt(KEY_NEW_VERSION_CODE, 0)){
            mUpdateVersion.setText(getText(R.string.sms_setting_update) + getString(R.string.sms_setting_newer, mSettings.getString(KEY_CUR_VERSION_NAME, "")));
        }else{
            mUpdateVersion.setText(getText(R.string.sms_setting_newest));
        }
        findViewById(R.id.suggestion_layout).setOnClickListener(onClickListener);
        findViewById(R.id.help_layout).setOnClickListener(onClickListener);
        findViewById(R.id.about_layout).setOnClickListener(onClickListener);
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
                SettingsActivity.this.finish();

            } else if (i == R.id.version_layout) {
                if (mSettings.getInt(KEY_CUR_VERSION_CODE, 0) < mSettings.getInt(KEY_NEW_VERSION_CODE, 0)) {
                    String url = mSettings.getString(KEY_NEW_VERSION_URL, "");
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

            } else if (i == R.id.suggestion_layout) {
                Intent suggestion = new Intent(SettingsActivity.this, SuggestionActivity.class);
                startActivity(suggestion);

            } else if (i == R.id.help_layout) {
                Intent help = new Intent(SettingsActivity.this, HelpActivity.class);
                startActivity(help);

            } else if (i == R.id.about_layout) {
                Intent about = new Intent(SettingsActivity.this, AboutActivity.class);
                startActivity(about);

            } else {
            }
        }
    };
        
}
