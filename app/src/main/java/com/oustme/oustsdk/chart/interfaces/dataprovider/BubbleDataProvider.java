package com.oustme.oustsdk.chart.interfaces.dataprovider;

import com.oustme.oustsdk.chart.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
