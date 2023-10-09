package com.oustme.oustsdk.model.request;

import androidx.annotation.Keep;

import com.oustme.oustsdk.request.ClickedFeedData;
import com.oustme.oustsdk.request.ViewedFeedData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oust on 5/9/19.
 */

@Keep
public class FeedProgressModel {
    private List<ClickedFeedData> clickedFeedDataList;
    private List<ViewedFeedData> viewedFeedDataList;
    private int userProgress;
    private int contentId;



    public FeedProgressModel(int userProgress, int contentId) {
        this.userProgress = userProgress;
        this.contentId = contentId;
        ClickedFeedData clickedFeedData = new ClickedFeedData();
        clickedFeedData.setClickedTimestamp(""+System.currentTimeMillis());
        clickedFeedData.setFeedId(contentId);
        clickedFeedDataList=new ArrayList<>();
        clickedFeedDataList.add(clickedFeedData);

        ViewedFeedData viewedFeedData = new ViewedFeedData();
        viewedFeedData.setFeedId(contentId);
        viewedFeedData.setViewedTimestamp(""+System.currentTimeMillis());
        viewedFeedDataList = new ArrayList<>();
        viewedFeedDataList.add(viewedFeedData);
    }

    public List<ClickedFeedData> getClickedFeedDataList() {
        return clickedFeedDataList;
    }

    public void setClickedFeedDataList(List<ClickedFeedData> clickedFeedDataList) {
        this.clickedFeedDataList = clickedFeedDataList;
    }

    public List<ViewedFeedData> getViewedFeedDataList() {
        return viewedFeedDataList;
    }

    public void setViewedFeedDataList(List<ViewedFeedData> viewdFeedDataList) {
        this.viewedFeedDataList = viewdFeedDataList;
    }

    public int getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(int userProgress) {
        this.userProgress = userProgress;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }
}
