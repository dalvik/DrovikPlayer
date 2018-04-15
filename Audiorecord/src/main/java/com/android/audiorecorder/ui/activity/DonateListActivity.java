package com.android.audiorecorder.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.adapter.TimeTaskScroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * 实现对listView的循环滚动 
 * @author gerry
 *
 */
public class DonateListActivity extends Activity {

	private ListView listView;
	private List<String> list;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_donate_list);
        listView = (ListView) findViewById(R.id.donate_user_list);
        list = getList();
        new Timer().schedule(new TimeTaskScroll(this, listView,list), 20, 20);
    }

    /**
     * 获取数据
     * @return
     */
    public List<String> getList(){
    	List<String> list =  new ArrayList<String>();
    	for (int i = 0; i < 1; i++) {
    	    String temp = "ac****@com" +";刘*才" + ";50.00";
			list.add(temp);
		}
    	return list;
    }

}
