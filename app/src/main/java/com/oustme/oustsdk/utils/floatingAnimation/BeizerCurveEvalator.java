package com.oustme.oustsdk.utils.floatingAnimation;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

public class BeizerCurveEvalator implements TypeEvaluator<PointF> {

    private PointF point1,point2;

    public BeizerCurveEvalator(PointF point1,PointF point2){
        this.point1 = point1;
        this.point2 = point2;
    }

    // directly apply Bessel's third-order formula
    @Override
    public PointF evaluate(float fraction, PointF point0, PointF point3) {

        PointF pointF = new PointF();

        pointF.x = point0.x*(1-fraction)*(1-fraction)*(1-fraction)
                + 3*point1.x*fraction*(1-fraction)*(1-fraction)
                + 3*point2.x*fraction*fraction*(1-fraction)
                + point3.x*fraction*fraction*fraction;

        pointF.y = point0.y*(1-fraction)*(1-fraction)*(1-fraction)
                + 3*point1.y*fraction*(1-fraction)*(1-fraction)
                + 3*point2.y*fraction*fraction*(1-fraction)
                + point3.y*fraction*fraction*fraction;

        return pointF;
    }
}
