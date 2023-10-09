package com.oustme.oustsdk.tools.charttools.interfaces.dataprovider;

import com.oustme.oustsdk.tools.charttools.data.BarData;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isDrawHighlightArrowEnabled();
}
