package com.oustme.oustsdk.response.FFContest;

import androidx.annotation.Keep;

/**
 * Created by admin on 04/10/17.
 */

@Keep
public class F3CUserMyPerfData {
    private String userId;
    private String displayName;
    private String avatar;
    private long responseTime;

    private boolean iAmTheBest;

    private long myResponseTime;
    private String myAnswer;
    private boolean myCorrect;
    private long qId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public boolean isiAmTheBest() {
        return iAmTheBest;
    }

    public void setiAmTheBest(boolean iAmTheBest) {
        this.iAmTheBest = iAmTheBest;
    }

    public long getMyResponseTime() {
        return myResponseTime;
    }

    public void setMyResponseTime(long myResponseTime) {
        this.myResponseTime = myResponseTime;
    }

    public String getMyAnswer() {
        return myAnswer;
    }

    public void setMyAnswer(String myAnswer) {
        this.myAnswer = myAnswer;
    }

    public boolean isMyCorrect() {
        return myCorrect;
    }

    public void setMyCorrect(boolean myCorrect) {
        this.myCorrect = myCorrect;
    }

    public long getqId() {
        return qId;
    }

    public void setqId(long qId) {
        this.qId = qId;
    }
}
