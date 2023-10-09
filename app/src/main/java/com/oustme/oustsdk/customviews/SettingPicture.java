package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkTools;

/**
 * Created by shilpysamaddar on 22/03/17.
 */

public class SettingPicture extends View {

    private Paint drawPaint;
    private Paint backPaint;
    private Paint liteBlackPaint;
    private int scrWidth;
    private int scrHeight;
    private int imageHeight;
    private int levelboxSize;

    private Bitmap tempBitmap;
    private Bitmap boxBitmap;
    private Bitmap mainBoxBitmap;
    public boolean showPicture=false;
    public int boxStartX=0;
    public int boxStartY=0;
    public int boxLength=0;
    private int imageStartY=0;

    private int touchX=0;
    private int touchY=0;

    private int startMoving=0;
    private int imgMinSize=0;



    public void setScreenWH(int scrW,int scrH, Bitmap bitmap){
        showPicture=true;
        levelboxSize = (int) (getResources().getDimension(R.dimen.oustlayout_dimen50));
        this.scrWidth=scrW;
        this.scrHeight=(scrH-levelboxSize);
        this.boxLength=(scrW/2);
        int n1=bitmap.getWidth();
        int n2=bitmap.getHeight();
        float f1=((float)n2)/((float)n1);
        imageHeight=(int)(f1*scrW);
        imageStartY = (scrHeight - imageHeight) / 2;
        tempBitmap = Bitmap.createScaledBitmap(bitmap, scrWidth, imageHeight, false);
        if(imageHeight>scrHeight){
            imageHeight=scrHeight;
            tempBitmap=Bitmap.createBitmap(tempBitmap,0,0,tempBitmap.getWidth(),scrHeight);
        }
        if(imageHeight>scrW){
            imgMinSize=scrW;
        }else {
            imgMinSize=imageHeight;
        }
        getBoxStartPosition();
        invalidate();
    }

    public void getBoxStartPosition(){
        boxLength=imgMinSize-20;
        boxStartX=(scrWidth/2)-(boxLength/2);
        boxStartY=(scrHeight/2)-(boxLength/2);
    }

    public Bitmap getFinalBitmap(){
        showPicture=false;
        if((boxStartX+boxLength)>scrWidth){
            boxStartX=scrWidth-boxLength-1;
        }
        if(((boxStartY-imageStartY)+boxLength)>imageHeight){
            boxStartY=imageHeight-boxLength+imageStartY-1;
        }
        if(boxStartX<0){
            boxStartX=0;
        }
        if(boxStartY<imageStartY){
            boxStartY=imageStartY;
        }
        Bitmap bma=Bitmap.createBitmap(tempBitmap,boxStartX,(boxStartY-imageStartY),boxLength,boxLength);
        return bma;
    }

