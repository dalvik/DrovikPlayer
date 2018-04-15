package com.android.audiorecorder;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class ParticleQueue
{
  private static final int ACTION_PARTICLES_COUNT_PER_TIME = 5;
  private static final int MSG_PRODUCE_ACTION_PARTICLES = 2;
  private static final int MSG_PRODUCE_SPAN_PARTICLES = 1;
  private static final int MSG_STOP_UPDATE = 4;
  private static final int MSG_TRANSLATE_PARTICLES = 3;
  private static final int PARTICLE_UPDATE_SPAN = 100;
  private static final String TAG = "TracePointQueue";
  private Particle[] Particles;
  private final Rect mContainerRect;
  private int mFront;
  ParticlesHandler mHandler;
  HandlerThread mHandlerThread;
  private int mLastBaseX;
  private int mLastBaseY;
  private int mLastTime;
  private int mRear;
  private int mSize;
  private Handler mTarget;
  private int mTargetMsgID;

  public ParticleQueue(int paramInt1, Handler paramHandler, int paramInt2)
  {
    Rect localRect = new Rect();
    this.mContainerRect = localRect;
    HandlerThread localHandlerThread1 = new HandlerThread("ParticleQueue", -1);
    /*this.mHandlerThread = localHandlerThread1;
    this.mFront = null;
    this.mRear = null;
    this.mLastBaseX = -2147483648;
    this.mLastBaseY = -2147483648;
    this.mLastTime = -2147483648;
    Particle[] arrayOfParticle = new Particle[paramInt1];
    this.Particles = arrayOfParticle;
    this.mSize = paramInt1;
    this.mFront = null;
    this.mRear = null;*/
    this.mTarget = paramHandler;
    this.mTargetMsgID = paramInt2;
    this.mHandlerThread.start();
    Looper localLooper = this.mHandlerThread.getLooper();
    ParticlesHandler localParticlesHandler = new ParticlesHandler(localLooper);
    this.mHandler = localParticlesHandler;
    StringBuilder localStringBuilder = new StringBuilder("ParticleQueue(size, target)");
    HandlerThread localHandlerThread2 = this.mHandlerThread;
  }

  public ParticleQueue(Handler paramHandler, int paramInt)
  {
    this(50, paramHandler, paramInt);
  }

  private void addNew(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    Rect localRect1 = this.mContainerRect;
    localRect1.setEmpty();
    Object localObject = null;
    while (true)
    {
      /*if (localObject >= paramInt1)
      {
        if (!localRect1.isEmpty())
        {
          Handler localHandler = this.mTarget;
          int i = this.mTargetMsgID;
          localHandler.obtainMessage(i).sendToTarget();
        }
        return;
      }*/
      Particle localParticle = Particle.obtain();
      localParticle.init(paramInt2, paramInt3, paramInt4, paramInt5);
      enqueue(localParticle);
      Rect localRect2 = localParticle.mRect;
      localRect1.union(localRect2);
      //localObject++;
    }
  }

  /** @deprecated */
  private void enqueue(Particle paramParticle)
  {
    /*monitorenter;
    try
    {
      int i = this.mRear;
      int j = this.mFront;
      int k = this.mSize;
      if ((i + 1) % k == j)
      {
        String str = "enqueue: Queue is full,override! (rear, front)=(" + i + "," + j + ")";
        Log.d("TracePointQueue", str);
        this.Particles[j].recycle();
        int m = (j + 1) % k;
        this.mFront = m;
      }
      this.Particles[i] = paramParticle;
      int n = (i + 1) % k;
      this.mRear = n;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;*/
  }

  /** @deprecated */
  public void clear()
  {
    /*monitorenter;
    try
    {
      int i = this.mRear;
      int k;
      int m;
      for (int j = this.mFront; ; j = k % m)
      {
        if (j == i)
        {
          this.mRear = null;
          this.mFront = null;
          this.mHandlerThread.quit();
          return;
        }
        this.Particles[j].recycle();
        k = j + 1;
        m = this.mSize;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;*/
  }

  public void produce(int paramInt1, int paramInt2)
  {
    int i = -2147483648;
    int j = this.mLastBaseX;
    int k = this.mLastBaseY;
    if ((j == i) || (k == i))
    {
      this.mLastBaseX = paramInt1;
      this.mLastBaseY = paramInt2;
    }
    int m = Math.abs(paramInt1 - j);
    int n = Math.abs(paramInt2 - k);
    int i1 = (m + n) * 5 / 30;
    if (i1 > 0)
    {
      ParticleQueue localParticleQueue = this;
      int i2 = paramInt1;
      int i3 = paramInt2;
      localParticleQueue.addNew(i1, i2, i3, j, k);
      this.mLastBaseX = paramInt1;
      this.mLastBaseY = paramInt2;
    }
    sheduleTranslate();
  }

  public void sheduleTranslate()
  {
    int i = 3;
    if (!this.mHandler.hasMessages(i))
    {
      Message localMessage = this.mHandler.obtainMessage(i);
      this.mHandler.sendMessageDelayed(localMessage, 100L);
    }
  }

  /** @deprecated */
  public void show(Canvas paramCanvas, Drawable paramDrawable)
  {
    /*monitorenter;
    try
    {
      int i = this.mRear;
      int k;
      int m;
      for (int j = this.mFront; ; j = k % m)
      {
        if (j == i)
          return;
        this.Particles[j].show(paramCanvas, paramDrawable);
        k = j + 1;
        m = this.mSize;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;*/
  }

  /** @deprecated */
  public boolean translate()
  {
    /*monitorenter;
    try
    {
      Rect localRect1 = this.mContainerRect;
      int i = this.mRear;
      int j = this.mFront;
      localRect1.setEmpty();
      int k;
      if (j == i)
      {
        boolean bool = localRect1.isEmpty();
        if (bool)
          break label126;
        Handler localHandler = this.mTarget;
        int n = this.mTargetMsgID;
        localHandler.obtainMessage(n).sendToTarget();
        k = 1;
      }
      while (true)
      {
        return k;
        Particle localParticle = this.Particles[j];
        Rect localRect2 = localParticle.mRect;
        localRect1.union(localRect2);
        localParticle.translate();
        localRect2 = localParticle.mRect;
        localRect1.union(localRect2);
        int m = j + 1;
        int i1 = this.mSize;
        j = m % i1;
        break;
        label126: Object localObject1 = null;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject2;*/
      return false;
  }

  class ParticlesHandler extends Handler
  {
    public ParticlesHandler(Looper arg2)
    {
      super();
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      case 4:
      default:
      case 3:
      }
      /*while (true)
      {
        super.handleMessage(paramMessage);
        return;
        if (!ParticleQueue.this.translate())
          continue;
        ParticleQueue.this.sheduleTranslate();
      }*/
    }
  }
}