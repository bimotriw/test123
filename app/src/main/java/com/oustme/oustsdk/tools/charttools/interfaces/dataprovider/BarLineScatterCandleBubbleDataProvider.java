package com.oustme.oustsdk.tools.charttools.interfaces.dataprovider;


import com.oustme.oustsdk.tools.charttools.components.YAxis;
import com.oustme.oustsdk.tools.charttools.data.BarLineScatterCandleBubbleData;
import com.oustme.oustsdk.tools.charttools.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(YAxis.AxisDependency axis);
    int getMaxVisibleCount();
    boolean isInverted(YAxis.AxisDependency axis);
    
    int getLowestVisibleXIndex();
    int getHighestVisibleXIndex();

    BarLineScatterCandleBubbleData getData();
}
