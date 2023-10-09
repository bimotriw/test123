package com.oustme.oustsdk.layoutFour.components.feedList;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.firebase.common.FilterCategory;

import java.util.ArrayList;
import java.util.List;

public class FeedFilterViewModel extends ViewModel {

    private MutableLiveData<List<FilterCategory>> mFilterCatagoryList;
    private MutableLiveData<ArrayList<UserFeedFilters.FeedFilter>>  listMutableLiveData;
    private FeedFilterRepository mRepo;

    public void init(Context context) {
        if (listMutableLiveData != null) {
            return;
        }
        mRepo = FeedFilterRepository.getInstance();
//        mFilterCatagoryList = mRepo.getFilterCategories(context);
        listMutableLiveData = mRepo.getFeedFiltersData(context);
    }
    public MutableLiveData<ArrayList<UserFeedFilters.FeedFilter>> getFeedFilters(){
        return listMutableLiveData;
    }

    public MutableLiveData<List<FilterCategory>> getFeedFilterList() {
        return mFilterCatagoryList;
    }

}
