package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

@Keep
public class FeedRewardCoinsUpdate {
    private String userId;
    private long feedId;
    private String timestamp;
    private String feedCoins;
    //private long feedCoins;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getFeedId() {
        return feedId;
    }

    public void setFeedId(long feedId) {
        this.feedId = feedId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /*public long getFeedCoins() {
        return feedCoins;
    }

    public void setFeedCoins(long feedCoins) {
        this.feedCoins = feedCoins;
    }*/

    public String getFeedCoins() {
        return feedCoins;
    }

    public void setFeedCoins(String feedCoins) {
        this.feedCoins = feedCoins;
    }
}
