package com.oustme.oustsdk.activity.common.noticeBoard.view;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBPostData;

import java.util.List;

/**
 * Created by oust on 2/20/19.
 */

public interface NBTopicDetailView {
    void updateTopicBanner(String imageUrl);
    void setToolbarText(String title);
    void OnErrorOccured(String error);
    void setOrUpdateAdapter(List<NBPostData> postDataList);
    void startApiCalls();
    void showProgressBar();
    void hideProgressBar();
    void createLoader();

}
