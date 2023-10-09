package com.oustme.oustsdk.feed_ui.tools;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class ResizeTranslateAnimation extends Animation {
    final int startWidth;
    final int targetWidth;
    final int startHeight;
    final int targetHeight;
    final float startX;
    final float startY;
    final float targetX;
    final float targetY;

    View view;

    public ResizeTranslateAnimation(View view, int targetWidth, int targetHeight, float targetX, float targetY) {
        this.view = view;
        this.targetWidth = targetWidth;
        this.startWidth = view.getWidth();
        this.targetHeight = targetHeight;
        this.startHeight  = view.getHeight();
        startX = AnimationUtil.getX(view);
        startY = AnimationUtil.getY(view);
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        int newWidth  = (int) (startWidth  + (targetWidth - startWidth)  * interpolatedTime);
        int newHeight = (int) (startHeight + (targetHeight -startHeight) * interpolatedTime);

        float newX = (startX  + (targetX - startX) * interpolatedTime);
        float newY = (startY  + (targetY - startY) * interpolatedTime);

        //Set new pos
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
        params.setMargins((int) newX, (int) newY, 0, 0);

        //Set new size
        params.width  = newWidth;
        params.height = newHeight;

        view.setLayoutParams(params);

        //Update the layout
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}
