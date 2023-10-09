package com.oustme.oustsdk.calendar_ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class OustCalendarVP extends ViewPager {

    private boolean mSwipeEnabled = true;

    public OustCalendarVP(Context context) {
        super(context);
    }

    public OustCalendarVP(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

            int h = child.getMeasuredHeight();

            if (h > height){
                height = h;
            }
        }

        if (height != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        this.mSwipeEnabled = swipeEnabled;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mSwipeEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mSwipeEnabled && super.onInterceptTouchEvent(event);
    }

}
