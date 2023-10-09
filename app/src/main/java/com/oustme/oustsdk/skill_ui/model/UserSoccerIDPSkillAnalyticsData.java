package com.oustme.oustsdk.skill_ui.model;

public class UserSoccerIDPSkillAnalyticsData {

    long idpTargetId;
    long contentCategoryId;
    long idpTargetScore;
    long bestScore;
    long levelSequence;
    long idpTargetDate;
    boolean verifyFlag;
    boolean redFlag;

    public long getIdpTargetId() {
        return idpTargetId;
    }

    public void setIdpTargetId(long idpTargetId) {
        this.idpTargetId = idpTargetId;
    }

    public long getContentCategoryId() {
        return contentCategoryId;
    }

    public void setContentCategoryId(long contentCategoryId) {
        this.contentCategoryId = contentCategoryId;
    }

    public long getIdpTargetScore() {
        return idpTargetScore;
    }

    public void setIdpTargetScore(long idpTargetScore) {
        this.idpTargetScore = idpTargetScore;
    }

    public long getBestScore() {
        return bestScore;
    }

    public void setBestScore(long bestScore) {
        this.bestScore = bestScore;
    }

    public long getLevelSequence() {
        return levelSequence;
    }

    public void setLevelSequence(long levelSequence) {
        this.levelSequence = levelSequence;
    }

    public long getIdpTargetDate() {
        return idpTargetDate;
    }

    public void setIdpTargetDate(long idpTargetDate) {
        this.idpTargetDate = idpTargetDate;
    }

    public boolean isVerifyFlag() {
        return verifyFlag;
    }

    public void setVerifyFlag(boolean verifyFlag) {
        this.verifyFlag = verifyFlag;
    }

    public boolean getRedFlag() {
        return redFlag;
    }

    public void setRedFlag(boolean redFlag) {
        this.redFlag = redFlag;
    }
}
