package com.drovik.player.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.android.audiorecorder.ui.SettingsActivity;
import com.android.library.ui.activity.BaseGuideActivity;
import com.drovik.player.R;

public class DrovikPlayerGuide extends BaseGuideActivity {

    private int[] guidePic = new int[3];
    private String drovikPlayerGuide = "drovik_player_guide";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.class.getName(), Context.MODE_PRIVATE);
        if(preferences.getBoolean(drovikPlayerGuide, false)){
            gotoHome();
        }
    }

    @Override
    public int[] initPictures() {
        guidePic[0] = R.drawable.drovik_guide_a;
        guidePic[1] = R.drawable.drovik_guide_b;
        guidePic[2] = R.drawable.drovik_guide_c;
        return guidePic;
    }

    @Override
    public void gotoNextActivity() {
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.class.getName(), Context.MODE_PRIVATE);
        preferences.edit().putBoolean(drovikPlayerGuide, true).apply();
        gotoHome();
    }

    private void gotoHome() {
        Intent intent = new Intent(this, LaunchActivity.class);
        startActivity(intent);
        DrovikPlayerGuide.this.finish();
    }
}
