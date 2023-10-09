package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by admin on 04/10/17.
 */

@Keep
public class UserF3CQuestionScoreData {
    private String answer;
    private boolean correct;
    private long qId;
    private long responseTime;

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

    public long getqId() {
        return qId;
    }

    public void setqId(long qId) {
        this.qId = qId;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
