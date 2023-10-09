package com.oustme.oustsdk.request;

import androidx.annotation.Keep;

/**
 * Created by shilpysamaddar on 20/03/17.
 */

@Keep
public class QuestionFeedBackRequest {
    private String questionId;
    private String studentid;
    private String likeUnlike; /*like / unlike */
    private String feedback;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getLikeUnlike() {
        return likeUnlike;
    }

    public void setLikeUnlike(String likeUnlike) {
        this.likeUnlike = likeUnlike;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    @Override
    public String toString() {
        return "QuestionFeedBackRequest{" +
                "questionId='" + questionId + '\'' +
                ", studentid='" + studentid + '\'' +
                ", likeUnlike='" + likeUnlike + '\'' +
                ", feedback='" + feedback + '\'' +
                '}';
    }
}
