package com.android.audiorecorder.ui.pager;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FragmentInfo {
    public final Class<?> clazz;
    public final Bundle bundle;
    public Fragment fragment;

    public FragmentInfo(Class<?> clazz, Bundle bundle) {
        this.clazz = clazz;
        this.bundle = bundle;
    }
}