    public SettingPicture(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        mainBoxBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.profilesettingbox);
    }



    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(6);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setColor(Color.WHITE);

        backPaint=new Paint();
        backPaint.setColor(Color.BLACK);

        liteBlackPaint=new Paint();
        liteBlackPaint.setColor(OustSdkTools.getColorBack(R.color.popupBackGroundb));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(showPicture){
            canvas.drawColor(Color.BLACK);
            if(boxLength<80) {
                boxLength=80;
            }
            boxBitmap = Bitmap.createScaledBitmap(mainBoxBitmap, boxLength, boxLength, false);
            canvas.drawBitmap(tempBitmap, 0, ((scrHeight / 2) - (imageHeight / 2)), null);
            final RectF arrowOval = new RectF();
            arrowOval.set(0, 0, boxStartX, scrHeight);
            canvas.drawRect(arrowOval, liteBlackPaint);
            arrowOval.set(boxStartX + boxLength, 0, scrWidth, scrHeight);
            canvas.drawRect(arrowOval, liteBlackPaint);
            arrowOval.set(boxStartX, 0, boxStartX + boxLength, boxStartY);
            canvas.drawRect(arrowOval, liteBlackPaint);
            arrowOval.set(boxStartX, boxStartY + boxLength, boxStartX + boxLength, scrHeight);
            canvas.drawRect(arrowOval, liteBlackPaint);
            canvas.drawBitmap(boxBitmap, boxStartX, boxStartY, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchX=(int)event.getX();
            touchY=(int)event.getY();
            if((touchX>(boxStartX+30))&&(touchX<(boxStartX+boxLength-30))&&(touchY>(boxStartY+30))&&(touchY<(boxStartY+boxLength-30))){
                startMoving = 2;
            }
            else if(((touchX<(boxStartX+50))&&(touchX>(boxStartX-50)))&&(touchY<(boxStartY+50))&&(touchY>(boxStartY-50))){
                startMoving=7;
            }
            else if(((touchX<(boxStartX+boxLength+50))&&(touchX>(boxStartX+boxLength-50)))&&(touchY<(boxStartY+boxLength+50))&&(touchY>(boxStartY+boxLength-50))){
                startMoving=8;
            }
            else if(((touchX<(boxStartX+30))&&(touchX>(boxStartX-30)))&&(touchY>(boxStartY+50))&&(touchY<(boxStartY+boxLength-50))){
                startMoving=3;
            }
            else if(((touchX<(boxStartX+boxLength+30))&&(touchX>(boxStartX+boxLength-30)))&&(touchY>(boxStartY+50))&&(touchY<(boxStartY+boxLength-50))){
                startMoving=4;
            }
            else if((touchX>(boxStartX+50))&&(touchX<(boxStartX+boxLength-50))&&(touchY>(boxStartY-30))&&(touchY<(boxStartY+30))){
                startMoving=5;
            }
            else if((touchX>(boxStartX+50))&&(touchX<(boxStartX+boxLength-50))&&(touchY>(boxStartY+boxLength-30))&&(touchY<(boxStartY+boxLength+30))){
                startMoving=6;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if(startMoving==2) {
                int diffX=(int) (event.getX()-touchX);
                int diifY=(int) (event.getY()-touchY);
                if((((boxStartX+boxLength+diffX)<scrWidth)&&((boxStartX+diffX)>0))) {
                    boxStartX += diffX;
                    touchX = (int) event.getX();
                }else {
                    if((boxStartX+boxLength+diffX)>=scrWidth){
                        boxStartX=(scrWidth-boxLength);
                    }else if((boxStartX+diffX)<=0){
                        boxStartX=0;
                    }
                }
                if(((boxStartY+boxLength+diifY)<=(imageHeight+imageStartY))&&((boxStartY+diifY)>imageStartY)){
                    boxStartY+= diifY;
                    touchY=(int)event.getY();
                }else {
                    if((boxStartY+boxLength+diifY)>=(imageHeight+imageStartY)){
                        boxStartY=((imageHeight+imageStartY)-boxLength);
                    }else if((boxStartY+diifY)>imageStartY){
                        boxStartY=imageStartY;
                    }
                }
            }else if(startMoving==3){
                int diffX=(int)((event.getX()-touchX));
                if(diffX>0){
                    boxLength-=diffX;
                    boxStartX+=(diffX);
                    touchX = (int) event.getX();
                }else if(diffX<0){
                    if(((boxLength-diffX)<imgMinSize)){
                        if(((boxStartY-imageStartY)+boxLength-diffX)>imageHeight){
                            boxStartY+=diffX;
                        }
                        boxLength -=diffX;
                        boxStartX+=(diffX);
                        touchX = (int) event.getX();
                    }
                }
            }
            else if(startMoving==4){
                int diffX=(int)((event.getX()-touchX));
                if(diffX>0){
                    if(((boxLength+diffX)<imgMinSize)){
                        if(((boxStartY-imageStartY)+boxLength+diffX)>imageHeight){
                            boxStartY-=diffX;
                        }
                        boxLength+=diffX;
                        touchX = (int) event.getX();
                    }
                }else if(diffX<0){
                    if(((boxLength+diffX)<imgMinSize)){
                        boxLength +=diffX;
                        touchX = (int) event.getX();
                    }
                }
            }
            else if(startMoving==5){
                int diffX=(int)((event.getY()-touchY));
                if(diffX>0){
                    if(((boxLength-diffX)<imgMinSize)){
                        boxLength-=diffX;
                        boxStartY+=diffX;
                        touchY = (int) event.getY();
                    }
                }else if(diffX<0){
                    if(((boxLength-diffX)<imgMinSize)){
                        if((boxStartY-diffX)>imageStartY){
                            boxStartY+=diffX;
                        }
                        if((boxStartX+boxLength-diffX)>scrWidth){
                            boxStartX+=diffX;
                        }
                        boxLength -=diffX;
                        touchY = (int) event.getY();
                    }
                }
            }else if(startMoving==6){
                int diffX=(int)((event.getY()-touchY));
                if(diffX>0){
                    if(((boxLength+diffX)<imgMinSize)){
                        if((boxStartY+diffX+boxLength)>=(imageStartY+imageHeight)){
                            boxStartY-=diffX;
                        }
                        if((boxStartX+boxLength+diffX)>scrWidth){
                            boxStartX-=diffX;
                        }
                        boxLength+=diffX;
                        touchY = (int) event.getY();
                    }
                }else if(diffX<0){
                    if(((boxLength+diffX)<imgMinSize)){
                        boxLength +=diffX;
                        touchY = (int) event.getY();
                    }
                }
            }
            else if(startMoving==7){
                int diffX=(int)((event.getX()-touchX));
                int diffY=(int)((event.getY()-touchY));
                if((diffX>0)&&(diffY>0)){
                    if(((boxLength-diffX)<imgMinSize)&&((boxLength-diffY)<imgMinSize)){
                        boxStartX+=diffX;
                        if(((boxStartY+boxLength+diffY)<=(imageHeight+imageStartY))){
                            boxStartY+=diffY;
                        }
                        boxLength-=diffX;
                        touchY = (int) event.getY();
                        touchX=(int) event.getX();
                    }
                }else if((diffX<0)&&(diffY<0)){
                    if((boxStartY+diffY)>imageStartY) {
                        if((boxStartX-diffX)>0) {
                            if (((boxLength - diffX) < imgMinSize) && ((boxLength - diffY) < imgMinSize)) {
                                boxLength -= diffX;
                                boxStartX+=diffX;
                                if((boxStartY+boxLength-diffX)>(imageStartY+imageHeight)){
                                    boxStartY += diffX;
                                }else {
                                    boxStartY += diffY;
                                }
                                touchY = (int) event.getY();
                                touchX=(int) event.getX();
                            }
                        }
                    }
                }
            }
            else if(startMoving==8){
                int diffX=(int)((event.getY()-touchY));
                if(diffX>0){
                    if(((boxLength+diffX)<imgMinSize)){
                        if((boxStartY+diffX+boxLength)>=(imageStartY+imageHeight)){
                            boxStartY-=diffX;
                        }
                        if((boxStartX+boxLength+diffX)>scrWidth){
                            boxStartX-=diffX;
                        }
                        boxLength+=diffX;
                        touchY = (int) event.getY();
                    }
                }else if(diffX<0){
                    if(((boxLength+diffX)<imgMinSize)){
                        boxLength +=diffX;
                        touchY = (int) event.getY();
                    }
                }
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            startMoving=0;
        }
        invalidate();
        return true;
    }
}
