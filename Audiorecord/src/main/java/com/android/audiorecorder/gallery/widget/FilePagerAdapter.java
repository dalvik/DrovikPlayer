/*
 Copyright (c) 2013 Roman Truba

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
package com.android.audiorecorder.gallery.widget;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.android.audiorecorder.gallery.bitmapfun.ImageFetcher;
import com.android.audiorecorder.gallery.genuin.BitmapCache;
import com.android.audiorecorder.gallery.touchview.FileTouchImageView;
import com.android.audiorecorder.ui.pager.FileImagePager;

/**
 Class wraps file paths to adapter, then it instantiates {@link FileTouchImageView} objects to paging up through them.
 */
public class FilePagerAdapter extends BasePagerAdapter {
	
    private int mType;
    
    private Context mContext;

    private ImageFetcher mFetcher;

    private String TAG = "FilePagerAdapter";
    
    private int mFilePathListLength;
    
    private boolean mHasMore;
    
    private BitmapCache mCache;
    
	public FilePagerAdapter(Context context, int filePathLength, int type, ImageFetcher fetcher, BitmapCache cache) {
		super(context);
		mContext =context;
		mFilePathListLength = filePathLength;
		mType = type;
        mFetcher = fetcher;
        mCache = cache;
	}
	
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if(object != null) {
            ((GalleryViewPager)container).mCurrentView = ((FileTouchImageView)object).getImageView();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position){
        FileTouchImageView iv = new FileTouchImageView(mContext, mType, mFetcher, mCache);
        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setUrl(position, getCurrentPath(mType, position));
        collection.addView(iv, 0);
        return iv;
    }

    public String getCurrentPath(int type, int position) {
        return FileImagePager.getFilePath(position);
    }
    
    public void  removeItem(int pos){
        mFilePathListLength -=1;
    }
    
    @Override
    public int getCount(){
        return mFilePathListLength;
    }
    
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    
    public void setCount(int curPageNumber){
        mFilePathListLength += curPageNumber;
        Log.i(TAG, "setCount = " + curPageNumber + " hasMore = " + mHasMore);
    }
    
}
