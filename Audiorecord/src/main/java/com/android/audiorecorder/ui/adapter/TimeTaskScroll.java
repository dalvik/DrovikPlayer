package com.android.audiorecorder.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import java.util.List;
import java.util.TimerTask;

public class TimeTaskScroll extends TimerTask {
	
	private ListView listView;
	
	public TimeTaskScroll(Context context, ListView listView, List<String> list){
		this.listView = listView;
		listView.setAdapter(new DonateListAdapter(context, list));
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			listView.smoothScrollBy(1, 0);
		};
	};

	@Override
	public void run() {
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
	}

}
