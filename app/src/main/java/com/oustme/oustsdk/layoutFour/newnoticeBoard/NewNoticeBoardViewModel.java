package com.oustme.oustsdk.layoutFour.newnoticeBoard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.layoutFour.newnoticeBoard.model.response.NewNBTopicData;

import java.util.ArrayList;

public class NewNoticeBoardViewModel extends ViewModel {

    private MutableLiveData<ArrayList<NewNBTopicData>> mNBTopicData;
//    private MutableLiveData<List<NewNBPostData>> mNbPostData;
    NewNoticeBoardRepository mRepo;

    public void init() {
        if (mNBTopicData != null) {
            return;
        }
//        if (mNbPostData != null) {
//            return;
//        }
        mRepo = NewNoticeBoardRepository.getInstance();
        mNBTopicData = mRepo.getNBTopicList();
//        mNbPostData = mRepo.getNBPostList();
    }

    public MutableLiveData<ArrayList<NewNBTopicData>> getNBTopicList() {
        return mNBTopicData;
    }

//    public MutableLiveData<List<NewNBPostData>> getNBPostList() {
//        return mNbPostData;
//    }

}
