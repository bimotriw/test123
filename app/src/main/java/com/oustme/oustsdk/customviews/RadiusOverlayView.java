package com.oustme.oustsdk.customviews;


import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.oustme.oustsdk.R;

/**
 * Created by shilpysamaddar on 16/03/17.
 */

public class RadiusOverlayView extends RelativeLayout {
    private Bitmap windowFrame;

    public RadiusOverlayView(Context context) {
        super(context);
    }

    public RadiusOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadiusOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RadiusOverlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (windowFrame == null) {
            createWindowFrame();
            // Lazy creation of the window frame, this is needed as we don't know the width & height of the screen until draw time
        }
        canvas.drawBitmap(windowFrame, 0, 0, null);
    }

    protected void createWindowFrame() {
        windowFrame = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        // Create a new image we will draw over the map
        Canvas osCanvas = new Canvas(windowFrame);
        // Create a   canvas to draw onto the new image
        RectF outerRectangle = new RectF(0, 0, getWidth(), getHeight());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Anti alias allows for smooth corners
        paint.setColor(getResources().getColor(R.color.Gray));
        // This is the color of your activity background
//        paint.setAlpha(0);
        osCanvas.drawRect(outerRectangle, paint);

        paint.setColor(Color.TRANSPARENT);
        // An obvious color to help debugging
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)); // A out B http://en.wikipedia.org/wiki/File:Alpha_compositing.svg
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float radius = Math.min(getWidth(), getHeight()) / 2 - getResources().getDimensionPixelSize(R.dimen.oustlayout_dimen8);
        osCanvas.drawCircle(centerX, centerY, getResources().getDimensionPixelSize(R.dimen.oustlayout_dimen30), paint);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        windowFrame = null; // If the layout changes null our frame so it can be recreated with the new width and height
    }
}
