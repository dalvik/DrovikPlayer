/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.audiorecorder.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.SoundRecorder.Recorder;

public class VUMeter extends View {
    static final float PIVOT_RADIUS = 3.5f;
    static final float PIVOT_Y_OFFSET = 10f;
    static final float SHADOW_OFFSET = 2.0f;
    static final float DROPOFF_STEP = 0.18f;
    static final float SURGE_STEP = 0.35f;
    static final long  ANIMATION_INTERVAL = 70;
    
    Paint mPaint, mShadow;
    float mCurrentAngle;
    private float x0;
    private float y0;
    private Bitmap deciel_top;
    Recorder mRecorder;
    private boolean mIsInvalidate;

    public VUMeter(Context context) {
        super(context);
        init(context);
    }

    public VUMeter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    void init(Context context) {
        deciel_top = BitmapFactory.decodeResource(getResources(), R.drawable.decibel_top);
        
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadow.setColor(Color.argb(60, 0, 0, 0));
        
        mRecorder = null;
        
        mCurrentAngle = 0;
        mIsInvalidate = false;
    }

    public void setRecorder(Recorder recorder) {
    	mRecorder = recorder;
    	invalidate();
    }
    
    public boolean isInvalidate(){
        return mIsInvalidate;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float minAngle = (float)Math.PI/8;
        final float maxAngle = (float)Math.PI*7/8;

        float angle = minAngle;
        if (mRecorder != null) {
            angle += (float)(maxAngle - minAngle)*mRecorder.getMaxAmplitude()/32768;
        }
        if (angle > mCurrentAngle)
            mCurrentAngle = angle;
        else
            mCurrentAngle = Math.max(angle, mCurrentAngle - DROPOFF_STEP);

        mCurrentAngle = Math.min(maxAngle, mCurrentAngle);
        float w = getWidth();
        float h = getHeight();
        float pivotX = w/2;
        float pivotY = h - PIVOT_RADIUS - PIVOT_Y_OFFSET;
        float l = h*4/5;
        float sin = (float) Math.sin(mCurrentAngle);
        float cos = (float) Math.cos(mCurrentAngle);
        x0 = pivotX - l*cos;
        y0 = pivotY - l*sin;
        canvas.drawLine(x0 + SHADOW_OFFSET, y0 + SHADOW_OFFSET, pivotX + SHADOW_OFFSET, pivotY + SHADOW_OFFSET, mShadow);
        canvas.drawCircle(pivotX + SHADOW_OFFSET, pivotY + SHADOW_OFFSET, PIVOT_RADIUS, mShadow);
        canvas.drawLine(x0, y0, pivotX, pivotY, mPaint);
        canvas.drawCircle(pivotX, pivotY, PIVOT_RADIUS, mPaint);
        canvas.drawBitmap(deciel_top, (w - deciel_top.getWidth())/2, (h - deciel_top.getHeight()-5), null);
        postInvalidateDelayed(ANIMATION_INTERVAL);
        if (mRecorder != null && mRecorder.state()) {
            mIsInvalidate = true;
        } else {
            mIsInvalidate = false;
        }
    }
    
}
