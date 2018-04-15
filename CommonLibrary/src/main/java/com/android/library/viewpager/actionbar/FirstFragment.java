package com.android.library.viewpager.actionbar;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.library.R;

public class FirstFragment extends Fragment {

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view =  inflater.from(getActivity()).inflate(R.layout.loading, null);
        System.out.println("FirstFragment onCreateView");
        /*mTextView.setTextColor(Color.RED);
        System.out.println("onCreateView0");
        mTextView.setText("0000000000000");
        //getActivity().getActionBar().hide();
        mTextView.setText(R.string.lib_string_code_scan_title);*/
        return view;
    }

}
