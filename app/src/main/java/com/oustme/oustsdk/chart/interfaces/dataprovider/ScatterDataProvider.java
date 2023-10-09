package com.oustme.oustsdk.chart.interfaces.dataprovider;

import com.oustme.oustsdk.chart.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
