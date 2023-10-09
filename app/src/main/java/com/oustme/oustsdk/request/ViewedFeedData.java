package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

@Keep
public class ViewedFeedData {
    private int feedId;
    private String viewedTimestamp;

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public String getViewedTimestamp() {
        return viewedTimestamp;
    }

    public void setViewedTimestamp(String viewedTimestamp) {
        this.viewedTimestamp = viewedTimestamp;
    }
}
