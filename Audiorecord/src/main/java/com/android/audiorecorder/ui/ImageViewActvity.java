/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.android.audiorecorder.ui;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.audiorecorder.R;
import com.android.audiorecorder.gallery.bitmapfun.ImageCache;
import com.android.audiorecorder.gallery.bitmapfun.ImageCache.ImageCacheParams;
import com.android.audiorecorder.gallery.bitmapfun.ImageFetcher;
import com.android.audiorecorder.gallery.genuin.BitmapCache;
import com.android.audiorecorder.gallery.genuin.GetterHandler;
import com.android.audiorecorder.gallery.genuin.ImageGetter;
import com.android.audiorecorder.gallery.genuin.ImageGetterCallback;
import com.android.audiorecorder.gallery.widget.FilePagerAdapter;
import com.android.audiorecorder.gallery.widget.GalleryViewPager;
import com.android.audiorecorder.ui.view.RotateLayout;
import com.android.audiorecorder.utils.LogUtil;
import com.android.audiorecorder.utils.StringUtil;

import java.io.File;

/**
 * show big imageview
 * single:
 * muti:
 *
 */
public class ImageViewActvity extends FragmentActivity{
    private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
    public static final int ORIENTATION_HYSTERESIS = 5;
    private int mOrientationCompensation = 0;
    
    private MyOrientationEventListener orientationEventListener;
    private RotateLayout rootView = null;
    private GalleryViewPager mGalleryViewPager;
    private String TAG = "ImageViewActvity";
    private static final String IMAGE_CACHE_DIR = "thumb";
    private FilePagerAdapter pagerAdapter;
    private final int MSG_ROTATE = 1;
    private final int MSG_MERGE = 2;
    private final int MSG_LOAD_BITMAP = 3;
    
    private final int MSG_DELETE_FILES = 10;
    private final int MSG_DELETE_COMPLETE = 11;
    private final int MSG_HIDE_PROGRESS_DIALOG = 12;
    private final int MSG_UPDATE_UI = 13;
    private final int MSG_DECODE_BITMAP = 14;
    
    private ImageFetcher mImageFetcher;
    
    private BroadcastReceiver receiver = null;

    private int mNewFileNumber;
    
    private final GetterHandler mGetterHandler = new GetterHandler();
    
    private ImageGetter mGetter;
    
    private BitmapCache mCache;
    
    private int mCurrentSelect = -1;
    
