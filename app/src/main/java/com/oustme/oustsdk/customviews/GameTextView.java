package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.oustme.oustsdk.tools.OustSdkTools;


/**
 * Created by admin on 30/11/17.
 */

public class GameTextView extends TextView {
    public GameTextView(Context context) {
        super(context);
        if(!isInEditMode())
            applyCustomFont(context);
    }

    public GameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            applyCustomFont(context);
    }

    public GameTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
            applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        try {
            setTypeface(OustSdkTools.getTypefaceLithoPro());
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
