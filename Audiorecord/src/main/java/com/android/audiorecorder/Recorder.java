package com.android.audiorecorder;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.text.SimpleDateFormat;

public class Recorder implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {
    private static boolean DEBUG = false;
    public static final int IDLE_STATE = 0;
    public static final int INTERNAL_ERROR = 2;
    public static final int IN_CALL_RECORD_ERROR = 3;
    public static final int MODE_IN_CALL_GSM = 4;
    public static final int NO_ERROR = 0;
    public static final int PAUSE_STATE = 3;
    public static final int PLAYING_STATE = 2;
    public static final int RECORDING_STATE = 1;
    static final String SAMPLE_LENGTH_KEY = "sample_length";
    static final String SAMPLE_PATH_KEY = "sample_path";
    static final String SAMPLE_PREFIX = "Recording_";
    public static final int SDCARD_ACCESS_ERROR = 1;
    private static int Storage_setting = 1;
    private String TAG = "Recorder";
    OnStateChangedListener mOnStateChangedListener = null;
    int mPausedLenth = 0;
    MediaPlayer mPlayer = null;
    MediaRecorder mRecorder = null;
    File mSampleFile = null;
    int mSampleLength = 0;
    long mSampleStart = 0L;
    int mState = 0;
    private SimpleDateFormat sdFormatter;

    static {
        boolean bool = DEBUG;
    }

    public Recorder(SimpleDateFormat paramSimpleDateFormat) {
        this.sdFormatter = paramSimpleDateFormat;
    }

    private void setError(int paramInt) {
        if (this.mOnStateChangedListener != null)
            this.mOnStateChangedListener.onError(paramInt);
    }

    private void setState(int paramInt) {
        int i = this.mState;
        if (paramInt == i)
            ;
        while (true) {
            return;
            //this.mState = paramInt;
           // int j = this.mState;
            //signalStateChanged(j);
        }
    }

    private void signalStateChanged(int paramInt) {
        if (this.mOnStateChangedListener != null)
            this.mOnStateChangedListener.onStateChanged(paramInt);
    }

