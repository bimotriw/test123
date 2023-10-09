package com.oustme.oustsdk.layoutFour.data.response;

import java.util.ArrayList;

public class LeaderBoardResponse {

    boolean success;
    String exceptionData;
    String error;
    String userDisplayName;
    int errorCode;
    String popup;
    long myRank;
    long myScore;
    ArrayList<LeaderBoardDataList> leaderBoardDataList;
    ArrayList<GroupDataList> groupLbDataList;
    GroupDataList filterGroup;
    boolean isFilterImplemented;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getExceptionData() {
        return exceptionData;
    }

    public void setExceptionData(String exceptionData) {
        this.exceptionData = exceptionData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public long getMyRank() {
        return myRank;
    }

    public void setMyRank(long myRank) {
        this.myRank = myRank;
    }

    public long getMyScore() {
        return myScore;
    }

    public void setMyScore(long myScore) {
        this.myScore = myScore;
    }

    public ArrayList<LeaderBoardDataList> getLeaderBoardDataList() {
        return leaderBoardDataList;
    }

    public void setLeaderBoardDataList(ArrayList<LeaderBoardDataList> leaderBoardDataList) {
        this.leaderBoardDataList = leaderBoardDataList;
    }

    public ArrayList<GroupDataList> getGroupLbDataList() {
        return groupLbDataList;
    }

    public void setGroupLbDataList(ArrayList<GroupDataList> groupLbDataList) {
        this.groupLbDataList = groupLbDataList;
    }

    public GroupDataList getFilterGroup() {
        return filterGroup;
    }

    public void setFilterGroup(GroupDataList filterGroup) {
        this.filterGroup = filterGroup;
    }

    public boolean isFilterImplemented() {
        return isFilterImplemented;
    }

    public void setFilterImplemented(boolean filterImplemented) {
        isFilterImplemented = filterImplemented;
    }
}
