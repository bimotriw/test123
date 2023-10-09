package com.oustme.oustsdk.utils.floatingAnimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import android.view.ViewGroup;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.oustme.oustsdk.R;

import java.util.Random;

public class BouquetView extends RelativeLayout {

    private int[] drawables;
    private Context mContext;
    //The width and height of the picture
    private int mDrawableHeight;
    private int mDrawableWidth;

    //random number - need to discuss with durai bro
    private Random mRandom;

    public BouquetView(Context context) {
        this(context,null);
    }

    public BouquetView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BouquetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        //Create a random number, which will be used later in Bessel
        mRandom = new Random();
        //Initialize the heart-shaped picture
        drawables = new int[]{R.drawable.ic_favourite_heart_fill,R.drawable.ic_favourite_heart_fill,R.drawable.ic_favourite_heart_fill};

        //Get the width and height of the heart-shaped picture
        Drawable drawable = ContextCompat.getDrawable(context,drawables[0]);
        if (drawable != null) {
            mDrawableHeight = drawable.getIntrinsicHeight();
            mDrawableWidth = drawable.getIntrinsicWidth();
        }

    }

    //Add a heart-shaped picture at the bottom of the screen
    public void addDrawables(){
        final ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(drawables[mRandom.nextInt(drawables.length-1)]);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        addView(imageView);

        //Create and start animation effects
        AnimatorSet animatorSet = getAnimator(imageView);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //After the animation execution ends, remove the view
                removeView(imageView);
            }
        });
        animatorSet.start();
    }

    //Create animation

    private AnimatorSet getAnimator(ImageView imageView){
        //Zoom animation
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(imageView,"scaleX",0.3f,1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(imageView,"scaleY",0.3f,1f);
        //Gradient animation
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(imageView,"alpha",0.3f,1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleXAnimator,scaleYAnimator,alphaAnimator);
        set.setDuration(300);

        //Create a translational painting and add it to the set execution after execution
        AnimatorSet wholeAnimator = new AnimatorSet();
        //Execute in order
        wholeAnimator.playSequentially(set,getBeizerAnimator(imageView));
        return wholeAnimator;
    }

    private ValueAnimator getBeizerAnimator(final ImageView imageView){
        //First determine the four points that need to be used
        //Point 0 is the center point at the beginning of the picture
        final PointF point0 = new PointF(getWidth()/2 - mDrawableWidth/2,getHeight() - mDrawableHeight);
        //Point 1 Point 2 is the control point of the Bezier curve. What needs to be controlled is that the height of point 2 is greater than the height of point 1
        PointF point1 = getPoint(1);
        PointF point2 = getPoint(2);
        PointF point3 = new PointF(mRandom.nextInt(getWidth()/2) - mDrawableWidth/2,0);
        BeizerCurveEvalator beizerEvalator = new BeizerCurveEvalator(point1,point2);
        ValueAnimator beizerAnimator = ObjectAnimator.ofObject(beizerEvalator,point0,point3);
        beizerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        beizerAnimator.setDuration(4000);
        beizerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //This method will be called during operation
                PointF pointF = (PointF) animation.getAnimatedValue();
                imageView.setX(pointF.x);
                imageView.setY(pointF.y);

                //Set the gradient during the movement
                //First get the current fraction
                float fraction = animation.getAnimatedFraction();
                imageView.setAlpha(1-fraction);
            }
        });
        return beizerAnimator;
    }

    private PointF getPoint(int i){
        Log.e("BouquetView","i width "+i+" "+mRandom.nextInt(getWidth()));
        Log.e("BouquetView","i height "+i+" "+mRandom.nextInt(getHeight()/2));
        Log.e("BouquetView","i height width "+mDrawableWidth+" "+getHeight()/2);
        return new PointF(mRandom.nextInt(getWidth())- mDrawableWidth,mRandom.nextInt(getHeight()/2) + ((i-1)*getHeight()/2));
    }
}
