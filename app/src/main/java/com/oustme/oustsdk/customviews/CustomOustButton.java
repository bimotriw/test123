package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.oustme.oustsdk.tools.OustSdkTools;


/**
 * Created by shilpysamaddar on 09/03/17.
 */

public class CustomOustButton extends Button {
    public CustomOustButton(Context context) {
        super(context);
        if(!isInEditMode())
            applyCustomFont(context);
    }

    public CustomOustButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            applyCustomFont(context);
    }

    public CustomOustButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
            applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        try {
            setTypeface(OustSdkTools.getAvenirLTStdMedium());
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
