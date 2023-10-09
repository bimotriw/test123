package com.oustme.oustsdk.chart.interfaces.dataprovider;

import com.oustme.oustsdk.chart.components.YAxis;
import com.oustme.oustsdk.chart.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
