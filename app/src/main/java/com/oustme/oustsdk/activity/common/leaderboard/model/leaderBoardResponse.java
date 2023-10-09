package com.oustme.oustsdk.activity.common.leaderboard.model;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.common.LeaderBoardDataRow;

import java.util.List;

@Keep
public class leaderBoardResponse {
    private boolean success;
    private String exceptionData;
    private String error;
    private String userDisplayName;
    private String popup;
    private int errorCode;
    private List<LeaderBoardDataRow> leaderBoardDataList;
    private List<GroupDataResponse> groupLbDataList;

    public leaderBoardResponse() {

    }

    public leaderBoardResponse(boolean success, String exceptionData, String error, String userDisplayName, String popup, int errorCode, List<LeaderBoardDataRow> leaderBoardDataList, List<GroupDataResponse> groupLbDataList) {
        this.success = success;
        this.exceptionData = exceptionData;
        this.error = error;
        this.userDisplayName = userDisplayName;
        this.popup = popup;
        this.errorCode = errorCode;
        this.leaderBoardDataList = leaderBoardDataList;
        this.groupLbDataList = groupLbDataList;
    }

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

    public String getPopup() {
        return popup;
    }

    public void setPopup(String popup) {
        this.popup = popup;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<LeaderBoardDataRow> getLeaderBoardDataList() {
        return leaderBoardDataList;
    }

    public void setLeaderBoardDataList(List<LeaderBoardDataRow> leaderBoardDataList) {
        this.leaderBoardDataList = leaderBoardDataList;
    }

    public List<GroupDataResponse> getGroupLbDataList() {
        return groupLbDataList;
    }

    public void setGroupLbDataList(List<GroupDataResponse> groupLbDataList) {
        this.groupLbDataList = groupLbDataList;
    }
}
