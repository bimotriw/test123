package com.oustme.oustsdk.interfaces.common;

import com.oustme.oustsdk.room.dto.DTONewFeed;

/**
 * Created by oust on 5/23/18.
 */

public interface FeedClickListener {
    void onFeedClick(long newFeedId, int cplId);
    void onFeedViewed(long newFeedId);
    void onFeedViewedOnScroll(int position);
    void cplFeedClicked(long cplId);
    void checkFeedData(long cplId, long feedId);
    void onRemoveVideo(long newFeedId);
    void onPlayVideo(int position, int lastPos);
    void refreshViews();
    void onFeedRewardCoinsUpdate(DTONewFeed newFeed);
}
