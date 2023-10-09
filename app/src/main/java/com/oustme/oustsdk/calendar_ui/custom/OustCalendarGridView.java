package com.oustme.oustsdk.calendar_ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class OustCalendarGridView extends GridView {

    public OustCalendarGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OustCalendarGridView(Context context) {
        super(context);
    }

    public OustCalendarGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

