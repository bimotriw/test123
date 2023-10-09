package com.oustme.oustsdk.layoutFour.components.popularFeeds;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.room.dto.DTOSpecialFeed;

import java.util.List;

public class PopularFeedViewModel extends ViewModel {

    private MutableLiveData<List<DTOSpecialFeed>> mFeedList;

    public MutableLiveData<List<DTOSpecialFeed>> getFeedList() {
        return mFeedList;
    }

    public void initData() {
        PopularFeedRepository mRepo = PopularFeedRepository.getInstance();
        mFeedList = mRepo.getFeedList();
    }
}
