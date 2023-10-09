package com.oustme.oustsdk.layoutFour.components.feedList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.layoutFour.components.feedList.adapter.AllFeedsData;

public class FeedViewModel extends ViewModel {

    private MutableLiveData<AllFeedsData> dtoUserFeedsMutableLiveData;
    FeedRepository mRepo;
    public void init(String filters, int pageNum, String query) {
        mRepo = new FeedRepository(filters, pageNum, query);
    }

    public MutableLiveData<AllFeedsData> getUserFeedsList() {
        return mRepo.getDtoUserFeeds();
    }
}
