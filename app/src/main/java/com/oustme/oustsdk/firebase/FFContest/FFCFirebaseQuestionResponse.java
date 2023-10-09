package com.oustme.oustsdk.firebase.FFContest;

import androidx.annotation.Keep;

/**
 * Created by admin on 04/08/17.
 */

@Keep
public class FFCFirebaseQuestionResponse {
    private String userId;
    private String avatar;
    private String displayName;
    private String answer;
    private boolean correct;
    private long reponseTime;
    private long userScore;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public long getReponseTime() {
        return reponseTime;
    }

    public void setReponseTime(long reponseTime) {
        this.reponseTime = reponseTime;
    }

    public long getUserScore() {
        return userScore;
    }

    public void setUserScore(long userScore) {
        this.userScore = userScore;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
