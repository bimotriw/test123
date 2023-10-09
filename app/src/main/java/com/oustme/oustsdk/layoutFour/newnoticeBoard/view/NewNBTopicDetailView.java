package com.oustme.oustsdk.layoutFour.newnoticeBoard.view;

import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBPostData;
import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;

import java.util.ArrayList;
import java.util.List;

public interface NewNBTopicDetailView {
    void updateTopicBanner(String imageUrl);
    void setToolbarText(String title);
    void OnErrorOccured(String error);
    void setOrUpdateAdapter(ArrayList<NewNBPostData> postDataList);
    void setOrUpdateAdapter2(List<NewNBPostData> postDataList);
    void startApiCalls();
    void showProgressBar();
    void hideProgressBar();
    void createLoader();

    void setData(NewNBTopicData nbTopicData);
}
