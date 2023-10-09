package com.oustme.oustsdk.layoutFour.components.noticeBoard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.activity.common.noticeBoard.model.response.NBTopicData;

import java.util.List;

public class NoticeBoardViewModel extends ViewModel {

    private MutableLiveData<List<NBTopicData>> mNBTopicData;
    private NoticeBoardRepository mRepo;

    public void init() {
        if (mNBTopicData != null) {
            return;
        }
        mRepo = NoticeBoardRepository.getInstance();
        mNBTopicData = mRepo.getNBTopicList();
    }

    public MutableLiveData<List<NBTopicData>> getNBTopicList() {
        return mNBTopicData;
    }

}
