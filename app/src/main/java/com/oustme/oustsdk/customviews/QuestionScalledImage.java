package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;

import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkTools;

/**
 * Created by shilpysamaddar on 20/03/17.
 */

public class QuestionScalledImage  extends View {
    private Bitmap mainBiutmap,checkedBitmap;
    private int  scrWidth,scrHeight;
    private int rightOption;
    private boolean isCheckAnserImage;
    private Paint backkPaint;
    private int size;

    public QuestionScalledImage(Context context, AttributeSet attrs) {
        super(context, attrs);

        backkPaint=new Paint();
        backkPaint.setStyle(Paint.Style.FILL);
        backkPaint.setColor(OustSdkTools.getColorBack(R.color.popupBackGroundb));
    }
    public void setMainBiutmap(String bitmapStr,int width,int rightOption,boolean isCheckAnserImage){
        try {
            byte[] decodedString = Base64.decode(bitmapStr, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            this.rightOption=rightOption;
            size=(int)getResources().getDimension(R.dimen.oustlayout_dimen20);
            this.scrWidth=((width/2)-size);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) this.getLayoutParams();
//            int n1=decodedByte.getWidth();
//            int n2=decodedByte.getHeight();
//            float f1=((float)n2)/((float)n1);
//            scrHeight=(int)(f1*scrWidth);
            float f1=(float)(scrWidth*0.8);
            scrHeight=(int)f1;
            params.height =scrHeight;
            params.width=scrWidth;
            this.setLayoutParams(params);
            mainBiutmap = Bitmap.createScaledBitmap(decodedByte, scrWidth, scrHeight, false);
            size=(int)getResources().getDimension(R.dimen.oustlayout_dimen25);
            this.isCheckAnserImage=isCheckAnserImage;
            if(rightOption==1){
                backkPaint.setColor(OustSdkTools.getColorBack(R.color.LiteGreen));
                checkedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whitewrong);
            }else if(rightOption==2) {
                backkPaint.setColor(OustSdkTools.getColorBack(R.color.Orange));
                checkedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.whitegood);
            }
            //checkedBitmap = Bitmap.createScaledBitmap(checkedBitmap, size, size, false);
            invalidate();
        }catch (Exception e){}
    }
    @Override
    protected void onDraw(Canvas canvas) {
        try {
            if (mainBiutmap != null) {
                canvas.drawBitmap(mainBiutmap, 0, 0, null);
                if(isCheckAnserImage){
                    if(checkedBitmap!=null) {
                        canvas.drawRect((scrWidth - checkedBitmap.getWidth() - 20), (scrHeight - checkedBitmap.getHeight() - 20), scrWidth, scrHeight, backkPaint);
                        canvas.drawBitmap(checkedBitmap, (scrWidth - checkedBitmap.getWidth()-10), (scrHeight - checkedBitmap.getHeight() -10), null);
                    }
                }
            }
        }catch (Exception e){}
    }
}
