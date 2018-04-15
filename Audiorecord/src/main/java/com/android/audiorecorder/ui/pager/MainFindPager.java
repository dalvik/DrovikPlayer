package com.android.audiorecorder.ui.pager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.activity.FriendCircleListActivity;
import com.android.library.ui.pager.BasePager;

public class MainFindPager extends BasePager {

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.lib_layout_ui_find, null);
    	view.findViewById(R.id.friendCircleRL).setOnClickListener(onClickListener);;
        return view;
    }

    @Override
    public void reload() {
    }
    
    
    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.lib_layout_ui_find, null);
        return view;
    }

    private OnClickListener onClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.friendCircleRL) {
                Intent intent = new Intent(getActivity(), FriendCircleListActivity.class);
                startActivity(intent);

            } else {
            }
        }
    };
}
