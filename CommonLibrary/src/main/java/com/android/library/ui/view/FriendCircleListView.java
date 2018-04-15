package com.android.library.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class FriendCircleListView extends ListView {
	public FriendCircleListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FriendCircleListView(Context context) {
		super(context);
	}

	public FriendCircleListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 该自定义控件只是重写了ListView的onMeasure方法，使其不会出现滚动条，ScrollView嵌套ListView也是同样的道理，不再赘述。
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}