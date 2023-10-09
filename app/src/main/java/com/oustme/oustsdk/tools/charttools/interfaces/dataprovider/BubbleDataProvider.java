package com.oustme.oustsdk.tools.charttools.interfaces.dataprovider;


import com.oustme.oustsdk.tools.charttools.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
