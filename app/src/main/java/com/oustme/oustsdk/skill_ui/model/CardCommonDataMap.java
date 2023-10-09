package com.oustme.oustsdk.skill_ui.model;


public class CardCommonDataMap {

    CardInfo cardInfo;
    long attemptCount;
    long userBestScore;


    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public long getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(long attemptCount) {
        this.attemptCount = attemptCount;
    }

    public long getUserBestScore() {
        return userBestScore;
    }

    public void setUserBestScore(long userBestScore) {
        this.userBestScore = userBestScore;
    }
}
