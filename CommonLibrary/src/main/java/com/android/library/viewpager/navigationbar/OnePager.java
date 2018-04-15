package com.android.library.viewpager.navigationbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.library.R;

public class OnePager extends Fragment {

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("oncreate1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTextView = (TextView)inflater.from(getActivity()).inflate(R.layout.toast_view, null);
        System.out.println("onCreateView1");
        return mTextView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        /*LayoutParams layoutParams = new LayoutParams();
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        mTextView.setLayoutParams(layoutParams);*/
        mTextView.setText("111111111");
        super.onViewCreated(view, savedInstanceState);
    }
}
