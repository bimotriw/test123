package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;

import android.view.View;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.firebase.course.HotspotPointData;
import com.oustme.oustsdk.room.dto.DTOHotspotPointData;
import com.oustme.oustsdk.tools.OustPreferences;
import com.oustme.oustsdk.tools.OustSdkApplication;
import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.List;

/**
 * Created by admin on 10/08/17.
 */

public class CustomTouchIndicatorClass extends View {
    private Paint wrongPaint, rightPaint, blackPaint, whitePaint;
    private boolean[] touchPoint;
    DTOHotspotPointData[] hotspotPointDataList;
    private HotspotPointData wrongAnsHotspotData;
    private boolean startDraw = false;
    private Bitmap wrongImage, rightImage;
    private boolean isNew;
    private boolean isHotSpotThumbsUpShown;
    private boolean isHotSpotThumbsDownShown;

    private boolean isRect;
    private int x1, y1, x2, y2;
    private List<HotspotPointData> hotspotPointDataListForV2;
    private float ratioH, ratioW;

    public CustomTouchIndicatorClass(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        BitmapDrawable thumbsDown_bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getString(R.string.thumbsdownsmall));
        BitmapDrawable thumbsUp_bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getString(R.string.thumbsupsmall));
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            thumbsDown_bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getString(R.string.incorrect_point));
            thumbsUp_bd = OustSdkTools.getImageDrawable(OustSdkApplication.getContext().getString(R.string.correct_point));
        }
        if (thumbsDown_bd != null)
            wrongImage = thumbsDown_bd.getBitmap();
        if (thumbsUp_bd != null)
            rightImage = thumbsUp_bd.getBitmap();
    }

    public void setList(List<DTOHotspotPointData> hotspotPointDataList, boolean[] touchPoint) {
        this.hotspotPointDataList = new DTOHotspotPointData[hotspotPointDataList.size()];
        for (int i = 0; i < hotspotPointDataList.size(); i++) {
            this.hotspotPointDataList[i] = hotspotPointDataList.get(i);
        }
        this.touchPoint = touchPoint;
        invalidate();
    }

    public void rightPoint(DTOHotspotPointData hotspotPointData, int no, boolean[] touchPoint) {
        this.hotspotPointDataList[no] = (hotspotPointData);
        this.touchPoint = touchPoint;
        startDraw = true;
        invalidate();
    }

    public void wrongPoint(HotspotPointData hotspotPointData) {
        wrongAnsHotspotData = hotspotPointData;
        wrongCount = 25;
        startDraw = true;
        invalidate();
    }

    private boolean isAssessment = false;
    private int number;

    public void assessmentPoint(DTOHotspotPointData hotspotPointData, int no, boolean isAssessment, int number, boolean[] touchPoint) {
        try {
            this.hotspotPointDataList[no] = hotspotPointData;
            this.touchPoint = touchPoint;
            startDraw = true;
            this.isAssessment = isAssessment;
            this.number = number + 1;
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void drawRect(List<HotspotPointData> hotspotPointData, boolean isRect, float ratioW, float ratioH) {
        // ratioW = ratioW *.85f;
        // ratioH = ratioH *.85f;
        this.ratioH = ratioH;
        this.ratioW = ratioW;
        hotspotPointDataListForV2 = hotspotPointData;
        wrongCount = 25;
        startDraw = true;
        this.isRect = isRect;
        invalidate();
    }

    public void drawRect(int x1, int y1, int x2, int y2, boolean isNew) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.isNew = isNew;
        this.startDraw = true;
        invalidate();
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        rightPaint = new Paint();
        rightPaint.setAntiAlias(true);
        rightPaint.setStyle(Paint.Style.FILL);
        rightPaint.setStrokeJoin(Paint.Join.ROUND);
        rightPaint.setStrokeCap(Paint.Cap.ROUND);
//        rightPaint.setAlpha(60);
        rightPaint.setColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparente));
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            rightPaint.setColor(OustSdkTools.getColorBack(R.color.progress_correct));
        }

        wrongPaint = new Paint();
        wrongPaint.setAntiAlias(true);
        wrongPaint.setStyle(Paint.Style.FILL);
        wrongPaint.setStrokeJoin(Paint.Join.ROUND);
        wrongPaint.setStrokeCap(Paint.Cap.ROUND);
        wrongPaint.setColor(OustSdkTools.getColorBack(R.color.error_incorrect));

        blackPaint = new Paint();
        blackPaint.setAntiAlias(true);
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setStrokeJoin(Paint.Join.ROUND);
        blackPaint.setStrokeCap(Paint.Cap.ROUND);
        blackPaint.setColor(OustSdkTools.getColorBack(R.color.primary_text));
        blackPaint.setTextSize(getResources().getDimension(R.dimen.ousttext_dimen12));

        whitePaint = new Paint();
        whitePaint.setAntiAlias(true);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setStrokeJoin(Paint.Join.ROUND);
        whitePaint.setStrokeCap(Paint.Cap.ROUND);
        whitePaint.setColor(OustSdkTools.getColorBack(R.color.white));

    }

    public void drawRect(HotspotPointData hotspotPointData, int x1, int y1, int x2, int y2, boolean isRect) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        wrongAnsHotspotData = hotspotPointData;
        wrongCount = 25;
        startDraw = true;
        this.isRect = isRect;
        invalidate();
    }

    private boolean isreviewMode = false;

    public void setReviewmode() {
        isreviewMode = true;
        rightPaint.setColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparente));
        if (OustPreferences.getAppInstallVariable("isLayout4")) {
            rightPaint.setColor(OustSdkTools.getColorBack(R.color.progress_correct));
        }
        rightPaint.setAlpha(140);
    }

    private int wrongCount = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (startDraw) {
                if (isNew) {
                    Paint p = new Paint();
                    p.setAntiAlias(true);
                    p.setColor(Color.RED);
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(2f);
                    canvas.drawRect(x1, y1, x2, y2, p);
                    invalidate();
                } else if (isRect && wrongAnsHotspotData != null) {
                    Paint p = new Paint();
                    p.setAntiAlias(true);
                    p.setColor(Color.RED);
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(2f);
                    canvas.drawRect(x1, y1, x2, y2, p);
                    invalidate();
                } else if (isRect && hotspotPointDataListForV2 != null) {
                    for (int i = 0; i < hotspotPointDataListForV2.size(); i++) {
                        Paint p = new Paint();
                        p.setAntiAlias(true);
                        p.setColor(Color.RED);
                        p.setStyle(Paint.Style.STROKE);
                        p.setStrokeWidth(2f);
                        float x1 = (hotspotPointDataListForV2.get(i).getStartX() * ratioW);
                        float y1 = (hotspotPointDataListForV2.get(i).getStartY() * ratioH);
                        float x2 = ((hotspotPointDataListForV2.get(i).getStartX() + hotspotPointDataListForV2.get(i).getWidth()) * ratioW);
                        float y2 = ((hotspotPointDataListForV2.get(i).getStartY() + hotspotPointDataListForV2.get(i).getHeight()) * ratioH);
                        canvas.drawRect(x1, y1, x2, y2, p);
                    }
                    invalidate();
                } else if (wrongAnsHotspotData != null) {
                    if (wrongCount > 0) {
                        wrongPaint.setAlpha((wrongCount * 10));
                        if (isHotSpotThumbsDownShown) {
                            if (wrongImage != null) {
                                canvas.drawBitmap(wrongImage, wrongAnsHotspotData.getStartX() - (wrongImage.getWidth() / 2), wrongAnsHotspotData.getStartY() - (wrongImage.getHeight() / 2), wrongPaint);
                            } else {
                                canvas.drawCircle(wrongAnsHotspotData.getStartX(), wrongAnsHotspotData.getStartY(), 25, wrongPaint);
                            }
                        }
                        wrongCount--;
                        invalidate();
                    }
                }
                for (int i = 0; i < hotspotPointDataList.length; i++) {
                    if (touchPoint[i]) {
                        if (!isreviewMode && !isAssessment) {
                            canvas.drawBitmap(rightImage, hotspotPointDataList[i].getStartX() - (rightImage.getWidth() / 2), hotspotPointDataList[i].getStartY() - (rightImage.getWidth() / 2), rightPaint);
                        } else {
                            if (isAssessment) {
                                canvas.drawCircle(hotspotPointDataList[i].getStartX(), hotspotPointDataList[i].getStartY(), 25, whitePaint);
                                int num = i + 1;
                                canvas.drawText("" + num, hotspotPointDataList[i].getStartX() - 5, hotspotPointDataList[i].getStartY() + 10, blackPaint);
                            } else {
                                if (isHotSpotThumbsUpShown) {
                                    canvas.drawCircle(hotspotPointDataList[i].getStartX(), hotspotPointDataList[i].getStartY(), 25, rightPaint);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("CANVAS", "crash onDraw()", e);
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }

    public void setThumbShow(boolean isHotSpotThumbsUpShown, boolean isHotSpotThumbsDownShown) {
        this.isHotSpotThumbsUpShown = isHotSpotThumbsUpShown;
        this.isHotSpotThumbsDownShown = isHotSpotThumbsDownShown;
    }
}
