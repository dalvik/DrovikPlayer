package com.android.audiorecorder.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.audiorecorder.R;

import java.util.Calendar;

public class ImageClock extends LinearLayout {
    private static final String M12 = "h:mm";
    static final String M24 = "kk:mm";
    private static Typeface sTypeface;
    private boolean mAttached;
    private Calendar mCalendar;
    private String mClockFormatString;
    private ImageView mColon;
    private ImageView mColon1;
    private final int mColonImageID = 2130837536;
    private TextView mFillBlank;
    private String mFormat;
    private ImageView mHour_1;
    private ImageView mHour_10;
    private ImageView mMinute_1;
    private ImageView mMinute_10;
    private final int[] mNumberImageID_List;
    private ImageView mSecond_1;
    private ImageView mSecond_10;

    public ImageClock(Context paramContext) {
        this(paramContext, null);
    }

    public ImageClock(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        int[] arrayOfInt = { R.drawable.new_image_clock_num_0, R.drawable.new_image_clock_num_1,
                R.drawable.new_image_clock_num_2, R.drawable.new_image_clock_num_3,
                R.drawable.new_image_clock_num_4, R.drawable.new_image_clock_num_5, 
                R.drawable.new_image_clock_num_6, R.drawable.new_image_clock_num_7, 
                R.drawable.new_image_clock_num_8, R.drawable.new_image_clock_num_9 };
        this.mNumberImageID_List = arrayOfInt;
        init();
    }

    private void init() {
        View localView = LayoutInflater.from(getContext()).inflate(R.layout.imageclock, null);
        addView(localView);
        
        this.mHour_10 = (ImageView) findViewById(R.id.hour1);
        this.mHour_1 = (ImageView) findViewById(R.id.hour2);
        
        this.mColon = (ImageView) findViewById(R.id.middle);
        
        this.mMinute_10 = (ImageView) findViewById(R.id.minute1);
        
        this.mMinute_1 = (ImageView) findViewById(R.id.minute2);
        
        this.mColon1 = (ImageView) findViewById(R.id.middle1);
        
        this.mSecond_10 = (ImageView) findViewById(R.id.second1);
        
        this.mSecond_1 = (ImageView) findViewById(R.id.second2);
        
        int i = this.mNumberImageID_List[0];
        this.mHour_10.setImageResource(i);
        
        int j = this.mNumberImageID_List[0];
        this.mHour_1.setImageResource(j);
        
        this.mColon.setImageResource(R.drawable.new_image_clock_colon);
        
        int k = this.mNumberImageID_List[0];
        this.mMinute_10.setImageResource(k);
        
        int m = this.mNumberImageID_List[0];
        this.mMinute_1.setImageResource(m);
        
        this.mColon1.setImageResource(R.drawable.new_image_clock_colon);
        
        int n = this.mNumberImageID_List[0];
        this.mSecond_10.setImageResource(n);
        
        int i1 = this.mNumberImageID_List[0];
        this.mSecond_1.setImageResource(i1);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.mAttached)
            this.mAttached = true;
        setText(0L);
    }

    protected void onDetachedFromWindow()
    {
      super.onDetachedFromWindow();
      if (this.mAttached)
        this.mAttached = false;
    }
    
    public void setText(long paramLong) {
        int i = (int) (paramLong / 60L % 60L);
        int j = (int) paramLong % 60;
        int k = (int) (paramLong / 60L) / 60;
        
        ImageView localImageView1 = this.mHour_10;
        int[] arrayOfInt1 = this.mNumberImageID_List;
        int m = k / 10;
        int n = arrayOfInt1[m];
        localImageView1.setImageResource(n);
        
        ImageView localImageView2 = this.mHour_1;
        int[] arrayOfInt2 = this.mNumberImageID_List;
        int i1 = k % 10;
        int i2 = arrayOfInt2[i1];
        localImageView2.setImageResource(i2);
        
        ImageView localImageView3 = this.mMinute_10;
        int[] arrayOfInt3 = this.mNumberImageID_List;
        int i3 = i / 10;
        int i4 = arrayOfInt3[i3];
        localImageView3.setImageResource(i4);
        
        ImageView localImageView4 = this.mMinute_1;
        int[] arrayOfInt4 = this.mNumberImageID_List;
        int i5 = i % 10;
        int i6 = arrayOfInt4[i5];
        localImageView4.setImageResource(i6);
        
        ImageView localImageView5 = this.mSecond_10;
        int[] arrayOfInt5 = this.mNumberImageID_List;
        int i7 = j / 10;
        int i8 = arrayOfInt5[i7];
        localImageView5.setImageResource(i8);
        
        ImageView localImageView6 = this.mSecond_1;
        int[] arrayOfInt6 = this.mNumberImageID_List;
        int i9 = j % 10;
        int i10 = arrayOfInt6[i9];
        localImageView6.setImageResource(i10);
    }
}