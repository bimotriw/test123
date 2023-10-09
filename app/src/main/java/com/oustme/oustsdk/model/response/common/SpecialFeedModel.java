package com.oustme.oustsdk.model.response.common;

import androidx.annotation.Keep;

@Keep
public class SpecialFeedModel {
    private long feedId;
    private boolean isClicked;

    public SpecialFeedModel() {
    }

    public SpecialFeedModel(long feedId, boolean isClicked) {
        this.feedId = feedId;
        this.isClicked = isClicked;
    }

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
