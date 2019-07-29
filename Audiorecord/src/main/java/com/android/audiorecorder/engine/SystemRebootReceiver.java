package com.android.audiorecorder.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.android.audiorecorder.provider.FileProviderService;

public class SystemRebootReceiver extends BroadcastReceiver {

	private String TAG = "SystemRebootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.d(TAG, "===> " + intent.getAction());
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
			Intent mutiService = new Intent(context, MultiMediaService.class);
			Intent fileService = new Intent(context, FileProviderService.class);
			/*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
				context.startForegroundService(mutiService);
				context.startForegroundService(fileService);
			} else {
				context.startService(mutiService);
				context.startService(fileService);
			}*/
			context.startService(mutiService);
			context.startService(fileService);
        }
	}

}
