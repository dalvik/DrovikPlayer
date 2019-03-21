package com.drovik.player.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.library.ui.activity.BaseCompatActivity;
import com.drovik.player.R;

public class TestActivity extends BaseCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
    }
}
