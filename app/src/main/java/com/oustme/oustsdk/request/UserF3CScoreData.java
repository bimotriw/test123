package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by admin on 20/09/17.
 */

@Keep
public class UserF3CScoreData {
    private long f3cId;
    private long score;
    private String avatar;
    private String displayName;
    private String userId;
    private long rightCount;
    private long userScore;
    private long averageTime;
    private long rank;

    public long getF3cId() {
        return f3cId;
    }

    public void setF3cId(long f3cId) {
        this.f3cId = f3cId;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getRightCount() {
        return rightCount;
    }

    public void setRightCount(long rightCount) {
        this.rightCount = rightCount;
    }

    public long getUserScore() {
        return userScore;
    }

    public void setUserScore(long userScore) {
        this.userScore = userScore;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }
}
