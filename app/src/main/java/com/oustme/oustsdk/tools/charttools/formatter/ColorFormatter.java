package com.oustme.oustsdk.tools.charttools.formatter;


import com.oustme.oustsdk.tools.charttools.data.Entry;

/**
 * Interface that can be used to return a customized color instead of setting
 * colors via the setColor(...) method of the DataSet.
 * 
 * @author Philipp Jahoda
 */
public interface ColorFormatter {

    int getColor(Entry e, int index);
}