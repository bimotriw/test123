package com.oustme.oustsdk.categorySwipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.interfaces.course.FlingListener;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustCommonUtils;
import com.oustme.oustsdk.tools.OustSdkTools;


public class FlingCardListener implements View.OnTouchListener {

    private final int INVALID_POINTER_ID = -1;

    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final int parentWidth;
    private final FlingListener mFlingListener;

    private final Object dataObject;
    private final float halfWidth;
    private float BASE_ROTATION_DEGREES;

    private float aPosX;
    private float aPosY;
    private float aDownTouchX;
    private float aDownTouchY;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;


    private final int TOUCH_ABOVE = 0;
    private final int TOUCH_BELOW = 1;
    private int touchPosition;
    private final Object obj = new Object();
    private boolean isAnimationRunning = false;
    private float MAX_COS = (float) Math.cos(Math.toRadians(45));


//    public FlingCardListener(View frame, Object itemAtPosition, FlingListener flingListener) {
//        this(frame, itemAtPosition, 15f, flingListener);
//    }

    public FlingCardListener(View frame, Object itemAtPosition, float rotation_degrees, FlingListener flingListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.BASE_ROTATION_DEGREES = rotation_degrees;
        this.mFlingListener = flingListener;
    }

    private boolean isCardMoving = false;

