package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.PointF;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;

/**
 * Created by shilpysamaddar on 08/03/17.
 */

public class MyCustomLayoutManager extends LinearLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 50f;
    private Context mContext;
    private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
    private int extraLayoutSpace = -1;

    public MyCustomLayoutManager(Context context) {
        super(context);
        mContext = context;
    }
    public MyCustomLayoutManager(Context context, int extraLayoutSpace) {
        super(context);
        this.mContext = context;
        this.extraLayoutSpace = extraLayoutSpace;
    }

    public MyCustomLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.mContext = context;
    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        if (extraLayoutSpace > 0) {
            return extraLayoutSpace;
        }
        return DEFAULT_EXTRA_LAYOUT_SPACE;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {

        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {
            //This controls the direction in which smoothScroll looks
            //for your view
            @Override
            public PointF computeScrollVectorForPosition
            (int targetPosition) {
                return MyCustomLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            //This returns the milliseconds it takes to
            //scroll one pixel.
            @Override
            protected float calculateSpeedPerPixel
            (DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH/displayMetrics.densityDpi;
            }
        };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}

