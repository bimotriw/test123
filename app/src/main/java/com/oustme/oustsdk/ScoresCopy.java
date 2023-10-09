package com.oustme.oustsdk;

import androidx.annotation.Keep;

/**
 * Created by oust on 1/29/18.
 */

@Keep
public class ScoresCopy {
    private String time;

    private boolean correct;

    private String score;

    private String answer;

    private String xp;

    private String question;

    private String questionType;

    private String questionSerialNo;

    private String grade;

    private String subject;

    private String topic;

    private String moduleId;

    private String userSubjectiveAns;

    String remarks;

    String questionMedia;

    private boolean surveyQuestion;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getXp() {
        return xp;
    }

    public void setXp(String xp) {
        this.xp = xp;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionSerialNo() {
        return questionSerialNo;
    }

    public void setQuestionSerialNo(String questionSerialNo) {
        this.questionSerialNo = questionSerialNo;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getUserSubjectiveAns() {
        return userSubjectiveAns;
    }

    public void setUserSubjectiveAns(String userSubjectiveAns) {
        this.userSubjectiveAns = userSubjectiveAns;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getQuestionMedia() {
        return questionMedia;
    }

    public void setQuestionMedia(String questionMedia) {
        this.questionMedia = questionMedia;
    }

    public boolean isSurveyQuestion() {
        return surveyQuestion;
    }

    public void setSurveyQuestion(boolean surveyQuestion) {
        this.surveyQuestion = surveyQuestion;
    }
}
