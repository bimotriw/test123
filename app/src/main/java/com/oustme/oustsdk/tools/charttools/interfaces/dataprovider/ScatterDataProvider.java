package com.oustme.oustsdk.tools.charttools.interfaces.dataprovider;


import com.oustme.oustsdk.tools.charttools.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
