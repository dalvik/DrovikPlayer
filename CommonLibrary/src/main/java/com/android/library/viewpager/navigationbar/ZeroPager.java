package com.android.library.viewpager.navigationbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.library.R;

public class ZeroPager extends Fragment {

    private TextView mTextView;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("oncreate0");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTextView = (TextView)inflater.from(getActivity()).inflate(R.layout.toast_view, null);
        System.out.println("onCreateView0");
        mTextView.setText("0000000000000");
        //getActivity().getActionBar().hide();
        return mTextView;
    }

    /*@Override
    public void reload() {
    }
    
    @Override
    protected View createView(LayoutInflater inflater, Bundle savedInstanceState) {
        return null;
    }*/

}
