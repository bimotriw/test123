package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.CubicCurveSegment;

/**
 * Created by oust on 8/10/18.
 */

public class SimpleLineMaker extends View{
    private int[] Xs;
    private int[] Ys;
    private int color;
    private int scrHeight;
    Paint mStrokePaint = new Paint();
    Paint mFillPaint=new Paint();
    Paint dashPaint=new Paint();

    CubicCurveSegment[] cubicCurveSegments;
    public SimpleLineMaker(Context context,CubicCurveSegment[] cubicCurveSegments,int[] Xs,int[] Ys,int color,int scrHeight) {
        super(context);
        this.Xs=Xs;
        this.Ys=Ys;
        this.cubicCurveSegments=cubicCurveSegments;
        this.color=color;
        this.scrHeight=scrHeight;
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(color);
        mStrokePaint.setStrokeWidth(60);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);

        dashPaint.setStrokeWidth(8);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{50.0f, 25.0f}, 0));
        dashPaint.setColor(Color.WHITE);
        dashPaint.setStyle(Paint.Style.STROKE);

        mFillPaint.setStyle(Paint.Style.STROKE);
        mFillPaint.setColor(context.getResources().getColor(R.color.LiteGray));
        mFillPaint.setStrokeWidth(80);
        mFillPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path=getDrawPath();
        canvas.drawPath(path,mFillPaint);
        canvas.drawPath(path, mStrokePaint);
        canvas.drawPath(path,dashPaint);

    }

    private Path getDrawPath() {
//        path = new Path();
//        path.moveTo(Xs[0],Ys[0]);
//        for(int i=1;i<Xs.length-2;i++){
//            path.cubicTo(Xs[i],Ys[i],Xs[i+1],Ys[i+1],Xs[i+2],Ys[i+2]);
//            path.moveTo(Xs[i+2],Ys[i+2]);
//            i=i+2;
//        }
//        return path;
        Path path = new Path();
        path.moveTo(Xs[0],Ys[0]);
        for(int i=0;i<Xs.length-1;i++){
            path.cubicTo(cubicCurveSegments[i].getControlPoint1().x,cubicCurveSegments[i].getControlPoint1().y,
                    cubicCurveSegments[i].getControlPoint2().x,cubicCurveSegments[i].getControlPoint2().y,Xs[i+1],Ys[i+1]);
            path.moveTo(Xs[i+1],Ys[i+1]);
        }
        return path;
    }

}
