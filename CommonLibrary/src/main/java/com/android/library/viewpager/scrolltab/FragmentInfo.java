package com.android.library.viewpager.scrolltab;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FragmentInfo {
    final Class<?> clazz;
    final Bundle bundle;
    Fragment fragment;

    FragmentInfo(Class<?> clazz, Bundle bundle) {
        this.clazz = clazz;
        this.bundle = bundle;
    }
}
