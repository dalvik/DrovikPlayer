package com.android.library.ui.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseInnerLoadListPager extends BaseListPager {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isNeedAddWaitingView = false;
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
