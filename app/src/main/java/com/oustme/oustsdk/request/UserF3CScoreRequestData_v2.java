package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

import java.util.List;

/**
 * Created by admin on 20/09/17.
 */

@Keep
public class UserF3CScoreRequestData_v2 {
    private UserF3CScoreData scoreData;
    private List<UserF3CQuestionScoreData> questionScoreData;


    public UserF3CScoreData getScoreData() {
        return scoreData;
    }

    public void setScoreData(UserF3CScoreData scoreData) {
        this.scoreData = scoreData;
    }

    public List<UserF3CQuestionScoreData> getQuestionScoreData() {
        return questionScoreData;
    }

    public void setQuestionScoreData(List<UserF3CQuestionScoreData> questionScoreData) {
        this.questionScoreData = questionScoreData;
    }
}
