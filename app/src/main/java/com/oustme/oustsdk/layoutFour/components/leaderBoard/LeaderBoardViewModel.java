package com.oustme.oustsdk.layoutFour.components.leaderBoard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.oustme.oustsdk.layoutFour.data.response.GroupDataList;
import com.oustme.oustsdk.layoutFour.data.response.LeaderBoardResponse;

public class LeaderBoardViewModel extends ViewModel {

    private MutableLiveData<LeaderBoardResponse> leaderBoardResponseMutableLiveData;
    LeaderBoardRepository mRepo;

    public void init(String type, long contentId) {
        if (leaderBoardResponseMutableLiveData != null) {
            return;
        }
        mRepo = LeaderBoardRepository.getInstance(type, contentId);
        leaderBoardResponseMutableLiveData = mRepo.getLeaderBoardData();
    }

    public MutableLiveData<LeaderBoardResponse> getLeaderBoardResponse() {
        return leaderBoardResponseMutableLiveData;
    }

    public void groupDataFilter(GroupDataList filterGroupData) {

        if (mRepo != null) {
            if (filterGroupData != null && filterGroupData.getGroupId() != 0) {
                mRepo.fetchLBFilterByGroup(filterGroupData);
            } else {
                mRepo.fetchLeaderBoardData();
            }

        }

    }

}