package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.oustme.oustsdk.R;

/**
 * Created by shilpysamaddar on 16/03/17.
 */

public class OustCheckBox extends View {
    Bitmap mainBiutmap;
    private boolean status=false;
    private int size=0;
    public OustCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        mainBiutmap = BitmapFactory.decodeResource(getResources(), R.drawable.checkbox);
        size=(int)getResources().getDimension(R.dimen.oustlayout_dimen18);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        mainBiutmap = Bitmap.createScaledBitmap(mainBiutmap,size, size, false);
        canvas.drawBitmap(mainBiutmap,0,0,null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(status==true){
                mainBiutmap = BitmapFactory.decodeResource(getResources(), R.drawable.checkbox);
                status=false;
            }else {
                mainBiutmap = BitmapFactory.decodeResource(getResources(), R.drawable.checked);
                status=true;
            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }
    public void detectTouch(){
        if(status==true){
            mainBiutmap = BitmapFactory.decodeResource(getResources(), R.drawable.checkbox);
            status=false;
        }else {
            mainBiutmap = BitmapFactory.decodeResource(getResources(), R.drawable.checked);
            status=true;
        }
        invalidate();
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus() {
        this.status = false;
        mainBiutmap = BitmapFactory.decodeResource(getResources(), R.drawable.checkbox);
        invalidate();
    }

}
