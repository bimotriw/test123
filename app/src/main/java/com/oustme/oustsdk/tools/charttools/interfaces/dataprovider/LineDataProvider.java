package com.oustme.oustsdk.tools.charttools.interfaces.dataprovider;


import com.oustme.oustsdk.tools.charttools.components.YAxis;
import com.oustme.oustsdk.tools.charttools.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
