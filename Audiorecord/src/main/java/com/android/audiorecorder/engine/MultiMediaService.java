package com.android.audiorecorder.engine;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.audiorecorder.DebugConfig;
import com.android.audiorecorder.dao.UserDao;
import com.android.audiorecorder.provider.RongMessagProvider;
import com.android.audiorecorder.ui.SettingsActivity;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.StringUtils;

import java.util.Calendar;
import java.util.List;

public class MultiMediaService extends Service {
	
    public final static int STATE_IDLE = 0;
    public final static int STATE_BUSY = 1;
    public final static int STATE_PREPARE = 2;

    public static final String PRE_AUT = "A";
    public static final String PRE_TEL = "T";
    public static final String PRE_TELI = "TI";
    public static final String PRE_TELO = "TO";
    public static final String PRE_MIC = "M";

    public static final int LUNCH_MODE_IDLE = OnRecordListener.LUNCH_MODE_IDLE;
    public static final int LUNCH_MODE_CALL = OnRecordListener.LUNCH_MODE_CALL;
    public static final int LUNCH_MODE_MANLY = OnRecordListener.LUNCH_MODE_MANLY;//no allowed time and tel to recorder

    public static final int LUNCH_MODE_AUTO = OnRecordListener.LUNCH_MODE_AUTO;
    public int mAudioRecordState = STATE_IDLE;

    public int mVideoRecordState = STATE_IDLE;

	public final static int PAGE_NUMBER = 1;
    private static final int PMS = 22;
    private static final int PME = 23;
    
    private static final int UPLOAD_START = 2;
    private static final int UPLOAD_END = 2;
    
    private static final int DELETE_START = 13;
    private static final int DELETE_END = 13;
    
    private int mCurMode;
    private boolean isScreenOn;
    
    public static final String Action_Audio_Record = "com.audio.Action_AudioRecord";
    public static final String Action_Video_Record = "com.audio.Action_VideoRecord";
    public static final String Action_Rong_Message = "com.audio.Action_RongMessage";
    
    
    private int mBatteryLevel;
    private final static int MIN_BATTERY_LEVEL = 25;//%

    
    public final static int MSG_START_UPLOAD = 2000;
    
    public final static int MSG_START_DELETE = 3000;
    
    private static final int MSG_BLUETOOTH_START_SCO = 4000;
    private static final int MSG_BLUETOOTH_STOP_SCO = 4001;
    private static final int MSG_BLUETOOTH_PROFILE_MATCH = 4002;
    private static final int MSG_START_TIMER = 4010;

    private boolean mIsBluetoothConnected;
    private boolean mAtdpEnable;

    private String TAG = "MultiMediaService";

    private AudioManager mAudioManager;
    private PowerManager mPowerManager;
    private int mRingerMode;

    private boolean mRecorderStart;
    private boolean mTalkStart;
    private long mAudioRecorderDuration;
    private long mTalkTime;

    private BroadcastReceiver mStateChnageReceiver = null;
    
    private SharedPreferences mPreferences;
    
    private TimeSchedule mTimeSchedule;
    
    private long mTransferedBytes;
    
    private BluetoothA2dp mService;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothHeadsetListener mBluetoothHeadsetListener = new BluetoothHeadsetListener();
    
    
    private IAudioService.Stub mAudioService;
    private IVideoService.Stub mVideoService;
    
    private RongMessagProvider mRongMessagProvider;
    
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                /*case MSG_UPDATE_TIMER:
                    notifyRecordState(MSG_UPDATE_TIMER);//only update recorder time on UI
                    updateNotifiaction();
                    break;*/
                case MSG_BLUETOOTH_PROFILE_MATCH:
                    if (mService != null) {
                        List<BluetoothDevice> devs = getConnectedDevices();
                        Log.d(TAG, "---> buletooth connected number = " + devs.size());
                    }
                    break;
                case MSG_BLUETOOTH_START_SCO:
                    if (mIsBluetoothConnected) {
                       mAudioManager.startBluetoothSco();
                       mAudioManager.setMode(AudioManager.MODE_IN_CALL);
                       Log.d(TAG, "---> start bluetooth sco : " + mAudioManager.isBluetoothScoOn());
                    }
                  break;

