
package com.oustme.oustsdk.tools.charttools.jobs;


import android.view.View;

import com.oustme.oustsdk.tools.charttools.utils.Transformer;
import com.oustme.oustsdk.tools.charttools.utils.ViewPortHandler;

/**
 * Runnable that is used for viewport modifications since they cannot be
 * executed at any time. This can be used to delay the execution of viewport
 * modifications until the onSizeChanged(...) method of the chartview is called.
 * 
 * @author Philipp Jahoda
 */
public class MoveViewJob implements Runnable {

    protected ViewPortHandler mViewPortHandler;
    protected float xIndex = 0f;
    protected float yValue = 0f;
    protected Transformer mTrans;
    protected View view;

    public MoveViewJob(ViewPortHandler viewPortHandler, float xIndex, float yValue,
            Transformer trans, View v) {

        this.mViewPortHandler = viewPortHandler;
        this.xIndex = xIndex;
        this.yValue = yValue;
        this.mTrans = trans;
        this.view = v;
    }

    @Override
    public void run() {

        float[] pts = new float[] {
                xIndex, yValue
        };

        mTrans.pointValuesToPixel(pts);
        mViewPortHandler.centerViewPort(pts, view);
    }
}
