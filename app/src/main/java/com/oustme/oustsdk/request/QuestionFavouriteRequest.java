package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 20/03/17.
 */

@Keep
public class QuestionFavouriteRequest {
    private String questionId;

    private String studentid;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    @Override
    public String toString() {
        return "QuestionFavouriteRequest{" +
                "questionId='" + questionId + '\'' +
                ", studentid='" + studentid + '\'' +
                '}';
    }
}
