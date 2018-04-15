package com.demo.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.android.library.R;
import com.android.library.ui.pager.BasePager;


public class FindPager extends BasePager implements View.OnClickListener {
    @Override
    public void reload() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lib_layout_ui_find, null);
        view.findViewById(R.id.dateRL).setOnClickListener(this);
        view.findViewById(R.id.senseRl).setOnClickListener(this);
        view.findViewById(R.id.nearbyUserRl).setOnClickListener(this);
        view.findViewById(R.id.showsRl).setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.dateRL:
                ActivityUtil.gotoFindDateActivity(activity);
                break;
            case R.id.senseRl:
                // ActivityUtil.gotoSenseActivity(activity);
                break;
            case R.id.nearbyUserRl:
                ActivityUtil.gotoNearbyUserActivity(activity);
                break;
            case R.id.showsRl:
                ActivityUtil.gotoShowsActivity(activity);
                break;
        }*/
    }
}
