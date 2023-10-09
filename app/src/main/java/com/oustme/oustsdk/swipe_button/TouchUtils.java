package com.oustme.oustsdk.swipe_button;

import android.view.MotionEvent;
import android.view.View;

public class TouchUtils {

    private TouchUtils() {
    }

    static boolean isTouchOutsideInitialPosition(MotionEvent event, View view) {
        return event.getX() > view.getX() + view.getWidth();
    }
}
