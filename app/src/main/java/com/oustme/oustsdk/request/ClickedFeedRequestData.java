package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by shilpysamaddar on 19/02/18.
 */

@Keep
public class ClickedFeedRequestData {
    private List<ClickedFeedData> clickedFeedDataList;
    private List<ViewedFeedData> viewdFeedDataList;
    private String studentid;

    public List<ClickedFeedData> getClickedFeedDataList() {
        return clickedFeedDataList;
    }

    public void setClickedFeedDataList(List<ClickedFeedData> clickedFeedDataList) {
        this.clickedFeedDataList = clickedFeedDataList;
    }

    public List<ViewedFeedData> getViewdFeedDataList() {
        return viewdFeedDataList;
    }

    public void setViewdFeedDataList(List<ViewedFeedData> viewdFeedDataList) {
        this.viewdFeedDataList = viewdFeedDataList;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }
}
