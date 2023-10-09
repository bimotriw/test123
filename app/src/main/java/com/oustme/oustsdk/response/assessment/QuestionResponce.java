package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.common.EncrypQuestions;
import com.oustme.oustsdk.response.common.Questions;

import java.util.List;

/**
 * Created by admin on 20/09/17.
 */

@Keep
public class QuestionResponce {
    private List<Questions> questions;
    private List<EncrypQuestions> questionsList;

    public List<Questions> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Questions> questions) {
        this.questions = questions;
    }

    public List<EncrypQuestions> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(List<EncrypQuestions> questionsList) {
        this.questionsList = questionsList;
    }
}
