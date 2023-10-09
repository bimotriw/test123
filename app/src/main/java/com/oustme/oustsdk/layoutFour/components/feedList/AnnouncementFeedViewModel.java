package com.oustme.oustsdk.layoutFour.components.feedList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.layoutFour.components.feedList.adapter.AllFeedsData;

public class AnnouncementFeedViewModel extends ViewModel {

    private MutableLiveData<AllFeedsData> dtoUserFeedsMutableLiveData;
    AnnouncementFeedRepository mRepo;
    public void init() {
        mRepo = new AnnouncementFeedRepository();

    }

    public MutableLiveData<AllFeedsData> getAnnouncementFeedsList() {
        return mRepo.getAnnouncementUserFeeds();
    }
}
