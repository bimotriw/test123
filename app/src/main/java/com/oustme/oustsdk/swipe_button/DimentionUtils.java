package com.oustme.oustsdk.swipe_button;

import android.content.Context;

public class DimentionUtils {

    private DimentionUtils() {
    }

    static float converPixelsToSp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }
}