    /*public void clear() {
        int i = 0;
        boolean bool;
        DEBUG = bool;
        if (bool)
            Log.i(this.TAG, "clear()");
        stop();
        this.mSampleLength = i;
        signalStateChanged(i);
    }

    public void delete() {
        int i = 0;
        boolean bool;
        DEBUG = bool;
        if (bool)
            Log.i(this.TAG, "delete()");
        stop();
        if (this.mSampleFile != null)
            this.mSampleFile.delete();
        this.mSampleFile = null;
        this.mSampleLength = i;
        signalStateChanged(i);
    }

    public int getMaxAmplitude() {
        int i = this.mState;
        if (i != 1)
            ;
        for (int j = null;; j = this.mRecorder.getMaxAmplitude())
            return j;
    }

    public int getPosition() {
        MediaPlayer localMediaPlayer = this.mPlayer;
        int i;
        if (localMediaPlayer != null)
            i = this.mPlayer.getCurrentPosition() / 1000;
        while (true) {
            return i;
            Object localObject = null;
        }
    }

    public int getStorage() {
        return Storage_setting;
    }

    // ERROR //
    public java.lang.Boolean isPauseSupport() {
        // Byte code:
        // 0: new 134 android/media/MediaRecorder
        // 3: dup
        // 4: invokespecial 152 android/media/MediaRecorder:<init> ()V
        // 7: astore_1
        // 8: aload_1
        // 9: invokevirtual 156 java/lang/Object:getClass ()Ljava/lang/Class;
        // 12: astore_2
        // 13: ldc 158
        // 15: astore_3
        // 16: aconst_null
        // 17: anewarray 160 java/lang/Class
        // 20: astore 4
        // 22: aload_2
        // 23: aload_3
        // 24: aload 4
        // 26: invokevirtual 164 java/lang/Class:getMethod
        // (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        // 29: astore 5
        // 31: aload_1
        // 32: ifnull +32 -> 64
        // 35: aconst_null
        // 36: anewarray 160 java/lang/Class
        // 39: astore 6
        // 41: aload_2
        // 42: ldc 166
        // 44: aload 6
        // 46: invokevirtual 164 java/lang/Class:getMethod
        // (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        // 49: astore_3
        // 50: aconst_null
        // 51: anewarray 4 java/lang/Object
        // 54: astore 7
        // 56: aload_3
        // 57: aload_1
        // 58: aload 7
        // 60: invokevirtual 172 java/lang/reflect/Method:invoke
        // (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
        // 63: pop
        // 64: iconst_1
        // 65: invokestatic 178 java/lang/Boolean:valueOf (Z)Ljava/lang/Boolean;
        // 68: astore_3
        // 69: aload_3
        // 70: areturn
        // 71: astore 8
        // 73: aload 8
        // 75: invokevirtual 181
        // java/lang/IllegalArgumentException:printStackTrace ()V
        // 78: aload_1
        // 79: ifnull -15 -> 64
        // 82: aconst_null
        // 83: anewarray 160 java/lang/Class
        // 86: astore 9
        // 88: aload_2
        // 89: ldc 166
        // 91: aload 9
        // 93: invokevirtual 164 java/lang/Class:getMethod
        // (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        // 96: astore_3
        // 97: aconst_null
        // 98: anewarray 4 java/lang/Object
        // 101: astore 10
        // 103: aload_3
        // 104: aload_1
        // 105: aload 10
        // 107: invokevirtual 172 java/lang/reflect/Method:invoke
        // (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
        // 110: pop
        // 111: goto -47 -> 64
        // 114: astore_3
        // 115: goto -51 -> 64
        // 118: astore 8
        // 120: aload 8
        // 122: invokevirtual 182
        // java/lang/NoSuchMethodException:printStackTrace ()V
        // 125: aconst_null
        // 126: invokestatic 178 java/lang/Boolean:valueOf
        // (Z)Ljava/lang/Boolean;
        // 129: astore_3
        // 130: aload_1
        // 131: ifnull -62 -> 69
        // 134: aconst_null
        // 135: anewarray 160 java/lang/Class
        // 138: astore 11
        // 140: aload_2
        // 141: ldc 166
        // 143: aload 11
        // 145: invokevirtual 164 java/lang/Class:getMethod
        // (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        // 148: astore 12
        // 150: aconst_null
        // 151: anewarray 4 java/lang/Object
        // 154: astore 13
        // 156: aload 12
        // 158: aload_1
        // 159: aload 13
        // 161: invokevirtual 172 java/lang/reflect/Method:invoke
        // (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
        // 164: pop
        // 165: goto -96 -> 69
        // 168: astore 14
        // 170: goto -101 -> 69
        // 173: astore_3
        // 174: aload_1
        // 175: ifnull +34 -> 209
        // 178: aconst_null
        // 179: anewarray 160 java/lang/Class
        // 182: astore 15
        // 184: aload_2
        // 185: ldc 166
        // 187: aload 15
        // 189: invokevirtual 164 java/lang/Class:getMethod
        // (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        // 192: astore 16
        // 194: aconst_null
        // 195: anewarray 4 java/lang/Object
        // 198: astore 17
        // 200: aload 16
        // 202: aload_1
        // 203: aload 17
        // 205: invokevirtual 172 java/lang/reflect/Method:invoke
        // (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
        // 208: pop
        // 209: aload_3
        // 210: athrow
        // 211: astore_3
        // 212: goto -148 -> 64
        // 215: astore 18
        // 217: goto -8 -> 209
        //
        // Exception table:
        // from to target type
        // 13 31 71 java/lang/IllegalArgumentException
        // 82 111 114 java/lang/Exception
        // 13 31 118 java/lang/NoSuchMethodException
        // 134 165 168 java/lang/Exception
        // 13 31 173 finally
        // 73 78 173 finally
        // 120 130 173 finally
        // 35 64 211 java/lang/Exception
        // 178 209 215 java/lang/Exception
    }

    public void onCompletion(MediaPlayer paramMediaPlayer) {
        stop();
        boolean bool;
        DEBUG = bool;
        if (bool)
            Log.v(this.TAG, "----------onCompletion--------");
    }

    public boolean onError(MediaPlayer paramMediaPlayer, int paramInt1,
            int paramInt2) {
        stop();
        setError(1);
        return true;
    }

    public void pause() {
        int i = this.mSampleLength;
        long l1 = System.currentTimeMillis();
        long l2 = this.mSampleStart;
        Object localObject;
        int j = (int) ((localObject - l2) / 1000L);
        int k = i + j;
        this.mSampleLength = k;
        DEBUG = k;
        if (k != 0)
            Log.i(this.TAG, "--->to mRecorder.pause()...");
        Class localClass = this.mRecorder.getClass();
        try {
            Class[] arrayOfClass = new Class[null];
            Method localMethod = localClass.getMethod("pause", arrayOfClass);
            localMethod.setAccessible(true);
            MediaRecorder localMediaRecorder = this.mRecorder;
            Object[] arrayOfObject = new Object[null];
            localMethod.invoke(localMediaRecorder, arrayOfObject);
            setState(3);
            return;
        } catch (IllegalArgumentException localIllegalArgumentException) {
            while (true)
                localIllegalArgumentException.printStackTrace();
        } catch (IllegalAccessException localIllegalAccessException) {
            while (true)
                localIllegalAccessException.printStackTrace();
        } catch (InvocationTargetException localInvocationTargetException) {
            while (true)
                localInvocationTargetException.printStackTrace();
        } catch (NoSuchMethodException localNoSuchMethodException) {
            while (true)
                localNoSuchMethodException.printStackTrace();
        }
    }

    public int progress() {
        int i = this.mState;
        long l2;
        long l3;
        if (i != 1) {
            i = this.mState;
            if (i != 2)
                ;
        } else {
            DEBUG = i;
            if (i != 0) {
                localObject1 = this.TAG;
                StringBuilder localStringBuilder = new StringBuilder(
                        "Progress() mSampleLength = ");
                int k = this.mSampleLength;
                String str = k;
                Log.i((String) localObject1, str);
            }
            Object localObject1 = System.currentTimeMillis();
            long l1 = this.mSampleStart;
            Object localObject2;
            l2 = (localObject2 - l1) / 1000L;
            l3 = this.mSampleLength;
        }
        for (int j = (int) (l2 + l3);; j = this.mSampleLength)
            return j;
    }

    public void resetState() {
        int i = 0;
        boolean bool;
        DEBUG = bool;
        if (bool)
            Log.i(this.TAG, "resetState()");
        this.mSampleFile = null;
        this.mSampleLength = i;
        signalStateChanged(i);
    }

    public void restoreState(Bundle paramBundle) {
        int i = -1;
        boolean bool;
        DEBUG = bool;
        if (bool)
            Log.i(this.TAG, "restoreState() ");
        String str1 = paramBundle.getString("sample_path");
        if (str1 == null)
            ;
        while (true) {
            return;
            int j = paramBundle.getInt("sample_length", i);
            if (j == i)
                continue;
            File localFile = new File(str1);
            if (!localFile.exists())
                continue;
            if (this.mSampleFile != null) {
                String str2 = this.mSampleFile.getAbsolutePath();
                String str3 = localFile.getAbsolutePath();
                if (str2.compareTo(str3) == 0)
                    continue;
            }
            delete();
            this.mSampleFile = localFile;
            this.mSampleLength = j;
            signalStateChanged(0);
        }
    }

    public void resume() {
        int i = 1;
        long l = System.currentTimeMillis();
        Object localObject;
        this.mSampleStart = localObject;
        DEBUG = l;
        if (l != 0)
            Log.i(this.TAG, "--->to mRecorder.resume()...");
        Class localClass = this.mRecorder.getClass();
        try {
            Class[] arrayOfClass = new Class[null];
            Method localMethod = localClass.getMethod("resume", arrayOfClass);
            localMethod.setAccessible(true);
            MediaRecorder localMediaRecorder = this.mRecorder;
            Object[] arrayOfObject = new Object[null];
            localMethod.invoke(localMediaRecorder, arrayOfObject);
            setState(i);
            return;
        } catch (IllegalArgumentException localIllegalArgumentException) {
            while (true)
                localIllegalArgumentException.printStackTrace();
        } catch (IllegalAccessException localIllegalAccessException) {
            while (true)
                localIllegalAccessException.printStackTrace();
        } catch (InvocationTargetException localInvocationTargetException) {
            while (true)
                localInvocationTargetException.printStackTrace();
        } catch (NoSuchMethodException localNoSuchMethodException) {
            while (true)
                localNoSuchMethodException.printStackTrace();
        }
    }

    public File sampleFile() {
        return this.mSampleFile;
    }

    public int sampleLength() {
        String str1 = this.TAG;
        StringBuilder localStringBuilder = new StringBuilder("mSampleLength = ");
        int i = this.mSampleLength;
        String str2 = i;
        Log.i(str1, str2);
        return this.mSampleLength;
    }

    public void saveState(Bundle paramBundle) {
        String str = this.mSampleFile.getAbsolutePath();
        paramBundle.putString("sample_path", str);
        int i = this.mSampleLength;
        paramBundle.putInt("sample_length", i);
    }

    public void setOnStateChangedListener(
            OnStateChangedListener paramOnStateChangedListener) {
        this.mOnStateChangedListener = paramOnStateChangedListener;
    }

    public void setStorage(int paramInt) {
        Storage_setting = paramInt;
        if (Storage_setting == 0)
            Storage_path = SoundRecorder.STORAGE_PATH_LOCAL_PHONE;
        while (true) {
            return;
            if (Storage_setting == 1) {
                Storage_path = SoundRecorder.STORAGE_PATH_SD_CARD;
                continue;
            }
            setStorage(0);
        }
    }

    public void startPlayback() {
        int i = 0;
        int j = 2;
        stop();
        boolean bool;
        DEBUG = bool;
        if (bool) {
            String str1 = this.TAG;
            StringBuilder localStringBuilder1 = new StringBuilder(
                    "-----mSampleFile.path = ");
            String str2 = this.mSampleFile.getAbsolutePath();
            String str3 = str2;
            Log.v(str1, str3);
        }
        MediaPlayer localMediaPlayer1 = new MediaPlayer();
        this.mPlayer = localMediaPlayer1;
        try {
            MediaPlayer localMediaPlayer2 = this.mPlayer;
            String str4 = this.mSampleFile.getAbsolutePath();
            localMediaPlayer2.setDataSource(str4);
            this.mPlayer.setOnCompletionListener(this);
            this.mPlayer.setOnErrorListener(this);
            this.mPlayer.prepare();
            int k = this.mPlayer.getDuration();
            int m = k / 1000;
            DEBUG = k;
            if (k != 0) {
                String str5 = this.TAG;
                StringBuilder localStringBuilder2 = new StringBuilder(
                        "-------duration = ").append(m).append("-----path = ");
                String str6 = this.mSampleFile.getAbsolutePath();
                String str7 = str6;
                Log.v(str5, str7);
            }
            this.mPlayer.start();
            long l = System.currentTimeMillis();
            Object localObject;
            this.mSampleStart = localObject;
            setState(j);
            return;
        } catch (IllegalArgumentException localIllegalArgumentException) {
            while (true) {
                setError(j);
                this.mPlayer = i;
            }
        } catch (IOException localIOException) {
            while (true) {
                setError(1);
                this.mPlayer = i;
            }
        }
    }

    public void startRecording(int paramInt1, String paramString, Context paramContext, int paramInt2)
{
  boolean bool1;
  DEBUG = bool1;
  if (bool1)
  {
    localObject1 = this.TAG;
    Log.i((String)localObject1, "--->to start...");
  }
  stop();
  Object localObject1 = this.mSampleFile;
  File localFile1;
  if (localObject1 == null)
  {
    DEBUG = (Z)localObject1;
    if (localObject1 != null)
    {
      localObject1 = this.TAG;
      Log.i((String)localObject1, "--->mSampleFile == null to create new file...");
    }
    localObject1 = Storage_path;
    localFile1 = new File((String)localObject1);
    localObject1 = localFile1.exists();
    if (localObject1 == 0)
    {
      boolean bool2 = localFile1.mkdirs();
      DEBUG = localObject1;
      if (localObject1 != 0)
      {
        localObject1 = this.TAG;
        String str1 = "   startRecording    sampleDir not exit " + localFile1 + " suc =" + bool2;
        Log.v((String)localObject1, str1);
      }
    }
    localObject1 = localFile1.canWrite();
    if (localObject1 == 0)
    {
      localObject1 = SoundRecorder.STORAGE_PATH_LOCAL_PHONE;
      localFile1 = new File((String)localObject1);
    }
    DEBUG = localObject1;
    if (localObject1 != 0)
    {
      localObject1 = this.TAG;
      String str2 = "---------sampleDir = " + localFile1;
      Log.v((String)localObject1, str2);
    }
  }
  AudioManager localAudioManager;
  try
  {
    long l1 = System.currentTimeMillis();
    Object localObject3;
    Date localDate = new Date(localObject3);
    String str3 = this.sdFormatter.format(localDate);
    localObject1 = new StringBuilder("Recording_").append(str3);
    String str4 = paramString;
    localObject1 = ((StringBuilder)localObject1).append(str4);
    String str5 = ((StringBuilder)localObject1).toString();
    DEBUG = (Z)localObject1;
    if (localObject1 != null)
    {
      localObject1 = this.TAG;
      StringBuilder localStringBuilder1 = new StringBuilder("---------mSampleFile Name= ");
      File localFile2 = this.mSampleFile;
      String str6 = localFile2 + "---strName = " + str5;
      Log.v((String)localObject1, str6);
    }
    localObject1 = new File(localFile1, str5);
    this.mSampleFile = ((File)localObject1);
    localObject1 = this.mSampleFile;
    ((File)localObject1).createNewFile();
    DEBUG = (Z)localObject1;
    if (localObject1 != null)
    {
      localObject1 = this.TAG;
      StringBuilder localStringBuilder2 = new StringBuilder("--->mSampleFile =");
      String str7 = this.mSampleFile.getAbsolutePath();
      String str8 = str7;
      Log.i((String)localObject1, str8);
    }
    localObject1 = new MediaRecorder();
    this.mRecorder = ((MediaRecorder)localObject1);
    DEBUG = (Z)localObject1;
    if (localObject1 != null)
    {
      localObject1 = this.TAG;
      Log.i((String)localObject1, "--->mRecorder to set start parameter...");
    }
    this.mRecorder.setAudioSource(1);
    localObject1 = this.mRecorder;
    int k = paramInt1;
    ((MediaRecorder)localObject1).setOutputFormat(k);
    int i = 1;
    if (paramInt1 == i)
    {
      localObject2 = this.mRecorder;
      ((MediaRecorder)localObject2).setAudioEncodingBitRate(96000);
    }
    localObject2 = this.mRecorder;
    int m = paramInt2;
    ((MediaRecorder)localObject2).setAudioEncoder(m);
    localObject2 = this.mRecorder;
    String str9 = this.mSampleFile.getAbsolutePath();
    ((MediaRecorder)localObject2).setOutputFile(str9);
  }
  catch (IOException localIOException2)
  {
    try
    {
      DEBUG = (Z)localObject2;
      if (localObject2 != null)
      {
        localObject2 = this.TAG;
        Log.i((String)localObject2, "--->mRecorder.prepare()");
      }
      localObject2 = this.mRecorder;
      ((MediaRecorder)localObject2).prepare();
    }
    catch (IOException localIOException2)
    {
      try
      {
        DEBUG = (Z)localObject2;
        if (localObject2 != null)
        {
          localObject2 = this.TAG;
          Log.i((String)localObject2, "--->mRecorder.start()");
        }
        localObject2 = this.mRecorder;
        ((MediaRecorder)localObject2).start();
        long l2 = System.currentTimeMillis();
        Object localObject4;
        this.mSampleStart = localObject4;
        setState(1);
        while (true)
        {
          return;
          localIOException1 = localIOException1;
          setError(1);
          continue;
          localIOException2 = localIOException2;
          setError(2);
          this.mRecorder.reset();
          this.mRecorder.release();
          this.mRecorder = null;
        }
      }
      catch (Exception localException)
      {
        localAudioManager = (AudioManager)paramContext.getSystemService("audio");
        localObject2 = localAudioManager.getMode();
        if (localObject2 == 2)
          break label796;
      }
    }
  }
  Object localObject2 = localAudioManager.getMode();
  label739: int j;
  if (localObject2 != 4)
  {
    Object localObject5 = null;
    if (localObject5 == null)
      break label802;
    j = 3;
    setError(j);
  }
  while (true)
  {
    DEBUG = j;
    if (j != 0)
      Log.v(this.TAG, "mRecorder.start()---error");
    this.mRecorder.reset();
    this.mRecorder.release();
    this.mRecorder = null;
    break;
    label796: int n = 1;
    break label739;
    label802: j = 2;
    setError(j);
  }
}

    public int state() {
        return this.mState;
    }

    public void stop() {
        boolean bool;
        DEBUG = bool;
        if (bool)
            Log.i(this.TAG, "--->to stop...");
        stopRecording();
        stopPlayback();
    }

    public void stopPlayback() {
        if (this.mPlayer == null)
            ;
        while (true) {
            return;
            this.mPlayer.stop();
            this.mPlayer.release();
            this.mPlayer = null;
            setState(0);
        }
    }

    public void stopRecording() {
        boolean bool;
        DEBUG = bool;
        if (bool)
            Log.v(this.TAG, "-----stopRecording()->mRecorder.stop() ");
        if (this.mRecorder == null)
            ;
        while (true) {
            return;
            try {
                this.mRecorder.stop();
                this.mRecorder.reset();
                this.mRecorder.release();
                label48: this.mRecorder = null;
                if (this.mState != 3) {
                    int i = this.mSampleLength;
                    long l1 = System.currentTimeMillis();
                    long l2 = this.mSampleStart;
                    Object localObject;
                    int j = (int) ((localObject - l2) / 1000L);
                    int k = i + j;
                    this.mSampleLength = k;
                }
                setState(null);
            } catch (Exception localException) {
                break label48;
            } catch (IllegalStateException localIllegalStateException) {
                break label48;
            }
        }
    }*/

    public abstract interface OnStateChangedListener {
        public abstract void onError(int paramInt);

        public abstract void onStateChanged(int paramInt);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        
    }
    public int getMaxAmplitude() {
        return 10;
    }
}