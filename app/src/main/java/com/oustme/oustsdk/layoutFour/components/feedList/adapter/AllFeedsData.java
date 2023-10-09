package com.oustme.oustsdk.layoutFour.components.feedList.adapter;

import com.oustme.oustsdk.room.dto.DTONewFeed;
import com.oustme.oustsdk.room.dto.DTOSpecialFeed;
import com.oustme.oustsdk.room.dto.DTOUserFeeds;

import java.util.ArrayList;
import java.util.List;

public class AllFeedsData {

    private List<DTONewFeed> dtoNewFeed;
    private ArrayList<DTOUserFeeds.FeedList> dtoUserFeeds;
    private List<DTOSpecialFeed> dtoSpecialFeed;

    int feedCount;
    public List<DTONewFeed> getDtoNewFeed() {
        return dtoNewFeed;
    }

    public void setDtoNewFeed(List<DTONewFeed> dtoNewFeed) {
        this.dtoNewFeed = dtoNewFeed;
    }

    public List<DTOSpecialFeed> getDtoSpecialFeed() {
        return dtoSpecialFeed;
    }

    public void setDtoSpecialFeed(List<DTOSpecialFeed> dtoSpecialFeed) {
        this.dtoSpecialFeed = dtoSpecialFeed;
    }

    public ArrayList<DTOUserFeeds.FeedList> getDtoUserFeeds() {
        return dtoUserFeeds;
    }

    public void setDtoUserFeeds(ArrayList<DTOUserFeeds.FeedList> dtoUserFeeds) {
        this.dtoUserFeeds = dtoUserFeeds;
    }

    public int getFeedCount() {
        return feedCount;
    }

    public void setFeedCount(int feedCount) {
        this.feedCount = feedCount;
    }
}
