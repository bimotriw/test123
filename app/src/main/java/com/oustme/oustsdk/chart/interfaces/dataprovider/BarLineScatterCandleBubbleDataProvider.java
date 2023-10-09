package com.oustme.oustsdk.chart.interfaces.dataprovider;

import com.oustme.oustsdk.chart.components.YAxis.AxisDependency;
import com.oustme.oustsdk.chart.data.BarLineScatterCandleBubbleData;
import com.oustme.oustsdk.chart.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
