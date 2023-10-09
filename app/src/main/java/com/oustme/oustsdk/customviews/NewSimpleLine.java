package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by shilpysamaddar on 10/03/17.
 */

public class NewSimpleLine extends View {
    private final int paintColor = Color.WHITE;
    // defines paint and canvas
    private Paint drawPaint;
    private Path path = new Path();
    private int scrWidth;
    private int scrHeight;

    private float pathX;
    private float pathY;

    private boolean isMapEnd=false;

    private int noofLevels=0;
    private int totalHeight=0;

    public void setScreenWH(int scrW,  int scrH, NewSimpleLine simpleLine, int noofLevels){
        this.scrHeight=scrH;
        this.scrWidth=scrW;
        this.noofLevels=noofLevels;
        pathX=60;
        pathY=90;
        int scrollViewHeight=0;
        totalHeight = (75 * (noofLevels)) + 40;
        if(noofLevels<4) {
            scrollViewHeight = (((noofLevels)) * (75 * scrHeight / 480)) + (255 * scrHeight / 480);
        }else {
            scrollViewHeight = (((noofLevels)) * (80 * scrHeight / 480)) + (255 * scrHeight / 480);
        }
        if(scrollViewHeight<scrHeight){
            scrollViewHeight=scrHeight;
        }
        simpleLine.setScrollY(scrH - scrollViewHeight);
        invalidate();
    }

    public NewSimpleLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(4);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setColor(Color.WHITE);
        drawPaint.setShadowLayer(3, 0.6f, 0, Color.WHITE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(noofLevels==0){
            return;
        }else {
            if((pathY)<(totalHeight+90)) {
                pathY+=10;
                path = new Path();
                path.moveTo(getXPosition(60), getYPosition(90));
                path.lineTo(getXPosition(pathX), getYPosition(pathY));
                canvas.drawPath(path, drawPaint);
                invalidate();
            }else {
                canvas.drawPath(path, drawPaint);
            }
        }
    }

    private float getXPosition(float x1){
        float x=scrWidth*x1/320;
        return x;
    }

    private float getYPosition(float y1){
        float y=scrHeight-(scrHeight*y1/480);
        return y;
    }


}

