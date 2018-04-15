package com.android.audiorecorder.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.android.audiorecorder.ParticleQueue;

import java.lang.reflect.Field;

public class ParticleSeekBar extends SeekBar {
    private final int MSG_ID_REDREW = 0;
    private Handler mHandler = null;
    private Drawable mParticleDrawable = null;
    private ParticleQueue mParticleQueue = null;

    public ParticleSeekBar(Context paramContext) {
        super(paramContext);
        init();
    }

    public ParticleSeekBar(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    public ParticleSeekBar(Context paramContext,
                           AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    private Field getField(String paramString) {
        Class localClass = getClass();
        while (true) {
            if (localClass.equals(Object.class)) {
                int i = 0;
            }
            try {
                Field localField = localClass.getDeclaredField(paramString);
                localField.setAccessible(true);
            } catch (NoSuchFieldException localNoSuchFieldException) {
                localClass = localClass.getSuperclass();
                continue;
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
    }

    private void init() {
        //this.mHandler = new ParticleSeekBarHandler(this);
        //this.mParticleQueue = new ParticleQueue(this.mHandler, 0);
    }

    public Object get(String paramString) {
       /* try {
            localObject = getField(paramString).get(this);
            return localObject;
        } catch (Exception localException) {
            while (true) {
                localException.printStackTrace();
                Object localObject = Integer.valueOf(0);
            }
        }*/
        return null;
    }

    /*protected void onDraw(Canvas paramCanvas) {
        try {
            if (this.mParticleDrawable == null)
                ;
            for (Drawable localDrawable = (Drawable) get("mThumb");; localDrawable = this.mParticleDrawable) {
                super.onDraw(paramCanvas);
                Rect localRect1 = localDrawable.getBounds();
                Rect localRect2 = new Rect(localRect1);
                this.mParticleQueue.show(paramCanvas, localDrawable);
                localDrawable.setBounds(localRect2);
                return;
            }
        } finally {
        }
    }*/

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        boolean bool = super.onKeyDown(paramInt, paramKeyEvent);
        int i = ((Drawable) get("mThumb")).getBounds().left;
        int j = getPaddingLeft();
        int k = i + j;
        int m = getHeight();
        int n = getPaddingTop();
        int i1 = m - n;
        int i2 = getPaddingBottom();
        int i3 = (i1 - i2) / 2;
        int i4 = getPaddingTop();
        int i5 = i3 + i4;
        this.mParticleQueue.produce(k, i5);
        return bool;
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        boolean bool = super.onTouchEvent(paramMotionEvent);
        ParticleQueue localParticleQueue = this.mParticleQueue;
        int i = (int) paramMotionEvent.getX();
        int j = (int) paramMotionEvent.getY();
        localParticleQueue.produce(i, j);
        return bool;
    }

    public void setParticleDrawable(Drawable paramDrawable) {
        this.mParticleDrawable = paramDrawable;
    }

    public void setParticleResource(int paramInt) {
        Drawable localDrawable = getResources().getDrawable(paramInt);
        this.mParticleDrawable = localDrawable;
    }

    private class ParticleSeekBarHandler extends Handler {
        public ParticleSeekBarHandler(ParticleSeekBar particleSeekBar) {

        }

        public void handleMessage(Message paramMessage) {
            switch (paramMessage.what) {
                default:
                    super.handleMessage(paramMessage);
                case 0:
            }
        }
    }
}