package com.android.library.location;

import com.android.library.location.MyLocation;

interface  ILocationListener {
	 void onLocationChange(in MyLocation location);
}