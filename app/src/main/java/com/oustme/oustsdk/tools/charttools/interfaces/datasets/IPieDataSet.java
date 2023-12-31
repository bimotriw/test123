package com.oustme.oustsdk.tools.charttools.interfaces.datasets;


import com.oustme.oustsdk.tools.charttools.data.Entry;

/**
 * Created by Philipp Jahoda on 03/11/15.
 */
public interface IPieDataSet extends IDataSet<Entry> {

    /**
     * Returns the space that is set to be between the piechart-slices of this
     * DataSet, in degrees.
     *
     * @return
     */
    float getSliceSpace();

    /**
     * Returns the distance a highlighted piechart slice is "shifted" away from
     * the chart-center in dp.
     *
     * @return
     */
    float getSelectionShift();
}

