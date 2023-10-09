package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.TextView;

public class ParallogramTextView extends TextView {

     Paint mInnerPaint;

        public ParallogramTextView(Context context) {
            super(context);
            init();
        }

        public ParallogramTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        public ParallogramTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }


        private void init() {
            mInnerPaint = new Paint();
            mInnerPaint.setAntiAlias(true);
            mInnerPaint.setColor(Color.parseColor("#13a89e"));
            mInnerPaint.setStyle(Paint.Style.FILL);
            mInnerPaint.setStrokeJoin(Paint.Join.ROUND);
        }


        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            Path path = new Path();
            path.moveTo(getWidth(),0);
            path.lineTo(getWidth()/2, 0);
            path.lineTo(0, getHeight());
            path.lineTo(getWidth()/2,getHeight());
            path.lineTo(getWidth(), 0);
            canvas.drawPath(path, mInnerPaint);
        }

}
