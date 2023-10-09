package com.oustme.oustsdk.customviews.customSeekBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.oustme.oustsdk.R;

public class IndicatorSeekBar extends AppCompatSeekBar {

    // brush
    private Paint mPaint;
    // Progress text location information
    private Rect mProgressTextRect = new Rect();
    // slider button width
    private int mThumbWidth = dp2px(50);
    // progress indicator width
    private int mIndicatorWidth = dp2px(50);
    // progress monitor
    private OnIndicatorSeekBarChangeListener mIndicatorSeekBarChangeListener;

    public IndicatorSeekBar(Context context) {
        this(context, null);
    }

    public IndicatorSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public IndicatorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#00574B"));
        mPaint.setTextSize(sp2px(16));

        // If you don't set padding, when you slide to the left or right, the slider will show incomplete.
        setPadding(mThumbWidth / 2, 0, mThumbWidth / 2, 0);

        this.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // NO OP
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mIndicatorSeekBarChangeListener != null) {
                    mIndicatorSeekBarChangeListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mIndicatorSeekBarChangeListener != null) {
                    mIndicatorSeekBarChangeListener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String progressText = getProgress() + "%";
        mPaint.getTextBounds(progressText, 0, progressText.length(), mProgressTextRect);

        // progress percentage
        float progressRatio = (float) getProgress() / getMax();
        // thumb offset
        float thumbOffset = (mThumbWidth - mProgressTextRect.width()) / 2 - mThumbWidth * progressRatio;
        float thumbX = getWidth() * progressRatio + thumbOffset;
        float thumbY = getHeight() / 2f + mProgressTextRect.height() / 2f;
        canvas.drawText(progressText, thumbX, thumbY, mPaint);

        if (mIndicatorSeekBarChangeListener != null) {
            float indicatorOffset = getWidth() * progressRatio - (mIndicatorWidth - mThumbWidth) / 2 - mThumbWidth * progressRatio;
            mIndicatorSeekBarChangeListener.onProgressChanged(this, getProgress(), indicatorOffset);
        }
    }


    public void setOnSeekBarChangeListener(OnIndicatorSeekBarChangeListener listener) {
        this.mIndicatorSeekBarChangeListener = listener;
    }

    public interface OnIndicatorSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, float indicatorOffset);


        public void onStartTrackingTouch(SeekBar seekBar);


        public void onStopTrackingTouch(SeekBar seekBar);
    }


    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
}

