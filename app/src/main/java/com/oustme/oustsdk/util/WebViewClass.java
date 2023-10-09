package com.oustme.oustsdk.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import androidx.annotation.NonNull;

public class WebViewClass extends WebView {
    public WebViewClass(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(false);
        setLongClickable(false);
        setFocusable(false);
        setFocusableInTouchMode(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }
}
