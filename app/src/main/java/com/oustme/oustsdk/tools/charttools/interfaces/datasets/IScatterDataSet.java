package com.oustme.oustsdk.tools.charttools.interfaces.datasets;


import com.oustme.oustsdk.tools.charttools.data.Entry;

/**
 * Created by philipp on 21/10/15.
 */
public interface IScatterDataSet extends ILineScatterCandleRadarDataSet<Entry> {

    /**
     * Returns the currently set scatter shape size
     *
     * @return
     */
    float getScatterShapeSize();

    /**
     * Returns all the different scattershapes the chart uses
     *
     * @return
     */
    //ScatterChart.ScatterShape getScatterShape();
}
