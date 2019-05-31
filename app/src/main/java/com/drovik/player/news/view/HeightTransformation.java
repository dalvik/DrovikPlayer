package com.drovik.player.news.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

import java.security.MessageDigest;

public class HeightTransformation implements Transformation<Bitmap> {

    @NonNull
    @Override
    public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
        return null;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }

    public enum CropType {
        TOP,
        CENTER,
        BOTTOM
    }

    private BitmapPool mBitmapPool;
    private int mWidth;
    private int mHeight;

    private CropType mCropType = CropType.CENTER;

    public HeightTransformation(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    private HeightTransformation(BitmapPool pool) {
        this(pool, 0, 0);
    }

    public HeightTransformation(Context context, int width, int height) {
        this(Glide.get(context).getBitmapPool(), width, height);
    }

    private HeightTransformation(BitmapPool pool, int width, int height) {
        this(pool, width, height, CropType.CENTER);
    }

    public HeightTransformation(Context context, int width, int height, CropType cropType) {
        this(Glide.get(context).getBitmapPool(), width, height, cropType);
    }

    private HeightTransformation(BitmapPool pool, int width, int height, CropType cropType) {
        mBitmapPool = pool;
        mWidth = width;
        mHeight = height;
        mCropType = cropType;
    }

    private float getTop(float scaledHeight) {
        switch (mCropType) {
            case TOP:
                return 0;
            case CENTER:
                return (mHeight - scaledHeight) / 2;
            case BOTTOM:
                return mHeight - scaledHeight;
            default:
                return 0;
        }
    }
}

