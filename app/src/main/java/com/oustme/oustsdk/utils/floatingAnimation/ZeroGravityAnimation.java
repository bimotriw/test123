package com.oustme.oustsdk.utils.floatingAnimation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.oustme.oustsdk.R;

public class ZeroGravityAnimation  {

    private static final int RANDOM_DURATION = -1;

    private Direction mOriginationDirection = Direction.RANDOM;
    private Direction mDestinationDirection = Direction.RANDOM;
    private int mDuration = RANDOM_DURATION;
    private int mCount = 1;
    private Drawable mImageResId;
    private float mScalingFactor = 1f;
    private Animation.AnimationListener mAnimationListener;

    public ZeroGravityAnimation setOriginationDirection(Direction direction) {
        this.mOriginationDirection = direction;
        return this;
    }

    public ZeroGravityAnimation setDestinationDirection(Direction direction) {
        this.mDestinationDirection = direction;
        return this;
    }

    public ZeroGravityAnimation setRandomDuration() {
        return setDuration(RANDOM_DURATION);
    }

    public ZeroGravityAnimation setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    public ZeroGravityAnimation setImage(Drawable resId) {
        this.mImageResId = resId;
        return this;
    }

    public ZeroGravityAnimation setScalingFactor(float scale) {
        this.mScalingFactor = scale;
        return this;
    }

    public ZeroGravityAnimation setAnimationListener(Animation.AnimationListener listener) {
        this.mAnimationListener = listener;
        return this;
    }

    public ZeroGravityAnimation setCount(int count) {
        this.mCount = count;
        return this;
    }

    public void play(Activity activity, ViewGroup ottParent) {

        DirectionGenerator generator = new DirectionGenerator();

        if(mCount > 0) {

            for (int i = 0; i < mCount; i++) {


                final int iDupe = i;

                Direction origin = mOriginationDirection == Direction.RANDOM ? generator.getRandomDirection() : mOriginationDirection;
                Direction destination = mDestinationDirection == Direction.RANDOM ? generator.getRandomDirection(origin) : mDestinationDirection;

                int startingPoints[] = generator.getPointsInDirection(activity, origin);
                int endPoints[] = generator.getPointsInDirection(activity,destination);


                Bitmap bitmap = drawableToBitmap(mImageResId);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * mScalingFactor), (int) (bitmap.getHeight() * mScalingFactor), false);

                switch (origin) {
                    case LEFT:
                        startingPoints[0] -= scaledBitmap.getWidth();
                        break;

                    case RIGHT:
                        startingPoints[0] += scaledBitmap.getWidth();
                        break;

                    case TOP:
                        startingPoints[1] -= scaledBitmap.getHeight();
                        break;
                    case BOTTOM:
                        startingPoints[1] += scaledBitmap.getHeight();
                        break;
                }

                switch (destination) {
                    case LEFT:
                        endPoints[0] -= scaledBitmap.getWidth();
                        break;

                    case RIGHT:
                        endPoints[0] += scaledBitmap.getWidth();
                        break;

                    case TOP:
                        endPoints[1] -= scaledBitmap.getHeight();
                        break;
                    case BOTTOM:
                        endPoints[1] += scaledBitmap.getHeight();
                        break;
                }


                final OverTheTopLayer layer = new OverTheTopLayer();

                FrameLayout ottLayout = layer.with(activity)
                        .scale(mScalingFactor)
                        .attachTo(ottParent)
                        .setBitmap(scaledBitmap, startingPoints)
                        .create();


                switch (origin) {
                    case LEFT:

                }

                int deltaX = endPoints[0]  - startingPoints[0];
                int deltaY = endPoints[1] - startingPoints[1];

                int duration = mDuration;
                if (duration == RANDOM_DURATION) {
                    duration = RandomUtil.generateRandomBetween(3500, 12500);
                }

                TranslateAnimation animation = new TranslateAnimation(0, deltaX, 0, deltaY);
                animation.setDuration(duration);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                        if (iDupe == 0) {
                            if (mAnimationListener != null) {
                                mAnimationListener.onAnimationStart(animation);
                            }
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        layer.destroy();

                        if (iDupe == (mCount - 1)) {
                            if (mAnimationListener != null) {
                                mAnimationListener.onAnimationEnd(animation);
                            }
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                layer.applyAnimation(animation);
            }
        }
        else  {

            Log.e(ZeroGravityAnimation.class.getSimpleName(),"Count was not provided, animation was not started");
        }
    }

    public void play(Activity activity) {

        play(activity,null);

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
