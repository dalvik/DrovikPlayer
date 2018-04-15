package com.android.audiorecorder.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.audiorecorder.R;

public class SuggestionActivity extends Activity {

    private String TAG = "SuggestionActivity";

    private EditText mSuggestionContent;
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suggestion);
        mSettings = getSharedPreferences(SettingsActivity.class.getName(), MODE_PRIVATE);
        mSuggestionContent = (EditText) findViewById(R.id.suggestion_content);
        findViewById(R.id.sms_settings_back).setOnClickListener(onClickListener);
        findViewById(R.id.suggestion_commit).setOnClickListener(onClickListener);
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
                SuggestionActivity.this.finish();

            } else if (i == R.id.suggestion_commit) {
                if (mSuggestionContent.getText().toString().trim().length() <= 0) {
                    Toast.makeText(SuggestionActivity.this, R.string.sms_setting_suggestion_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSuggestionContent.getText().toString().trim().length() > 160) {
                    Toast.makeText(SuggestionActivity.this, R.string.sms_setting_suggestion_long, Toast.LENGTH_SHORT).show();
                    return;
                }
                String phoneNumber = mSettings.getString(SettingsActivity.KEY_SUGGESTION_PHONE_NUMBER, "");
                if (phoneNumber != null && phoneNumber.length() > 0) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, mSuggestionContent.getText().toString().trim(), null, null);
                    Toast.makeText(SuggestionActivity.this, R.string.sms_setting_suggestion_success, Toast.LENGTH_SHORT).show();
                    SuggestionActivity.this.finish();
                }

            } else {
            }
        }
    };

}
