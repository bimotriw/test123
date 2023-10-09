package com.oustme.oustsdk.customviews;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.viewpager.widget.ViewPager;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.lang.reflect.Field;


/**
 * Created by shilpysamaddar on 07/03/17.
 */

public class CustomViewPager extends ViewPager {
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    private void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 500 /*1 secs*/);
        }
    }
}
