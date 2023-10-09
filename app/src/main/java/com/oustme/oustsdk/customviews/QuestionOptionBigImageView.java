package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.presenter.assessments.QuestionActivityPresenter;
import com.oustme.oustsdk.room.dto.DTOImageChoice;
import com.oustme.oustsdk.tools.OustAppState;
import com.oustme.oustsdk.tools.OustSdkTools;

/**
 * Created by shilpysamaddar on 16/03/17.
 */

public class QuestionOptionBigImageView extends View {
    private Bitmap mainBiutmap,checkedBitmap,expandBitmap;
    private boolean isMrqQuestion=false;
    private QuestionActivityPresenter presenter;
    private int size=0,size1=0,spaceStart=0;
    private boolean isCheckOption;
    private String bitmapStr,answerStr;
    private Paint drawPaint,backkPaint,checkPaint;
    private int  scrWidth,scrHeight;
    private int checkY=0;
    public static boolean clickedOnOption=false;
    public QuestionOptionBigImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPaint = new Paint();
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setColor(OustSdkTools.getColorBack(R.color.popupBackGroundd));

        backkPaint=new Paint();
        backkPaint.setStyle(Paint.Style.FILL);
        backkPaint.setColor(OustSdkTools.getColorBack(R.color.popupBackGroundb));

        checkPaint=new Paint();
        checkPaint.setStyle(Paint.Style.FILL);
        checkPaint.setColor(OustSdkTools.getColorBack(R.color.LiteGreen_transparentd));
    }
    public void setMainBiutmap(DTOImageChoice imageChoiceData, boolean isMrqQuestion, QuestionActivityPresenter presenter, int width){
        try {
            clickedOnOption=false;
            this.bitmapStr=imageChoiceData.getImageData();
            this.answerStr=imageChoiceData.getImageFileName();
            byte[] decodedString = Base64.decode(bitmapStr, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            this.isMrqQuestion=isMrqQuestion;
            this.presenter=presenter;

            size=(int)getResources().getDimension(R.dimen.oustlayout_dimen20);
            size1=(int)getResources().getDimension(R.dimen.oustlayout_dimen30);
            spaceStart=(int)getResources().getDimension(R.dimen.oustlayout_dimen5);
            this.scrWidth=((width)-size1);
            float f2=(float)(scrWidth*0.56);
            scrHeight=(int)f2;
            mainBiutmap = Bitmap.createScaledBitmap(decodedByte, scrWidth, scrHeight, false);

            scrHeight+=size1;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.getLayoutParams();
            params.height =(scrHeight);
            params.width=scrWidth;
            this.setLayoutParams(params);

            checkedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whitecheck);
            checkedBitmap = Bitmap.createScaledBitmap(checkedBitmap, size, size, false);
            expandBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.full);
            expandBitmap = Bitmap.createScaledBitmap(expandBitmap, size, size, false);
            invalidate();
        }catch (Exception e){}
    }
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            if (mainBiutmap != null) {
                canvas.drawBitmap(mainBiutmap, 0, 0, null);
                if(!isMrqQuestion){
                    if(isCheckOption){
                        canvas.drawRect(0,0, scrWidth, scrHeight, checkPaint);
                    }
                }
                canvas.drawRect(0,(scrHeight-size1), scrWidth, scrHeight, backkPaint);
                if(isMrqQuestion) {
                    if (isCheckOption) {
                        canvas.drawRect(0, 0, scrWidth, scrHeight, drawPaint);
                    }
                    canvas.drawBitmap(checkedBitmap,spaceStart,(scrHeight-(checkedBitmap.getHeight()+spaceStart)),null);
                }
                canvas.drawBitmap(expandBitmap,(scrWidth-(expandBitmap.getWidth()+spaceStart)),(scrHeight-(expandBitmap.getHeight()+spaceStart)),null);
            }
        }catch (Exception e){}
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            checkY=(int)event.getY();
            return true;
        }else if(event.getAction()==MotionEvent.ACTION_UP) {
            if (!clickedOnOption) {
                if ((checkY - ((int) event.getY()) > 15) || (((int) event.getY()) - checkY) > 15) {
                    return false;
                }
                int x = (int) event.getX();
                int y = (int) event.getY();
                int n1 = (int) ((float) (0.56 * scrHeight));
                int n2 = (int) ((float) 0.8 * scrWidth);
                if ((y > (n1)) && (x > (n2))) {
                    presenter.showBigQuestionImage(bitmapStr);
                } else {
                    if (isMrqQuestion) {
                        if (isCheckOption) {
                            isCheckOption = false;
                            checkedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whitecheck);
                            checkedBitmap = Bitmap.createScaledBitmap(checkedBitmap, size, size, false);
                            invalidate();
                        } else {
                            isCheckOption = true;
                            checkedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whiteunchecked);
                            checkedBitmap = Bitmap.createScaledBitmap(checkedBitmap, size, size, false);
                            invalidate();
                        }
                    } else {
                        if(OustAppState.getInstance().isAssessmentGame()) {
                            if (isCheckOption) {
                                isCheckOption = false;
                            } else {
                                isCheckOption = true;
                                presenter.clickOnBigImageOption();
                                invalidate();
                            }
                        }else {
                            presenter.calculateQuestionScoreForImage(answerStr);
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean isCheckOption() {
        return isCheckOption;
    }

    public void setCheckOption(boolean checkOption) {
        isCheckOption = checkOption;
        invalidate();
    }
}
