package com.oustme.oustsdk.categorySwipe;

/**
 * Created by shilpysamaddar on 28/06/17.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.course.FlingListener;
import com.oustme.oustsdk.question_module.fragment.CategoryQuestionFragment;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinosaurs might appear!
 */

public class SwipeFlingAdapterViewNew extends BaseFlingAdapterView {


    private int MAX_VISIBLE = 4;
    private int MIN_ADAPTER_STACK = 6;
    private float ROTATION_DEGREES = 15.f;

    private Adapter mAdapter;
    private int LAST_OBJECT_IN_STACK = 0;
    private onFlingListener mFlingListener;
    private AdapterDataSetObserver mDataSetObserver;
    private boolean mInLayout = false;
    private View mActiveCard = null;
    private OnItemClickListener mOnItemClickListener;
    private CategoryQuestionFragment.FlingCardListenerNew flingCardListener;
    private PointF mLastTouchPoint;

    private float CURRENT_TRANSY_VAL = 0;
    private float CURRENT_SCALE_VAL = 0;
    private static final double SCALE_OFFSET = 0.01;
    private static final float TRANS_OFFSET = 10;

    public SwipeFlingAdapterViewNew(Context context) {
        this(context, null);
    }

    public SwipeFlingAdapterViewNew(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SwipeFlingStyle2);
    }

    public SwipeFlingAdapterViewNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeFlingAdapterView, defStyle, 0);
        MAX_VISIBLE = a.getInt(R.styleable.SwipeFlingAdapterView_max_visible, MAX_VISIBLE);
        MIN_ADAPTER_STACK = a.getInt(R.styleable.SwipeFlingAdapterView_min_adapter_stack, MIN_ADAPTER_STACK);
        ROTATION_DEGREES = a.getFloat(R.styleable.SwipeFlingAdapterView_rotation_degrees, ROTATION_DEGREES);
        a.recycle();
    }


    /**
     * A shortcut method to set both the listeners and the adapter.
     *
     * @param context  The activity context which extends onFlingListener, OnItemClickListener or both
     * @param mAdapter The adapter you have to set.
     */
    public void init(final Context context, Adapter mAdapter) {
        if (context instanceof onFlingListener) {
            mFlingListener = (onFlingListener) context;
        } else {
            throw new RuntimeException("Activity does not implement SwipeFlingAdapterView.onFlingListener");
        }
        if (context instanceof OnItemClickListener) {
            mOnItemClickListener = (OnItemClickListener) context;
        }
        setAdapter(mAdapter);
    }

    @Override
    public View getSelectedView() {
        return mActiveCard;
    }


    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            return;
        }

        mInLayout = true;
        final int adapterCount = mAdapter.getCount();

        if (adapterCount == 0) {
            removeAllViewsInLayout();
        } else {
            View topCard = getChildAt(LAST_OBJECT_IN_STACK);
            if (mActiveCard != null && topCard != null && topCard == mActiveCard) {
                if (this.flingCardListener.isTouching()) {
                    PointF lastPoint = this.flingCardListener.getLastPoint();
                    if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {
                        this.mLastTouchPoint = lastPoint;
                        removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
                        layoutChildren(1, adapterCount);
                    }
                }
            } else {
                // Reset the UI and set top view listener
                removeAllViewsInLayout();
                layoutChildren(0, adapterCount);
                setTopView();
            }
        }

        mInLayout = false;

        if (adapterCount <= MIN_ADAPTER_STACK) mFlingListener.onAdapterAboutToEmpty(adapterCount);
    }


    private void layoutChildren(int startingIndex, int adapterCount) {
        resetOffsets();
        int viewStack = 0;

        while (startingIndex < Math.min(adapterCount, MAX_VISIBLE)) {
            View newUnderChild = mAdapter.getView(startingIndex, null, this);
            if (newUnderChild.getVisibility() != GONE) {
                makeAndAddView(newUnderChild, false);
                LAST_OBJECT_IN_STACK = startingIndex;
            }
            startingIndex++;
            viewStack++;

        }

        /**
         * This is to add a base view at end. To make an illusion that views come out from
         * a base card. The scale and translation of this view is same as the one previous to
         * this.
         */
        if (startingIndex >= adapterCount) {
            LAST_OBJECT_IN_STACK = --viewStack;
            return;
        }
        View newUnderChild = mAdapter.getView(startingIndex, null, this);
        if (newUnderChild != null && newUnderChild.getVisibility() != GONE) {
            makeAndAddView(newUnderChild, true);
            LAST_OBJECT_IN_STACK = viewStack;
        }
    }

    private void resetOffsets() {
        CURRENT_TRANSY_VAL = 0;
        CURRENT_SCALE_VAL = 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void makeAndAddView(View child, boolean isBase) {

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();

        if (isBase) {
            child.setScaleX((float) (child.getScaleX() - (CURRENT_SCALE_VAL - SCALE_OFFSET)));
            child.setScaleY((float) (child.getScaleY() - (CURRENT_SCALE_VAL - SCALE_OFFSET)));
            child.setX(child.getTranslationX() + CURRENT_TRANSY_VAL - TRANS_OFFSET);
            child.setY(child.getTranslationY() + CURRENT_TRANSY_VAL - TRANS_OFFSET);
        } else {
            child.setScaleX(child.getScaleX() - CURRENT_SCALE_VAL);
            child.setScaleY(child.getScaleY() - CURRENT_SCALE_VAL);
            child.setX(child.getTranslationX() + CURRENT_TRANSY_VAL);
            child.setY(child.getTranslationY() + CURRENT_TRANSY_VAL);
        }

        CURRENT_SCALE_VAL += SCALE_OFFSET;
        CURRENT_TRANSY_VAL += TRANS_OFFSET;

        addViewInLayout(child, 0, lp, true);

        final boolean needToMeasure = child.isLayoutRequested();
        if (needToMeasure) {
            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                    lp.height);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }


        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();

        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }


        int layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        int childLeft;
        int childTop;
        if ((absoluteGravity == Gravity.CENTER_HORIZONTAL) || (Gravity.CENTER_HORIZONTAL == Gravity.HORIZONTAL_GRAVITY_MASK)) {
            childLeft = (getWidth() + getPaddingLeft() - getPaddingRight() - w) / 2 +
                    lp.leftMargin - lp.rightMargin;
        } else if ((absoluteGravity == Gravity.END) || (Gravity.END == Gravity.HORIZONTAL_GRAVITY_MASK)) {
            childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
        } else if ((absoluteGravity == Gravity.START) || (Gravity.START == Gravity.HORIZONTAL_GRAVITY_MASK)) {
            childLeft = getPaddingLeft() + lp.leftMargin;
        } else {
            childLeft = getPaddingLeft() + lp.leftMargin;
        }

        if (verticalGravity == Gravity.CENTER_VERTICAL) {
            childTop = (getHeight() + getPaddingTop() - getPaddingBottom() - h) / 2 +
                    lp.topMargin - lp.bottomMargin;
        } else if (verticalGravity == Gravity.BOTTOM) {
            childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
        } else if (verticalGravity == Gravity.TOP) {
            childTop = getPaddingTop() + lp.topMargin;
        } else {
            childTop = getPaddingTop() + lp.topMargin;
        }

        child.layout(childLeft, childTop, childLeft + w, childTop + h);
    }


    /**
     * Set the top view and add the fling listener
     */
    private void setTopView() {
        if (getChildCount() > 0) {

            mActiveCard = getChildAt(LAST_OBJECT_IN_STACK);
            if (mActiveCard != null) {

                flingCardListener = new CategoryQuestionFragment.FlingCardListenerNew(mActiveCard, mAdapter.getItem(0),
                        ROTATION_DEGREES, new FlingListener() {

                    @Override
                    public void onCardExited() {
                        mActiveCard = null;
                        mFlingListener.removeFirstObjectInAdapter();
                    }

                    @Override
                    public void leftExit(Object dataObject) {
                        mFlingListener.onLeftCardExit(dataObject);
                    }

                    @Override
                    public void rightExit(Object dataObject) {
                        mFlingListener.onRightCardExit(dataObject);
                    }

                    @Override
                    public void onClick(Object dataObject) {
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClicked(0, dataObject);

                    }

                    @Override
                    public void onScroll(float scrollProgressPercent) {
                        mFlingListener.onScroll(scrollProgressPercent);
                    }

                });
                mActiveCard.setOnTouchListener(flingCardListener);
            }
        }
    }

    public CategoryQuestionFragment.FlingCardListenerNew getTopCardListener() throws NullPointerException {
        if (flingCardListener == null) {
            throw new NullPointerException();
        }
        return flingCardListener;
    }

    public void setMaxVisible(int MAX_VISIBLE) {
        this.MAX_VISIBLE = MAX_VISIBLE;
    }

    public void setMinStackInAdapter(int MIN_ADAPTER_STACK) {
        this.MIN_ADAPTER_STACK = MIN_ADAPTER_STACK;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }


    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }

        mAdapter = adapter;

        if (mAdapter != null && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    public void setFlingListener(onFlingListener onFlingListener) {
        this.mFlingListener = onFlingListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }


    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            requestLayout();
        }

    }


    public interface OnItemClickListener {
        void onItemClicked(int itemPosition, Object dataObject);
    }

    public interface onFlingListener {
        void removeFirstObjectInAdapter();

        void onLeftCardExit(Object dataObject);

        void onRightCardExit(Object dataObject);

        void onAdapterAboutToEmpty(int itemsInAdapter);

        void onScroll(float scrollProgressPercent);
    }


}