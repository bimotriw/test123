package com.oustme.oustsdk.skill_ui.model;

import java.util.ArrayList;

public class IdpAnalyticsResponse {

    boolean success;
    String exceptionData;
    String error;
    String userDisplayName;
    int errorCode;
    String popup;
    ArrayList<UserSoccerIDPSkillAnalyticsData> userSoccerIDPSkillAnalyticsData;
    String skillCategory;
    String userId;
    long soccerSkillId;

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

    public ArrayList<UserSoccerIDPSkillAnalyticsData> getUserSoccerIDPSkillAnalyticsData() {
        return userSoccerIDPSkillAnalyticsData;
    }

    public void setUserSoccerIDPSkillAnalyticsData(ArrayList<UserSoccerIDPSkillAnalyticsData> userSoccerIDPSkillAnalyticsData) {
        this.userSoccerIDPSkillAnalyticsData = userSoccerIDPSkillAnalyticsData;
    }

    public String getSkillCategory() {
        return skillCategory;
    }

    public void setSkillCategory(String skillCategory) {
        this.skillCategory = skillCategory;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getSoccerSkillId() {
        return soccerSkillId;
    }

    public void setSoccerSkillId(long soccerSkillId) {
        this.soccerSkillId = soccerSkillId;
    }
}
