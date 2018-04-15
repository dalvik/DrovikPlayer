package com.demo.pager;

import android.view.View;

import com.android.library.R;
import com.demo.ThirdRegisterActivity;

import java.util.Map;

public class ThirdRegisterModifyPager extends RegisterModifyPager {

    @Override
    protected void initUI(View view) {
        super.initUI(view);
        Map<String, Object> infos = ((ThirdRegisterActivity) activity).getInfos();
        if (infos != null && !infos.isEmpty()) {
            // 性别
            if (infos.get("gender") != null) {
                if ("男".equals(infos.get("gender"))) {
                    sexRg.check(R.id.male);
                    //sex = ReqConstants.Sex.SEX_MALE;
                } else if ("女".equals(infos.get("gender"))) {
                    sexRg.check(R.id.female);
                    //sex = ReqConstants.Sex.SEX_FEMALE;
                }
            }
        }
    }
}
