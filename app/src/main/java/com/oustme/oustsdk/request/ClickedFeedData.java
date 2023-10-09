package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 19/02/18.
 */

@Keep
public class ClickedFeedData {
    private int feedId;
    private String clickedTimestamp;
    private int cplId;

    public int getCplId() {
        return cplId;
    }

    public void setCplId(int cplId) {
        this.cplId = cplId;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public String getClickedTimestamp() {
        return clickedTimestamp;
    }

    public void setClickedTimestamp(String clickedTimestamp) {
        this.clickedTimestamp = clickedTimestamp;
    }
}
