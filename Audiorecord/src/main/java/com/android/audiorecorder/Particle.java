package com.android.audiorecorder;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Particle {
    static final int BASE_LITE_TIME = 5;
    static final int MAX_DELTA_X = 6;
    static final int MAX_DELTA_Y = 6;
    static final int MAX_POOL_SIZE = 50;
    static final int MAX_RADIUS = 30;
    static final int PARTICLE_Z_TRANSLATION_BASE = 10;
    private static String TAG;
    private static Particle mPool;
    private static int mPoolSize;
    private static Object mPoolSync = new Object();
    private static int mTotoalSize;
    int mDeltaX;
    int mDeltaY;
    int mDeltaZ;
    int mLifeTime;
    Rect mRect;
    Particle next;

    /*static {
        mPoolSize = null;
        mTotoalSize = null;
        TAG = "Particle";
    }
*/
    private Particle() {
        Rect localRect = new Rect();
        this.mRect = localRect;
        this.mDeltaX = 0;
        this.mDeltaY = 0;
        this.mDeltaZ = 0;
        this.mLifeTime = 0;
        this.next = null;
        int i = mTotoalSize;
        int j = 0;
        j++;
        mTotoalSize = i;
        String str1 = TAG;
        StringBuilder localStringBuilder = new StringBuilder(
                "Particle() totol size = ");
        int k = mTotoalSize;
    }

    private void clearForRecycle() {
        this.mRect.setEmpty();
        this.mDeltaX = -2147483648;
        this.mDeltaY = -2147483648;
        this.mDeltaZ = -2147483648;
    }

    public static Particle obtain() {
        synchronized (mPoolSync) {
            /*Particle localParticle1 = mPool;
            if (localParticle1 != null) {
                localParticle2 = mPool;
                mPool = localParticle2.next;
                int i = 0;
                localParticle2.next = i;
                return localParticle2;
            }*/
            Particle localParticle2 = new Particle();
        }
        return null;
    }

    public static Particle obtain(Particle paramParticle) {
        Particle localParticle = obtain();
        Rect localRect1 = localParticle.mRect;
        Rect localRect2 = paramParticle.mRect;
        localRect1.set(localRect2);
        int i = paramParticle.mDeltaX;
        localParticle.mDeltaX = i;
        int j = paramParticle.mDeltaY;
        localParticle.mDeltaY = j;
        int k = paramParticle.mDeltaZ;
        localParticle.mDeltaZ = k;
        return localParticle;
    }

    public void init(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        /*int i = (int) (Math.random() * 4618441417868443648L * 4611686018427387904L)
                + -6;
        int j = (int) (Math.random() * 4618441417868443648L * 4611686018427387904L)
                + -6;
        int k = (int) (Math.random() * 4621819117588971520L / 4611686018427387904L - 4621819117588971520L);
        int m = (int) (Math.random() * 4629137466983448576L) + 1;
        int n = m;
        paramInt1 += i;
        int i1 = paramInt2 + j;
        int i2 = paramInt1 - paramInt3;
        int i3 = paramInt2 - paramInt4;
        double d1 = Math.random();
        double d3 = i2;
        Object localObject;
        int i4 = (int) (localObject * d3) + paramInt3;
        int i7 = (int) (Math.random() * 4629137466983448576L);
        int i8 = i4 + i7 + -15;
        double d2 = Math.random();
        double d4 = i3;
        int i5 = (int) (localObject * d4) + paramInt4;
        int i9 = (int) (Math.random() * 4629137466983448576L);
        int i10 = i5 + i9 + -15;
        Rect localRect = this.mRect;
        int i11 = m / 2;
        int i12 = i8 - i11;
        int i13 = n / 2;
        int i14 = i10 - i13;
        int i15 = m / 2 + i8;
        int i16 = n / 2 + i10;
        localRect.set(i12, i14, i15, i16);
        this.mDeltaX = i;
        this.mDeltaY = j;
        this.mDeltaZ = k;
        int i6 = i8 - paramInt3;
        try {
            int i17 = Math.abs(i6);
            int i18 = Math.abs(i10 - paramInt4);
            int i19 = (i6 + i18) * 5;
            int i20 = Math.abs(paramInt1 - paramInt3);
            int i21 = Math.abs(paramInt1 - paramInt4);
            int i22 = i20 + i21;
            int i23 = i6 / i22;
            this.mLifeTime = i6;
            if (this.mLifeTime < 5)
                this.mLifeTime = 5;
            return;
        } catch (Exception localException) {
            while (true)
                this.mLifeTime = 5;
        }*/
    }

    public void recycle() {
        synchronized (mPoolSync) {
            int i = mPoolSize;
            if (i < 50) {
                clearForRecycle();
                Particle localParticle = mPool;
                this.next = localParticle;
                mPool = this;
            }
            return;
        }
    }

    public void show(Canvas paramCanvas, Drawable paramDrawable) {
        Rect localRect = this.mRect;
        paramDrawable.setBounds(localRect);
        paramDrawable.draw(paramCanvas);
    }

    public void translate() {
        Rect localRect = this.mRect;
        Object localObject1 = null;
        Object localObject2 = null;
        int k = 0;
        int m = 0;
        int n = this.mLifeTime;
        int i1 = n + -1;
        this.mLifeTime = i1;
        int i;
        int j;
        if (n > 0) {
            int i2 = localRect.centerX();
            int i3 = this.mDeltaX;
            i = i2 + i3;
            int i4 = localRect.centerY();
            int i5 = this.mDeltaY;
            j = i4 + i5;
            k = localRect.width();
            m = localRect.height();
        }
        /*int i6 = k / 2;
        int i7 = i - i6;
        int i8 = m / 2;
        int i9 = j - i8;
        int i10 = k / 2 + i;
        int i11 = m / 2 + j;
        localRect.set(i7, i9, i10, i11);*/
    }

    public void translate(boolean paramBoolean) {
        Rect localRect = this.mRect;
        int i = localRect.centerX();
        int j = this.mDeltaX;
        int k = i + j;
        int m = localRect.centerY();
        int n = this.mDeltaY;
        int i1 = m + n;
        int i2 = localRect.width();
        int i3 = this.mDeltaZ;
        int i4 = i2 * i3 / 10;
        int i5 = localRect.height();
        int i6 = this.mDeltaZ;
        int i7 = i5 * i6 / 10;
        int i8 = i4 / 2;
        int i9 = k - i8;
        int i10 = i7 / 2;
        int i11 = i1 - i10;
        int i12 = i4 / 2 + k;
        int i13 = i7 / 2 + i1;
        localRect.set(i9, i11, i12, i13);
    }
}