    public boolean onTouch(View view, MotionEvent event) {
        try {
            int action = event.getAction();
            if ((action == MotionEvent.ACTION_DOWN) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN)) {
                // Save the ID of this pointer
                isCardMoving = true;
                mActivePointerId = event.getPointerId(0);
                float x = 0;
                float y = 0;
                boolean success = false;
                try {
                    x = event.getX(mActivePointerId);
                    y = event.getY(mActivePointerId);
                    success = true;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
            OustSdkTools.sendSentryException(e);
                }
                if (success) {
                    // Remember where we started
                    aDownTouchX = x;
                    aDownTouchY = y;
                    //to prevent an initial jump of the magnifier, aposX and aPosY must
                    //have the values from the magnifier frame
                    if (aPosX == 0) {
                        aPosX = frame.getX();
                    }
                    if (aPosY == 0) {
                        aPosY = frame.getY();
                    }

                    if (y < objectH / 2) {
                        touchPosition = TOUCH_ABOVE;
                    } else {
                        touchPosition = TOUCH_BELOW;
                    }
                }

                view.getParent().requestDisallowInterceptTouchEvent(true);
            } else if ((action == MotionEvent.ACTION_UP) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP)) {
                mActivePointerId = INVALID_POINTER_ID;
                isCardMoving = false;
                resetCardViewOnStack();
                view.getParent().requestDisallowInterceptTouchEvent(false);
            } else if ((action == MotionEvent.ACTION_POINTER_DOWN) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_POINTER_DOWN)) {
            } else if ((action == MotionEvent.ACTION_POINTER_UP) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_POINTER_UP)) {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
            } else if ((action == MotionEvent.ACTION_MOVE) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_MOVE)) {

                // Find the index of the active pointer and fetch its position
                final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                final float xMove = event.getX(pointerIndexMove);
                final float yMove = event.getY(pointerIndexMove);

                //from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Calculate the distance moved
                final float dx = xMove - aDownTouchX;
                final float dy = yMove - aDownTouchY;


                // Move the frame
                aPosX += dx;
                aPosY += dy;

                // calculate the rotation degrees
                float distobjectX = aPosX - objectX;
                float rotation = BASE_ROTATION_DEGREES * 2.f * distobjectX / parentWidth;
                if (touchPosition == TOUCH_BELOW) {
                    rotation = -rotation;
                }

                //in this area would be code for doing something with the view as the frame moves.
                frame.setX(aPosX);
                frame.setY(aPosY);
                frame.setRotation(rotation);
                mFlingListener.onScroll(getScrollProgressPercent());
            } else if ((action == MotionEvent.ACTION_CANCEL) || (MotionEvent.ACTION_MASK == MotionEvent.ACTION_CANCEL)) {
                mActivePointerId = INVALID_POINTER_ID;
                view.getParent().requestDisallowInterceptTouchEvent(false);
                isCardMoving = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }

        return true;
    }

    private float getScrollProgressPercent() {
        if (movedBeyondLeftBorder()) {
            return -1f;
        } else if (movedBeyondRightBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private boolean resetCardViewOnStack() {
            if (movedBeyondLeftBorder() || movedBeyondRightBorder()) {
                Log.e("Category", "resetCardViewOnStack() left or right swipe");
                OustSdkTools.totalAttempt++;
            }
            if (movedBeyondLeftBorder()) {
                // Left Swipe
                if (OustSdkTools.isAssessmentQuestion) {
                    onSelected(true, getExitPoint(-objectW), 100);
                    mFlingListener.onScroll(-1.0f);
                    if((OustSdkTools.categoryData!=null &&  OustSdkTools.categoryData.size()>0) && (OustSdkTools.optionData!=null && OustSdkTools.optionData.size()>0 )) {
                        OustSdkTools.optionSelected++;
                        if (OustSdkTools.categoryData.get(0).getCode().equalsIgnoreCase(OustSdkTools.optionData.get(0).getOptionCategory())) {
                            OustSdkTools.totalCategoryRight++;
                        }
                    }
                } else {
                    if((OustSdkTools.categoryData!=null &&  OustSdkTools.categoryData.size()>0) && (OustSdkTools.optionData!=null && OustSdkTools.optionData.size()>0) ) {
                        if (OustSdkTools.categoryData.get(0).getCode().equalsIgnoreCase(OustSdkTools.optionData.get(0).getOptionCategory())) {
                            onSelected(true, getExitPoint(-objectW), 100);
                            mFlingListener.onScroll(-1.0f);
                            OustSdkTools.optionSelected++;
                        } else {
                            wrongAnswer();
                        }
                    }
                }

            } else if (movedBeyondRightBorder()) {
                // Right Swipe
                if (OustSdkTools.isAssessmentQuestion) {
                    onSelected(false, getExitPoint(parentWidth), 100);
                    mFlingListener.onScroll(1.0f);
                    if((OustSdkTools.categoryData!=null &&  OustSdkTools.categoryData.size()>0) && (OustSdkTools.optionData!=null && OustSdkTools.optionData.size()>0 )) {
                        OustSdkTools.optionSelected++;
                        if (OustSdkTools.categoryData.get(1).getCode().equalsIgnoreCase(OustSdkTools.optionData.get(0).getOptionCategory())) {
                            OustSdkTools.totalCategoryRight++;
                        }
                    }
                } else {
                    if((OustSdkTools.categoryData!=null &&  OustSdkTools.categoryData.size()>0) && (OustSdkTools.optionData!=null && OustSdkTools.optionData.size()>0 )) {
                        if (OustSdkTools.categoryData.get(1).getCode().equalsIgnoreCase(OustSdkTools.optionData.get(0).getOptionCategory())) {
                            onSelected(false, getExitPoint(parentWidth), 100);
                            mFlingListener.onScroll(1.0f);
                            OustSdkTools.optionSelected++;
                        } else {
                            wrongAnswer(); // praveen one
                        }
                    }
                }

            } else {
                wrongAnswer();// card released any where except left or right
            }
        return false;
    }
    private void wrongAnswer(){
        wrongAnswerSound();
        vibrateandShake(frame);
        Log.e("Category", "resetCardViewOnStack() wrong attempt");
        float abslMoveDistance = Math.abs(aPosX - objectX);
        aPosX = 0;
        aPosY = 0;
        aDownTouchX = 0;
        aDownTouchY = 0;
        frame.animate()
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .x(objectX)
                .y(objectY)
                .rotation(0);
        mFlingListener.onScroll(0.0f);
        if (abslMoveDistance < 4.0) {
            mFlingListener.onClick(dataObject);
        }
    }

    private boolean movedBeyondLeftBorder() {
        return aPosX + halfWidth < leftBorder();
    }

    private boolean movedBeyondRightBorder() {
        return aPosX + halfWidth > rightBorder();
    }


    public float leftBorder() {
        return parentWidth / 4.f;
    }

    public float rightBorder() {
        return 3 * parentWidth / 4.f;
    }


    public void onSelected(final boolean isLeft, float exitY, long duration) {
        isAnimationRunning = true;
        float exitX;
        if (isLeft) {
            exitX = -objectW - getRotationWidthOffset();
        } else {
            exitX = parentWidth + getRotationWidthOffset();
        }
        this.frame.animate()
                .setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .x(exitX)
                .y(exitY)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isLeft) {
                            Log.e("Category", "onSelected() isLeft : " + "true");
                            mFlingListener.onCardExited();
                            mFlingListener.leftExit(dataObject);
                        } else {
                            Log.e("Category", "onSelected() isLeft : " + "false");
                            mFlingListener.onCardExited();
                            mFlingListener.rightExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }
                })
                .rotation(getExitRotation(isLeft));
    }


    /**
     * Starts a default left exit animation.
     */
    public void selectLeft() {
        if (!isAnimationRunning)
            onSelected(true, objectY, 200);
    }

    /**
     * Starts a default right exit animation.
     */
    public void selectRight() {
        if (!isAnimationRunning)
            onSelected(false, objectY, 200);
    }


    private float getExitPoint(int exitXPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression = new LinearRegression(x, y);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitXPoint + (float) regression.intercept();
    }

    private float getExitRotation(boolean isLeft) {
        float rotation = BASE_ROTATION_DEGREES * 2.f * (parentWidth - objectX) / parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isLeft) {
            rotation = -rotation;
        }
        return rotation;
    }


    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     * <p/>
     * The below method calculates the width offset of the rotation.
     */
    private float getRotationWidthOffset() {
        return objectW / MAX_COS - objectW;
    }


    public void setRotationDegrees(float degrees) {
        this.BASE_ROTATION_DEGREES = degrees;
    }

    public boolean isTouching() {
        return this.mActivePointerId != INVALID_POINTER_ID;
    }

    public PointF getLastPoint() {
        return new PointF(this.aPosX, this.aPosY);
    }

    private void wrongAnswerSound() {
        try {
            MediaPlayer mediaPlayer1 = MediaPlayer.create(OustSdkApplication.getContext(), Uri.fromFile(OustCommonUtils.getAudioFile("answer_incorrect")));
            mediaPlayer1.start();
        } catch (Exception e) {
        }
    }

    public void vibrateandShake(View v) {
        try {
            Animation shakeAnim = AnimationUtils.loadAnimation(OustSdkApplication.getContext(), R.anim.shakescreen_anim);
            v.startAnimation(shakeAnim);
            ((Vibrator) OustSdkApplication.getContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
        } catch (Exception e) {
            //OustFlurryTools.LogFlurryErrorEvent(this.getClass().getName(), new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName(), e);
        }
    }

}