    //private CustomProgressDialog m_Dialog = null;
    private Object locked = new Object();
    private boolean mDeleteResult;
    private HandlerThread mFileDeleteHandlerThread;
    private Handler mFileDeleteHandler;
    private FileDeleteHandlerCallback mFileProcHandlerCallback = new FileDeleteHandlerCallback();
    
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ROTATE:
                    rootView.setOrientation(mOrientationCompensation, true);
                    rootView.scheduleLayoutAnimation();
                    pagerAdapter.notifyDataSetChanged();
                    Bitmap bitmap = mCache.getBitmap(mCurrentSelect);
                    if(bitmap != null){
                        mCache.put(mCurrentSelect, bitmap);
                        mGalleryViewPager.mCurrentView.setTag(1);
                        mGalleryViewPager.mCurrentView.setImageBitmap(bitmap);
                        return;
                    }
                    LogUtil.d(TAG, "rotate reload image = " + mCurrentSelect);
                    //mGetter.setPosition(mCurrentSelect, pagerAdapter.getCurrentPath(categoryType, mCurrentSelect), cb, mGetterHandler);
                    loadBitmap(mCurrentSelect);
                    break;
                case MSG_MERGE:
                    pagerAdapter.setCount(mNewFileNumber);
                    pagerAdapter.notifyDataSetChanged();
                    int currentIndex = mGalleryViewPager.getCurrentItem();
                    int total = pagerAdapter.getCount();
                    LogUtil.i(TAG, "currentIndex = " + currentIndex  + " total = " + total);
                    if(total == 0) {
                        ImageViewActvity.this.finish();
                        return;
                    }
                    if(mCurrentSelect>=total){
                        mCurrentSelect = mCurrentSelect - 1;
                    }
                    mGalleryViewPager.setCurrentItem(mCurrentSelect);
                    loadBitmap(mCurrentSelect);
                    break;
                case MSG_LOAD_BITMAP:
                    loadBitmap(mCurrentSelect);
                    break;
                case MSG_DELETE_COMPLETE:
                    int current = mGalleryViewPager.getCurrentItem();
                    int count = pagerAdapter.getCount();
                    LogUtil.i(TAG, "mCurrentSelect = " + mCurrentSelect  + " count = " + count);
                    if(count == 0 || mCurrentSelect<=-1) {
                        ImageViewActvity.this.finish();
                        return;
                    }
                    clearCache();
                    pagerAdapter.removeItem(current);
                    mCurrentSelect = current;
                    pagerAdapter.notifyDataSetChanged();
                    mGalleryViewPager.setCurrentItem(mCurrentSelect);
                    loadBitmap(mCurrentSelect);
                    break;
                case MSG_HIDE_PROGRESS_DIALOG:
                    hideProgressDialg();
                    break;
                default:
                    break;
            }
        };
    };
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        com.android.library.utils.Utils.setStatusTextColor(true, this);
        mFileDeleteHandlerThread = new HandlerThread("ImageView");
        mFileDeleteHandlerThread.start();
        mFileDeleteHandler = new Handler(mFileDeleteHandlerThread.getLooper(), mFileProcHandlerCallback);
        orientationEventListener = new MyOrientationEventListener(this);
        orientationEventListener.enable();
        setContentView(R.layout.layout_image_view);
        rootView = (RotateLayout)findViewById(R.id.picture_view_root);
        /*rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);*/
        ImageCacheParams cacheParams = new ImageCacheParams(this, IMAGE_CACHE_DIR);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        final int longest = (height > width ? height : width) * 2/3;
        mImageFetcher = new ImageFetcher(this, longest);
        cacheParams.setMemCacheSizePercent(0.25f);
        mImageFetcher.setImageFadeIn(false);
        mImageFetcher.setImageSize(240, 240);
        mImageFetcher.addImageCache(getFragmentManager(), cacheParams);
        Intent intent = getIntent();
        mCurrentSelect = intent.getIntExtra("TAG_GRID_INDEX", 0);
        mNewFileNumber = intent.getIntExtra("TAG_PATH_LIST_SIZE", 0);
        mGetter = new ImageGetter();
        mCache = new BitmapCache(1);
        mGalleryViewPager = (GalleryViewPager)findViewById(R.id.viewer);
        pagerAdapter = new FilePagerAdapter(this, mNewFileNumber,0, mImageFetcher, mCache);
        mGalleryViewPager.setAdapter(pagerAdapter);
        mGalleryViewPager.setOffscreenPageLimit(1);
        mGalleryViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            
            @Override
            public void onPageSelected(int index) {
                ImageCache imageCache = mImageFetcher.getImageCache();
                if(imageCache != null) {
                    BitmapDrawable value = imageCache.getBitmapFromMemCache(pagerAdapter.getCurrentPath(0, mCurrentSelect));
                    if(value != null){
                        mGalleryViewPager.mCurrentView.setImageBitmap(value.getBitmap());
                    }
                }
                LogUtil.i(TAG, "---> page index " + index + " select.");
                mCurrentSelect = index;
            }
            
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            
            @Override
            public void onPageScrollStateChanged(int state) {
                if(ViewPager.SCROLL_STATE_IDLE == state){
                    mHandler.removeMessages(MSG_LOAD_BITMAP);
                    mHandler.sendEmptyMessageDelayed(MSG_LOAD_BITMAP, 0);
                }
            }
        });
        mGalleryViewPager.setCurrentItem(mCurrentSelect);
        mHandler.removeMessages(MSG_LOAD_BITMAP);
        mHandler.sendEmptyMessageDelayed(MSG_LOAD_BITMAP, 50);
        ActionBar localActionBar = getActionBar();
        if(localActionBar != null){
            localActionBar.setCustomView(R.layout.layout_actionbar_customview);
            //localActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_ground_transparent));
            localActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            localActionBar.getCustomView().findViewById(android.R.id.home).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ImageViewActvity.this.finish();

                }
            });
            updateTitle(pagerAdapter.getCurrentPath(0, mCurrentSelect));
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        orientationEventListener.disable();
        ImageViewActvity.this.finish();
    }
    
    protected void onDestroy() {
        super.onDestroy();
        mDeleteResult = false;
        clearCache();
        if(mGetter != null){
            mGetter.stop();
            mGetter = null;
        }
        if(mImageFetcher != null){
            mImageFetcher.closeCache();
            mImageFetcher = null;
        }
        if(receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        if(mFileDeleteHandlerThread != null) {
            mFileDeleteHandlerThread.quit();
            mFileDeleteHandlerThread = null;
        }
    };
    
    private void share() {
    }
    
    private void camera() {
		Intent intent = new Intent();
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setAction("com.dahuatech.application.dhcamera.INNER_START_CAMERA");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    
    private boolean cameraIsAvilible(String pkg) {
        boolean avilible = false;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(pkg, 0);
            if(packageInfo != null) {
                avilible = true;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return avilible;
    }
    
    private void showProgressDialg(int textId) {
        /*if (m_Dialog == null) {
            m_Dialog = CustomProgressDialog.createDialog(this,
                    R.style.CustomProgressDialog);
        }
        if (m_Dialog != null
                && (!isFinishing())) {
            m_Dialog.setMessage(getResources().getString(textId));
            m_Dialog.setCancelable(false);
            if (!m_Dialog.isShowing()) {
                m_Dialog.show();
            }
        }*/
    }

    private void updateTitle(String path){
        File tem = new File(path);
        String name = tem.getName();
        String title = tem.getName().substring(0, name.lastIndexOf("."));
        ActionBar localActionBar = getActionBar();
        if(localActionBar != null){
            TextView textViewTitle = (TextView) localActionBar.getCustomView().findViewById(R.id.title);
            textViewTitle.setText(title);
        }
    }
    
    public void hideProgressDialg() {
        /*if (m_Dialog != null && (!isFinishing()) && m_Dialog.isShowing()) {
            m_Dialog.dismiss();
        }*/
    }
    
    private class FileDeleteHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_DELETE_FILES:
                    //delete();
                    break;
                case MSG_DECODE_BITMAP:
                    mCache.recycleLast();
                    mGetter.setPosition(mCurrentSelect, pagerAdapter.getCurrentPath(0, mCurrentSelect), cb, mGetterHandler);
                    break;
                 default:
                    break;
            }
            return true;
        }
    }
    
      private class MyOrientationEventListener extends OrientationEventListener {
          public MyOrientationEventListener(Context context) {
              super(context);
          }

          @Override
          public void onOrientationChanged(int orientation) {
              // We keep the last known orientation. So if the user first orient
              // the camera then point the camera to floor or sky, we still have
              // the correct orientation.
              if (orientation == ORIENTATION_UNKNOWN)
                  return;
              mOrientation = StringUtil.roundOrientation(orientation, mOrientation);
              // When the screen is unlocked, display rotation may change. Always
              // calculate the up-to-date orientationCompensation.
              int orientationCompensation = mOrientation + StringUtil.getDisplayRotation(ImageViewActvity.this);   
              if (mOrientationCompensation != orientationCompensation) {
                  mOrientationCompensation = orientationCompensation;
                  mHandler.removeMessages(MSG_ROTATE);
                  mHandler.sendEmptyMessageDelayed(MSG_ROTATE, 150);
              }
          }
      }
      
      public void clearCache(){
          if(mCache != null) {
              mCache.clear();
          }
      }
      
      private void loadBitmap(int currentPos){
          Bitmap bitmap = mCache.getBitmap(currentPos);
          if(bitmap != null){
              mGalleryViewPager.mCurrentView.setTag(1);
              mGalleryViewPager.mCurrentView.setImageBitmap(bitmap);
              return;
          }
          updateTitle(pagerAdapter.getCurrentPath(0, currentPos));
          mFileDeleteHandler.removeMessages(MSG_DECODE_BITMAP);
          mFileDeleteHandler.sendEmptyMessage(MSG_DECODE_BITMAP);
      }
      
      private ImageGetterCallback cb = new ImageGetterCallback() {
          
          public void completed() {
          }


          public int fullImageSizeToUse(int pos, int offset) {
              // this number should be bigger so that we can zoom.  we may
              // need to get fancier and read in the fuller size image as the
              // user starts to zoom.
              // Originally the value is set to 480 in order to avoid OOM.
              // Now we set it to 2048 because of using
              // native memory allocation for Bitmaps.
              final int imageViewSize = 2047;
              return imageViewSize;
          }


          public void imageLoaded(int pos, int offset, Bitmap bitmap) {
              // shouldn't get here after onPause()
              // We may get a result from a previous request. Ignore it.
              LogUtil.i(TAG, "imageLoaded = " + pos + " mCurrentSelect = " + mCurrentSelect);
              if (pos != mCurrentSelect) {
                  bitmap.recycle();
                  return;
              }
              mCache.put(pos, bitmap);
              // isThumb: We always load thumb bitmap first, so we will
              // reset the supp matrix for then thumb bitmap, and keep
              // the supp matrix when the full bitmap is loaded.
              mGalleryViewPager.mCurrentView.setTag(1);
              mGalleryViewPager.mCurrentView.setImageBitmap(bitmap);
          }
      };
}
