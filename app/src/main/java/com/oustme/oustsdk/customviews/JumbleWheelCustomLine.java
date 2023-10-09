package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.oustme.oustsdk.R;

/**
 * Created by shilpysamaddar on 16/11/17.
 */

public class JumbleWheelCustomLine extends View {


    private Paint paint;
    private Path linepath;
    private Path drawpath;

    public JumbleWheelCustomLine(Context context) {
        super(context);
        init();
    }

    public JumbleWheelCustomLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JumbleWheelCustomLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.White));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(12);
        paint.setAntiAlias(true);
    }

    public void setLinePath(Path linePath){
//        float radius = 50.0f;
//        CornerPathEffect cornerPathEffect = new CornerPathEffect(radius);
//        paint.setPathEffect(cornerPathEffect);
        this.linepath=linePath;
        invalidate();
    }

    public void setDrawpath(Path drawPath){
        this.drawpath=drawPath;
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(linepath!=null)
            canvas.drawPath(linepath, paint);
        if(drawpath!=null){
            canvas.drawPath(drawpath, paint);
        }
    }
}
