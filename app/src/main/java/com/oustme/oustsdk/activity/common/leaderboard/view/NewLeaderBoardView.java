package com.oustme.oustsdk.activity.common.leaderboard.view;

import com.oustme.oustsdk.activity.common.leaderboard.model.GroupDataResponse;
import com.oustme.oustsdk.response.common.LeaderBoardDataRow;
import com.oustme.oustsdk.tools.ActiveUser;

import java.util.List;

public interface NewLeaderBoardView {
    void getRankersData();
    void updateRankersData(List<LeaderBoardDataRow> mLeaderBoardDataRowList);
    void hideProgressBar();
    void showProgressBar();
    void showError(String error);
    void updateUserData(ActiveUser activeUser);
    void updateGroupData(List<GroupDataResponse> mGroupDataResponses);
    void updateGroupLBData(List<LeaderBoardDataRow> mLeaderBoardDataRowList, String groupName);
    void noLB();
}
