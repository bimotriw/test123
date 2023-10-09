package com.oustme.oustsdk.skill_ui.model;

public class LeaderBoardData {

    String userid;
    String displayName;
    String avatar;
    long userScore;
    long userCurrentLevelSequence;
    long rank;
    String lbAddInfo;
   // String lbDetails;


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getUserScore() {
        return userScore;
    }

    public void setUserScore(long userScore) {
        this.userScore = userScore;
    }

    public long getUserCurrentLevelSequence() {
        return userCurrentLevelSequence;
    }

    public void setUserCurrentLevelSequence(long userCurrentLevelSequence) {
        this.userCurrentLevelSequence = userCurrentLevelSequence;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public String getLbAddInfo() {
        return lbAddInfo;
    }

    public void setLbAddInfo(String lbAddInfo) {
        this.lbAddInfo = lbAddInfo;
    }
}
