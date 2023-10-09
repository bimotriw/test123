package com.oustme.oustsdk.assessment_ui.examMode;


import androidx.annotation.Keep;

@Keep
public class QuestionView {

    private int questionId;
    private boolean isViewed;
    private boolean isAnswered;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }
}