              case MSG_BLUETOOTH_STOP_SCO:
                  if (mIsBluetoothConnected) {
                      mAudioManager.stopBluetoothSco();
                      mAudioManager.setBluetoothScoOn(false);
                      mAudioManager.setMode(AudioManager.MODE_NORMAL);
                  }
                  break;
              case MSG_START_TIMER:
            	  processAutoTimerAlarm();
            	  break;
                default:
                    break;
            }
        };
    };

    @Override
    public IBinder onBind(Intent intent) {
        if (Action_Audio_Record.equalsIgnoreCase(intent.getAction())) {
            return getAudioService();
        } else if (Action_Video_Record.equalsIgnoreCase(intent.getAction())) {
            return getVideoService();
        } else if(Action_Rong_Message.equalsIgnoreCase(intent.getAction())){
        	return getRongMessageProvider();
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        isScreenOn = mPowerManager.isScreenOn();
        this.mPreferences = getSharedPreferences(SettingsActivity.class.getName(), Context.MODE_PRIVATE);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mCurMode = LUNCH_MODE_MANLY;
        getAudioService();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimeSchedule.ACTION_TIMER_ALARM);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(StringUtils.ACTION_USER_LOGIN);
        if (mStateChnageReceiver == null) {
            mStateChnageReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if(DebugConfig.DEBUG){
                        Log.i(TAG, "---> action = " + action);
                    }
                    if(TimeSchedule.ACTION_TIMER_ALARM.equalsIgnoreCase(action)){
                    	 Log.i(TAG, "---> alarm.");
                    } else if(Intent.ACTION_USER_PRESENT.equalsIgnoreCase(action)){//user login, screen on
                        isScreenOn = true;
                        try {
                            if(getAudioService().getMode() == LUNCH_MODE_AUTO){//stop record
                                getAudioService().stopAudioMode();
                                Log.i(TAG, "---> user present.");
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
                        if(state == BluetoothAdapter.STATE_CONNECTED){
                             mIsBluetoothConnected = true;
                             BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                             if(bluetoothAdapter != null){
                                  bluetoothAdapter.getProfileProxy(MultiMediaService.this, mBluetoothHeadsetListener, BluetoothProfile.A2DP);
                                  bluetoothAdapter.getProfileProxy(MultiMediaService.this, mBluetoothHeadsetListener, BluetoothProfile.HEADSET);
                             }
                        } else if(state == BluetoothAdapter.STATE_DISCONNECTED || state == BluetoothAdapter.STATE_DISCONNECTING){
                             mAtdpEnable =  false;
                             mIsBluetoothConnected = false;
                             mAudioManager.stopBluetoothSco();
                             mAudioManager.setBluetoothScoOn(false);
                             mAudioManager.setMode(AudioManager.MODE_NORMAL);
                             Log.d(TAG,"==> recv bluetooth connected  state = " + state + " atdp enable = " + mAtdpEnable);
                        }
                    } else if(action.equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)) {
                        int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, AudioManager.SCO_AUDIO_STATE_ERROR);
                        Log.d(TAG,"===> bluetooth sco state = " + state + " mIsBluetoothConnected = " + mIsBluetoothConnected + " mAtdpEnable = " + mAtdpEnable);
                        if(state == AudioManager.SCO_AUDIO_STATE_CONNECTED){
                             mAudioManager.setBluetoothScoOn(true);
                        } else if(state == AudioManager.SCO_AUDIO_STATE_DISCONNECTED){
                             if(mAudioManager.isBluetoothScoOn()) {
                                 mAudioManager.stopBluetoothSco();
                                   mAudioManager.setBluetoothScoOn(false);
                                   mAudioManager.setMode(AudioManager.MODE_NORMAL);
                             }
                             if(mIsBluetoothConnected && !mAtdpEnable){
                                   Log.d(TAG, "===> start reconnected bluetooth sco.");
                                   mHandler.removeMessages(MSG_BLUETOOTH_START_SCO);
                                   mHandler.sendEmptyMessageDelayed(MSG_BLUETOOTH_START_SCO, 1500);
                             }
                        }
                    } else if(action.equals(Intent.ACTION_BATTERY_CHANGED)){
                        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        if(level != mBatteryLevel){
                            mBatteryLevel = level;
                            Log.i(TAG, "--> mBatteryLevel = " + mBatteryLevel);
                        }
                    } else if(action.equals(Intent.ACTION_SCREEN_OFF)){
                        isScreenOn = false;
                        mHandler.removeMessages(MSG_START_TIMER);
                        mHandler.sendEmptyMessageDelayed(MSG_START_TIMER, 3000);
                    } else if(action.equals(StringUtils.ACTION_USER_LOGIN)){
                    	UserDao mDao = new UserDao();
                    	UserResp userResp = mDao.getUser(MultiMediaService.this);
                    	LogUtil.d(TAG, "==> user login success.");
                    	if(userResp != null) {
                    		try {
    							getRongMessageProvider().refreshToken(userResp.userCode, userResp.nickName, userResp.headIcon);
    						} catch (RemoteException e) {
    							e.printStackTrace();
    						}
                    	}
                    	//String userId = StringUtils.getString(context, StringUtils.KEY_USER_ID, "");
                    	//String name = StringUtils.getString(context, StringUtils.KEY_USER_NAME, "");
                    	//String headerIcon = StringUtils.getString(context, StringUtils.KEY_USER_HEADER_ICON, "");
                    	
                    	//TODO
						
                    }
                }
            };
            registerReceiver(mStateChnageReceiver, filter);
        }
        mCurMode = LUNCH_MODE_IDLE;
        Log.d(TAG, "===> onCreate. screen state : " + isScreenOn);
    }

    private void processAutoTimerAlarm(){
        if(!isScreenOn){//srceen off
            if(DebugConfig.DEBUG){
                Log.i(TAG, "--> isValidRecorderTime : " + isValidAudoRecorderTime() + " BatteryLevel : " + mBatteryLevel + " isValidDeleteTime : " + isValidDeleteTime());
            }
            /**
             * check is valid recorder time and batter level is enough
             */
            if(isValidAudoRecorderTime() && mBatteryLevel >= MIN_BATTERY_LEVEL){//start
                if(DebugConfig.DEBUG){
                    Log.i(TAG, "processTimerAlarm mCurMode = " + mCurMode + " mRecorderStart = " + mRecorderStart);
                }
                /**
                 * if curmode is idle, start record with auto mode
                 */
                try {
					if(getAudioService().getMode() == LUNCH_MODE_IDLE){
						getAudioService().startAutoMode();
					} else {
						/**
		                 * if curmode is auto, stop record and restart with auto mode
		                 */
						if(mCurMode == LUNCH_MODE_AUTO){
                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                getAudioService().stopRecord();
                                getAudioService().startAutoMode();
                            }
		                }
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            } else {// stop auto recorder
                try {
					if(getAudioService().getMode() == LUNCH_MODE_AUTO){
					    Log.i(TAG, "---> auto stop mode.");
					    getAudioService().stopRecord();
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
            }
        }
    }
    
    private boolean isValidAudoRecorderTime(){
        Calendar rightNow = Calendar.getInstance();
        int minute = rightNow.get(Calendar.MINUTE);
        int dayOfHour = rightNow.get(Calendar.HOUR_OF_DAY);
        Log.d(TAG, "dayOfHour: " + dayOfHour + " minute: " + minute);
        //dayOfHour = dayOfHour * 100 + minute;
        return dayOfHour>=mPreferences.getInt(SettingsActivity.KEY_RECORDER_START, PMS) && dayOfHour<=mPreferences.getInt(SettingsActivity.KEY_RECORDER_END, PME);
    }
    
    private boolean isValidDeleteTime(){
        Calendar rightNow = Calendar.getInstance();
        int dayOfHour = rightNow.get(Calendar.HOUR_OF_DAY);
        return dayOfHour>=DELETE_START && dayOfHour<=DELETE_END;
    }
   
    private List<BluetoothDevice> getConnectedDevices() {
        return mService.getDevicesMatchingConnectionStates(new int[] {BluetoothProfile.STATE_CONNECTED});
    }
    
    private class BluetoothHeadsetListener implements BluetoothProfile.ServiceListener {

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if(profile == BluetoothProfile.A2DP){
                mAtdpEnable = true;
                mService = (BluetoothA2dp) proxy;
                List<BluetoothDevice> list = mService.getConnectedDevices();
                Log.d(TAG, "a2dp list size =  " + list);
                /*for(BluetoothDevice device:list){
                    boolean state = mService.isA2dpPlaying(device);
                    Log.d(TAG, "a2dp state = " + state);
                }*/
            } else if(profile == BluetoothProfile.HEADSET){
                mAtdpEnable = false;
                mBluetoothHeadset =  (BluetoothHeadset) proxy;
                List<BluetoothDevice> list = mBluetoothHeadset.getConnectedDevices();
                Log.d(TAG, "no a2dp list size =  " + list);
                for(BluetoothDevice device:list){
                    BluetoothClass bluetoothClass = device.getBluetoothClass();
                    String bluetoothDeviceClass = bluetoothClass.toString();
                    boolean isAudioConnected = mBluetoothHeadset.isAudioConnected(device);
                    Log.d(TAG, "isAudioConnected = " + isAudioConnected + " bluetoothDeviceClass = " + bluetoothDeviceClass);
                }
            }
            mHandler.removeMessages(MSG_BLUETOOTH_PROFILE_MATCH);
            mHandler.sendEmptyMessageDelayed(MSG_BLUETOOTH_PROFILE_MATCH, 500);
        }

        @Override
        public void onServiceDisconnected(int profile) {
            
        }
        
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startForeground(CUSTOM_VIEW_ID, new Notification());
        //Notification note = new Notification(0, null, System.currentTimeMillis() );
        //note.flags |= Notification.FLAG_NO_CLEAR;
        //startForeground(42, note);
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (mStateChnageReceiver != null) {
            unregisterReceiver(mStateChnageReceiver);
            mStateChnageReceiver = null;
        }
        if(mAudioService != null) {
            try {
                mAudioService.release();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "===> onDestroy.");
    }
    
    private IAudioService.Stub getAudioService(){
        if(mAudioService == null){
            mAudioService = new AudioRecordSystem(MultiMediaService.this);
        }
        Log.d(TAG, "===> getAudioService init..");
        return mAudioService;
    }
    
    private IVideoService.Stub getVideoService(){
        if(mVideoService == null){
            mVideoService = new VideoRecordSystem(MultiMediaService.this);
        }
        return mVideoService;
    }
    
    private IRongMessage.Stub getRongMessageProvider(){
    	if(mRongMessagProvider == null){
    		mRongMessagProvider = new RongMessagProvider(MultiMediaService.this);
    	}
    	return mRongMessagProvider;
    }
    public interface OnRecordListener{
        
        public final static int STATE_IDLE = 0;
        public final static int STATE_BUSY = 1;
        public final static int STATE_PREPARE = 2;
        
        public static final int MSG_START_RECORD = 0xE1;
        public static final int MSG_STOP_RECORD = 0xE2;
        
        public static final int LUNCH_MODE_IDLE = 0;
        public static final int LUNCH_MODE_CALL = 1;
        public static final int LUNCH_MODE_MANLY = 2;//no allowed time and tel to recorder
        public static final int LUNCH_MODE_AUTO = 3;
        
        public void setMode(int mode);
        
    }
	
}
