package com.drovik.player.video;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

	private ImageView[][] mImageViews;

	private List<String> mImageRes;

	public ViewPagerAdapter(ImageView[][] imageViews, List<String> imageRes) {
		this.mImageViews = imageViews;
		this.mImageRes = imageRes;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mImageRes.size() == 1) {
			return mImageViews[position / mImageRes.size() % 2][0];
		} else {
			((ViewPager) container).addView(mImageViews[position
					/ mImageRes.size() % 2][position % mImageRes.size()], 0);
		}
		return mImageViews[position / mImageRes.size() % 2][position
				% mImageRes.size()];
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mImageRes.size() == 1) {
			((ViewPager) container).removeView(mImageViews[position
					/ mImageRes.size() % 2][0]);
		} else {
			((ViewPager) container).removeView(mImageViews[position
					/ mImageRes.size() % 2][position % mImageRes.size()]);
		}
	}
}
