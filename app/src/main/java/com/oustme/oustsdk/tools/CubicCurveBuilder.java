package com.oustme.oustsdk.tools;

import android.graphics.PointF;

/**
 * Created by oust on 8/22/18.
 */

public class CubicCurveBuilder {

    private PointF[] firstControlPoints;
    private PointF[] secondControlPoints;
    private PointF[] dataPoints;

    public CubicCurveBuilder(PointF[] dataPoints) {
        this.firstControlPoints = new PointF[dataPoints.length];
        this.secondControlPoints = new PointF[dataPoints.length];
        this.dataPoints=dataPoints;
    }

    public CubicCurveSegment[] controlPointsFromPoints()

    {
        int count = dataPoints.length - 1;

        //P0, P1, P2, P3 are the points for each segment, where P0 & P3 are the knots and P1, P2 are the control points.
        if (count == 1) {
            PointF P0 = dataPoints[0];
            PointF P3 = dataPoints[1];

            //Calculate First Control Point
            //3P1 = 2P0 + P3

            float P1x = (2 * P0.x + P3.x) / 3;
            float P1y = (2 * P0.y + P3.y) / 3;

            firstControlPoints[0] = new PointF(P1x, P1y);

            //Calculate second Control Point
            //P2 = 2P1 - P0
            float P2x = (2 * P1x - P0.x);
            float P2y = (2 * P1y - P0.y);

            secondControlPoints[0] = new PointF(P2x, P2y);
        } else {
            firstControlPoints = new PointF[count];

            PointF[] rhsArray = new PointF[count];

            //Array of Coefficients
            double[] a = new double[count];
            double[] b = new double[count];
            double[] c = new double[count];

            for (int i = 0; i<count ; i++ ){

                float rhsValueX = 0;
                float rhsValueY = 0;

                PointF P0 = dataPoints[i];
                PointF P3 = dataPoints[i + 1];

                if( i == 0) {
                    a[0]=(0);
                    b[0]=(2);
                    c[0]=(1);

                    //rhs for first segment
                    rhsValueX = P0.x + 2 * P3.x;
                    rhsValueY = P0.y + 2 * P3.y;

                } else if (i == count - 1 ){
                    a[i]=(2);
                    b[i]=(7);
                    c[i]=(0);

                    //rhs for last segment
                    rhsValueX = 8 * P0.x + P3.x;
                    rhsValueY = 8 * P0.y + P3.y;
                } else{
                    a[i]=(1);
                    b[i]=(4);
                    c[i]=(1);

                    rhsValueX = 4 * P0.x + 2 * P3.x;
                    rhsValueY = 4 * P0.y + 2 * P3.y;
                }

                rhsArray[i]=new PointF(rhsValueX, rhsValueY);
            }

            //Solve Ax=B. Use Tridiagonal matrix algorithm a.k.a Thomas Algorithm

            for (int i = 1; i<count ; i++ ){
                float rhsValueX = rhsArray[i].x;
                float rhsValueY = rhsArray[i].y;

                float prevRhsValueX = rhsArray[i - 1].x;
                float prevRhsValueY = rhsArray[i - 1].y;

                float m = (float)( a[i] / b[i - 1]);

                float b1 = (float)(b[i] - m * c[i - 1]);
                b[i] = b1;

                float r2x = rhsValueX - m * prevRhsValueX;
                float r2y = rhsValueY - m * prevRhsValueY;

                rhsArray[i] = new PointF(r2x, r2y);

            }

            //Get First Control Points

            //Last control Point
            float lastControlPointX = (float)(rhsArray[count - 1].x / b[count - 1]);
            float lastControlPointY = (float)(rhsArray[count - 1].y / b[count - 1]);

            firstControlPoints[count - 1] = new PointF(lastControlPointX, lastControlPointY);

            for (int i = count - 2; i >= 0; --i ){
                 PointF nextControlPoint = firstControlPoints[i + 1] ;
                    float controlPointX = (float)((rhsArray[i].x - c[i] * nextControlPoint.x) / b[i]);
                    float controlPointY = (float)((rhsArray[i].y - c[i] * nextControlPoint.y) / b[i]);

                    firstControlPoints[i] = new PointF(controlPointX, controlPointY);

                }
            }

            //Compute second Control Points from first

            for (int i = 0; i<count ; i++ ){

                if (i == count - 1 ){
                    PointF P3 = dataPoints[i + 1];

                   PointF P1 = firstControlPoints[i];
                    float controlPointX = (P3.x + P1.x) / 2;
                    float controlPointY = (P3.y + P1.y) / 2;

                    secondControlPoints[i]=new PointF(controlPointX, controlPointY);

                } else{
                    PointF P3 = dataPoints[i + 1];

                    PointF nextP1 = firstControlPoints[i + 1];

                    float controlPointX = 2 * P3.x - nextP1.x;
                    float controlPointY = 2 * P3.y - nextP1.y;

                    secondControlPoints[i]=new PointF(controlPointX, controlPointY);
                }
            }


        CubicCurveSegment[] controlPoints = new CubicCurveSegment[count];

        for (int i = 0; i < count; i++) {
            PointF firstControlPoint = firstControlPoints[i];
            PointF secondControlPoint = secondControlPoints[i];
            CubicCurveSegment segment =new  CubicCurveSegment(firstControlPoint,secondControlPoint);
            controlPoints[i]=segment;

        }

        return controlPoints;
    }

}





