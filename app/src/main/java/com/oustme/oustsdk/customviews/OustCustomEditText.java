package com.oustme.oustsdk.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.oustme.oustsdk.tools.OustSdkTools;

/**
 * Created by shilpysamaddar on 15/03/17.
 */

public class OustCustomEditText extends EditText {
    public OustCustomEditText(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public OustCustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public OustCustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        setTypeface(OustSdkTools.getAvenirLTStdMedium());
    }
}

