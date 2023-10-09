package com.oustme.oustsdk.skill_ui.model;

import java.util.ArrayList;

public class UserSoccerSkillAnalyticsDataList {

    String userId;
    long soccerSkillId;
    String soccerSkillName;
    String bannerImg;
    long cardId;
    String cardTitle;
    String cardType;
    ArrayList<UserSkillLevelAnalyticsDataList> userSkillLevelAnalyticsDataList;
    ArrayList<UserSoccerSkillDailyAnalyticsDataList> userSoccerSkillDailyAnalyticsDataList;

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

    public String getSoccerSkillName() {
        return soccerSkillName;
    }

    public void setSoccerSkillName(String soccerSkillName) {
        this.soccerSkillName = soccerSkillName;
    }

    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public ArrayList<UserSkillLevelAnalyticsDataList> getUserSkillLevelAnalyticsDataList() {
        return userSkillLevelAnalyticsDataList;
    }

    public void setUserSkillLevelAnalyticsDataList(ArrayList<UserSkillLevelAnalyticsDataList> userSkillLevelAnalyticsDataList) {
        this.userSkillLevelAnalyticsDataList = userSkillLevelAnalyticsDataList;
    }

    public ArrayList<UserSoccerSkillDailyAnalyticsDataList> getUserSoccerSkillDailyAnalyticsDataList() {
        return userSoccerSkillDailyAnalyticsDataList;
    }

    public void setUserSoccerSkillDailyAnalyticsDataList(ArrayList<UserSoccerSkillDailyAnalyticsDataList> userSoccerSkillDailyAnalyticsDataList) {
        this.userSoccerSkillDailyAnalyticsDataList = userSoccerSkillDailyAnalyticsDataList;
    }
}
