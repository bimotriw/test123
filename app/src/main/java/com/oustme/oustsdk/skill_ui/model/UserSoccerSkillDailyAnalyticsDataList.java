package com.oustme.oustsdk.skill_ui.model;

public class UserSoccerSkillDailyAnalyticsDataList {
    long userBestScorePerDay;
    long userCurrentHighestLevel;
    String dateIndicator;
    String  entryDate;
    long levelSequence;
    String levelName;
    String levelDescription;
    String levelBannerImg;
    long submissionTimeInMillis;
    String userSubmittedMediaFileName;
    String userViewPercentage;
    long userViewInterval;
    boolean redFlag;
    boolean verifyFlag;


    public long getUserBestScorePerDay() {
        return userBestScorePerDay;
    }

    public void setUserBestScorePerDay(long userBestScorePerDay) {
        this.userBestScorePerDay = userBestScorePerDay;
    }

    public long getUserCurrentHighestLevel() {
        return userCurrentHighestLevel;
    }

    public void setUserCurrentHighestLevel(long userCurrentHighestLevel) {
        this.userCurrentHighestLevel = userCurrentHighestLevel;
    }

    public String getDateIndicator() {
        return dateIndicator;
    }

    public void setDateIndicator(String dateIndicator) {
        this.dateIndicator = dateIndicator;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public long getLevelSequence() {
        return levelSequence;
    }

    public void setLevelSequence(long levelSequence) {
        this.levelSequence = levelSequence;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelDescription() {
        return levelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }

    public String getLevelBannerImg() {
        return levelBannerImg;
    }

    public void setLevelBannerImg(String levelBannerImg) {
        this.levelBannerImg = levelBannerImg;
    }

    public long getSubmissionTimeInMillis() {
        return submissionTimeInMillis;
    }

    public void setSubmissionTimeInMillis(long submissionTimeInMillis) {
        this.submissionTimeInMillis = submissionTimeInMillis;
    }

    public String getUserSubmittedMediaFileName() {
        return userSubmittedMediaFileName;
    }

    public void setUserSubmittedMediaFileName(String userSubmittedMediaFileName) {
        this.userSubmittedMediaFileName = userSubmittedMediaFileName;
    }

    public String getUserViewPercentage() {
        return userViewPercentage;
    }

    public void setUserViewPercentage(String userViewPercentage) {
        this.userViewPercentage = userViewPercentage;
    }

    public long getUserViewInterval() {
        return userViewInterval;
    }

    public void setUserViewInterval(long userViewInterval) {
        this.userViewInterval = userViewInterval;
    }

    public boolean isRedFlag() {

        return redFlag;
    }

    public void setRedFlag(boolean redFlag) {
        this.redFlag = redFlag;
    }

    public boolean isVerifyFlag() {
        return verifyFlag;
    }

    public void setVerifyFlag(boolean verifyFlag) {
        this.verifyFlag = verifyFlag;
    }
}
