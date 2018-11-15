package com.android.audiorecorder.ui.activity;

import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.android.audiorecorder.R;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.audiorecorder.utils.URLS;
import com.android.library.ui.activity.BaseCompatActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import uk.co.senab.photoview.PhotoView;

public class ShowBigImageActivity extends BaseCompatActivity {

    private String mUrl;

    PhotoView mPv;
    ProgressBar mPb;
    private FrameLayout mView;
    private PopupWindow mPopupWindow;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_show_big_image);
    	//actionView.setVisibility(View.GONE);
    	mPv = (PhotoView) findViewById(R.id.pv);
    	mPv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				ShowBigImageActivity.this.finish();
				
			}
		});
    	mPb = (ProgressBar) findViewById(R.id.pb);
    	//mPv.enable();// 启用图片缩放功能
    	imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
        //.showImageOnLoading(defaultHeaderRes)
        //.showImageForEmptyUri(defaultHeaderRes)
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .bitmapConfig(Config.RGB_565)
        .build();
        ImageLoader.getInstance().displayImage(URLS.DOMAIN + mUrl, mPv, options);
    	//ImageLoaderManager.LoadNetImage(mUrl, mPv);
    }
    
    @Override
    protected boolean initIntent() {
    	mUrl = getIntent().getStringExtra(ActivityUtil.INTENT_URL);
        if (mUrl == null || mUrl.length() == 0) {
            return false;
        }
        return true;
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.itemMore:
                showPopupMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu() {
        if (mView == null) {
            mView = new FrameLayout(this);
            mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mView.setBackgroundColor(UIUtils.getColor(R.color.white));

            TextView tv = new TextView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2Px(45));
            tv.setLayoutParams(params);
            tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            tv.setPadding(UIUtils.dip2Px(20), 0, 0, 0);
            tv.setTextColor(UIUtils.getColor(R.color.gray0));
            tv.setTextSize(14);
            tv.setText("保存到手机");
            mView.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    //下载头像
                    final String dirPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getPackageName()).getAbsolutePath();
                    final String fileName = "header.jpg";
                    OkHttpUtils.get().url(mUrl).build().execute(new FileCallBack(dirPath, fileName) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            UIUtils.showToast("头像保存失败");
                        }

                        @Override
                        public void onResponse(File response, int id) {
                            UIUtils.showToast("头像保存在" + dirPath + "/" + fileName);
                        }
                    });
                }
            });
        }
        mPopupWindow = PopupWindowFactory.getPopupWindowAtLocation(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopupWindowFactory.makeWindowLight(ShowBigImageActivity.this);
            }
        });
        PopupWindowFactory.makeWindowDark(this);
    }*/
}
