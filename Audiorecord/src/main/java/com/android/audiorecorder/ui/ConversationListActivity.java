package com.android.audiorecorder.ui;

import android.os.Bundle;

import com.android.audiorecorder.R;
import com.android.library.ui.activity.BaseCompatActivity;

public class ConversationListActivity extends BaseCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_conversation_activity);
		setActionBarBackground(R.drawable.lib_drawable_common_actionbar_background);
	}
}
