package com.oustme.oustsdk.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

/**
 * Created by admin on 14/10/17.
 */

public class CurvedRelativeLayout extends RelativeLayout {

    Context mContext;

    Path mClipPath;
    Path mOutlinePath;

    int width = 0;
    int height = 0;
    private int curveWidth=20;

    Paint mPaint;
    private PorterDuffXfermode porterDuffXfermode;
    private String TAG = "CRESCENTO_CONTAINER";

    public CurvedRelativeLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CurvedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

//        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
//
//        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setColor(Color.WHITE);
//
//        mClipPath = new Path();
//        mOutlinePath = new Path();
//
//
//        TypedArray styledAttributes = mContext.obtainStyledAttributes(attrs, R.styleable.CurveViewData, 0, 0);
//        if (styledAttributes.hasValue(R.styleable.CurveViewData_curveviewwidth)) {
//            curveWidth = (int) styledAttributes.getDimension(R.styleable.CurveViewData_curveviewwidth, getDpForPixel(curveWidth));
//        }
//        styledAttributes.recycle();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        width = getMeasuredWidth();
//        height = getMeasuredHeight();
//        mClipPath = getMyPath();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ViewCompat.setElevation(this, ViewCompat.getElevation(this));
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            try {
//                setOutlineProvider(getOutlineProvider());
//            } catch (Exception e) {
//                Log.d(TAG, e.getMessage());
//            }
//        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
//                try {
//                    outline.setConvexPath(getMyPath());
//                } catch (Exception e) {
//                    Log.d("Outline Path", e.getMessage());
//                }
            }
        };
    }

    private Path getMyPath(){
        Path mPath = new Path();
        mPath.moveTo(0,0);
        mPath.lineTo(curveWidth,0);
        mPath.quadTo(0,0,0,curveWidth);
        mPath.lineTo(0,0);
        mPath.moveTo(width-curveWidth,0);
        mPath.quadTo(width,0,width,curveWidth);
        mPath.lineTo(width,0);
        mPath.lineTo(width-curveWidth,0);

        mPath.moveTo(width,height-curveWidth);
        mPath.quadTo(width,height,width-curveWidth,height);
        mPath.lineTo(width,height);
        mPath.lineTo(width,height-curveWidth);

        mPath.moveTo(curveWidth,height);
        mPath.quadTo(0,height,0,height-curveWidth);
        mPath.lineTo(0,height);
        mPath.lineTo(curveWidth,height);
        return mPath;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
//        mPaint.setXfermode(porterDuffXfermode);
//        canvas.drawPath(mClipPath, mPaint);
//        canvas.restoreToCount(saveCount);
//        mPaint.setXfermode(null);
    }
    private int getDpForPixel (int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, mContext.getResources().getDisplayMetrics());
    }
}
