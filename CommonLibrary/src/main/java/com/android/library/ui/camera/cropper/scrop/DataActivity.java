package com.android.library.ui.camera.cropper.scrop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DataActivity extends Activity {
    public static void passData(Activity activity, Intent intent) {
        Intent dataIntent = new Intent(activity, DataActivity.class);
        dataIntent.putExtras(intent.getExtras());
        activity.startActivityForResult(dataIntent, Crop.REQUEST_CROP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_OK, getIntent());
        finish();
    }
}
