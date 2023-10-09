package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by shilpysamaddar on 15/11/17.
 */

public class JumbleWordCustomCircle extends View {
    public JumbleWordCustomCircle(Context context) {
        super(context);
    }

    public JumbleWordCustomCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JumbleWordCustomCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float width,height;
    private int radius;
    private Path path;
    private Paint paint;

    public void setPath(Path path) {
        paint= new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        this.path = path;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(path!=null){
            canvas.drawPath(path, paint);
        }
    }

}
