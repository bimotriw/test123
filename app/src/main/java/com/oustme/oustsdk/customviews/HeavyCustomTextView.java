package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.oustme.oustsdk.tools.OustSdkTools;


/**
 * Created by shilpysamaddar on 07/03/17.
 */

public class HeavyCustomTextView extends TextView {
    public HeavyCustomTextView(Context context) {
        super(context);
        if(!isInEditMode())
            applyCustomFont(context);
    }

    public HeavyCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode())
            applyCustomFont(context);
    }

    public HeavyCustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(!isInEditMode())
            applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        try {
            setTypeface(OustSdkTools.getAvenirLTStdHeavy());
        }catch (Exception e){
            e.printStackTrace();
            OustSdkTools.sendSentryException(e);
        }
    }
}
