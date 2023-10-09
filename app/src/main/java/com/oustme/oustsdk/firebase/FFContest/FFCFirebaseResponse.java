package com.oustme.oustsdk.firebase.FFContest;

import androidx.annotation.Keep;

/**
 * Created by admin on 04/08/17.
 */

@Keep
public class FFCFirebaseResponse {
    private String userId;
    private String avatar;
    private String displayName;
    private long rightCount;
    private long averageTime;
    private long userScore;
    private boolean correct;
    private String answer;
    private long sequence;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public long getRightCount() {
        return rightCount;
    }

    public void setRightCount(long rightCount) {
        this.rightCount = rightCount;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    public long getUserScore() {
        return userScore;
    }

    public void setUserScore(long userScore) {
        this.userScore = userScore;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
