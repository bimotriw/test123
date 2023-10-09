package com.oustme.oustsdk.response.assessment;

import androidx.annotation.Keep;

import com.oustme.oustsdk.response.common.EncrypQuestions;
import com.oustme.oustsdk.response.common.Questions;
import com.oustme.oustsdk.response.course.CommonResponse;
import com.oustme.oustsdk.room.dto.DTOQuestions;

import java.util.List;

/**
 * Created by shilpysamaddar on 15/03/17.
 */

@Keep
public class QuestionResponse extends CommonResponse {
    private List<EncrypQuestions> questionsList;
    private List<DTOQuestions> questions;

    public List<DTOQuestions> getQuestions() {
        return questions;
    }

    public void setQuestions(List<DTOQuestions> questions) {
        this.questions = questions;
    }

    public List<EncrypQuestions> getQuestionsList() {
        return questionsList;
    }

    public void setQuestionsList(List<EncrypQuestions> questionsList) {
        this.questionsList = questionsList;
    }
}
