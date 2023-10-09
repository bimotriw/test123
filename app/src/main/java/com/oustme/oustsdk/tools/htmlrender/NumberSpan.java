package com.oustme.oustsdk.tools.htmlrender;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import com.oustme.oustsdk.R;
import com.oustme.oustsdk.tools.OustSdkApplication;

/**
 * Created by admin on 14/03/17.
 */

public class NumberSpan implements LeadingMarginSpan {
    private final String mNumber;
    private final int mTextWidth;

    public NumberSpan(int number) {
        mNumber = Integer.toString(number).concat(". ");
        mTextWidth=(int) OustSdkApplication.getContext().getResources().getDimension(R.dimen.oustlayout_dimen20);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return mTextWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline,
                                  int bottom, CharSequence text, int start, int end,
                                  boolean first, Layout l) {
        if (text instanceof Spanned) {
            int spanStart = ((Spanned) text).getSpanStart(this);
            if (spanStart == start) {
                c.drawText(mNumber, x, baseline, p);
            }
        }
    }
}