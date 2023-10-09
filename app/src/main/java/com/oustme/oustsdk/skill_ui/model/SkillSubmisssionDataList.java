package com.oustme.oustsdk.skill_ui.model;

public class SkillSubmisssionDataList {

    long soccerSkillId;
    long cardId;
    long userScore;
    long soccerSkillLevelId;
    String userSubmittedMediaFileName;
    String userViewPercentage;
    long userViewInterval;
    long submissionTimeInMillis;

    public long getSoccerSkillId() {
        return soccerSkillId;
    }

    public void setSoccerSkillId(long soccerSkillId) {
        this.soccerSkillId = soccerSkillId;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public long getUserScore() {
        return userScore;
    }

    public void setUserScore(long userScore) {
        this.userScore = userScore;
    }

    public long getSoccerSkillLevelId() {
        return soccerSkillLevelId;
    }

    public void setSoccerSkillLevelId(long soccerSkillLevelId) {
        this.soccerSkillLevelId = soccerSkillLevelId;
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

    public long getSubmissionTimeInMillis() {
        return submissionTimeInMillis;
    }

    public void setSubmissionTimeInMillis(long submissionTimeInMillis) {
        this.submissionTimeInMillis = submissionTimeInMillis;
    }
}
