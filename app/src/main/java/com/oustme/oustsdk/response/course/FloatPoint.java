package com.oustme.oustsdk.response.course;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 15/11/17.
 */

@Keep
public class FloatPoint {
    float x, y;

    public FloatPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
