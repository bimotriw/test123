package com.oustme.oustsdk.skill_ui.model;

import java.util.ArrayList;

public class SkillAnalyticsResponse {

    boolean success;
    String exceptionData;
    String error;
    String userDisplayName;
    int errorCode;
    String popup;
    ArrayList<UserSoccerSkillAnalyticsDataList> userSoccerSkillAnalyticsDataList;

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

    public ArrayList<UserSoccerSkillAnalyticsDataList> getUserSoccerSkillAnalyticsDataList() {
        return userSoccerSkillAnalyticsDataList;
    }

    public void setUserSoccerSkillAnalyticsDataList(ArrayList<UserSoccerSkillAnalyticsDataList> userSoccerSkillAnalyticsDataList) {
        this.userSoccerSkillAnalyticsDataList = userSoccerSkillAnalyticsDataList;
    }
}
