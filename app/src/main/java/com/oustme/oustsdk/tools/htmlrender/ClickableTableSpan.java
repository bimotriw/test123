package com.oustme.oustsdk.tools.htmlrender;

import android.text.style.ClickableSpan;

/**
 * Created by admin on 14/03/17.
 */

public abstract class ClickableTableSpan extends ClickableSpan {
    protected String tableHtml;

    // This sucks, but we need this so that each table can get its own ClickableTableSpan.
// Otherwise, we end up removing the clicking from earlier tables.
    public abstract ClickableTableSpan newInstance();

    public void setTableHtml(String tableHtml) {
        this.tableHtml = tableHtml;
    }

    public String getTableHtml() {
        return tableHtml;
    }
}