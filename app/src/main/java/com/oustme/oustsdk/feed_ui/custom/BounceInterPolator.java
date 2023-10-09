package com.oustme.oustsdk.feed_ui.custom;

import android.view.animation.Interpolator;

public class BounceInterPolator implements Interpolator {

    private double mAmplitude = 1;
    private double mFrequency = 10;

    public BounceInterPolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        double amplitude = mAmplitude;
        if (amplitude == 0) { amplitude = 0.05; }
        return (float) (-1 * Math.pow(Math.E, -time/ amplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}
