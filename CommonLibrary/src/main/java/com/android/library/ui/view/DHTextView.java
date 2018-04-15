package com.android.library.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class DHTextView extends TextView {

    public DHTextView(Context context) {
        super(context);
    }
    
    public DHTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public DHTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
    

}
