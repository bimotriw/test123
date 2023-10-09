package com.oustme.oustsdk.chart;

import com.oustme.oustsdk.tools.charttools.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class MyValueFormatter implements ValueFormatter {

    private DecimalFormat mFormat;

    public MyValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.00");
    }



    @Override
    public String getFormattedValue(float value, com.oustme.oustsdk.tools.charttools.data.Entry entry, int dataSetIndex, com.oustme.oustsdk.tools.charttools.utils.ViewPortHandler viewPortHandler) {
        System.out.println("Value date "+value);
        if(value > 0) {
            return mFormat.format(value);
        } else {
            return "";
        }

    }
}
