package com.oustme.oustsdk.tools;

import android.graphics.PointF;

public class CubicCurveSegment {
        PointF controlPoint1;
        PointF controlPoint2;

        public CubicCurveSegment(PointF controlPoint1, PointF controlPoint2) {
            this.controlPoint1 = controlPoint1;
            this.controlPoint2 = controlPoint2;
        }

        public PointF getControlPoint1() {
            return controlPoint1;
        }

        public void setControlPoint1(PointF controlPoint1) {
            this.controlPoint1 = controlPoint1;
        }

        public PointF getControlPoint2() {
            return controlPoint2;
        }

        public void setControlPoint2(PointF controlPoint2) {
            this.controlPoint2 = controlPoint2;
        }
    }