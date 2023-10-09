package com.oustme.oustsdk.interfaces.common;

/**
 * Created by oust on 5/9/19.
 */

public interface NewsFeedProgressListener {
    void updateFeedProgress(int progress);
    void updateFeedProgressNew(int progress, long timeInterval, boolean isCompleted);
}
