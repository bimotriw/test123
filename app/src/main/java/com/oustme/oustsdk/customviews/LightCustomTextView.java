package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.oustme.oustsdk.tools.OustSdkTools;

/**
 * Created by admin on 04/08/17.
 */

public class LightCustomTextView extends TextView {
    public LightCustomTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public LightCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public LightCustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        setTypeface(OustSdkTools.getTypefaceLight());
    }
}
