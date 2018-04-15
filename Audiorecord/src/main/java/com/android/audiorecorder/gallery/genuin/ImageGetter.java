/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.android.audiorecorder.gallery.genuin;


import android.graphics.Bitmap;
import android.os.Process;
import android.util.Log;

/*
 * Here's the loading strategy.  For any given image, load the thumbnail
 * into memory and post a callback to display the resulting bitmap.
 *
 * Then proceed to load the full image bitmap.   Three things can
 * happen at this point:
 *
 * 1.  the image fails to load because the UI thread decided
 * to move on to a different image.  This "cancellation" happens
 * by virtue of the UI thread closing the stream containing the
 * image being decoded.  BitmapFactory.decodeStream returns null
 * in this case.
 *
 * 2.  the image loaded successfully.  At that point we post
 * a callback to the UI thread to actually show the bitmap.
 *
 * 3.  when the post runs it checks to see if the image that was
 * loaded is still the one we want.  The UI may have moved on
 * to some other image and if so we just drop the newly loaded
 * bitmap on the floor.
 */

public class ImageGetter {

    private static final String TAG = "ViewImage";

    // The thread which does the work.
    private Thread mGetterThread;

    // The current request serial number.
    // This is increased by one each time a new job is assigned.
    // It is only written in the main thread.
    private int mCurrentSerial;
    
    private String mPath;

    private String mPathTemp;
    
    // The base position that's being retrieved.  The actual images retrieved
    // are this base plus each of the offets. -1 means there is no current
    // request needs to be finished.
    private int mCurrentPosition = -1;

    // The callback to invoke for each image.
    private ImageGetterCallback mCB;

    // The handler to do callback.
    private GetterHandler mHandler;

    // True if we want to cancel the current loading.
    private volatile boolean mCancel = true;

    // True if the getter thread is idle waiting.
    private boolean mIdle = false;

    // True when the getter thread should exit.
    private boolean mDone = false;

    private class ImageGetterRunnable implements Runnable {

        private Runnable callback(final int position, final int offset,
                                  final boolean isThumb,
                                  final Bitmap bitmap,
                                  final int requestSerial) {
            return new Runnable() {
                public void run() {
                    // check for inflight callbacks that aren't applicable
                    // any longer before delivering them 
                    Log.d(TAG,"Runnable---> requestSerial = " + requestSerial + "  mCurrentSerial = " + mCurrentSerial);
                    if (requestSerial == mCurrentSerial) {
                        mCB.imageLoaded(position, offset, bitmap);
                    } else if (bitmap != null) {
                        bitmap.recycle();
                    }
                    mPathTemp = null;
                }
            };
        }

        private Runnable completedCallback(final int requestSerial) {
            return new Runnable() {
                public void run() {
                    if (requestSerial == mCurrentSerial) {
                        mCB.completed();
			   Log.d(TAG, "image getter completedCallback");
                    }
                }
            };
        }

        public void run() {
            // Lower the priority of this thread to avoid competing with
            // the UI thread.
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            while (true) {
                synchronized (ImageGetter.this) {
                    while (mCancel || mDone || mCurrentPosition == -1) {
                        if (mDone) return;
                        mIdle = true;
                        ImageGetter.this.notify();
                        try {
                            ImageGetter.this.wait();
                        } catch (InterruptedException ex) {
                            // ignore
                        }
                        mIdle = false;
                    }
                }
                executeRequest();

                synchronized (ImageGetter.this) {
                    mCurrentPosition = -1;
                }
            }
        }
        
        private void executeRequest() {
            int sizeToUse = mCB.fullImageSizeToUse(
                    mCurrentPosition, 0);
            if(mPathTemp != null && mPathTemp.equalsIgnoreCase(mPath)){
                mPathTemp = null;
                Log.w(TAG, "same path not load image. mCurrentPosition " + mCurrentPosition);
                return;
            }
            mPathTemp = mPath;
            Bitmap b = Util.makeBitmap(sizeToUse, 3 * 1024 * 1024, mPath,  IImage.USE_NATIVE);
            if (b == null) {
                Log.w(TAG, "load image null mCurrentPosition = " + mCurrentPosition);
                return;
            }
            if (mCancel) {
                b.recycle();
                b = null;
                mPathTemp = null;
                Log.w(TAG, "cancle, load image null mCurrentPosition = " + mCurrentPosition);
                return;
            }
            Runnable cb = callback(mCurrentPosition, 0,
                    false, b, mCurrentSerial);
            mHandler.postGetterCallback(cb);
            mHandler.postGetterCallback(completedCallback(mCurrentSerial));
        }
    }

    public ImageGetter() {
        mGetterThread = new Thread(new ImageGetterRunnable());
        mGetterThread.setName("ImageGettter");
        mGetterThread.start();
    }

    // Cancels current loading (without waiting).
    public synchronized void cancelCurrent() {
        mCancel = true;
        BitmapManager.instance().cancelThreadDecoding(mGetterThread);
    }

    // Cancels current loading (with waiting).
    private synchronized void cancelCurrentAndWait() {
        cancelCurrent();
        while (mIdle != true) {
            try {
                wait();
            } catch (InterruptedException ex) {
                // ignore.
            }
        }
    }

    // Stops this image getter.
    public void stop() {
        synchronized (this) {
            cancelCurrentAndWait();
            mDone = true;
            notify();
        }
        try {
            mGetterThread.join();
        } catch (InterruptedException ex) {
            // Ignore the exception
        }
        mGetterThread = null;
    }

    public synchronized void setPosition(int position, String path, ImageGetterCallback cb, GetterHandler handler) {
        // Cancel the previous request.
        cancelCurrentAndWait();

        // Set new data.
        mCurrentPosition = position;
        mPath = path;
        mCB = cb;
        mHandler = handler;
        mCurrentSerial = mCurrentPosition;//+= 1;
        Log.i(TAG, "setPosition = " + mCurrentPosition);
        // Kick-start the current request.
        mCancel = false;
        BitmapManager.instance().allowThreadDecoding(mGetterThread);
        notify();
    }
}

