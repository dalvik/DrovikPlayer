package com.android.library.ui.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sean.xie on 2015/11/28.
 */
public abstract class BaseInnerLoadPager extends BasePager {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isNeedAddWaitingView = true;
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
