package com.oustme.oustsdk.tools.charttools.interfaces.dataprovider;


import android.graphics.PointF;
import android.graphics.RectF;

import com.oustme.oustsdk.tools.charttools.data.ChartData;
import com.oustme.oustsdk.tools.charttools.formatter.ValueFormatter;

/**
 * Interface that provides everything there is to know about the dimensions,
 * bounds, and range of the chart.
 * 
 * @author Philipp Jahoda
 */
public interface ChartInterface {

    float getXChartMin();

    float getXChartMax();

    float getYChartMin();

    float getYChartMax();
    
    int getXValCount();

    int getWidth();

    int getHeight();

    PointF getCenterOfView();

    PointF getCenterOffsets();

    RectF getContentRect();
    
    ValueFormatter getDefaultValueFormatter();

    ChartData getData();
}
