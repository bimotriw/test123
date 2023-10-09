package com.oustme.oustsdk.tools.charttools.interfaces.dataprovider;


import com.oustme.oustsdk.tools.charttools.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
